package scripts.usa.api.gui;

import org.tribot.api.General;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class JFXDialogs {

	public static class TextPrompt {
		private JFXDialog dialog;
		private JFXTextField textField;
		private JFXButton button;

		TextPrompt(JFXDialog dialog, JFXTextField textField, JFXButton button) {
			this.dialog = dialog;
			this.textField = textField;
			this.button = button;
		}

		public JFXButton getButton() {
			return this.button;
		}

		public String getText() {
			return this.textField.getText();
		}

		public void close() {
			this.dialog.close();
		}
	}

	public static TextPrompt createTextPrompt(StackPane stackPane, String heading, String labelText, String defaultText, String buttonText) {
		JFXDialogLayout content = new JFXDialogLayout();
		Label label = new Label(labelText);
		JFXTextField textField = new JFXTextField(defaultText);
		HBox hBox = new HBox(label, textField);
		hBox.setSpacing(10);
		hBox.setAlignment(Pos.CENTER);
		content.setHeading(new Label(heading));
		content.setBody(hBox);
		content.setMaxWidth(275);
		content.setMaxHeight(200);
		JFXButton button = new JFXButton(buttonText);
		content.setActions(button);
		JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.BOTTOM);
		dialog.show();
		return new TextPrompt(dialog, textField, button);
	}

	public static class OptionDialog {
		private JFXDialog dialog;
		private JFXComboBox<String> comboBox;
		private JFXButton button;

		OptionDialog(JFXDialog dialog, JFXComboBox<String> comboBox, JFXButton button) {
			this.dialog = dialog;
			this.comboBox = comboBox;
			this.button = button;
		}

		public JFXButton getButton() {
			return this.button;
		}

		public String getSelected() {
			return this.comboBox.getSelectionModel()
					.getSelectedItem();
		}

		public void close() {
			this.dialog.close();
		}
	}

	public static OptionDialog createOptionDialog(StackPane stackPane, String heading, String buttonText, String promptText, String... options) {
		JFXDialogLayout content = new JFXDialogLayout();
		JFXComboBox<String> comboBox = new JFXComboBox<String>(FXCollections.observableArrayList(options));
		comboBox.setPromptText(promptText);
		HBox hBox = new HBox(comboBox);
		hBox.setAlignment(Pos.CENTER);
		content.setHeading(new Label(heading));
		content.setBody(hBox);
		content.setMaxWidth(200);
		content.setMaxHeight(200);
		JFXButton button = new JFXButton(buttonText);
		content.setActions(button);
		JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.BOTTOM);
		dialog.show();
		return new OptionDialog(dialog, comboBox, button);
	}

}
