package scripts.starfox.api2007.walking;

import java.awt.Point;
import java.awt.Rectangle;
import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Player;
import org.tribot.api2007.Trading;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceComponent;
import scripts.starfox.api.Client;
import scripts.starfox.api.util.ArrayUtil;
import scripts.starfox.api2007.Inventory07;
import scripts.starfox.api2007.Player07;
import scripts.starfox.api2007.Trading07;
import scripts.starfox.interfaces.ui.Listable;

/**
 * @author Starfox
 */
public enum TeleportOption
        implements Listable {

    //Tabs
    VARROCK_TAB(TeleportType.TAB, 8007, "Varrock Tab", "Varrock"),
    LUMBRIDGE_TAB(TeleportType.TAB, 8008, "Lumbridge Tab", "Lumbridge"),
    FALADOR_TAB(TeleportType.TAB, 8009, "Falador Tab", "Falador"),
    CAMELOT_TAB(TeleportType.TAB, 8010, "Camelot Tab", "Camelot"),
    //HOUSE_TAB("House Tab (Portal)", "Your House (Portal)"),
    //Minigames
    BARBARIAN_ASSAULT(TeleportType.MINIGAME, 1, "Barbarian Assault"),
    BLAST_FURNACE(TeleportType.MINIGAME, 2, "Blast Furnace"),
    CASTLE_WARS(TeleportType.MINIGAME, 3, "Castle Wars"),
    FISHING_TRAWLER(TeleportType.MINIGAME, 4, "Fishing Trawler"),
    BURTHORPE_GAMES_ROOM(TeleportType.MINIGAME, 5, "Burthorpe Games Room"),
    NIGHTMARE_ZONE(TeleportType.MINIGAME, 6, "Nightmare Zone"),
    PEST_CONTROL(TeleportType.MINIGAME, 7, "Pest Control"),
    RAT_PITS(TeleportType.MINIGAME, 8, "Rat Pits"),
    SHADES_OF_MORTTON(TeleportType.MINIGAME, 9, "Shades of Mort'ton"),
    TROUBLE_BREWING(TeleportType.MINIGAME, 10, "Trouble Brewing"),
    TZHAAR_FIGHT_PIT(TeleportType.MINIGAME, 11, "TzHaar Fight Pit"),
    PHOENIX_GANG(TeleportType.MINIGAME, 12, "Phoenix Gang members"),
    BLACKARM_GANG(TeleportType.MINIGAME, 13, "Black Arm Gang members"),
    GODWARS(TeleportType.MINIGAME, 14, "Godwars"),
    DAGGANOTH_KINGS(TeleportType.MINIGAME, 15, "Dagannoth Kings"),
    PLAYER_OWNED_HOUSE(TeleportType.MINIGAME, 16, "Player Owned Houses");

    private static final long COOLDOWN = 1230000;
    private static long lastTeleportTime = 0;

    private final int[] minigameAnims = {4847, 4850, 4853, 4855, 4857};

    private final TeleportType type;
    private final String listName;
    private final String fullName;
    private final int id;

    TeleportOption(final TeleportType type, final int id, final String listName, final String fullName) {
        this.type = type;
        this.listName = listName;
        this.fullName = fullName;
        this.id = id;
    }

    TeleportOption(final TeleportType type, final int id, final String name) {
        this.type = type;
        this.listName = name;
        this.fullName = name;
        this.id = id;
    }

    public final String getFullName() {
        return fullName;
    }

    public final int getId() {
        return id;
    }

    public final TeleportType getType() {
        return type;
    }

    @Override
    public String getListDisplay() {
        return listName;
    }

    /**
     * Teleports using this TeleportOption.
     *
     * @return Returns true if the teleport was successful, false otherwise.
     */
    public boolean teleport() {
        switch (type) {
            case TAB:
                boolean didTele = teleportTab();
                General.sleep(500);
                return didTele;
            case MINIGAME:
                if (teleportMinigame()) {
                    General.sleep(1500);
                    lastTeleportTime = System.currentTimeMillis();
                    return true;
                } else {
                    return false;
                }
        }
        return false;
    }

    /**
     * Teleports this TeleportOption. This TeleportOption MUST be a teleport tablet.
     *
     * @return true if the teleport was successful, false otherwise.
     */
    private boolean teleportTab() {
        final Point base;
        switch (this) {
            case VARROCK_TAB:
                base = new Point(3212, 3423);
                break;
            case LUMBRIDGE_TAB:
                base = new Point(3221, 3219);
                break;
            case FALADOR_TAB:
                base = new Point(2965, 3378);
                break;
            case CAMELOT_TAB:
                base = new Point(2757, 3478);
                break;
//            case HOUSE_TAB:
//                id = 8013;
//                base = new Point(2757, 3478);
//                break;
            default:
                return false;
        }
        int xDiff = Math.abs(base.x - Player07.getPosition().getX());
        int yDiff = Math.abs(base.y - Player07.getPosition().getY());
        if (xDiff < 7 && yDiff < 7) {
            return true;
        }
        Interfaces.closeAll();
        if (Trading07.isOpen()) {
            Trading.close();
        }
        
        if (!Inventory07.contains(id) || !Clicking.click("Break", Inventory07.getItem(id))) {
            return false;
        }
        //You are also going to have to check and make sure that you aren't in a random event here.
        return Timing.waitCondition(new Condition() {
            @Override
            public boolean active() {
                int xDiff = Math.abs(base.x - Player07.getPosition().getX());
                int yDiff = Math.abs(base.y - Player07.getPosition().getY());
                return xDiff < 7 && yDiff < 7;
            }
        }, 5000);
    }

    private boolean teleportMinigame() {
        if (isOnCooldown()) {
            return false;
        }
        int index = id;
        for (int i = 0; i < 5 && !GameTab.TABS.QUESTS.isOpen(); i++) {
            Keyboard.pressFunctionKey(3);
            if (!Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    return GameTab.TABS.QUESTS.isOpen();
                }
            }, 500)) {
                return false;
            }
        }
        if (GameTab.TABS.QUESTS.isOpen() && Interfaces.get(76) == null) {
            RSInterfaceChild button = null;
            if (Interfaces.get(274) != null) {
                button = Interfaces.get(274, 12);
            } else if (Interfaces.get(259) != null) {
                button = Interfaces.get(259, 11);
            }
            if (button != null && button.click()) {
                Timing.waitCondition(new Condition() {
                    @Override
                    public boolean active() {
                        return Interfaces.get(76) != null;
                    }
                }, 4000);
            }
        }
        if (Interfaces.get(76) != null) {
            boolean selected = false;
            RSInterfaceChild menuText = Interfaces.get(76, 9);
            if (menuText != null) {
                String text = menuText.getText();
                if (text != null) {
                    if (text.equalsIgnoreCase(getFullName())) {
                        selected = true;
                    }
                    if (!text.contains("Select")) {
                        for (TeleportOption m : TeleportOption.values()) {
                            if (m.getFullName().equalsIgnoreCase(text)) {
                                index = m.getId();
                            }
                        }
                    }
                }
            }
            if (!selected) {
                final RSInterfaceChild menuButton = Interfaces.get(76, 8);
                final RSInterfaceChild menu = Interfaces.get(76, 15);
                if (menu != null && menu.isHidden()) {
                    if (menuButton != null && menuButton.click()) {
                        Timing.waitCondition(new Condition() {
                            @Override
                            public boolean active() {
                                return !menu.isHidden();
                            }
                        }, 4000);
                    }
                }
                if (menu != null && !menu.isHidden()) {
                    final RSInterfaceChild realMenu = Interfaces.get(76, 19);
                    if (realMenu != null) {
                        final RSInterfaceComponent[] list = realMenu.getChildren();
                        if (getId() > 13) {
                            final RSInterfaceChild scrollBox = Interfaces.get(76, 14);
                            if (scrollBox != null) {
                                final RSInterfaceComponent scrollBar = scrollBox.getChild(1);
                                if (scrollBar != null) {
                                    Rectangle r = scrollBar.getAbsoluteBounds();
                                    if (r != null) {
                                        Point center = new Point((int) r.getCenterX(), (int) r.getCenterY());
                                        Mouse.drag(center, new Point(center.x, center.y + 20), 1);
                                    }
                                }
                            }
                        }
                        if (list != null) {
                            int realIndex = getId();
                            if (getId() >= index) {
                                realIndex = getId() - 1;
                            }
                            if (list[realIndex].click()) {
                                Timing.waitCondition(new Condition() {
                                    @Override
                                    public boolean active() {
                                        final RSInterfaceChild menuText = Interfaces.get(76, 9);
                                        if (menuText != null) {
                                            String text = menuText.getText();
                                            return text != null && text.equalsIgnoreCase(getFullName());
                                        }
                                        return false;
                                    }
                                }, 4000);
                            }
                        }
                    }
                }
            }
            menuText = Interfaces.get(76, 9);
            if (menuText != null) {
                String text = menuText.getText();
                if (text != null && text.equalsIgnoreCase(getFullName())) {
                    final RSInterfaceChild teleport = Interfaces.get(76, 25);
                    if (teleport != null) {
                        if (Clicking.click(teleport)) {
                            Client.println("Clicked Teleport");
                            int currentX = Player07.getPosition().getX();
                            int currentY = Player07.getPosition().getY();
                            if (Timing.waitCondition(new Condition() {
                                @Override
                                public boolean active() {
                                    return ArrayUtil.contains(Player.getAnimation(), minigameAnims);
                                }
                            }, 3000)) {
                                Timing.waitCondition(new Condition() {
                                    @Override
                                    public boolean active() {
                                        General.sleep(50);
                                        return !ArrayUtil.contains(Player.getAnimation(), minigameAnims);
                                    }
                                }, 15000);
                                return !new Point(currentX, currentY).equals(new Point(Player07.getPosition().getX(), Player07.getPosition().getY()));
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean isOnCooldown() {
        return type == TeleportType.MINIGAME && System.currentTimeMillis() - lastTeleportTime <= COOLDOWN;
    }

    @Override
    public String searchName() {
        return getListDisplay();
    }

    @Override
    public String getPulldownDisplay() {
        return getListDisplay();
    }
}
