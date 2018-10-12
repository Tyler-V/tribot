package scripts.starfox.api.util;

import scripts.starfox.api.ErrorReporting;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * The Website class provides a way to safely browse a website.
 *
 * @author Nolan
 */
public class Website {

    /**
     * Browses the website at specified url with the systems default browser.
     *
     * @param url The url.
     */
    public static void browse(String url) {
        try {
            Desktop.getDesktop().browse(URI.create(url));
        } catch (IOException ex) {
            ErrorReporting.report(ex);
        }
    }

    /**
     * Gets the raw HTML as a string from the specified url.
     *
     * @param url               The URL string.
     * @param requestProperties Any request properties that need to be set.
     *                          Format each property as such: key,,value.
     * @return The raw HTML as a string.
     *         Null if the url could not be connected to or the url was null.
     */
    public static String html(String url, String... requestProperties) {
        try {
            return html(new URL(url), requestProperties);
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Gets the raw HTML as a string from the specified url.
     *
     * @param url               The URL string.
     * @param requestProperties Any request properties that need to be set.
     *                          Format each property as such: key,,value.
     * @return The raw HTML as a string.
     *         Null if the url could not be connected to or the url was null.
     */
    public static String html(URL url, String... requestProperties) {
        if (url == null) {
            return null;
        }
        try {
            URLConnection cn = url.openConnection();
            for (String property : requestProperties) {
                String key = property.split(",,")[0];
                String value = property.split(",,")[1];
                cn.setRequestProperty(key, value);
            }
            StringBuilder sb = new StringBuilder();
            InputStreamReader in = new InputStreamReader(cn.getInputStream());
            BufferedReader br = new BufferedReader(in);
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            in.close();
            br.close();
            return sb.toString();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Gets each line of the HTML from the specified url.
     *
     * @param url               The URL string.
     * @param requestProperties Any request properties that need to be set.
     *                          Format each property as such: key,,value.
     * @return An array of strings where each element represents a line of the HTML.
     *         Null if the url could not be connected to or the url was null.
     */
    public static String[] htmlLine(String url, String... requestProperties) {
        if (url == null) {
            return null;
        }
        try {
            URLConnection cn = new URL(url).openConnection();
            for (String property : requestProperties) {
                String key = property.split(",,")[0];
                String value = property.split(",,")[1];
                cn.setRequestProperty(key, value);
            }
            ArrayList<String> lines = new ArrayList<>();
            try (InputStreamReader in = new InputStreamReader(cn.getInputStream()); BufferedReader br = new BufferedReader(in);) {
                String line;
                while ((line = br.readLine()) != null) {
                    lines.add(line);
                }
            }
            return lines.toArray(new String[lines.size()]);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
