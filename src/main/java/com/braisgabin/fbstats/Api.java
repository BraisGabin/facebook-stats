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

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class Api {
	private final OkHttpClient client;
	private final String accessToken;
	private final ObjectMapper mapper;

	public Api(String accessToken) {
		this.accessToken = accessToken;

		this.client = new OkHttpClient();

		this.mapper = new ObjectMapper();
		this.mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
		this.mapper.setVisibility(PropertyAccessor.FIELD, Visibility.NONE);
		this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		this.mapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, true);
	}

	public <T> T download(String url, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
		Request request = new Request.Builder()
				.url(url)
				.addHeader("Authorization", "OAuth " + accessToken).build();

		Response response = client.newCall(request).execute();
		if (!response.isSuccessful()) {
			throw new RuntimeException(response.body().string());
		}
		return mapper.readValue(response.body().byteStream(), clazz);
	}
}
