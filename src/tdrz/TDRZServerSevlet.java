package tdrz;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;

import server.CommunicationHandler;
import server.ProxyServerServlet;
import server.ServerConfig;
import tool.FunctionUtils;

@SuppressWarnings("serial")
public class TDRZServerSevlet extends ProxyServerServlet {
	/** 游戏现有的服务器 */
	public final static List<String> GAME_SERVER_LIST = Arrays.asList(//
			"125.6.184.16", "125.6.187.205", "125.6.187.229", "125.6.187.253", //
			"125.6.188.25", "125.6.189.7", "125.6.189.39", "125.6.189.71", //
			"125.6.189.103", "125.6.189.135", "125.6.189.167", "125.6.189.215", //
			"125.6.189.247", "203.104.209.71", "203.104.209.87", "203.104.248.135", //
			"203.104.209.23", "203.104.209.39", "203.104.209.55", "203.104.209.102" //
	);

	public static void main(String[] args) {
		try {
			new TDRZServerSevlet(new ServerConfig(() -> 22222, () -> false, () -> "127.0.0.1", () -> 1080)).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TDRZServerSevlet(ServerConfig config) {
		super(config);
	}

	@Override
	public CommunicationHandler getHandler(String serverName, String uri) {
		if (GAME_SERVER_LIST.contains(serverName)) {
			if (uri.startsWith("/kcsapi/")) {
				return new TDRZApiHandler(serverName, uri);
			}
		}
		return super.getHandler(serverName, uri);
	}

	private static class TDRZApiHandler extends CommunicationHandler {
		private final static SimpleDateFormat JSONFILETIMEFORMAT = new SimpleDateFormat("yyMMdd_HHmmss.SSS");
		private final long time = System.currentTimeMillis();

		public TDRZApiHandler(String serverName, String uri) {
			super(serverName, uri);
		}

		@Override
		public void onSuccess(HttpServletRequest httpRequest, HttpServletResponse httpResponse, Map<String, String> headers, ByteArrayOutputStream requestBody, ByteArrayOutputStream responseBody) throws IOException {
			Thread processDataThread = new Thread(() -> processData(this.time, this.serverName, this.uri, headers, requestBody, responseBody));
			processDataThread.setDaemon(false);
			processDataThread.start();
		}

		private static void processData(long time, String serverName, String uri, Map<String, String> headers, ByteArrayOutputStream requestBody, ByteArrayOutputStream responseBody) {
			try {
				CommunicationData data = new CommunicationData(time, serverName, uri, headers, requestBody, responseBody);
				String filename = String.format("json\\%s%s_%s.json", //
						FunctionUtils.notNull(data.getType(), type -> "", "undefined\\"), //
						JSONFILETIMEFORMAT.getNumberFormat().format(data.time),//
						FunctionUtils.notNull(data.getType(), DataType::toString, ""));
				FileUtils.write(new File(filename), data.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
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
}
