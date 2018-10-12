package scripts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tribot.api2007.Banking;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;

@ScriptManifest(authors = { "Usa" }, category = "Tools", name = "USA Bank Checker")
public class UsaBankChecker extends Script {

	private int total = 0;
	private int item = 1;

	@Override
	public void run() {

		if (Banking.isBankScreenOpen())
			Banking.openBank();

		if (Banking.isBankScreenOpen()) {
			RSItem[] items = Banking.getAll();
			if (items.length > 0) {
				for (RSItem r : items) {
					if (r != null) {
						RSItemDefinition d = r.getDefinition();
						if (d != null) {
							String name = d.getName();
							if (name != null) {
								int id = r.getID();
								int stack = r.getStack();
								if (stack > 0 && id > 0) {
									if (id == 995) {
										println(item + ": You have " + addCommasToNumericString(Integer.toString(stack))
												+ " coins!");
										total += stack;
									} else {
										long timer = System.currentTimeMillis() + 5000;
										while (timer > System.currentTimeMillis()) {
											int value = getPrice(id) * stack;
											if (value != 0) {
												println(item + ": (" + addCommasToNumericString(Integer.toString(stack))
														+ ") x " + name + " has a value of "
														+ addCommasToNumericString(Integer.toString(value)) + " gp.");
												total += value;
												break;
											}
											sleep(100);
										}
									}
									item++;
								}
							}
						}
					}
				}
			}
		}
		println("The total value of your bank is " + addCommasToNumericString(Integer.toString(total)) + " gp.");
	}

	private String addCommasToNumericString(String digits) {
		String result = "";
		int len = digits.length();
		int nDigits = 0;

		if (digits.length() < 4)
			return digits;

		for (int i = len - 1; i >= 0; i--) {
			result = digits.charAt(i) + result;
			nDigits++;
			if (((nDigits % 3) == 0) && (i > 0)) {
				result = "," + result;
			}
		}
		return (result);
	}

	public int getPrice(final int itemID) {

		try {

			URL u = new URL(
					"http://services.runescape.com/m=itemdb_oldschool/api/catalogue/detail.json?item=" + itemID);
			URLConnection c = u.openConnection();

			BufferedReader r = new BufferedReader(new InputStreamReader(c.getInputStream()));

			try {

				String text = r.readLine();
				if (text == null || text.length() == 0)
					return 0;

				String regex = "current\"\\:\\{(.*?)\\}";
				Matcher matcher = Pattern.compile(regex).matcher(text);
				if (matcher.find()) {
					regex = matcher.group().replaceAll("\"|:|}|\\{|,", "").replaceAll("^(.*?)price", "");
					if (regex.contains("m")) {
						return Integer.parseInt(
								String.format("%.0f", Double.parseDouble(regex.replaceAll("[^\\d.]", "")) * 1000000));
					} else if (regex.contains("k")) {
						return Integer.parseInt(
								String.format("%.0f", Double.parseDouble(regex.replaceAll("[^\\d.]", "")) * 1000));
					} else {
						return Integer.parseInt(regex);
					}
				}

			} finally {
				r.close();
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return 0;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}

		return 0;
	}

}
