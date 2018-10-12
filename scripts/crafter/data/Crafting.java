package scripts.crafter.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;

import scripts.crafter.data.Constants.Interfaces;
import scripts.crafter.data.Constants.Materials;
import scripts.crafter.data.Constants.Tools;
import scripts.crafter.data.Locations.Type;
import scripts.usa.api.web.items.osbuddy.OSBuddy;

public class Crafting {

	public static String[] getMaterials() {
		ArrayList<String> materials = new ArrayList<String>();
		for (Products product : Products.values()) {
			String material = product.getMaterials().get(0).getName();
			if (!materials.contains(material))
				materials.add(material);
		}
		Collections.sort(materials);
		return materials.toArray(new String[materials.size()]);
	}

	public static String[] getProductsFor(String name) {
		return Arrays.stream(Products.values())
				.filter(p -> p.getMaterials().get(0).getName().equalsIgnoreCase(name))
				.map(p -> p.getName())
				.toArray(String[]::new);
	}

	public static Products getProduct(String name) {
		Optional<Products> product = Arrays.stream(Products.values()).filter(p -> p.getName().equalsIgnoreCase(name)).findFirst();
		return product.isPresent() ? product.get() : null;
	}

	public static Products getHighestProductFor(String material) {
		return Arrays.stream(Products.values())
				.filter(p -> p.getMaterials().get(0).getName().equalsIgnoreCase(material) && Skills.getCurrentLevel(SKILLS.CRAFTING) >= p.getLevel())
				.max(Comparator.comparing(Products::getLevel))
				.get();
	}

	public enum Products {
		/**
		 * SPINNING
		 */
		BALL_OF_WOOL(1, "Ball of wool", Materials.BALL_OF_WOOL.getID(), Interfaces.GENERAL, "Ball of Wool", "1", Materials.WOOL, Type.SPINNING_WHEEL),
		BOW_STRING(10, "Bow string", 1777, Interfaces.GENERAL, "Bow String", "3", Materials.FLAX, Type.SPINNING_WHEEL),
		CROSSBOW_STRING(
				10,
				"Crossbow string",
				9438,
				Interfaces.GENERAL,
				"Crossbow String (Tree Roots)",
				"6",
				Materials.WILLOW_ROOTS,
				Type.SPINNING_WHEEL),
		MAGIC_STRING(19, "Magic string", 1777, Interfaces.GENERAL, "Magic String", "7", Materials.MAGIC_ROOTS, Type.SPINNING_WHEEL),
		ROPE(30, "Rope", 1777, Interfaces.GENERAL, "Rope", "4", Materials.HAIR, Type.SPINNING_WHEEL),

		/**
		 * LEATHER
		 */
		LEATHER_GLOVES(1, "Leather gloves", 1059, Interfaces.GENERAL, "Leather gloves", "1", Materials.LEATHER, Tools.NEEDLE_AND_THREAD, Type.BANK),
		LEATHER_BOOTS(7, "Leather boots", 1061, Interfaces.GENERAL, "Leather boots", "2", Materials.LEATHER, Tools.NEEDLE_AND_THREAD, Type.BANK),
		LEATHER_COWL(9, "Leather cowl", 1167, Interfaces.GENERAL, "Leather cowl", "3", Materials.LEATHER, Tools.NEEDLE_AND_THREAD, Type.BANK),
		LEATHER_VAMBRACES(
				11,
				"Leather vambraces",
				1063,
				Interfaces.GENERAL,
				"4",
				"Leather vambraces",
				Materials.LEATHER,
				Tools.NEEDLE_AND_THREAD,
				Type.BANK),
		LEATHER_BODY(14, "Leather body", 1129, Interfaces.GENERAL, "Leather body", "5", Materials.LEATHER, Tools.NEEDLE_AND_THREAD, Type.BANK),
		LEATHER_CHAPS(18, "Leather chaps", 1095, Interfaces.GENERAL, "Leather chaps", "6", Materials.LEATHER, Tools.NEEDLE_AND_THREAD, Type.BANK),
		COIF(38, "Coif", 1169, Interfaces.GENERAL, "Coif", "7", Materials.LEATHER, Tools.NEEDLE_AND_THREAD, Type.BANK),

		/**
		 * HARD LEATHER
		 */
		HARDLEATHER_BODY(
				29,
				"Hardleather body",
				1131,
				Interfaces.GENERAL,
				"Hardleather body",
				" ",
				Materials.HARD_LEATHER,
				Tools.NEEDLE_AND_THREAD,
				Type.BANK),

		/**
		 * GREEN DRAGON LEATHER
		 */
		GREEN_DHIDE_VAMB(
				57,
				"Green dragonhide vambraces",
				1065,
				Interfaces.GENERAL,
				"Green dragonhide vambraces",
				"2",
				new Material("Green dragon leather", 1745, 1),
				Tools.NEEDLE_AND_THREAD,
				Type.BANK),
		GREEN_DHIDE_CHAPS(
				60,
				"Green dragonhide chaps",
				1099,
				Interfaces.GENERAL,
				"Green dragonhide chaps",
				"3",
				new Material("Green dragon leather", 1745, 2),
				Tools.NEEDLE_AND_THREAD,
				Type.BANK),
		GREEN_DHIDE_BODY(
				63,
				"Green dragonhide body",
				1135,
				Interfaces.GENERAL,
				"Green dragonhide body",
				"1",
				new Material("Green dragon leather", 1745, 3),
				Tools.NEEDLE_AND_THREAD,
				Type.BANK),

		/**
		 * BLUE DRAGON LEATHER
		 */
		BLUE_DHIDE_VAMB(
				66,
				"Blue dragonhide vambraces",
				2487,
				Interfaces.GENERAL,
				"Blue dragonhide vambraces",
				"2",
				new Material("Blue dragon leather", 2505, 1),
				Tools.NEEDLE_AND_THREAD,
				Type.BANK),
		BLUE_DHIDE_CHAPS(
				68,
				"Blue dragonhide chaps",
				2493,
				Interfaces.GENERAL,
				"Blue dragonhide chaps",
				"3",
				new Material("Blue dragon leather", 2505, 2),
				Tools.NEEDLE_AND_THREAD,
				Type.BANK),
		BLUE_DHIDE_BODY(
				71,
				"Blue dragonhide body",
				2499,
				Interfaces.GENERAL,
				"Blue dragonhide body",
				"1",
				new Material("Blue dragon leather", 2505, 3),
				Tools.NEEDLE_AND_THREAD,
				Type.BANK),

		/**
		 * RED DRAGON LEATHER
		 */
		RED_DHIDE_VAMB(
				73,
				"Red dragonhide vambraces",
				2489,
				Interfaces.GENERAL,
				"Red dragonhide vambraces",
				"2",
				new Material("Red dragon leather", 2507, 1),
				Tools.NEEDLE_AND_THREAD,
				Type.BANK),
		RED_DHIDE_CHAPS(
				75,
				"Red dragonhide chaps",
				2495,
				Interfaces.GENERAL,
				"Red dragonhide chaps",
				"3",
				new Material("Red dragon leather", 2507, 2),
				Tools.NEEDLE_AND_THREAD,
				Type.BANK),
		RED_DHIDE_BODY(
				77,
				"Red dragonhide body",
				2501,
				Interfaces.GENERAL,
				"Red dragonhide body",
				"1",
				new Material("Red dragon leather", 2507, 3),
				Tools.NEEDLE_AND_THREAD,
				Type.BANK),

		/**
		 * BLACK DRAGON LEATHER
		 */
		BLACK_DHIDE_VAMB(
				79,
				"Black dragonhide vambraces",
				2491,
				Interfaces.GENERAL,
				"Black dragonhide vambraces",
				"2",
				new Material("Black dragon leather", 2509, 1),
				Tools.NEEDLE_AND_THREAD,
				Type.BANK),
		BLACK_DHIDE_CHAPS(
				82,
				"Black dragonhide chaps",
				2497,
				Interfaces.GENERAL,
				"Black dragonhide chaps",
				"3",
				new Material("Black dragon leather", 2509, 2),
				Tools.NEEDLE_AND_THREAD,
				Type.BANK),
		BLACK_DHIDE_BODY(
				84,
				"Black dragonhide body",
				2503,
				Interfaces.GENERAL,
				"Black dragonhide body",
				"1",
				new Material("Black dragon leather", 2509, 3),
				Tools.NEEDLE_AND_THREAD,
				Type.BANK),

		/**
		 * GLASSBLOWING
		 */
		MOLTEN_GLASS(
				1,
				"Molten glass",
				1775,
				Interfaces.GENERAL,
				"Molten glass",
				" ",
				Arrays.asList(Materials.BUCKET_OF_SAND, Materials.SODA_ASH),
				Type.FURNACE),
		BEER_GLASS(1, "Beer glass", 1919, Interfaces.GENERAL, "Beer glass", "1", Materials.MOLTEN_GLASS, Tools.GLASSBLOWING_PIPE, Type.BANK),
		CANDLE_LANTERN(
				4,
				"Candle lantern",
				4527,
				Interfaces.GENERAL,
				"Candle lantern",
				"2",
				Materials.MOLTEN_GLASS,
				Tools.GLASSBLOWING_PIPE,
				Type.BANK),
		OIL_LAMP(12, "Oil lamp", 4525, Interfaces.GENERAL, "Oil lamp", "3", Materials.MOLTEN_GLASS, Tools.GLASSBLOWING_PIPE, Type.BANK),
		VIAL(33, "Vial", 229, Interfaces.GENERAL, "Vial", "4", Materials.MOLTEN_GLASS, Tools.GLASSBLOWING_PIPE, Type.BANK),
		FISHBOWL(42, "Fishbowl", 6667, Interfaces.GENERAL, "Fishbowl", "5", Materials.MOLTEN_GLASS, Tools.GLASSBLOWING_PIPE, Type.BANK),
		UNPOWERED_STAFF_ORB(
				46,
				"Unpowered staff orb",
				567,
				Interfaces.GENERAL,
				"Unpowered staff orb",
				"6",
				Materials.MOLTEN_GLASS,
				Tools.GLASSBLOWING_PIPE,
				Type.BANK),
		LANTERN_LENS(49, "Lantern lens", 4542, Interfaces.GENERAL, "Lantern lens", "7", Materials.MOLTEN_GLASS, Tools.GLASSBLOWING_PIPE, Type.BANK),
		LIGHT_ORB(87, "Light orb", 10980, Interfaces.GENERAL, "Light orb", "8", Materials.MOLTEN_GLASS, Tools.GLASSBLOWING_PIPE, Type.BANK),

		/**
		 * UNCUT GEMS
		 */
		OPAL(1, "Opal", Materials.OPAL.getID(), Interfaces.GENERAL, "Uncut opal", " ", new Material("Uncut opal", 1625, 1), Tools.CHISEL, Type.BANK),
		JADE(13, "Jade", Materials.JADE.getID(), Interfaces.GENERAL, "Uncut jade", " ", new Material("Uncut jade", 1627, 1), Tools.CHISEL, Type.BANK),
		RED_TOPAZ(
				16,
				"Red topaz",
				Materials.RED_TOPAZ.getID(),
				Interfaces.GENERAL,
				"Uncut red topaz",
				" ",
				new Material("Uncut red topaz", 1629, 1),
				Tools.CHISEL,
				Type.BANK),
		SAPPHIRE(
				20,
				"Sapphire",
				Materials.SAPPHIRE.getID(),
				Interfaces.GENERAL,
				"Uncut sapphire",
				" ",
				new Material("Uncut sapphire", 1623, 1),
				Tools.CHISEL,
				Type.BANK),
		EMERALD(
				27,
				"Emerald",
				Materials.EMERALD.getID(),
				Interfaces.GENERAL,
				"Uncut emerald",
				" ",
				new Material("Uncut emerald", 1621, 1),
				Tools.CHISEL,
				Type.BANK),
		RUBY(34, "Ruby", Materials.RUBY.getID(), Interfaces.GENERAL, "Uncut ruby", " ", new Material("Uncut ruby", 1619, 1), Tools.CHISEL, Type.BANK),
		DIAMOND(
				43,
				"Diamond",
				Materials.DIAMOND.getID(),
				Interfaces.GENERAL,
				"Uncut diamond",
				" ",
				new Material("Uncut diamond", 1617, 1),
				Tools.CHISEL,
				Type.BANK),
		DRAGONSTONE(
				55,
				"Dragonstone",
				Materials.DRAGONSTONE.getID(),
				Interfaces.GENERAL,
				"Uncut dragonstone",
				" ",
				new Material("Uncut dragonstone", 1631, 1),
				Tools.CHISEL,
				Type.BANK),

		/**
		 * GOLD JEWELRY
		 */
		GOLD_RING(5, "Gold ring", 1635, Interfaces.GOLD_JEWELRY, "Gold ring", null, Materials.GOLD_BAR, Tools.RING_MOULD, Type.FURNACE),
		GOLD_NECKLACE(
				6,
				"Gold necklace",
				1654,
				Interfaces.GOLD_JEWELRY,
				"Gold necklace",
				null,
				Materials.GOLD_BAR,
				Tools.NECKLACE_MOULD,
				Type.FURNACE),
		GOLD_BRACELET(
				7,
				"Gold bracelet",
				11069,
				Interfaces.GOLD_JEWELRY,
				"Gold bracelet",
				null,
				Materials.GOLD_BAR,
				Tools.BRACELET_MOULD,
				Type.FURNACE),
		GOLD_AMULET_UNSTRUNG(
				8,
				"Gold amulet (u)",
				1673,
				Interfaces.GOLD_JEWELRY,
				"Gold amulet (u)",
				null,
				Materials.GOLD_BAR,
				Tools.AMULET_MOULD,
				Type.FURNACE),

		/**
		 * SILVER JEWELRY
		 */
		UNSTRUNG_SYMBOL(
				16,
				"Unstrung symbol",
				1714,
				Interfaces.SILVER_JEWELRY,
				"Unstrung symbol",
				null,
				Materials.SILVER_BAR,
				Tools.HOLY_MOULD,
				Type.FURNACE),
		SILVER_SICKLE(
				18,
				"Silver sickle",
				5525,
				Interfaces.SILVER_JEWELRY,
				"Silver sickle",
				null,
				Materials.SILVER_BAR,
				Tools.SICKLE_MOULD,
				Type.FURNACE),
		TIARA(23, "Tiara", 5525, Interfaces.SILVER_JEWELRY, "Tiara", null, Materials.SILVER_BAR, Tools.TIARA_MOULD, Type.FURNACE),

		/**
		 * OPAL JEWELRY
		 */
		OPAL_RING(
				1,
				"Opal ring",
				21081,
				Interfaces.SILVER_JEWELRY,
				"Opal ring",
				null,
				Arrays.asList(Materials.OPAL, Materials.SILVER_BAR),
				Tools.BRACELET_MOULD,
				Type.FURNACE),
		OPAL_NECKLACE(
				16,
				"Opal necklace",
				21090,
				Interfaces.SILVER_JEWELRY,
				"Opal necklace",
				null,
				Arrays.asList(Materials.OPAL, Materials.SILVER_BAR),
				Tools.NECKLACE_MOULD,
				Type.FURNACE),
		OPAL_BRACELET(
				22,
				"Opal bracelet",
				21117,
				Interfaces.SILVER_JEWELRY,
				"Opal bracelet",
				null,
				Arrays.asList(Materials.OPAL, Materials.SILVER_BAR),
				Tools.BRACELET_MOULD,
				Type.FURNACE),
		OPAL_AMULET_UNSTRUNG(
				27,
				"Opal amulet (u)",
				21099,
				Interfaces.SILVER_JEWELRY,
				"Opal amulet (u)",
				null,
				Arrays.asList(Materials.OPAL, Materials.SILVER_BAR),
				Tools.AMULET_MOULD,
				Type.FURNACE),

		/**
		 * JADE JEWELRY
		 */
		JADE_RING(
				13,
				"Jade ring",
				21084,
				Interfaces.SILVER_JEWELRY,
				"Jade ring",
				null,
				Arrays.asList(Materials.JADE, Materials.SILVER_BAR),
				Tools.BRACELET_MOULD,
				Type.FURNACE),
		JADE_NECKLACE(
				25,
				"Jade necklace",
				21093,
				Interfaces.SILVER_JEWELRY,
				"Jade necklace",
				null,
				Arrays.asList(Materials.JADE, Materials.SILVER_BAR),
				Tools.NECKLACE_MOULD,
				Type.FURNACE),
		JADE_BRACELET(
				29,
				"Jade bracelet",
				21120,
				Interfaces.SILVER_JEWELRY,
				"Jade bracelet",
				null,
				Arrays.asList(Materials.JADE, Materials.SILVER_BAR),
				Tools.BRACELET_MOULD,
				Type.FURNACE),
		JADE_AMULET_UNSTRUNG(
				34,
				"Jade amulet (u)",
				21102,
				Interfaces.SILVER_JEWELRY,
				"Jade amulet (u)",
				null,
				Arrays.asList(Materials.JADE, Materials.SILVER_BAR),
				Tools.AMULET_MOULD,
				Type.FURNACE),

		/**
		 * RED TOPAZ JEWELRY
		 */
		TOPAZ_RING(
				16,
				"Topaz ring",
				21087,
				Interfaces.SILVER_JEWELRY,
				"Topaz ring",
				null,
				Arrays.asList(Materials.RED_TOPAZ, Materials.SILVER_BAR),
				Tools.BRACELET_MOULD,
				Type.FURNACE),
		TOPAZ_NECKLACE(
				32,
				"Topaz necklace",
				21096,
				Interfaces.SILVER_JEWELRY,
				"Topaz necklace",
				null,
				Arrays.asList(Materials.RED_TOPAZ, Materials.SILVER_BAR),
				Tools.NECKLACE_MOULD,
				Type.FURNACE),
		TOPAZ_BRACELET(
				38,
				"Topaz bracelet",
				21123,
				Interfaces.SILVER_JEWELRY,
				"Topaz bracelet",
				null,
				Arrays.asList(Materials.RED_TOPAZ, Materials.SILVER_BAR),
				Tools.BRACELET_MOULD,
				Type.FURNACE),
		TOPAZ_AMULET_UNSTRUNG(
				45,
				"Topaz amulet (u)",
				21105,
				Interfaces.SILVER_JEWELRY,
				"Topaz amulet (u)",
				null,
				Arrays.asList(Materials.RED_TOPAZ, Materials.SILVER_BAR),
				Tools.AMULET_MOULD,
				Type.FURNACE),

		/**
		 * SAPPHIRE JEWELRY
		 */
		SAPPHIRE_RING(
				20,
				"Sapphire ring",
				1637,
				Interfaces.GOLD_JEWELRY,
				"Sapphire ring",
				null,
				Arrays.asList(Materials.SAPPHIRE, Materials.GOLD_BAR),
				Tools.RING_MOULD,
				Type.FURNACE),
		SAPPHIRE_NECKLACE(
				22,
				"Sapphire necklace",
				1656,
				Interfaces.GOLD_JEWELRY,
				"Sapphire necklace",
				null,
				Arrays.asList(Materials.SAPPHIRE, Materials.GOLD_BAR),
				Tools.NECKLACE_MOULD,
				Type.FURNACE),
		SAPPHIRE_BRACELET(
				23,
				"Sapphire bracelet",
				11072,
				Interfaces.GOLD_JEWELRY,
				"Sapphire bracelet",
				null,
				Arrays.asList(Materials.SAPPHIRE, Materials.GOLD_BAR),
				Tools.BRACELET_MOULD,
				Type.FURNACE),
		SAPPHIRE_AMULET_UNSTRUNG(
				24,
				"Sapphire amulet (u)",
				1675,
				Interfaces.GOLD_JEWELRY,
				"Sapphire amulet (u)",
				null,
				Arrays.asList(Materials.SAPPHIRE, Materials.GOLD_BAR),
				Tools.AMULET_MOULD,
				Type.FURNACE),

		/**
		 * EMERALD JEWELRY
		 */
		EMERALD_RING(
				27,
				"Emerald ring",
				1639,
				Interfaces.GOLD_JEWELRY,
				"Emerald ring",
				null,
				Arrays.asList(Materials.EMERALD, Materials.GOLD_BAR),
				Tools.RING_MOULD,
				Type.FURNACE),
		EMERALD_NECKLACE(
				29,
				"Emerald necklace",
				1658,
				Interfaces.GOLD_JEWELRY,
				"Emerald necklace",
				null,
				Arrays.asList(Materials.EMERALD, Materials.GOLD_BAR),
				Tools.NECKLACE_MOULD,
				Type.FURNACE),
		EMERALD_BRACELET(
				30,
				"Emerald bracelet",
				11076,
				Interfaces.GOLD_JEWELRY,
				"Emerald bracelet",
				null,
				Arrays.asList(Materials.EMERALD, Materials.GOLD_BAR),
				Tools.BRACELET_MOULD,
				Type.FURNACE),
		EMERALD_AMULET_UNSTRUNG(
				31,
				"Emerald amulet (u)",
				1677,
				Interfaces.GOLD_JEWELRY,
				"Emerald amulet (u)",
				null,
				Arrays.asList(Materials.EMERALD, Materials.GOLD_BAR),
				Tools.AMULET_MOULD,
				Type.FURNACE),

		/**
		 * RUBY JEWELRY
		 */
		RUBY_RING(
				34,
				"Ruby ring",
				1641,
				Interfaces.GOLD_JEWELRY,
				"Ruby ring",
				null,
				Arrays.asList(Materials.RUBY, Materials.GOLD_BAR),
				Tools.RING_MOULD,
				Type.FURNACE),
		RUBY_NECKLACE(
				40,
				"Ruby necklace",
				1660,
				Interfaces.GOLD_JEWELRY,
				"Ruby necklace",
				null,
				Arrays.asList(Materials.RUBY, Materials.GOLD_BAR),
				Tools.NECKLACE_MOULD,
				Type.FURNACE),
		RUBY_BRACELET(
				42,
				"Ruby bracelet",
				11085,
				Interfaces.GOLD_JEWELRY,
				"Ruby bracelet",
				null,
				Arrays.asList(Materials.RUBY, Materials.GOLD_BAR),
				Tools.BRACELET_MOULD,
				Type.FURNACE),
		RUBY_AMULET_UNSTRUNG(
				50,
				"Ruby amulet (u)",
				1679,
				Interfaces.GOLD_JEWELRY,
				"Ruby amulet (u)",
				null,
				Arrays.asList(Materials.RUBY, Materials.GOLD_BAR),
				Tools.AMULET_MOULD,
				Type.FURNACE),

		/**
		 * DIAMOND JEWELRY
		 */
		DIAMOND_RING(
				43,
				"Diamond ring",
				1643,
				Interfaces.GOLD_JEWELRY,
				"Diamond ring",
				null,
				Arrays.asList(Materials.DIAMOND, Materials.GOLD_BAR),
				Tools.RING_MOULD,
				Type.FURNACE),
		DIAMOND_NECKLACE(
				56,
				"Diamond necklace",
				1662,
				Interfaces.GOLD_JEWELRY,
				"Diamond necklace",
				null,
				Arrays.asList(Materials.DIAMOND, Materials.GOLD_BAR),
				Tools.NECKLACE_MOULD,
				Type.FURNACE),
		DIAMOND_BRACELET(
				58,
				"Diamond bracelet",
				11092,
				Interfaces.GOLD_JEWELRY,
				"Diamond bracelet",
				null,
				Arrays.asList(Materials.DIAMOND, Materials.GOLD_BAR),
				Tools.BRACELET_MOULD,
				Type.FURNACE),
		DIAMOND_AMULET_UNSTRUNG(
				70,
				"Diamond amulet (u)",
				1681,
				Interfaces.GOLD_JEWELRY,
				"Diamond amulet (u)",
				null,
				Arrays.asList(Materials.DIAMOND, Materials.GOLD_BAR),
				Tools.AMULET_MOULD,
				Type.FURNACE),

		/**
		 * DRAGONSTONE JEWELRY
		 */
		DRAGONSTONE_RING(
				55,
				"Dragonstone ring",
				1645,
				Interfaces.GOLD_JEWELRY,
				"Dragonstone ring",
				null,
				Arrays.asList(Materials.DRAGONSTONE, Materials.GOLD_BAR),
				Tools.RING_MOULD,
				Type.FURNACE),
		DRAGON_NECKLACE(
				72,
				"Dragon necklace",
				1664,
				Interfaces.GOLD_JEWELRY,
				"Dragon necklace",
				null,
				Arrays.asList(Materials.DRAGONSTONE, Materials.GOLD_BAR),
				Tools.NECKLACE_MOULD,
				Type.FURNACE),
		DRAGONSTONE_BRACELET(
				74,
				"Dragonstone bracelet",
				11115,
				Interfaces.GOLD_JEWELRY,
				"Dragonstone bracelet",
				null,
				Arrays.asList(Materials.DRAGONSTONE, Materials.GOLD_BAR),
				Tools.BRACELET_MOULD,
				Type.FURNACE),
		DRAGONSTONE_AMULET_UNSTRUNG(
				80,
				"Dragonstone amulet (u)",
				1683,
				Interfaces.GOLD_JEWELRY,
				"Dragonstone amulet (u)",
				null,
				Arrays.asList(Materials.DRAGONSTONE, Materials.GOLD_BAR),
				Tools.AMULET_MOULD,
				Type.FURNACE),

		/**
		 * STRINGING AMULETS
		 */
		OPAL_AMULET(
				1,
				"Opal amulet",
				21108,
				Interfaces.GENERAL,
				"Opal amulet",
				" ",
				Arrays.asList(Materials.BALL_OF_WOOL, new Material("Opal amulet (u)", 21099, 1)),
				Type.BANK),
		JADE_AMULET(
				1,
				"Jade amulet",
				21111,
				Interfaces.GENERAL,
				"Jade amulet",
				" ",
				Arrays.asList(Materials.BALL_OF_WOOL, new Material("Jade amulet (u)", 21102, 1)),
				Type.BANK),
		TOPAZ_AMULET(
				1,
				"Topaz amulet",
				21114,
				Interfaces.GENERAL,
				"Topaz amulet",
				" ",
				Arrays.asList(Materials.BALL_OF_WOOL, new Material("Topaz amulet (u)", 21105, 1)),
				Type.BANK),
		SAPPHIRE_AMULET(
				1,
				"Sapphire amulet",
				1694,
				Interfaces.GENERAL,
				"Sapphire amulet",
				" ",
				Arrays.asList(Materials.BALL_OF_WOOL, new Material("Sapphire amulet (u)", 1675, 1)),
				Type.BANK),
		EMERALD_AMULET(
				1,
				"Emerald amulet",
				1696,
				Interfaces.GENERAL,
				"Emerald amulet",
				" ",
				Arrays.asList(Materials.BALL_OF_WOOL, new Material("Emerald amulet (u)", 1677, 1)),
				Type.BANK),
		RUBY_AMULET(
				1,
				"Ruby amulet",
				1698,
				Interfaces.GENERAL,
				"Ruby amulet",
				" ",
				Arrays.asList(Materials.BALL_OF_WOOL, new Material("Ruby amulet (u)", 1679, 1)),
				Type.BANK),
		DIAMOND_AMULET(
				1,
				"Diamond amulet",
				1700,
				Interfaces.GENERAL,
				"Diamond amulet",
				" ",
				Arrays.asList(Materials.BALL_OF_WOOL, new Material("Diamond amulet (u)", 1681, 1)),
				Type.BANK),
		DRAGONSTONE_AMULET(
				1,
				"Dragonstone amulet",
				1702,
				Interfaces.GENERAL,
				"Dragonstone amulet",
				" ",
				Arrays.asList(Materials.BALL_OF_WOOL, new Material("Dragonstone amulet (u)", 1683, 1)),
				Type.BANK),

		/**
		 * BATTLESTAVES
		 */
		WATER_BATTLESTAFF(
				54,
				"Water battlestaff",
				1395,
				Interfaces.GENERAL,
				"Water battlestaff",
				" ",
				Arrays.asList(Materials.BATTLESTAFF, new Material("Water orb", 571, 1)),
				Type.BANK),
		EARTH_BATTLESTAFF(
				58,
				"Earth battlestaff",
				1399,
				Interfaces.GENERAL,
				"Earth battlestaff",
				" ",
				Arrays.asList(Materials.BATTLESTAFF, new Material("Earth orb", 575, 1)),
				Type.BANK),
		FIRE_BATTLESTAFF(
				62,
				"Fire battlestaff",
				1393,
				Interfaces.GENERAL,
				"Fire battlestaff",
				" ",
				Arrays.asList(Materials.BATTLESTAFF, new Material("Fire orb", 569, 1)),
				Type.BANK),
		AIR_BATTLESTAFF(
				66,
				"Air battlestaff",
				1397,
				Interfaces.GENERAL,
				"Air battlestaff",
				" ",
				Arrays.asList(Materials.BATTLESTAFF, new Material("Air orb", 573, 1)),
				Type.BANK);

		private final String name;
		private final int id;
		private final int level;
		private final int masterIndex;
		private final String componentText;
		private final String key;
		private final List<Material> materials;
		private final List<Tool> tools;
		private final Type type;

		Products(int level, String name, int id, int index, String componentText, String key, List<Material> materials, List<Tool> tools, Type type) {
			this.level = level;
			this.name = name;
			this.id = id;
			this.masterIndex = index;
			this.componentText = componentText;
			this.key = key;
			this.materials = materials;
			this.tools = tools;
			this.type = type;
		}

		Products(int level, String name, int id, int masterIndex, String componentText, String key, Material material, Tool tool, Type type) {
			this(level, name, id, masterIndex, componentText, key, Arrays.asList(material), Arrays.asList(tool), type);
		}

		Products(int level, String name, int id, int masterIndex, String componentText, String key, Material material, List<Tool> tools, Type type) {
			this(level, name, id, masterIndex, componentText, key, Arrays.asList(material), tools, type);
		}

		Products(int level, String name, int id, int masterIndex, String componentText, String key, List<Material> materials, Tool tool, Type type) {
			this(level, name, id, masterIndex, componentText, key, materials, Arrays.asList(tool), type);
		}

		Products(int level, String name, int id, int masterIndex, String componentText, String key, List<Material> materials, Type type) {
			this(level, name, id, masterIndex, componentText, key, materials, new ArrayList<Tool>(), type);
		}

		Products(int level, String name, int id, int masterIndex, String componentText, String key, Material material, Type type) {
			this(level, name, id, masterIndex, componentText, key, Arrays.asList(material), new ArrayList<Tool>(), type);
		}

		public String getName() {
			return this.name;
		}

		public int getID() {
			return this.id;
		}

		public int getLevel() {
			return this.level;
		}

		public int getMasterIndex() {
			return this.masterIndex;
		}

		public String getComponentText() {
			return this.componentText;
		}

		public boolean useHotKey() {
			return this.key != null;
		}

		public String getKey() {
			return this.key;
		}

		public List<Material> getMaterials() {
			return this.materials;
		}

		public List<Tool> getTools() {
			return this.tools;
		}

		public Type getType() {
			return this.type;
		}

		public int getValue() {
			return OSBuddy.get(this.id).getAveragePrice();
		}

		public int getProfit() {
			int cost = this.materials.stream().mapToInt(m -> m.getRequiredAmount() * m.getValue()).sum();
			return this.getValue() - cost;
		}
	}
}
