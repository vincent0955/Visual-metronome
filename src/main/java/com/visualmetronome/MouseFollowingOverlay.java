package com.visualmetronome;


import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;
import javax.inject.Inject;
import java.awt.*;
import java.awt.Dimension;
import java.awt.Graphics2D;

public class MouseFollowingOverlay extends Overlay {
    private final Client client;
    private final VisualMetronomeConfig config;
    private final VisualMetronomePlugin plugin;

    @Inject
    public MouseFollowingOverlay(Client client, VisualMetronomeConfig config, VisualMetronomePlugin plugin) {
        super(plugin);
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (config.mouseFollowingTick()) {
            Point mousePos = plugin.getMousePosition();
            if (mousePos != null) {
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

                // Render
                OverlayUtil.renderTextLocation(graphics, textPosition, text, config.NumberColor());
            }
        }
        return null;
    }
}