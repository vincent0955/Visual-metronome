package com.visualmetronome.panel;

import com.visualmetronome.FontTypes;
import com.visualmetronome.VisualMetronomeConfig;
import com.visualmetronome.messages.ColorRequestMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.Keybind;
import net.runelite.client.party.PartyService;
import net.runelite.client.ui.PluginPanel;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SwingUtilities;
import javax.swing.BorderFactory;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionListener;

import java.util.Arrays;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Objects;

public class VisualMetronomePanel extends PluginPanel
{
    // General Metronome
    final JCheckBox enableMetronome;
    final JCheckBox highlightCurrentTile;
    final JSpinner boxWidth;
    final JSpinner tickCount;

    // Tick Number
    final JCheckBox showTick;
    final JCheckBox showPlayerTick;
    final JCheckBox disableFontScaling;
    final JSpinner fontSize;
    final ColorButtonPanel numberColorBtn;
    final JComboBox<String> fontType;

    // True Tile Overlay
    final ColorButtonPanel currentTileFillColorBtn;
    final JSpinner currentTileBorderWidth;
    final JCheckBox changeFillColor;
    final JSpinner changeFillColorOpacity;

    // Party Sync
    final JCheckBox enablePartySync;
    final JComboBox<String> memberDropdown;
    String lastSelectedMember;

    // Colors
    final JSpinner colorCycleSpinner;
    final ColorButtonPanel[] tickColorBtns = new ColorButtonPanel[10];

    // Hotkeys
    final JButton tickResetHotkeyBtn;
    final JButton resetHotkeyBtn;
    Keybind tickResetHotkey;
    final JSpinner tickResetStartTick;

    // Mouse Following
    final JCheckBox mouseFollowingTick;
    final JSpinner mouseOffsetX;
    final JSpinner mouseOffsetY;

    // Additional Overhead Cycles
    final JCheckBox enableCycle2;
    final JSpinner tickCount2;
    final ColorButtonPanel cycle2ColorBtn;
    final JCheckBox enableCycle3;
    final JSpinner tickCount3;
    final ColorButtonPanel cycle3ColorBtn;
    final JSpinner overheadCyclesGapDistance;
    final JSpinner overheadHeight;
    final JSpinner overheadXCenterOffset;
    final JCheckBox overheadUseCurrentColor;

    final ConfigManager configManager;
    final VisualMetronomeConfig config;
    final PartyService partyService;
    public final VisualMetronomeConfigHandler configHandler;

    public boolean updatingFromConfig = false;

    public VisualMetronomePanel(ConfigManager configManager,VisualMetronomeConfig config, PartyService partyService)
    {
        this.configManager = configManager;
        this.partyService = partyService;
        this.config = config;
        this.configHandler = new VisualMetronomeConfigHandler(configManager, config, this);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // --- Party Sync Section ---
        JPanel partySyncPanel = new JPanel();
        partySyncPanel.setLayout(new BoxLayout(partySyncPanel, BoxLayout.Y_AXIS));
        enablePartySync = new JCheckBox();
        partySyncPanel.add(GuiUtils.labeledCheckbox("Enable Tick Sync", enablePartySync));
        memberDropdown = new JComboBox<>();
        partySyncPanel.add(GuiUtils.labeled("Party Member:", memberDropdown));
        JButton refreshMembersBtn = GuiUtils.getRefreshMembersBtn(configManager, config, partyService,
                members -> updateMembers(members, config, configManager));
        partySyncPanel.add(refreshMembersBtn);
        add(new CollapsibleSection("Party Sync Settings", partySyncPanel));
        JButton requestColorsButton = new JButton("Color Sync");
        requestColorsButton.addActionListener(e ->
        {
            ColorRequestMessage message = new ColorRequestMessage(config.syncTarget());
            partyService.send(message);
        });


        // --- General Metronome Section ---
        JPanel generalPanel = new JPanel();
        generalPanel.setLayout(new BoxLayout(generalPanel, BoxLayout.Y_AXIS));
        enableMetronome = new JCheckBox();
        generalPanel.add(GuiUtils.labeledCheckbox("Enable Visual Metronome", enableMetronome));
        highlightCurrentTile = new JCheckBox();
        generalPanel.add(GuiUtils.labeledCheckbox("Enable True Tile Overlay", highlightCurrentTile));
        boxWidth = GuiUtils.spinner(25, 16, 200, 1);
        tickCount = GuiUtils.spinner(1, 1, 10, 1);
        generalPanel.add(GuiUtils.labeled("Box Width:", boxWidth));
        generalPanel.add(GuiUtils.labeled("Tick Count:", tickCount));
        add(new CollapsibleSection("General Metronome", generalPanel));

        // --- Tick Number Section ---
        JPanel tickNumberPanel = new JPanel();
        tickNumberPanel.setLayout(new BoxLayout(tickNumberPanel, BoxLayout.Y_AXIS));
        showTick = new JCheckBox();
        tickNumberPanel.add(GuiUtils.labeledCheckbox("Show Metronome Tick Number", showTick));
        showPlayerTick = new JCheckBox();
        tickNumberPanel.add(GuiUtils.labeledCheckbox("Show Tick Above Player", showPlayerTick));
        disableFontScaling = new JCheckBox();
        tickNumberPanel.add(GuiUtils.labeledCheckbox("Disable Font Scaling", disableFontScaling));
        fontSize = GuiUtils.spinner(15, 8, 50, 1);
        numberColorBtn = new ColorButtonPanel("Tick Number Color", config.NumberColor());
        fontType = new JComboBox<>(Arrays.stream(FontTypes.values())
                .map(FontTypes::name)
                .toArray(String[]::new));
        tickNumberPanel.add(GuiUtils.labeled("Font Size:", fontSize));
        tickNumberPanel.add(numberColorBtn);
        tickNumberPanel.add(GuiUtils.labeled("Font Type:", fontType));
        add(new CollapsibleSection("Tick Number Settings", tickNumberPanel));

        // --- True Tile Overlay Section ---
        JPanel tilePanel = new JPanel();
        tilePanel.setLayout(new BoxLayout(tilePanel, BoxLayout.Y_AXIS));
        currentTileFillColorBtn = new ColorButtonPanel("Tile Fill Color", config.currentTileFillColor());
        currentTileBorderWidth = GuiUtils.spinner(2, 0, 10, 0.5);
        changeFillColor = new JCheckBox();
        tilePanel.add(GuiUtils.labeledCheckbox("Enable Tile Fill Metronome", changeFillColor));
        changeFillColorOpacity = GuiUtils.spinner(50, 0, 255, 1);
        tilePanel.add(currentTileFillColorBtn);
        tilePanel.add(GuiUtils.labeled("Tile Border Width:", currentTileBorderWidth));
        tilePanel.add(GuiUtils.labeled("Fill Color Opacity:", changeFillColorOpacity));
        add(new CollapsibleSection("True Tile Overlay Settings", tilePanel));

        // --- Color Settings Section ---
        JPanel colorPanel = new JPanel();
        colorPanel.setLayout(new BoxLayout(colorPanel, BoxLayout.Y_AXIS));
        colorCycleSpinner = GuiUtils.spinner(2, 2, 10, 1);
        colorPanel.add(GuiUtils.labeled("Number of Colors:", colorCycleSpinner));

        tickColorBtns[0] = new ColorButtonPanel("Tick Color", config.getTickColor());
        colorPanel.add(tickColorBtns[0]);

        tickColorBtns[1] = new ColorButtonPanel("Tock Color", config.getTockColor());
        colorPanel.add(tickColorBtns[1]);

        tickColorBtns[2] = new ColorButtonPanel("3 Tick Color", config.getTick3Color());
        colorPanel.add(tickColorBtns[2]);

        tickColorBtns[3] = new ColorButtonPanel("4 Tick Color", config.getTick4Color());
        colorPanel.add(tickColorBtns[3]);

        tickColorBtns[4] = new ColorButtonPanel("5 Tick Color", config.getTick5Color());
        colorPanel.add(tickColorBtns[4]);

        tickColorBtns[5] = new ColorButtonPanel("6 Tick Color", config.getTick6Color());
        colorPanel.add(tickColorBtns[5]);

        tickColorBtns[6] = new ColorButtonPanel("7 Tick Color", config.getTick7Color());
        colorPanel.add(tickColorBtns[6]);

        tickColorBtns[7] = new ColorButtonPanel("8 Tick Color", config.getTick8Color());
        colorPanel.add(tickColorBtns[7]);

        tickColorBtns[8] = new ColorButtonPanel("9 Tick Color", config.getTick9Color());
        colorPanel.add(tickColorBtns[8]);

        tickColorBtns[9] = new ColorButtonPanel("10 Tick Color", config.getTick10Color());
        colorPanel.add(tickColorBtns[9]);

        add(new CollapsibleSection("Color Settings", colorPanel));


        // --- Hotkeys Section ---
        JPanel hotkeyPanel = new JPanel();
        hotkeyPanel.setLayout(new BoxLayout(hotkeyPanel, BoxLayout.Y_AXIS));
        tickResetHotkeyBtn = new JButton("Set Reset Hotkey");
        tickResetHotkeyBtn.setMargin(new Insets(3, 8, 3, 8));
        tickResetHotkeyBtn.addActionListener(e -> {
            Keybind newKeybind = promptForKeybind();
            if (newKeybind != null) {
                tickResetHotkey = newKeybind;
                tickResetHotkeyBtn.setText("Hotkey: " + newKeybind.toString());
                configHandler.updateConfigThrottled();
            }
        });
        resetHotkeyBtn = new JButton("Reset Hotkey");
        resetHotkeyBtn.setMargin(new Insets(3, 8, 3, 8));

        resetHotkeyBtn.addActionListener(e -> {
            tickResetHotkey = Keybind.NOT_SET;
            tickResetHotkeyBtn.setText("Set Reset Hotkey");
            configHandler.updateConfigThrottled();
        });
        tickResetStartTick = GuiUtils.spinner(0, 0, 10, 1);

        JPanel hotkeyButtonsPanel = new JPanel();
        hotkeyButtonsPanel.setLayout(new BoxLayout(hotkeyButtonsPanel, BoxLayout.X_AXIS));
        hotkeyButtonsPanel.add(tickResetHotkeyBtn);
        hotkeyButtonsPanel.add(Box.createHorizontalStrut(5));
        hotkeyButtonsPanel.add(resetHotkeyBtn);
        hotkeyButtonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        hotkeyPanel.add(hotkeyButtonsPanel);
        hotkeyPanel.add(GuiUtils.labeled("Reset to Tick:", tickResetStartTick));
        add(new CollapsibleSection("Hotkey Settings", hotkeyPanel));

        // --- Mouse Following Section ---
        JPanel mousePanel = new JPanel();
        mousePanel.setLayout(new BoxLayout(mousePanel, BoxLayout.Y_AXIS));
        mouseFollowingTick = new JCheckBox();
        mousePanel.add(GuiUtils.labeledCheckbox("Tick Counter Follows Mouse", mouseFollowingTick));
        mouseOffsetX = GuiUtils.spinner(10, -100, 100, 1);
        mouseOffsetY = GuiUtils.spinner(-10, -100, 100, 1);
        mousePanel.add(GuiUtils.labeled("Mouse Offset X:", mouseOffsetX));
        mousePanel.add(GuiUtils.labeled("Mouse Offset Y:", mouseOffsetY));
        add(new CollapsibleSection("Mouse Following Settings", mousePanel));

        // --- Additional Overhead Cycles Section ---
        JPanel overheadPanel = new JPanel();
        overheadPanel.setLayout(new BoxLayout(overheadPanel, BoxLayout.Y_AXIS));

        enableCycle2 = new JCheckBox();
        overheadPanel.add(GuiUtils.labeledCheckbox("Enable Second Cycle", enableCycle2));
        tickCount2 = GuiUtils.spinner(2, 2, 20, 1);
        cycle2ColorBtn = new ColorButtonPanel("Second Cycle Color", config.cycle2Color());
        overheadPanel.add(GuiUtils.labeled("Second Cycle Length:", tickCount2));
        overheadPanel.add(cycle2ColorBtn);

        enableCycle3 = new JCheckBox();
        overheadPanel.add(GuiUtils.labeledCheckbox("Enable Third Cycle", enableCycle3));
        tickCount3 = GuiUtils.spinner(2, 2, 20, 1);
        cycle3ColorBtn = new ColorButtonPanel("Third Cycle Color", config.cycle3Color());
        overheadPanel.add(GuiUtils.labeled("Third Cycle Length:", tickCount3));
        overheadPanel.add(cycle3ColorBtn);

        overheadCyclesGapDistance = GuiUtils.spinner(20, 0, 100, 1);
        overheadHeight = GuiUtils.spinner(20, -500, 500, 1);
        overheadXCenterOffset = GuiUtils.spinner(0, -50, 50, 1);
        overheadUseCurrentColor = new JCheckBox();
        overheadPanel.add(GuiUtils.labeledCheckbox("Metronome Color for Overhead", overheadUseCurrentColor));
        overheadPanel.add(GuiUtils.labeled("Gap Distance:", overheadCyclesGapDistance));
        overheadPanel.add(GuiUtils.labeled("Overhead Height:", overheadHeight));
        overheadPanel.add(GuiUtils.labeled("X Center Offset:", overheadXCenterOffset));
        add(new CollapsibleSection("Additional Overhead Cycles", overheadPanel));

        //Initialize event listeners to update on value changes
        new VisualMetronomePanelListener(this, configHandler);
    }

    public void updateMembers(List<String> members, VisualMetronomeConfig config, ConfigManager configManager)
    {
        if (members == null || members.isEmpty())
        {
            return;
        }

        SwingUtilities.invokeLater(() -> {

            ActionListener[] listeners = memberDropdown.getActionListeners();
            for (ActionListener l : listeners) {
                memberDropdown.removeActionListener(l);
            }

            memberDropdown.removeAllItems();

            Set<String> uniqueMembers = new LinkedHashSet<>(members);
            uniqueMembers.remove("<unknown>");

            if (lastSelectedMember != null) {
                uniqueMembers.remove(lastSelectedMember);
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

            if (!Objects.equals(lastSelectedMember, config.syncTarget()))
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

    private Keybind promptForKeybind()
    {
        final KeyCaptureDialog dialog = new KeyCaptureDialog();
        return dialog.showAndGetKeybind();
    }
}
