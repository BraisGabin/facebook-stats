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

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommentList {

	@JsonProperty("data")
	private Comment[] data;
	@JsonProperty("paging")
	private Paging paging;

	public boolean hasMore() {
		return paging.hasNext();
	}

	public String getNext() {
		return paging.getNext();
	}

	public Comment[] getData() {
		return data;
	}

	public boolean hasComments() {
		return data != null;
	}
}
