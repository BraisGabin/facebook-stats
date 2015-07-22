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
package com.braisgabin.fbstats.domain;

import java.io.IOException;
import java.util.Date;

import com.braisgabin.fbstats.Api;
import com.braisgabin.fbstats.helpers.DateDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class Topic {
	@JsonProperty("id")
	private String id;
	@JsonProperty("from")
	private User owner;
	@JsonProperty("message")
	private String message;
	@JsonProperty("created_time")
	@JsonDeserialize(using = DateDeserializer.class)
	private Date time;
	@JsonProperty("comments")
	private CommentList comments;
	@JsonProperty("likes")
	private LikeList likes;
	private int likeCount = -1;

	public String getId() {
		return id;
	}

	public String getOwnerName() {
		return owner.getName();
	}

	public String getOwnerId() {
		return owner.getId();
	}

	public Date getDate() {
		return time;
	}

	public int getMessageLength() {
		return message == null ? 0 : message.length();
	}

	public int getLikeCount(Api api) throws JsonParseException, JsonMappingException, IOException {
		if (likeCount < 0) {
			likeCount = calculeLikeCount(api);
		}
		return likeCount;
	}

	private int calculeLikeCount(Api api) throws JsonParseException, JsonMappingException, IOException {
		int likeCount = 0;
		if (likes != null) {
			likeCount += likes.getData().length;
			LikeList likeList = likes;
			while (likeList.hasMore()) {
				likeList = api.download(likes.getNext(), LikeList.class);
				likeCount += likeList.getData().length;
			}
		}
		return likeCount;
	}

	public CommentList getCommentList() {
		return comments;
	}

	public boolean hasComments() {
		return comments != null;
	}
}
