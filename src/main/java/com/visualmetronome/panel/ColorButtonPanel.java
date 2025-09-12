package com.visualmetronome.panel;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.colorchooser.AbstractColorChooserPanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Font;

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

        Dimension btnSize = new Dimension(100, 20);
        btn = new JButton(String.format("#%06X", initial.getRGB() & 0xFFFFFF));
        btn.setFont(btn.getFont().deriveFont(Font.BOLD, 14f));
        btn.setBackground(initial);
        btn.setPreferredSize(btnSize);
        btn.setMinimumSize(btnSize);
        btn.setMaximumSize(btnSize);
        btn.setAlignmentY(Component.CENTER_ALIGNMENT);
        btn.addActionListener(e -> {
            final JColorChooser chooser = new JColorChooser(getColor());
            chooser.setPreviewPanel(new JPanel());

            // keep only HSV panel
            for (AbstractColorChooserPanel panel : chooser.getChooserPanels()) {
                if (!"HSV".equals(panel.getDisplayName())) {
                    chooser.removeChooserPanel(panel);
                }
            }

            // build default swatches panel
            JPanel swatchPanel = new JPanel(new GridLayout(0, 8, 6, 6));
            swatchPanel.setBorder(BorderFactory.createTitledBorder("Default Colors"));

            Color[] defaultColors = new Color[] {
                    Color.BLACK, Color.DARK_GRAY, Color.GRAY, Color.LIGHT_GRAY, Color.WHITE,
                    Color.RED, Color.PINK, Color.ORANGE, Color.YELLOW, Color.GREEN,
                    Color.MAGENTA, Color.CYAN, Color.BLUE, new Color(128,0,128), new Color(165,42,42), new Color(0,128,128)
            };

            // previous/current color panel
            JPanel prevCurrentPanel = new JPanel();
            prevCurrentPanel.setLayout(new GridLayout(1, 2, 5, 5));
            prevCurrentPanel.setBorder(BorderFactory.createTitledBorder("  Prev       Current"));

            Color currentBtnColor = getColor();

            JButton prevColorBtn = new JButton();
            prevColorBtn.setBackground(currentBtnColor);
            Dimension colorPreviewSize = new Dimension(50,50);
            prevColorBtn.setPreferredSize(colorPreviewSize);
            prevColorBtn.setMinimumSize(colorPreviewSize);
            prevColorBtn.setMaximumSize(colorPreviewSize);
            prevColorBtn.setOpaque(true);
            prevColorBtn.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

            JButton currentColorBtn = new JButton();
            currentColorBtn.setBackground(currentBtnColor);
            currentColorBtn.setPreferredSize(colorPreviewSize);
            currentColorBtn.setMinimumSize(colorPreviewSize);
            currentColorBtn.setMaximumSize(colorPreviewSize);
            currentColorBtn.setOpaque(true);
            currentColorBtn.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

            prevCurrentPanel.add(prevColorBtn);
            prevCurrentPanel.add(currentColorBtn);

            // add default color swatches
            for (Color c : defaultColors) {
                JButton sw = new JButton();
                sw.setPreferredSize(new Dimension(24, 24));
                sw.setMaximumSize(new Dimension(24, 24));
                sw.setBackground(c);
                sw.setOpaque(true);
                sw.setFocusPainted(false);
                sw.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
                sw.addActionListener(evt -> {
                    chooser.setColor(c);
                });
                swatchPanel.add(sw);
            }

            Dimension swatchSize = new Dimension(300,75);
            swatchPanel.setMaximumSize(swatchSize);
            swatchPanel.setMinimumSize(swatchSize);
            swatchPanel.setPreferredSize(swatchSize);

            // Update current color preview in real-time
            chooser.getSelectionModel().addChangeListener(ee -> {
                Color selected = chooser.getColor();
                currentColorBtn.setBackground(selected);
            });

            // wrap default colors and prev/current side by side
            JPanel swatchesWrapper = new JPanel();
            swatchesWrapper.setLayout(new BorderLayout(10, 0));
            swatchesWrapper.add(prevCurrentPanel, BorderLayout.WEST);
            swatchesWrapper.add(swatchPanel, BorderLayout.CENTER);

            swatchesWrapper.setPreferredSize(swatchSize);

            // Hex input panel
            JPanel hexPanel = new JPanel();
            hexPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            hexPanel.setBorder(BorderFactory.createTitledBorder("Hex Input"));

            JTextField hexField = new JTextField(String.format("#%06X", getColor().getRGB() & 0xFFFFFF), 7);
            hexPanel.add(new JLabel("Hex:"));
            hexPanel.add(hexField);

            // Sync hex input with color chooser
            hexField.addActionListener(evt -> {
                String text = hexField.getText();
                if (text.matches("^#([0-9A-Fa-f]{6})$")) {
                    chooser.setColor(Color.decode(text));
                }
            });

            // Update hex field in real-time as color changes
            chooser.getSelectionModel().addChangeListener(ee -> {
                Color selected = chooser.getColor();
                hexField.setText(String.format("#%06X", selected.getRGB() & 0xFFFFFF));
            });

            JPanel swatchesAndHexWrapper = new JPanel();
            swatchesAndHexWrapper.setLayout(new BoxLayout(swatchesAndHexWrapper, BoxLayout.X_AXIS));
            swatchesAndHexWrapper.add(hexPanel);
            swatchesAndHexWrapper.add(Box.createRigidArea(new Dimension(10, 0)));
            swatchesAndHexWrapper.add(swatchesWrapper);

            // vertical panel for chooser + swatches
            JPanel verticalPanel = new JPanel();
            verticalPanel.setLayout(new BoxLayout(verticalPanel, BoxLayout.Y_AXIS));
            verticalPanel.add(chooser);
            verticalPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            verticalPanel.add(swatchesAndHexWrapper);


            JDialog dialog = new JDialog(
                    SwingUtilities.getWindowAncestor(ColorButtonPanel.this),
                    "Choose " + labelText,
                    Dialog.ModalityType.APPLICATION_MODAL
            );

            dialog.getContentPane().add(verticalPanel);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);

            // Update color after dialog closes
            setColor(chooser.getColor());

        });

        add(label);
        add(Box.createRigidArea(new Dimension(5, 0)));
        add(btn);
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setBorder(BorderFactory.createEmptyBorder(2, 0, 4, 0));
    }

    public void setColor(Color c) {
        btn.setBackground(c);
        btn.setText(String.format("#%06X", c.getRGB() & 0xFFFFFF));
        btn.setForeground(getContrastColor(c));
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

    private Color getContrastColor(Color c) {
        // Using YIQ formula to determine brightness
        int yiq = ((c.getRed()*299) + (c.getGreen()*587) + (c.getBlue()*114)) / 1000;
        return yiq >= 100 ? Color.BLACK : Color.WHITE;
    }

    public interface ColorChangeListener {
        void colorChanged(Color newColor);
    }
}