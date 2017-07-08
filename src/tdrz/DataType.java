package tdrz;

import java.util.Arrays;
import java.util.HashMap;

public enum DataType {
	;

	/*--------------------------------------------------------------------------------------------------------------*/
	public static final HashMap<String, DataType> TYPEMAP = new HashMap<>();
	static {
		Arrays.stream(DataType.values()).forEach(type -> TYPEMAP.put(type.uri, type));
	}

	private String uri;

	private DataType(String uri) {
		this.uri = uri;
	}
}
