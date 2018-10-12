package scripts.usa.api.web.items.osrs;

public class OSRSItem {

	private int id;
	private String name;
	private String description;
	private String icon;
	private String icon_large;
	private String type;
	private String typeIcon;
	private ItemPrice current;
	private ItemTrend today;
	private boolean members;
	private ItemTrend day30;
	private ItemTrend day90;
	private ItemTrend day180;
	private boolean loaded;

	public OSRSItem(int id) {
		this.id = id;
		this.loaded = false;
	}

	public OSRSItem(int id, String name, String description, boolean members, String icon, String icon_large, String type, String typeIcon,
			ItemPrice current, ItemTrend today, ItemTrend day30, ItemTrend day90, ItemTrend day180) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.members = members;
		this.icon = icon;
		this.icon_large = icon_large;
		this.type = type;
		this.typeIcon = typeIcon;
		this.current = current;
		this.today = today;
		this.day30 = day30;
		this.day90 = day90;
		this.day180 = day180;
		this.loaded = true;
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		lazyLoad();
		return this.name;
	}

	public String getDescription() {
		lazyLoad();
		return this.description;
	}

	@SuppressWarnings("unused")
	private boolean isMembers() {
		lazyLoad();
		return this.members;
	}

	public String getIcon() {
		lazyLoad();
		return this.icon;
	}

	public String getIconLarge() {
		lazyLoad();
		return this.icon_large;
	}

	public String getType() {
		lazyLoad();
		return this.type;
	}

	public String getTypeIcon() {
		lazyLoad();
		return this.typeIcon;
	}

	public ItemPrice getCurrent() {
		lazyLoad();
		return this.current;
	}

	public ItemTrend getToday() {
		lazyLoad();
		return this.today;
	}

	public ItemTrend getDay30() {
		lazyLoad();
		return this.day30;
	}

	public ItemTrend getDay90() {
		lazyLoad();
		return this.day90;
	}

	public ItemTrend getDay180() {
		lazyLoad();
		return this.day180;
	}

	private boolean isLazyLoaded() {
		return this.loaded;
	}

	private boolean lazyLoad() {
		if (isLazyLoaded())
			return true;
		OSRSItem item = OSRS.get(this.id);
		if (item == null)
			return false;
		this.icon = item.icon;
		this.icon_large = item.icon_large;
		this.id = item.id;
		this.type = item.type;
		this.typeIcon = item.typeIcon;
		this.name = item.name;
		this.description = item.description;
		this.current = item.current;
		this.today = item.today;
		this.members = item.members;
		this.day30 = item.day30;
		this.day90 = item.day90;
		this.day180 = item.day180;
		return this.loaded = true;
	}
}
