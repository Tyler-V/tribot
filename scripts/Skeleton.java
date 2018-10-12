package scripts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import javax.swing.JOptionPane;

import org.tribot.api2007.Player;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;

@ScriptManifest(authors = { "Usa" }, category = "Skeleton", name = "Skeleton")
public class Skeleton extends Script {

	/**
	 * AUTHENTICATION START
	 */
	private String version = "0.1";
	private String script = "spinner";
	private Timer trialTime = new Timer(900000); // 15 MIN
	private Timer checkSession = new Timer(1800000); // 30MIN
	private boolean authenticated = false;
	private String rs_username = Player.getRSPlayer().getName();
	private String username = "";
	private String password = "";
	private String key;
	private String script_version;
	private String server = "vorpahl";

	/**
	 * AUTHENTICATION END
	 */

	boolean run = true;

	@Override
	public void run() {

		/**
		 * AUTHENTICATION START
		 */
		try {
			script_version = checkVersion(script);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!version.equalsIgnoreCase(script_version)) {
			println("You have version " + version + " and version "
					+ script_version + " is available!");
			println("Please download the latest update!");
			stopScript();
		}
		println("Version " + version + " is up to date!");
		username = JOptionPane.showInputDialog("Enter authorization login: ");
		if (!username.equalsIgnoreCase(server)) {
			password = JOptionPane
					.showInputDialog("Enter authorization password: ");
			key = generateRandom(5);
			try {
				checkAuthentication();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			assignSession(script, username, password, key);
			if (authenticated == true) {
				println("" + username + " is authorized to run " + script
						+ " !");
				println("Unique Session Key: " + key);
			}
		}
		/**
		 * AUTHENTICATION END
		 */
		while (run) {

			if (!username.equalsIgnoreCase(server) && !checkSession.isRunning()) {
				try {
					if (!checkSession(script, username, password)) {
						println("Stopping script!");
						println("Detected script is being run on more than one account, you must purchase additional licenses!");
						run = false;
					} else {
						key = generateRandom(5);
						println("Assigning unique session key: " + key);
						assignSession(script, username, password, key);
						checkSession.reset();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (username.equalsIgnoreCase(server) || trialTime.isRunning()
					|| authenticated) {

				// *****
				// YOUR
				// SCRIPT'S
				// CODE
				// GOES
				// HERE
				// *****

			} else {
				try {
					if (!authenticate(script, username, password)) {
						println("" + rs_username
								+ " is not authorized to run this script!");
						println("Your 10 minute trial is complete, please purchase the script!");
						run = false;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private String generateRandom(int length) {
		Random random = new Random();
		String key = "";
		// char[] digits = new char[length];
		key = key + (random.nextInt(9) + '1');
		for (int i = 1; i < length; i++) {
			key = key + (random.nextInt(10) + '0');
		}
		return key;
	}

	private void checkAuthentication() throws IOException {
		if (authenticate(script, username, password) == true) {
			authenticated = true;
		} else {
			authenticated = false;
		}
	}

	private void assignSession(String s, String u, String pw, String key) {
		final String LINK = "http://tribot-scripts.org/panel.php?action=assign";
		String script = "&script=" + s;
		String user = "&user=" + u;
		String pass = "&pass=" + pw;
		String session = "&session=" + key;
		String phpCall = (LINK + script + user + pass + session);
		try {
			String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.57 Safari/537.17";
			URL url = new URL(phpCall);
			URLConnection uc = url.openConnection();
			uc.setRequestProperty("User-Agent", USER_AGENT);
			InputStreamReader inr = new InputStreamReader(uc.getInputStream());
		} catch (IOException e) {
			println("Error connecting to server");
			e.printStackTrace();
		}
	}

	private boolean checkSession(String s, String u, String pw)
			throws IOException {
		final String LINK = "http://tribot-scripts.org/panel.php?action=retrieve";
		String script = "&script=" + s;
		String user = "&user=" + u;
		String pass = "&pass=" + pw;
		String phpCall = (LINK + script + user + pass);
		String line = "";
		BufferedReader br = getReader(phpCall);
		line = br.readLine();
		// println("we read... " + line);
		// println("original key... " + key);
		if (line.equalsIgnoreCase(key)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean authenticate(String s, String u, String pw)
			throws IOException {
		final String LINK = "http://tribot-scripts.org/panel.php?action=auth";
		String script = "&script=" + s;
		String user = "&user=" + u;
		String pass = "&pass=" + pw;
		String phpCall = (LINK + script + user + pass);
		String line = "";
		BufferedReader br = getReader(phpCall);
		line = br.readLine();
		if (line.equalsIgnoreCase("True")) {
			return true;
		} else {
			return false;
		}
	}

	public String checkVersion(String s) throws IOException {
		final String LINK = "http://tribot-scripts.org/panel.php?action=get_version";
		String script = "&script=" + s;
		String phpCall = (LINK + script);
		String line = "";
		BufferedReader br = getReader(phpCall);
		line = br.readLine();
		return line;
	}

	private BufferedReader getReader(String link) throws IOException {
		String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.57 Safari/537.17";
		URL url = new URL(link);
		URLConnection uc = url.openConnection();
		uc.setRequestProperty("User-Agent", USER_AGENT);
		InputStreamReader inr = new InputStreamReader(uc.getInputStream(),
				"UTF-8");
		return new BufferedReader(inr);
	}

	public class Timer {

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
}