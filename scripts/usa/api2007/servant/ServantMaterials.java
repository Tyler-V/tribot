package scripts.usa.api2007.servant;

import scripts.usa.api.util.Strings;

public enum ServantMaterials {

	WOODEN_PLANKS,
	OAK_PLANKS,
	TEAK_PLANKS,
	MAHOGANY_PLANKS,
	SOFT_CLAY,
	LIMESTONE_BRICK,
	STEEL_BAR,
	CLOTH,
	GOLD_LEAF,
	MARBLE_BLOCK,
	MAGIC_HOUSING_STONE;

	public String getName() {
		return Strings.toSentenceCase(this.name());
	}
}
