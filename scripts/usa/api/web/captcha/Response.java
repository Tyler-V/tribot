package scripts.usa.api.web.captcha;

public class Response {
	private String response = "";

	Response(String response) {
		this.response = response;
	}

	public boolean isOk() {
		if (this.response.isEmpty())
			return false;
		return this.response.substring(0, 2).equalsIgnoreCase("ok");
	}

	public String getResult() {
		if (this.response.isEmpty())
			return this.response;
		return this.response.substring(3);
	}

	public String getResponse() {
		return this.response;
	}
}