package com.visualmetronome.panel;

import com.visualmetronome.VisualMetronomeConfig;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.party.PartyMember;
import net.runelite.client.party.PartyService;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.BorderFactory;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;

import java.util.List;
import java.util.stream.Collectors;

public final class GuiUtils
{
    private GuiUtils() {}

    public static JButton getRefreshMembersBtn(ConfigManager configManager,
                                               VisualMetronomeConfig config,
                                               PartyService partyService,
                                               java.util.function.Consumer<List<String>> updateMembers)
    {
        JButton refreshMembersBtn = new JButton("Refresh Party");
        refreshMembersBtn.addActionListener(e -> {
            if (partyService != null) {
                List<PartyMember> membersList = partyService.getMembers();
                List<String> memberNames = membersList.stream()
                        .map(PartyMember::getDisplayName)
                        .filter(name -> !"<unknown>".equals(name))
                        .collect(Collectors.toList());
                SwingUtilities.invokeLater(() -> updateMembers.accept(memberNames));
            }
        });
        return refreshMembersBtn;
    }

    public static JLabel sectionLabel(String text)
    {
        JLabel label = new JLabel(text, SwingConstants.LEADING);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        label.setBorder(BorderFactory.createEmptyBorder(8, 0, 4, 0));
        return label;
    }

    public static JPanel labeled(String text, JComponent comp)
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

    public static JPanel labeledCheckbox(String text, JCheckBox checkBox)
    {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel label = new JLabel(text);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        panel.add(label, BorderLayout.WEST);

        checkBox.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(checkBox, BorderLayout.EAST);

        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(BorderFactory.createEmptyBorder(2, 0, 4, 0));
        return panel;
    }

    public static JSpinner spinner(Number value, Number min, Number max, Number step)
    {
        return new JSpinner(new SpinnerNumberModel(
                value.doubleValue(),
                min.doubleValue(),
                max.doubleValue(),
                step.doubleValue()
        ));
    }
}
