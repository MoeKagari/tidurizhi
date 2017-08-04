package tdrz.update;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.json.JsonObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.widgets.Display;

import tdrz.gui.window.main.ApplicationMain;
import tdrz.update.data.ApiData;
import tdrz.update.data.ApiDataListener;
import tool.FunctionUtils;

public class GlobalContextUpdater {
	private final static Logger log = LogManager.getLogger(GlobalContextUpdater.class);
	private final static SimpleDateFormat JSONFILETIMEFORMAT = new SimpleDateFormat("yyMMdd_HHmmss.SSS");
	private final static List<ApiDataListener> LISTENERS = new ArrayList<>();

	public static void addListener(ApiDataListener listener) {
		LISTENERS.add(listener);
	}

	public static void removeListener(ApiDataListener listener) {
		LISTENERS.remove(listener);
	}

	public static void update(long time, String serverName, String uri, Map<String, String> headers, ByteArrayOutputStream requestBody, ByteArrayOutputStream responseBody) {
		ApiData data;
		try {
			data = new ApiData(time, serverName, uri, headers, requestBody, responseBody);
		} catch (Exception e) {
			log.warn(uri + "\r\n" + requestBody + "\r\n" + responseBody, e);
			return;
		}

		try {
//			String filename = String.format("json\\%s%s_%s.json", //
//					FunctionUtils.notNull(data.getType(), type -> "", "undefined\\"), //
//					JSONFILETIMEFORMAT.getNumberFormat().format(data.getTime()),//
//					FunctionUtils.notNull(data.getType(), DataType::toString, ""));
//			FileUtils.write(new File(filename), data.toString(), Charset.forName("utf-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (data.getType() == null) {
			System.out.println("Ｘ定义的api : " + data.getUri());
			return;
		}
		System.out.println("Ｏ定义的api : " + data.getUri());

		JsonObject json = data.getJsonObject();
		int api_result = json.getInt("api_result");
		if (api_result != 1) {
			ApplicationMain.main.printMessage(String.format("猫了,猫娘: %d", api_result), true);
			log.warn(String.format("%s,猫了,%d,api-%s", JSONFILETIMEFORMAT.format(data.getTime()), api_result, data.getType()));
			return;
		}

		try {
			GlobalContext.update(data);
		} catch (Exception e) {
			log.warn(data);
			log.warn(String.format("api-%s更新错误", data.getType()), e);
			return;
		}
		LISTENERS.parallelStream().map(listener -> FunctionUtils.getRunnable(listener::update, data.getType())).forEach(Display.getDefault()::asyncExec);
	}
}
