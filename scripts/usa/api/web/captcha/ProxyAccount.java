package scripts.usa.api.web.captcha;

public class ProxyAccount {
	private String username;
	private String password;
	private String ip;
	private String port;

	ProxyAccount(String username, String password, String ip, String port) {
		this.username = username;
		this.password = password;
		this.ip = ip;
		this.port = port;
	}

	public String getUsername() {
		return this.username;
	}

	public String getPassword() {
		return this.password;
	}

	public String getIP() {
		return this.ip;
	}

	public String getPort() {
		return this.port;
	}
}