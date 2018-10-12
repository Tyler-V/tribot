package scripts.starfox.api2007.zybez;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author erickho123 cuhz i want my name in dis bitch yolo
 */
public class Curse {

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36";
    public static final String LOGIN_HTTP_URL = "http://forums.zybez.net/index.php?app=curseauth&module=global&section=login";
    public static final String POST_HTTP_URL = "http://forums.zybez.net/index.php?app=curseauth&module=global&section=login&do=process";

    //protected int type = 0;

    private final String username;
    private final String password;
    private String auth_key;

    /**
     * Constructs a new Curse.
     *
     * @param username The curse username.
     * @param password The curse password.
     */
    public Curse(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Gets the curse username.
     *
     * @return The curse username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the curse password.
     *
     * @return The curse password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the authorization key.
     *
     * @return The authorization key.
     */
    public String getAuthKey() {
        return auth_key;
    }

    /**
     * Sets the authorization key.
     *
     * @param auth_key The authorization key to set.
     */
    public void setAuthKey(String auth_key) {
        this.auth_key = auth_key;
    }

    /**
     * Logs into curse.
     *
     * @return True if successful, false otherwise.
     */
    public boolean login() {
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
        if (goToLoginPageHTTP()) {
            return goToPostPageHTTP();
        }
        return false;
    }

    /**
     * Navigates to the login HTTP page.
     *
     * @return True if successful, false otherwise.
     */
    private boolean goToLoginPageHTTP() {
        try {
            URL obj = new URL(LOGIN_HTTP_URL);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);

            int responseCode = con.getResponseCode();

            StringBuilder s;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                s = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    s.append(inputLine);
                }
            }
            if (responseCode == 200) {
                int idx = s.indexOf("auth_key");
                if (idx > 0) {
                    idx = s.indexOf("value='", idx) + 7;
                    setAuthKey(s.substring(idx, s.indexOf("'", idx)));
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to navigate curse login HTTP page.");
        }
        return false;

    }

    /**
     * Navigates to the curse post HTTP page.
     *
     * @return True if successful, false otherwise.
     */
    private boolean goToPostPageHTTP() {
        boolean retValue = false;
        try {
            URL obj = new URL(POST_HTTP_URL);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setInstanceFollowRedirects(false);

            con.setRequestMethod("POST");
            con.setRequestProperty("Accept", "text/html, application/xhtml+xml, */*");
            con.setRequestProperty("Referer", LOGIN_HTTP_URL);
            con.setRequestProperty("Accept-Language", "en-US");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("Cache-Control", "no-cache");

            String urlParameters = "auth_key=" + getAuthKey()
                    + "&rememberMe=1&ips_username=" + getUsername()
                    + "&ips_password=" + getPassword()
                    + "&submit=Login";

            con.setDoOutput(true);

            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.writeBytes(urlParameters);
                wr.flush();
            }

            int responseCode = con.getResponseCode();

            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                //
            }
            if ((responseCode == 302) || (responseCode == 301)) {
                return true;
            } else {
                System.out.println("The username/password combination for curse login failed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retValue;
    }
}
