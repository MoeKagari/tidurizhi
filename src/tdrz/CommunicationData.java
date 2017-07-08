package tdrz;

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

public class CommunicationData {
	private final long time;
	private final String serverName;
	private final String uri;
	private final DataType type;
	private Map<String, String> fields = null;
	private JsonObject json = null;

	public CommunicationData(long time, String serverName, String uri, Map<String, String> headers, ByteArrayOutputStream requestBody, ByteArrayOutputStream responseBody) {
		this.time = time;
		this.serverName = serverName;
		this.uri = uri;
		this.type = DataType.TYPEMAP.get(uri);

		try {
			this.fields = Arrays.stream(URLDecoder.decode(new String(requestBody.toByteArray()), "utf-8").trim().split("&"))//
					.map(param -> param.split("=")).filter(pair -> FunctionUtils.isFalse("api_token".equalsIgnoreCase(pair[0])) && FunctionUtils.isFalse("api_verno".equalsIgnoreCase(pair[0])))//
					.collect(Collectors.toMap(pair -> pair[0], pair -> pair.length == 2 ? pair[1] : null, (a, b) -> String.format("%s,%s", a, b)));
		} catch (Exception e) {
			this.fields = null;
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
			this.json = null;
		}
	}

	@Override
	public String toString() {
		return FunctionUtils.notNull(this.type, DataType::toString, "type==null") + "\r\n"//
				+ this.uri + "\r\n"//
				+ FunctionUtils.notNull(this.fields, HashMap<String, String>::new, "fields==null") + "\r\n" //
				+ FunctionUtils.notNull(this.json, JsonObject::toString, "json==null");
	}

	public JsonObject getJsonObject() {
		return this.json;
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

	public Map<String, String> getFields() {
		return this.fields;
	}

	public String getField(String key) {
		return this.fields == null ? null : this.fields.get(key);
	}

	public long getTime() {
		return this.time;
	}
}
