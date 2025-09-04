package com.visualmetronome.panel;

import net.runelite.client.config.Keybind;

import javax.swing.JLabel;
import javax.swing.JDialog;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyCaptureDialog extends JDialog
{
    private Keybind capturedKeybind;

    public KeyCaptureDialog()
    {
        setTitle("Press a key...");
        setModal(true);
        setSize(300, 100);
        setLocationRelativeTo(null);

        JLabel label = new JLabel("Press a key to bind (ESC to cancel)", SwingConstants.CENTER);
        add(label, BorderLayout.CENTER);

        addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                {
                    capturedKeybind = null;
                }
                else
                {
                    capturedKeybind = new Keybind(e.getKeyCode(), e.getModifiersEx());
                }
                dispose();
            }
        });

        // Ensure the dialog actually receives key focus
        setFocusable(true);
        requestFocusInWindow();
    }

    public Keybind showAndGetKeybind()
    {
        setVisible(true);
        return capturedKeybind;
    }
}
