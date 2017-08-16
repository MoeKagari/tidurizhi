package tdrz.update.context.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server.CommunicationHandler;
import tdrz.update.context.GlobalContextUpdater;

public class TDRZApiHandler extends CommunicationHandler {
	private final long time = System.currentTimeMillis();

	public TDRZApiHandler(String serverName, String uri) {
		super(serverName, uri);
	}

	@Override
	public void onSuccess(HttpServletRequest httpRequest, HttpServletResponse httpResponse, Map<String, String> headers, ByteArrayOutputStream requestBody, ByteArrayOutputStream responseBody) throws IOException {
		Thread processDataThread = new Thread(() -> GlobalContextUpdater.update(this.time, this.serverName, this.uri, headers, requestBody, responseBody));
		//processDataThread.setDaemon(false);
		processDataThread.start();
	}

	@Override
	public boolean storeRequestBody() {
		return true;
	}

	@Override
	public boolean storeResponseBody() {
		return true;
	}

	@Override
	public boolean storeResponseHeaders() {
		return true;
	}
}
