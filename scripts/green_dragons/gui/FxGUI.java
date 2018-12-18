package scripts.green_dragons.gui;

import scripts.usa.api.gui.AbstractFxGUI;

public class FxGUI extends AbstractFxGUI {

	@Override
	protected String fxml() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + "\r\n" +
				"<?import com.jfoenix.controls.JFXButton?>\r\n" +
				"<?import com.jfoenix.controls.JFXCheckBox?>\r\n" +
				"<?import com.jfoenix.controls.JFXComboBox?>\r\n" +
				"<?import com.jfoenix.controls.JFXListView?>\r\n" +
				"<?import com.jfoenix.controls.JFXSlider?>\r\n" +
				"<?import com.jfoenix.controls.JFXTabPane?>\r\n" +
				"<?import com.jfoenix.controls.JFXTextField?>\r\n" +
				"<?import javafx.scene.control.Label?>\r\n" +
				"<?import javafx.scene.control.Spinner?>\r\n" +
				"<?import javafx.scene.control.Tab?>\r\n" +
				"<?import javafx.scene.control.TitledPane?>\r\n" +
				"<?import javafx.scene.image.ImageView?>\r\n" +
				"<?import javafx.scene.layout.AnchorPane?>\r\n" +
				"<?import javafx.scene.layout.Pane?>\r\n" +
				"\r\n" +
				"<Pane maxHeight=\"-Infinity\" maxWidth=\"-Infinity\" minHeight=\"-Infinity\" minWidth=\"-Infinity\" prefHeight=\"502.0\" prefWidth=\"546.0\" styleClass=\"rounded-pane\" stylesheets=\"@styles.css\" xmlns=\"http://javafx.com/javafx/8.0.171\" xmlns:fx=\"http://javafx.com/fxml/1\" fx:controller=\"scripts.green_dragons.gui.FxController\">\r\n" +
				"   <children>\r\n" +
				"      <Pane prefHeight=\"56.0\" prefWidth=\"546.0\" styleClass=\"rounded-title-pane\">\r\n" +
				"         <children>\r\n" +
				"            <Label layoutX=\"4.0\" layoutY=\"4.0\" prefHeight=\"42.0\" prefWidth=\"537.0\" styleClass=\"title\" text=\"USA Green Dragons\" />\r\n" +
				"            <Label fx:id=\"versionLabel\" layoutX=\"491.0\" layoutY=\"4.0\" prefHeight=\"42.0\" prefWidth=\"35.0\" styleClass=\"version\" text=\"v1.0\" />\r\n" +
				"         </children>\r\n" +
				"      </Pane>\r\n" +
				"      <JFXTabPane layoutX=\"10.0\" layoutY=\"50.0\" prefHeight=\"392.0\" prefWidth=\"526.0\">\r\n" +
				"         <tabs>\r\n" +
				"            <Tab text=\"General\">\r\n" +
				"              <content>\r\n" +
				"                <AnchorPane minHeight=\"0.0\" minWidth=\"0.0\" prefHeight=\"180.0\" prefWidth=\"624.0\">\r\n" +
				"                     <children>\r\n" +
				"                        <TitledPane animated=\"false\" collapsible=\"false\" prefHeight=\"96.0\" prefWidth=\"279.0\" snapToPixel=\"false\" text=\"Green Dragons\">\r\n" +
				"                           <content>\r\n" +
				"                              <AnchorPane prefHeight=\"42.0\" prefWidth=\"316.0\">\r\n" +
				"                                 <children>\r\n" +
				"                                    <JFXComboBox fx:id=\"locationComboBox\" prefHeight=\"25.0\" prefWidth=\"244.0\" promptText=\"Location\" />\r\n" +
				"                                 </children>\r\n" +
				"                              </AnchorPane>\r\n" +
				"                           </content>\r\n" +
				"                        </TitledPane>\r\n" +
				"                        <TitledPane animated=\"false\" collapsible=\"false\" layoutY=\"96.0\" prefHeight=\"115.0\" prefWidth=\"291.0\" snapToPixel=\"false\" text=\"World Hopping\">\r\n" +
				"                           <content>\r\n" +
				"                              <AnchorPane prefHeight=\"42.0\" prefWidth=\"316.0\">\r\n" +
				"                                 <children>\r\n" +
				"                                    <Label layoutY=\"2.0\" text=\"Players in area exceed\" />\r\n" +
				"                                    <Spinner fx:id=\"maxPlayersSpinner\" layoutX=\"127.0\" prefHeight=\"25.0\" prefWidth=\"117.0\" />\r\n" +
				"                                    <JFXCheckBox fx:id=\"dwarfCannonCheckBox\" layoutY=\"38.0\" text=\"Dwarf cannon\" />\r\n" +
				"                                 </children>\r\n" +
				"                              </AnchorPane>\r\n" +
				"                           </content>\r\n" +
				"                        </TitledPane>\r\n" +
				"                     </children>\r\n" +
				"                  </AnchorPane>\r\n" +
				"              </content>\r\n" +
				"            </Tab>\r\n" +
				"            <Tab text=\"Consumables\">\r\n" +
				"              <content>\r\n" +
				"                <AnchorPane minHeight=\"0.0\" minWidth=\"0.0\" prefHeight=\"180.0\" prefWidth=\"551.0\">\r\n" +
				"                     <children>\r\n" +
				"                        <TitledPane animated=\"false\" collapsible=\"false\" prefHeight=\"158.0\" prefWidth=\"186.0\" snapToPixel=\"false\" text=\"Food\">\r\n" +
				"                           <content>\r\n" +
				"                              <AnchorPane prefHeight=\"42.0\" prefWidth=\"218.0\">\r\n" +
				"                                 <children>\r\n" +
				"                                    <JFXTextField fx:id=\"foodTextField\" prefHeight=\"25.0\" prefWidth=\"135.0\" promptText=\"Name\" />\r\n" +
				"                                    <Spinner fx:id=\"foodSpinner\" editable=\"true\" layoutX=\"147.0\" layoutY=\"2.0\" prefHeight=\"25.0\" prefWidth=\"60.0\" />\r\n" +
				"                                    <JFXSlider fx:id=\"eatSlider\" layoutX=\"40.0\" layoutY=\"49.0\" majorTickUnit=\"10.0\" min=\"50.0\" minorTickCount=\"5\" prefHeight=\"38.0\" prefWidth=\"167.0\" showTickLabels=\"true\" showTickMarks=\"true\" value=\"75.0\" />\r\n" +
				"                                    <Label layoutY=\"48.0\" text=\"Eat %\" />\r\n" +
				"                                 </children>\r\n" +
				"                              </AnchorPane>\r\n" +
				"                           </content>\r\n" +
				"                        </TitledPane>\r\n" +
				"                        <TitledPane animated=\"false\" collapsible=\"false\" layoutX=\"254.0\" prefHeight=\"196.0\" prefWidth=\"255.0\" snapToPixel=\"false\" text=\"Potions\">\r\n" +
				"                           <content>\r\n" +
				"                              <AnchorPane prefHeight=\"62.0\" prefWidth=\"285.0\">\r\n" +
				"                                 <children>\r\n" +
				"                                    <JFXComboBox fx:id=\"potion1ComboBox\" prefHeight=\"25.0\" prefWidth=\"208.0\" promptText=\"Potion\" />\r\n" +
				"                                    <JFXComboBox fx:id=\"potion2ComboBox\" layoutY=\"40.0\" prefHeight=\"25.0\" prefWidth=\"208.0\" promptText=\"Potion\" />\r\n" +
				"                                    <JFXComboBox fx:id=\"potion3ComboBox\" layoutY=\"80.0\" prefHeight=\"25.0\" prefWidth=\"208.0\" promptText=\"Potion\" />\r\n" +
				"                                    <JFXButton fx:id=\"potionsResetButton\" layoutX=\"54.0\" layoutY=\"122.0\" text=\"Reset\" />\r\n" +
				"                                 </children>\r\n" +
				"                              </AnchorPane>\r\n" +
				"                           </content>\r\n" +
				"                        </TitledPane>\r\n" +
				"                     </children>\r\n" +
				"                  </AnchorPane>\r\n" +
				"              </content>\r\n" +
				"            </Tab>\r\n" +
				"            <Tab text=\"Equipment\">\r\n" +
				"              <content>\r\n" +
				"                <AnchorPane minHeight=\"0.0\" minWidth=\"0.0\" prefHeight=\"180.0\" prefWidth=\"200.0\">\r\n" +
				"                     <children>\r\n" +
				"                        <TitledPane animated=\"false\" collapsible=\"false\" prefHeight=\"296.0\" prefWidth=\"185.0\" snapToPixel=\"false\" text=\"Equipment\">\r\n" +
				"                           <content>\r\n" +
				"                              <AnchorPane prefHeight=\"258.0\" prefWidth=\"183.0\">\r\n" +
				"                                 <children>\r\n" +
				"                                    <Pane fx:id=\"equipmentSlots\" prefHeight=\"194.0\" prefWidth=\"148.0\">\r\n" +
				"                                       <children>\r\n" +
				"                                          <ImageView fx:id=\"equipmentSlotsImageView\" fitHeight=\"194.0\" fitWidth=\"148.0\" pickOnBounds=\"true\" preserveRatio=\"true\" />\r\n" +
				"                                          <ImageView fx:id=\"equipmentEmptyImageView\" fitHeight=\"194.0\" fitWidth=\"148.0\" pickOnBounds=\"true\" preserveRatio=\"true\" visible=\"false\" />\r\n" +
				"                                       </children>\r\n" +
				"                                    </Pane>\r\n" +
				"                                    <Pane fx:id=\"equipmentItems\" prefHeight=\"194.0\" prefWidth=\"148.0\">\r\n" +
				"                                       <children>\r\n" +
				"                                          <ImageView fx:id=\"helmetImageView\" fitHeight=\"32.0\" fitWidth=\"36.0\" layoutX=\"58.0\" layoutY=\"2.0\" pickOnBounds=\"true\" preserveRatio=\"true\" />\r\n" +
				"                                          <ImageView fx:id=\"amuletImageView\" fitHeight=\"32.0\" fitWidth=\"36.0\" layoutX=\"58.0\" layoutY=\"41.0\" pickOnBounds=\"true\" preserveRatio=\"true\" />\r\n" +
				"                                          <ImageView fx:id=\"bodyImageView\" fitHeight=\"32.0\" fitWidth=\"36.0\" layoutX=\"58.0\" layoutY=\"80.0\" pickOnBounds=\"true\" preserveRatio=\"true\" />\r\n" +
				"                                          <ImageView fx:id=\"legsImageView\" fitHeight=\"32.0\" fitWidth=\"36.0\" layoutX=\"58.0\" layoutY=\"120.0\" pickOnBounds=\"true\" preserveRatio=\"true\" />\r\n" +
				"                                          <ImageView fx:id=\"bootsImageView\" fitHeight=\"32.0\" fitWidth=\"36.0\" layoutX=\"58.0\" layoutY=\"160.0\" pickOnBounds=\"true\" preserveRatio=\"true\" />\r\n" +
				"                                          <ImageView fx:id=\"capeImageView\" fitHeight=\"32.0\" fitWidth=\"36.0\" layoutX=\"16.0\" layoutY=\"41.0\" pickOnBounds=\"true\" preserveRatio=\"true\" />\r\n" +
				"                                          <ImageView fx:id=\"weaponImageView\" fitHeight=\"32.0\" fitWidth=\"36.0\" layoutX=\"2.0\" layoutY=\"80.0\" pickOnBounds=\"true\" preserveRatio=\"true\" />\r\n" +
				"                                          <ImageView fx:id=\"glovesImageView\" fitHeight=\"32.0\" fitWidth=\"36.0\" layoutX=\"2.0\" layoutY=\"160.0\" pickOnBounds=\"true\" preserveRatio=\"true\" />\r\n" +
				"                                          <ImageView fx:id=\"shieldImageView\" fitHeight=\"32.0\" fitWidth=\"36.0\" layoutX=\"114.0\" layoutY=\"80.0\" pickOnBounds=\"true\" preserveRatio=\"true\" />\r\n" +
				"                                          <ImageView fx:id=\"ringImageView\" fitHeight=\"32.0\" fitWidth=\"36.0\" layoutX=\"114.0\" layoutY=\"160.0\" pickOnBounds=\"true\" preserveRatio=\"true\" />\r\n" +
				"                                          <ImageView fx:id=\"arrowImageView\" fitHeight=\"32.0\" fitWidth=\"36.0\" layoutX=\"99.0\" layoutY=\"41.0\" pickOnBounds=\"true\" preserveRatio=\"true\" />\r\n" +
				"                                       </children>\r\n" +
				"                                    </Pane>\r\n" +
				"                                    <JFXButton fx:id=\"refreshEquipmentButton\" layoutX=\"24.0\" layoutY=\"207.0\" text=\"Refresh\" />\r\n" +
				"                                 </children>\r\n" +
				"                              </AnchorPane>\r\n" +
				"                           </content>\r\n" +
				"                        </TitledPane>\r\n" +
				"                        <TitledPane animated=\"false\" collapsible=\"false\" layoutX=\"195.0\" prefHeight=\"145.0\" prefWidth=\"234.0\" snapToPixel=\"false\" text=\"Special Attack\">\r\n" +
				"                           <content>\r\n" +
				"                              <AnchorPane prefHeight=\"74.0\" prefWidth=\"165.0\">\r\n" +
				"                                 <children>\r\n" +
				"                                    <JFXCheckBox fx:id=\"specialAttackCheckBox\" prefHeight=\"18.0\" prefWidth=\"168.0\" text=\"Use Special Attack\" />\r\n" +
				"                                    <JFXSlider fx:id=\"specialAttackSlider\" blockIncrement=\"25.0\" layoutY=\"38.0\" min=\"25.0\" minorTickCount=\"5\" prefHeight=\"38.0\" prefWidth=\"187.0\" showTickLabels=\"true\" showTickMarks=\"true\" snapToTicks=\"true\" value=\"50.0\" />\r\n" +
				"                                 </children>\r\n" +
				"                              </AnchorPane>\r\n" +
				"                           </content>\r\n" +
				"                        </TitledPane>\r\n" +
				"                        <TitledPane animated=\"false\" collapsible=\"false\" layoutX=\"195.0\" layoutY=\"147.0\" prefHeight=\"141.0\" prefWidth=\"200.0\" snapToPixel=\"false\" text=\"Ammunition\">\r\n" +
				"                           <content>\r\n" +
				"                              <AnchorPane prefHeight=\"178.0\" prefWidth=\"183.0\">\r\n" +
				"                                 <children>\r\n" +
				"                                    <JFXTextField fx:id=\"ammunitionTextField\" editable=\"false\" prefHeight=\"25.0\" prefWidth=\"185.0\" promptText=\"Ammunition Name\" />\r\n" +
				"                                    <Spinner fx:id=\"ammunitionPerTripSpinner\" editable=\"true\" layoutX=\"104.0\" layoutY=\"45.0\" prefHeight=\"25.0\" prefWidth=\"83.0\" />\r\n" +
				"                                    <Label layoutY=\"47.0\" text=\"Amount Per Trip\" />\r\n" +
				"                                 </children>\r\n" +
				"                              </AnchorPane>\r\n" +
				"                           </content>\r\n" +
				"                        </TitledPane>\r\n" +
				"                     </children>\r\n" +
				"                  </AnchorPane>\r\n" +
				"              </content>\r\n" +
				"            </Tab>\r\n" +
				"            <Tab text=\"Looting\">\r\n" +
				"              <content>\r\n" +
				"                <AnchorPane minHeight=\"0.0\" minWidth=\"0.0\" prefHeight=\"180.0\" prefWidth=\"200.0\">\r\n" +
				"                     <children>\r\n" +
				"                        <TitledPane animated=\"false\" collapsible=\"false\" prefHeight=\"131.0\" prefWidth=\"281.0\" snapToPixel=\"false\" text=\"Loot Value\">\r\n" +
				"                           <content>\r\n" +
				"                              <AnchorPane prefHeight=\"129.0\" prefWidth=\"356.0\">\r\n" +
				"                                 <children>\r\n" +
				"                                    <Label layoutY=\"2.0\" prefWidth=\"34.0\" text=\"Above\" />\r\n" +
				"                                    <Spinner fx:id=\"minLootValueSpinner\" editable=\"true\" layoutX=\"60.0\" prefHeight=\"25.0\" prefWidth=\"174.0\" />\r\n" +
				"                                    <Label layoutY=\"37.0\" text=\"Below\" />\r\n" +
				"                                    <Spinner fx:id=\"maxLootValueSpinner\" editable=\"true\" layoutX=\"60.0\" layoutY=\"35.0\" prefHeight=\"25.0\" prefWidth=\"174.0\" />\r\n" +
				"                                 </children>\r\n" +
				"                              </AnchorPane>\r\n" +
				"                           </content>\r\n" +
				"                        </TitledPane>\r\n" +
				"                        <TitledPane animated=\"false\" collapsible=\"false\" layoutY=\"131.0\" prefHeight=\"127.0\" prefWidth=\"281.0\" snapToPixel=\"false\" text=\"Maximum Loot Distance\">\r\n" +
				"                           <content>\r\n" +
				"                              <AnchorPane prefHeight=\"129.0\" prefWidth=\"266.0\">\r\n" +
				"                                 <children>\r\n" +
				"                                    <JFXSlider fx:id=\"maxLootDistanceSlider\" indicatorPosition=\"RIGHT\" majorTickUnit=\"10.0\" max=\"50.0\" minorTickCount=\"5\" prefHeight=\"14.0\" prefWidth=\"233.0\" showTickLabels=\"true\" showTickMarks=\"true\" value=\"10.0\" />\r\n" +
				"                                 </children>\r\n" +
				"                              </AnchorPane>\r\n" +
				"                           </content>\r\n" +
				"                        </TitledPane>\r\n" +
				"                        <TitledPane animated=\"false\" collapsible=\"false\" layoutX=\"281.0\" prefHeight=\"131.0\" prefWidth=\"228.0\" snapToPixel=\"false\" text=\"Ranging\">\r\n" +
				"                           <content>\r\n" +
				"                              <AnchorPane prefHeight=\"178.0\" prefWidth=\"183.0\">\r\n" +
				"                                 <children>\r\n" +
				"                                    <JFXCheckBox fx:id=\"lootAmmunitionCheckBox\" prefHeight=\"18.0\" prefWidth=\"168.0\" text=\"Loot Ammunition\" />\r\n" +
				"                                    <Spinner fx:id=\"lootAmmunitionStackSpinner\" editable=\"true\" layoutX=\"96.0\" layoutY=\"35.0\" prefHeight=\"25.0\" prefWidth=\"85.0\" />\r\n" +
				"                                    <Label layoutY=\"39.0\" text=\"Minimum Stack\" />\r\n" +
				"                                 </children>\r\n" +
				"                              </AnchorPane>\r\n" +
				"                           </content>\r\n" +
				"                        </TitledPane>\r\n" +
				"                     </children></AnchorPane>\r\n" +
				"              </content>\r\n" +
				"            </Tab>\r\n" +
				"            <Tab text=\"Evading\">\r\n" +
				"              <content>\r\n" +
				"                <AnchorPane minHeight=\"0.0\" minWidth=\"0.0\" prefHeight=\"180.0\" prefWidth=\"518.0\">\r\n" +
				"                     <children>\r\n" +
				"                        <TitledPane animated=\"false\" collapsible=\"false\" prefHeight=\"338.0\" prefWidth=\"247.0\" snapToPixel=\"false\" text=\"Enemy Equipment\">\r\n" +
				"                           <content>\r\n" +
				"                              <AnchorPane prefHeight=\"129.0\" prefWidth=\"356.0\">\r\n" +
				"                                 <children>\r\n" +
				"                                    <JFXListView fx:id=\"equipmentListView\" editable=\"true\" prefHeight=\"267.0\" prefWidth=\"200.0\" />\r\n" +
				"                                 </children>\r\n" +
				"                              </AnchorPane>\r\n" +
				"                           </content>\r\n" +
				"                        </TitledPane>\r\n" +
				"                        <TitledPane animated=\"false\" collapsible=\"false\" layoutX=\"247.0\" prefHeight=\"134.0\" prefWidth=\"262.0\" snapToPixel=\"false\" text=\"Evade\">\r\n" +
				"                           <content>\r\n" +
				"                              <AnchorPane prefHeight=\"80.0\" prefWidth=\"200.0\">\r\n" +
				"                                 <children>\r\n" +
				"                                    <JFXCheckBox fx:id=\"quickPrayerCheckBox\" layoutY=\"45.0\" text=\"Activate Quick Prayers\" />\r\n" +
				"                                    <JFXComboBox fx:id=\"evadeComboBox\" prefHeight=\"25.0\" prefWidth=\"215.0\" promptText=\"Evade when...\" />\r\n" +
				"                                 </children>\r\n" +
				"                              </AnchorPane>\r\n" +
				"                           </content>\r\n" +
				"                        </TitledPane>\r\n" +
				"                     </children>\r\n" +
				"                  </AnchorPane>\r\n" +
				"              </content>\r\n" +
				"            </Tab>\r\n" +
				"            <Tab text=\"Antiban\">\r\n" +
				"              <content>\r\n" +
				"                <AnchorPane minHeight=\"0.0\" minWidth=\"0.0\" prefHeight=\"180.0\" prefWidth=\"200.0\">\r\n" +
				"                     <children>\r\n" +
				"                        <TitledPane animated=\"false\" collapsible=\"false\" prefHeight=\"94.0\" prefWidth=\"279.0\" snapToPixel=\"false\" text=\"ABCL\">\r\n" +
				"                           <content>\r\n" +
				"                              <AnchorPane prefHeight=\"42.0\" prefWidth=\"316.0\">\r\n" +
				"                                 <children>\r\n" +
				"                                    <JFXCheckBox fx:id=\"antibanCheckbox\" selected=\"true\" text=\"Use Antiban\" />\r\n" +
				"                                    <JFXCheckBox fx:id=\"reactionTimingCheckbox\" layoutY=\"32.0\" text=\"Use Reaction Timing\" />\r\n" +
				"                                 </children>\r\n" +
				"                              </AnchorPane>\r\n" +
				"                           </content>\r\n" +
				"                        </TitledPane>\r\n" +
				"                     </children></AnchorPane>\r\n" +
				"              </content>\r\n" +
				"            </Tab>\r\n" +
				"         </tabs>\r\n" +
				"      </JFXTabPane>\r\n" +
				"      <JFXButton fx:id=\"startButton\" buttonType=\"RAISED\" layoutX=\"173.0\" layoutY=\"453.0\" prefHeight=\"30.0\" prefWidth=\"200.0\" text=\"Start\" />\r\n" +
				"   </children>\r\n" +
				"</Pane>\r\n" +
				"";
	}

	@Override
	protected String stylesheet() {
		return ".root {\r\n" + "  -fx-font-family: \"Roboto Mono\";\r\n" +
				"  -fx-focus-color: #198e33;\r\n" +
				"}\r\n" +
				".rounded-pane {\r\n" +
				"  -fx-background-color: #ECEFF1;\r\n" +
				"  -fx-border-color: #DDDDDD;\r\n" +
				"  -fx-border-radius: 5;\r\n" +
				"  -fx-border-insets: 5;\r\n" +
				"  -fx-background-insets: 5;\r\n" +
				"  -fx-background-radius: 10;\r\n" +
				"  -fx-border-style: solid;\r\n" +
				"  -fx-border-width: 1;\r\n" +
				"  -fx-effect: dropshadow(three-pass-box, rgba(100, 100, 100, 0.5), 10, 0.5, 0, 0);\r\n" +
				"}\r\n" +
				".rounded-title-pane {\r\n" +
				"  -fx-background-color: #036b1a;\r\n" +
				"  -fx-background-insets: 5;\r\n" +
				"  -fx-background-radius: 5 5 0 0;\r\n" +
				"  -fx-pref-height: 50;\r\n" +
				"}\r\n" +
				".rounded-title-pane .title {\r\n" +
				"  -fx-font-weight: bold;\r\n" +
				"  -fx-text-fill: #FFFFFF;\r\n" +
				"  -fx-font-size: 18px;\r\n" +
				"  -fx-alignment: center;\r\n" +
				"}\r\n" +
				".rounded-title-pane .version {\r\n" +
				"  -fx-font-weight: bold;\r\n" +
				"  -fx-text-fill: #FFFFFF;\r\n" +
				"  -fx-font-size: 11px;\r\n" +
				"  -fx-alignment: center_right;\r\n" +
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
				"  -fx-background-color: #036b1a;\r\n" +
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
				"  -fx-background-color: #036b1a;\r\n" +
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
				"  -jfx-checked-color: #198e33;\r\n" +
				"}\r\n" +
				".input-focused-line {\r\n" +
				"  -fx-background-color: #198e33;\r\n" +
				"}\r\n" +
				".jfx-slider .track,\r\n" +
				".jfx-slider .colored-track,\r\n" +
				".jfx-slider .thumb,\r\n" +
				".jfx-slider .animated-thumb {\r\n" +
				"  -fx-background-color: #198e33;\r\n" +
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
				"  -fx-background-color: #036b1a;\r\n" +
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
