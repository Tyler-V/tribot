package scripts.usa.api.util.fx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.tribot.util.Util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.TextInputDialog;

public class FxSettings {

	private Properties properties;
	private String folderName;

	public FxSettings(String folderName) {
		this.folderName = folderName;
		this.properties = new Properties();
	}

	public Properties getProperties() {
		return this.properties;
	}

	public void newProperties() {
		this.properties = new Properties();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void put(Object control) {
		try {
			if (control instanceof javafx.scene.control.TextField) {
				put(((javafx.scene.control.TextField) control).getId(), ((javafx.scene.control.TextField) control).getText());
			}
			else if (control instanceof javafx.scene.control.Slider) {
				put(((javafx.scene.control.Slider) control).getId(), Double.toString(((javafx.scene.control.Slider) control).getValue()));
			}
			else if (control instanceof javafx.scene.control.ChoiceBox) {
				put(((javafx.scene.control.ChoiceBox) control).getId(), ((javafx.scene.control.ChoiceBox) control).getValue());
			}
			else if (control instanceof javafx.scene.control.Spinner) {
				IntegerSpinnerValueFactory valueFactory = (IntegerSpinnerValueFactory) ((javafx.scene.control.Spinner) control).getValueFactory();
				String values = valueFactory.getMin() + ", " +
						valueFactory.getMax() +
						", " +
						valueFactory.getValue() +
						", " +
						valueFactory.getAmountToStepBy();
				put(((javafx.scene.control.Spinner) control).getId(), values);
			}
			else if (control instanceof javafx.scene.control.ListView) {
				Object[] values = ((javafx.scene.control.ListView) control).getItems().stream().toArray(String[]::new);
				if (values.length > 0) {
					StringBuilder sb = new StringBuilder();
					for (Object name : values)
						sb.append(name.toString() + ", ");
					put(((javafx.scene.control.ListView) control).getId(), sb.toString().substring(0, sb.length() - 2));
				}
			}
			else if (control instanceof javafx.scene.control.CheckBox) {
				put(((javafx.scene.control.CheckBox) control).getId(), Boolean.toString(((javafx.scene.control.CheckBox) control).isSelected()));
			}
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}

	private void put(Object key, Object value) {
		if (value == null || value.toString() == null || value.toString().length() == 0)
			return;
		this.properties.put(key, value);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void set(Object control) {
		try {
			if (control instanceof javafx.scene.control.TextField) {
				String value = getProperty(((javafx.scene.control.TextField) control).getId());
				if (value != null)
					((javafx.scene.control.TextField) control).setText(value);
			}
			else if (control instanceof javafx.scene.control.Slider) {
				String value = getProperty(((javafx.scene.control.Slider) control).getId());
				if (value != null)
					((javafx.scene.control.Slider) control).setValue(Double.parseDouble(value));
			}
			else if (control instanceof javafx.scene.control.ChoiceBox) {
				String value = getProperty(((javafx.scene.control.ChoiceBox) control).getId());
				if (value != null)
					((javafx.scene.control.ChoiceBox) control).setValue(value);
			}
			else if (control instanceof javafx.scene.control.Spinner) {
				String value = getProperty(((javafx.scene.control.Spinner) control).getId());
				if (value != null) {
					String[] split = value.split(", ");
					((javafx.scene.control.Spinner) control).setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
							Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3])));
				}
			}
			else if (control instanceof javafx.scene.control.ListView) {
				String value = getProperty(((javafx.scene.control.ListView) control).getId());
				if (value != null) {
					ObservableList<String> items = FXCollections.observableArrayList(value.split(", "));
					((javafx.scene.control.ListView) control).setItems(items);
				}
			}
			else if (control instanceof javafx.scene.control.CheckBox) {
				String value = getProperty(((javafx.scene.control.CheckBox) control).getId());
				if (value != null)
					((javafx.scene.control.CheckBox) control).setSelected(Boolean.parseBoolean(value));
			}
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}

	private String getProperty(String key) {
		String value = (String) this.properties.get(key);
		if (value != null && value.length() > 0)
			return value;
		return null;
	}

	private String promptTextInput() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle(folderName);
		dialog.setHeaderText("Please enter a name to save your profile.");
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent())
			return result.get();
		else
			System.out.println("You must enter a name to save your profile.");
		return null;
	}

	public void save() {
		try {
			String fileName = this.promptTextInput();
			if (fileName == null)
				return;
			File folder = new File(Util.getWorkingDirectory() + "/" + this.folderName);
			File file = new File(folder.toString() + "/" + fileName + ".txt");
			if (!folder.exists())
				folder.mkdir();
			if (file.createNewFile())
				System.out.println("Added " + fileName + " to " + file.toString());
			else
				System.out.println("Updated " + fileName + " at " + folder.toString());
			if (file.exists())
				this.properties.store(new FileOutputStream(file), null);
			System.out.println(this.properties);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void load(String fileName) {
		try {
			File folder = new File(Util.getWorkingDirectory() + "/" + folderName);
			File file = new File(folder.toString() + "/" + fileName + ".txt");
			if (!file.exists()) {
				System.out.println("Unable to open " + file.toString());
				return;
			}
			Properties prop = new Properties();
			prop.load(new FileInputStream(file));
			this.properties = prop;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<String> getProfiles() {
		List<String> names = new ArrayList<String>();
		try {
			File folder = new File(Util.getWorkingDirectory() + "/" + folderName);
			for (File file : folder.listFiles()) {
				if (file.getName().contains(".txt"))
					names.add(file.getName().replaceAll(".txt", ""));
			}
		}
		catch (Exception e) {
		}
		return names;
	}
}
