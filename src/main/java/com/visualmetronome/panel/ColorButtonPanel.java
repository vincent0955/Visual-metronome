package com.visualmetronome.panel;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BorderFactory;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;


public class ColorButtonPanel extends JPanel {
    private final JButton btn;
    private final List<ColorChangeListener> listeners = new ArrayList<>();

    public ColorButtonPanel(String labelText, Color initial) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        JLabel label = new JLabel(labelText);
        label.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        label.setOpaque(true);
        label.setAlignmentY(Component.CENTER_ALIGNMENT);

        Dimension minSize = new Dimension(80, 20);
        Dimension prefSize = new Dimension(100, 20);
        Dimension maxSize = new Dimension(120, 20);
        label.setMinimumSize(minSize);
        label.setPreferredSize(prefSize);
        label.setMaximumSize(maxSize);

        btn = new JButton("                ");
        btn.setBackground(initial);
        btn.setPreferredSize(new Dimension(50, 20));
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
        notifyListeners(c);
    }

    public Color getColor() {
        return btn.getBackground();
    }

    public void addColorChangeListener(ColorChangeListener listener) {
        listeners.add(listener);
    }

    public void removeColorChangeListener(ColorChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(Color newColor) {
        for (ColorChangeListener listener : listeners) {
            listener.colorChanged(newColor);
        }
    }

    public interface ColorChangeListener {
        void colorChanged(Color newColor);
    }
}