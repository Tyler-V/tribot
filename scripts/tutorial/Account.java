package scripts.tutorial;

public class Account {

	private String display;
	private String email;
	private String password;

	public Account(String display, String email, String password) {
		this.display = display;
		this.email = email;
		this.password = password;
	}

	public String getDisplayName() {
		return display;
	}

	public void setDisplayName(String display) {
		this.display = display;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

}
