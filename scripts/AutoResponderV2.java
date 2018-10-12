package scripts;

import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.tribot.api.General;
import org.tribot.api.input.Keyboard;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Player;
import org.tribot.api2007.Players;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.Painting;
import org.w3c.dom.Document;

@ScriptManifest(authors = { "Usa" }, category = "Client", name = "Auto Responder V2")
public abstract class AutoResponderV2 extends Script implements Painting, MessageListening07 {

	private static ArrayList<Conversation> history = new ArrayList<Conversation>();

	private static int MAX_PLAYERS_ON_SCREEN_TO_CHAT = 1;
	private static int MAX_RESPONSES_TO_EACH_PLAYER = 5;
	private static long MIN_DELAY_BETWEEN_MESSAGES = 20000;
	private static long MAX_DELAY_BETWEEN_MESSAGES = 60000;
	private static double SMILE_CHANCE = 10.0;
	private static double QUESTION_CHANCE = 20.0;
	private static double PUNCTUATION_CHANCE = 10.0;
	private static double TYPO_CHANCE = 10.0;

	private final static String[] GREETINGS = new String[] { "hey", "hi", "sup", "hola", "yo", "yoo", "hello" };

	private final static String[] LEAVING_KEYWORDS = new String[] { "bye", "cya", "cyah", "later", "goodbye", "seeya",
			"see-ya", "lata", "peace" };

	private final static String LEAVING_RESPONSES[] = new String[] { "ok", "bye", "laterr", "cya", "later", "lata",
			"peace", "good bye", "goodbye", "later man", "later dude", "bye man", "bye dude", "peace man",
			"ok sounds good", "thanks cya", "haha kk", "k", "haha" };

	private final static String[] BOTTING_KEYWORDS = new String[] { "bot", "bots", "botters", "macroers", "cheaters",
			"bottin", "botting", "macro", "macroing", "cheat", "cheating", "cheater", "botter", "macroer", "boting",
			"botin" };

	private final static String BOTTING_RESPONSES[] = new String[] { "no", "nop", "nope", "nah", "nahh", "noo", "naw",
			"nopee", "not a bot", "not botting", "lol whatever", "lol", "nice try", "i thought you were",
			"lol i figured you were", "i guessed u were lol", "r u?", "u sure u arent?", "i think you are",
			"maybe you are", "not me", "haha", "lol", "what?", "?", "??", "???", "...?", "..??", "..?", "no man",
			"no i dont do that", "nah im legit", "im legit", "i am legit", "nah man", "naw man", "rofl", "lol???",
			"dont think so", "you sure?", "hahaha", "lol??" };

	private final static String GENERIC_RESPONSES[] = new String[] { "?", "??", "huh", "what", "wat", "lol", "sorry",
			"idk", "idk sorry", "what do you mean", "not sure", "not sure what you mean", "not sure what u mean", "uhh",
			"hm", "hmm", "how", "why" };

	private final static String SMILE_FACES[] = new String[] { ":)", ":)", ":)", ":D", ":O", ":]", ":(", ":[", ":X" };

	private final static String CONFUSED_RESPONSES[] = new String[] { "what skill", "what do u mean", "which",
			"what one", "?", "??", "???", "huh", "what", "hm", "huh", "idk", "lol what", "wat", "lol what", "lol wat" };

	private final static String QUESTION_RESPONSES[] = new String[] { "why", "y", "u", "you", "hbu", "what about you",
			"how bout yourself", "how about u", "how about yourself", "and you", "..you", "..u" };

	private final static String PUNCTUATION[] = new String[] { ".", ".", "!", "?" };

	private final static String PREFERENCES[] = { "is annoying", "is so annoying", "is my favorite", "is fun",
			"is so fun", "can be fun", "is not fun", "can be stressful", "is meh", "is interesting", "is challenging",
			"is stressful", "is not my best stat", "is what im working on", "is what im trying to level up" };

	private final static String LEVEL_KEYWORDS[] = new String[] { "lvl", "lvls", "level", "levels", "stat", "stats",
			"lev", "combat" };

	private final static String LEVEL_RESPONSES[] = new String[] { "level", "level", "level", "idk", "99",
			"i am " + "level" + " in " + "skill", "i have " + "level" + " in " + "skill",
			"i have " + "level" + " " + "skill", "i got " + "level" + " in " + "skill",
			"i got " + "level" + " " + "skill", "i've got " + "level", "lvl " + "level", "level" + " wooo",
			"about to be " + "level1", "Just got " + "level", "meh, " + "level", "like " + "level",
			"gettin close to " + "level1", "idk " + "level", "around " + "level", "almost " + "level1",
			"level1" + " soon", "level" + " " + "skill", "level" + " ish", "recently got " + "level",
			"im around " + "level", "uhh " + "level", "uhh like " + "level", "level" + " or " + "level1" + " i think",
			"level" + " lol", "lol " + "level", "over " + "level", "im about " + "level", "idk like " + "level",
			"im like " + "level", "about " + "level", "like almost " + "level1", "almost " + "level1",
			"closing in on " + "level1",
			"level" + ". " + "skill" + " " + PREFERENCES[General.random(0, PREFERENCES.length - 1)],
			"level" + ".. " + "skill" + " " + PREFERENCES[General.random(0, PREFERENCES.length - 1)],
			"level" + ", " + "skill" + " " + PREFERENCES[General.random(0, PREFERENCES.length - 1)] };

	public AutoResponderV2(int MAX_PLAYERS_ON_SCREEN_TO_CHAT, int MAX_RESPONSES_TO_EACH_PLAYER,
			long MIN_DELAY_BETWEEN_MESSAGES, long MAX_DELAY_BETWEEN_MESSAGES, double SMILE_CHANCE,
			double QUESTION_CHANCE, double PUNCTUATION_CHANCE, double TYPO_CHANCE) {

		this.MAX_PLAYERS_ON_SCREEN_TO_CHAT = MAX_PLAYERS_ON_SCREEN_TO_CHAT;
		this.MAX_RESPONSES_TO_EACH_PLAYER = MAX_RESPONSES_TO_EACH_PLAYER;
		this.MIN_DELAY_BETWEEN_MESSAGES = MIN_DELAY_BETWEEN_MESSAGES;
		this.MAX_DELAY_BETWEEN_MESSAGES = MAX_DELAY_BETWEEN_MESSAGES;
		this.SMILE_CHANCE = SMILE_CHANCE;
		this.QUESTION_CHANCE = QUESTION_CHANCE;
		this.PUNCTUATION_CHANCE = PUNCTUATION_CHANCE;
		this.TYPO_CHANCE = TYPO_CHANCE;

	}

	public static ArrayList<Conversation> getHistory() {

		return history;

	}

	public static void checkMessage(String username, String message, ArrayList<Conversation> history) {

		if (playerCommunicatingWithUs(username, message, history)) {

			Conversation conversation = getChatHistory(username, history);

			if (conversation == null) {

				Session session = new Session();
				conversation = new Conversation(username, new ArrayList<Message>(), session, 0);
				history.add(conversation);

			}

			int delay = General.random((int) MIN_DELAY_BETWEEN_MESSAGES, (int) MAX_DELAY_BETWEEN_MESSAGES);

			if (System.currentTimeMillis() > (conversation.getTimeOfLastMessage() + delay)) {

				String response = "";

				boolean isBot = false;

				String[] m = formatMessage(message);

				if (responseContains(LEVEL_KEYWORDS, m)) {

					response = generateLevelResponse(getSkill(m));

				} else if (responseContains(BOTTING_KEYWORDS, m)) {

					response = selectRandomResponse(BOTTING_RESPONSES);

				} else if (responseContains(GREETINGS, m)) {

					response = selectRandomResponse(GREETINGS);

				} else if (responseContains(LEAVING_KEYWORDS, m)) {

					response = selectRandomResponse(LEAVING_RESPONSES);

				} else {

					try {

						response = PandoraBot.ask(conversation.getSession(), message);

						if (!validPandoraResponse(response, history)) {

							response = selectRandomResponse(GENERIC_RESPONSES);

						} else {

							isBot = true;

						}

					} catch (Exception e) {

						e.printStackTrace();

					}

				}

				if (!responseAlreadyUsed(response, history)) {

					if (isChance(TYPO_CHANCE))
						response = generateTypo(response);

					if (isChance(SMILE_CHANCE))
						response = response + " " + SMILE_FACES[General.random(0, SMILE_FACES.length - 1)];

					if (isChance(PUNCTUATION_CHANCE))
						response = response + " " + PUNCTUATION[General.random(0, PUNCTUATION.length - 1)];

					System.out.println(username + ": " + message + " -> Response: " + response);

					conversation.addMessage(new Message(username, message, response, isBot));

					General.sleep((long) General.randomSD(1000, 250));

					Keyboard.typeSend(response);

					conversation.setTime(System.currentTimeMillis());

				}

			}

		}

	}

	private static boolean validPandoraResponse(String response, ArrayList<Conversation> history) {

		if (response == null || response.isEmpty() || response.contains("www") || response.contains("http")
				|| response.contains("bot") || response.contains("Chomsky") || response.contains("question")
				|| response.contains("talk") || response.contains("Wikipedia")) {
			return false;
		}

		if (response.length() > 30)
			return false;

		if (getPunctuationCount(response, 3) >= 3)
			return false;

		if (responseAlreadyUsed(response, history))
			return false;

		return true;

	}

	private static boolean responseAlreadyUsed(String response, ArrayList<Conversation> history) {

		for (Conversation c : history) {

			for (Message m : c.getMessages()) {

				if (m.getResponse().equalsIgnoreCase(response))
					return true;

			}

		}

		return false;

	}

	private static int getPunctuationCount(String response, int max) {

		char[] punctuation = { '?', '.', '!', ',', '\'' };
		int count = 0;

		for (char m : response.toCharArray()) {

			if (count >= max)
				return count;

			for (char c : punctuation) {
				if (m == c)
					count++;
			}

		}

		return count;

	}

	private static boolean isChance(double percent) {

		double r = General.randomDouble(0.0, 100.0);

		return r <= percent;

	}

	private static String[] formatMessage(String message) {

		String s = message.replaceAll("\\?", "").replaceAll("\\.", "").replaceAll("!", "");

		return s.split(" ");

	}

	/**
	 * Cases for when to engage in conversation with a player.
	 * 
	 * 1. If we've talked to the player before and we've responded less than the
	 * MAX_RESPONSES value.
	 * 
	 * 2. If there is only one other player loaded in your area.
	 * 
	 * 3. If there are <= MAX_PLAYERS_ON_SCREEN_TO_CHAT on screen talking.
	 * 
	 * 4. If the message partially or fully contains your player username.
	 * 
	 * 5. If a player is interacting with your player (i.e.
	 * following/attacking/etc.)
	 * 
	 * 6. If there is only one player on screen and a BOT_KEYWORD is mentioned
	 */
	private static boolean playerCommunicatingWithUs(String username, String message, ArrayList<Conversation> history) {

		String n = Player.getRSPlayer().getName();
		if (n == null)
			return false;

		if (n.equals(username))
			return false;

		Conversation c = getChatHistory(username, history);

		if (c != null) {

			if (MAX_RESPONSES_TO_EACH_PLAYER != 0 && c.getMessages().size() >= MAX_RESPONSES_TO_EACH_PLAYER)
				return false;

			for (Message m : c.getMessages()) {

				if (m.getMessage().equalsIgnoreCase(message))
					return false;

			}

			return true;

		}

		if (onePlayerInArea())
			return true;

		if (getPlayersOnScreen() <= MAX_PLAYERS_ON_SCREEN_TO_CHAT)
			return true;

		if (messageContainsOurName(message))
			return true;

		if (playerInteractingWithUs(username))
			return true;

		if (getPlayersOnScreen() == 1 && responseContains(BOTTING_KEYWORDS, formatMessage(message)))
			return true;

		return false;

	}

	private static boolean onePlayerInArea() {

		String name = Player.getRSPlayer().getName();

		if (name == null)
			return false;

		return Players.getAll(Filters.Players.nameNotEquals(name)).length == 1;

	}

	private static boolean playerInteractingWithUs(String username) {

		return Players.getAll(new Filter<RSPlayer>() {

			public boolean accept(RSPlayer player) {

				if (player == null)
					return false;

				if (!player.getName().equalsIgnoreCase(username))
					return false;

				if (!player.isInteractingWithMe())
					return false;

				return true;

			}

		}).length > 0;

	}

	private static boolean messageContainsOurName(String message) {

		String username = Player.getRSPlayer().getName();

		if (username == null)
			return false;

		String split[] = username.split(" ");

		for (String s : split) {

			if (message.contains(s))
				return true;

		}

		return false;

	}

	private static int getPlayersOnScreen() {

		return Players.getAll(new Filter<RSPlayer>() {

			public boolean accept(RSPlayer player) {

				if (player == null)
					return false;

				String MY_NAME = Player.getRSPlayer().getName();
				if (MY_NAME == null)
					return false;

				String PLAYER_NAME = player.getName();
				if (PLAYER_NAME == null)
					return false;

				if (MY_NAME.equalsIgnoreCase(PLAYER_NAME))
					return false;

				if (!player.isOnScreen() && player.getPosition().distanceTo(Player.getPosition()) > 8)
					return false;

				return true;

			}

		}).length;

	}

	private static Conversation getChatHistory(String username, ArrayList<Conversation> conversation) {

		for (Conversation c : conversation) {

			if (c.username.equals(username))
				return c;

		}

		return null;

	}

	private static boolean responseContains(String[] KEYWORDS, String[] message) {

		for (String i : message) {

			for (String j : KEYWORDS) {

				if (i.equalsIgnoreCase(j))
					return true;

			}

		}

		return false;

	}

	private static String selectRandomResponse(String[] RESPONSES) {

		return RESPONSES[General.random(0, RESPONSES.length - 1)];

	}

	private static SKILL_KEYWORDS getSkill(String[] message) {

		for (SKILL_KEYWORDS skill : SKILL_KEYWORDS.values()) {

			for (String m : message) {

				for (String k : skill.keywords) {

					if (m.equalsIgnoreCase(k))
						return skill;

				}

			}

		}

		return null;

	}

	private static String generateTypo(String response) {

		char[] char_array = response.toCharArray();

		int place = General.random(0, char_array.length - 1);

		char_array[place] = '\0';

		return new String(char_array);

	}

	private static String generateLevelResponse(SKILL_KEYWORDS SKILL) {

		if (SKILL != null) {

			int level = Skills.getActualLevel(SKILL.skill);

			String skill = SKILL.keywords[General.random(0, SKILL.keywords.length - 1)];

			String response = LEVEL_RESPONSES[General.random(0, LEVEL_RESPONSES.length - 1)];

			response = response.replaceAll("level1", Integer.toString(level + 1));

			response = response.replaceAll("level", Integer.toString(level));

			response = response.replaceAll("skill", skill);

			if (isChance(QUESTION_CHANCE)) {

				response = response + " " + QUESTION_RESPONSES[General.random(0, QUESTION_RESPONSES.length - 1)];

				if (isChance(50.0)) {
					response = response + "?";

				} else if (isChance(PUNCTUATION_CHANCE)) {

					response = response + PUNCTUATION[General.random(0, PUNCTUATION.length - 1)];

				}

				return response;

			}

			return response;

		}

		return CONFUSED_RESPONSES[General.random(0, CONFUSED_RESPONSES.length - 1)];

	}

	private enum SKILL_KEYWORDS {

		ATTACK(SKILLS.ATTACK, new String[] { "atk", "attk", "attack" }),

		STRENGTH(SKILLS.STRENGTH, new String[] { "str", "strength" }),

		DEFENCE(SKILLS.DEFENCE, new String[] { "def", "defence", "defense" }),

		RANGED(SKILLS.RANGED, new String[] { "rang", "range", "ranged" }),

		PRAYER(SKILLS.PRAYER, new String[] { "pray", "prayer" }),

		MAGIC(SKILLS.MAGIC, new String[] { "mage", "magic" }),

		RUNECRAFTING(SKILLS.RUNECRAFTING, new String[] { "rc", "runecraft", "runecrafting", "runecraftin" }),

		CONSTRUCTION(SKILLS.CONSTRUCTION, new String[] { "con", "construction", "constructing" }),

		HITPOINTS(SKILLS.HITPOINTS, new String[] { "hp", "hitpoint", "hitpoints", "health" }),

		AGILITY(SKILLS.AGILITY, new String[] { "agil", "agile", "agility" }),

		HERBLORE(SKILLS.HERBLORE, new String[] { "herb", "herblore" }),

		THIEVING(SKILLS.THIEVING, new String[] { "thieve", "thievin", "thieving" }),

		CRAFTING(SKILLS.CRAFTING, new String[] { "craft", "craftin", "crafting" }),

		FLETCHING(SKILLS.FLETCHING, new String[] { "fletch", "fletching" }),

		SLAYER(SKILLS.SLAYER, new String[] { "slay", "slayer", "slaying" }),

		HUNTER(SKILLS.HUNTER, new String[] { "hunt", "hunter", "hunting" }),

		MINING(SKILLS.MINING, new String[] { "mine", "minin", "mining" }),

		SMITHING(SKILLS.SMITHING, new String[] { "smith", "smithin", "smithing" }),

		FISHING(SKILLS.FISHING, new String[] { "fish", "fishin", "fishing" }),

		COOKING(SKILLS.COOKING, new String[] { "cook", "cookin", "cooking" }),

		FIREMAKING(SKILLS.FIREMAKING, new String[] { "fire", "fm", "firemakin", "firemaking" }),

		WOODCUTTING(SKILLS.WOODCUTTING, new String[] { "wc", "woodcuttin", "woodcutting" }),

		FARMING(SKILLS.FARMING, new String[] { "farm", "farmin", "farming" });

		private final SKILLS skill;
		private final String[] keywords;

		SKILL_KEYWORDS(SKILLS skill, String[] keywords) {
			this.skill = skill;
			this.keywords = keywords;
		}

	}

	// }

	public static class PandoraBot {

		public static String ask(Session session, String text) throws Exception {
			session.vars.put("input", text);

			String response = post("http://www.pandorabots.com/pandora/talk-xml", session.vars);

			return (xPathSearch(response, "//result/that/text()"));
		}

		public static String parametersToWWWFormURLEncoded(Map<String, String> parameters) throws Exception {
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

		public static String md5(String input) throws Exception {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(input.getBytes("UTF-8"));
			BigInteger hash = new BigInteger(1, md5.digest());
			return String.format("%1$032X", hash);
		}

		public static String post(String url, Map<String, String> parameters) throws Exception {
			URLConnection connection = new URL(url).openConnection();
			connection.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.95 Safari/537.36");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
			osw.write(parametersToWWWFormURLEncoded(parameters));
			osw.flush();
			osw.close();
			Reader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringWriter w = new StringWriter();
			char[] buffer = new char[1024];
			int n = 0;
			while ((n = r.read(buffer)) != -1) {
				w.write(buffer, 0, n);
			}
			r.close();
			return w.toString();
		}

		public static String xPathSearch(String input, String expression) throws Exception {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			XPath xPath = XPathFactory.newInstance().newXPath();
			XPathExpression xPathExpression = xPath.compile(expression);
			Document document = documentBuilder.parse(new ByteArrayInputStream(input.getBytes("UTF-8")));
			String output = (String) xPathExpression.evaluate(document, XPathConstants.STRING);
			return output == null ? "" : output.trim();
		}

		public static String stringAtIndex(String[] strings, int index) {
			if (index >= strings.length)
				return "";
			return strings[index];
		}
	}

	private static class Session {

		private final Map<String, String> vars;
		private final String botid = "b0dafd24ee35a477";

		public Session() {
			vars = new LinkedHashMap<String, String>();
			vars.put("botid", botid);
			vars.put("custid", UUID.randomUUID().toString());
		}

	}

	public static class Message {

		private String username;
		private String message;
		private String response;
		private boolean bot;

		public Message(String username, String message, String response, boolean bot) {
			this.message = message;
			this.response = response;
		}

		public String getUsername() {
			return username;
		}

		public String getMessage() {
			return message;
		}

		public String getResponse() {
			return response;
		}

		public boolean isBot() {
			return bot;
		}

	}

	public static class Conversation {

		private String username;
		private ArrayList<Message> messages;
		private Session session;
		private long time;

		public Conversation(String username, ArrayList<Message> messages, Session session, long time) {
			this.username = username;
			this.messages = messages;
			this.session = session;
			this.time = time;
		}

		public String getUsername() {
			return username;
		}

		public ArrayList<Message> getMessages() {
			return messages;
		}

		public void addMessage(Message message) {
			messages.add(message);
		}

		public Session getSession() {
			return session;
		}

		public void setTime(long t) {
			this.time = t;
		}

		public long getTimeOfLastMessage() {
			return time;
		}
	}

}