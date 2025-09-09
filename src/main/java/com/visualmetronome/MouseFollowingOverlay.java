package com.visualmetronome;

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
    private final VisualMetronomeConfig config;
    private final VisualMetronomePlugin plugin;

    @Inject
    public MouseFollowingOverlay(Client client, VisualMetronomeConfig config, VisualMetronomePlugin plugin)
    {
        super(plugin);
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        setPriority(OverlayPriority.HIGH);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (config.mouseFollowingTick())
        {
            Point mousePos = client.getMouseCanvasPosition();
            if (mousePos != null)
            {
                // Set font
                Font font = config.fontType() == FontTypes.REGULAR
                        ? FontManager.getRunescapeFont().deriveFont(Font.PLAIN, config.fontSize())
                        : new Font(config.fontType().toString(), Font.PLAIN, config.fontSize());
                graphics.setFont(font);

                // Set text
                String text = config.tickCount() == 1
                        ? String.valueOf(plugin.currentColorIndex)
                        : String.valueOf(plugin.tickCounter);

                // Apply configurable offsets using getX()/getY()
                Point textPosition = new Point(
                        (int)mousePos.getX() + config.mouseOffsetX(),
                        (int)mousePos.getY() + config.mouseOffsetY()
                );

                // Set which color to use

                Color numberColor;
                if (config.overheadUseCurrentColor())
                {
                    numberColor = plugin.currentColor;
                }
                else
                {
                    numberColor = config.NumberColor();
                }

                // Render
                OverlayUtil.renderTextLocation(graphics, textPosition, text, numberColor);
            }
        }
        return null;
    }
}