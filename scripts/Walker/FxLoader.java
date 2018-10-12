package scripts.Walker;

import java.net.URL;

import javax.swing.SwingUtilities;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class FxLoader extends Application {

    private URL fxml;
    private URL css;

    private FxController controller;
    private Parent root;
    private Scene scene;
    private Stage stage;

    public FxLoader(Class<?> script, URL fxml, URL css) {
	this(script, fxml, css, false);
    }

    public FxLoader(Class<?> script, URL fxml, URL css, boolean local) {
	if (local) {
	    this.fxml = script.getResource(fxml.toString().split("/")[fxml.toString().split("/").length - 1]);
	    if (css != null)
		this.css = script.getResource(css.toString().split("/")[css.toString().split("/").length - 1]);
	} else {
	    this.fxml = fxml;
	    if (css != null)
		this.css = css;
	}

	SwingUtilities.invokeLater(() -> {
	    new JFXPanel();
	    Platform.runLater(() -> {
		try {
		    start(new Stage());
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    });
	});

	waitForInit();
    }

    private void waitForInit() {
	Timing.waitCondition(new Condition() {
	    @Override
	    public boolean active() {
		General.sleep(100);
		return getStage() != null;
	    }
	}, 5000);
    }

    @Override
    public void start(Stage stage) throws Exception {
	try {
	    Platform.setImplicitExit(false);
	    FXMLLoader loader = new FXMLLoader(fxml);
	    loader.setClassLoader(this.getClass().getClassLoader());
	    FxController controller = new FxController();
	    loader.setController(controller);
	    Parent root = loader.load();

	    Scene scene = new Scene(root);
	    scene.setFill(Color.TRANSPARENT);
	    if (this.css != null)
		scene.getStylesheets().add(this.css.toExternalForm());

	    stage.initStyle(StageStyle.TRANSPARENT);
	    stage.setAlwaysOnTop(false);
	    stage.setScene(scene);

	    stage.show();

	    this.controller = loader.getController();
	    this.root = root;
	    this.scene = scene;
	    this.stage = stage;
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public FxController getController() {
	return this.controller;
    }

    public Parent getRoot() {
	return this.root;
    }

    public Scene getScene() {
	return this.scene;
    }

    public Stage getStage() {
	return this.stage;
    }

    public boolean isShowing() {
	if (stage == null)
	    return false;
	return this.getStage().isShowing();
    }

    public void close() {
	if (stage == null)
	    return;
	Platform.runLater(() -> stage.close());
    }
}
