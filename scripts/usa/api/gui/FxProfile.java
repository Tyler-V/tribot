package scripts.usa.api.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import org.tribot.api.General;
import org.tribot.util.Util;

import com.google.gson.Gson;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import scripts.usa.api.framework.task.ScriptVars;
import scripts.usa.api.fxgson.FxGson;
import scripts.usa.api.gui.JFXDialogs.TextPrompt;

public class FxProfile {

	private String folderName;
	private String fileName;

	public FxProfile(String folderName, String fileName) {
		this.folderName = folderName;
		this.fileName = fileName;
	}

	public FxProfile(String folderName) {
		this(folderName, null);
	}

	public String getFileName() {
		return this.fileName;
	}

	private String getDirectory() {
		return Util.getWorkingDirectory() + File.separator + folderName;
	}

	private String getFilePath(String fileName) {
		this.fileName = fileName;
		return getDirectory() + File.separator + fileName;
	}

	public <T> void save(String fileName, T object) {
		Gson gson = FxGson.coreBuilder()
				.setPrettyPrinting()
				.create();

		save(gson, object, fileName);
	}

	private <T> void save(Gson gson, T object, String fileName) {
		try {
			File file = new File(getFilePath(fileName));
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

	public <T> Object load(String fileName, Class<T> classOfT) {
		return FxGson.create()
				.fromJson(getJson(fileName), classOfT);
	}

	private String getJson(String fileName) {
		try {
			File file = new File(getFilePath(fileName));
			if (!file.exists()) {
				return "";
			}
			return readFile(file);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	private String readFile(File file) throws IOException {
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

	public String[] getProfiles() {
		File folder = new File(getDirectory());
		return Arrays.stream(folder.listFiles())
				.map(file -> file.getName())
				.toArray(String[]::new);
	}

	// private String promptTextInput(String defaultValue) {
	// return FxDialogs.showTextInput(ScriptVars.get()
	// .getScriptManifest()
	// .name(), "Enter a name to save your profile", "Name:", defaultValue);
	// }
	//
	// private String promptSelectProfile() {
	// String[] profiles = getProfiles();
	// return FxDialogs.showChoiceDialog(ScriptVars.get()
	// .getScriptManifest()
	// .name(), "Select a profile", "Profiles:", profiles);
	// }
}
