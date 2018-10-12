package scripts.usa.api.web.captcha;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tribot.api.General;

import scripts.usa.api.web.RandomUserAgent;
import scripts.usa.api.web.WebUtils;

public class AccountCreator {

	private String API_KEY;
	private ProxyAccount proxy = null;
	private int TIMEOUT = 10000;
	private final String PAGE_URL = "https://secure.runescape.com/m=account-creation/g=oldscape/create_account";
	private final String GOOGLE_KEY = "6LccFA0TAAAAAHEwUJx_c1TfTBWMTAOIphwTtd1b";

	public AccountCreator(String API_KEY) {
		this.API_KEY = API_KEY;
	}

	public AccountCreator(String API_KEY, ProxyAccount proxy) {
		this.API_KEY = API_KEY;
		this.proxy = proxy;
	}

	public enum Result {
		SUCCESS,
		TIMEOUT,
		EMAIL_IN_USE,
		USERNAME_NOT_AVAILABLE,
		INVALID_API_KEY,
		INVALID_CAPTCHA,
		INVALID_EMAIL,
		UNAVAILABLE,
		BLOCKED
	}

	public Result create(int age, String display, String email, String password) {
		System.out.println("Attempting to create '" + display + "'");
		System.out.println("Getting reCAPTCHA...");
		Response captcha = null;
		try {
			captcha = new Response(get("http://2captcha.com/in.php?key=" + this.API_KEY +
					"&method=userrecaptcha&googlekey=" +
					this.GOOGLE_KEY +
					"&pageurl=" +
					this.PAGE_URL));
		}
		catch (Exception e) {
			return Result.INVALID_API_KEY;
		}
		if (!captcha.isOk()) {
			System.out.println("Unable to get reCAPTCHA!");
			return Result.INVALID_API_KEY;
		}
		System.out.println("Getting solution...");
		Response code = null;
		while ((System.currentTimeMillis() + TIMEOUT) > System.currentTimeMillis()) {
			try {
				code = new Response(get("http://2captcha.com/res.php?key=" + this.API_KEY + "&action=get&id=" + captcha.getResult()));
				if (!code.getResponse().equals("CAPCHA_NOT_READY"))
					break;
			}
			catch (Exception e) {
				return Result.UNAVAILABLE;
			}
			General.sleep(1000);
		}
		if (!code.isOk()) {
			System.out.println("reCAPTCHA is invalid!");
			return Result.INVALID_CAPTCHA;
		}
		System.out.println("Creating account...");
		Response response = null;
		try {
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("trialactive", true);
			params.put("trialactive", true);
			params.put("onlyOneEmail", 1);
			params.put("displayname_present", true);
			params.put("age", age);
			params.put("displayname", display);
			params.put("email1", email);
			params.put("password1", password);
			params.put("password2", password);
			params.put("agree_email", "on");
			params.put("g-recaptcha-response", code.getResult());
			params.put("submit", "Join Now");
			response = new Response(post(PAGE_URL, params));
		}
		catch (Exception e) {
			if (e.getClass().toString().contains("FileNotFoundException"))
				return Result.BLOCKED;
			return Result.UNAVAILABLE;
		}
		if (response.getResponse().isEmpty()) {
			System.out.println("Request timed out!");
			return Result.TIMEOUT;
		}
		if (response.getResponse().contains("Your email address has already been taken. Please choose a different address.")) {
			System.out.println("Email address '" + email + "' is in use!");
			return Result.EMAIL_IN_USE;
		}
		if (response.getResponse().contains("The email address you have entered is not valid.")) {
			System.out.println("The email address, '" + email + "', is not valid!");
			return Result.INVALID_EMAIL;
		}
		if (response.getResponse().contains("Sorry, that character name is not available.")) {
			System.out.println("Character name '" + display + "' is not available!");
			return Result.USERNAME_NOT_AVAILABLE;
		}
		if (response.getResponse().contains("If your confirmation email has not arrived please check your spam filter.")) {
			System.out.println("Successfully created account!");
			return Result.SUCCESS;
		}
		System.out.println("Error during creation!");
		return Result.UNAVAILABLE;
	}

	public String post(String url, Map<String, Object> body) throws Exception {
		StringBuilder params = new StringBuilder();
		for (Map.Entry<String, Object> param : body.entrySet()) {
			if (params.length() != 0)
				params.append('&');
			params.append(URLEncoder.encode(param.getKey(), "UTF-8"));
			params.append('=');
			params.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
		}
		byte[] contentBytes = params.toString().getBytes("UTF-8");
		HttpURLConnection conn;
		if (proxy != null) {
			Authenticator authenticator = new Authenticator() {

				public PasswordAuthentication getPasswordAuthentication() {
					return (new PasswordAuthentication(proxy.getUsername(), proxy.getPassword().toCharArray()));
				}
			};
			Authenticator.setDefault(authenticator);
			conn = (HttpURLConnection) new URL(url)
					.openConnection(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxy.getIP(), Integer.parseInt(proxy.getPort()))));
		}
		else {
			conn = (HttpURLConnection) new URL(url).openConnection();
		}
		conn.setDoOutput(true);
		conn.setConnectTimeout(TIMEOUT);
		conn.setReadTimeout(TIMEOUT);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		conn.setRequestProperty("Accept-Language", "en-US,en;q=0.8");
		conn.setRequestProperty("Cache-Control", "max-age=0");
		conn.setRequestProperty("Connection", "keep-alive");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", String.valueOf(contentBytes.length));
		conn.setRequestProperty("Host", "secure.runescape.com");
		conn.setRequestProperty("Origin", "https://secure.runescape.com");
		conn.setRequestProperty("Referer", "https://secure.runescape.com/m=account-creation/g=oldscape/create_account");
		conn.setRequestProperty("Upgrade-Insecure-Request", "1");
		conn.setRequestProperty("User-Agent", RandomUserAgent.getRandomUserAgent());
		conn.getOutputStream().write(contentBytes);
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
		String line;
		StringBuilder sb = new StringBuilder();
		while ((line = in.readLine()) != null)
			sb.append(line);
		in.close();
		return sb.toString();
	}

	public String get(String url) throws Exception {
		URLConnection conn = new URL(url).openConnection();
		conn.setConnectTimeout(TIMEOUT);
		conn.setReadTimeout(TIMEOUT);
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		StringBuilder sb = new StringBuilder();
		while ((line = in.readLine()) != null)
			sb.append(line);
		in.close();
		return sb.toString();
	}

	public static String generateString(boolean display, int index, String input) {
		String letters = "abcdefghijklmnopqrstuvxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String numbers = "0123456789";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i != input.length(); i++) {
			if (input.charAt(i) == '&') {
				sb.append(WebUtils.getRandomWord());
			}
			else if (input.charAt(i) == '#') {
				sb.append(numbers.charAt(General.random(0, numbers.length() - 1)));
			}
			else if (input.charAt(i) == '$') {
				sb.append(letters.charAt(General.random(0, letters.length() - 1)));
			}
			else if (input.charAt(i) == '?') {
				int r = General.random(1, (display ? 3 : 2));
				if (r == 1) {
					sb.append(numbers.charAt(General.random(0, numbers.length() - 1)));
				}
				else if (r == 2) {
					sb.append(letters.charAt(General.random(0, letters.length() - 1)));
				}
				else {
					sb.append(" ");
				}
			}
			else if (input.charAt(i) == '{') {
				sb.append(WebUtils.getRandomWord("" + input.charAt(i + 1)));
				i += 2;
			}
			else {
				sb.append(input.charAt(i));
			}
		}
		String line = sb.toString();
		String pattern = "(\\[\\d+\\])";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(line);
		while (m.find()) {
			int i = Integer.parseInt(m.group(0).replace("[", "").replace("]", "")) + index;
			line = line.replace(m.group(0), Integer.toString(i));
		}
		sb = new StringBuilder(line);
		if (display) {
			if (sb == null || sb.length() > 12)
				return null;
			for (int i = 0; i < sb.length(); i++) {
				char c = sb.charAt(i);
				if (Character.isLetter(c)) {
					sb.deleteCharAt(i);
					sb.insert(i, Character.toUpperCase(c));
					break;
				}
			}
		}
		return sb.toString();
	}

	public static String obfuscatePassword(String password) {
		return new String(new char[password.length()]).replace("\0", "*");
	}
}
