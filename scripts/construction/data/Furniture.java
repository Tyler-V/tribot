package scripts.construction.data;

import java.util.Arrays;
import java.util.List;

public enum Furniture {

	OAK_LARDER("Oak larder", "Larder space", "Larder", 30, Arrays.asList(new Material(Materials.Plank.OAK, 8))),

	OAK_KITCHEN_TABLE("Oak kitchen table", "Table space", "Oak table", 52, Arrays.asList(new Material(Materials.Plank.OAK, 6))),

	MAHOGANY_TABLE("Mahogany table", "Table space", "Mahogany table", 52, Arrays.asList(new Material(Materials.Plank.OAK, 6)));

	private final String name;
	private final String buildingSpace;
	private final String removeName;
	private final int level;
	private final List<Material> materials;

	Furniture(String name, String buildingSpace, String removeName, int level, List<Material> materials) {
		this.name = name;
		this.buildingSpace = buildingSpace;
		this.removeName = removeName;
		this.level = level;
		this.materials = materials;
	}

	public String getName() {
		return this.name;
	}

	public String getBuildingSpace() {
		return this.buildingSpace;
	}

	public String getRemoveName() {
		return this.removeName;
	}

	public int getLevel() {
		return this.level;
	}

	public List<Material> getMaterials() {
		return this.materials;
	}
}
