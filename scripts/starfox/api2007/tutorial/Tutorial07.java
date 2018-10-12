package scripts.starfox.api2007.tutorial;

import java.awt.Color;
import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.colour.Tolerance;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Combat;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.Game;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Magic;
import org.tribot.api2007.NPCChat;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Objects;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.Screen;
import org.tribot.api2007.Walking;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSObjectDefinition;
import org.tribot.api2007.types.RSTile;
import scripts.starfox.api.AntiBan;
import scripts.starfox.api.Client;
import scripts.starfox.api2007.Interfaces07;
import scripts.starfox.api2007.Inventory07;
import scripts.starfox.api2007.Mouse07;
import scripts.starfox.api2007.Options07;
import scripts.starfox.api2007.Player07;
import scripts.starfox.api2007.banking.Bank07;
import scripts.starfox.api2007.entities.Entities;
import scripts.starfox.api2007.entities.NPCs07;
import scripts.starfox.api2007.entities.Objects07;
import scripts.starfox.api2007.skills.mining.Mining;
import scripts.starfox.api2007.skills.mining.Rock;
import scripts.starfox.api2007.walking.Walking07;
import scripts.starfox.api2007.walking.WalkingConditions;
import scripts.starfox.enums.smithing.Smithable;

enum State {

    MAKE_CHARACTER, TALK_TO_GUIDE, CONTINUE_DIALOGUE, CLICK_WRENCH, LEAVE_FIRST_DOOR, TALK_TO_SURVIVAL, OPEN_INVENTORY, CHOP_TREE,
    MAKE_FIRE, OPEN_STATS, FISH, COOK_FISH, CLICK_CONTINUE, WALK_TO_GATE, TALK_TO_CHEF, MAKE_DOUGH, COOK_DOUGH, OPEN_MUSIC, LEAVE_COOK,
    OPEN_EMOTES, DO_EMOTES, TOGGLE_RUN, OPEN_QUEST_DOOR, TALK_TO_QUEST, OPEN_QUEST, CLIMB_DOWN, TALK_TO_SMITHER, PROSPECT, PROSPECT_TIN,
    MINE_ROCKS, SMELT_ORES, SMITH_DAGGERS, LEAVE_TO_GATE, TALK_TO_COMBAT, OPEN_EQUIPMENT, OPEN_EQUIPMENT_INTERFACE, WIELD_DAGGER,
    UNEQUIP, WIELD_SWORD, OPEN_COMBAT_STYLE, OPEN_RAT_GATE, MELEE_RAT, LEAVE_RAT_PLACE, RANGE_RAT, CLIMB_LADDER_UP, BANK, BANK_CLOSE,
    OPEN_BANK_DOOR, TALK_TO_MONEY_GUY, OPEN_OTHER_BANK_DOOR, OPEN_CHAPEL_DOOR, OPEN_PRAYER, OPEN_FRIENDS, OPEN_IGNORE,
    LEAVE_CHAPEL_AREA, TO_WIZARDS, OPEN_MAGIC, KILL_CHICKEN, FINAL_MOMENT, LOGOUT, LEAVE_SECOND_DOOR, LOST, ANIMATING, CHECK, PROSPECT_COPPER, WAIT_FOR_RAT_DEATH, SELECT_OPTION, TALK_TO_GOD, TALK_TO_WIZARD, WALK_TO_SPOT, STOP_SCRIPT, START_UP, BAD_UPTEXT, LOGIN, MONEY_POLL, CLOSE_INTERFACE
}

public class Tutorial07 {

    private final int[] STEP_PARENT_IDS = {372, 421};
    private final int LOG_ID = 2511;
    private final int TINDER_BOX_ID = 590;
    private final int SHRIMP_ID = 2514;
    private final int BUCKET_OF_WATER_ID = 1929;
    private final int POT_OF_FLOUR_ID = 2516;
    private final int RAW_DOUGH_ID = 2307;
    private final RSTile QUEST_LADDER_TILE = new RSTile(3088, 3119, 0);
    private final int COPPER_ORE_ID = 436;
    private final int TIN_ORE_ID = 438;
    private final int BRONZE_BAR_ID = 2349;
    private final int DAGGER_ID = 1205;
    private final int SWORD_ID = 1277;
    private final int SHIELD_ID = 1171;
    private final String[] NPC_OPTIONS = {"Yes"};

    public Tutorial07() {
        Walking.setWalkingTimeout(1000);
    }

    private boolean isNextStep(String... texts) {
        for (int id : STEP_PARENT_IDS) {
            final RSInterface next_interface = Interfaces.get(id);
            if (next_interface != null) {
                final RSInterface[] childs = next_interface.getChildren();
                if (childs != null && childs.length > 0) {
                    for (RSInterface child : childs) {
                        String interfaceText = child.getText();
                        for (String text : texts) {
                            if (interfaceText != null && interfaceText.contains(text)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private RSInterface getInterfaceByText(String... texts) {
        RSInterface[] all = Interfaces.getAll();
        for (RSInterface next_interface : all) {
            if (next_interface != null) {
                final RSInterface[] childs = next_interface.getChildren();
                if (childs != null && childs.length > 0) {
                    for (RSInterface child : childs) {
                        String interfaceText = child.getText();
                        for (String text : texts) {
                            if (interfaceText != null && interfaceText.contains(text)) {
                                return child;
                            }
                        }
                    }
                }

            }
        }
        return null;
    }

    private boolean isClickContinueUp() {
        Color b = new Color(0, 0, 128);
        Color c = Screen.getColorAt(241, 432);
        return org.tribot.api.Screen.coloursMatch(b, c, new Tolerance(5));
    }

    public String getStatus() {
        return getState().toString();
    }

    private State getState() {
        if (getInterfaceByText("Old School") != null && getInterfaceByText("Question 1") != null) {
            return State.CLOSE_INTERFACE;
        } else if (Interfaces07.isClickContinueUp() || isTutorialClickContinueUp()) {
            return State.CONTINUE_DIALOGUE;
        } else if (Interfaces07.isSelectOptionUp()) {
            return State.SELECT_OPTION;
        } else if (Game.isUptext("->")) {
            return State.BAD_UPTEXT;
        } else if (Interfaces.get(269, 97) != null) {
            return State.MAKE_CHARACTER;
        } else if (isNextStep("To start the tutorial use your left mouse button")) {
            return State.TALK_TO_GUIDE;
        } else if (isNextStep("Please click on the flashing")) {
            return State.CLICK_WRENCH;
        } else if (isNextStep("On the side panel, you can now see")) {
            return State.TALK_TO_GUIDE;
        } else if (isNextStep("You can interact with many items of scenery by")) {
            return State.TALK_TO_SURVIVAL;
        } else if (isNextStep("Talk to the Survival Expert by")) {
            return State.TALK_TO_SURVIVAL;
        } else if (isNextStep("Click on the flashing backpack")) {
            return State.OPEN_INVENTORY;
        } else if (isNextStep("You can click on the backpack icon at any time")) {
            return State.CHOP_TREE;
        } else if (isNextStep("Well done! You managed to cut some log")) {
            return State.MAKE_FIRE;
        } else if (isNextStep("You gained some experience")) {
            return State.OPEN_STATS;
        } else if (isNextStep("Here you will see how good your skills are")) {
            return State.TALK_TO_SURVIVAL;
        } else if (isNextStep("Catch some Shrimp")) {
            return State.FISH;
        } else if (isNextStep("Now you have caught some shrimp", "Now right click", "You have just burnt")) {
            if (Inventory.getCount(SHRIMP_ID) < 2 && isNextStep("Now you have caught some shrimp")) {
                return State.FISH;
            }
            RSObject[] fire = Objects.findNearest(15, "Fire");
            if (fire.length > 0) {
                return State.COOK_FISH;
            } else if (Inventory.find(LOG_ID).length == 0) {
                return State.CHOP_TREE;
            } else {
                return State.MAKE_FIRE;
            }
        } else if (isNextStep("If you'd like a recap on anything you've learnt so far", "Follow the path until you get to the door with the yellow arrow", "Talk to the chef indicated. He will teach you the more advanced")) {
            return State.TALK_TO_CHEF;
        } else if (isNextStep("This is the base for many of the meals")) {
            return State.MAKE_DOUGH;
        } else if (isNextStep("Now you have made dough")) { // too fix
            return State.COOK_DOUGH;
        } else if (isNextStep("Cooking, you will be able to make")) {
            return State.OPEN_MUSIC;
        } else if (isNextStep("The music player.")) {
            return State.LEAVE_COOK;
        } else if (isNextStep("Emotes.")) {
            return State.DO_EMOTES;
        } else if (isNextStep("Running")) {
            return State.TOGGLE_RUN;

        } else if (isNextStep("Now that you have the run button turned on", "Talk with the Quest Guide", "This is your Quest Journal, a list of all the quests")) {
            return State.TALK_TO_QUEST;
        } else if (isNextStep("Open the Quest Journal")) {
            return State.OPEN_QUEST;
        } else if (isNextStep("It's time to enter some caves.")) {
            return State.CLIMB_DOWN;
        } else if (isNextStep("Next let's get you a weapon, or more to the point, you", "Talk to the Mining Instructor to find out", "You've made a bronze bar")) {
            if (Player.getPosition().getY() < 4000) {
                return State.CLIMB_DOWN;
            } else {
                return State.TALK_TO_SMITHER;
            }
        } else if (isNextStep("To prospect a mineable rock, just right click it")) {
            return State.PROSPECT_COPPER;
        } else if (isNextStep("So now you know there's tin in the grey rocks")) {
            return State.PROSPECT_TIN;
        } else if (isNextStep("So now you know there's copper")) {
            return State.PROSPECT_TIN;
        } else if (isNextStep("Mining")) {
            return State.MINE_ROCKS;
        } else if (isNextStep("Smelting")) {
            if (Inventory.find(COPPER_ORE_ID).length > 0 && Inventory.find(TIN_ORE_ID).length > 0) {
                return State.SMELT_ORES;
            } else {
                return State.MINE_ROCKS;
            }
        } else if (isNextStep("Smithing a dagger")) {
            return State.SMITH_DAGGERS;
        } else if (isNextStep("So let's move on. Go through the gates", "In this area you will find out", "You're now holding your dagger", "Well done, you've made your first kill")) {
            return State.TALK_TO_COMBAT;
        } else if (isNextStep("You now have access to a new interface.")) {
            return State.OPEN_EQUIPMENT;
        } else if (isNextStep("From here you can see what items you have equipped")) {
            return State.OPEN_EQUIPMENT_INTERFACE;
        } else if (isNextStep("You can see what items you are wearing")) {
            return State.WIELD_DAGGER;
        } else if (isNextStep("In your worn inventory panel")) {
            return State.WIELD_SWORD;
        } else if (isNextStep("Combat interface")) {
            return State.OPEN_COMBAT_STYLE;
        } else if (isNextStep("Attacking", "Sit back and watch", "From this interface you can select")) {
            return State.MELEE_RAT;
        } else if (isNextStep("Rat ranging")) {
            return State.RANGE_RAT;
        } else if (isNextStep("You have completed the tasks here", "Banking")) {
            return State.BANK;
        } else if (isNextStep("This is your bank box")) {
            return State.MONEY_POLL;
        } else if (isNextStep("This is a poll booth", "The guide here will tell you all about making cash")) {
            return State.TALK_TO_MONEY_GUY;
        } else if (isNextStep("Continue through the next door", "Follow the path to the chapel", "Talk with Brother Brace", "This is your ignore list.")) {
            return State.TALK_TO_GOD;
        } else if (isNextStep("Your Prayer menu")) {
            return State.OPEN_PRAYER;
        } else if (isNextStep("Friends list")) {
            return State.OPEN_FRIENDS;
        } else if (isNextStep("This will be explained by")) {
            return State.OPEN_IGNORE;
        } else if (isNextStep("Your final instructor", "This is your spells list", "You have almost completed the tutorial")) {
            return State.TALK_TO_WIZARD;
        } else if (isNextStep("Open up your final menu")) {
            return State.OPEN_MAGIC;
        } else if (isNextStep("Cast Wind Strike")) {
            return State.KILL_CHICKEN;
        } else if (isNextStep("yolo")) {
            return State.KILL_CHICKEN;
        } else if (!Player07.isInTutorialIsland()) {
            return State.STOP_SCRIPT;
        }
        return State.CHECK;
    }

    public void handleState() {
        switch (getState()) {
            case CLOSE_INTERFACE:
                Interfaces07.closeAll();
                //Mouse.clickBox(485, 21, 500, 35, 1);
                break;
            case CONTINUE_DIALOGUE:
                if (isTutorialClickContinueUp()) {
                    Clicking.click(getClickContinueInterface());
                    Client.sleep(250);
                } else {
                    NPCChat.clickContinue(true);
                }
                break;
            case SELECT_OPTION:
                String[] options = NPCChat.getOptions();
                if (options != null && options.length > 0) {
                    for (String option : options) {
                        for (String option1 : NPC_OPTIONS) {
                            if (option.contains(option1)) {
                                NPCChat.selectOption(option, true);
                            }
                        }
                    }
                }
                break;
            case BAD_UPTEXT:
                Mouse07.fixSelected();
                break;
            case CLICK_WRENCH:
                TABS.OPTIONS.open();
                break;
            case MAKE_CHARACTER:
                int angle = Camera.getCameraAngle();
                if (angle < 70) {
                    Camera.setCameraAngle(General.random(70, 100));
                }
                makeCharacter();
                break;
            case OPEN_INVENTORY:
                TABS.INVENTORY.open();
                break;
            case TALK_TO_GUIDE:
                RSNPC rsGuide = NPCs07.getNPC("RuneScape Guide");
                Camera.turnToTile(rsGuide);
                if (Clicking.click("Talk-to", rsGuide)) {
                    waitForChat();
                }
                break;
            case TALK_TO_SURVIVAL:
                final RSNPC survivalExpert = NPCs07.getNPC("Survival Expert");
                if (!Entities.isCenterOnScreen(survivalExpert)) {
                    Walking07.aStarWalk(survivalExpert);
                } else {
                    if (Clicking.click("Talk-to", survivalExpert)) {
                        waitForChat();
                    }
                }
                break;
            case CHOP_TREE:
                RSObject[] trees = Objects.findNearest(20, "Tree");
                if (trees.length > 0) {
                    RSObject tree = trees[0];
                    if (tree != null) {
                        if (Clicking.click("Chop", tree)) {
                            if (waitForAnimation(0)) {
                                Timing.waitCondition(new Condition() {
                                    @Override
                                    public boolean active() {
                                        General.sleep(100, 200);
                                        return Inventory.getCount(LOG_ID) > 0;
                                    }
                                }, 3000);
                            }
                        }
                    }
                }
                break;
            case MAKE_FIRE:
                RSObject[] fires = Objects.getAt(Player.getPosition());
                boolean fireOnMe = false;
                for (RSObject object : fires) {
                    if (fires != null) {
                        RSObjectDefinition def = object.getDefinition();
                        if (def != null) {
                            String name = def.getName();
                            if (name.equalsIgnoreCase("Fire")) {
                                fireOnMe = true;
                                break;
                            }
                        }
                    }
                }

                if (fireOnMe) {
                    Walking07.walkNearbyTile();
                } else if (Entities.useOn(Inventory07.getItem(LOG_ID), Inventory07.getItem(TINDER_BOX_ID))) {
                    if (waitForAnimation(0)) {
                        Timing.waitCondition(new Condition() {
                            @Override
                            public boolean active() {
                                General.sleep(100, 200);
                                return Objects.findNearest(10, "Fire").length > 0;
                            }
                        }, 3000);
                    }
                }
                break;
            case OPEN_STATS:
                TABS.STATS.open();
                break;
            case FISH:
                final RSNPC fishingSpot = NPCs07.getNPC("Fishing spot");
                if (fishingSpot != null && !Entities.isOnScreen(fishingSpot)) {
                    Walking07.straightWalk(fishingSpot.getPosition());
                } else {
                    if (Clicking.click("Net", fishingSpot)) {
                        if (waitForAnimation(0)) {
                            Timing.waitCondition(new Condition() {
                                @Override
                                public boolean active() {
                                    General.sleep(100, 200);
                                    return Inventory.getCount(SHRIMP_ID) > 1 || Player.getAnimation() == -1;
                                }
                            }, 10000);
                        }
                    }
                }
                break;
            case COOK_FISH:
                final int start = Inventory07.getCount("Raw shrimps");
                RSObject fire = Objects07.getObject("Fire", 15);
                if (fire != null && !Entities.isOnScreen(fire)) {
                    Walking07.straightWalk(fire.getPosition());
                } else {
                    if (Clicking.click(Inventory07.getItem(SHRIMP_ID))) {
                        if (Clicking.click("-> Fire", fire)) {
                            Timing.waitCondition(new Condition() {
                                @Override
                                public boolean active() {
                                    return Inventory07.getCount("Raw shrimps") < start;
                                }
                            }, 5500);
                        }
                    }
                }
                break;
            case TALK_TO_CHEF:
                RSNPC chef = NPCs07.getNPC("Master Chef");
                if (chef != null) {
                    if (chef.isOnScreen() && PathFinding.canReach(chef, false)) {
                        if (Clicking.click("Talk-To", chef)) {
                            waitForChat();
                        }
                    } else {
                        Walking07.aStarWalk(chef);
                    }
                } else {
                    Walking07.aStarWalk(new RSTile(3079, 3084, 0), WalkingConditions.genericCondition(new RSTile(3079, 3084, 0)));
                }
                break;
            case MAKE_DOUGH:
                final int length = Inventory.getAll().length;
                if (Entities.useOn(Inventory07.getItem(BUCKET_OF_WATER_ID), Inventory07.getItem(POT_OF_FLOUR_ID))) {
                    Timing.waitCondition(new Condition() {
                        @Override
                        public boolean active() {
                            General.sleep(100, 200);
                            return length != Inventory.getAll().length;
                        }
                    }, 1200);
                }
                break;
            case COOK_DOUGH:
                if (Entities.useOn(Inventory07.getItem(RAW_DOUGH_ID), Objects07.getObject("Range", 50))) {
                    Timing.waitCondition(new Condition() {
                        @Override
                        public boolean active() {
                            General.sleep(100, 200);
                            return Inventory.find(RAW_DOUGH_ID).length == 0;
                        }
                    }, 8000);
                }
                break;
            case OPEN_MUSIC:
                TABS.MUSIC.open();
                break;
            case LEAVE_COOK:
                Walking07.aStarWalk(new RSTile(3071, 3090, 0));
                break;
            case DO_EMOTES:
                TABS.EMOTES.open();
                RSInterface emote = getRandomEmoteInterface();
                if (emote != null && emote.click()) {
                    if (waitForAnimation(0)) {
                        waitForAnimation(-1);
                    }
                }
                break;
            case TOGGLE_RUN:
                Options07.setRun(true);
                break;
            case TALK_TO_QUEST:
                RSNPC quester = NPCs07.getNPC("Quest Guide");
                if (quester != null) {
                    if (quester.isOnScreen() && PathFinding.canReach(quester, false)) {
                        if (Clicking.click("Talk-To", quester)) {
                            waitForChat();
                        }
                    } else {
                        Walking07.aStarWalk(quester);
                    }
                } else {
                    Walking07.aStarWalk(new RSTile(3083, 3128, 0), WalkingConditions.genericCondition(new RSTile(3083, 3128, 0)));
                }
                break;
            case OPEN_QUEST:
                TABS.QUESTS.open();
                break;
            case CLIMB_DOWN:
                final RSObject ladder = Objects07.getObjectAt(QUEST_LADDER_TILE);
                if (ladder != null) {
                    quester = NPCs07.getNPC("Quest Guide");
                    if (quester != null) {
                        if (PathFinding.canReach(quester, true)) {
                            if (Entities.isCenterOnScreen(ladder)) {
                                if (Clicking.click("Climb-down", ladder)) {
                                    if (waitForAnimation(0)) {
                                        Timing.waitCondition(new Condition() {
                                            @Override
                                            public boolean active() {
                                                General.sleep(100, 200);
                                                return Player.getAnimation() == -1;
                                            }
                                        }, 1200);
                                    }
                                }

                            } else {
                                Walking07.aStarWalk(ladder, WalkingConditions.genericCondition(ladder));
                            }
                        } else {
                            Walking07.aStarWalk(new RSTile(3086, 3124, 0), WalkingConditions.genericCondition(new RSTile(3086, 3124, 0)));
                        }
                    }
                }
                break;
            case TALK_TO_SMITHER:
                RSNPC miningInstructor = NPCs07.getNPC("Mining Instructor");
                if (miningInstructor != null) {
                    if (!miningInstructor.isOnScreen()) {
                        Walking07.straightWalk(miningInstructor.getPosition());
                    } else {
                        if (Clicking.click("Talk-to", miningInstructor)) {
                            waitForChat();
                        }
                    }
                } else {
                    RSTile mITile = new RSTile(3080, 9504, 0);
                    Walking07.straightWalk(mITile, new Condition() {
                        @Override
                        public boolean active() {
                            Client.sleep(100, 200);
                            return NPCs07.getNPC("Mining Instructor") != null;
                        }
                    });
                }
                break;
            case PROSPECT_COPPER:
            case PROSPECT_TIN:
                RSObject ore = isNextStep("It's tin") ? Mining.getRock(20, Rock.COPPER) : Mining.getRock(20, Rock.TIN);
                if (ore != null) {
                    if (Entities.isCenterOnScreen(ore)) {
                        if (Clicking.click("Prospect", ore)) {
                            if (Timing.waitCondition(new Condition() {
                                @Override
                                public boolean active() {
                                    General.sleep(100, 200);
                                    return NPCChat.getMessage() != null;
                                }
                            }, 4000)) {
                                // removed abc
                            }
                        }
                    } else {
                        Walking07.aStarWalk(ore);
                    }
                }
                break;
            case MINE_ROCKS:
                ore = Inventory.getCount(COPPER_ORE_ID) == 0 ? Mining.getRock(60, Rock.COPPER) : Mining.getRock(60, Rock.TIN);
                if (ore != null) {
                    if (Entities.isCenterOnScreen(ore)) {
                        final int invLength = Inventory.getAll().length;
                        if (Clicking.click("Mine", ore)) {
                            if (waitForAnimation(0)) {
                                Timing.waitCondition(new Condition() {
                                    @Override
                                    public boolean active() {
                                        General.sleep(100, 200);
                                        return invLength != Inventory.getAll().length;
                                    }
                                }, 5200);
                            }
                        }
                    } else {
                        Walking07.aStarWalk(ore);
                    }
                }
                break;
            case SMELT_ORES:
                RSTile safeSmeltTile = new RSTile(3079, 9498, 0);
                RSTile destination = Game.getDestination();
                if (Player.getPosition().distanceTo(safeSmeltTile) < 1.5 || destination != null && destination.distanceTo(safeSmeltTile) == 0) {
                    if (Entities.useOn(Inventory07.getItem(General.random(1, 2) == 1 ? COPPER_ORE_ID : TIN_ORE_ID), Objects07.getObjectAt(new RSTile(3078, 9495, 0)))) {
                        if (waitForAnimation(0)) {
                            waitForAnimation(-1);
                        }
                    }
                } else {
                    if (Walking07.aStarWalk(safeSmeltTile)) {
                        Walking07.walkDirect(safeSmeltTile);
                    }
                }
                break;
            case SMITH_DAGGERS:
                final Smithable dagger = Smithable.DAGGER;
                if (dagger.getInterface() != null) {
                    if (dagger.clickInterface()) {
                        Timing.waitCondition(new Condition() {
                            @Override
                            public boolean active() {
                                General.sleep(100, 200);
                                return Inventory07.contains("Bronze dagger");
                            }
                        }, 5200);
                    }
                } else {
                    RSObject anvil = Objects07.getObject("Anvil", 40);
                    if (anvil != null) {
                        if (Entities.useOn(Inventory07.getItem(BRONZE_BAR_ID), anvil)) {
                            Timing.waitCondition(new Condition() {
                                @Override
                                public boolean active() {
                                    General.sleep(100, 200);
                                    return dagger.getInterface() != null;
                                }
                            }, 5200);
                        }
                    }
                }
                break;
            case TALK_TO_COMBAT:
                Interfaces.closeAll();
                final RSNPC vanakka = NPCs07.getNPC("Combat Instructor");
                if (vanakka != null) {
                    if (vanakka.isOnScreen() && PathFinding.canReach(vanakka, false)) {
                        if (Clicking.click("Talk-to", vanakka)) {
                            waitForChat();
                        }
                    } else {
                        Walking07.aStarWalk(vanakka);
                    }
                } else {
                    Walking07.aStarWalk(new RSTile(3105, 9509, 0));
                }
                break;
            case OPEN_EQUIPMENT:
                TABS.EQUIPMENT.open();
                break;
            case OPEN_EQUIPMENT_INTERFACE:
                if (TABS.EQUIPMENT.open()) {
                    if (Clicking.click(Interfaces07.get(387, 17))) {
                        Timing.waitCondition(new Condition() {
                            @Override
                            public boolean active() {
                                Client.sleep(50);
                                return Interfaces07.isUp(84);
                            }
                        }, 1500);
                    }
                }
                break;
            case WIELD_DAGGER:
                Interfaces.closeAll();
                if (Clicking.click(Inventory.find(DAGGER_ID))) {
                    Timing.waitCondition(new Condition() {
                        @Override
                        public boolean active() {
                            General.sleep(100, 200);
                            return Equipment.isEquipped(DAGGER_ID);
                        }
                    }, 1500);
                }
                break;
            case WIELD_SWORD:
                for (RSItem item : Inventory07.find("Bronze sword", "Wooden shield")) {
                    Clicking.click(item);
                    Client.sleep(500, 1000);
                }
                break;
            case OPEN_COMBAT_STYLE:
                Interfaces.closeAll();
                TABS.COMBAT.open();
                break;
            case MELEE_RAT:
                final RSTile rat = new RSTile(3105, 9518);
                if (PathFinding.canReach(rat, false)) {
                    if (!Player.getRSPlayer().isInCombat()) {
                        final RSNPC giantRat = NPCs07.getNPC("Giant Rat");
                        if (giantRat != null) {
                            if (giantRat.isOnScreen() && PathFinding.canReach(giantRat, false)) {
                                if (Clicking.click("Attack", giantRat)) {
                                    Timing.waitCondition(new Condition() {
                                        @Override
                                        public boolean active() {
                                            General.sleep(100, 200);
                                            return Player.getRSPlayer().isInCombat();
                                        }
                                    }, 5500);
                                }
                            } else {
                                Walking07.aStarWalk(giantRat);
                            }
                        }
                    }
                } else {
                    Walking07.aStarWalk(rat, WalkingConditions.genericCondition(rat));
                }
                break;
            case RANGE_RAT:
                RSTile safeSpot = new RSTile(3110, 9514, 0);
                if (safeSpot.distanceTo(Player.getPosition()) < 1.5) {
                    RSItem[] rangeItems = Inventory.find(841, 882);
                    if (rangeItems.length > 0) {
                        if (Clicking.clickAll(0, 0, rangeItems) > 0) {
                            Timing.waitCondition(new Condition() {
                                @Override
                                public boolean active() {
                                    General.sleep(100, 200);
                                    return Inventory.find(841, 882).length == 0;
                                }
                            }, 1500);
                        }
                    } else {
                        if (Clicking.click("Attack", NPCs.findNearest("Giant rat"))) {
                            if (Timing.waitCondition(new Condition() {
                                @Override
                                public boolean active() {
                                    General.sleep(100, 200);
                                    return Combat.isUnderAttack();
                                }
                            }, 1500)) {
                                Timing.waitCondition(new Condition() {
                                    @Override
                                    public boolean active() {
                                        General.sleep(100, 200);
                                        return !Combat.isUnderAttack();
                                    }
                                }, 10500);
                            }
                        }
                    }
                } else {
                    Walking07.straightWalk(safeSpot);
                }
                break;
            case BANK:
                RSObject ratLadder = Objects07.getObjectAt(new RSTile(3111, 9526, 0));
                if (ratLadder != null) {
                    Walking07.aStarWalk(ratLadder, WalkingConditions.genericCondition(ratLadder));
                    if (Clicking.click("Climb-up", ratLadder)) {
                        Timing.waitCondition(new Condition() {
                            @Override
                            public boolean active() {
                                General.sleep(100, 200);
                                return Objects07.getObjectAt(new RSTile(3111, 9526, 0)) == null;
                            }
                        }, 2000);
                    }
                } else {
                    if (Bank07.isOnScreen()) {
                        Bank07.open();
                    } else {
                        Walking07.straightWalk(new RSTile(3121, 3122, 0));
                    }
                }
                break;
            case MONEY_POLL:
                Bank07.depositAll();
                Interfaces.closeAll();
                RSObject[] poll = Objects.getAt(new RSTile(3119, 3121, 0));
                if (poll.length > 0) {
                    if (Entities.isCenterOnScreen(poll[0])) {
                        if (Clicking.click("Use", poll)) {
                            Timing.waitCondition(new Condition() {
                                @Override
                                public boolean active() {
                                    General.sleep(100, 200);
                                    return NPCChat.getMessage() != null || NPCChat.getOptions() != null;
                                }
                            }, 5000);
                        }
                    } else {
                        Walking07.aStarWalk(poll[0]);
                        if (Player.getPosition().distanceTo(poll[0].getPosition()) <= General.random(5, 7)) {
                            Camera.turnToTile(poll[0].getPosition());
                        }
                    }
                }
                break;
            case TALK_TO_MONEY_GUY:
                Interfaces07.closeAll();
                final RSNPC financialAdvisor = NPCs07.getNPC("Financial Advisor");
                if (financialAdvisor != null) {
                    if (financialAdvisor.isOnScreen() && PathFinding.canReach(financialAdvisor, false)) {
                        if (Clicking.click("Talk-to", financialAdvisor)) {
                            waitForChat();
                        }
                    } else {
                        Walking07.aStarWalk(financialAdvisor);
                    }
                }
                break;
            case TALK_TO_GOD:
                final RSNPC god = NPCs07.getNPC("Brother Brace");
                if (god != null) {
                    if (god.isOnScreen() && PathFinding.canReach(god, false)) {
                        if (Clicking.click("Talk-to", god)) {
                            waitForChat();
                        }
                    } else {
                        Walking07.aStarWalk(god);
                    }
                } else {
                    Walking07.aStarWalk(new RSTile(3126, 3107, 0), new Condition() {
                        @Override
                        public boolean active() {
                            Client.sleep(200);
                            return NPCs07.getNPC("Brother Brace") != null;
                        }
                    });
                }
                break;
            case OPEN_PRAYER:
                TABS.PRAYERS.open();
                AntiBan.sleep(General.random(8, 12));
                break;
            case OPEN_FRIENDS:
                TABS.FRIENDS.open();
                AntiBan.sleep(General.random(8, 12));
                break;
            case OPEN_IGNORE:
                TABS.IGNORE.open();
                AntiBan.sleep(General.random(8, 12));
                break;
            case TALK_TO_WIZARD:
                RSNPC[] mage = NPCs.findNearest("Magic Instructor");
                if (mage.length > 0) {
                    if (Entities.isCenterOnScreen(mage[0])) {
                        if (Clicking.click("Talk-to", mage)) {
                            waitForChat();
                        }
                    } else {
                        Walking07.straightWalk(new RSTile(3141, 3088, 0));
                    }
                } else {
                    RSObject door = Objects07.getObject("Door", 7);
                    Camera.turnToTile(door);
                    Walking07.aStarWalk(new RSTile(3141, 3088, 0));
                }
                break;
            case OPEN_MAGIC:
                TABS.MAGIC.open();
                AntiBan.sleep(General.random(8, 12));
                break;
            case KILL_CHICKEN:
                RSTile safe = new RSTile(3139, 3090, 0);
                if (Entities.distanceTo(safe) < 1.5) {
                    RSNPC[] npc = NPCs.findNearest("Chicken");
                    if (Magic.selectSpell("Wind strike")) {
                        if (Clicking.click("-> Chicken", npc)) {
                            if (waitForAnimation(0)) {
                                waitForAnimation(-1);
                            }
                        }
                    }
                } else {
                    Walking07.aStarWalk(safe);
                }
                break;
            case WALK_TO_SPOT:
                break;
            case STOP_SCRIPT:
                break;
        }
        AntiBan.sleep();
    }

    private void makeCharacter() {
//        new CharacterCreation07().makeCharacter();
        int[] childInterfaces = {137, 113, 114, 115, 116, 117, 118, 119, 121, 127, 129, 130, 131};
        for (int child : childInterfaces) {
            RSInterface customPart = Interfaces.get(269, child);
            if (customPart != null) {
                int limit = child == 137 ? General.random(0, 1) : child == 131 ? General.random(0, 8) : General.random(0, 10);
                for (int i = 0; i < limit; i++) {
                    if (customPart.click()) {
                        AntiBan.sleep(General.random(2, 10));
                    }
                }
            }
        }
        AntiBan.sleep(General.random(2, 10));
        RSInterface acceptButton = Interfaces.get(269, 99);
        if (acceptButton != null && acceptButton.click()) {
            Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    AntiBan.sleep();
                    return !Interfaces07.isUp(269);
                }
            }, General.random(3000, 4200));
        }
    }

    private RSInterfaceChild getClickContinueInterface() {
        return Interfaces07.get(548, 124);
//        RSInterfaceMaster master = Interfaces.get(548);
//        Rectangle g = new Rectangle(12, 349, 500, 125);
//        for (RSInterfaceChild child : master.getChildren()) {
//            if (child != null && !child.isHidden() && child.getTextColour() == 128 && g.contains(child.getAbsolutePosition())) {
//                if (child.getText() != null) {
//                    String text = child.getText();
//                    if (text.contains("Click to continue")) {
//                        return child;
//                    }
//                }
//            }
//        }
//        return null;
    }

    private boolean isTutorialClickContinueUp() {
        RSInterfaceChild clickContinue = getClickContinueInterface();
        return clickContinue != null && !clickContinue.isHidden();
    }

    private RSInterface getRandomEmoteInterface() {
        int random = General.random(0, 19);
        RSInterface emotes = Interfaces.get(216, 1);
        emotes = emotes.getChild(random);
        return emotes != null ? emotes : null;
    }

    private boolean waitForChat() {
        return Timing.waitCondition(new Condition() {
            @Override
            public boolean active() {
                AntiBan.sleep();
                return NPCChat.getMessage() != null;
            }
        }, 3000);
    }

    private boolean waitForAnimation(final int animation) {
        return Timing.waitCondition(new Condition() {
            @Override
            public boolean active() {
                AntiBan.sleep();
                int playerAnimation = Player.getAnimation();
                return animation == 0 ? playerAnimation != -1 : playerAnimation == animation;
            }
        }, 2000);
    }
}
