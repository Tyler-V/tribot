package scripts.magic.guis;

import org.tribot.api.input.Mouse;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.api2007.types.RSNPC;
import org.tribot.util.Util;
import scripts.magic.data.*;
import scripts.magic.tasks.*;
import scripts.starfox.api.Client;
import scripts.starfox.api.listeners.InventoryListener;
import scripts.starfox.api.util.Images;
import scripts.starfox.api2007.Inventory07;
import scripts.starfox.api2007.login.Login07;
import scripts.starfox.api2007.skills.magic.books.LunarSpell;
import scripts.starfox.api2007.skills.magic.books.NormalSpell;
import scripts.starfox.api2007.skills.magic.data.SpellType;
import scripts.starfox.interfaces.listening.InventoryListening07;
import scripts.starfox.scriptframework.ScriptFrame;
import scripts.starfox.scriptframework.TerminateCondition;
import scripts.starfox.scriptframework.taskframework.TaskManager;
import scripts.starfox.swing.SearchBox;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

/**
 * @author Starfox
 */
public class SigmaMagicGui
        extends ScriptFrame
        implements InventoryListening07 {

    String showingCard;
    File directory;
    SigmaMagicFAQDialog faq;
    InventoryListener inventoryListener;

    /**
     * Constructs a new SigmaMagicGui.
     */
    public SigmaMagicGui() {
        super("Magic");
        initComponents();
        initCustom();
    }

    /**
     * Initialize all custom settings for the gui
     */
    private void initCustom() {
        //fuck card layout it is so bad i hate my life.
        showingCard = "noSpellOptionsCard";
        directory = new File(Util.getWorkingDirectory().getPath() + "/Sigma/Magic");
        directory.mkdirs();
        stopAtLevelSpinner.setValue(SKILLS.MAGIC.getActualLevel() + 1);
        //if we are 99 magic or higher then disable the stop at checkbox.
        if (SKILLS.MAGIC.getActualLevel() >= 99) {
            stopAtCheck.setEnabled(false);
        }
        ToolTipManager.sharedInstance().setInitialDelay(250);
        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
        ArrayList<Object> spellList = new ArrayList<>();
        spellList.addAll(Arrays.asList(NormalSpell.values()));
        spellList.addAll(Arrays.asList(LunarSpell.values()));
        removeUnfinishedSpells(spellList, SpellType.COMBAT, SpellType.TELEKINETIC_GRAB, SpellType.TELE_OTHER);
        ((SearchBox) searchBox).setSearchList(spellList);
        loadScriptOptions();
        updateComboBoxes();
        updateFiles();
        inventoryListener = new InventoryListener();
        inventoryListener.addListener(this);
        inventoryListener.start();
    }

    private void loadScriptOptions() {
        Properties p = new Properties();
        File f = new File(directory.getPath() + "/script options.txt");
        try {
            if (!f.exists()) {
                if (!f.createNewFile()) {
                    System.out.println("There was a problem creating a file: " + f.getAbsolutePath());
                }
            }
            p.load(new FileReader(f));
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        if (p.stringPropertyNames().isEmpty()) {
            return;
        }
        stopAtCheck.setSelected(Boolean.parseBoolean(p.getProperty("stop_at")));
        stopAtLevelSpinner.setValue(Integer.parseInt(p.getProperty("stop_at_level")));
        mouseSpeedSpinner.setValue(Integer.parseInt(p.getProperty("mouse_speed")));
    }

    private void saveScriptOptions() {
        Properties p = new Properties();
        File f = new File(directory.getPath() + "/script options.txt");
        p.setProperty("stop_at", "" + stopAtCheck.isSelected());
        p.setProperty("stop_at_level", "" + (int) stopAtLevelSpinner.getValue());
        p.setProperty("mouse_speed", "" + (int) mouseSpeedSpinner.getValue());
        try {
            if (!f.exists()) {
                if (!f.createNewFile()) {
                    System.out.println("There was a problem creating a file: " + f.getAbsolutePath());
                }
            }
            p.store(new FileWriter(f), "Script options");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteCurrentTemplate() {
        new File(directory.getPath() + "/" + savedTemplatesBox.getSelectedItem() + ".magic").delete();
        updateFiles();
    }

    private void openTemplate(String fName) {
        if (fName == null || fName.isEmpty()) {
            System.err.println("Tried to open null/empty template. Please report this!");
            return;
        }
        FileInputStream fin = null;
        ObjectInputStream ois = null;
        File file = new File(directory.getPath() + "/" + fName + ".magic");
        try {
            fin = new FileInputStream(file);
            ois = new ObjectInputStream(fin);
            ((SearchBox) searchBox).clear();
            searchBox.setSelectedItem(ois.readObject());
            showingCard = ois.readObject().toString();
            for (Component c : forName(showingCard).getComponents()) {
                if (c instanceof JCheckBox) {
                    System.out.println("Loaded checkbox");
                    ((JCheckBox) c).setSelected(ois.readBoolean());
                } else if (c instanceof JSpinner) {
                    System.out.println("Loaded spinner");
                    ((JSpinner) c).setValue(ois.readObject());
                } else if (c instanceof JComboBox) {
                    System.out.println("Loaded combobox");
                    ((JComboBox) c).setSelectedItem(ois.readObject());
                }
            }
            ((CardLayout) cardPanel.getLayout()).show(cardPanel, showingCard);
            fileNameField.setText(fName);
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (fin != null) {
                    fin.close();
                }
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveTemplate() {
        if (fileNameField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter a file name before saving.");
            return;
        }
        Object item = searchBox.getSelectedItem();
        if (item == null || item.toString().isEmpty() || item.toString().equalsIgnoreCase(((SearchBox) searchBox).getPrompt().toString()) || !isSpellValid(item)) {
            JOptionPane.showMessageDialog(null, "Please select a valid spell before saving.");
            return;
        }
        FileOutputStream fout = null;
        ObjectOutputStream oos = null;
        File file = new File(directory.getPath() + "/" + fileNameField.getText() + ".magic");
        try {
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    System.out.println("There was a problem creating a file: " + file.getAbsolutePath());
                    return;
                }
            }
            new FileOutputStream(file).close(); //clear the file
            fout = new FileOutputStream(file);
            oos = new ObjectOutputStream(fout);
            oos.writeObject(searchBox.getSelectedItem());
            oos.writeObject(showingCard);
            for (Component c : forName(showingCard).getComponents()) {
                if (c instanceof JCheckBox) {
                    oos.writeBoolean(((JCheckBox) c).isSelected());
                } else if (c instanceof JSpinner) {
                    oos.writeObject(((JSpinner) c).getValue());
                } else if (c instanceof JComboBox) {
                    oos.writeObject(((JComboBox) c).getSelectedItem());
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                    System.out.println("Successfully closed object output stream");
                }
                if (fout != null) {
                    fout.close();
                    System.out.println("Successfully closed file output stream");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        updateFiles();
    }

    private void updateFiles() {
        String[] fList = directory.list();
        String selectedFile = fileNameField.getText();
        savedTemplatesBox.removeAllItems();
        for (String fileName : fList) {
            if (fileName.contains(".magic")) {
                String fName = fileName.replace(".magic", "");
                savedTemplatesBox.addItem(fName);
            }
        }
        if (selectedFile != null) {
            savedTemplatesBox.setSelectedItem(selectedFile);
        }
        if (savedTemplatesBox.getSelectedItem() == null) {
            setBlank();
        }
    }

    private JPanel forName(String name) {
        for (Component c : cardPanel.getComponents()) {
            if (c.getName().equalsIgnoreCase(name)) {
                return (JPanel) c;
            }
        }
        return null;
    }

    private void setBlank() {
        fileNameField.setText("");
        savedTemplatesBox.setSelectedItem(null);
        ((SearchBox) searchBox).reset();
        showingCard = "noSpellOptionsCard";
        ((CardLayout) cardPanel.getLayout()).show(cardPanel, showingCard);
    }

    private boolean isSpellValid(Object obj) {
        return obj instanceof NormalSpell || obj instanceof LunarSpell;
    }

    private void updateComboBoxes() {
        try {
            alchItemComboBox.removeAllItems();
            curseNPCComboBox.removeAllItems();
            curseAlchItemBox.removeAllItems();
            curseAlchNPCBox.removeAllItems();
            for (RSItem item : Inventory07.filterDuplicates(Inventory.getAll())) {
                if (item != null) {
                    RSItemDefinition def = item.getDefinition();
                    if (def != null) {
                        String name = def.getName();
                        if (name != null) {
                            alchItemComboBox.addItem(name);
                            curseAlchItemBox.addItem(name);
                        }
                    }
                }
            }
            for (RSNPC npc : NPCs.getAll()) {
                if (npc != null) {
                    String name = npc.getName();
                    if (name != null) {
                        curseNPCComboBox.addItem(name);
                        curseAlchNPCBox.addItem(name);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeUnfinishedSpells(ArrayList list, SpellType... types) {
        for (NormalSpell spell : NormalSpell.values()) {
            for (SpellType type : types) {
                if (type == spell.getType()) {
                    list.remove(spell);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        teleOtherGroup = new javax.swing.ButtonGroup();
        contentPanel = new JPanel();
        welcomePanel = new JPanel();
        fileOptionPanel = new JPanel();
        fileNameLabel = new javax.swing.JLabel();
        fileNameField = new javax.swing.JTextField();
        savedTemplatesLabel = new javax.swing.JLabel();
        savedTemplatesBox = new JComboBox();
        deleteTemplateButton = new javax.swing.JButton();
        searchPanel = new JPanel();
        searchBox = new SearchBox("Start Typing Spell Name...");
        scriptOptionsPanel = new JPanel();
        stopAtCheck = new JCheckBox();
        stopAtLevelSpinner = new JSpinner();
        mouseSpeedLabel = new javax.swing.JLabel();
        mouseSpeedSpinner = new JSpinner();
        cardPanel = new JPanel();
        noSpellOptionsCard = new JPanel();
        noAdditionalOptionsLabel = new javax.swing.JLabel();
        alchemyCard = new JPanel();
        alchItemLabel = new javax.swing.JLabel();
        alchItemComboBox = new JComboBox();
        bonesToFruitCard = new JPanel();
        boneLabel = new javax.swing.JLabel();
        boneComboBox = new JComboBox();
        boneAmountLabel = new javax.swing.JLabel();
        boneAmountComboBox = new JComboBox();
        curseCard = new JPanel();
        curseNPCLabel = new javax.swing.JLabel();
        curseNPCComboBox = new JComboBox();
        curseAlchemyCard = new JPanel();
        jLabel1 = new javax.swing.JLabel();
        curseAlchAlchBox = new JComboBox();
        jLabel2 = new javax.swing.JLabel();
        curseAlchItemBox = new JComboBox();
        jLabel3 = new javax.swing.JLabel();
        curseAlchCurseBox = new JComboBox();
        jLabel4 = new javax.swing.JLabel();
        curseAlchNPCBox = new JComboBox();
        enchantmentCard = new JPanel();
        enchantLabel = new javax.swing.JLabel();
        enchantComboBox = new JComboBox();
        superheatItemCard = new JPanel();
        barLabel = new javax.swing.JLabel();
        barComboBox = new JComboBox();
        telekineticGrabCard = new JPanel();
        tkGrabLabel = new javax.swing.JLabel();
        tkGrabItemNameField = new javax.swing.JTextField();
        teleOtherCard = new JPanel();
        casterRadioButton = new javax.swing.JRadioButton();
        receiverRadioButton = new javax.swing.JRadioButton();
        bakePieCard = new JPanel();
        pieLabel = new javax.swing.JLabel();
        pieComboBox = new JComboBox();
        humidifyCard = new JPanel();
        humidifyLabel = new javax.swing.JLabel();
        humidifyComboBox = new JComboBox();
        superglassMakeCard = new JPanel();
        ingredientLabel = new javax.swing.JLabel();
        glassMakeIngredientComboBox = new JComboBox();
        stringJewelryCard = new JPanel();
        stringLabel = new javax.swing.JLabel();
        stringJewelryComboBox = new JComboBox();
        plankMakeCard = new JPanel();
        plankLabel = new javax.swing.JLabel();
        plankComboBox = new JComboBox();
        startButton = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        newMenuItem = new javax.swing.JMenuItem();
        saveMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        faqMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sigma Magic");
        setIconImage(Images.getImageFromUrl("http://i.imgur.com/7sXslUV.png"));
        setResizable(false);

        contentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED), "Welcome", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N
        contentPanel.setToolTipText("");

        welcomePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED), "Template Options", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N

        fileOptionPanel.setLayout(new java.awt.GridLayout(4, 0));

        fileNameLabel.setText("File Name:");
        fileOptionPanel.add(fileNameLabel);
        fileOptionPanel.add(fileNameField);

        savedTemplatesLabel.setText("Saved Templates:");
        fileOptionPanel.add(savedTemplatesLabel);

        savedTemplatesBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                savedTemplatesBoxActionPerformed(evt);
            }
        });
        fileOptionPanel.add(savedTemplatesBox);

        deleteTemplateButton.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        deleteTemplateButton.setText("Delete Selected Template");
        deleteTemplateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteTemplateButtonActionPerformed(evt);
            }
        });

        searchPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), "Select Spell", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N

        searchBox.setEditable(true);
        searchBox.setAutoscrolls(true);
        searchBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout searchPanelLayout = new javax.swing.GroupLayout(searchPanel);
        searchPanel.setLayout(searchPanelLayout);
        searchPanelLayout.setHorizontalGroup(
                searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(searchBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        searchPanelLayout.setVerticalGroup(
                searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(searchBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout welcomePanelLayout = new javax.swing.GroupLayout(welcomePanel);
        welcomePanel.setLayout(welcomePanelLayout);
        welcomePanelLayout.setHorizontalGroup(
                welcomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(welcomePanelLayout.createSequentialGroup()
                                .addGroup(welcomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(searchPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(deleteTemplateButton, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
                                        .addComponent(fileOptionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(0, 0, Short.MAX_VALUE))
        );
        welcomePanelLayout.setVerticalGroup(
                welcomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(welcomePanelLayout.createSequentialGroup()
                                .addComponent(fileOptionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(deleteTemplateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(searchPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(6, 6, 6))
        );

        scriptOptionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED), "Script Options", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N
        scriptOptionsPanel.setLayout(new java.awt.GridLayout(2, 2));

        stopAtCheck.setText("Stop at level:");
        stopAtCheck.setMargin(new java.awt.Insets(2, -2, 2, 2));
        stopAtCheck.setName("stopAtLevel"); // NOI18N
        scriptOptionsPanel.add(stopAtCheck);

        stopAtLevelSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                stopAtLevelSpinnerStateChanged(evt);
            }
        });
        scriptOptionsPanel.add(stopAtLevelSpinner);

        mouseSpeedLabel.setText("Mouse speed:");
        scriptOptionsPanel.add(mouseSpeedLabel);

        mouseSpeedSpinner.setToolTipText("<html><b>WARNING:</b> Increasing the mouse speed above the default (90) is not recommended</html>");
        mouseSpeedSpinner.setValue(90);
        mouseSpeedSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                mouseSpeedSpinnerStateChanged(evt);
            }
        });
        scriptOptionsPanel.add(mouseSpeedSpinner);

        cardPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED), "Additional Spell Options", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N
        cardPanel.setLayout(new CardLayout());

        noSpellOptionsCard.setName("noSpellOptionsCard"); // NOI18N

        noAdditionalOptionsLabel.setText("None!");

        javax.swing.GroupLayout noSpellOptionsCardLayout = new javax.swing.GroupLayout(noSpellOptionsCard);
        noSpellOptionsCard.setLayout(noSpellOptionsCardLayout);
        noSpellOptionsCardLayout.setHorizontalGroup(
                noSpellOptionsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(noSpellOptionsCardLayout.createSequentialGroup()
                                .addComponent(noAdditionalOptionsLabel)
                                .addGap(0, 201, Short.MAX_VALUE))
        );
        noSpellOptionsCardLayout.setVerticalGroup(
                noSpellOptionsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(noSpellOptionsCardLayout.createSequentialGroup()
                                .addComponent(noAdditionalOptionsLabel)
                                .addGap(0, 87, Short.MAX_VALUE))
        );

        cardPanel.add(noSpellOptionsCard, "noSpellOptionsCard");

        alchemyCard.setName("alchemyCard"); // NOI18N

        alchItemLabel.setText("Alch Item:");

        javax.swing.GroupLayout alchemyCardLayout = new javax.swing.GroupLayout(alchemyCard);
        alchemyCard.setLayout(alchemyCardLayout);
        alchemyCardLayout.setHorizontalGroup(
                alchemyCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(alchemyCardLayout.createSequentialGroup()
                                .addComponent(alchItemLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(alchItemComboBox, 0, 169, Short.MAX_VALUE))
        );
        alchemyCardLayout.setVerticalGroup(
                alchemyCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(alchemyCardLayout.createSequentialGroup()
                                .addGroup(alchemyCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(alchItemComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(alchItemLabel))
                                .addGap(0, 78, Short.MAX_VALUE))
        );

        cardPanel.add(alchemyCard, "alchemyCard");

        bonesToFruitCard.setName("bonesToFruitCard"); // NOI18N

        boneLabel.setText("Bone:");

        boneComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"Bones", "Big bones", "Wolf bones", "Bat bones"}));

        boneAmountLabel.setText("Amount per cast:");

        boneAmountComboBox.setModel(new DefaultComboBoxModel<>(Amount.values()));

        javax.swing.GroupLayout bonesToFruitCardLayout = new javax.swing.GroupLayout(bonesToFruitCard);
        bonesToFruitCard.setLayout(bonesToFruitCardLayout);
        bonesToFruitCardLayout.setHorizontalGroup(
                bonesToFruitCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(bonesToFruitCardLayout.createSequentialGroup()
                                .addGroup(bonesToFruitCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(boneAmountLabel)
                                        .addComponent(boneLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(bonesToFruitCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(boneComboBox, 0, 133, Short.MAX_VALUE)
                                        .addComponent(boneAmountComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        bonesToFruitCardLayout.setVerticalGroup(
                bonesToFruitCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(bonesToFruitCardLayout.createSequentialGroup()
                                .addGroup(bonesToFruitCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(boneComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(boneLabel))
                                .addGap(4, 4, 4)
                                .addGroup(bonesToFruitCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(boneAmountLabel)
                                        .addComponent(boneAmountComboBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(50, Short.MAX_VALUE))
        );

        cardPanel.add(bonesToFruitCard, "bonesToFruitCard");

        curseCard.setName("curseCard"); // NOI18N

        curseNPCLabel.setText("Curse NPC:");

        javax.swing.GroupLayout curseCardLayout = new javax.swing.GroupLayout(curseCard);
        curseCard.setLayout(curseCardLayout);
        curseCardLayout.setHorizontalGroup(
                curseCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(curseCardLayout.createSequentialGroup()
                                .addComponent(curseNPCLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(curseNPCComboBox, 0, 161, Short.MAX_VALUE))
        );
        curseCardLayout.setVerticalGroup(
                curseCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(curseCardLayout.createSequentialGroup()
                                .addGroup(curseCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(curseNPCComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(curseNPCLabel))
                                .addGap(0, 78, Short.MAX_VALUE))
        );

        cardPanel.add(curseCard, "curseCard");

        curseAlchemyCard.setName("curseAlchemyCard"); // NOI18N

        jLabel1.setText("Alchemy spell:");

        curseAlchAlchBox.setModel(new DefaultComboBoxModel<>(new NormalSpell[]{NormalSpell.LOW_ALCHEMY, NormalSpell.HIGH_ALCHEMY}));

        jLabel2.setText("Alchemy item:");

        curseAlchItemBox.setModel(new DefaultComboBoxModel<>());

        jLabel3.setText("Curse spell:");

        curseAlchCurseBox.setModel(new DefaultComboBoxModel<NormalSpell>());
        for (NormalSpell spell : NormalSpell.values()) {
            if (spell.getType() == SpellType.CURSE) {
                curseAlchCurseBox.addItem(spell);
            }
        }
        curseAlchCurseBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                curseAlchCurseBoxActionPerformed(evt);
            }
        });

        jLabel4.setText("Curse NPC:");

        curseAlchNPCBox.setModel(new DefaultComboBoxModel<>());

        javax.swing.GroupLayout curseAlchemyCardLayout = new javax.swing.GroupLayout(curseAlchemyCard);
        curseAlchemyCard.setLayout(curseAlchemyCardLayout);
        curseAlchemyCardLayout.setHorizontalGroup(
                curseAlchemyCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(curseAlchemyCardLayout.createSequentialGroup()
                                .addGroup(curseAlchemyCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(curseAlchemyCardLayout.createSequentialGroup()
                                                .addGroup(curseAlchemyCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel2)
                                                        .addComponent(jLabel1)
                                                        .addComponent(jLabel3))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(curseAlchemyCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(curseAlchCurseBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addGroup(curseAlchemyCardLayout.createSequentialGroup()
                                                                .addComponent(curseAlchAlchBox, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(0, 0, Short.MAX_VALUE))
                                                        .addComponent(curseAlchItemBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                        .addGroup(curseAlchemyCardLayout.createSequentialGroup()
                                                .addComponent(jLabel4)
                                                .addGap(27, 27, 27)
                                                .addComponent(curseAlchNPCBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        curseAlchemyCardLayout.setVerticalGroup(
                curseAlchemyCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(curseAlchemyCardLayout.createSequentialGroup()
                                .addGroup(curseAlchemyCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel1)
                                        .addComponent(curseAlchAlchBox, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(curseAlchemyCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(curseAlchItemBox, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(curseAlchemyCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(curseAlchCurseBox, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(curseAlchemyCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel4)
                                        .addComponent(curseAlchNPCBox, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        cardPanel.add(curseAlchemyCard, "curseAlchemyCard");

        enchantmentCard.setName("enchantmentCard"); // NOI18N

        enchantLabel.setText("Enchant:");

        enchantComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"Ring", "Bracelet", "Necklace", "Amulet"}));

        javax.swing.GroupLayout enchantmentCardLayout = new javax.swing.GroupLayout(enchantmentCard);
        enchantmentCard.setLayout(enchantmentCardLayout);
        enchantmentCardLayout.setHorizontalGroup(
                enchantmentCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(enchantmentCardLayout.createSequentialGroup()
                                .addComponent(enchantLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(enchantComboBox, 0, 174, Short.MAX_VALUE))
        );
        enchantmentCardLayout.setVerticalGroup(
                enchantmentCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(enchantmentCardLayout.createSequentialGroup()
                                .addGroup(enchantmentCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(enchantComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(enchantLabel))
                                .addGap(0, 78, Short.MAX_VALUE))
        );

        cardPanel.add(enchantmentCard, "enchantmentCard");

        superheatItemCard.setName("superheatItemCard"); // NOI18N

        barLabel.setText("Bar:");

        barComboBox.setModel(new DefaultComboBoxModel<>(SuperheatBar.values()));

        javax.swing.GroupLayout superheatItemCardLayout = new javax.swing.GroupLayout(superheatItemCard);
        superheatItemCard.setLayout(superheatItemCardLayout);
        superheatItemCardLayout.setHorizontalGroup(
                superheatItemCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(superheatItemCardLayout.createSequentialGroup()
                                .addComponent(barLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(barComboBox, 0, 196, Short.MAX_VALUE))
        );
        superheatItemCardLayout.setVerticalGroup(
                superheatItemCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(superheatItemCardLayout.createSequentialGroup()
                                .addGroup(superheatItemCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(barComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(barLabel))
                                .addGap(0, 78, Short.MAX_VALUE))
        );

        cardPanel.add(superheatItemCard, "superheatItemCard");

        telekineticGrabCard.setName("telekineticGrabCard"); // NOI18N

        tkGrabLabel.setText("Item name:");

        tkGrabItemNameField.setToolTipText("This is the name of the item you want to grab. This field is not case sensitive.");

        javax.swing.GroupLayout telekineticGrabCardLayout = new javax.swing.GroupLayout(telekineticGrabCard);
        telekineticGrabCard.setLayout(telekineticGrabCardLayout);
        telekineticGrabCardLayout.setHorizontalGroup(
                telekineticGrabCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(telekineticGrabCardLayout.createSequentialGroup()
                                .addComponent(tkGrabLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tkGrabItemNameField, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE))
        );
        telekineticGrabCardLayout.setVerticalGroup(
                telekineticGrabCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(telekineticGrabCardLayout.createSequentialGroup()
                                .addGroup(telekineticGrabCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(tkGrabLabel)
                                        .addComponent(tkGrabItemNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 83, Short.MAX_VALUE))
        );

        cardPanel.add(telekineticGrabCard, "telekineticGrabCard");

        teleOtherCard.setName("teleOtherCard"); // NOI18N

        teleOtherGroup.add(casterRadioButton);
        casterRadioButton.setSelected(true);
        casterRadioButton.setText("Caster");

        teleOtherGroup.add(receiverRadioButton);
        receiverRadioButton.setText("Receiver");

        javax.swing.GroupLayout teleOtherCardLayout = new javax.swing.GroupLayout(teleOtherCard);
        teleOtherCard.setLayout(teleOtherCardLayout);
        teleOtherCardLayout.setHorizontalGroup(
                teleOtherCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(teleOtherCardLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(teleOtherCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(casterRadioButton)
                                        .addComponent(receiverRadioButton))
                                .addContainerGap(152, Short.MAX_VALUE))
        );
        teleOtherCardLayout.setVerticalGroup(
                teleOtherCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(teleOtherCardLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(casterRadioButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(receiverRadioButton)
                                .addContainerGap(49, Short.MAX_VALUE))
        );

        cardPanel.add(teleOtherCard, "teleOtherCard");

        bakePieCard.setName("bakePieCard"); // NOI18N

        pieLabel.setText("Pie:");

        pieComboBox.setModel(new DefaultComboBoxModel<>(Pie.values()));

        javax.swing.GroupLayout bakePieCardLayout = new javax.swing.GroupLayout(bakePieCard);
        bakePieCard.setLayout(bakePieCardLayout);
        bakePieCardLayout.setHorizontalGroup(
                bakePieCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(bakePieCardLayout.createSequentialGroup()
                                .addComponent(pieLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pieComboBox, 0, 199, Short.MAX_VALUE))
        );
        bakePieCardLayout.setVerticalGroup(
                bakePieCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(bakePieCardLayout.createSequentialGroup()
                                .addGroup(bakePieCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(pieComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(pieLabel))
                                .addGap(0, 78, Short.MAX_VALUE))
        );

        cardPanel.add(bakePieCard, "bakePieCard");

        humidifyCard.setName("humidifyCard"); // NOI18N

        humidifyLabel.setText("Humidify:");

        humidifyComboBox.setModel(new DefaultComboBoxModel(new String[]{"Bucket", "Jug", "Empty vial", "Bowl", "Clay", "Fishbowl"}));

        javax.swing.GroupLayout humidifyCardLayout = new javax.swing.GroupLayout(humidifyCard);
        humidifyCard.setLayout(humidifyCardLayout);
        humidifyCardLayout.setHorizontalGroup(
                humidifyCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(humidifyCardLayout.createSequentialGroup()
                                .addComponent(humidifyLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(humidifyComboBox, 0, 172, Short.MAX_VALUE))
        );
        humidifyCardLayout.setVerticalGroup(
                humidifyCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(humidifyCardLayout.createSequentialGroup()
                                .addGroup(humidifyCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(humidifyComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(humidifyLabel))
                                .addGap(0, 78, Short.MAX_VALUE))
        );

        cardPanel.add(humidifyCard, "humidifyCard");

        superglassMakeCard.setName("superglassMakeCard"); // NOI18N

        ingredientLabel.setText("Ingredient:");

        glassMakeIngredientComboBox.setModel(new DefaultComboBoxModel(new String[]{"Soda ash", "Seaweed", "Swamp weed"}));

        javax.swing.GroupLayout superglassMakeCardLayout = new javax.swing.GroupLayout(superglassMakeCard);
        superglassMakeCard.setLayout(superglassMakeCardLayout);
        superglassMakeCardLayout.setHorizontalGroup(
                superglassMakeCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(superglassMakeCardLayout.createSequentialGroup()
                                .addComponent(ingredientLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(glassMakeIngredientComboBox, 0, 166, Short.MAX_VALUE))
        );
        superglassMakeCardLayout.setVerticalGroup(
                superglassMakeCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(superglassMakeCardLayout.createSequentialGroup()
                                .addGroup(superglassMakeCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(glassMakeIngredientComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(ingredientLabel))
                                .addGap(0, 78, Short.MAX_VALUE))
        );

        cardPanel.add(superglassMakeCard, "superglassMakeCard");

        stringJewelryCard.setName("stringJewelryCard"); // NOI18N

        stringLabel.setText("String:");

        stringJewelryComboBox.setModel(new DefaultComboBoxModel(new String[]{"Gold amulet", "Sapphire amulet", "Emerald amulet", "Ruby amulet", "Diamond amulet", "Dragonstone ammy", "Onyx amulet"}));

        javax.swing.GroupLayout stringJewelryCardLayout = new javax.swing.GroupLayout(stringJewelryCard);
        stringJewelryCard.setLayout(stringJewelryCardLayout);
        stringJewelryCardLayout.setHorizontalGroup(
                stringJewelryCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(stringJewelryCardLayout.createSequentialGroup()
                                .addComponent(stringLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(stringJewelryComboBox, 0, 185, Short.MAX_VALUE))
        );
        stringJewelryCardLayout.setVerticalGroup(
                stringJewelryCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(stringJewelryCardLayout.createSequentialGroup()
                                .addGroup(stringJewelryCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(stringJewelryComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(stringLabel))
                                .addGap(0, 78, Short.MAX_VALUE))
        );

        cardPanel.add(stringJewelryCard, "stringJewelryCard");

        plankMakeCard.setName("plankMakeCard"); // NOI18N

        plankLabel.setText("Plank:");

        plankComboBox.setModel(new DefaultComboBoxModel<>(Plank.values()));

        javax.swing.GroupLayout plankMakeCardLayout = new javax.swing.GroupLayout(plankMakeCard);
        plankMakeCard.setLayout(plankMakeCardLayout);
        plankMakeCardLayout.setHorizontalGroup(
                plankMakeCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(plankMakeCardLayout.createSequentialGroup()
                                .addComponent(plankLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(plankComboBox, 0, 188, Short.MAX_VALUE))
        );
        plankMakeCardLayout.setVerticalGroup(
                plankMakeCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(plankMakeCardLayout.createSequentialGroup()
                                .addGroup(plankMakeCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(plankComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(plankLabel))
                                .addGap(0, 78, Short.MAX_VALUE))
        );

        cardPanel.add(plankMakeCard, "plankMakeCard");

        startButton.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        startButton.setText("Start");
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout contentPanelLayout = new javax.swing.GroupLayout(contentPanel);
        contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout.setHorizontalGroup(
                contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(contentPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(startButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(contentPanelLayout.createSequentialGroup()
                                                .addComponent(welcomePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(scriptOptionsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                                                        .addComponent(cardPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                                                .addGap(0, 1, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        contentPanelLayout.setVerticalGroup(
                contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(contentPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(contentPanelLayout.createSequentialGroup()
                                                .addComponent(scriptOptionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(cardPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(welcomePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(startButton)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        fileMenu.setText("File");

        newMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        newMenuItem.setText("New");
        newMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(newMenuItem);

        saveMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveMenuItem.setText("Save");
        saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText("Help");

        faqMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        faqMenuItem.setText("F.A.Q.");
        faqMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                faqMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(faqMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void stopAtLevelSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_stopAtLevelSpinnerStateChanged
        if ((int) stopAtLevelSpinner.getValue() < SKILLS.MAGIC.getActualLevel() + 1) {
            stopAtLevelSpinner.setValue(SKILLS.MAGIC.getActualLevel() + 1);
            Client.println("You cannot stop at any level below your current level + 1");
        }
    }//GEN-LAST:event_stopAtLevelSpinnerStateChanged

    private void saveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMenuItemActionPerformed
        saveTemplate();
        saveScriptOptions();
    }//GEN-LAST:event_saveMenuItemActionPerformed

    private void newMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newMenuItemActionPerformed
        setBlank();
    }//GEN-LAST:event_newMenuItemActionPerformed

    private void deleteTemplateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteTemplateButtonActionPerformed
        deleteCurrentTemplate();
    }//GEN-LAST:event_deleteTemplateButtonActionPerformed

    private void savedTemplatesBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_savedTemplatesBoxActionPerformed
        Object selected = savedTemplatesBox.getSelectedItem();
        if (selected == null) {
            return;
        }
        System.out.println("Opening template");
        openTemplate(selected.toString());
    }//GEN-LAST:event_savedTemplatesBoxActionPerformed

    private void searchBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBoxActionPerformed
        Object selected = searchBox.getSelectedItem();
        CardLayout cl = ((CardLayout) cardPanel.getLayout());
        if (selected instanceof NormalSpell) {
            switch (((NormalSpell) selected).getType()) {
                case ALCHEMY:
                    showingCard = "alchemyCard";
                    break;
                case BOLT_ENCHANTMENT:
                    showingCard = "noSpellOptionsCard";
                    break;
                case BONES_TO_FRUIT:
                    showingCard = "bonesToFruitCard";
                    break;
                case CURSE:
                    showingCard = "curseCard";
                    break;
                case ENCHANTMENT:
                    showingCard = "enchantmentCard";
                    break;
                case SUPERHEAT_ITEM:
                    showingCard = "superheatItemCard";
                    break;
                case TELEKINETIC_GRAB:
                    showingCard = "telekineticGrabCard";
                    break;
                case TELE_OTHER:
                    showingCard = "teleOtherCard";
                    break;
                case TELEPORT:
                    showingCard = "noSpellOptionsCard";
                    break;
            }
            if (selected == NormalSpell.CURSE_ALCHEMY) {
                showingCard = "curseAlchemyCard";
            }
        } else if (selected instanceof LunarSpell) {
            LunarSpell spell = (LunarSpell) selected;
            if (spell.name().contains("TELE")) {
                showingCard = "noSpellOptionsCard";
            } else {
                switch (spell) {
                    case BAKE_PIE:
                        showingCard = "bakePieCard";
                        break;
                    case HUMIDIFY:
                        showingCard = "humidifyCard";
                        break;
                    case SUPERGLASS_MAKE:
                        showingCard = "superglassMakeCard";
                        break;
                    case STRING_JEWELRY:
                        showingCard = "stringJewelryCard";
                        break;
                    case PLANK_MAKE:
                        showingCard = "plankMakeCard";
                        break;
                }
            }
        }
        cl.show(cardPanel, showingCard);
    }//GEN-LAST:event_searchBoxActionPerformed

    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
        try {
            Object selected = searchBox.getSelectedItem();
            if (selected instanceof NormalSpell) {
                NormalSpell spell = (NormalSpell) selected;
                switch (spell.getType()) {
                    case ALCHEMY:
                        TaskManager.setTask(new Alchemy(spell, alchItemComboBox.getSelectedItem().toString()));
                        break;
                    case BOLT_ENCHANTMENT:
                        String boltName;
                        if (spell == NormalSpell.DRAGONSTONE_BOLT_ENCHANT) {
                            boltName = "Dragon bolts";
                        } else {
                            boltName = spell.getName().replaceAll(" enchant", "s");
                        }
                        TaskManager.setTask(new EnchantBolt(spell, boltName));
                        break;
                    case BONES_TO_FRUIT:
                        TaskManager.setTask(new BonesToFruit(spell, boneComboBox.getSelectedItem().toString(),
                                ((Amount) boneAmountComboBox.getSelectedItem()).getAmount()));
                        break;
                    case CURSE:
                        TaskManager.setTask(new Curse(spell, curseNPCComboBox.getSelectedItem().toString()));
                        break;
                    case ENCHANTMENT:
                        String itemName = "";
                        switch (spell) {
                            case ENCHANT_LEVEL_ONE:
                                itemName = "Sapphire ";
                                break;
                            case ENCHANT_LEVEL_TWO:
                                itemName = "Emerald ";
                                break;
                            case ENCHANT_LEVEL_THREE:
                                itemName = "Ruby ";
                                break;
                            case ENCHANT_LEVEL_FOUR:
                                itemName = "Diamond ";
                                break;
                            case ENCHANT_LEVEL_FIVE:
                                if (enchantComboBox.getSelectedItem().toString().toLowerCase().equalsIgnoreCase("necklace")) {
                                    itemName = "Dragon ";
                                } else {
                                    itemName = "Dragonstone ";
                                }
                                break;
                            case ENCHANT_LEVEL_SIX:
                                itemName = "Onyx ";
                                break;
                        }
                        String selection = enchantComboBox.getSelectedItem().toString().toLowerCase();
                        TaskManager.setTask(new Enchant(spell, itemName + selection));
                        break;
                    case SUPERHEAT_ITEM:
                        TaskManager.setTask(new Superheat((SuperheatBar) barComboBox.getSelectedItem()));
                        break;
                    case TELEPORT:
                        TaskManager.setTask(new Teleport(spell));
                        break;

                }
                if (spell == NormalSpell.CURSE_ALCHEMY) {
                    TaskManager.setTask(new CurseAlchemy((NormalSpell) curseAlchAlchBox.getSelectedItem(), (NormalSpell) curseAlchCurseBox.getSelectedItem(),
                            curseAlchItemBox.getSelectedItem().toString(), curseAlchNPCBox.getSelectedItem().toString()));
                }
            } else if (selected instanceof LunarSpell) {
                LunarSpell spell = (LunarSpell) selected;
                if (spell.name().contains("TELE")) {
                    TaskManager.setTask(new Teleport(spell));
                } else {
                    switch (spell) {
                        case BAKE_PIE:
                            TaskManager.setTask(new BakePie((Pie) pieComboBox.getSelectedItem()));
                            break;
                        case HUMIDIFY:
                            TaskManager.setTask(new Humidify(humidifyComboBox.getSelectedItem().toString()));
                            break;
                        case SUPERGLASS_MAKE:
                            TaskManager.setTask(new SuperglassMake(glassMakeIngredientComboBox.getSelectedItem().toString()));
                            break;
                        case STRING_JEWELRY:
                            TaskManager.setTask(new StringJewelry(stringJewelryComboBox.getSelectedItem().toString()));
                            break;
                        case PLANK_MAKE:
                            TaskManager.setTask(new PlankMake((Plank) plankComboBox.getSelectedItem()));
                            break;
                    }
                }
            }
            saveScriptOptions();
            Mouse.setSpeed((int) mouseSpeedSpinner.getValue());
            final int stopLevel = (int) stopAtLevelSpinner.getValue();
            if (stopAtCheck.isSelected()) {
                TaskManager.getTask().addTerminateCondition(new TerminateCondition() {
                    @Override
                    public boolean isMet() {
                        if (Login07.isLoggedIn()) {
                            return SKILLS.MAGIC.getActualLevel() >= stopLevel;
                        }
                        return false;
                    }

                    @Override
                    public String diagnosis() {
                        return "Reached level " + stopLevel + " magic.";
                    }
                });
            }
            notifyScriptKit();
            dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_startButtonActionPerformed

    private void faqMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_faqMenuItemActionPerformed
        if (faq == null) {
            faq = new SigmaMagicFAQDialog(this);
        }
        faq.setVisible(true);
        faq.requestFocus();
        faq.setLocationRelativeTo(null);
    }//GEN-LAST:event_faqMenuItemActionPerformed

    private void mouseSpeedSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_mouseSpeedSpinnerStateChanged
        int value = (int) mouseSpeedSpinner.getValue();
        if (value < 70) {
            Client.println("You cannot have a mouse speed slower than 70.");
            mouseSpeedSpinner.setValue(70);
        } else if (value > 200) {
            Client.println("You cannot have a mouse speed greater than 200.");
            mouseSpeedSpinner.setValue(200);
        }
    }//GEN-LAST:event_mouseSpeedSpinnerStateChanged

    private void curseAlchCurseBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_curseAlchCurseBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_curseAlchCurseBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JComboBox alchItemComboBox;
    private javax.swing.JLabel alchItemLabel;
    private JPanel alchemyCard;
    private JPanel bakePieCard;
    private JComboBox barComboBox;
    private javax.swing.JLabel barLabel;
    private JComboBox boneAmountComboBox;
    private javax.swing.JLabel boneAmountLabel;
    private JComboBox boneComboBox;
    private javax.swing.JLabel boneLabel;
    private JPanel bonesToFruitCard;
    private JPanel cardPanel;
    private javax.swing.JRadioButton casterRadioButton;
    private JPanel contentPanel;
    private JComboBox curseAlchAlchBox;
    private JComboBox curseAlchCurseBox;
    private JComboBox curseAlchItemBox;
    private JComboBox curseAlchNPCBox;
    private JPanel curseAlchemyCard;
    private JPanel curseCard;
    private JComboBox curseNPCComboBox;
    private javax.swing.JLabel curseNPCLabel;
    private javax.swing.JButton deleteTemplateButton;
    private JComboBox enchantComboBox;
    private javax.swing.JLabel enchantLabel;
    private JPanel enchantmentCard;
    private javax.swing.JMenuItem faqMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JTextField fileNameField;
    private javax.swing.JLabel fileNameLabel;
    private JPanel fileOptionPanel;
    private JComboBox glassMakeIngredientComboBox;
    private javax.swing.JMenu helpMenu;
    private JPanel humidifyCard;
    private JComboBox humidifyComboBox;
    private javax.swing.JLabel humidifyLabel;
    private javax.swing.JLabel ingredientLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JLabel mouseSpeedLabel;
    private JSpinner mouseSpeedSpinner;
    private javax.swing.JMenuItem newMenuItem;
    private javax.swing.JLabel noAdditionalOptionsLabel;
    private JPanel noSpellOptionsCard;
    private JComboBox pieComboBox;
    private javax.swing.JLabel pieLabel;
    private JComboBox plankComboBox;
    private javax.swing.JLabel plankLabel;
    private JPanel plankMakeCard;
    private javax.swing.JRadioButton receiverRadioButton;
    private javax.swing.JMenuItem saveMenuItem;
    private JComboBox savedTemplatesBox;
    private javax.swing.JLabel savedTemplatesLabel;
    private JPanel scriptOptionsPanel;
    private JComboBox searchBox;
    private JPanel searchPanel;
    private javax.swing.JButton startButton;
    private JCheckBox stopAtCheck;
    private JSpinner stopAtLevelSpinner;
    private JPanel stringJewelryCard;
    private JComboBox stringJewelryComboBox;
    private javax.swing.JLabel stringLabel;
    private JPanel superglassMakeCard;
    private JPanel superheatItemCard;
    private JPanel teleOtherCard;
    private javax.swing.ButtonGroup teleOtherGroup;
    private JPanel telekineticGrabCard;
    private javax.swing.JTextField tkGrabItemNameField;
    private javax.swing.JLabel tkGrabLabel;
    private JPanel welcomePanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void dispose() {
        inventoryListener.stop();
        if (faq != null) {
            faq.dispose();
        }
        super.dispose();
    }

    @Override
    public void itemAdded(int id, RSItemDefinition definition, int amount, InventoryListener.Source source) {
        updateComboBoxes();
    }

    @Override
    public void itemRemoved(int id, RSItemDefinition definition, int amount, InventoryListener.Source source) {
        updateComboBoxes();
    }

    @Override
    public void load() {

    }

    @Override
    public void save() {

    }
}
