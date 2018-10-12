package scripts.usa.api2007.enums;

import java.util.Arrays;
import java.util.List;

public enum Lecterns {

	ANY(
			new int[] { 13642, 13643, 13644, 13645, 13646, 13647, 13648 },
			Tablets.VARROCK_TELEPORT,
			Tablets.LUMBRIDGE_TELEPORT,
			Tablets.FALADOR_TELEPORT,
			Tablets.TELEPORT_TO_HOUSE,
			Tablets.CAMELOT_TELEPORT,
			Tablets.ARDOUGNE_TELEPORT,
			Tablets.WATCHTOWER_TELEPORT,
			Tablets.ENCHANT_SAPPHIRE_OR_OPAL,
			Tablets.ENCHANT_EMERALD_OR_JADE,
			Tablets.ENCHANT_RUBY_OR_TOPAZ,
			Tablets.ENCHANT_DIAMOND,
			Tablets.ENCHANT_DRAGONSTONE,
			Tablets.ENCHANT_ONYX,
			Tablets.BONES_TO_BANANAS,
			Tablets.BONES_TO_PEACHES),
	OAK(13642, Tablets.VARROCK_TELEPORT, Tablets.ENCHANT_SAPPHIRE_OR_OPAL),
	EAGLE(13643, Tablets.VARROCK_TELEPORT, Tablets.LUMBRIDGE_TELEPORT, Tablets.FALADOR_TELEPORT, Tablets.ENCHANT_SAPPHIRE_OR_OPAL),
	DEMON(13644, Tablets.VARROCK_TELEPORT, Tablets.ENCHANT_SAPPHIRE_OR_OPAL, Tablets.ENCHANT_EMERALD_OR_JADE, Tablets.BONES_TO_BANANAS),
	TEAK_EAGLE(
			13645,
			Tablets.VARROCK_TELEPORT,
			Tablets.LUMBRIDGE_TELEPORT,
			Tablets.FALADOR_TELEPORT,
			Tablets.CAMELOT_TELEPORT,
			Tablets.ARDOUGNE_TELEPORT,
			Tablets.ENCHANT_SAPPHIRE_OR_OPAL),
	TEAK_DEMON(
			13646,
			Tablets.VARROCK_TELEPORT,
			Tablets.ENCHANT_SAPPHIRE_OR_OPAL,
			Tablets.ENCHANT_EMERALD_OR_JADE,
			Tablets.ENCHANT_RUBY_OR_TOPAZ,
			Tablets.ENCHANT_DIAMOND,
			Tablets.BONES_TO_BANANAS),
	MAHOGANY_EAGLE(
			13647,
			Tablets.VARROCK_TELEPORT,
			Tablets.LUMBRIDGE_TELEPORT,
			Tablets.FALADOR_TELEPORT,
			Tablets.TELEPORT_TO_HOUSE,
			Tablets.CAMELOT_TELEPORT,
			Tablets.ARDOUGNE_TELEPORT,
			Tablets.WATCHTOWER_TELEPORT,
			Tablets.ENCHANT_SAPPHIRE_OR_OPAL),
	MAHOGANY_DEMON(
			13648,
			Tablets.VARROCK_TELEPORT,
			Tablets.ENCHANT_SAPPHIRE_OR_OPAL,
			Tablets.ENCHANT_EMERALD_OR_JADE,
			Tablets.ENCHANT_RUBY_OR_TOPAZ,
			Tablets.ENCHANT_DIAMOND,
			Tablets.ENCHANT_DRAGONSTONE,
			Tablets.ENCHANT_ONYX,
			Tablets.BONES_TO_BANANAS,
			Tablets.BONES_TO_PEACHES);

	private final int[] id;
	private final List<Tablets> tablets;

	Lecterns(int[] id, Tablets... tablets) {
		this.id = id;
		this.tablets = Arrays.asList(tablets);
	}

	Lecterns(int id, Tablets... tablets) {
		this(new int[] { id }, tablets);
	}

	public int[] getId() {
		return this.id;
	}

	public List<Tablets> getTablets() {
		return this.tablets;
	}
}
