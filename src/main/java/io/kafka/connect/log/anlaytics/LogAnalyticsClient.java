
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

package io.kafka.connect.log.anlaytics;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogAnalyticsClient {

	private static final Logger log = LoggerFactory.getLogger(LogAnalyticsClient.class);

	final String key;
	final String workspace;

	public LogAnalyticsClient(String workspaceId, String base64Key) {
		this.key = base64Key;
		this.workspace = workspaceId;
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	private HttpsURLConnection getConnection() throws IOException {
		String url = "https://" + this.workspace + ".ods.opinsights.azure.com/api/logs?api-version=2016-04-01";
		URL objUrl = new URL(url);
		return (HttpsURLConnection) objUrl.openConnection();
	}

	private static final String HMAC_SHA256_ALG = "HmacSHA256";

	// The Java built-in DateTimeFormatter.RFC_1123_DATE_TIME encodes day-of-month
	// as a single digit, which the API doesn't like
	public static final SimpleDateFormat RFC_1123_DATE_TIME = new SimpleDateFormat("E, dd MMM YYYY HH:mm:ss zzz");

	/**
	 * 
	 * @param workspaceId
	 * @param key
	 * @param contentLength
	 * @param rfc1123Date
	 * @return
	 */
	static String createAuthorization(String workspaceId, String key, int contentLength, String rfc1123Date) {
		try {
			// Documentation:
			// https://docs.microsoft.com/en-us/rest/api/loganalytics/create-request
			String signature = String.format("POST\n%d\napplication/json\nx-ms-date:%s\n/api/logs", contentLength,
					rfc1123Date);

			byte[] decodedBytes = Base64.decodeBase64(key);
			Mac hasher = Mac.getInstance(HMAC_SHA256_ALG);
			hasher.init(new SecretKeySpec(decodedBytes, HMAC_SHA256_ALG));
			byte[] hash = hasher.doFinal(signature.getBytes());

			String encodedHash = DatatypeConverter.printBase64Binary(hash);
			return String.format("SharedKey %s:%s", workspaceId, encodedHash);

		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	/**
	 * 
	 * @param rawJson
	 * @param logType
	 */
	public void execute(String rawJson, String logType) {
		int bodyLength = rawJson.length();
		RFC_1123_DATE_TIME.setTimeZone(TimeZone.getTimeZone("GMT"));
		String nowRfc1123 = RFC_1123_DATE_TIME.format(new Date());

		String createAuthorization = createAuthorization(this.workspace, this.key, bodyLength, nowRfc1123);

		try {
			HttpsURLConnection con = getConnection();
			con.setDoOutput(true);
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Log-Type", logType);
			con.setRequestProperty("x-ms-date", nowRfc1123);
			con.setRequestProperty("Authorization", createAuthorization);

			try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
				wr.writeBytes(rawJson);
				wr.flush();
			}

			int responseCode = con.getResponseCode();
			log.info("Response Code :" + responseCode);
			if (responseCode != HttpStatus.OK_200) {
				log.info(con.getResponseMessage());
			}

		} catch (Exception e) {
			System.out.println("Catch statement: " + e);
		}
	}

}