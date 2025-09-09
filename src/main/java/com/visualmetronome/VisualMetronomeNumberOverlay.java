package com.visualmetronome;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import javax.inject.Inject;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Font;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPriority;
import java.awt.Color;


public class VisualMetronomeNumberOverlay extends Overlay
{

    private final Client client;
    private final VisualMetronomeConfig config;
    private final VisualMetronomePlugin plugin;

    @Inject
    public VisualMetronomeNumberOverlay(Client client, VisualMetronomeConfig config, VisualMetronomePlugin plugin)
    {
        super(plugin);
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.UNDER_WIDGETS);
        setPriority(OverlayPriority.MED);

    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (config.showPlayerTick())
        {
            if (config.fontType() == FontTypes.REGULAR)
            {
                graphics.setFont(new Font(FontManager.getRunescapeFont().getName(), Font.PLAIN, config.fontSize()));
            }
            else
            {
                graphics.setFont(new Font(config.fontType().toString(), Font.PLAIN, config.fontSize()));
            }

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

            final int height = client.getLocalPlayer().getLogicalHeight() + config.overheadHeight();
            final LocalPoint localLocation = client.getLocalPlayer().getLocalLocation();
            final Point playerPoint = Perspective.localToCanvas(client, localLocation, client.getPlane(), height);
            final int valueX = playerPoint.getX() + config.overheadXCenterOffset();
            final int valueY = playerPoint.getY();
            final Point tickPoint = new Point(valueX,valueY);

            if (config.tickCount() == 1)
            {
                OverlayUtil.renderTextLocation(graphics, tickPoint, String.valueOf(plugin.currentColorIndex), numberColor);
            }
            else
            {
                OverlayUtil.renderTextLocation(graphics, tickPoint, String.valueOf(plugin.tickCounter), numberColor);
            }

            if (config.enableCycle2())
            {
                final int valueX2 = valueX - config.overheadCyclesGapDistance();
                final Point tick2Point = new Point(valueX2,valueY);
                OverlayUtil.renderTextLocation(graphics, tick2Point, String.valueOf(plugin.tickCounter2), config.cycle2Color());
            }

            if (config.enableCycle3())
            {
                final int valueX3 = valueX + config.overheadCyclesGapDistance();
                final Point tick3Point = new Point(valueX3,valueY);
                OverlayUtil.renderTextLocation(graphics, tick3Point, String.valueOf(plugin.tickCounter3), config.cycle3Color());
            }

        }
        return null;
    }

}


