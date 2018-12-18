package scripts.bot_tools.data;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scripts.bot_tools.models.GEItem;

public class BotToolsSettings {

	private ListProperty<GEItem> buyingItems = new SimpleListProperty<>(FXCollections.observableArrayList());
	private ListProperty<GEItem> sellingItems = new SimpleListProperty<>(FXCollections.observableArrayList());

	public ObservableList<GEItem> getBuyingItems() {
		return buyingItems.get();
	}

	public ListProperty<GEItem> getBuyingItemsProperty() {
		return buyingItems;
	}

	public ObservableList<GEItem> getSellingItems() {
		return sellingItems.get();
	}

	public ListProperty<GEItem> getSellingItemsProperty() {
		return sellingItems;
	}
}
