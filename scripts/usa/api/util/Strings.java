package scripts.usa.api.util;

import java.text.NumberFormat;
import java.util.Locale;

public class Strings {

	public static String format(int number) {
		return NumberFormat.getNumberInstance(Locale.US).format(number);
	}

	public static String format(double number) {
		return NumberFormat.getNumberInstance(Locale.US).format((int) number);
	}

	private static char getLastChar(StringBuilder sb) {
		if (sb.toString().isEmpty())
			return Character.SPACE_SEPARATOR;
		return sb.toString().charAt(sb.length() - 1);
	}

	public static String toProperCase(String input) {
		StringBuilder sb = new StringBuilder();
		for (char c : input.toCharArray()) {
			if (c == '_' || Character.isWhitespace(c)) {
				sb.append(" ");
			}
			else {
				char lastChar = getLastChar(sb);
				sb.append((lastChar == '_' || Character.isWhitespace(lastChar)) ? Character.toUpperCase(c) : Character.toLowerCase(c));
			}
		}
		return sb.toString();
	}

	public static String toSentenceCase(String input) {
		StringBuilder sb = new StringBuilder();
		for (char c : input.toCharArray()) {
			if (c == '_' || Character.isWhitespace(c)) {
				sb.append(" ");
			}
			else {
				sb.append(sb.length() == 0 ? Character.toUpperCase(c) : Character.toLowerCase(c));
			}
		}
		return sb.toString();
	}

	public static String toEnumCase(String text) {
		StringBuilder sb = new StringBuilder();
		for (char c : text.toCharArray()) {
			if (c == '_' || Character.isWhitespace(c)) {
				sb.append("_");
			}
			else {
				sb.append(Character.toUpperCase(c));
			}
		}
		return sb.toString();
	}
}
