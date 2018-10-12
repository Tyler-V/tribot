package scripts.crafter.data;

import java.util.Arrays;
import java.util.List;

public class Constants {

	static class Materials {
		public static Material LEATHER = new Material("Leather", 1741, 1);
		public static Material HARD_LEATHER = new Material("Hard leather", 1743, 1);
		public static Material BUCKET_OF_SAND = new Material("Bucket of sand", 1783, 1);
		public static Material SODA_ASH = new Material("Soda ash", 1781, 1);
		public static Material GOLD_BAR = new Material("Gold bar", 2357, 1);
		public static Material SILVER_BAR = new Material("Silver bar", 2355, 1);
		public static Material OPAL = new Material("Opal", 1609, 1);
		public static Material JADE = new Material("Jade", 1611, 1);
		public static Material RED_TOPAZ = new Material("Red topaz", 1613, 1);
		public static Material SAPPHIRE = new Material("Sapphire", 1607, 1);
		public static Material EMERALD = new Material("Emerald", 1605, 1);
		public static Material RUBY = new Material("Ruby", 1603, 1);
		public static Material DIAMOND = new Material("Diamond", 1601, 1);
		public static Material DRAGONSTONE = new Material("Dragonstone", 1615, 1);
		public static Material BATTLESTAFF = new Material("Battlestaff", 1391, 1);
		public static Material MOLTEN_GLASS = new Material("Molten glass", 1775, 1);
		public static Material BALL_OF_WOOL = new Material("Ball of wool", 1759, 1);
		public static Material WOOL = new Material("Wool", 1737, 1);
		public static Material FLAX = new Material("Flax", 1779, 1);
		public static Material SINEW = new Material("Sinew", 9436, 1);
		public static Material WILLOW_ROOTS = new Material("Willow roots", 6045, 1);
		public static Material MAGIC_ROOTS = new Material("Magic roots", 6051, 1);
		public static Material HAIR = new Material("Hair", 10814, 1);
	}

	static class Tools {
		public static Tool NEEDLE = new Tool("Needle", 1733, 1, true, true);
		public static Tool THREAD = new Tool("Thread", 1734, 1, false, true);
		public static List<Tool> NEEDLE_AND_THREAD = Arrays.asList(Tools.NEEDLE, Tools.THREAD);
		public static Tool CHISEL = new Tool("Chisel", 1755, 1, true);
		public static Tool BRACELET_MOULD = new Tool("Bracelet mould", 11065, 1);
		public static Tool NECKLACE_MOULD = new Tool("Necklace mould", 1597, 1);
		public static Tool AMULET_MOULD = new Tool("Amulet mould", 1595, 1);
		public static Tool RING_MOULD = new Tool("Ring mould", 1592, 1);
		public static Tool HOLY_MOULD = new Tool("Holy mould", 11065, 1);
		public static Tool TIARA_MOULD = new Tool("Tiara mould", 5523, 1);
		public static Tool SICKLE_MOULD = new Tool("Sickle mould", 2976, 1);
		public static Tool GLASSBLOWING_PIPE = new Tool("Glassblowing pipe", 1785, 1);
	}

	static class Interfaces {
		public static int GENERAL = 270;
		public static int GOLD_JEWELRY = 446;
		public static int SILVER_JEWELRY = 6;
	}
}
