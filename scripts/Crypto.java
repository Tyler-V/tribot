package scripts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.security.SecureRandom;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.tribot.api.input.Keyboard;
import org.tribot.api2007.Game;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Painting;

@ScriptManifest(authors = { "Usa" }, category = "Crypto", name = "Crypto")
public class Crypto extends Script implements Painting {

	private final RSTile NATURE_RIFT_TILE = new RSTile(3035, 4842, 0);
	private static int NATURE_RIFT = 7142, NATURE_ALTAR = 10068;
	int[] ALTARS = {};

	// Set Angle 90, Rotation 180 degrees
	// "Exit-through" Nature rift

	// "Craft-rune" Altar
	// Angle/Rotation is fine

	private final static int NATURE_RUNE = 561;

	RSArea Script_AREA = new RSArea(new RSTile(2928, 3275, 0), new RSTile(2956,
			3380, 0));

	private int[] before = new int[Game.getSettingsArray().length];
	private int[] after = new int[Game.getSettingsArray().length];
	private boolean changes = false;
	RSObject[] objects;

	private int[] OBSTACLES = { 7142, 7143, 7144, 7145, 7146, 7147, 7148, 7149,
			7150, 7151, 7152, 7153 };
	ArrayList<RSTile> objectTiles = new ArrayList<RSTile>();

	private int[] RIFTS = { 7136 };

	private boolean rift = false;

	private int success = 0;
	private int fail = 0;

	String code = "1234";
	private final static int LOBSTER = 379;

	private final static int AIR_RUNE = 556;
	private final static int EARTH_RUNE = 557;
	private final static int DUST_RUNE = 4696;
	private final static int OUTSIDE_PORTAL = 15478;
	private final static int HOUSE_GLORY = 13523;
	private final static int[] BUILDING_MODE_OBJECTS = { 15314, 15307 };
	private final static int HOUSE_PORTAL = 13405;
	private final static int HOUSE_TELEPORT = 8013;
	private final static int ABYSSAL_LEECH = 2263;
	private final static int ABYSSAL_GUARDIAN = 2264;
	private final static int ABYSSAL_WALKER = 2265;
	private final static int ZAMORAK_MAGE = 2257;
	private final static int DARK_MAGE = 2262;
	private final static int GLORY_1 = 1706;
	private final static int GLORY_2 = 1708;
	private final static int GLORY_3 = 1710;
	private final static int GLORY_4 = 1712;
	private final static int UNCHARGED_GLORY = 1704;
	private final static int TINDERBOX = 590;
	private int PURE_ESSENCE = 0;
	private final static int SMALL_POUCH = 5509;
	private final static int MEDIUM_POUCH = 5510;
	private final static int LARGE_POUCH = 5512;
	private final static int GIANT_POUCH = 5514;
	private final static int MEDIUM_POUCH_DAMAGED = 5511;
	private final static int LARGE_POUCH_DAMAGED = 5513;
	private final static int GIANT_POUCH_DAMAGED = 5515;
	private int[] POUCHES = { SMALL_POUCH, MEDIUM_POUCH, LARGE_POUCH,
			GIANT_POUCH, MEDIUM_POUCH_DAMAGED, LARGE_POUCH_DAMAGED,
			GIANT_POUCH_DAMAGED };
	private int[] POUCHES_AND_TINDERBOX_AND_TABLET_AND_AXES = { SMALL_POUCH,
			MEDIUM_POUCH, LARGE_POUCH, GIANT_POUCH, MEDIUM_POUCH_DAMAGED,
			LARGE_POUCH_DAMAGED, GIANT_POUCH_DAMAGED, TINDERBOX,
			HOUSE_TELEPORT, BRONZE_HATCHET, IRON_HATCHET, STEEL_HATCHET,
			MITH_HATCHET, ADAMANT_HATCHET, RUNE_HATCHET, BRONZE_PICKAXE,
			IRON_PICKAXE, STEEL_PICKAXE, MITH_PICKAXE, ADAMANT_PICKAXE,
			RUNE_PICKAXE, AIR_RUNE, EARTH_RUNE, DUST_RUNE };
	private int[] POUCHES_DAMAGED = { MEDIUM_POUCH_DAMAGED,
			LARGE_POUCH_DAMAGED, GIANT_POUCH_DAMAGED };
	private final static int BRONZE_HATCHET = 1351;
	private final static int IRON_HATCHET = 1351;
	private final static int STEEL_HATCHET = 1351;
	private final static int MITH_HATCHET = 1351;
	private final static int ADAMANT_HATCHET = 1351;
	private final static int RUNE_HATCHET = 1351;
	private final static int BRONZE_PICKAXE = 1265;
	private final static int IRON_PICKAXE = 1267;
	private final static int STEEL_PICKAXE = 1269;
	private final static int MITH_PICKAXE = 1273;
	private final static int ADAMANT_PICKAXE = 1271;
	private final static int RUNE_PICKAXE = 1275;
	private final static int[] HATCHETS = { BRONZE_HATCHET, IRON_HATCHET,
			STEEL_HATCHET, MITH_HATCHET, ADAMANT_HATCHET, RUNE_HATCHET };
	private final static int[] PICKAXES = { BRONZE_PICKAXE, IRON_PICKAXE,
			STEEL_PICKAXE, MITH_PICKAXE, ADAMANT_PICKAXE, RUNE_PICKAXE };
	private final static int ENERGY_POTION_3 = 3010;
	private final static int ENERGY_POTION_2 = 3012;
	private final static int ENERGY_POTION_1 = 3014;
	private int[] ENERGY_POTIONS = { ENERGY_POTION_3, ENERGY_POTION_2,
			ENERGY_POTION_1 };
	private final static int SUPER_ENERGY_POTION_3 = 3018;
	private final static int SUPER_ENERGY_POTION_2 = 3020;
	private final static int SUPER_ENERGY_POTION_1 = 3022;
	private int[] SUPER_ENERGY_POTIONS = { SUPER_ENERGY_POTION_3,
			SUPER_ENERGY_POTION_2, SUPER_ENERGY_POTION_1 };
	private int[] POTIONS = { SUPER_ENERGY_POTION_3, SUPER_ENERGY_POTION_2,
			SUPER_ENERGY_POTION_1, ENERGY_POTION_3, ENERGY_POTION_2,
			ENERGY_POTION_1 };
	private final static int EMPTY_VIAL = 229;
	private int[] DONT_DEPOSIT = { PURE_ESSENCE, GLORY_1, GLORY_2, GLORY_3,
			GLORY_4, SMALL_POUCH, MEDIUM_POUCH, LARGE_POUCH, GIANT_POUCH,
			MEDIUM_POUCH_DAMAGED, LARGE_POUCH_DAMAGED, GIANT_POUCH_DAMAGED,
			LOBSTER, HOUSE_TELEPORT, TINDERBOX, BRONZE_HATCHET, IRON_HATCHET,
			STEEL_HATCHET, MITH_HATCHET, ADAMANT_HATCHET, RUNE_HATCHET,
			BRONZE_PICKAXE, IRON_PICKAXE, STEEL_PICKAXE, MITH_PICKAXE,
			ADAMANT_PICKAXE, RUNE_PICKAXE, AIR_RUNE, EARTH_RUNE, DUST_RUNE,
			SUPER_ENERGY_POTION_3 };

	private boolean changedWorlds = false;
	private RSObject[] obstacles;

	@Override
	public void run() {


	}

	public static class crypto {

		public static String encrypt(String seed, String cleartext)
				throws Exception {
			byte[] rawKey = getRawKey(seed.getBytes());
			byte[] result = encrypt(rawKey, cleartext.getBytes());
			return toHex(result);
		}

		public static String decrypt(String seed, String encrypted)
				throws Exception {
			byte[] rawKey = getRawKey(seed.getBytes());
			byte[] enc = toByte(encrypted);
			byte[] result = decrypt(rawKey, enc);
			return new String(result);
		}

		private static byte[] getRawKey(byte[] seed) throws Exception {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
			sr.setSeed(seed);
			kgen.init(128, sr); // 192 and 256 bits may not be available
			SecretKey skey = kgen.generateKey();
			byte[] raw = skey.getEncoded();
			return raw;
		}

		private static byte[] encrypt(byte[] raw, byte[] clear)
				throws Exception {
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			byte[] encrypted = cipher.doFinal(clear);
			return encrypted;
		}

		private static byte[] decrypt(byte[] raw, byte[] encrypted)
				throws Exception {
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			byte[] decrypted = cipher.doFinal(encrypted);
			return decrypted;
		}

		public String toHex(String txt) {
			return toHex(txt.getBytes());
		}

		public String fromHex(String hex) {
			return new String(toByte(hex));
		}

		public static byte[] toByte(String hexString) {
			int len = hexString.length() / 2;
			byte[] result = new byte[len];
			for (int i = 0; i < len; i++)
				result[i] = Integer.valueOf(
						hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
			return result;
		}

		public static String toHex(byte[] buf) {
			if (buf == null)
				return "";
			StringBuffer result = new StringBuffer(2 * buf.length);
			for (int i = 0; i < buf.length; i++) {
				appendHex(result, buf[i]);
			}
			return result.toString();
		}

		private final static String HEX = "0123456789ABCDEF";

		private static void appendHex(StringBuffer sb, byte b) {
			sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
		}

	}

	private void sendText(String s) {
		char[] line;
		line = s.toCharArray();
		for (int i = 0; i < line.length; i++) {
			Keyboard.typeKey(line[i]);
		}
		Keyboard.pressEnter();
	}

	private class Timer {

		private long end;
		private final long start;
		private final long period;

		public Timer(final long period) {
			this.period = period;
			start = System.currentTimeMillis();
			end = start + period;
		}

		public Timer(final long period, long addition) {
			this.period = period;
			start = System.currentTimeMillis() + addition;
			end = start + period;
		}

		public long getElapsed() {
			return System.currentTimeMillis() - start;
		}

		public long getRemaining() {
			if (isRunning()) {
				return end - System.currentTimeMillis();
			}
			return 0;
		}

		public boolean isRunning() {
			return System.currentTimeMillis() < end;
		}

		public void reset() {
			end = System.currentTimeMillis() + period;
		}

		public long setEndIn(final long ms) {
			end = System.currentTimeMillis() + ms;
			return end;
		}

		public String toElapsedString() {
			return format(getElapsed());
		}

		public String toRemainingString() {
			return format(getRemaining());
		}

		public String format(final long time) {
			final StringBuilder t = new StringBuilder();
			final long total_secs = time / 1000;
			final long total_mins = total_secs / 60;
			final long total_hrs = total_mins / 60;
			final int secs = (int) total_secs % 60;
			final int mins = (int) total_mins % 60;
			final int hrs = (int) total_hrs % 60;
			if (hrs < 10) {
				t.append("0");
			}
			t.append(hrs);
			t.append(":");
			if (mins < 10) {
				t.append("0");
			}
			t.append(mins);
			t.append(":");
			if (secs < 10) {
				t.append("0");
			}
			t.append(secs);
			return t.toString();
		}
	}

	/**
	 * @author Jewtage
	 */

	public class RSArea {
		private final Polygon area;
		private final int plane;

		public RSArea(final RSTile[] tiles, final int plane) {
			area = tilesToPolygon(tiles);
			this.plane = plane;
		}

		public RSArea(final RSTile[] tiles) {
			this(tiles, 0);
		}

		public RSArea(final RSTile southwest, final RSTile northeast) {
			this(southwest, northeast, 0);
		}

		public RSArea(final int swX, final int swY, final int neX, final int neY) {
			this(new RSTile(swX, swY), new RSTile(neX, neY), 0);
		}

		public RSArea(final int swX, final int swY, final int neX,
				final int neY, final int plane) {
			this(new RSTile(swX, swY), new RSTile(neX, neY), plane);
		}

		public RSArea(final RSTile southwest, final RSTile northeast,
				final int plane) {
			this(new RSTile[] { southwest,
					new RSTile(northeast.getX() + 1, southwest.getY()),
					new RSTile(northeast.getX() + 1, northeast.getY() + 1),
					new RSTile(southwest.getX(), northeast.getY() + 1) }, plane);
		}

		public boolean contains(final RSTile... tiles) {
			final RSTile[] areaTiles = getTiles();
			for (final RSTile check : tiles) {
				for (final RSTile space : areaTiles) {
					if (check.equals(space)) {
						return true;
					}
				}
			}
			return false;
		}

		public boolean contains(final int x, final int y) {
			return this.contains(new RSTile(x, y));
		}

		public boolean contains(final int plane, final RSTile... tiles) {
			return this.plane == plane && this.contains(tiles);
		}

		public Rectangle getDimensions() {
			return new Rectangle(area.getBounds().x + 1,
					area.getBounds().y + 1, getWidth(), getHeight());
		}

		public RSTile getNearestTile(final RSTile base) {
			RSTile tempTile = null;
			for (final RSTile tile : getTiles()) {
				if (tempTile == null
						|| distanceBetween(base, tile) < distanceBetween(
								tempTile, tile)) {
					tempTile = tile;
				}
			}
			return tempTile;
		}

		public int getPlane() {
			return plane;
		}

		public Polygon getPolygon() {
			return area;
		}

		public RSTile[] getTiles() {
			ArrayList<RSTile> tiles = new ArrayList<RSTile>();
			for (int x = getX(); x <= getX() + getWidth(); x++) {
				for (int y = getY(); y <= getY() + getHeight(); y++) {
					if (area.contains(x, y)) {
						tiles.add(new RSTile(x, y));
					}
				}
			}
			return tiles.toArray(new RSTile[tiles.size()]);
		}

		public int getWidth() {
			return area.getBounds().width;
		}

		public int getHeight() {
			return area.getBounds().height;
		}

		public int getX() {
			return area.getBounds().x;
		}

		public int getY() {
			return area.getBounds().y;
		}

		public Polygon tilesToPolygon(final RSTile[] tiles) {
			final Polygon polygon = new Polygon();
			for (final RSTile t : tiles) {
				polygon.addPoint(t.getX(), t.getY());
			}
			return polygon;
		}

		public double distanceBetween(RSTile curr, RSTile dest) {
			return Math.sqrt((curr.getX() - dest.getX())
					* (curr.getX() - dest.getX()) + (curr.getY() - dest.getY())
					* (curr.getY() - dest.getY()));
		}
	}

	public void onPaint(Graphics g) {
		g.setColor(Color.YELLOW);
		if (obstacles != null && obstacles.length > 0) {
			if (obstacles[0].isOnScreen()) {
				Polygon[] triangles = obstacles[0].getModel().getTriangles();
				for (int i = 0; i < triangles.length; i++) {
					g.drawPolygon(triangles[50]);
				}
			}
		}
	}
}