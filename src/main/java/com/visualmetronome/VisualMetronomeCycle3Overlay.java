package com.visualmetronome;

import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.inject.Inject;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.OverlayUtil;

public class VisualMetronomeCycle3Overlay extends Overlay
{
    private final VisualMetronomeConfig config;
    private final VisualMetronomePlugin plugin;

    private static final int MINIMUM_SIZE = 16;

    @Inject
    public VisualMetronomeCycle3Overlay(VisualMetronomeConfig config, VisualMetronomePlugin plugin)
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
        if (!config.enableCycle3() || !config.showCycle3Overlay())
        {
            return null;
        }

        Dimension preferredSize = getPreferredSize();
        if (preferredSize == null)
        {
            preferredSize = plugin.DEFAULT_SIZE;
            setPreferredSize(preferredSize);
        }

        graphics.setColor(plugin.currentColor);
        graphics.fillRect(0, 0, preferredSize.width, preferredSize.height);

        final String text = String.valueOf(plugin.tickCounter3);
        if (config.disableFontScaling())
        {
            final int padding = Math.min(preferredSize.width, preferredSize.height) / 2 - 4;
            graphics.setColor(config.cycle3Color());
            graphics.drawString(text, padding, preferredSize.height - padding);
        }
        else
        {
            final Font baseFont = (config.fontType() == FontTypes.REGULAR)
                ? new Font(FontManager.getRunescapeFont().getName(), Font.PLAIN, 1)
                : new Font(config.fontType().toString(), Font.PLAIN, 1);
            graphics.setFont(getBestFitFont(graphics, baseFont, text, preferredSize.width, preferredSize.height));
            final Point center = getCenteredTextPoint(graphics, text, preferredSize.width, preferredSize.height);
            OverlayUtil.renderTextLocation(graphics, center, text, config.cycle3Color());
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
        final double sizeBoost = (config.fontType() == FontTypes.SEGOE_UI) ? 1.45 : 1.2;
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
