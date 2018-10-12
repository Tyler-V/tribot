package scripts.usa.api.responder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tribot.api.General;
import org.tribot.api.input.Keyboard;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Player;
import org.tribot.api2007.Players;
import org.tribot.api2007.Skills;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSPlayer;

import com.sun.glass.events.KeyEvent;

import scripts.usa.api.responder.Keywords.Skill;

public class AutoResponder {

	private List<Conversation> history;

	private int MAX_PLAYERS_ON_SCREEN_TO_CHAT = 1;
	private int MAX_RESPONSES_TO_EACH_PLAYER = 5;
	private long MIN_DELAY_BETWEEN_MESSAGES = 10000;
	private long MAX_DELAY_BETWEEN_MESSAGES = 30000;
	private double SMILE_CHANCE = 10.0;
	private double QUESTION_CHANCE = 10.0;
	private double PUNCTUATION_CHANCE = 10.0;
	private double TYPO_CHANCE = 10.0;

	public AutoResponder(int MAX_PLAYERS_ON_SCREEN_TO_CHAT, int MAX_RESPONSES_TO_EACH_PLAYER, long MIN_DELAY_BETWEEN_MESSAGES,
			long MAX_DELAY_BETWEEN_MESSAGES, double SMILE_CHANCE, double QUESTION_CHANCE, double PUNCTUATION_CHANCE, double TYPO_CHANCE) {
		this.MAX_PLAYERS_ON_SCREEN_TO_CHAT = MAX_PLAYERS_ON_SCREEN_TO_CHAT;
		this.MAX_RESPONSES_TO_EACH_PLAYER = MAX_RESPONSES_TO_EACH_PLAYER;
		this.MIN_DELAY_BETWEEN_MESSAGES = MIN_DELAY_BETWEEN_MESSAGES;
		this.MAX_DELAY_BETWEEN_MESSAGES = MAX_DELAY_BETWEEN_MESSAGES;
		this.SMILE_CHANCE = SMILE_CHANCE;
		this.QUESTION_CHANCE = QUESTION_CHANCE;
		this.PUNCTUATION_CHANCE = PUNCTUATION_CHANCE;
		this.TYPO_CHANCE = TYPO_CHANCE;
		this.history = Collections.synchronizedList(new ArrayList<Conversation>());
		General.println("Auto Responder initialized");
	}

	public AutoResponder() {
		this.history = Collections.synchronizedList(new ArrayList<Conversation>());
		General.println("Auto Responder initialized");
	}

	public List<Conversation> getHistory() {
		return this.history;
	}

	public void generateResponse(String username, String message) {
		if (playerCommunicatingWithUs(username, message, this.history)) {
			Conversation conversation = getChatHistory(username, this.history);
			if (conversation == null) {
				Session session = new Session();
				conversation = new Conversation(username, new ArrayList<Message>(), session, 0);
				this.history.add(conversation);
			}
			if (System.currentTimeMillis() > conversation.getNextMessageTime()) {
				String response = "";
				boolean isBot = false;
				String[] words = formatAndSplit(message);
				if (messageContains(Keywords.LEVEL_KEYWORDS, words) || messageEqualsSkill(words)) {
					response = generateLevelResponse(getSkill(words));
				}
				else if (messageContains(Keywords.BOTTING_KEYWORDS, words)) {
					response = selectRandomResponse(Keywords.BOTTING_RESPONSES);
				}
				else if (messageContains(Keywords.GREETINGS, words)) {
					response = selectRandomResponse(Keywords.GREETINGS);
				}
				else if (messageContains(Keywords.LEAVING_KEYWORDS, words)) {
					response = selectRandomResponse(Keywords.LEAVING_RESPONSES);
				}
				else {
					try {
						response = PandoraBot.ask(conversation.getSession(), message);
						if (!validPandoraResponse(response)) {
							response = selectRandomResponse(Keywords.GENERIC_RESPONSES);
						}
						else {
							isBot = true;
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (!responseAlreadyUsed(response, this.history)) {
					if (isChance(TYPO_CHANCE))
						response = generateTypo(response);
					if (isChance(SMILE_CHANCE))
						response = response + " " + Keywords.SMILE_FACES[General.random(0, Keywords.SMILE_FACES.length - 1)];
					if (isChance(PUNCTUATION_CHANCE) && !messageContainsPunctuation(response))
						response = response + " " + Keywords.PUNCTUATION[General.random(0, Keywords.PUNCTUATION.length - 1)];
					General.println(username + ": \"" + message + "\" | Response: \"" + response + "\"");
					conversation.addMessage(new Message(username, message, response, isBot));
					General.sleep((long) General.randomSD(1000, 250));
					if (!isChatEmpty())
						clearChat();
					Keyboard.typeSend(response);
					conversation.setNextMessageTime(System.currentTimeMillis() +
							General.random((int) MIN_DELAY_BETWEEN_MESSAGES, (int) MAX_DELAY_BETWEEN_MESSAGES));
				}
			}
		}
	}

	private void clearChat() {
		Keyboard.holdKey((char) KeyEvent.VK_BACKSPACE, Keyboard.getKeyCode((char) KeyEvent.VK_BACKSPACE), new Condition() {
			@Override
			public boolean active() {
				return isChatEmpty();
			}
		});
	}

	private boolean isChatEmpty() {
		RSInterfaceChild child = Interfaces.get(162, 42);
		if (child == null)
			return true;
		String text = child.getText();
		if (text == null || text.isEmpty())
			return true;
		Matcher m = Pattern.compile(Pattern.quote("<col=0000ff>") + "(.*?)" + Pattern.quote("</col>")).matcher(text);
		if (!m.find())
			return true;
		return m.group(1).isEmpty();
	}

	private boolean validPandoraResponse(String response) {
		if (response == null || response.isEmpty() ||
				response.contains("www") ||
				response.contains("http") ||
				response.contains("bot") ||
				response.contains("Chomsky") ||
				response.contains("question") ||
				response.contains("talk") ||
				response.contains("Wikipedia")) {
			return false;
		}
		if (response.length() > 30)
			return false;
		if (getPunctuationCount(response, 3) >= 3)
			return false;
		if (responseAlreadyUsed(response, this.history))
			return false;
		return true;
	}

	private boolean messageContainsPunctuation(String response) {
		return getPunctuationCount(response, 1) > 0;
	}

	private boolean responseAlreadyUsed(String response, List<Conversation> history) {
		for (Conversation c : history) {
			for (Message m : c.getMessages()) {
				if (m.getResponse().equalsIgnoreCase(response))
					return true;
			}
		}
		return false;
	}

	private int getPunctuationCount(String response, int max) {
		char[] punctuation = { '?', '.', '!', ',', '\'' };
		int count = 0;
		for (char i : response.toCharArray()) {
			if (count >= max)
				return count;
			for (char j : punctuation) {
				if (i == j)
					count++;
			}
		}
		return count;
	}

	private boolean isChance(double percent) {
		double r = General.randomDouble(0.0, 100.0);
		return r <= percent;
	}

	private String[] formatAndSplit(String message) {
		message = message.toLowerCase();
		message = message.trim();
		RSPlayer player = Player.getRSPlayer();
		if (player != null) {
			String username = player.getName().toLowerCase();
			if (username != null) {
				String[] split = username.split(" ");
				for (String str : split) {
					message = message.replaceAll(str, "");
				}
			}
		}
		message = message.replaceAll("\\s+", " ");
		message = message.replaceAll("\\.", "");
		message = message.replaceAll("\\?", "");
		message = message.replaceAll(",", "");
		message = message.replaceAll(";", "");
		return message.split(" ");
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
	private boolean playerCommunicatingWithUs(String username, String message, List<Conversation> history) {
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
		if (messageContainsUsername(message))
			return true;
		if (playerInteractingWithUs(username))
			return true;
		if (getPlayersOnScreen() == 1 && messageContains(Keywords.BOTTING_KEYWORDS, formatAndSplit(message)))
			return true;
		return false;
	}

	private boolean onePlayerInArea() {
		String name = Player.getRSPlayer().getName();
		if (name == null)
			return false;
		return Players.getAll(Filters.Players.nameNotEquals(name)).length == 1;
	}

	private boolean playerInteractingWithUs(String username) {
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

	private boolean messageContainsUsername(String message) {
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

	private int getPlayersOnScreen() {
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

	private Conversation getChatHistory(String username, List<Conversation> conversation) {
		for (Conversation c : conversation) {
			if (c.getUsername().equals(username))
				return c;
		}
		return null;
	}

	private boolean messageEqualsSkill(String[] message) {
		if (message.length == 0)
			return false;
		boolean question = false;
		if (message.length > 1) {
			for (String word : message) {
				if (word.toLowerCase().contains("what")) {
					question = true;
					break;
				}
			}
			if (!question)
				return false;
		}
		for (SKILL_KEYWORDS skill : SKILL_KEYWORDS.values()) {
			for (String keyword : skill.getKeywords()) {
				if (message[0].equalsIgnoreCase(keyword))
					return true;
			}
		}
		return false;
	}

	private boolean messageContains(String[] KEYWORDS, String[] message) {
		for (String i : message) {
			for (String j : KEYWORDS) {
				if (i.equalsIgnoreCase(j))
					return true;
			}
		}
		return false;
	}

	private String selectRandomResponse(String[] RESPONSES) {
		return RESPONSES[General.random(0, RESPONSES.length - 1)];
	}

	private Skill getSkill(String[] words) {
		for (Skill skill : Skill.values()) {
			for (String word : words) {
				for (String keyword : skill.getKeywords()) {
					if (word.contains(keyword))
						return skill;
				}
			}
		}
		return null;
	}

	private String generateTypo(String response) {
		char[] char_array = response.toCharArray();
		int place = General.random(0, char_array.length - 1);
		char_array[place] = '\0';
		return new String(char_array);
	}

	private String generateLevelResponse(Skill skill) {
		if (skill == null)
			return Keywords.CONFUSED_RESPONSES[General.random(0, Keywords.CONFUSED_RESPONSES.length - 1)];
		int level = Skills.getActualLevel(skill.getSkill());
		String response = Keywords.LEVEL_RESPONSES[General.random(0, Keywords.LEVEL_RESPONSES.length - 1)];
		response = response.replaceAll("level1", Integer.toString(level + 1));
		response = response.replaceAll("level", Integer.toString(level));
		response = response.replaceAll("skill", skill.getKeywords()[General.random(0, skill.getKeywords().length - 1)]);
		if (isChance(QUESTION_CHANCE))
			response = response + " " + Keywords.QUESTION_RESPONSES[General.random(0, Keywords.QUESTION_RESPONSES.length - 1)];
		return response;
	}
}
