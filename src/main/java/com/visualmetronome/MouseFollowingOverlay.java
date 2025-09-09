package com.visualmetronome;

import com.visualmetronome.panel.VisualMetronomePanel;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.Graphics2D;

public class MouseFollowingOverlay extends Overlay {
    private final Client client;
    private final VisualMetronomePanel panel;
    private final VisualMetronomePlugin plugin;

    @Inject
    public MouseFollowingOverlay(Client client, VisualMetronomePanel panel, VisualMetronomePlugin plugin)
    {
        super(plugin);
        this.client = client;
        this.panel = panel;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        setPriority(OverlayPriority.HIGH);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (panel.isMouseFollowingTick())
        {
            Point mousePos = client.getMouseCanvasPosition();
            if (mousePos != null)
            {
                // Set font
                Font font = panel.getFontType().equals(FontTypes.REGULAR.name())
                        ? FontManager.getRunescapeFont().deriveFont(Font.PLAIN, panel.getFontSize())
                        : new Font(panel.getFontType(), Font.PLAIN, panel.getFontSize());
                graphics.setFont(font);

                // Set text
                String text = panel.getTickCount() == 1
                        ? String.valueOf(plugin.currentColorIndex)
                        : String.valueOf(plugin.tickCounter);

                // Apply configurable offsets using getX()/getY()
                Point textPosition = new Point(
                        (int)mousePos.getX() + panel.getMouseOffsetX(),
                        (int)mousePos.getY() + panel.getMouseOffsetY()
                );

                // Set which color to use
                Color numberColor;
                if (panel.isOverheadUseCurrentColor())
                {
                    numberColor = plugin.currentColor;
                }
                else
                {
                    numberColor = panel.getNumberColor();
                }

                // Render
                OverlayUtil.renderTextLocation(graphics, textPosition, text, numberColor);
            }
        }
        return null;
    }
}
