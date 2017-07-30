package tdrz.utils;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;

public class JsonUtils {
	public static int[] getIntArray(JsonArray array) {
		return array.getValuesAs(JsonNumber.class).stream().mapToInt(JsonNumber::intValue).toArray();
	}

	public static int[] getIntArray(JsonObject json, String key) {
		return getIntArray(json.getJsonArray(key));
	}

	public static double[] getDoubleArray(JsonArray array) {
		return array.getValuesAs(JsonNumber.class).stream().mapToDouble(JsonNumber::doubleValue).toArray();
	}

	public static double[] getDoubleArray(JsonObject json, String key) {
		return getDoubleArray(json.getJsonArray(key));
	}

	public static String[] getStringArray(JsonArray array) {
		return array.getValuesAs(JsonString.class).stream().map(JsonString::getString).toArray(String[]::new);
	}

	public static String[] getStringArray(JsonObject json, String key) {
		return getStringArray(json.getJsonArray(key));
	}
}
