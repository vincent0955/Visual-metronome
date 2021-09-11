package com.visualmetronome;

import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;

public class FullResizableVisualMetronomeOverlay extends Overlay
{

    private final visualmetronomeConfig config;
    private final visualmetronomePlugin plugin;

    private static int TITLE_PADDING = 10;
    private static final int MINIMUM_SIZE = 16; // too small and resizing becomes impossible, requiring a reset

    @Inject
    public FullResizableVisualMetronomeOverlay(visualmetronomeConfig config, visualmetronomePlugin plugin)
    {
        super(plugin);
        this.config = config;
        this.plugin = plugin;
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        setMinimumSize(MINIMUM_SIZE);
        setResizable(true);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        Dimension preferredSize = getPreferredSize();

        if (preferredSize == null)
        {
            // if this happens, reset to default - should be rare, but eg. alt+rightclick will cause this
            preferredSize = plugin.DEFAULT_SIZE;
            setPreferredSize(preferredSize);
        }

        if (config.enableMetronome())
        {
            graphics.setColor(plugin.CurrentColor);
            graphics.fillRect(0, 0, preferredSize.width, preferredSize.height);
            TITLE_PADDING = (Math.min(preferredSize.width, preferredSize.height) / 2 - 4); // scales tick number position with box size

            if (config.showTick()) {
                graphics.setColor(config.NumberColor());
                graphics.drawString(String.valueOf(plugin.tickCounter), TITLE_PADDING, preferredSize.height - TITLE_PADDING);
            }
        }

        return preferredSize;
    }
}
