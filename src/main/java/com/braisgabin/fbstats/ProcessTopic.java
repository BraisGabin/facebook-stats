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

import java.io.IOException;

import com.braisgabin.fbstats.domain.Comment;
import com.braisgabin.fbstats.domain.CommentList;
import com.braisgabin.fbstats.domain.Topic;

public class ProcessTopic extends Thread {
	private final Topic topic;
	private final Api api;
	private final Writer writer;

	public ProcessTopic(Topic topic, Api api, Writer writer) {
		super("Topic: " + topic.getId());
		this.topic = topic;
		this.api = api;
		this.writer = writer;
	}

	@Override
	public void run() {
		try {
			writer.write(topic.getId(), true, topic.getOwnerName(), topic.getOwnerId(), topic.getDate(), topic.getMessageLength(), topic.getLikeCount(api));

			if (topic.hasComments()) {
				CommentList commentList = topic.getCommentList();
				process(commentList.getData());
				while (commentList.hasMore()) {
					commentList = api.download(commentList.getNext(), CommentList.class);
					process(commentList.getData());
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void process(Comment[] comments) {
		for (Comment comment : comments) {
			process(comment);
		}
	}

	private void process(Comment comment) {
		writer.write(topic.getId(), false, comment.getOwnerName(), comment.getOwnerId(), comment.getDate(), comment.getMessageLength(), comment.getLikeCount());
	}
}
