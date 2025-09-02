package com.visualmetronome;

import javax.swing.*;
import java.awt.*;

public class ColorButtonPanel extends JPanel {
    private final JButton btn;

    public ColorButtonPanel(String labelText, Color initial) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        JLabel label = new JLabel(labelText);
        label.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        label.setOpaque(true);
        label.setAlignmentY(Component.CENTER_ALIGNMENT);

        Dimension minSize = new Dimension(80, 20);
        Dimension prefSize = new Dimension(100, 20);
        Dimension maxSize = new Dimension(120, 20);
        label.setMinimumSize(minSize);
        label.setPreferredSize(prefSize);
        label.setMaximumSize(maxSize);

        btn = new JButton("             ");
        btn.setBackground(initial);
        btn.setPreferredSize(new Dimension(40, 20));
        btn.setAlignmentY(Component.CENTER_ALIGNMENT);
        btn.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(this, "Choose " + labelText, initial);
            if (chosen != null) {
                setColor(chosen);
            }
        });

        add(label);
        add(Box.createRigidArea(new Dimension(5, 0)));
        add(btn);
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setBorder(BorderFactory.createEmptyBorder(2, 0, 4, 0));
    }

    public void setColor(Color c) {
        btn.setBackground(c);
        btn.repaint();
    }

    public Color getColor() {
        return btn.getBackground();
    }
}