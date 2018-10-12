package scripts.usa.api.web;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;

public class WebUtils {

	public static void open(String url) {
		try {
			java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static BufferedImage getImage(String url) {
		try {
			return ImageIO.read(new URL(url));
		}
		catch (IOException e) {
			return null;
		}
	}

	public static String read(String url, String... requestProperties) {
		try {
			return read(new URL(url), requestProperties);
		}
		catch (MalformedURLException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

	public static String read(URL url, String... requestProperties) {
		if (url == null) {
			return null;
		}
		try {
			URLConnection cn = url.openConnection();
			for (String property : requestProperties) {
				String key = property.split(",,")[0];
				String value = property.split(",,")[1];
				cn.setRequestProperty(key, value);
			}
			StringBuilder sb = new StringBuilder();
			InputStreamReader in = new InputStreamReader(cn.getInputStream());
			BufferedReader br = new BufferedReader(in);
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			in.close();
			br.close();
			return sb.toString();
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

	public static String getRandomWord(String length) {
		try {
			String url;
			if (length != null)
				url = "http://www.setgetgo.com/randomword/get.php?len=" + length;
			else
				url = "http://www.setgetgo.com/randomword/get.php";
			URLConnection conn = new URL(url).openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			StringBuilder sb = new StringBuilder();
			while ((line = in.readLine()) != null)
				sb.append(line);
			in.close();
			return capitalizeFirstLetter(sb.toString());
		}
		catch (Exception e) {
			System.out.println(e.toString());
		}
		return "";
	}

	public static String getRandomWord() {
		return getRandomWord(null);
	}

	public static String capitalizeFirstLetter(String original) {
		return original.length() == 0 ? original : original.substring(0, 1).toUpperCase() + original.substring(1);
	}
}
