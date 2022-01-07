package com.visualmetronome;

import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;

import java.awt.*;
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
            if (config.enableCycle())
            {
                if (config.colorCycle()>3)
                {
                    if (plugin.tickCounter == 4) {
                        graphics.setColor(config.getTick4Color());
                        graphics.fillRect(0, 0, preferredSize.width, preferredSize.height);
                    } else {
                        graphics.setColor(new Color((config.getTick4Color()).getRed(), (config.getTick4Color()).getGreen(), (config.getTick4Color()).getBlue(), (config.getTick4Color()).getAlpha() / 6));
                        graphics.fillRect(0, 0, preferredSize.width, preferredSize.height);
                    }
                }

                if (config.colorCycle()>2)
                {
                    if (plugin.tickCounter == 3)
                    {
                        graphics.setColor(config.getTick3Color());
                        graphics.fillRect(0 - ((preferredSize.width + 3)), 0, preferredSize.width, preferredSize.height);
                    }
                    else 
                        {
                        graphics.setColor(new Color((config.getTick3Color()).getRed(), (config.getTick3Color()).getGreen(), (config.getTick3Color()).getBlue(), (config.getTick3Color()).getAlpha() / 6));
                        graphics.fillRect(0 - ((preferredSize.width + 3)), 0, preferredSize.width, preferredSize.height);
                    }
                }

                if (plugin.tickCounter == 2)
                {
                    graphics.setColor(config.getTockColor());
                    graphics.fillRect(0 - ((preferredSize.width + 3) * 2), 0, preferredSize.width, preferredSize.height);
                }
                else {
                    graphics.setColor(new Color((config.getTockColor()).getRed(), (config.getTockColor()).getGreen(), (config.getTockColor()).getBlue(), (config.getTockColor()).getAlpha() /6));
                    graphics.fillRect(0 - ((preferredSize.width + 3) * 2), 0, preferredSize.width, preferredSize.height);
                }
                if (plugin.tickCounter == 1)
                {
                    graphics.setColor(config.getTickColor());
                    graphics.fillRect(0 - ((preferredSize.width + 3) * 3), 0, preferredSize.width, preferredSize.height);
                }
                else {
                    graphics.setColor(new Color((config.getTickColor()).getRed(), (config.getTickColor()).getGreen(), (config.getTickColor()).getBlue(), (config.getTickColor()).getAlpha() /6));
                    graphics.fillRect(0 - ((preferredSize.width + 3) * 3), 0, preferredSize.width, preferredSize.height);
                }

                if (config.showTick()) {
                    graphics.setColor(config.NumberColor());
                    graphics.drawString(String.valueOf(plugin.tickCounter), TITLE_PADDING, preferredSize.height - TITLE_PADDING);

                }
            }
            else{
                graphics.setColor(plugin.CurrentColor);
                graphics.fillRect(0, 0, preferredSize.width, preferredSize.height);
                TITLE_PADDING = (Math.min(preferredSize.width, preferredSize.height) / 2 - 4); // scales tick number position with box size

                if (config.showTick()) {
                    graphics.setColor(config.NumberColor());
                    graphics.drawString(String.valueOf(plugin.tickCounter), TITLE_PADDING, preferredSize.height - TITLE_PADDING);
                }
            }

        }

        return preferredSize;
    }
}
