package scripts.usa.api.responder;

public class Message {
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