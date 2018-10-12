package scripts;

import java.awt.Color;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.colour.ColourPoint;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api.util.ABCUtil;
import org.tribot.api.util.Screenshots;
import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.rs3.LodestoneNetwork.LOCATIONS;
import org.tribot.api2007.Combat;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.Game;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.NPCChat;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Objects;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.Projection;
import org.tribot.api2007.Screen;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Walking;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSCharacter;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceComponent;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.api2007.types.RSModel;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSNPCDefinition;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSObject.TYPES;
import org.tribot.api2007.types.RSObjectDefinition;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.api2007.types.RSTile;
import org.tribot.api2007.util.DPathNavigator;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.MouseActions;
import org.tribot.script.interfaces.Painting;
import org.tribot.util.Util;
import org.w3c.dom.Document;

import com.sun.glass.events.KeyEvent;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import scripts.usa.api.GrandExchange.GrandExchange;
import scripts.usa.api.GrandExchange.STATUS;
import scripts.usa.api.antiban.ABC;
import scripts.usa.api.antiban.responder.AutoResponder;
import scripts.usa.api.condition.Conditional;
import scripts.usa.api.condition.Conditions;
import scripts.usa.api.wagu.Block;
import scripts.usa.api.wagu.Board;
import scripts.usa.api.wagu.Table;
import scripts.usa.api.web.captcha.AccountCreator;
import scripts.usa.api.web.methods.Web;
import scripts.usa.data.Location;
import scripts.api.v1.api.banking.Bank;
import scripts.api.v1.api.entity.Entity;
import scripts.api.v1.api.entity.Targets;
import scripts.api.v1.api.entity.Entity.Types;
import scripts.api.v1.api.items.Consumables;
import scripts.api.v1.api.wilderness.Wilderness;
import scripts.api.v1.api.worlds.LOGIN_WORLD;
import scripts.api.v1.api.worlds.SORTING;
import scripts.api.v1.api.worlds.TYPE;
import scripts.api.v1.api.worlds.WORLD;
import scripts.api.v1.api.worlds.WorldHopper;
import scripts.api.v1.api2.Walk;
import scripts.api.v1.api2.Walk.Style;
import scripts.lava.CombatSpell.CastingStyle;
import scripts.usa.api.GameSettings.GameSettings;
import scripts.usa.api.GameSettings.RunOptions;

@ScriptManifest(authors = { "Usa" }, category = "Test", name = "Test")
public class Test extends Script implements Painting, MouseActions {

    public List<WORLD> worlds = new ArrayList<WORLD>();

    public void run() {

	// super.setLoginBotState(false);

	RSTile end = new RSTile(3097, 3485, 0);

	RSTile[] path = Walk.generatePath(end, 0);

	if (Walk.Path(Style.Screen, path)) {
	    println("End");
	}

	while (true) {
	    sleep(500);
	}

    }

    @Override
    public void onPaint(Graphics g) {
	Graphics2D g2 = (Graphics2D) g;
	RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,
		RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	g2.setRenderingHints(rh);
	for (WORLD world : worlds) {
	    g2.setColor(Color.BLACK);
	    g2.fillRect(world.getWorldSelectBounds().x, world.getWorldSelectBounds().y,
		    world.getWorldSelectBounds().width, world.getWorldSelectBounds().height);
	    g2.setColor(Color.WHITE);
	    g2.drawString(Integer.toString(world.getNumber()), world.getWorldSelectBounds().x + 10,
		    world.getWorldSelectBounds().y + 14);
	}
	if (Walk.getPaintPath().length > 0) {
	    for (RSTile tile : Walk.getPaintPath()) {
		if (Walk.isTileOnMinimap(tile)) {
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
    }

    @Override
    public void mouseClicked(Point arg0, int arg1, boolean arg2) {
	// println(arg0);
    }

    @Override
    public void mouseDragged(Point arg0, int arg1, boolean arg2) {
	// TODO Auto-generated method stub
    }

    @Override
    public void mouseMoved(Point arg0, boolean arg1) {
	// TODO Auto-generated method stub
    }

    @Override
    public void mouseReleased(Point arg0, int arg1, boolean arg2) {
	// TODO Auto-generated method stub
    }
}