package scripts.usa.api.gui;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringJoiner;

import javax.swing.SwingUtilities;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.tribot.api.General;
import org.tribot.util.Util;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import scripts.usa.api.condition.Condition;
import scripts.usa.api.framework.task.ScriptVars;

public class FxApplication extends Application {

	private AbstractFxGUI fxGUI;
	private AbstractFxController controller;

	private Stage stage;
	private Parent parent;
	private Scene scene;
	private double sceneX;
	private double sceneY;

	public FxApplication(AbstractFxGUI fxGUI, AbstractFxController controller) {
		this.fxGUI = fxGUI;
		this.controller = controller;
		start();
	}

	private boolean isLocal() {
		return ScriptVars.get().getTaskScript().getRepoID() == -1;
	}

	private String getScriptPackage() {
		String[] split = fxGUI.getClass().getName().split("\\.");
		StringJoiner joiner = new StringJoiner("/");
		return joiner.add(split[0]).add(split[1]).toString();
	}

	private String getPackageDirectory(String packagePath) {
		return FilenameUtils.separatorsToSystem(Util.getWorkingDirectory().getAbsolutePath() + "/src/" + packagePath);
	}

	private String getFXMLFile(String directory) {
		return getFileNameWithExtension(directory, ".fxml").toString();
	}

	private Path getFileNameWithExtension(String directory, String extension) {
		try {
			return Files.walk(Paths.get(directory)).filter(file -> file.toString().endsWith(extension)).findFirst().get().getFileName();
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String getScriptDirectory() {
		return FilenameUtils.separatorsToSystem(Util.getWorkingDirectory().getAbsolutePath() + "/" + ScriptVars.get().getScriptManifest().name());
	}

	private void start() {
		SwingUtilities.invokeLater(() -> {
			new JFXPanel();
			Platform.runLater(() -> {
				try {
					start(this.stage = new Stage());
				}
				catch (Exception e) {
					System.out.println("FXApplication: " + e);
				}
			});
		});
	}

	@Override
	public void start(Stage stage) throws Exception {
		Platform.setImplicitExit(false);
		FXMLLoader loader;

		if (isLocal()) {
			System.out.println("Loading fxml file...");
			URL fxml = fxGUI.getClass().getResource(getFXMLFile(getPackageDirectory(getScriptPackage())));
			if (fxml == null) {
				System.out.println("No .fxml found!");
				return;
			}
			loader = new FXMLLoader(fxml);
			loader.setClassLoader(getClass().getClassLoader());
			parent = loader.load();
			scene = new Scene(parent);
		}
		else {
			System.out.println("Loading fxml string...");
			if (fxGUI.getFXML() == null) {
				System.out.println("No fxml string found!");
				return;
			}
			loader = new FXMLLoader();
			loader.setClassLoader(getClass().getClassLoader());
			loader.setController(controller);
			parent = loader.load(new ByteArrayInputStream(fxGUI.getFXML().getBytes()));
			scene = new Scene(parent);

			if (fxGUI.stylesheet() == null) {
				System.out.println("No stylesheet string found!");
			}
			else {
				File file = new File(getScriptDirectory() + "/stylesheet.css");
				System.out.println("Loading stylesheet from " + file.getPath());
				FileUtils.writeStringToFile(file, fxGUI.stylesheet(), Charset.forName("UTF-8"));
				Condition.wait(() -> file.exists());
				scene.getStylesheets().add(file.toURI().toString());
				file.deleteOnExit();
			}
		}

		if (loader.getController() == null) {
			System.out.println("No controller!");
		}
		else {
			controller = (AbstractFxController) loader.getController();
			controller.setFxApplication(this);
		}

		if (!fxGUI.isDecorated()) {
			stage.initStyle(StageStyle.TRANSPARENT);
			scene.setFill(Color.TRANSPARENT);
		}

		stage.setScene(scene);
		stage.setAlwaysOnTop(true);

		setEvents();
	}

	private void setEvents() {
		scene.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				sceneX = event.getSceneX();
				sceneY = event.getSceneY();
			}
		});

		scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				scene.getWindow().setX(event.getScreenX() - sceneX);
				scene.getWindow().setY(event.getScreenY() - sceneY);
			}
		});
	}

	public Stage getStage() {
		return this.stage;
	}

	public Parent getParent() {
		return this.parent;
	}

	public Scene getScene() {
		return this.scene;
	}

	public void show() {
		Condition.wait(() -> stage != null);
		if (!Platform.isFxApplicationThread()) {
			Platform.runLater(() -> {
				stage.show();
				centerWindow();
			});
		}
		else {
			stage.show();
			centerWindow();
		}
		Condition.wait(() -> isShowing());
	}

	public void centerWindow() {
		if (isShowing()) {
			Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
			stage.setX((bounds.getWidth() - stage.getWidth()) / 2);
			stage.setY((bounds.getHeight() - stage.getHeight()) / 2);
		}
	}

	public void close() {
		Platform.runLater(() -> stage.close());
	}

	public void minimize() {
		if (isShowing()) {
			if (!Platform.isFxApplicationThread()) {
				Platform.runLater(() -> stage.setIconified(true));
			}
			else {
				stage.setIconified(true);
			}
		}
	}

	public boolean isShowing() {
		return stage != null && stage.isShowing();
	}
}
