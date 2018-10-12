package scripts.starfox.graphics;

import org.tribot.api.interfaces.Clickable;
import org.tribot.api2007.Projection;
import org.tribot.api2007.types.*;

import java.awt.*;
import java.util.List;

/**
 * The Drawing class is a utility class that safely draws entities on the screen.
 *
 * @author Nolan
 */
public class Drawing {

    /**
     * Draws the specified {@link Clickable} onto the screen.
     * This method cannot draw {@link RSGEOffer}'s.
     *
     * @param g         The {@link Graphics} to render with.
     * @param color     The {@link Color} to use.
     * @param clickable The {@link Clickable} to draw.
     */
    public static void draw(Graphics g, Color color, Clickable clickable) {
        if (clickable == null || clickable instanceof RSGEOffer) {
            return;
        }
        if (clickable instanceof RSItem) {
            RSItem item = (RSItem) clickable;
            drawRectangle(g, item.getArea(), color);
        } else if (clickable instanceof RSTile) {
            RSTile tile = (RSTile) clickable;
            if (tile.isOnScreen()) {
                drawPolygon(g, Projection.getTileBoundsPoly(tile, 0), color);
            }
        } else if (clickable instanceof RSObject) {
            RSObject object = (RSObject) clickable;
            if (object.isOnScreen()) {
                drawModel(g, object.getModel(), color);
            }
        } else if (clickable instanceof RSCharacter) {
            RSCharacter character = (RSCharacter) clickable;
            if (character.isOnScreen()) {
                drawModel(g, character.getModel(), color);
            }
        } else if (clickable instanceof RSInterface) {
            RSInterface rsInterface = (RSInterface) clickable;
            drawRectangle(g, rsInterface.getAbsoluteBounds(), color);
        } else if (clickable instanceof RSGroundItem) {
            RSGroundItem groundItem = (RSGroundItem) clickable;
            drawModel(g, groundItem.getModel(), color);
        }
    }

    /**
     * Draws the specified {@link RSModel} onto the screen.
     *
     * @param g     The {@link Graphics} to render with.
     * @param model The {@link RSModel} to draw.
     * @param color The {@link Color} to use.
     */
    public static void drawModel(Graphics g, RSModel model, Color color) {
        if (model != null) {
            Polygon poly = model.getEnclosedArea();
            if (poly != null) {
                g.setColor(color);
                g.drawPolygon(poly);
            }
        }
    }

    /**
     * Draws the specified {@link Polygon} onto the screen.
     *
     * @param g       The {@link Graphics} to render with.
     * @param polygon The {@link Polygon} to draw.
     * @param color   The {@link Color} to draw with.
     */
    public static void drawPolygon(Graphics g, Polygon polygon, Color color) {
        if (polygon == null) {
            return;
        }
        g.setColor(color);
        g.drawPolygon(polygon);
    }

    /**
     * Draws the specified {@link Rectangle} onto the screen.
     *
     * @param g         The {@link Graphics} to render with.
     * @param rectangle The {@link Rectangle} to draw.
     * @param color     The {@link Color} to draw with.
     */
    public static void drawRectangle(Graphics g, Rectangle rectangle, Color color) {
        if (rectangle == null) {
            return;
        }
        g.setColor(color);
        ((Graphics2D) g).draw(rectangle);
    }

    /**
     * Draws the specified tile on the mini-map if it is in the mini-map.
     *
     * @param g     The {@link Graphics} to render with.
     * @param tile  The tile.
     * @param color The color to use.
     */
    public static void drawTileMM(Graphics g, RSTile tile, Color color) {
        if (tile != null) {
            Point tilePoint = new Point(Projection.tileToMinimap(tile));
            if (Projection.isInMinimap(tilePoint)) {
                g.setColor(color);
                g.fillRect(tilePoint.x, tilePoint.y, 6, 6);
            }
        }
    }

    /**
     * Draws the specified tile if it is on screen.
     *
     *
     * @param g      The {@link Graphics} to render with.
     * @param tile   The tile.
     * @param outer  The outer color.
     *               Provide null for no outer color.
     * @param filler The filler color.
     *               Provide null for no inner color.
     */
    public static void drawTile(Graphics g, RSTile tile, Color outer, Color filler) {
        if (tile != null && tile.isOnScreen()) {
            Polygon poly = Projection.getTileBoundsPoly(tile, 0);
            if (poly != null) {
                if (filler != null) {
                    g.setColor(filler);
                    g.fillPolygon(poly);
                }
                if (outer != null) {
                    g.setColor(outer);
                    g.drawPolygon(poly);
                }
            }
        }
    }

    /**
     * Draws each tile on the specified path.
     *
     * @param g      The {@link Graphics} to render with.
     * @param outer  The outer color.
     *               Provide null for no outer color.
     * @param filler The filler color.
     *               Provide null for no filler color.
     * @param tiles  The path.
     */
    public static void drawPath(Graphics g, Color outer, Color filler, RSTile... tiles) {
        if (tiles != null) {
            for (RSTile tile : tiles) {
                drawTile(g, tile, outer, filler);
            }
        }
    }

    /**
     * Draws each tile on the specified path.
     *
     * @param g      TThe {@link Graphics} to render with.
     * @param outer  The outer color.
     *               Provide null for no outer color.
     * @param filler The filler color.
     *               Provide null for no filler color.
     * @param tiles  The path.
     */
    public static void drawPath(Graphics g, Color outer, Color filler, List<RSTile> tiles) {
        if (tiles != null) {
            for (RSTile tile : tiles) {
                drawTile(g, tile, outer, filler);
            }
        }
    }

    /**
     * Draws a line to each tile on the specified path.
     *
     * @param g     The {@link Graphics} to render with.
     * @param tiles The path.
     */
    public static void drawPathLines(Graphics g, List<RSTile> tiles) {
        for (int i = 0; i < tiles.size() - 1; i++) {
            final RSTile one = tiles.get(i);
            final RSTile two = tiles.get(i + 1);
            g.drawLine(Projection.tileToScreen(one, 0).x, Projection.tileToScreen(one, 0).y,
                    Projection.tileToScreen(two, 0).x, Projection.tileToScreen(two, 0).y);
        }
    }

    /**
     * Draws an area on the mini-map and screen bounded by the specified tiles. Note that these tiles SHOULD NOT be all RSTiles contained in the area, but the
     * RSTiles that are used to <i>create</i> the area.
     *
     *
     * @param g             The graphics object being used to draw the area.
     * @param tiles         The tiles that make up the bounds of the area (not the area itself).
     * @param boundingColor The color of the border of the area.
     * @param fillColor     The color of the fill of the area.
     * @param mmColor       The color of the tiles that are drawn on the mini-map.
     * @see #drawAreaOutline(Graphics, RSTile[], Color, Color)
     * @see #drawAreaOutlineMM(Graphics, RSTile[], Color)
     */
    public static void drawAreaOutlineBoth(Graphics g, RSTile[] tiles, Color boundingColor, Color fillColor, Color mmColor) {
        drawAreaOutline(g, tiles, boundingColor, fillColor);
        drawAreaOutlineMM(g, tiles, mmColor);
    }

    /**
     * Draws an area on the screen bounded by the specified tiles. Note that these tiles SHOULD NOT be all RSTiles contained in the area, but the RSTiles that
     * are used to <i>create</i> the area.
     *
     *
     * @param g             The graphics object being used to draw the area.
     * @param tiles         The tiles that make up the bounds of the area (not the area itself).
     * @param boundingColor The color of the border of the area.
     * @param fillColor     The color of the fill of the area.
     */
    public static void drawAreaOutline(Graphics g, RSTile[] tiles, Color boundingColor, Color fillColor) {
        if (tiles != null) {
            RSArea area = new RSArea(tiles);
            for (RSTile tile : area.getAllTiles()) {
                drawTile(g, tile, null, fillColor);
            }
            Polygon screenPoly = new Polygon();
            for (RSTile tile : tiles) {
                Point screenPt = Projection.tileToScreen(tile, 0);
                screenPoly.addPoint(screenPt.x, screenPt.y);
            }
            if (boundingColor != null) {
                g.setColor(boundingColor);
            }
            g.drawPolygon(screenPoly);
        }
    }

    /**
     * Draws an area on the mini-map bounded by the specified tiles. Note that these tiles SHOULD NOT be all RSTiles contained in the area, but the RSTiles that
     * are used to <i>create</i> the area.
     *
     *
     * @param g       The graphics object being used to draw the area.
     * @param tiles   The tiles that make up the bounds of the area (not the area itself).
     * @param mmColor The color of the tiles that are drawn on the mini-map.
     */
    public static void drawAreaOutlineMM(Graphics g, RSTile[] tiles, Color mmColor) {
        if (tiles != null) {
            Graphics gg = g.create();
            RSArea area = new RSArea(tiles);
            drawAreaMM(gg, area, mmColor, false);
            gg.dispose();
        }
    }

    /**
     * Draws each tile in the specified area on the mini-map.
     *
     *
     * @param g      The graphics object to render with.
     * @param area   The area to draw.
     * @param color  The color to use.
     * @param scarce True if the tiles drawn should be spaced out to save CPU, false otherwise.
     */
    public static void drawAreaMM(Graphics g, RSArea area, Color color, boolean scarce) {
        if (area != null && color != null) {
            Graphics gg = g.create();
            final RSTile[] tiles = area.getAllTiles();
            for (int i = 0; i < tiles.length - 1; i += (scarce ? 3 : 1)) {
                final RSTile tile = tiles[i];
                Point tilePoint = Projection.tileToMinimap(tile);
                if (tilePoint != null && Projection.isInMinimap(tilePoint)) {
                    drawTileMM(gg, tile, color);
                }
            }
            gg.dispose();
        }
    }
}
