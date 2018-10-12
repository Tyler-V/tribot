package scripts.usa.api.gui;

import javafx.fxml.Initializable;

public abstract class AbstractFxController implements Initializable {

	private FxApplication application;

	public void setFxApplication(FxApplication application) {
		this.application = application;
	}

	public FxApplication getFxApplication() {
		return this.application;
	}
}
