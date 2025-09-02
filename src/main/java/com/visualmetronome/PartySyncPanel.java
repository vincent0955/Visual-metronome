package com.visualmetronome;

import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class PartySyncPanel extends PluginPanel
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

    // Colors
    private final JSpinner colorCycleSpinner;
    private final ColorButtonPanel[] tickColorBtns = new ColorButtonPanel[10];

    // Hotkeys
    private final JButton tickResetHotkeyBtn;
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

    private boolean updatingFromConfig = false;

    public PartySyncPanel()
    {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // --- Party Sync Section ---
        JPanel partySyncPanel = new JPanel();
        partySyncPanel.setLayout(new BoxLayout(partySyncPanel, BoxLayout.Y_AXIS));
        enablePartySync = new JCheckBox();
        partySyncPanel.add(labeledCheckbox("Enable Tick Sync", enablePartySync));
        memberDropdown = new JComboBox<>();
        partySyncPanel.add(labeled("Sync Target:", memberDropdown));
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
        tickResetHotkeyBtn = new JButton("Set Reset Hotkey"); // placeholder
        tickResetStartTick = spinner(0, 0, 10, 1);
        hotkeyPanel.add(tickResetHotkeyBtn);
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
        overheadPanel.add(labeledCheckbox("Use Metronome Color for Overhead", overheadUseCurrentColor));
        overheadPanel.add(labeled("Gap Distance:", overheadCyclesGapDistance));
        overheadPanel.add(labeled("Overhead Height:", overheadHeight));
        overheadPanel.add(labeled("X Center Offset:", overheadXCenterOffset));
        add(new CollapsibleSection("Additional Overhead Cycles", overheadPanel));

    }

    // --- Utility builders ---
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
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5)); // optional spacing
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


    // --- Party Sync member list ---
    public void updateMembers(List<String> members)
    {
        SwingUtilities.invokeLater(() -> {
            memberDropdown.removeAllItems();
            for (String member : members)
            {
                memberDropdown.addItem(member);
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

            // Assign contentPanel first
            contentPanel = content;

            // Optional: add a border around content
            contentPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.BLACK),       // outer line border
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)      // inner padding
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
        memberDropdown.setSelectedItem(config.syncTarget());

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


}
