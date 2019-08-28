/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.kafka.connect.log.anlaytics.sink;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.connect.json.JsonConverter;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.apache.kafka.connect.storage.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;

import io.kafka.connect.log.anlaytics.LogAnalyticsClient;
import io.kafka.connect.log.anlaytics.config.LogAnalyticsSinkConfig;

public class LogAnlayticsSinkTask extends SinkTask {

	private static final Logger log = LoggerFactory.getLogger(LogAnlayticsSinkTask.class);

	private static Converter JSON_CONVERTER = null;

	private LogAnalyticsClient laClient;

	@Override
	public String version() {
		return LogAnalyticsSinkConnector.VERSION;
	}

	@Override
	public void start(Map<String, String> properties) {
		log.info("log analytics read started");
		LogAnalyticsSinkConfig laSinkConfig = new LogAnalyticsSinkConfig(properties);

		this.laClient = new LogAnalyticsClient(laSinkConfig.getPropertyValue("workspace.id"),
				laSinkConfig.getPropertyValue("workspace.key"));

	}

	@Override
	public void put(Collection<SinkRecord> records) {
		try {
			JSON_CONVERTER = new JsonConverter();
			JSON_CONVERTER.configure(Collections.singletonMap("schemas.enable", "false"), false);

			Iterables.partition(records, 100).forEach(r -> {

				Map<String, String> messages = new HashMap<>();
				r.stream().forEach(e -> appendMessage(messages, e));
				messages.forEach((k, v) -> this.laClient.execute("[" + v + "]", k));

			});

		} catch (Exception e) {
			log.error("Exception while persisting records", records, e);
			throw new RuntimeException(e);
		}

	}

	/**
	 * 
	 * @param messages
	 * @param e
	 */
	private void appendMessage(Map<String, String> messages, SinkRecord e) {
		final String payload = new String(JSON_CONVERTER.fromConnectData(e.topic(), e.valueSchema(), e.value()),
				StandardCharsets.UTF_8);
		String message = messages.containsKey(e.topic()) ? messages.get(e.topic()) + "," + payload : payload;
		messages.put(e.topic(), message);
	}

	@Override
	public void stop() {
		// NO-OP
	}
}
