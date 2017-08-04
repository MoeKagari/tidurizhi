package tdrz.update.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import javax.json.Json;
import javax.json.JsonObject;

import tool.FunctionUtils;

public class ApiData {
	private final long time;
	private final String serverName;
	private final String uri;
	private final DataType type;
	private Map<String, String> fields = null;
	private JsonObject json = null;

	public ApiData(long time, String serverName, String uri, Map<String, String> headers, ByteArrayOutputStream requestBody, ByteArrayOutputStream responseBody) {
		this.time = time;
		this.serverName = serverName;
		this.uri = uri;
		this.type = DataType.getType(uri);

		try {
			this.fields = Arrays.stream(URLDecoder.decode(new String(requestBody.toByteArray()), "utf-8").trim().split("&"))//
					.map(param -> param.split("="))//
					.filter(pair -> pair.length == 2)//
					.filter(pair -> FunctionUtils.isFalse("api_token".equals(pair[0]) || "api_verno".equals(pair[0])))//
					.collect(Collectors.toMap(pair -> pair[0], pair -> pair[1]));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("fields解析出错\r\n" + new String(requestBody.toByteArray()));
		}

		try {
			InputStream stream = new ByteArrayInputStream(responseBody.toByteArray());
			if ("gzip".equalsIgnoreCase(headers.get("Content-Encoding"))) {
				stream = new GZIPInputStream(stream);
			}

			int b;
			while ((b = stream.read()) != -1) {
				if (b == '=') {
					break;
				}
			}

			this.json = Json.createReader(stream).readObject();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("json解析出错\r\n" + new String(responseBody.toByteArray()));
		}
	}

	@Override
	public String toString() {
		return (this.type == null ? "null" : String.format("%s,%s", this.type, this.type.getDetail())) + "\r\n"//
				+ this.getUrl() + "\r\n"//
				+ FunctionUtils.notNull(this.fields, HashMap<String, String>::new, "null") + "\r\n" //
				+ String.valueOf(this.json);
	}

	public String getServerName() {
		return this.serverName;
	}

	public String getUri() {
		return this.uri;
	}

	public String getUrl() {
		return this.serverName + this.uri;
	}

	public DataType getType() {
		return this.type;
	}

	public long getTime() {
		return this.time;
	}

	public JsonObject getJsonObject() {
		return this.json;
	}

	public String getField(String key) {
		return this.fields == null ? null : this.fields.get(key);
	}
}
