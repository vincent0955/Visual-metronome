package com.visualmetronome.panel;

import net.runelite.client.party.PartyMember;

import com.visualmetronome.FontTypes;
import com.visualmetronome.VisualMetronomeConfig;
import com.visualmetronome.messages.ColorRequestMessage;
import com.visualmetronome.messages.ColorSyncMessage;
import com.visualmetronome.messages.TickSyncMessage;
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
import java.util.stream.Collectors;
import java.util.List;
import java.awt.event.ActionListener;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Objects;

public class VisualMetronomePanel extends PluginPanel
{
    // General Metronome
    public JCheckBox enableMetronome;
    public JCheckBox highlightCurrentTile;
    public JSpinner boxWidth;
    public JSpinner tickCount;

    // Tick Number
    public JCheckBox showTick;
    public JCheckBox showPlayerTick;
    public JCheckBox disableFontScaling;
    public JSpinner fontSize;
    public ColorButtonPanel numberColorBtn;
    public JComboBox<String> fontType;

    // True Tile Overlay
    public ColorButtonPanel currentTileFillColorBtn;
    public JSpinner currentTileBorderWidth;
    public JCheckBox changeFillColor;
    public JSpinner changeFillColorOpacity;

    // Party Sync
    public JCheckBox enablePartySync;
    public JComboBox<String> memberDropdown;
    public String lastSelectedMember;

    // Colors
    public JSpinner colorCycleSpinner;
    public ColorButtonPanel[] tickColorBtns = new ColorButtonPanel[10];

    // Hotkeys
    public JButton tickResetHotkeyBtn;
    public JButton resetHotkeyBtn;
    public Keybind tickResetHotkey;
    public JSpinner tickResetStartTick;

    // Mouse Following
    public JCheckBox mouseFollowingTick;
    public JSpinner mouseOffsetX;
    public JSpinner mouseOffsetY;

    // Additional Overhead Cycles
    public JCheckBox enableCycle2;
    public JSpinner tickCount2;
    public ColorButtonPanel cycle2ColorBtn;
    public JCheckBox enableCycle3;
    public JSpinner tickCount3;
    public ColorButtonPanel cycle3ColorBtn;
    public JSpinner overheadCyclesGapDistance;
    public JSpinner overheadHeight;
    public JSpinner overheadXCenterOffset;
    public JCheckBox overheadUseCurrentColor;

    public final ConfigManager configManager;
    public final VisualMetronomeConfig config;
    public final PartyService partyService;

    public final VisualMetronomeConfigHandler configHandler;

    public VisualMetronomePanel(ConfigManager configManager,VisualMetronomeConfig config, PartyService partyService)
    {
        this.configManager = configManager;
        this.partyService = partyService;
        this.config = config;
        this.configHandler = new VisualMetronomeConfigHandler(configManager, config, this);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        add(createGeneralMetronomeSection());
        add(createTickNumberSection());
        add(createTrueTileOverlaySection());
        add(createPartySyncSection());
        add(createColorSettingsSection());
        add(createHotkeysSection());
        add(createMouseFollowingSection());
        add(createAdditionalOverheadCyclesSection());

        //Initialize event listeners to update on value changes
        new VisualMetronomePanelListener(this, configHandler);
    }

    // --- Party Sync Section ---
    private CollapsibleSection createPartySyncSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        enablePartySync = new JCheckBox();
        panel.add(GuiUtils.labeledCheckbox("Enable Tick Sync", enablePartySync));

        memberDropdown = new JComboBox<>();
        panel.add(GuiUtils.labeled("Party Member:", memberDropdown));

        JPanel buttonRow = new JPanel();
        buttonRow.setLayout(new BoxLayout(buttonRow, BoxLayout.X_AXIS));
        buttonRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton requestColorsButton = new JButton("Sync Colors");
        requestColorsButton.setAlignmentY(Component.CENTER_ALIGNMENT);
        requestColorsButton.addActionListener(e -> {
            PartyMember localPlayer  = partyService.getLocalMember();
            if(localPlayer != null)
            {
                ColorRequestMessage message = new ColorRequestMessage(config.syncTarget(), localPlayer.getDisplayName());
                partyService.send(message);
            }
        });

        buttonRow.add(Box.createHorizontalStrut(0));
        buttonRow.add(requestColorsButton);

        panel.add(buttonRow);
        return new CollapsibleSection("Party Sync Settings", panel);
    }

    // --- General Metronome Section ---
    private CollapsibleSection createGeneralMetronomeSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        enableMetronome = new JCheckBox();
        panel.add(GuiUtils.labeledCheckbox("Enable Visual Metronome", enableMetronome));

        highlightCurrentTile = new JCheckBox();
        panel.add(GuiUtils.labeledCheckbox("Enable True Tile Overlay", highlightCurrentTile));

        boxWidth = GuiUtils.spinner(25, 16, 200, 1);
        tickCount = GuiUtils.spinner(1, 1, 10, 1);

        panel.add(GuiUtils.labeled("Box Width:", boxWidth));
        panel.add(GuiUtils.labeled("Tick Count:", tickCount));

        return new CollapsibleSection("General Metronome", panel);
    }

    // --- Tick Number Section ---
    private CollapsibleSection createTickNumberSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        showTick = new JCheckBox();
        panel.add(GuiUtils.labeledCheckbox("Show Metronome Tick Number", showTick));

        showPlayerTick = new JCheckBox();
        panel.add(GuiUtils.labeledCheckbox("Show Tick Above Player", showPlayerTick));

        disableFontScaling = new JCheckBox();
        panel.add(GuiUtils.labeledCheckbox("Disable Font Scaling", disableFontScaling));

        fontSize = GuiUtils.spinner(15, 8, 50, 1);
        numberColorBtn = new ColorButtonPanel("Tick Number Color", config.NumberColor());

        fontType = new JComboBox<>(Arrays.stream(FontTypes.values())
                .map(FontTypes::name)
                .toArray(String[]::new));

        panel.add(GuiUtils.labeled("Font Size:", fontSize));
        panel.add(numberColorBtn);
        panel.add(GuiUtils.labeled("Font Type:", fontType));

        return new CollapsibleSection("Tick Number Settings", panel);
    }

    // --- True Tile Overlay Section ---
    private CollapsibleSection createTrueTileOverlaySection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        changeFillColor = new JCheckBox();
        panel.add(GuiUtils.labeledCheckbox("Enable Tile Fill Metronome", changeFillColor));

        currentTileFillColorBtn = new ColorButtonPanel("Tile Fill Color", config.currentTileFillColor());
        currentTileBorderWidth = GuiUtils.spinner(2, 0, 10, 0.5);
        changeFillColorOpacity = GuiUtils.spinner(50, 0, 255, 1);

        panel.add(currentTileFillColorBtn);
        panel.add(GuiUtils.labeled("Tile Border Width:", currentTileBorderWidth));
        panel.add(GuiUtils.labeled("Fill Color Opacity:", changeFillColorOpacity));

        return new CollapsibleSection("True Tile Overlay Settings", panel);
    }

    // --- Color Settings Section ---
    private CollapsibleSection createColorSettingsSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        colorCycleSpinner = GuiUtils.spinner(2, 2, 10, 1);
        panel.add(GuiUtils.labeled("Number of Colors:", colorCycleSpinner));

        tickColorBtns[0] = new ColorButtonPanel("Tick Color", config.getTickColor());
        tickColorBtns[1] = new ColorButtonPanel("Tock Color", config.getTockColor());
        tickColorBtns[2] = new ColorButtonPanel("3 Tick Color", config.getTick3Color());
        tickColorBtns[3] = new ColorButtonPanel("4 Tick Color", config.getTick4Color());
        tickColorBtns[4] = new ColorButtonPanel("5 Tick Color", config.getTick5Color());
        tickColorBtns[5] = new ColorButtonPanel("6 Tick Color", config.getTick6Color());
        tickColorBtns[6] = new ColorButtonPanel("7 Tick Color", config.getTick7Color());
        tickColorBtns[7] = new ColorButtonPanel("8 Tick Color", config.getTick8Color());
        tickColorBtns[8] = new ColorButtonPanel("9 Tick Color", config.getTick9Color());
        tickColorBtns[9] = new ColorButtonPanel("10 Tick Color", config.getTick10Color());

        for (ColorButtonPanel btn : tickColorBtns) {
            panel.add(btn);
        }

        return new CollapsibleSection("Color Settings", panel, true);
    }

    // --- Hotkeys Section ---
    private CollapsibleSection createHotkeysSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

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

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        buttonsPanel.add(tickResetHotkeyBtn);
        buttonsPanel.add(Box.createHorizontalStrut(5));
        buttonsPanel.add(resetHotkeyBtn);
        buttonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(buttonsPanel);
        panel.add(GuiUtils.labeled("Reset to Tick:", tickResetStartTick));

        return new CollapsibleSection("Hotkey Settings", panel, true);
    }

    // --- Mouse Following Section ---
    private CollapsibleSection createMouseFollowingSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        mouseFollowingTick = new JCheckBox();
        panel.add(GuiUtils.labeledCheckbox("Tick Counter Follows Mouse", mouseFollowingTick));

        mouseOffsetX = GuiUtils.spinner(10, -100, 100, 1);
        mouseOffsetY = GuiUtils.spinner(-10, -100, 100, 1);
        panel.add(GuiUtils.labeled("Mouse Offset X:", mouseOffsetX));
        panel.add(GuiUtils.labeled("Mouse Offset Y:", mouseOffsetY));

        return new CollapsibleSection("Mouse Following Settings", panel, true);
    }

    // --- Additional Overhead Cycles Section ---
    private CollapsibleSection createAdditionalOverheadCyclesSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        enableCycle2 = new JCheckBox();
        panel.add(GuiUtils.labeledCheckbox("Enable Second Cycle", enableCycle2));
        tickCount2 = GuiUtils.spinner(2, 2, 20, 1);
        cycle2ColorBtn = new ColorButtonPanel("Second Cycle Color", config.cycle2Color());
        panel.add(GuiUtils.labeled("Second Cycle Length:", tickCount2));
        panel.add(cycle2ColorBtn);

        enableCycle3 = new JCheckBox();
        panel.add(GuiUtils.labeledCheckbox("Enable Third Cycle", enableCycle3));
        tickCount3 = GuiUtils.spinner(2, 2, 20, 1);
        cycle3ColorBtn = new ColorButtonPanel("Third Cycle Color", config.cycle3Color());
        panel.add(GuiUtils.labeled("Third Cycle Length:", tickCount3));
        panel.add(cycle3ColorBtn);

        overheadCyclesGapDistance = GuiUtils.spinner(20, 0, 100, 1);
        overheadHeight = GuiUtils.spinner(20, -500, 500, 1);
        overheadXCenterOffset = GuiUtils.spinner(0, -50, 50, 1);
        overheadUseCurrentColor = new JCheckBox();
        panel.add(GuiUtils.labeledCheckbox("Metronome Color for Overhead", overheadUseCurrentColor));
        panel.add(GuiUtils.labeled("Gap Distance:", overheadCyclesGapDistance));
        panel.add(GuiUtils.labeled("Overhead Height:", overheadHeight));
        panel.add(GuiUtils.labeled("X Center Offset:", overheadXCenterOffset));

        return new CollapsibleSection("Additional Overhead Cycles", panel, true);
    }


    public void updateMembers(List<String> members, VisualMetronomeConfig config, ConfigManager configManager)
    {
        if (partyService != null && (members == null || members.isEmpty())) {
            List<PartyMember> membersList = partyService.getMembers();
            members = membersList.stream()
                    .map(PartyMember::getDisplayName)
                    .filter(name -> !"<unknown>".equals(name))
                    .collect(Collectors.toList());
        }

        if (members == null || members.isEmpty()) {
            memberDropdown.removeAll();
            memberDropdown.addItem(lastSelectedMember);
        }

        // update lastSelectedMember based on dropdown
        String selected = (String) memberDropdown.getSelectedItem();
        if (selected != null && !selected.equals(lastSelectedMember)) {
            lastSelectedMember = selected;
       }

        final List<String> finalMembers = members;

        SwingUtilities.invokeLater(() -> {
            ActionListener[] listeners = memberDropdown.getActionListeners();
            for (ActionListener l : listeners) {
                memberDropdown.removeActionListener(l);
            }

            memberDropdown.removeAllItems();

            Set<String> uniqueMembers = new LinkedHashSet<>(finalMembers);
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

            if (!Objects.equals(lastSelectedMember, config.syncTarget())) {
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
    public int getBoxWidth() { return ((Number) boxWidth.getValue()).intValue(); }
    public int getTickCount() { return ((Number) tickCount.getValue()).intValue(); }

    // --- Tick Number ---
    public boolean isShowTick() { return showTick.isSelected(); }
    public boolean isShowPlayerTick() { return showPlayerTick.isSelected(); }
    public boolean isDisableFontScaling() { return disableFontScaling.isSelected(); }
    public int getFontSize() { return ((Number) fontSize.getValue()).intValue(); }
    public Color getNumberColor() { return numberColorBtn.getColor(); }
    public String getFontType() { return (String) fontType.getSelectedItem(); }

    // --- True Tile Overlay ---
    public Color getCurrentTileFillColor() { return currentTileFillColorBtn.getColor(); }
    public double getCurrentTileBorderWidth() { return ((Number) currentTileBorderWidth.getValue()).doubleValue(); }
    public boolean isChangeFillColor() { return changeFillColor.isSelected(); }
    public int getChangeFillColorOpacity() { return ((Number) changeFillColorOpacity.getValue()).intValue(); }

    // --- Party Sync ---
    public boolean isEnablePartySync() { return enablePartySync.isSelected(); }
    public String getSelectedMember() { return (String) memberDropdown.getSelectedItem(); }

    // --- Color Settings ---
    public int getColorCycle() { return ((Number) colorCycleSpinner.getValue()).intValue(); }
    public Color getTickColor(int i)
    {
        if (i < 1 || i > 10) throw new IllegalArgumentException("Tick index must be 1-10");
        return tickColorBtns[i-1].getColor();
    }

    // --- Hotkeys ---
    public int getTickResetStartTick() { return ((Number) tickResetStartTick.getValue()).intValue(); }
    public Keybind getTickResetHotkey() {return tickResetHotkey;}

    // --- Mouse Following ---
    public boolean isMouseFollowingTick() { return mouseFollowingTick.isSelected(); }
    public int getMouseOffsetX() { return ((Number) mouseOffsetX.getValue()).intValue(); }
    public int getMouseOffsetY() { return ((Number) mouseOffsetY.getValue()).intValue(); }

    // --- Additional Overhead Cycles ---
    public boolean isEnableCycle2() { return enableCycle2.isSelected(); }
    public int getTickCount2() { return ((Number) tickCount2.getValue()).intValue(); }
    public Color getCycle2Color() { return cycle2ColorBtn.getColor(); }

    public boolean isEnableCycle3() { return enableCycle3.isSelected(); }
    public int getTickCount3() { return ((Number) tickCount3.getValue()).intValue(); }
    public Color getCycle3Color() { return cycle3ColorBtn.getColor(); }

    public int getOverheadCyclesGapDistance() { return ((Number) overheadCyclesGapDistance.getValue()).intValue(); }
    public int getOverheadHeight() { return ((Number) overheadHeight.getValue()).intValue(); }
    public int getOverheadXCenterOffset() { return ((Number) overheadXCenterOffset.getValue()).intValue(); }
    public boolean isOverheadUseCurrentColor() { return overheadUseCurrentColor.isSelected(); }

    private Keybind promptForKeybind()
    {
        final KeyCaptureDialog dialog = new KeyCaptureDialog();
        return dialog.showAndGetKeybind();
    }

    public TickSyncMessage toTickSyncMessage(int tickCounter, int tickCounter2, int tickCounter3, int currentColorIndex, String sender) {
        return new TickSyncMessage(
                tickCounter,
                tickCounter2,
                tickCounter3,
                currentColorIndex,
                getColorCycle(),
                getTickCount(),
                getTickCount2(),
                getTickCount3(),
                sender
        );
    }

    public void applyTickSyncMessage(TickSyncMessage msg) {
        tickCount.setValue((double) msg.getTickCount());
        tickCount2.setValue((double) msg.getTickCount2());
        tickCount3.setValue((double) msg.getTickCount3());
        colorCycleSpinner.setValue((double) msg.getConfigColorIndex());
    }

    public ColorSyncMessage toColorSyncMessage(String reqSender) {
        return new ColorSyncMessage(
                getColorCycle(),
                getTickColor(1),
                getTickColor(2),
                getTickColor(3),
                getTickColor(4),
                getTickColor(5),
                getTickColor(6),
                getTickColor(7),
                getTickColor(8),
                getTickColor(9),
                getTickColor(10),
                getNumberColor(),
                isOverheadUseCurrentColor(),
                getCycle2Color(),
                getCycle3Color(),
                getCurrentTileFillColor(),
                isChangeFillColor(),
                getChangeFillColorOpacity(),
                reqSender
        );
    }

    public void applyColorSyncMessage(ColorSyncMessage msg) {
        colorCycleSpinner.setValue(msg.getColorCycle());
        tickColorBtns[0].setColor(msg.getTickColor());
        tickColorBtns[1].setColor(msg.getTockColor());
        tickColorBtns[2].setColor(msg.getTick3Color());
        tickColorBtns[3].setColor(msg.getTick4Color());
        tickColorBtns[4].setColor(msg.getTick5Color());
        tickColorBtns[5].setColor(msg.getTick6Color());
        tickColorBtns[6].setColor(msg.getTick7Color());
        tickColorBtns[7].setColor(msg.getTick8Color());
        tickColorBtns[8].setColor(msg.getTick9Color());
        tickColorBtns[9].setColor(msg.getTick10Color());

        numberColorBtn.setColor(msg.getNumberColor());
        cycle2ColorBtn.setColor(msg.getCycle2Color());
        cycle3ColorBtn.setColor(msg.getCycle3Color());
        currentTileFillColorBtn.setColor(msg.getCurrentTileFillColor());
        changeFillColorOpacity.setValue(msg.getChangeFillColorOpacity());
    }

}
