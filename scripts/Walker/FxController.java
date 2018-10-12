package scripts.Walker;

import java.awt.Point;
import java.net.URL;
import java.util.ResourceBundle;

import com.allatori.annotations.DoNotRename;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class FxController implements Initializable {

    public Point pointClicked;
    private double xOffset;
    private double yOffset;
    private boolean mousePressed;
    private boolean dragging;

    @FXML
    @DoNotRename
    public Pane pane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
	System.out.println("JavaFX Initialized");
	Image image = new Image("http://i.imgur.com/3fcc1WN.png");
	ImageView iv = new ImageView(image);
	pane.getChildren().add(iv);
    }

    @FXML
    @DoNotRename
    private void onMouseClicked(MouseEvent event) {
	if (!dragging) {
	    this.pointClicked = new Point((int) event.getX(), (int) event.getY());
	    Stage stage = (Stage) pane.getScene().getWindow();
	    stage.close();
	}
	dragging = false;
	mousePressed = false;
    }

    @FXML
    @DoNotRename
    private void onMousePressed(MouseEvent event) {
	xOffset = event.getSceneX();
	yOffset = event.getSceneY();
	mousePressed = true;
    }

    @FXML
    @DoNotRename
    private void onMouseDragged(MouseEvent event) {
	if (mousePressed) {
	    pane.getScene().getWindow().setX(event.getScreenX() - xOffset);
	    pane.getScene().getWindow().setY(event.getScreenY() - yOffset);
	    dragging = true;
	}
    }
}