package com.visualmetronome;

import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import java.awt.*;
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
    private final JButton numberColorBtn;
    private final JComboBox<String> fontType;

    // True Tile Overlay
    private final JButton currentTileFillColorBtn;
    private final JSpinner currentTileBorderWidth;
    private final JCheckBox changeFillColor;
    private final JSpinner changeFillColorOpacity;

    // Party Sync
    private final JCheckBox enablePartySync;
    private final JComboBox<String> memberDropdown;

    // Colors
    private final JSpinner colorCycleSpinner;
    private final JButton[] tickColorBtns = new JButton[10];

    // Hotkeys
    private final JButton tickResetHotkeyBtn; // Placeholder (actual keybind integration is separate)
    private final JSpinner tickResetStartTick;

    // Mouse Following
    private final JCheckBox mouseFollowingTick;
    private final JSpinner mouseOffsetX;
    private final JSpinner mouseOffsetY;

    // Additional Overhead Cycles
    private final JCheckBox enableCycle2;
    private final JSpinner tickCount2;
    private final JButton cycle2ColorBtn;
    private final JCheckBox enableCycle3;
    private final JSpinner tickCount3;
    private final JButton cycle3ColorBtn;
    private final JSpinner overheadCyclesGapDistance;
    private final JSpinner overheadHeight;
    private final JSpinner overheadXCenterOffset;
    private final JCheckBox overheadUseCurrentColor;

    public PartySyncPanel()
    {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // --- General Metronome Section ---
        JPanel generalPanel = new JPanel();
        generalPanel.setLayout(new BoxLayout(generalPanel, BoxLayout.Y_AXIS));
        enableMetronome = new JCheckBox("Enable Visual Metronome");
        highlightCurrentTile = new JCheckBox("Enable True Tile Overlay");
        boxWidth = spinner(25, 16, 200, 1);
        tickCount = spinner(1, 1, 10, 1);
        generalPanel.add(enableMetronome);
        generalPanel.add(highlightCurrentTile);
        generalPanel.add(labeled("Box Width:", boxWidth));
        generalPanel.add(labeled("Tick Count:", tickCount));
        add(new CollapsibleSection("General Metronome", generalPanel));

        // --- Tick Number Section ---
        JPanel tickNumberPanel = new JPanel();
        tickNumberPanel.setLayout(new BoxLayout(tickNumberPanel, BoxLayout.Y_AXIS));
        showTick = new JCheckBox("Show Metronome Tick Number");
        showPlayerTick = new JCheckBox("Show Tick Above Player");
        disableFontScaling = new JCheckBox("Disable Font Scaling");
        fontSize = spinner(15, 8, 50, 1);
        numberColorBtn = colorButton("Tick Number Color", Color.CYAN);
        fontType = new JComboBox<>(new String[]{"REGULAR", "BOLD", "ITALIC"});
        tickNumberPanel.add(showTick);
        tickNumberPanel.add(showPlayerTick);
        tickNumberPanel.add(disableFontScaling);
        tickNumberPanel.add(labeled("Font Size:", fontSize));
        tickNumberPanel.add(numberColorBtn);
        tickNumberPanel.add(labeled("Font Type:", fontType));
        add(new CollapsibleSection("Tick Number Settings", tickNumberPanel));

        // --- True Tile Overlay Section ---
        JPanel tilePanel = new JPanel();
        tilePanel.setLayout(new BoxLayout(tilePanel, BoxLayout.Y_AXIS));
        currentTileFillColorBtn = colorButton("Tile Fill Color", new Color(0, 0, 0, 50));
        currentTileBorderWidth = spinner(2, 0, 10, 0.5);
        changeFillColor = new JCheckBox("Enable Tile Fill Metronome");
        changeFillColorOpacity = spinner(50, 0, 255, 1);
        tilePanel.add(currentTileFillColorBtn);
        tilePanel.add(labeled("Tile Border Width:", currentTileBorderWidth));
        tilePanel.add(changeFillColor);
        tilePanel.add(labeled("Fill Color Opacity:", changeFillColorOpacity));
        add(new CollapsibleSection("True Tile Overlay Settings", tilePanel));

        // --- Party Sync Section ---
        JPanel partySyncPanel = new JPanel();
        partySyncPanel.setLayout(new BoxLayout(partySyncPanel, BoxLayout.Y_AXIS));
        enablePartySync = new JCheckBox("Enable Tick Sync");
        memberDropdown = new JComboBox<>();
        partySyncPanel.add(enablePartySync);
        partySyncPanel.add(labeled("Sync Target:", memberDropdown));
        add(new CollapsibleSection("Party Sync Settings", partySyncPanel));

        // --- Color Settings Section ---
        JPanel colorPanel = new JPanel();
        colorPanel.setLayout(new BoxLayout(colorPanel, BoxLayout.Y_AXIS));
        colorCycleSpinner = spinner(2, 2, 10, 1);
        colorPanel.add(labeled("Number of Colors:", colorCycleSpinner));
        for (int i = 0; i < 10; i++)
        {
            tickColorBtns[i] = colorButton((i + 1) + " Tick Color", Color.LIGHT_GRAY);
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
        mouseFollowingTick = new JCheckBox("Tick Counter Follows Mouse");
        mouseOffsetX = spinner(10, -100, 100, 1);
        mouseOffsetY = spinner(-10, -100, 100, 1);
        mousePanel.add(mouseFollowingTick);
        mousePanel.add(labeled("Mouse Offset X:", mouseOffsetX));
        mousePanel.add(labeled("Mouse Offset Y:", mouseOffsetY));
        add(new CollapsibleSection("Mouse Following Settings", mousePanel));

        // --- Additional Overhead Cycles Section ---
        JPanel overheadPanel = new JPanel();
        overheadPanel.setLayout(new BoxLayout(overheadPanel, BoxLayout.Y_AXIS));
        enableCycle2 = new JCheckBox("Enable Second Cycle");
        tickCount2 = spinner(2, 2, 20, 1);
        cycle2ColorBtn = colorButton("Second Cycle Color", Color.CYAN);
        enableCycle3 = new JCheckBox("Enable Third Cycle");
        tickCount3 = spinner(2, 2, 20, 1);
        cycle3ColorBtn = colorButton("Third Cycle Color", Color.CYAN);
        overheadCyclesGapDistance = spinner(20, 0, 100, 1);
        overheadHeight = spinner(20, -500, 500, 1);
        overheadXCenterOffset = spinner(0, -50, 50, 1);
        overheadUseCurrentColor = new JCheckBox("Use Metronome Color for Overhead");
        overheadPanel.add(enableCycle2);
        overheadPanel.add(labeled("Second Cycle Length:", tickCount2));
        overheadPanel.add(cycle2ColorBtn);
        overheadPanel.add(enableCycle3);
        overheadPanel.add(labeled("Third Cycle Length:", tickCount3));
        overheadPanel.add(cycle3ColorBtn);
        overheadPanel.add(labeled("Gap Distance:", overheadCyclesGapDistance));
        overheadPanel.add(labeled("Overhead Height:", overheadHeight));
        overheadPanel.add(labeled("X Center Offset:", overheadXCenterOffset));
        overheadPanel.add(overheadUseCurrentColor);
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
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        comp.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(comp);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(BorderFactory.createEmptyBorder(2, 0, 4, 0));
        return panel;
    }


    private JSpinner spinner(Number value, Number min, Number max, Number step)
    {
        return new JSpinner(new SpinnerNumberModel(value.doubleValue(), min.doubleValue(), max.doubleValue(), step.doubleValue()));
    }

    private JButton colorButton(String label, Color initial)
    {
        JButton btn = new JButton(label);
        btn.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(this, "Choose " + label, initial);
            if (chosen != null)
            {
                btn.setBackground(chosen);
            }
        });
        return btn;
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
                    BorderFactory.createLineBorder(Color.GRAY),       // outer line border
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



}
