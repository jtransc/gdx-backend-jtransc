package com.jtransc.media.limelibgdx;

import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class LimeNet implements Net {
	@Override
	public void sendHttpRequest(HttpRequest httpRequest, final HttpResponseListener httpResponseListener) {
		System.out.println("LimeNet.sendHttpRequest(" + httpRequest.getUrl() + ")");
		try {
			URL url = new URL(httpRequest.getUrl());
			final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod(httpRequest.getMethod());

			if (httpRequest.getTimeOut() != 0) {
				connection.setConnectTimeout(httpRequest.getTimeOut());
			}

			for (Map.Entry<String, String> entry : httpRequest.getHeaders().entrySet()) {
				connection.setRequestProperty(entry.getKey(), entry.getValue());
			}

			OutputStream out = connection.getOutputStream();
			InputStream input = httpRequest.getContentStream();

			if (input != null) {
				for (int i = 0; i < httpRequest.getContentLength(); ++i) {
					out.write(input.read());
				}
			} else if (httpRequest.getContent() != null) {
				out.write(httpRequest.getContent().getBytes());
			}

			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						connection.connect();
						connection.getResponseCode();
						System.out.println("LimeNet.sendHttpRequest(" + httpRequest.getUrl() + ") successful.");
					} catch (Exception e) {
						httpResponseListener.failed(e);
					}

					httpResponseListener.handleHttpResponse(new LimeHttpResponse(connection));
				}
			}).start();

		} catch (Exception e) {
			httpResponseListener.failed(e);
		}
	}

	@Override
	public void cancelHttpRequest(HttpRequest httpRequest) {
//		throw new NotImplementedException();
	}

	@Override
	public ServerSocket newServerSocket(Protocol protocol, String s, int i, ServerSocketHints serverSocketHints) {
		throw new NotImplementedException();
	}

	@Override
	public ServerSocket newServerSocket(Protocol protocol, int i, ServerSocketHints serverSocketHints) {
		throw new NotImplementedException();
	}

	@Override
	public Socket newClientSocket(Protocol protocol, String s, int i, SocketHints socketHints) {
		throw new NotImplementedException();
	}

	@Override
	public boolean openURI(String s) {
		throw new NotImplementedException();
	}
}
