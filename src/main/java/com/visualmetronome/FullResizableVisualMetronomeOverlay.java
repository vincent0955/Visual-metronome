package com.visualmetronome;

import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import javax.inject.Inject;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.OverlayUtil;

public class FullResizableVisualMetronomeOverlay extends Overlay
{

    private final VisualMetronomeConfig config;
    private final VisualMetronomePlugin plugin;

    private static int TITLE_PADDING = 10;
    private static final int MINIMUM_SIZE = 16; // too small and resizing becomes impossible, requiring a reset
    private Point tickCounterCenter;

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
                    String text;
                    if (config.tickCount() == 1)
                    {
                        text = String.valueOf(plugin.currentColorIndex);
                    }
                    else
                    {
                        text = String.valueOf(plugin.tickCounter);
                    }
                    final Font baseFont = (config.fontType() == FontTypes.REGULAR)
                        ? new Font(FontManager.getRunescapeFont().getName(), Font.PLAIN, 1)
                        : new Font(config.fontType().toString(), Font.PLAIN, 1);
                    graphics.setFont(getBestFitFont(graphics, baseFont, text, preferredSize.width, preferredSize.height));
                    tickCounterCenter = getCenteredTextPoint(graphics, text, preferredSize.width, preferredSize.height);
                    OverlayUtil.renderTextLocation(graphics, tickCounterCenter, text, config.NumberColor());
                }
            }
        }

        return preferredSize;
    }

    private Font getBestFitFont(Graphics2D graphics, Font baseFont, String text, int boxWidth, int boxHeight)
    {
        final int maxSize = Math.max(MINIMUM_SIZE, Math.min(boxWidth, boxHeight));
        final int horizontalPadding = Math.max(2, boxWidth / 12);
        final int verticalPadding = Math.max(2, boxHeight / 12);
        final int availableWidth = Math.max(1, boxWidth - horizontalPadding);
        final int availableHeight = Math.max(1, boxHeight - verticalPadding);

        final Font maxFont = baseFont.deriveFont((float) maxSize);
        final FontMetrics maxMetrics = graphics.getFontMetrics(maxFont);
        final Rectangle maxBounds = maxMetrics.getStringBounds(text, graphics).getBounds();
        final int maxTextHeight = maxMetrics.getAscent() + maxMetrics.getDescent();

        final double widthScale = (maxBounds.width <= 0) ? 1.0 : (double) availableWidth / maxBounds.width;
        final double heightScale = (maxTextHeight <= 0) ? 1.0 : (double) availableHeight / maxTextHeight;
        final double fitScale = Math.min(1.0, Math.min(widthScale, heightScale));
        final double sizeBoost;
        if (config.fontType() == FontTypes.SEGOE_UI)
        {
            sizeBoost = 1.45;
        } else {
            sizeBoost = 1.2;
        }
        final int fittedSize = Math.min(
            maxSize,
            Math.max(MINIMUM_SIZE, (int) Math.floor(maxSize * fitScale * sizeBoost))
        );

        return baseFont.deriveFont((float) fittedSize);
    }

    private Point getCenteredTextPoint(Graphics2D graphics, String text, int boxWidth, int boxHeight)
    {
        final FontMetrics metrics = graphics.getFontMetrics();
        final Rectangle textBounds = metrics.getStringBounds(text, graphics).getBounds();
        final int textX = (boxWidth - textBounds.width) / 2;
        final int visualNudgeDown = (config.fontType() == FontTypes.REGULAR) ? Math.max(1, boxHeight / 8) : 0;
        final int textY = (boxHeight - (metrics.getAscent() + metrics.getDescent())) / 2 + metrics.getAscent() + visualNudgeDown;
        return new Point(textX, textY);
    }
}
