package com.jtransc.media.limelibgdx;

import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpStatus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LimeHttpResponse implements Net.HttpResponse {

	private HttpURLConnection connection;
	private Map<String, List<String>> headers;

	LimeHttpResponse(HttpURLConnection connection) {
		this.connection = connection;
	}

	@Override
	public byte[] getResult() {
		try {
			InputStream is = connection.getInputStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];

			int read = 0;
			while (read >= 0) {
				read = is.read(buffer, 0, buffer.length);
				if(read > 0) {
					baos.write(buffer, 0, read);
				}
			}

			return baos.toByteArray();
		} catch (Exception e) {
			// ignore
		}

		return new byte[0];
	}

	@Override
	public String getResultAsString() {
		return new String(getResult());
	}

	@Override
	public InputStream getResultAsStream() {
		try {
			return connection.getInputStream();
		} catch (Exception e) {
			// ignore
		}
		return new ByteArrayInputStream(new byte[0]);
	}

	@Override
	public HttpStatus getStatus() {
		int responseCode = 0;
		try {
			responseCode = connection.getResponseCode();
		} catch (Exception e) {
			// ignore
		}
		return new HttpStatus(responseCode);
	}

	@Override
	public String getHeader(String name) {
		return connection.getHeaderField(name);
	}

	@Override
	public Map<String, List<String>> getHeaders() {
		if(headers == null) {
			headers = new HashMap<>();
			int count = 0;
			String key = "";
			while (key != null) {
				key = connection.getHeaderFieldKey(count);
				if (key != null) {
					String value = connection.getHeaderField(key);
					List<String> values = new ArrayList<>();
					values.add(value);
					headers.put(key, values);
				}
				count++;
			}
		}
		return headers;
	}
}
