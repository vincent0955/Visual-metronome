package com.visualmetronome.panel;

import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.awt.Color;

public class CollapsibleSection extends JPanel
{
    private final JPanel contentPanel;
    private final JToggleButton toggle;

    public CollapsibleSection(String title, JPanel content, boolean collapsed)
    {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

        contentPanel = content;
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        contentPanel.setVisible(!collapsed); // hide content if collapsed

        toggle = new JToggleButton(title);
        toggle.setSelected(!collapsed); // toggle selected if expanded
        toggle.addActionListener(e -> {
            contentPanel.setVisible(toggle.isSelected());
            revalidate();
            repaint();
        });

        add(toggle, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    //constructor defaults to expanded
    public CollapsibleSection(String title, JPanel content) {
        this(title, content, false);
    }
}
