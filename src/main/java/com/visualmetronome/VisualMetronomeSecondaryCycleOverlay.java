package com.visualmetronome;

import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.OverlayUtil;

public class VisualMetronomeSecondaryCycleOverlay extends Overlay
{
    private final VisualMetronomeConfig config;
    private final VisualMetronomePlugin plugin;
    private final int cycleNumber;

    private static final int MINIMUM_SIZE = 16;

    public VisualMetronomeSecondaryCycleOverlay(VisualMetronomeConfig config, VisualMetronomePlugin plugin, int cycleNumber)
    {
        super(plugin);
        this.config = config;
        this.plugin = plugin;
        if (cycleNumber != 2 && cycleNumber != 3)
        {
            throw new IllegalArgumentException("cycleNumber must be 2 or 3");
        }
        this.cycleNumber = cycleNumber;
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        setMinimumSize(MINIMUM_SIZE);
        setResizable(true);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!isCycleEnabled() || !showOverlay())
        {
            return null;
        }

        Dimension preferredSize = getPreferredSize();
        if (preferredSize == null)
        {
            preferredSize = plugin.DEFAULT_SIZE;
            setPreferredSize(preferredSize);
        }

        final String text = getTickText();
        final Color textColor = getTextColor();
        if (config.disableFontScaling())
        {
            final int padding = Math.min(preferredSize.width, preferredSize.height) / 2 - 4;
            graphics.setColor(textColor);
            graphics.drawString(text, padding, preferredSize.height - padding);
        }
        else
        {
            final Font baseFont = (config.fontType() == FontTypes.REGULAR)
                ? new Font(FontManager.getRunescapeFont().getName(), Font.PLAIN, 1)
                : new Font(config.fontType().toString(), Font.PLAIN, 1);
            graphics.setFont(getBestFitFont(graphics, baseFont, text, preferredSize.width, preferredSize.height));
            final Point center = getCenteredTextPoint(graphics, text, preferredSize.width, preferredSize.height);
            OverlayUtil.renderTextLocation(graphics, center, text, textColor);
        }

        return preferredSize;
    }

    private boolean isCycleEnabled()
    {
        return cycleNumber == 2 ? config.enableCycle2() : config.enableCycle3();
    }

    private boolean showOverlay()
    {
        return cycleNumber == 2 ? config.showCycle2Overlay() : config.showCycle3Overlay();
    }

    private String getTickText()
    {
        final int tickCounter = cycleNumber == 2 ? plugin.tickCounter2 : plugin.tickCounter3;
        return String.valueOf(tickCounter);
    }

    private Color getTextColor()
    {
        return cycleNumber == 2 ? config.cycle2Color() : config.cycle3Color();
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
