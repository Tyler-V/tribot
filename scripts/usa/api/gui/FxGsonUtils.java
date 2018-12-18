package scripts.usa.api.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

import org.tribot.util.Util;

import com.google.gson.Gson;

import javafx.scene.control.TextInputDialog;
import scripts.usa.api.framework.task.ScriptVars;
import scripts.usa.api.fxgson.FxGson;

public class FxGsonUtils {

	public static String promptTextInput() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle(ScriptVars.get()
				.getScriptManifest()
				.name());
		dialog.setHeaderText("Please enter a name to save your profile.");
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent())
			return result.get();
		else
			System.out.println("You must enter a name to save your profile.");
		return null;
	}

	public static <T> void save(T object, String filePath) {
		Gson gson = FxGson.coreBuilder()
				.setPrettyPrinting()
				.create();

		save(gson, object, filePath);
	}

	public static <T> void save(Gson gson, T object, String filePath) {
		try {
			File file = new File(filePath);
			file.getParentFile()
					.mkdirs();

			if (!file.exists())
				file.createNewFile();

			try (FileWriter writer = new FileWriter(file)) {
				writer.write(gson.toJson(object));
				writer.flush();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String load(String directory, String fileName) {
		try {
			File file = new File(Util.getWorkingDirectory() + File.separator + directory + File.separator + fileName);
			if (!file.exists()) {
				return null;
			}
			return readFile(file);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	private static String readFile(File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			return sb.toString();
		}
		finally {
			br.close();
		}
	}

}
