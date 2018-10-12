package scripts.usa.api.responder;

import java.util.List;

public class Conversation {
    private String username;
    private List<Message> messages;
    private Session session;
    private long time;

    public Conversation(String username, List<Message> messages, Session session, long time) {
	this.username = username;
	this.messages = messages;
	this.session = session;
	this.time = time;
    }

    public String getUsername() {
	return this.username;
    }

    public List<Message> getMessages() {
	return this.messages;
    }

    public void addMessage(Message message) {
	this.messages.add(message);
    }

    public Session getSession() {
	return session;
    }

    public void setNextMessageTime(long time) {
	this.time = time;
    }

    public long getNextMessageTime() {
	return this.time;
    }
}