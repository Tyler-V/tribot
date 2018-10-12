package scripts.fighterv2;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.Combat;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.Projection;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.types.RSAnimableEntity;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.api2007.types.RSModel;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.Painting;

import javafx.application.Platform;
import scripts.api.v1.api2007.Entity.Entity;
import scripts.api.v1.api2007.Entity.Npc;
import scripts.api.v1.api2007.Entity.Targets;
import scripts.api.v1.api2007.Entity.Preferences.NpcPreferences;
import scripts.api.v1.api2007.Utility.Condition.Conditional;
import scripts.api.v1.api2007.Utility.Condition.Conditions;
import scripts.api.v1.api2007.Worlds.WorldHopper;
import scripts.usa.api.antiban.ABC;
import scripts.usa.api.antiban.responder.AutoResponder;
import scripts.usa.api.items.LootItem;
import scripts.usa.api.items.Looting;
import scripts.usa.api.items.LootingStyle;
import scripts.usa.api.tracking.Tracker;
import scripts.usa.api.ui.Paint;
import scripts.usa.api.util.fx.FxUtil;
import scripts.usa.api2007.Banking;
import scripts.usa.api2007.Camera;
import scripts.usa.api2007.Walking;
import scripts.usa.api2007.util.training.Trainer;
import scripts.usa.api2007.util.training.TrainingLevel;
import scripts.usa.api2007.Consumables;
import scripts.webwalker_logic.WebWalker;

@ScriptManifest(authors = { "Usa" }, category = "ABC", name = "Usa Fighter")
public class UsaFighter extends Script implements Painting, Ending, MessageListening07 {

    private boolean running = false;
    private FxLoader gui;

    private Tracking tracking;
    private Trainer trainer;
    private Tracker tracker;
    private Targets target;
    private int interactions = 0;
    private int death = 0;

    private String[] names = new String[0];
    private Looting looter;
    private RSTile startTile = Player.getPosition();
    private int combatRadius;
    private int lootRadius;
    private Positionable[] available;
    private Positionable[] targets;
    private String foodName;
    private int foodQuantity;
    private int eatPercentage;
    private AutoResponder responder;

    private void init() {
	// ABC.setSleepReaction(false);
	// ABC.alwaysHover();
	// ABC.alwaysOpenMenu();

	try {
	    URL fxml = new URL("http://usa-tribot.org/fxml/UsaFighter.fxml");
	    URL css = new URL("http://usa-tribot.org/fxml/UsaFighter.css");
	    gui = new FxLoader(this.getClass(), fxml, css, true);
	} catch (MalformedURLException e) {
	    e.printStackTrace();
	}

	while (!WorldHopper.isInGame())
	    General.sleep(100);

	gui.getController().startTileText.setText(Player.getPosition().toString());

	while (gui.isShowing()) {
	    startTile = FxUtil.parseTile(gui.getController().startTileText.getText());
	    combatRadius = (int) gui.getController().npcDistanceSlider.getValue();
	    lootRadius = (int) gui.getController().lootDistanceSlider.getValue();
	    General.sleep(200);
	}

	setGUI();
	Thread tracking = new Thread(new Tracking());
	tracking.setName("Tracking");
	tracking.start();
	Camera.setCameraAngle(100);
	tracker = new Tracker();
	running = true;
    }

    public void run() {
	init();
	while (running) {
	    trainer.setSkill();
	    ABC.activateRun();
	    if (shouldEat()) {
		Consumables.eat();
	    }
	    if (shouldBank()) {
		bank();
	    } else {
		if (Player.getPosition().distanceTo(startTile) > combatRadius) {
		    Walking.travel(startTile, 1);
		} else {
		    if (looter != null && looter.getStyle() == LootingStyle.ALWAYS_LOOT
			    && looter.findLoot().length > 0) {
			if (Inventory.isFull()) {
			    Consumables.eat();
			}
			looter.loot();
		    } else {
			if (Npc.interact("Attack", target, new NpcPreferences(true, true), new Conditional(
				Conditions.ActivePlayer(), Conditions.NpcInteractingWithPlayer(false)))) {
			    interactions++;
			    if (looter != null) {
				if (looter.getStyle() == LootingStyle.LOOT_AFTER_KILL)
				    looter.loot();
			    }
			}
		    }
		}
	    }
	    ABC.performAntiban();
	    sleep(50, 100);
	}
    }

    private boolean shouldEat() {
	return ABC.shouldEat() || Combat.getHPRatio() < eatPercentage;
    }

    private boolean shouldBank() {
	if (looter != null && Inventory.isFull() && !Consumables.hasFood()) {
	    return true;
	}
	if (shouldEat() && !Consumables.hasFood()) {
	    return true;
	}
	if (Banking.isInBank() && Inventory.getCount(foodName) < foodQuantity) {
	    return true;
	}
	return false;
    }

    private void bank() {
	if (Banking.isInBank()) {
	    if (Banking.openBank()) {
		if (Banking.depositExcept(foodName)) {
		    Banking.withdraw(foodQuantity, foodName);
		}
	    }
	} else {
	    Walking.travelToBank();
	}
    }

    private ArrayList<RSTile> getTilesInRadius(RSTile center, int radius) {
	ArrayList<RSTile> tiles = new ArrayList<RSTile>();
	int diameter = (1 + (2 * radius));
	int plane = center.getPlane();
	int x = center.getX() - radius;
	int y = center.getY() + radius;
	for (int i = 0; i < diameter; i++) {
	    x = center.getX() - radius;
	    for (int j = 0; j < diameter; j++) {
		tiles.add(new RSTile(x, y, plane));
		x += 1;
	    }
	    y -= 1;
	}
	return tiles;
    }

    private boolean hasEquippableItems() {
	return getEquippableItems().size() > 0;
    }

    private List<RSItem> getEquippableItems() {
	final String[] ITEM_ACTIONS = { "Wear", "Wield", "Equip" };
	List<RSItem> items = new ArrayList<RSItem>();
	for (RSItem item : Inventory.getAll()) {
	    RSItemDefinition definition = item.getDefinition();
	    if (definition == null)
		continue;
	    String[] actions = definition.getActions();
	    if (actions == null || actions.length == 0)
		continue;
	    for (String action : ITEM_ACTIONS) {
		if (Arrays.asList(actions).contains(action)) {
		    items.add(item);
		    break;
		}
	    }
	}
	return items;
    }

    private void equipItems() {
	while (hasEquippableItems()) {
	    List<RSItem> items = getEquippableItems();
	    for (RSItem item : items) {
		if (Inventory.open()) {
		    if (item.click()) {
			sleep(General.randomSD(750, 250));
		    }
		}
	    }
	    sleep(50);
	}
    }

    public void onPaint(Graphics g) {
	try {
	    Graphics2D g2 = (Graphics2D) g;
	    RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,
		    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	    g2.setRenderingHints(rh);
	    g2.setColor(new Color(255, 0, 0, 150));

	    if (!running) {
		if (startTile != null) {
		    List<RSTile> tiles;
		    g2.setColor(new Color(255, 255, 255, 50));
		    tiles = getTilesInRadius(startTile, combatRadius);
		    for (RSTile tile : tiles) {
			if (tile.isOnScreen())
			    g2.fillPolygon(Projection.getTileBoundsPoly(tile, 0));
			Point point = Projection.tileToMinimap(tile);
			if (Projection.isInMinimap(point))
			    g2.fillRect(point.x - 1, point.y - 1, 2, 2);

		    }
		    g2.setColor(new Color(0, 255, 0, 50));
		    tiles = getTilesInRadius(Player.getPosition(), lootRadius);
		    for (RSTile tile : tiles) {
			if (tile.isOnScreen())
			    g2.fillPolygon(Projection.getTileBoundsPoly(tile, 0));
			Point point = Projection.tileToMinimap(tile);
			if (Projection.isInMinimap(point))
			    g2.fillRect(point.x - 1, point.y - 1, 2, 2);
		    }
		}
		return;
	    }

	    if (available != null && available.length > 0) {
		for (Positionable target : available) {
		    RSModel m = ((RSAnimableEntity) target).getModel();
		    if (m != null && m.isClickable()) {
			Polygon a = m.getEnclosedArea();
			if (a != null)
			    g2.fillPolygon(a);
		    }
		}
	    }

	    if (targets != null && targets.length > 0) {
		g2.setColor(new Color(0, 255, 0, 150));
		if (targets instanceof RSNPC[]) {
		    for (Positionable target : targets) {
			RSModel model = ((RSAnimableEntity) target).getModel();
			if (model != null && model.isClickable()) {
			    Polygon area = model.getEnclosedArea();
			    if (area != null)
				g2.fillPolygon(area);
			}
		    }
		} else if (targets instanceof RSObject[]) {
		    for (Positionable target : targets) {
			RSModel model = ((RSObject) target).getModel();
			if (model != null && model.isClickable()) {
			    Polygon area = model.getEnclosedArea();
			    if (area != null)
				g2.fillPolygon(area);
			}
		    }
		}
	    }

	    if (Npc.current != null) {
		g2.setColor(new Color(255, 255, 255, 50));
		Positionable entity = Npc.current;
		if (entity != null) {
		    RSModel model = null;
		    if (entity instanceof RSNPC) {
			model = ((RSAnimableEntity) entity).getModel();
		    } else if (entity instanceof RSObject) {
			model = ((RSObject) entity).getModel();
		    }
		    if (model != null && model.isClickable()) {
			Polygon area = model.getEnclosedArea();
			if (area != null)
			    g2.fillPolygon(area);
		    }
		}
	    }

	    if (Walking.getPaintPath() != null) {
		for (RSTile tile : Walking.getPaintPath()) {
		    if (Walking.isTileOnMinimap(tile)) {
			Point point = Projection.tileToMinimap(tile);
			if (point != null) {
			    g2.setColor(new Color(50, 200, 50, 255));
			    g2.fillRect(point.x, point.y, 2, 2);
			}
			if (tile.isOnScreen()) {
			    Polygon poly = Projection.getTileBoundsPoly(tile, 0);
			    if (poly != null) {
				g2.setColor(new Color(50, 200, 50, 50));
				g2.fillPolygon(poly);
				g2.setColor(new Color(50, 200, 50, 255));
				g2.drawPolygon(poly);
			    }
			}
		    }
		}
	    }

	    g2.setFont(new Font("Consolas", Font.PLAIN, 12));
	    Color background = new Color(120, 27, 10, 230);
	    Color border = new Color(0, 0, 0, 255);
	    Color text = Color.WHITE;
	    int height = 15;
	    int width = 200;
	    int spacing = 20;
	    int x;
	    int y;

	    ArrayList<String> list = new ArrayList<String>();
	    x = 88;
	    y = 363;
	    list.add("      USA ABC2 Fighter");
	    list.add("Time: " + Timing.msToString(tracker.getElapsedTime()));
	    list.add("Hover: " + ABC.shouldHover() + " | Menu: " + ABC.shouldOpenMenu());
	    list.add("Reaction Sleep: " + ABC.getReactionTime() + " ms");
	    list.add("Interacting with " + ((Player.getRSPlayer().getInteractingCharacter() != null)
		    ? Player.getRSPlayer().getInteractingCharacter().getName() : "null"));
	    for (String print : list) {
		g2.setColor(background);
		g2.fillRoundRect(x, y - height, width, height, 4, 4);
		g2.setColor(border);
		g2.drawRoundRect(x, y - height, width, height, 4, 4);
		g2.setColor(text);
		g2.drawString(print, x + 3, y - 3);
		y += spacing;
	    }

	    list.clear();
	    x += width + 5;
	    y = 363;
	    list.add("Combat Level: " + Player.getRSPlayer().getCombatLevel());
	    list.add("XP: " + tracker.getXPGained() + " (" + tracker.getXPPerHour() + "/hr)");
	    list.add("Kills: " + TabletsPaint.format(interactions));
	    list.add("Levels Gained: " + tracker.getLevelsGained());
	    int profitPerHour = looter == null ? 0 : (int) (looter.getProfit() * 3600000D / tracker.getElapsedTime());
	    list.add("Profit: " + TabletsPaint.format(looter.getProfit()) + " (" + TabletsPaint.format(profitPerHour) + "/hr)");
	    for (String print : list) {
		g2.setColor(background);
		g2.fillRoundRect(x, y - height, width, height, 4, 4);
		g2.setColor(border);
		g2.drawRoundRect(x, y - height, width, height, 4, 4);
		g2.setColor(text);
		g2.drawString(print, x + 3, y - 3);
		y += spacing;
	    }
	} catch (Exception e) {
	    System.out.println(e);
	}
    }

    public class Tracking implements Runnable {

	private volatile boolean stop = false;

	public void run() {
	    while (!stop) {
		try {
		    if (Login.getLoginState() == Login.STATE.INGAME && targets != null)
			targets = target.getNPCS();
		} catch (Exception ex) {
		    ex.printStackTrace();
		}
		sleep(0, 50);
	    }
	}

	public void setStop(boolean stop) {
	    System.out.println("Stopped Threat Searching thread.");
	    this.stop = stop;
	}
    }

    public void onEnd() {
	Platform.runLater(new Runnable() {
	    public void run() {
		if (gui != null && gui.isShowing())
		    gui.close();
	    }
	});
	if (tracking != null)
	    tracking.setStop(true);
    }

    @Override
    public void clanMessageReceived(String username, String message) {
    }

    @Override
    public void duelRequestReceived(String username, String message) {
    }

    @Override
    public void personalMessageReceived(String username, String message) {
    }

    @Override
    public void playerMessageReceived(String username, String message) {
	if (responder != null)
	    responder.generateResponse(username, message);
    }

    @Override
    public void serverMessageReceived(String message) {
	if (message.equalsIgnoreCase("Oh dear, you are dead!")) {
	    death++;
	    equipItems();
	    trainer.setAttackStyle();
	}
    }

    @Override
    public void tradeRequestReceived(String username) {
    }

    private void setGUI() {
	startTile = FxUtil.parseTile(gui.getController().startTileText.getText());
	combatRadius = (int) gui.getController().npcDistanceSlider.getValue();
	names = gui.getController().selectedNpcsList.getItems().stream().toArray(String[]::new);
	target = new Targets(names);
	target.setArea(startTile, combatRadius);
	if (gui.getController().trainingOption.getSelectionModel().getSelectedItem() == "Skill Level") {
	    HashMap<SKILLS, TrainingLevel> map = new HashMap<SKILLS, TrainingLevel>();
	    gui.getController().trainingTable.getItems().stream().forEach(row -> {
		map.put(row.getSkill(), new TrainingLevel(Integer.parseInt(row.getLevel())));
	    });
	    trainer = new Trainer(map);
	} else {
	    trainer = new Trainer();
	}
	looter = new Looting();
	looter.setMinValue(gui.getController().minLootSpinner.getValue());
	looter.setMaxValue(gui.getController().maxLootSpinner.getValue());
	looter.setRadius((int) gui.getController().lootDistanceSlider.getValue());
	gui.getController().lootList.getItems().stream().forEach(name -> {
	    looter.put(new LootItem(name, 0, 0, 0, true));
	});
	if (gui.getController().lootingStyleOption.getSelectionModel().getSelectedItem() == "Always Loot") {
	    looter.setLootingStyle(LootingStyle.ALWAYS_LOOT);
	} else {
	    looter.setLootingStyle(LootingStyle.LOOT_AFTER_KILL);
	}
	foodName = gui.getController().foodText.getText();
	foodQuantity = gui.getController().foodSpinner.getValue();
	eatPercentage = (int) gui.getController().eatSlider.getValue();
	if (gui.getController().autoResponder.isSelected())
	    responder = new AutoResponder();
	ABC.setSleepReaction(gui.getController().ABCL.isSelected());
    }
}
