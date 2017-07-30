package tdrz.logic;

import tdrz.config.AppConstants;

public class TimeString {
	private static final int ONE_HOUR = 60 * 60;
	private static final int ONE_MINUTES = 60;

	private static final String[] format0 = { "天", "时", "分", "秒" };
	private static final String[] format1 = { ":", ":", ":", " " };
	private static final String[] format2 = { "∶", "∶", "∶", " " };

	public static String toDateRestString(long rest, String defaultString) {
		if (rest < 0) return defaultString;

		long d, h, m, s;//天时分秒
		String[] se;

		int timeStyle = 0;

		d = 0;
		h = rest / ONE_HOUR;
		m = rest % ONE_HOUR / ONE_MINUTES;
		s = rest % ONE_HOUR % ONE_MINUTES;

		switch (timeStyle) {
			case 0:
				se = format0;
				break;
			case 1:
				se = format1;
				break;
			case 2:
				se = format2;
				break;
			default:
				se = format0;
				break;
		}

		if (d != 0) {
			return String.format("%d%s%02d%s%02d%s%02d%s", d, se[0], h, se[1], m, se[2], s, se[3]);
		}
		if (h != 0) {
			return String.format("%d%s%02d%s%02d%s", h, se[1], m, se[2], s, se[3]);
		}
		if (m != 0) {
			return String.format("%d%s%02d%s", m, se[2], s, se[3]);
		}
		return String.format("%d%s", s, se[3]);
	}

	public static String toDateRestString(long rest) {
		return toDateRestString(rest, "");
	}

	public static long getCurrentTime() {
		return System.currentTimeMillis();
	}

	public static String formatForTable(long time) {
		return AppConstants.TABLE_TIME_FORMAT.format(time);
	}
}
