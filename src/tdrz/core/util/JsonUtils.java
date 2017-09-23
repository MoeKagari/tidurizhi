package tdrz.core.util;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

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

	public static int[] dissociateIntArray(JsonObject json, String key) {
		int[] intArray = null;
		if (json.containsKey(key)) {
			JsonArray array = json.getJsonArray(key);
			intArray = new int[array.size()];
			for (int i = 0; i < array.size(); i++) {
				intArray[i] = dissociateInt(array.get(i));
			}
		}
		return intArray;
	}

	public static int dissociateInt(JsonValue value) {
		switch (value.getValueType()) {
			case STRING:
				return Integer.parseInt(((JsonString) value).getString());
			case NUMBER:
				return ((JsonNumber) value).intValue();
			default:
				throw new RuntimeException("只接受STRING和NUMBER两种JsonValue");
		}
	}
}
