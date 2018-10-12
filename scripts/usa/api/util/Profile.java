package scripts.usa.api.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import javax.swing.JOptionPane;

import org.tribot.util.Util;

public class Profile {

	public static ArrayList<String> getSavedProfiles(String folderName) {
		File folder = new File(Util.getWorkingDirectory() + "/" + folderName);
		if (!folder.exists())
			folder.mkdir();
		ArrayList<String> files = new ArrayList<>();
		for (File file : folder.listFiles()) {
			if (file.getName().contains(".txt"))
				files.add(file.getName().replaceAll(".txt", ""));
		}
		return files;
	}

	public static void saveProfile(String folderName, Map<String, Object> data) {
		try {
			String fileName = JOptionPane.showInputDialog("Enter a name to save the profile");
			File folder = new File(Util.getWorkingDirectory() + "/" + folderName);
			File file = new File(folder.toString() + "/" + fileName + ".txt");
			if (!folder.exists())
				folder.mkdir();
			if (file.createNewFile()) {
				System.out.println("Added " + fileName + " to " + file.toString());
			}
			else {
				System.out.println("Updated " + fileName + " at " + folder.toString());
			}
			if (!file.exists())
				return;
			Properties properties = new Properties();
			for (Map.Entry<String, Object> property : data.entrySet())
				properties.put(property.getKey(), property.getValue());
			properties.store(new FileOutputStream(file), null);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}
