package com.visualmetronome;

import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

public class FullResizableVisualMetronomeOverlay extends Overlay {

    private final visualmetronomeConfig config;
    private final visualmetronomePlugin plugin;

    private static final int MINIMUM_SIZE = 16; // too small and resizing becomes impossible, requiring a reset

    @Inject
    public FullResizableVisualMetronomeOverlay(visualmetronomeConfig config, visualmetronomePlugin plugin) {
        super(plugin);
        this.config = config;
        this.plugin = plugin;
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        setMinimumSize(MINIMUM_SIZE);
        setResizable(true);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        Dimension preferredSize = getPreferredSize();

        if (preferredSize == null) {
            // if this happens, reset to default - should be rare, but eg. alt+rightclick will cause this
            preferredSize = plugin.DEFAULT_SIZE;
            setPreferredSize(preferredSize);
        }

        graphics.setColor(plugin.CurrentColor);
        graphics.fillRect(0, 0, preferredSize.width, preferredSize.height);

        return preferredSize;
    }
}
