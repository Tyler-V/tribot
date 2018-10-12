package scripts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

	public class ItemPrice {
		private static final String LINK = "http://forums.zybez.net/pages/2007-price-guide?id=";
		private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.57 Safari/537.17";

		private final static Pattern NAME = Pattern
				.compile("Price Guide: (.+?)</h1>");
		private final static Pattern PRICE = Pattern
				.compile("Recent Trade Price: ~(.+?) GP");

		private final String name;
		private static int id;
		private static int price;

		private ItemPrice(final String name, final int id, final int price) {
			this.name = name;
			this.id = id;
			this.price = price;
		}

		public String getName() {
			return name;
		}

		public int getId() {
			return id;
		}

		public int getPrice() {
			return price;
		}

		public final static ItemPrice lookup(final int id) {
			try {
				final BufferedReader br = getReader(id);
				br.skip(18700);

				Matcher m = NAME.matcher(br.readLine());
				String name = "";
				if (m.find()) {
					name = m.group(1);
				}

				String line = "";
				while (!line.contains("Recent Trade Price")) {
					line = br.readLine();
				}

				m = PRICE.matcher(line);
				int price = 0;
				if (m.find()) {
					price = Integer.parseInt(m.group(1).replaceAll(",", ""));
				}
				return new ItemPrice(name, id, price);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		private static BufferedReader getReader(final int id) {
			try {
				final URL url = new URL(LINK + id);
				final URLConnection con = url.openConnection();
				con.setRequestProperty("User-Agent", USER_AGENT);
				final Reader inr = new InputStreamReader(con.getInputStream());
				return new BufferedReader(inr);
			} catch (IOException e) {
				System.out.println("Item not found.");
			}
			return null;
		}
	}