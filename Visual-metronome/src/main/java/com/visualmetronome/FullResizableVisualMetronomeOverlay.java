package com.visualmetronome;

import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Font;
import javax.inject.Inject;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.OverlayUtil;

public class FullResizableVisualMetronomeOverlay extends Overlay
{

    private final VisualMetronomeConfig config;
    private final VisualMetronomePlugin plugin;

    private static int TITLE_PADDING = 10;
    private static final int MINIMUM_SIZE = 16; // too small and resizing becomes impossible, requiring a reset

    @Inject
    public FullResizableVisualMetronomeOverlay(VisualMetronomeConfig config, VisualMetronomePlugin plugin)
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
            graphics.setColor(plugin.currentColor);
            graphics.fillRect(0, 0, preferredSize.width, preferredSize.height);
            TITLE_PADDING = (Math.min(preferredSize.width, preferredSize.height) / 2 - 4); // scales tick number position with box size

            if (config.showTick())
            {
                if (config.disableFontScaling())
                {
                    graphics.setColor(config.NumberColor());
                    if (config.tickCount() == 1)
                    {
                        graphics.drawString(String.valueOf(plugin.currentColorIndex), TITLE_PADDING, preferredSize.height - TITLE_PADDING);
                    }
                    else
                    {
                        graphics.drawString(String.valueOf(plugin.tickCounter), TITLE_PADDING, preferredSize.height - TITLE_PADDING);
                    }

                }
                else
                {
                    if (config.fontType() == FontTypes.REGULAR)
                    {
                        graphics.setFont(new Font(FontManager.getRunescapeFont().getName(), Font.PLAIN, Math.min(preferredSize.width, preferredSize.height))); //scales font size based on the size of the metronome
                    }
                    else
                    {
                        graphics.setFont(new Font(config.fontType().toString(), Font.PLAIN, Math.min(preferredSize.width, Math.min(preferredSize.width, preferredSize.height))));
                    }

                    final Point tickCounterPoint = new Point(preferredSize.width / 3, preferredSize.height);
                    if (config.tickCount() == 1)
                    {
                        OverlayUtil.renderTextLocation(graphics, tickCounterPoint, String.valueOf(plugin.currentColorIndex), config.NumberColor());
                    }
                    else
                    {
                        OverlayUtil.renderTextLocation(graphics, tickCounterPoint, String.valueOf(plugin.tickCounter), config.NumberColor());
                    }
                }
            }
        }

        return preferredSize;
    }
}
