package com.visualmetronome.panel;

import com.visualmetronome.FontTypes;
import com.visualmetronome.VisualMetronomeConfig;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.Keybind;
import net.runelite.client.party.PartyMember;
import net.runelite.client.party.PartyService;
import net.runelite.client.ui.PluginPanel;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.BorderFactory;

import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;


import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class VisualMetronomePanel extends PluginPanel
{
    // General Metronome
    private final JCheckBox enableMetronome;
    private final JCheckBox highlightCurrentTile;
    private final JSpinner boxWidth;
    private final JSpinner tickCount;

    // Tick Number
    private final JCheckBox showTick;
    private final JCheckBox showPlayerTick;
    private final JCheckBox disableFontScaling;
    private final JSpinner fontSize;
    private final ColorButtonPanel numberColorBtn;
    private final JComboBox<String> fontType;

    // True Tile Overlay
    private final ColorButtonPanel currentTileFillColorBtn;
    private final JSpinner currentTileBorderWidth;
    private final JCheckBox changeFillColor;
    private final JSpinner changeFillColorOpacity;

    // Party Sync
    private final JCheckBox enablePartySync;
    private final JComboBox<String> memberDropdown;
    private String lastSelectedMember;

    // Colors
    private final JSpinner colorCycleSpinner;
    private final ColorButtonPanel[] tickColorBtns = new ColorButtonPanel[10];

    // Hotkeys
    private final JButton tickResetHotkeyBtn;
    private final JButton resetHotkeyBtn;
    private Keybind tickResetHotkey;
    private final JSpinner tickResetStartTick;

    // Mouse Following
    private final JCheckBox mouseFollowingTick;
    private final JSpinner mouseOffsetX;
    private final JSpinner mouseOffsetY;

    // Additional Overhead Cycles
    private final JCheckBox enableCycle2;
    private final JSpinner tickCount2;
    private final ColorButtonPanel cycle2ColorBtn;
    private final JCheckBox enableCycle3;
    private final JSpinner tickCount3;
    private final ColorButtonPanel cycle3ColorBtn;
    private final JSpinner overheadCyclesGapDistance;
    private final JSpinner overheadHeight;
    private final JSpinner overheadXCenterOffset;
    private final JCheckBox overheadUseCurrentColor;

    private final ConfigManager configManager;
    private final PartyService partyService;

    public boolean updatingFromConfig = false;

    public VisualMetronomePanel(ConfigManager configManager,VisualMetronomeConfig config, PartyService partyService)
    {
        this.configManager = configManager;
        this.partyService = partyService;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // --- Party Sync Section ---
        JPanel partySyncPanel = new JPanel();
        partySyncPanel.setLayout(new BoxLayout(partySyncPanel, BoxLayout.Y_AXIS));
        enablePartySync = new JCheckBox();
        partySyncPanel.add(labeledCheckbox("Enable Tick Sync", enablePartySync));
        memberDropdown = new JComboBox<>();
        partySyncPanel.add(labeled("Sync Target:", memberDropdown));
        JButton refreshMembersBtn = getRefreshMembersBtn(configManager, config, partyService);
        partySyncPanel.add(refreshMembersBtn);  // add the button to your Party Sync panel
        add(new CollapsibleSection("Party Sync Settings", partySyncPanel));

        // --- General Metronome Section ---
        JPanel generalPanel = new JPanel();
        generalPanel.setLayout(new BoxLayout(generalPanel, BoxLayout.Y_AXIS));
        enableMetronome = new JCheckBox();
        generalPanel.add(labeledCheckbox("Enable Visual Metronome", enableMetronome));
        highlightCurrentTile = new JCheckBox();
        generalPanel.add(labeledCheckbox("Enable True Tile Overlay", highlightCurrentTile));
        boxWidth = spinner(25, 16, 200, 1);
        tickCount = spinner(1, 1, 10, 1);
        generalPanel.add(labeled("Box Width:", boxWidth));
        generalPanel.add(labeled("Tick Count:", tickCount));
        add(new CollapsibleSection("General Metronome", generalPanel));

        // --- Tick Number Section ---
        JPanel tickNumberPanel = new JPanel();
        tickNumberPanel.setLayout(new BoxLayout(tickNumberPanel, BoxLayout.Y_AXIS));
        showTick = new JCheckBox();
        tickNumberPanel.add(labeledCheckbox("Show Metronome Tick Number", showTick));
        showPlayerTick = new JCheckBox();
        tickNumberPanel.add(labeledCheckbox("Show Tick Above Player", showPlayerTick));
        disableFontScaling = new JCheckBox();
        tickNumberPanel.add(labeledCheckbox("Disable Font Scaling", disableFontScaling));
        fontSize = spinner(15, 8, 50, 1);
        numberColorBtn = new ColorButtonPanel("Tick Number Color", Color.CYAN);
        fontType = new JComboBox<>(Arrays.stream(FontTypes.values())
                .map(FontTypes::name)
                .toArray(String[]::new));
        tickNumberPanel.add(labeled("Font Size:", fontSize));
        tickNumberPanel.add(numberColorBtn);
        tickNumberPanel.add(labeled("Font Type:", fontType));
        add(new CollapsibleSection("Tick Number Settings", tickNumberPanel));

        // --- True Tile Overlay Section ---
        JPanel tilePanel = new JPanel();
        tilePanel.setLayout(new BoxLayout(tilePanel, BoxLayout.Y_AXIS));
        currentTileFillColorBtn = new ColorButtonPanel("Tile Fill Color", new Color(0, 0, 0, 50));
        currentTileBorderWidth = spinner(2, 0, 10, 0.5);
        changeFillColor = new JCheckBox();
        tilePanel.add(labeledCheckbox("Enable Tile Fill Metronome", changeFillColor));
        changeFillColorOpacity = spinner(50, 0, 255, 1);
        tilePanel.add(currentTileFillColorBtn);
        tilePanel.add(labeled("Tile Border Width:", currentTileBorderWidth));
        tilePanel.add(labeled("Fill Color Opacity:", changeFillColorOpacity));
        add(new CollapsibleSection("True Tile Overlay Settings", tilePanel));

        // --- Color Settings Section ---
        JPanel colorPanel = new JPanel();
        colorPanel.setLayout(new BoxLayout(colorPanel, BoxLayout.Y_AXIS));
        colorCycleSpinner = spinner(2, 2, 10, 1);
        colorPanel.add(labeled("Number of Colors:", colorCycleSpinner));

        tickColorBtns[0] = new ColorButtonPanel("Tick Color", Color.LIGHT_GRAY);
        colorPanel.add(tickColorBtns[0]);
        tickColorBtns[1] = new ColorButtonPanel("Tock Color", Color.LIGHT_GRAY);
        colorPanel.add(tickColorBtns[1]);

        for (int i = 2; i < 10; i++) {
            tickColorBtns[i] = new ColorButtonPanel((i + 1) + " Tick Color", Color.LIGHT_GRAY);
            colorPanel.add(tickColorBtns[i]);
        }
        add(new CollapsibleSection("Color Settings", colorPanel));


        // --- Hotkeys Section ---
        JPanel hotkeyPanel = new JPanel();
        hotkeyPanel.setLayout(new BoxLayout(hotkeyPanel, BoxLayout.Y_AXIS));
        tickResetHotkeyBtn = new JButton("Set Reset Hotkey");
        tickResetHotkeyBtn.addActionListener(e -> {
            Keybind newKeybind = promptForKeybind();
            if (newKeybind != null) {
                tickResetHotkey = newKeybind;
                tickResetHotkeyBtn.setText("Hotkey: " + newKeybind.toString());
                updateConfigThrottled(); // update directly
            }
        });
        resetHotkeyBtn = new JButton("Reset Hotkey");
        resetHotkeyBtn.addActionListener(e -> {
            tickResetHotkey = Keybind.NOT_SET;
            tickResetHotkeyBtn.setText("Set Reset Hotkey");
            updateConfigThrottled();
        });
        tickResetStartTick = spinner(0, 0, 10, 1);

        JPanel hotkeyButtonsPanel = new JPanel();
        hotkeyButtonsPanel.setLayout(new BoxLayout(hotkeyButtonsPanel, BoxLayout.X_AXIS));
        hotkeyButtonsPanel.add(tickResetHotkeyBtn);
        hotkeyButtonsPanel.add(Box.createHorizontalStrut(5));
        hotkeyButtonsPanel.add(resetHotkeyBtn);
        hotkeyButtonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        hotkeyPanel.add(hotkeyButtonsPanel);
        hotkeyPanel.add(labeled("Reset to Tick:", tickResetStartTick));
        add(new CollapsibleSection("Hotkey Settings", hotkeyPanel));


        // --- Mouse Following Section ---
        JPanel mousePanel = new JPanel();
        mousePanel.setLayout(new BoxLayout(mousePanel, BoxLayout.Y_AXIS));
        mouseFollowingTick = new JCheckBox();
        mousePanel.add(labeledCheckbox("Tick Counter Follows Mouse", mouseFollowingTick));
        mouseOffsetX = spinner(10, -100, 100, 1);
        mouseOffsetY = spinner(-10, -100, 100, 1);
        mousePanel.add(labeled("Mouse Offset X:", mouseOffsetX));
        mousePanel.add(labeled("Mouse Offset Y:", mouseOffsetY));
        add(new CollapsibleSection("Mouse Following Settings", mousePanel));

        // --- Additional Overhead Cycles Section ---
        JPanel overheadPanel = new JPanel();
        overheadPanel.setLayout(new BoxLayout(overheadPanel, BoxLayout.Y_AXIS));
        enableCycle2 = new JCheckBox();
        overheadPanel.add(labeledCheckbox("Enable Second Cycle", enableCycle2));
        tickCount2 = spinner(2, 2, 20, 1);
        cycle2ColorBtn = new ColorButtonPanel("Second Cycle Color", Color.CYAN);
        overheadPanel.add(labeled("Second Cycle Length:", tickCount2));
        overheadPanel.add(cycle2ColorBtn);
        enableCycle3 = new JCheckBox();
        overheadPanel.add(labeledCheckbox("Enable Third Cycle", enableCycle3));
        tickCount3 = spinner(2, 2, 20, 1);
        cycle3ColorBtn = new ColorButtonPanel("Third Cycle Color", Color.CYAN);
        overheadPanel.add(labeled("Third Cycle Length:", tickCount3));
        overheadPanel.add(cycle3ColorBtn);
        overheadCyclesGapDistance = spinner(20, 0, 100, 1);
        overheadHeight = spinner(20, -500, 500, 1);
        overheadXCenterOffset = spinner(0, -50, 50, 1);
        overheadUseCurrentColor = new JCheckBox();
        overheadPanel.add(labeledCheckbox("Metronome Color for Overhead", overheadUseCurrentColor));
        overheadPanel.add(labeled("Gap Distance:", overheadCyclesGapDistance));
        overheadPanel.add(labeled("Overhead Height:", overheadHeight));
        overheadPanel.add(labeled("X Center Offset:", overheadXCenterOffset));
        add(new CollapsibleSection("Additional Overhead Cycles", overheadPanel));

        setupListeners();
    }

    // --- Utility builders ---
    private JButton getRefreshMembersBtn(ConfigManager configManager, VisualMetronomeConfig config, PartyService partyService) {
        JButton refreshMembersBtn = new JButton("Refresh Members");
        refreshMembersBtn.addActionListener(e -> {
            if (partyService != null) {
                List<PartyMember> membersList = partyService.getMembers();
                List<String> memberNames = membersList.stream()
                        .map(PartyMember::getDisplayName)
                        .filter(name -> !"<unknown>".equals(name))
                        .collect(Collectors.toList());
                SwingUtilities.invokeLater(() -> updateMembers(memberNames, config, configManager));
            }
        });
        return refreshMembersBtn;
    }

    private JLabel sectionLabel(String text)
    {
        JLabel label = new JLabel(text, SwingConstants.LEADING);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        label.setBorder(BorderFactory.createEmptyBorder(8, 0, 4, 0));
        return label;
    }

    private JPanel labeled(String text, JComponent comp)
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        comp.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(comp);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(BorderFactory.createEmptyBorder(2, 0, 4, 0));
        return panel;
    }

    private JPanel labeledCheckbox(String text, JCheckBox checkBox)
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel label = new JLabel(text);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        panel.add(label, BorderLayout.WEST);

        checkBox.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(checkBox, BorderLayout.EAST);

        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(BorderFactory.createEmptyBorder(2, 0, 4, 0));
        return panel;
    }

    private JSpinner spinner(Number value, Number min, Number max, Number step)
    {
        return new JSpinner(new SpinnerNumberModel(value.doubleValue(), min.doubleValue(), max.doubleValue(), step.doubleValue()));
    }

    public void updateMembers(List<String> members, VisualMetronomeConfig config, ConfigManager configManager)
    {
        if (members == null) return;

        SwingUtilities.invokeLater(() -> {

            ActionListener[] listeners = memberDropdown.getActionListeners();
            for (ActionListener l : listeners) {
                memberDropdown.removeActionListener(l);
            }

            memberDropdown.removeAllItems();

            Set<String> uniqueMembers = new LinkedHashSet<>(members);
            uniqueMembers.remove("<unknown>");
            if (lastSelectedMember != null) uniqueMembers.remove(lastSelectedMember);

            if (lastSelectedMember != null) {
                memberDropdown.addItem(lastSelectedMember);
            }

            for (String member : uniqueMembers) {
                memberDropdown.addItem(member);
            }

            // Set selection to lastSelectedMember if available, otherwise first item
            if (lastSelectedMember != null && memberDropdown.getItemCount() > 0) {
                memberDropdown.setSelectedItem(lastSelectedMember);
            } else if (memberDropdown.getItemCount() > 0) {
                memberDropdown.setSelectedIndex(0);
            }

            if (lastSelectedMember != config.syncTarget())
            {
                configManager.setConfiguration("visualmetronome", "syncTarget", lastSelectedMember);
            }

            // Reattach the action listeners
            for (ActionListener l : listeners) {
                memberDropdown.addActionListener(l);
            }
        });
    }


    // --- General Metronome ---
    public boolean isEnableMetronome() { return enableMetronome.isSelected(); }
    public boolean isHighlightCurrentTile() { return highlightCurrentTile.isSelected(); }
    public int getBoxWidth() { return ((Double) boxWidth.getValue()).intValue(); }
    public int getTickCount() { return ((Double) tickCount.getValue()).intValue(); }

    // --- Tick Number ---
    public boolean isShowTick() { return showTick.isSelected(); }
    public boolean isShowPlayerTick() { return showPlayerTick.isSelected(); }
    public boolean isDisableFontScaling() { return disableFontScaling.isSelected(); }
    public int getFontSize() { return ((Double) fontSize.getValue()).intValue(); }
    public Color getNumberColor() { return numberColorBtn.getBackground(); }
    public String getFontType() { return (String) fontType.getSelectedItem(); }

    // --- True Tile Overlay ---
    public Color getCurrentTileFillColor() { return currentTileFillColorBtn.getBackground(); }
    public double getCurrentTileBorderWidth() { return (Double) currentTileBorderWidth.getValue(); }
    public boolean isChangeFillColor() { return changeFillColor.isSelected(); }
    public int getChangeFillColorOpacity() { return ((Double) changeFillColorOpacity.getValue()).intValue(); }

    // --- Party Sync ---
    public boolean isEnablePartySync() { return enablePartySync.isSelected(); }
    public String getSelectedMember() { return (String) memberDropdown.getSelectedItem(); }

    // --- Color Settings ---
    public int getColorCycle() { return ((Double) colorCycleSpinner.getValue()).intValue(); }
    public Color getTickColor(int i)
    {
        if (i < 1 || i > 10) throw new IllegalArgumentException("Tick index must be 1-10");
        return tickColorBtns[i-1].getBackground();
    }

    // --- Hotkeys ---
    public int getTickResetStartTick() { return ((Double) tickResetStartTick.getValue()).intValue(); }
    public Keybind getTickResetHotkey() {return tickResetHotkey;}

    // --- Mouse Following ---
    public boolean isMouseFollowingTick() { return mouseFollowingTick.isSelected(); }
    public int getMouseOffsetX() { return ((Double) mouseOffsetX.getValue()).intValue(); }
    public int getMouseOffsetY() { return ((Double) mouseOffsetY.getValue()).intValue(); }

    // --- Additional Overhead Cycles ---
    public boolean isEnableCycle2() { return enableCycle2.isSelected(); }
    public int getTickCount2() { return ((Double) tickCount2.getValue()).intValue(); }
    public Color getCycle2Color() { return cycle2ColorBtn.getBackground(); }

    public boolean isEnableCycle3() { return enableCycle3.isSelected(); }
    public int getTickCount3() { return ((Double) tickCount3.getValue()).intValue(); }
    public Color getCycle3Color() { return cycle3ColorBtn.getBackground(); }

    public int getOverheadCyclesGapDistance() { return ((Double) overheadCyclesGapDistance.getValue()).intValue(); }
    public int getOverheadHeight() { return ((Double) overheadHeight.getValue()).intValue(); }
    public int getOverheadXCenterOffset() { return ((Double) overheadXCenterOffset.getValue()).intValue(); }
    public boolean isOverheadUseCurrentColor() { return overheadUseCurrentColor.isSelected(); }


    public class CollapsibleSection extends JPanel
    {
        private final JPanel contentPanel;
        private final JToggleButton toggle;

        public CollapsibleSection(String title, JPanel content)
        {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

            contentPanel = content;

            contentPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.BLACK),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));

            contentPanel.setVisible(true);

            toggle = new JToggleButton(title);
            toggle.setSelected(true);
            toggle.addActionListener(e -> {
                contentPanel.setVisible(toggle.isSelected());
                revalidate();
                repaint();
            });

            add(toggle, BorderLayout.NORTH);
            add(contentPanel, BorderLayout.CENTER);
        }
    }

    public void loadFromConfig(VisualMetronomeConfig config) {
        updatingFromConfig = true;

        enableMetronome.setSelected(config.enableMetronome());
        highlightCurrentTile.setSelected(config.highlightCurrentTile());
        boxWidth.setValue((double) config.boxWidth());
        tickCount.setValue((double) config.tickCount());

        showTick.setSelected(config.showTick());
        showPlayerTick.setSelected(config.showPlayerTick());
        disableFontScaling.setSelected(config.disableFontScaling());
        fontSize.setValue((double) config.fontSize());
        numberColorBtn.setColor(config.NumberColor());
        fontType.setSelectedItem(config.fontType().name());

        currentTileFillColorBtn.setColor(config.currentTileFillColor());
        currentTileBorderWidth.setValue(config.currentTileBorderWidth());
        changeFillColor.setSelected(config.changeFillColor());
        changeFillColorOpacity.setValue((double) config.changeFillColorOpacity());

        enablePartySync.setSelected(config.enablePartySync());
        lastSelectedMember = config.syncTarget();

        if (lastSelectedMember != null &&
                Arrays.stream(memberDropdown.getModel().getSelectedItem() != null ? new String[]{memberDropdown.getSelectedItem().toString()} : new String[]{})
                        .noneMatch(s -> s.equals(lastSelectedMember)))
        {
            memberDropdown.addItem(lastSelectedMember);
        }

        colorCycleSpinner.setValue((double) config.colorCycle());
        tickColorBtns[0].setColor(config.getTickColor());
        tickColorBtns[1].setColor(config.getTockColor());
        tickColorBtns[2].setColor(config.getTick3Color());
        tickColorBtns[3].setColor(config.getTick4Color());
        tickColorBtns[4].setColor(config.getTick5Color());
        tickColorBtns[5].setColor(config.getTick6Color());
        tickColorBtns[6].setColor(config.getTick7Color());
        tickColorBtns[7].setColor(config.getTick8Color());
        tickColorBtns[8].setColor(config.getTick9Color());
        tickColorBtns[9].setColor(config.getTick10Color());

        tickResetHotkey = config.tickResetHotkey();
        if (tickResetHotkey != null && tickResetHotkey != Keybind.NOT_SET) {
            tickResetHotkeyBtn.setText("Hotkey: " + tickResetHotkey.toString());
        } else {
            tickResetHotkeyBtn.setText("Set Reset Hotkey");
        }

        tickResetStartTick.setValue((double) config.tickResetStartTick());
        mouseFollowingTick.setSelected(config.mouseFollowingTick());
        mouseOffsetX.setValue((double) config.mouseOffsetX());
        mouseOffsetY.setValue((double) config.mouseOffsetY());

        enableCycle2.setSelected(config.enableCycle2());
        tickCount2.setValue((double) config.tickCount2());
        cycle2ColorBtn.setColor(config.cycle2Color());

        enableCycle3.setSelected(config.enableCycle3());
        tickCount3.setValue((double) config.tickCount3());
        cycle3ColorBtn.setColor(config.cycle3Color());

        overheadCyclesGapDistance.setValue((double) config.overheadCyclesGapDistance());
        overheadHeight.setValue((double) config.overheadHeight());
        overheadXCenterOffset.setValue((double) config.overheadXCenterOffset());
        overheadUseCurrentColor.setSelected(config.overheadUseCurrentColor());

        updatingFromConfig = false;
    }
    private void setupListeners() {
        ActionListener updateAction = e -> updateConfigThrottled();
        ChangeListener updateChange = e -> updateConfigThrottled();

        // --- General Metronome ---
        enableMetronome.addActionListener(updateAction);
        highlightCurrentTile.addActionListener(updateAction);
        boxWidth.addChangeListener(updateChange);
        tickCount.addChangeListener(updateChange);

        // --- Tick Number ---
        showTick.addActionListener(updateAction);
        showPlayerTick.addActionListener(updateAction);
        disableFontScaling.addActionListener(updateAction);
        fontSize.addChangeListener(updateChange);
        fontType.addActionListener(updateAction);
        numberColorBtn.addColorChangeListener(c -> updateConfigThrottled());

        // --- True Tile Overlay ---
        currentTileFillColorBtn.addColorChangeListener(c -> updateConfigThrottled());
        currentTileBorderWidth.addChangeListener(updateChange);
        changeFillColor.addActionListener(updateAction);
        changeFillColorOpacity.addChangeListener(updateChange);

        // --- Party Sync ---
        enablePartySync.addActionListener(updateAction);
        memberDropdown.addActionListener(updateAction);

        // --- Color Settings ---
        colorCycleSpinner.addChangeListener(updateChange);
        for (ColorButtonPanel btn : tickColorBtns) {
            btn.addColorChangeListener(c -> updateConfigThrottled());
        }

        // --- Hotkeys ---
        tickResetStartTick.addChangeListener(updateChange);
        //tickResetHotkeyBtn.addActionListener(updateAction);

        // --- Mouse Following ---
        mouseFollowingTick.addActionListener(updateAction);
        mouseOffsetX.addChangeListener(updateChange);
        mouseOffsetY.addChangeListener(updateChange);

        // --- Additional Overhead Cycles ---
        enableCycle2.addActionListener(updateAction);
        tickCount2.addChangeListener(updateChange);
        cycle2ColorBtn.addColorChangeListener(c -> updateConfigThrottled());

        enableCycle3.addActionListener(updateAction);
        tickCount3.addChangeListener(updateChange);
        cycle3ColorBtn.addColorChangeListener(c -> updateConfigThrottled());

        overheadCyclesGapDistance.addChangeListener(updateChange);
        overheadHeight.addChangeListener(updateChange);
        overheadXCenterOffset.addChangeListener(updateChange);
        overheadUseCurrentColor.addActionListener(updateAction);
    }

    private Keybind promptForKeybind()
    {
        final KeyCaptureDialog dialog = new KeyCaptureDialog();
        return dialog.showAndGetKeybind();
    }

    private void updateConfig()
    {
        //System.out.println("[DEBUG] updateConfig called at " + System.currentTimeMillis());
        if (updatingFromConfig) return;

        // --- General Metronome ---
        configManager.setConfiguration("visualmetronome", "enableMetronome", isEnableMetronome());
        configManager.setConfiguration("visualmetronome", "highlightCurrentTile", isHighlightCurrentTile());
        configManager.setConfiguration("visualmetronome", "boxWidth", getBoxWidth());
        configManager.setConfiguration("visualmetronome", "tickCount", getTickCount());

        // --- Tick Number ---
        configManager.setConfiguration("visualmetronome", "showTick", isShowTick());
        configManager.setConfiguration("visualmetronome", "showPlayerTick", isShowPlayerTick());
        configManager.setConfiguration("visualmetronome", "disableFontScaling", isDisableFontScaling());
        configManager.setConfiguration("visualmetronome", "fontSize", getFontSize());
        configManager.setConfiguration("visualmetronome", "countColor", numberColorBtn.getColor());
        configManager.setConfiguration("visualmetronome", "fontType", getFontType());

        // --- True Tile Overlay ---
        configManager.setConfiguration("visualmetronome", "currentTileFillColor", currentTileFillColorBtn.getColor());
        configManager.setConfiguration("visualmetronome", "currentTileBorderWidth", getCurrentTileBorderWidth());
        configManager.setConfiguration("visualmetronome", "changeFillColor", isChangeFillColor());
        configManager.setConfiguration("visualmetronome", "changeFillColorOpacity", getChangeFillColorOpacity());

        // --- Party Sync ---
        lastSelectedMember = (String) memberDropdown.getSelectedItem();
        configManager.setConfiguration("visualmetronome", "enablePartySync", isEnablePartySync());
        configManager.setConfiguration("visualmetronome", "syncTarget", lastSelectedMember);

        // --- Color Settings ---
        configManager.setConfiguration("visualmetronome", "colorCycle", getColorCycle());
        configManager.setConfiguration("visualmetronome", "tickColor", tickColorBtns[0].getColor());
        configManager.setConfiguration("visualmetronome", "tockColor", tickColorBtns[1].getColor());
        configManager.setConfiguration("visualmetronome", "tick3Color", tickColorBtns[2].getColor());
        configManager.setConfiguration("visualmetronome", "tick4Color", tickColorBtns[3].getColor());
        configManager.setConfiguration("visualmetronome", "tick5Color", tickColorBtns[4].getColor());
        configManager.setConfiguration("visualmetronome", "tick6Color", tickColorBtns[5].getColor());
        configManager.setConfiguration("visualmetronome", "tick7Color", tickColorBtns[6].getColor());
        configManager.setConfiguration("visualmetronome", "tick8Color", tickColorBtns[7].getColor());
        configManager.setConfiguration("visualmetronome", "tick9Color", tickColorBtns[8].getColor());
        configManager.setConfiguration("visualmetronome", "tick10Color", tickColorBtns[9].getColor());

        // --- Hotkeys ---
        configManager.setConfiguration("visualmetronome", "tickResetStartTick", getTickResetStartTick());
        configManager.setConfiguration("visualmetronome", "tickResetHotkey", getTickResetHotkey());

        // --- Mouse Following ---
        configManager.setConfiguration("visualmetronome", "mouseFollowingTick", isMouseFollowingTick());
        configManager.setConfiguration("visualmetronome", "mouseOffsetX", getMouseOffsetX());
        configManager.setConfiguration("visualmetronome", "mouseOffsetY", getMouseOffsetY());

        // --- Additional Overhead Cycles ---
        configManager.setConfiguration("visualmetronome", "showSecondCycle", isEnableCycle2());
        configManager.setConfiguration("visualmetronome", "tickCount2", getTickCount2());
        configManager.setConfiguration("visualmetronome", "cycle2Color", cycle2ColorBtn.getColor());

        configManager.setConfiguration("visualmetronome", "showThirdCycle", isEnableCycle3());
        configManager.setConfiguration("visualmetronome", "tickCount3", getTickCount3());
        configManager.setConfiguration("visualmetronome", "cycle3Color", cycle3ColorBtn.getColor());

        configManager.setConfiguration("visualmetronome", "overheadCyclesGapDistance", getOverheadCyclesGapDistance());
        configManager.setConfiguration("visualmetronome", "overheadHeight", getOverheadHeight());
        configManager.setConfiguration("visualmetronome", "overheadXCenterOffset", getOverheadXCenterOffset());
        configManager.setConfiguration("visualmetronome", "overheadUseCurrentColor", isOverheadUseCurrentColor());
    }

    private boolean updatePending = false;

    private void updateConfigThrottled() {
        if (updatePending) return;
        updatePending = true;
        SwingUtilities.invokeLater(() -> {
            updateConfig();
            updatePending = false;
        });
    }
}
