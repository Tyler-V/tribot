package scripts;

import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.tribot.api.General;
import org.tribot.api.input.Keyboard;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Player;
import org.tribot.api2007.Players;
import org.tribot.api2007.Skills;
import org.tribot.api2007.types.RSInterfaceComponent;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Painting;

@ScriptManifest(authors = { "Usa" }, category = "USA", name = "Auto Responder")
public class AutoResponder extends Script implements Painting {

	private int randomMessagePercetange = 25; // %25 chance for random message
	private int punctuationPercentage = 50; // %50 chance to add punctuation
	private int smileyPercentage = 25; // %25 chance to add a smiley at the end

	ArrayList<ChatMessage> chat = new ArrayList<ChatMessage>();
	Cleverbot cleverbot = new Cleverbot();
	String lastMessage;

	@Override
	public void run() {

		while (true) {

			getCurrentChat();

			String username = Player.getRSPlayer().getName().toLowerCase();
			String playerMessage = getMessageContainingPlayer(username);
			String singlePlayerMessage = getMessageFromPlayer(getUsernameOfSinglePlayerMessagingMe());
			String playerFollowingMe = getMessageFromPlayer(getPlayerUsernameFollowingMe());

			if (!playerMessage.isEmpty()) {
				/**
				 * Case for if your name is mentioned
				 */
				generateMessage(playerMessage, username);

			} else if (!singlePlayerMessage.isEmpty()) {
				/**
				 * Case for if only one player in area
				 */
				generateMessage(singlePlayerMessage, username);
			} else if (!playerFollowingMe.isEmpty()) {
				/**
				 * Case for player is following you and talking
				 */
				println("yes");
				generateMessage(playerFollowingMe, username);
			}

			sleep(1000);
		}
	}

	public void generateMessage(String message, String username) {

		if (!chat.get(chat.size() - 1).getUsername().equals(username)) {
			if (!message.equals(lastMessage)) {

				String replyMessage = generateResponse(message);

				if (replyMessage.isEmpty()) {
					try {
						replyMessage = formatMessage(
								cleverbot.sendMessage(message), username);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						println("Error!");
						e.printStackTrace();
					}
				}
				if (replyMessage.length() <= 30) {
					lastMessage = message;
					typeFast(replyMessage);
					sleep(2000);
					// sleepUntilMessageSent(username);
				}
			}
		}
	}

	private String getPlayerUsernameFollowingMe() {
		RSPlayer[] p = Players.getAll();

		for (int i = 0; i < p.length; i++) {
			if (p[i].isInteractingWithMe() && p[i].isOnScreen()
					&& !Player.getRSPlayer().isInCombat()) {
				return p[i].getName();
			}
		}

		return "";
	}

	public void sleepUntilMessageSent(String username) {
		Timer t = new Timer(3000);
		// println("Sleeping...");
		// println(Player.getRSPlayer().getName());
		while (t.isRunning()) {
			getCurrentChat();
			// println(chat.get(chat.size() - 1).getUsername());
			if (chat.get(chat.size() - 1).getUsername()
					.equals(Player.getRSPlayer().getName())) {
				// println("Slept for " + t.getElapsed());
				break;
			}
			sleep(500);
		}
		// println("Slept for 3000");
	}

	/**
	 * @return - returns the message addressed to your runescape player
	 */
	public String getMessageContainingPlayer(String player) {
		for (int i = chat.size() - 1; i > 0; i--) {
			if (chat.get(i).getMessage().contains(player.toLowerCase())
					&& !chat.get(i).getUsername().contains(player)) {
				// println("[playerMessage()] Replying to: "
				// + chat.get(i).getMessage());
				return chat.get(i).getMessage();
			}
		}
		return "";
	}

	/**
	 * @return - returns a message from a player messaging your username
	 */
	public String getMessageFromPlayer(String username) {
		for (int i = chat.size() - 1; i > 0; i--) {
			if (chat.get(i).getUsername().equalsIgnoreCase(username)) {
				// println("[getPlayerMessage()] Replying to: "
				// + chat.get(i).getMessage());
				return chat.get(i).getMessage();
			}
		}
		return "";
	}

	public String getUsernameOfSinglePlayerMessagingMe() {
		RSPlayer[] p = Players.getAll();

		if (p != null && p.length == 2) {
			if (p[0].getName().equals(Player.getRSPlayer().getName())) {
				return p[1].getName().toString();
			} else {
				return p[0].getName().toString();
			}
		}

		return "";
	}

	public String generateResponse(String message) {
		String response = "";
		String[] level = { "level", "lvl", "Level", "Lvl", "levels", "Levels" };

		String[] attack = { "atk", "attack", "Atk", "Attack" };
		String[] strength = { "str", "Strength", "Str", "strength" };
		String[] defence = { "def", "Def", "Defence", "Defense", "defence",
				"defense" };
		String[] ranged = { "range", "Range", "ranged", "Ranged", "rang",
				"Rang" };
		String[] prayer = { "pray", "Pray", "prayer", "Prayer" };
		String[] magic = { "magic", "Magic", "mage", "Mage" };
		String[] runecrafting = { "rc", "Rc", "Runecrafting", "runecrafting" };
		String[] construction = { "con", "Con", "Construction", "construction" };
		String[] hitpoints = { "hp", "Hp", "HP", "hitpoints", "Hitpoints",
				"hitpoint", "Hitpoint" };
		String[] agility = { "agil", "Agil", "Agility", "agility" };
		String[] herblore = { "herblore", "Herblore", "herb", "Herb" };
		String[] thieving = { "thieve", "Thieve", "Thieving", "thieving" };
		String[] crafting = { "craft", "Craft", "crafting", "Crafting" };
		String[] fletching = { "fletch", "Fletch", "Fletching", "fletching" };
		String[] slayer = { "slay", "Slay", "Slayer", "slayer", "slaying",
				"Slaying" };
		String[] hunter = { "hunt", "Hunt", "Hunter", "hunter" };
		String[] mining = { "mining", "Mining", "Mine", "mine" };
		String[] smithing = { "smith", "Smith", "Smithing", "smithing" };
		String[] fishing = { "fishing", "Fishing", "fish", "Fish" };
		String[] cooking = { "cook", "Cook", "Cooking", "cooking" };
		String[] firemaking = { "firemaking", "Firemaking", "Fm", "fm" };
		String[] woodcutting = { "Woodcutting", "woodcutting", "wc", "Wc" };
		String[] farming = { "farm", "Farm", "Farming", "farming" };

		// if (messageContainsKeyword(message, attack)) {
		// if (messageContainsKeyword(message, level)) {
		// response = randomResponse(attack,
		// Skills.getActualLevel("attack"));
		// }
		// } else if (messageContainsKeyword(message, strength)) {
		// if (messageContainsKeyword(message, level)) {
		// response = randomResponse(strength,
		// Skills.getActualLevel("strength"));
		// }
		// } else if (messageContainsKeyword(message, defence)) {
		// if (messageContainsKeyword(message, level)) {
		// response = randomResponse(defence,
		// Skills.getActualLevel("defence"));
		// }
		// } else if (messageContainsKeyword(message, ranged)) {
		// if (messageContainsKeyword(message, level)) {
		// response = randomResponse(ranged,
		// Skills.getActualLevel("ranged"));
		// }
		// } else if (messageContainsKeyword(message, prayer)) {
		// if (messageContainsKeyword(message, level)) {
		// response = randomResponse(prayer,
		// Skills.getActualLevel("prayer"));
		// }
		// } else if (messageContainsKeyword(message, magic)) {
		// if (messageContainsKeyword(message, level)) {
		// response = randomResponse(magic, Skills.getActualLevel("magic"));
		// }
		// } else if (messageContainsKeyword(message, runecrafting)) {
		// if (messageContainsKeyword(message, level)) {
		// response = randomResponse(runecrafting,
		// Skills.getActualLevel("runecrafting"));
		// }
		// } else if (messageContainsKeyword(message, construction)) {
		// if (messageContainsKeyword(message, level)) {
		// response = randomResponse(construction,
		// Skills.getActualLevel("construction"));
		// }
		// } else if (messageContainsKeyword(message, hitpoints)) {
		// if (messageContainsKeyword(message, level)) {
		// response = randomResponse(hitpoints,
		// Skills.getActualLevel("hitpoints"));
		// }
		// } else if (messageContainsKeyword(message, agility)) {
		// if (messageContainsKeyword(message, level)) {
		// response = randomResponse(agility,
		// Skills.getActualLevel("agility"));
		// }
		// } else if (messageContainsKeyword(message, herblore)) {
		// if (messageContainsKeyword(message, level)) {
		// response = randomResponse(herblore,
		// Skills.getActualLevel("herblore"));
		// }
		// } else if (messageContainsKeyword(message, thieving)) {
		// if (messageContainsKeyword(message, level)) {
		// response = randomResponse(thieving,
		// Skills.getActualLevel("thieving"));
		// }
		// } else if (messageContainsKeyword(message, crafting)) {
		// if (messageContainsKeyword(message, level)) {
		// response = randomResponse(crafting,
		// Skills.getActualLevel("crafting"));
		// }
		// } else if (messageContainsKeyword(message, fletching)) {
		// if (messageContainsKeyword(message, level)) {
		// response = randomResponse(fletching,
		// Skills.getActualLevel("fletching"));
		// }
		// } else if (messageContainsKeyword(message, slayer)) {
		// if (messageContainsKeyword(message, level)) {
		// response = randomResponse(slayer,
		// Skills.getActualLevel("slayer"));
		// }
		// } else if (messageContainsKeyword(message, hunter)) {
		// if (messageContainsKeyword(message, level)) {
		// response = randomResponse(hunter,
		// Skills.getActualLevel("hunter"));
		// }
		// } else if (messageContainsKeyword(message, mining)) {
		// if (messageContainsKeyword(message, level)) {
		// response = randomResponse(mining,
		// Skills.getActualLevel("mining"));
		// }
		// } else if (messageContainsKeyword(message, smithing)) {
		// if (messageContainsKeyword(message, level)) {
		// response = randomResponse(smithing,
		// Skills.getActualLevel("smithing"));
		// }
		// } else if (messageContainsKeyword(message, fishing)) {
		// if (messageContainsKeyword(message, level)) {
		// response = randomResponse(fishing,
		// Skills.getActualLevel("fishing"));
		// }
		// } else if (messageContainsKeyword(message, cooking)) {
		// if (messageContainsKeyword(message, level)) {
		// response = randomResponse(cooking,
		// Skills.getActualLevel("cooking"));
		// }
		// } else if (messageContainsKeyword(message, firemaking)) {
		// if (messageContainsKeyword(message, level)) {
		// response = randomResponse(firemaking,
		// Skills.getActualLevel("firemaking"));
		// }
		// } else if (messageContainsKeyword(message, woodcutting)) {
		// if (messageContainsKeyword(message, level)) {
		// response = randomResponse(woodcutting,
		// Skills.getActualLevel("woodcutting"));
		// }
		// } else if (messageContainsKeyword(message, farming)) {
		// if (messageContainsKeyword(message, level)) {
		// response = randomResponse(farming,
		// Skills.getActualLevel("farming"));
		// }
		// }

		return response;
	}

	public String randomResponse(String[] category, int level) {
		StringBuilder response = new StringBuilder();
		int r1 = General.random(0, category.length - 1);
		String skill = category[r1];

		String level_responses[] = { level + ", you",
				"I am " + level + " in " + skill,
				"I have " + level + " " + skill, level + " hbu", level + " u",
				"I " + level, level + " wooo", level + " why", "" + level,
				"about to be " + (level + 1),
				"Just got " + level + " what about you", "meh, " + level,
				"like " + level, "gettin close to " + (level + 1),
				"idk " + level, "around " + level, "idk",
				"almost " + (level + 1), (level + 1) + " soon",
				level + " " + skill, level + "ish", "recently got " + level,
				"im around " + level, " " + level, "" + level, "uhhh " + level,
				"uhh like " + level,
				"" + level + " or " + (level + 1) + " i cant remember haha",
				level + " lol", "lol " + level,
				"over " + (level - (General.random(3, 10))),
				"im around " + level + " why", "idk like " + level + " why",
				"99 :D jk " + level, "99! :D jk " + level,
				"like almmost " + level + 1, "like almmost " + level + 1,
				"like almmost " + level + 1 + " why",
				"closing in on " + level + (General.random(1, 3)) };
		int r2 = General.random(0, level_responses.length - 1);
		String level_message = level_responses[r2];

		String random_responses[] = { "99",
				"idk, " + skill + " is boring anyways",
				"who cares about " + skill,
				"low but im almost done with " + skill, "i got low " + skill,
				skill + " is my highest", skill + "?", skill, "who knows",
				"look me up if you care that much", "look me up",
				"sorry thats private", "rather not tell you thanks", "meh",
				"idk", "i forget", "how do i check?",
				"why dont you mind your own business",
				"im too embarassed to tell you", "low", "higher than you",
				"its over 9000", "tree fiddy", "why do you ask",
				"why do you care", "not high", "decent", "not tellin",
				"look me up", "if you care, look me up", "low, whats yours",
				"higher than you", "nice try", "brb", "i d k",
				"above 1, less than 99",
				"not high, " + skill + " is my lowest",
				"not that high, i dont train " + skill + " much",
				"not very high, i dont like " + skill,
				"... " + skill + " is my lowest",
				"haha not too good, dont usually train " + skill,
				"err idk but " + skill + " takes forever haha",
				"idk but " + skill + " sucks..",
				skill + " is the hardest for me to train..",
				"idk but im almost done with " + skill,
				"low.. i just need a few more levels and im done",
				"why dont you tell me yours first", "you tell me yours",
				"higher than you, whats yours", "prob higher than you" };
		int r3 = General.random(0, random_responses.length - 1);
		String random_message = random_responses[r3];

		String punctuation[] = { ".", ".", ".", "?", "!" };
		int r4 = General.random(0, punctuation.length - 1);
		String punctuation_addition = punctuation[r4];

		String smiley[] = { " :D", " :P", " :)", " :(", " :-0", " :O", " :0",
				" :/", " :X", " :]", " :[" };
		int r5 = General.random(0, smiley.length - 1);
		String smiley_addition = smiley[r5];

		int randomResponse = General.random(0, 100);
		int punctuationRandom = General.random(0, 100);
		int smileyRandom = General.random(0, 100);

		if (randomResponse <= randomMessagePercetange) { // % chance to be
															// random response
			response.append(random_message);
		} else {
			response.append(level_message);
		}

		if (punctuationRandom <= punctuationPercentage) { // % chance to add
															// punctuation
			response.append(punctuation_addition);
		}

		if (smileyRandom <= smileyPercentage) { // % chance to add a smiley
			response.append(smiley_addition);
		}

		return response.toString();
	}

	public boolean messageContainsKeyword(String message, String[] keywords) {

		for (int i = 0; i < keywords.length; i++) {
			if (message.contains(keywords[i])) {
				return true;
			}
		}
		return false;
	}

	public void onPaint(Graphics g) {
		g.setColor(new Color(50, 50, 50, 200));
		g.fillRect(10, 208, 500, 130); // main chat

		int x = 15;
		int y = 223;
		if (chat.size() > 0) {
			for (int i = 0; i < chat.size(); i++) {
				g.setColor(Color.YELLOW);
				g.drawString(chat.get(i).getUsername() + ": "
						+ chat.get(i).getMessage() + " ", x, y);
				y += 15;
			}
		}
	}

	public String formatMessage(String s, String username) {
		String message = s;

		message.replaceAll(username, "").replaceAll("Ai", "noob")
				.replaceAll("bot", "noob").replaceAll("Bot", "noob")
				.replaceAll("AI", "noob").replaceAll("ai", "noob")
				.replaceAll("cleverbot", "noob").replaceAll("auto", "noob")
				.replaceAll("Auto", "noob").replaceAll("script", "noob")
				.replaceAll("Cleverbot", "noob");

		return message;
	}

	public void typeFast(String s) {
		println(s);
		char[] letters = s.toCharArray();
		for (int i = 0; i < letters.length; i++) {
			Keyboard.typeKey(letters[i]);
			sleep(25);
		}
		Keyboard.pressEnter();
	}

	private void getCurrentChat() {
		chat.clear();

		int[] currentChat = currentWindow();
		RSInterfaceComponent[] chatWindow = Interfaces.get(137, 2)
				.getChildren();

		for (int i = currentChat[0]; i < currentChat[1]; i++) {
			if (i % 2 == 0) {
				String username;
				String message;

				username = chatWindow[i].getText()
						.replaceAll("<col=800080>", "").replaceAll(":", "");
				message = chatWindow[i + 1].getText()
						.replaceAll("<col=0000ff>", "").replaceAll("<.*>", "")
						.replaceAll(" {2}", " ");
				chat.add(new ChatMessage(username, message));
			}
		}
	}

	private static int[] currentWindow() {
		RSInterfaceComponent[] chat = Interfaces.get(137, 2).getChildren();

		int start = 0;
		int end = 0;

		if (chat != null && chat.length > 0) {
			for (int i = 0; i < 200; i++) {
				if (chat[i].getText().length() == 0) {
					int next = i + 1;
					if (chat[next] != null
							&& chat[next].getText().length() == 0) {
						if (i % 2 == 0) {
							if (i <= 15) {
								start = 0;
								end = 15;
							} else {
								start = (i - 1) - 15;
								end = (i - 1);
							}
							break;
						} else {
							if (i <= 15) {
								start = 0;
								end = 15;
							} else {
								start = i - 15;
								end = i;
							}
							break;
						}
					}
				}
			}
		}
		return new int[] { start, end };
	}

	private class ChatMessage {

		private String username;
		private String message;

		public ChatMessage(String username, String message) {
			this.username = username;
			this.message = message;
		}

		public String getUsername() {
			return username;
		}

		public String getMessage() {
			return message;
		}

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

	public class Cleverbot {

		private final String URL = "http://www.cleverbot.com/webservicemin";
		private final Map<String, String> vars;

		public Cleverbot() {
			vars = new LinkedHashMap<String, String>();
			vars.put("start", "y");
			vars.put("icognoid", "wsf");
			vars.put("fno", "0");
			vars.put("sub", "Say");
			vars.put("islearning", "1");
			vars.put("cleanslate", "false");
		}

		public String sendMessage(String message) throws Exception {
			vars.put("stimulus", message);
			String formData = parametersToWWWFormURLEncoded(vars);
			String formDataToDigest = formData.substring(9, 29);
			String formDataDigest = md5(formDataToDigest);
			vars.put("icognocheck", formDataDigest);
			String response = post(URL, vars);
			String[] responseValues = response.split("\r");
			vars.put("sessionid", stringAtIndex(responseValues, 1));
			vars.put("logurl", stringAtIndex(responseValues, 2));
			vars.put("vText8", stringAtIndex(responseValues, 3));
			vars.put("vText7", stringAtIndex(responseValues, 4));
			vars.put("vText6", stringAtIndex(responseValues, 5));
			vars.put("vText5", stringAtIndex(responseValues, 6));
			vars.put("vText4", stringAtIndex(responseValues, 7));
			vars.put("vText3", stringAtIndex(responseValues, 8));
			vars.put("vText2", stringAtIndex(responseValues, 9));
			vars.put("prevref", stringAtIndex(responseValues, 10));
			vars.put("emotionalhistory", stringAtIndex(responseValues, 12));
			vars.put("ttsLocMP3", stringAtIndex(responseValues, 13));
			vars.put("ttsLocTXT", stringAtIndex(responseValues, 14));
			vars.put("ttsLocTXT3", stringAtIndex(responseValues, 15));
			vars.put("ttsText", stringAtIndex(responseValues, 16));
			vars.put("lineRef", stringAtIndex(responseValues, 17));
			vars.put("lineURL", stringAtIndex(responseValues, 18));
			vars.put("linePOST", stringAtIndex(responseValues, 19));
			vars.put("lineChoices", stringAtIndex(responseValues, 20));
			vars.put("lineChoicesAbbrev", stringAtIndex(responseValues, 21));
			vars.put("typingData", stringAtIndex(responseValues, 22));
			vars.put("divert", stringAtIndex(responseValues, 23));

			return stringAtIndex(responseValues, 16);
		}

		public String parametersToWWWFormURLEncoded(
				Map<String, String> parameters) throws Exception {
			StringBuilder s = new StringBuilder();
			for (Map.Entry<String, String> parameter : parameters.entrySet()) {
				if (s.length() > 0) {
					s.append("&");
				}
				s.append(URLEncoder.encode(parameter.getKey(), "UTF-8"));
				s.append("=");
				s.append(URLEncoder.encode(parameter.getValue(), "UTF-8"));
			}
			return s.toString();
		}

		private String md5(String input) throws Exception {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(input.getBytes("UTF-8"));
			BigInteger hash = new BigInteger(1, md5.digest());
			return String.format("%1$032X", hash);
		}

		private String post(String url, Map<String, String> parameters)
				throws Exception {
			URLConnection connection = new java.net.URL(url).openConnection();
			connection
					.setRequestProperty("User-Agent",
							"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			OutputStreamWriter osw = new OutputStreamWriter(
					connection.getOutputStream());
			osw.write(parametersToWWWFormURLEncoded(parameters));
			osw.flush();
			osw.close();
			Reader r = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			StringWriter w = new StringWriter();
			char[] buffer = new char[1024];
			int n = 0;
			while ((n = r.read(buffer)) != -1) {
				w.write(buffer, 0, n);
			}
			r.close();
			return w.toString();
		}

		private String stringAtIndex(String[] strings, int index) {
			if (index >= strings.length)
				return "";
			return strings[index];
		}

	}
}