/*
 * Copyright 2014 Brais Gabin
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.braisgabin.fbstats;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import com.braisgabin.fbstats.domain.FeedRequest;
import com.braisgabin.fbstats.domain.Topic;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final int argsCount = args.length;
		if (argsCount != 3 && argsCount != 4) {
			System.err.println("facebook-stats <output_file_path> <fb_user_id_group_id_or_page_id> <fb_access_token> [max_posts]");
			System.out.println("If you want the stats for a user the access token must have the \"read_stream\" permission.");
			System.out.println("If you want the stats for a close or private group the access token must have the \"user_groups\" permission.");
			return;
		}
		final String path = args[0];
		final String groupId = args[1];
		final String accessToken = args[2];
		final int maxPosts;
		if (argsCount == 4) {
			maxPosts = Integer.parseInt(args[3]);
		} else {
			maxPosts = Integer.MAX_VALUE;
		}

		try {
			new Main(path, groupId, accessToken).calcule(maxPosts);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private final Api api;

	private final File file;
	private final String groupId;

	public Main(String path, String groupId, String accessToken) {
		this.file = new File(path);
		this.groupId = groupId;

		this.api = new Api(accessToken);
	}

	private void calcule(int maxPosts) throws IOException, InterruptedException {
		final CsvWriter writer = new CsvWriter(new FileOutputStream(file));
		Stack<Thread> threads = new Stack<Thread>();
		FeedRequest feedRequest = api.download("https://graph.facebook.com/v2.1/" + groupId + "/feed?fields=message,from,id,comments.fields(id,message,like_count,from,created_time),likes.limit(100),created_time", FeedRequest.class);
		threads.addAll(process(feedRequest.getTopics(), writer));

		while (feedRequest.hasMore() && threads.size() <= maxPosts) {
			feedRequest = api.download(feedRequest.getNext(), FeedRequest.class);
			threads.addAll(process(feedRequest.getTopics(), writer));
		}

		while (!threads.isEmpty()) {
			threads.pop().join();
		}
		writer.close();
	}

	private List<Thread> process(Topic[] topics, Writer writer) {
		List<Thread> list = new ArrayList<Thread>();
		for (Topic topic : topics) {
			Thread t = new ProcessTopic(topic, api, writer);
			t.start();
			list.add(t);
		}
		return list;
	}

	private static class CsvWriter implements Writer, Closeable {
		private final PrintStream printStream;
		private final DateFormat dateFormat;

		public CsvWriter(OutputStream out) throws UnsupportedEncodingException {
			printStream = new PrintStream(out, true, "ISO-8859-1");
			dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			printStream.append("Thread id").append(",");
			printStream.append("Is thread").append(",");
			printStream.append("User name").append(",");
			printStream.append("User id").append(",");
			printStream.append("Date").append(",");
			printStream.append("Lenght").append(",");
			printStream.append("Likes").append("\n");
		}

		@Override
		public synchronized void write(String topic_id, boolean isTopic, String userName, String userId, Date date, int lenght, int likeCount) {
			printStream.append(topic_id).append(",");
			printStream.append(isTopic ? "1" : "0").append(",");
			printStream.append("\"").append(userName).append("\",");
			printStream.append(userId).append(",");
			printStream.append(dateFormat.format(date)).append(",");
			printStream.append(lenght + "").append(",");
			printStream.append(likeCount + "").append("\n");
		}

		@Override
		public void close() throws IOException {
			printStream.close();
		}
	}
}
