package scripts.usa.api.gui;

public abstract class AbstractFxGUI {

	protected abstract String fxml();

	protected abstract String stylesheet();

	protected abstract boolean isDecorated();

	public String getFXML() {
		return fxml().replaceAll("\\sfx:controller=\".*\"", "").replaceAll("\\sstylesheets=\"@.*.css\"", "");
	}
}
