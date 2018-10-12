package scripts.usa.api.painting.media;

import java.awt.image.BufferedImage;

import scripts.usa.api.web.WebUtils;

public class Icons {

	public enum Color {
		LIGHT,
		DARK
	}

	public enum MaterialDesign {

		VISIBILITY_WHITE("https://github.com/google/material-design-icons/raw/master/action/drawable-mdpi/ic_visibility_white_18dp.png"),

		VISIBILITY_OFF_WHITE("https://github.com/google/material-design-icons/raw/master/action/drawable-mdpi/ic_visibility_off_white_18dp.png"),

		VISIBILITY_BLACK("https://github.com/google/material-design-icons/raw/master/action/drawable-mdpi/ic_visibility_black_18dp.png"),

		VISIBILITY_OFF_BLACK("https://github.com/google/material-design-icons/raw/master/action/drawable-mdpi/ic_visibility_off_black_18dp.png");

		private final String url;
		private BufferedImage image;

		MaterialDesign(String url) {
			this.url = url;
		}

		public String getURL() {
			return this.url;
		}

		public BufferedImage getImage() {
			return this.image != null ? this.image : (this.image = WebUtils.getImage(this.url));
		}
	}

}
