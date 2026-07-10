package com.visualmetronome.fullresizableoverlay;

import com.visualmetronome.FontTypes;
import com.visualmetronome.VisualMetronomeConfig;
import com.visualmetronome.VisualMetronomePlugin;
import net.runelite.api.Point;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public abstract class FullResizableVisualMetronomeOverlay extends Overlay
{
    protected final VisualMetronomeConfig config;
    protected final VisualMetronomePlugin plugin;

    private static final int MINIMUM_SIZE = 16;

    protected FullResizableVisualMetronomeOverlay(VisualMetronomeConfig config, VisualMetronomePlugin plugin)
    {
        super(plugin);
        this.config = config;
        this.plugin = plugin;
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        setMinimumSize(MINIMUM_SIZE);
        setResizable(true);
    }

    protected abstract boolean isVisible();

    protected abstract String getTickText();

    protected abstract Color getTextColor();

    protected boolean showText()
    {
        return true;
    }

    protected boolean showBackgroundColor()
    {
        return true;
    }

    @Override
    public final Dimension render(Graphics2D graphics)
    {
        Dimension preferredSize = getPreferredSize();
        if (preferredSize == null)
        {
            preferredSize = new Dimension(config.boxWidth(), config.boxWidth());
            setPreferredSize(preferredSize);
        }

        if (isVisible())
        {
            if (showBackgroundColor())
            {
                graphics.setColor(plugin.getCurrentColor());
                graphics.fillRect(0, 0, preferredSize.width, preferredSize.height);
            }

            if (showText())
            {
                final String text = getTickText();
                if (config.disableFontScaling())
                {
                    final int padding = Math.min(preferredSize.width, preferredSize.height) / 2 - 4;
                    graphics.setColor(getTextColor());
                    graphics.drawString(text, padding, preferredSize.height - padding);
                }
                else
                {
                    final Font baseFont = (config.fontType() == FontTypes.REGULAR)
                        ? new Font(FontManager.getRunescapeFont().getName(), Font.PLAIN, 1)
                        : new Font(config.fontType().toString(), Font.PLAIN, 1);
                    graphics.setFont(getBestFitFont(graphics, baseFont, text, preferredSize.width, preferredSize.height));
                    final Point center = getCenteredTextPoint(graphics, text, preferredSize.width, preferredSize.height);
                    OverlayUtil.renderTextLocation(graphics, center, text, getTextColor());
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
