package scripts.usa.api.painting.media;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.net.URL;

public class Fonts {

	public enum Google {

		COURGETTE("Courgette", "https://github.com/google/fonts/raw/master/ofl/courgette/Courgette-Regular.ttf"),

		ROBOTO("Roboto", "https://github.com/google/fonts/raw/master/apache/roboto/Roboto-Regular.ttf");

		private final String name;
		private final String url;
		private Font font;

		Google(String name, String url) {
			this.name = name;
			this.url = url;
			this.font = this.getFont();
		}

		public String getURL() {
			return this.url;
		}

		public String getName() {
			return this.name;
		}

		public Font getFont() {
			if (this.font != null) {
				return this.font;
			}
			Font font = Fonts.getFont(this.url);
			if (font != null) {
				this.font = font;
			}
			return this.font;
		}
	}

	public static Font getFont(String url) {
		try {
			URL fontUrl = new URL(url);
			Font font = Font.createFont(Font.TRUETYPE_FONT, fontUrl.openStream());
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			if (ge.registerFont(font)) {
				System.out.println("Registered new Font '" + font.getName() + "'");
				return font;
			}
		}
		catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}

}
