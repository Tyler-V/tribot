package scripts.usa.api.gui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class FxDialogs {

	public static void showInformation(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.initStyle(StageStyle.UTILITY);
		alert.setTitle("Information");
		alert.setHeaderText(title);
		alert.setContentText(message);

		alert.showAndWait();
	}

	public static void showWarning(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.initStyle(StageStyle.UTILITY);
		alert.setTitle("Warning");
		alert.setHeaderText(title);
		alert.setContentText(message);

		alert.showAndWait();
	}

	public static void showError(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.initStyle(StageStyle.UTILITY);
		alert.setTitle("Error");
		alert.setHeaderText(title);
		alert.setContentText(message);

		alert.showAndWait();
	}

	public static void showException(String title, String message, Exception exception) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.initStyle(StageStyle.UTILITY);
		alert.setTitle("Exception");
		alert.setHeaderText(title);
		alert.setContentText(message);

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);
		String exceptionText = sw.toString();

		Label label = new Label("Details:");

		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		alert.getDialogPane()
				.setExpandableContent(expContent);

		alert.showAndWait();
	}

	public static final String YES = "Yes";
	public static final String NO = "No";
	public static final String OK = "OK";
	public static final String CANCEL = "Cancel";

	public static String showConfirm(String title, String message, String... options) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.initStyle(StageStyle.UTILITY);
		alert.setTitle("Choose an option");
		alert.setHeaderText(title);
		alert.setContentText(message);

		if (options == null || options.length == 0) {
			options = new String[] { OK, CANCEL };
		}

		List<ButtonType> buttons = new ArrayList<>();
		for (String option : options) {
			buttons.add(new ButtonType(option));
		}

		alert.getButtonTypes()
				.setAll(buttons);

		Optional<ButtonType> result = alert.showAndWait();
		if (!result.isPresent()) {
			return CANCEL;
		}
		else {
			return result.get()
					.getText();
		}
	}

	public static String showTextInput(String title, String header, String message, String defaultValue) {
		TextInputDialog dialog = new TextInputDialog(defaultValue);
		dialog.initStyle(StageStyle.UTILITY);
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		dialog.setContentText(message);

		Stage stage = (Stage) dialog.getDialogPane()
				.getScene()
				.getWindow();
		stage.setAlwaysOnTop(true);
		stage.toFront();

		dialog.setOnShown(new EventHandler<DialogEvent>() {
			public void handle(final DialogEvent event) {
				centerWindow(stage, 300, 185);
			}
		});

		Optional<String> result = dialog.showAndWait();
		return result.orElse(null);
	}

	public static String showChoiceDialog(String title, String header, String message, String... options) {
		ChoiceDialog<String> dialog = new ChoiceDialog<>(options[0], options);
		dialog.initStyle(StageStyle.UTILITY);
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		dialog.setContentText(message);

		Stage stage = (Stage) dialog.getDialogPane()
				.getScene()
				.getWindow();
		stage.setAlwaysOnTop(true);
		stage.toFront();

		dialog.setOnShown(new EventHandler<DialogEvent>() {
			public void handle(final DialogEvent event) {
				centerWindow(stage, 225, 185);
			}
		});

		Optional<String> result = dialog.showAndWait();
		return result.orElse(null);
	}

	private static void centerWindow(Stage stage, double width, double height) {
		Rectangle2D bounds = Screen.getPrimary()
				.getVisualBounds();
		stage.setX((bounds.getWidth() - width) / 2);
		stage.setY((bounds.getHeight() - height) / 2);
	}

}
