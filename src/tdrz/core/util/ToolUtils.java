package tdrz.core.util;

import java.util.Arrays;
import java.util.function.DoubleToIntFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;

public class ToolUtils {
	public static int[] doubleToInteger(double[] ds, DoubleToIntFunction fun) {
		return Arrays.stream(ds).mapToInt(fun).toArray();
	}

	public static int[] doubleToIntegerFloor(double[] ds) {
		return doubleToInteger(ds, d -> (int) Math.floor(d));
	}

	public static <S> int[] toIntArray(S[] ss, ToIntFunction<S> fun) {
		return Arrays.stream(ss).mapToInt(fun).toArray();
	}

	public static String[] toStringArray(int[] is, IntFunction<String> fun) {
		return Arrays.stream(is).mapToObj(fun).toArray(String[]::new);
	}

	public static <S> String[] toStringArray(S[] ss, Function<S, String> fun) {
		return Arrays.stream(ss).map(fun).toArray(String[]::new);
	}

	public static String[] toStringArray(int length, IntFunction<String> fun) {
		return IntStream.range(0, length).mapToObj(fun).toArray(String[]::new);
	}
}
