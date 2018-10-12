package scripts.crafter.gui;

import scripts.usa.api.gui.AbstractFxGUI;

public class FxGUI extends AbstractFxGUI {

	@Override
	protected String fxml() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + "\r\n" +
				"<?import com.jfoenix.controls.JFXButton?>\r\n" +
				"<?import com.jfoenix.controls.JFXCheckBox?>\r\n" +
				"<?import com.jfoenix.controls.JFXComboBox?>\r\n" +
				"<?import javafx.scene.control.Separator?>\r\n" +
				"<?import javafx.scene.layout.Pane?>\r\n" +
				"<?import javafx.scene.text.Text?>\r\n" +
				"\r\n" +
				"<Pane maxHeight=\"-Infinity\" maxWidth=\"-Infinity\" minHeight=\"-Infinity\" minWidth=\"-Infinity\" prefHeight=\"253.0\" prefWidth=\"396.0\" styleClass=\"rounded-pane\" stylesheets=\"@styles.css\" xmlns=\"http://javafx.com/javafx/8.0.171\" xmlns:fx=\"http://javafx.com/fxml/1\" fx:controller=\"scripts.crafter.gui.FxController\">\r\n" +
				"	<children>\r\n" +
				"      <Pane prefHeight=\"200.0\" prefWidth=\"396.0\" styleClass=\"rounded-title-pane\">\r\n" +
				"         <children>\r\n" +
				"            <Text fx:id=\"titleText\" layoutY=\"31.0\" strokeType=\"OUTSIDE\" strokeWidth=\"0.0\" styleClass=\"title\" text=\"USA Crafter\" wrappingWidth=\"396.0000042915344\" />\r\n" +
				"            <Text fx:id=\"versionText\" layoutX=\"359.0\" layoutY=\"31.0\" strokeType=\"OUTSIDE\" strokeWidth=\"0.0\" styleClass=\"version\" text=\"v1.0\" />\r\n" +
				"         </children>\r\n" +
				"      </Pane>\r\n" +
				"      <JFXComboBox fx:id=\"materialOption\" layoutX=\"20.0\" layoutY=\"60.0\" prefHeight=\"23.0\" prefWidth=\"227.0\" promptText=\"Material\" />\r\n" +
				"      <JFXComboBox fx:id=\"productOption\" layoutX=\"20.0\" layoutY=\"100.0\" prefHeight=\"23.0\" prefWidth=\"227.0\" promptText=\"Product\" />\r\n" +
				"      <JFXComboBox fx:id=\"locationOption\" layoutX=\"20.0\" layoutY=\"140.0\" prefHeight=\"23.0\" prefWidth=\"227.0\" promptText=\"Location\" />\r\n" +
				"		<Separator layoutX=\"5.0\" layoutY=\"190.0\" prefHeight=\"3.0\" prefWidth=\"385.0\" />\r\n" +
				"      <JFXButton fx:id=\"startButton\" layoutX=\"98.0\" layoutY=\"204.0\" prefHeight=\"30.0\" prefWidth=\"200.0\" text=\"Start\" />\r\n" +
				"      <JFXCheckBox fx:id=\"autoProgressionCheckbox\" layoutX=\"262.0\" layoutY=\"106.0\" text=\"Auto Progression\" />\r\n" +
				"	</children>\r\n" +
				"</Pane>\r\n" +
				"";
	}

	@Override
	protected String stylesheet() {
		return ".root {\r\n" + "  -fx-font-family: \"Roboto Mono\";\r\n" +
				"  -fx-focus-color: #3700B3;\r\n" +
				"}\r\n" +
				".rounded-pane {\r\n" +
				"  -fx-background-color: #ECEFF1;\r\n" +
				"  -fx-border-color: #DDDDDD;\r\n" +
				"  -fx-border-radius: 10;\r\n" +
				"  -fx-border-insets: 5;\r\n" +
				"  -fx-background-insets: 5;\r\n" +
				"  -fx-background-radius: 10;\r\n" +
				"  -fx-border-style: solid;\r\n" +
				"  -fx-border-width: 1;\r\n" +
				"  -fx-effect: dropshadow(three-pass-box, rgba(100, 100, 100, 0.5), 10, 0.5, 0, 0);\r\n" +
				"}\r\n" +
				".rounded-title-pane {\r\n" +
				"  -fx-background-color: #192ac4;\r\n" +
				"  -fx-background-insets: 5;\r\n" +
				"  -fx-background-radius: 5 5 0 0;\r\n" +
				"  -fx-pref-height: 50;\r\n" +
				"  -fx-fill: #FFFFFF;\r\n" +
				"}\r\n" +
				".rounded-title-pane .title {\r\n" +
				"  -fx-font-weight: bold;\r\n" +
				"  -fx-fill: #FFFFFF;\r\n" +
				"  -fx-font-size: 18px;\r\n" +
				"  -fx-text-alignment: center;\r\n" +
				"}\r\n" +
				".rounded-title-pane .version {\r\n" +
				"  -fx-fill: #FFFFFF;\r\n" +
				"  -fx-font-size: 12px;\r\n" +
				"}\r\n" +
				".titled-pane .content {\r\n" +
				"  -fx-background-color: #FFFFFF;\r\n" +
				"  -fx-border-color: #DDDDDD;\r\n" +
				"}\r\n" +
				".titled-pane > *.content {\r\n" +
				"  -fx-padding: 15;\r\n" +
				"}\r\n" +
				".titled-pane > *.content AnchorPane {\r\n" +
				"  -fx-padding: 0;\r\n" +
				"}\r\n" +
				".jfx-button {\r\n" +
				"  -fx-background-color: #192ac4;\r\n" +
				"  -fx-text-fill: #FFFFFF;\r\n" +
				"  -fx-font-size: 14px;\r\n" +
				"  -fx-padding: 5px;\r\n" +
				"  -jfx-button-type: RAISED;\r\n" +
				"  -fx-min-width: 100;\r\n" +
				"}\r\n" +
				".jfx-tab-pane {\r\n" +
				"  -fx-background-color: #FFFFFF;\r\n" +
				"  -fx-border-color: #DDDDDD;\r\n" +
				"  -fx-border-style: solid;\r\n" +
				"  -fx-border-width: 1;\r\n" +
				"  -fx-padding: 0;\r\n" +
				"}\r\n" +
				".jfx-tab-pane .tab {\r\n" +
				"  -fx-padding: 0;\r\n" +
				"}\r\n" +
				".jfx-tab-pane .tab:selected,\r\n" +
				".jfx-tab-pane .tab-header-area .tab-header-background {\r\n" +
				"  -fx-background-color: #192ac4;\r\n" +
				"  -fx-padding: 0;\r\n" +
				"}\r\n" +
				".jfx-tab-pane .tab-label {\r\n" +
				"  -fx-text-fill: #FFFFFF;\r\n" +
				"  -fx-font-weight: normal;\r\n" +
				"}\r\n" +
				".jfx-tab-pane .tab-header-area .tab-selected-line {\r\n" +
				"  -fx-background-color: #FFFFFF;\r\n" +
				"}\r\n" +
				".jfx-tab-pane .tab-content-area {\r\n" +
				"  -fx-padding: 15;\r\n" +
				"}\r\n" +
				".jfx-tab-pane .tab-content-area .titled-pane {\r\n" +
				"  -fx-padding: 0 15 15 0;\r\n" +
				"}\r\n" +
				".jfx-check-box {\r\n" +
				"  -jfx-checked-color: #3700B3;\r\n" +
				"}\r\n" +
				".input-focused-line {\r\n" +
				"  -fx-background-color: #3700B3;\r\n" +
				"}\r\n" +
				".jfx-slider .track,\r\n" +
				".jfx-slider .colored-track,\r\n" +
				".jfx-slider .thumb,\r\n" +
				".jfx-slider .animated-thumb {\r\n" +
				"  -fx-background-color: #3700B3;\r\n" +
				"}\r\n" +
				".jfx-list-view {\r\n" +
				"  -fx-border-color: #DDDDDD;\r\n" +
				"  -fx-background-insets: 0;\r\n" +
				"}\r\n" +
				".jfx-list-view .list-cell:even {\r\n" +
				"  -fx-background-color: #F9F9F9;\r\n" +
				"}\r\n" +
				".jfx-list-view .list-cell:odd {\r\n" +
				"  -fx-background-color: #EDEDED;\r\n" +
				"}\r\n" +
				".jfx-list-view .list-cell:empty {\r\n" +
				"  -fx-opacity: 0;\r\n" +
				"}\r\n" +
				".jfx-list-view .list-cell:filled:selected,\r\n" +
				".jfx-list-view .list-cell:filled:hover {\r\n" +
				"  -fx-background-color: #192ac4;\r\n" +
				"  -fx-text-fill: #FFFFFF;\r\n" +
				"}\r\n" +
				".jfx-list-view .list-cell .text-field {\r\n" +
				"  -fx-text-fill: -fx-text-inner-color;\r\n" +
				"  -fx-highlight-fill: derive(-fx-control-inner-background, -20%);\r\n" +
				"  -fx-highlight-text-fill: -fx-text-inner-color;\r\n" +
				"  -fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);\r\n" +
				"  -fx-background-color: #FFFFFF;\r\n" +
				"  -fx-background-insets: 1;\r\n" +
				"  -fx-background-radius: 0;\r\n" +
				"  -fx-cursor: text;\r\n" +
				"  -fx-padding: 0;\r\n" +
				"  -fx-font-size: 12px;\r\n" +
				"}\r\n" +
				"";
	}

	@Override
	protected boolean isDecorated() {
		return false;
	}

}
