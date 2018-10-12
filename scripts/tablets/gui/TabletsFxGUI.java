package scripts.tablets.gui;

import scripts.usa.api.gui.AbstractFxGUI;

public class TabletsFxGUI extends AbstractFxGUI {

	@Override
	protected String fxml() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + "\r\n" +
				"<?import com.jfoenix.controls.JFXButton?>\r\n" +
				"<?import com.jfoenix.controls.JFXCheckBox?>\r\n" +
				"<?import com.jfoenix.controls.JFXComboBox?>\r\n" +
				"<?import com.jfoenix.controls.JFXTabPane?>\r\n" +
				"<?import com.jfoenix.controls.JFXTextField?>\r\n" +
				"<?import javafx.scene.control.Tab?>\r\n" +
				"<?import javafx.scene.layout.AnchorPane?>\r\n" +
				"<?import javafx.scene.layout.Pane?>\r\n" +
				"<?import javafx.scene.text.Text?>\r\n" +
				"\r\n" +
				"<Pane maxHeight=\"-Infinity\" maxWidth=\"-Infinity\" minHeight=\"-Infinity\" minWidth=\"-Infinity\" prefHeight=\"298.0\" prefWidth=\"378.0\" styleClass=\"rounded-pane\" stylesheets=\"@styles.css\" xmlns=\"http://javafx.com/javafx/8.0.171\" xmlns:fx=\"http://javafx.com/fxml/1\" fx:controller=\"scripts.tablets.gui.FxController\">\r\n" +
				"   <children>\r\n" +
				"      <Pane prefHeight=\"37.0\" prefWidth=\"378.0\" styleClass=\"rounded-title-pane\">\r\n" +
				"         <children>\r\n" +
				"            <Text fx:id=\"titleText\" layoutY=\"24.0\" styleClass=\"title\" text=\"USA Tablet Maker\" wrappingWidth=\"381.0000047222711\">\r\n" +
				"            </Text>\r\n" +
				"            <Text fx:id=\"versionText\" layoutX=\"321.0\" layoutY=\"32.0\" strokeType=\"OUTSIDE\" strokeWidth=\"0.0\" styleClass=\"version\" text=\"v1.0\" textAlignment=\"RIGHT\" wrappingWidth=\"40.0\" />\r\n" +
				"         </children>\r\n" +
				"      </Pane>\r\n" +
				"      <JFXTabPane layoutX=\"16.0\" layoutY=\"55.0\" prefHeight=\"186.0\" prefWidth=\"347.0\">\r\n" +
				"         <tabs>\r\n" +
				"            <Tab text=\"General\">\r\n" +
				"              <content>\r\n" +
				"                <AnchorPane minHeight=\"0.0\" minWidth=\"0.0\" prefHeight=\"180.0\" prefWidth=\"366.0\">\r\n" +
				"                     <children>\r\n" +
				"                        <JFXComboBox fx:id=\"lecternComboBox\" layoutX=\"14.0\" layoutY=\"20.0\" prefHeight=\"25.0\" prefWidth=\"318.0\" promptText=\"Lectern\" />\r\n" +
				"                        <JFXComboBox fx:id=\"tabletComboBox\" layoutX=\"14.0\" layoutY=\"61.0\" prefHeight=\"25.0\" prefWidth=\"318.0\" promptText=\"Tablet\" />\r\n" +
				"                        <JFXComboBox fx:id=\"houseComboBox\" layoutX=\"14.0\" layoutY=\"98.0\" prefHeight=\"25.0\" prefWidth=\"150.0\" promptText=\"House\" />\r\n" +
				"                        <JFXTextField fx:id=\"friendTextField\" layoutX=\"187.0\" layoutY=\"98.0\" prefHeight=\"25.0\" prefWidth=\"145.0\" promptText=\"Friend's Name\" />\r\n" +
				"                     </children>\r\n" +
				"                  </AnchorPane>\r\n" +
				"              </content>\r\n" +
				"            </Tab>\r\n" +
				"            <Tab text=\"Servant\">\r\n" +
				"              <content>\r\n" +
				"                <AnchorPane minHeight=\"0.0\" minWidth=\"0.0\" prefHeight=\"180.0\" prefWidth=\"200.0\">\r\n" +
				"                     <children>\r\n" +
				"                        <JFXCheckBox fx:id=\"servantCheckBox\" layoutX=\"15.0\" layoutY=\"15.0\" text=\"Use Servant\" />\r\n" +
				"                     </children>\r\n" +
				"                  </AnchorPane>\r\n" +
				"              </content>\r\n" +
				"            </Tab>\r\n" +
				"            <Tab text=\"Hosting\">\r\n" +
				"              <content>\r\n" +
				"                <AnchorPane minHeight=\"0.0\" minWidth=\"0.0\" prefHeight=\"180.0\" prefWidth=\"200.0\">\r\n" +
				"                     <children>\r\n" +
				"                        <JFXCheckBox fx:id=\"hostingCheckBox\" layoutX=\"15.0\" layoutY=\"15.0\" text=\"Remain Idle\" />\r\n" +
				"                     </children>\r\n" +
				"                  </AnchorPane>\r\n" +
				"              </content>\r\n" +
				"            </Tab>\r\n" +
				"            <Tab text=\"Antiban\">\r\n" +
				"              <content>\r\n" +
				"                <AnchorPane minHeight=\"0.0\" minWidth=\"0.0\" prefHeight=\"180.0\" prefWidth=\"200.0\">\r\n" +
				"                     <children>\r\n" +
				"                        <JFXCheckBox fx:id=\"antibanCheckBox\" layoutX=\"15.0\" layoutY=\"15.0\" selected=\"true\" text=\"Use Antiban\" />\r\n" +
				"                        <JFXCheckBox fx:id=\"reactionCheckBox\" layoutX=\"15.0\" layoutY=\"44.0\" selected=\"true\" text=\"Sleep Reaction Time\" />\r\n" +
				"                     </children>\r\n" +
				"                  </AnchorPane>\r\n" +
				"              </content>\r\n" +
				"            </Tab>\r\n" +
				"         </tabs>\r\n" +
				"      </JFXTabPane>\r\n" +
				"      <JFXButton fx:id=\"startButton\" buttonType=\"RAISED\" layoutX=\"90.0\" layoutY=\"251.0\" prefHeight=\"30.0\" prefWidth=\"200.0\" text=\"Start\" />\r\n" +
				"   </children>\r\n" +
				"</Pane>\r\n" +
				"";
	}

	@Override
	protected String stylesheet() {
		return ".root {\r\n" + "    -fx-font-family: \"Roboto Mono\";\r\n" +
				"}\r\n" +
				"\r\n" +
				".rounded-pane {\r\n" +
				"    -fx-background-color: #ECEFF1;\r\n" +
				"    -fx-border-color: #DDDDDD;\r\n" +
				"    -fx-border-radius: 5;\r\n" +
				"    -fx-border-insets: 5;\r\n" +
				"    -fx-background-insets: 5;\r\n" +
				"    -fx-background-radius: 10;\r\n" +
				"    -fx-border-style: solid;\r\n" +
				"    -fx-border-width: 1;\r\n" +
				"    -fx-effect: dropshadow(three-pass-box, rgba(100, 100, 100, .5), 10, 0.5, 0, 0);\r\n" +
				"}\r\n" +
				"\r\n" +
				".rounded-title-pane {\r\n" +
				"    -fx-background-color: #192ac4;\r\n" +
				"    -fx-background-insets: 5;\r\n" +
				"    -fx-background-radius: 5 5 0 0;\r\n" +
				"    -fx-pref-height: 50;\r\n" +
				"}\r\n" +
				"\r\n" +
				".title {\r\n" +
				"    -fx-fill: #FFFFFF;\r\n" +
				"    -fx-font-size: 20px;\r\n" +
				"    -fx-font-weight: bold;\r\n" +
				"    -fx-text-origin: center;\r\n" +
				"    -fx-text-alignment: center;\r\n" +
				"}\r\n" +
				"\r\n" +
				".version {\r\n" +
				"    -fx-fill: #FFFFFF;\r\n" +
				"    -fx-font-size: 12px;\r\n" +
				"}\r\n" +
				"\r\n" +
				".jfx-button {\r\n" +
				"    -fx-background-color: #192ac4;\r\n" +
				"    -fx-text-fill: #FFFFFF;\r\n" +
				"    -fx-font-size: 14px;\r\n" +
				"    -fx-padding: 5px;\r\n" +
				"    -jfx-button-type: RAISED;\r\n" +
				"    -fx-min-width: 100;\r\n" +
				"}\r\n" +
				"\r\n" +
				".jfx-tab-pane {\r\n" +
				"    -fx-background-color: white;\r\n" +
				"    -fx-border-color: #DDDDDD;\r\n" +
				"    -fx-border-style: solid;\r\n" +
				"    -fx-border-width: 1;\r\n" +
				"}\r\n" +
				"\r\n" +
				".jfx-tab-pane .tab:selected,\r\n" +
				".jfx-tab-pane .tab-header-area .tab-header-background {\r\n" +
				"    -fx-background-color: #192ac4;\r\n" +
				"}\r\n" +
				"\r\n" +
				".jfx-tab-pane .tab-label {\r\n" +
				"    -fx-text-fill: #FFFFFF;\r\n" +
				"    -fx-font-weight: normal;\r\n" +
				"}\r\n" +
				"\r\n" +
				".jfx-tab-pane .tab-header-area .tab-selected-line {\r\n" +
				"    -fx-background-color: #FFFFFF;\r\n" +
				"}\r\n" +
				"\r\n" +
				".jfx-check-box {\r\n" +
				"    -jfx-checked-color: #3700B3;\r\n" +
				"}\r\n" +
				"\r\n" +
				".input-focused-line {\r\n" +
				"    -fx-background-color: #3700B3;\r\n" +
				"}";
	}

	@Override
	protected boolean isDecorated() {
		return false;
	}

}
