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
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Font;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPriority;


public class VisualMetronomeTileOverlay extends Overlay
{

    private final Client client;
    private final VisualMetronomeConfig config;
    private final VisualMetronomePlugin plugin;

    @Inject
    public VisualMetronomeTileOverlay(Client client, VisualMetronomeConfig config, VisualMetronomePlugin plugin)
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
        if (config.highlightCurrentTile())
        {
            final WorldPoint playerPos = client.getLocalPlayer().getWorldLocation();
            if (playerPos == null)
            {
                return null;
            }

            final LocalPoint playerPosLocal = LocalPoint.fromWorld(client, playerPos);
            if (playerPosLocal == null)
            {
                return null;
            }

            if (config.changeFillColor())
            {
                final Color fillColor = new Color(plugin.currentColor.getRed(), plugin.currentColor.getGreen(), plugin.currentColor.getBlue(), config.changeFillColorOpacity());
                renderTile(graphics, playerPosLocal, plugin.currentColor, fillColor, config.currentTileBorderWidth());
            }
            else
            {
                renderTile(graphics, playerPosLocal, plugin.currentColor, config.currentTileFillColor(), config.currentTileBorderWidth());
            }
        }

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

            final int height = client.getLocalPlayer().getLogicalHeight()+20;
            final LocalPoint localLocation = client.getLocalPlayer().getLocalLocation();
            final Point playerPoint = Perspective.localToCanvas(client, localLocation, client.getPlane(), height);
            if (config.tickCount() == 1)
            {
                OverlayUtil.renderTextLocation(graphics, playerPoint, String.valueOf(plugin.currentColorIndex), config.NumberColor());
            }
            else
            {
                OverlayUtil.renderTextLocation(graphics, playerPoint, String.valueOf(plugin.tickCounter), config.NumberColor());
            }
        }

        return null;
    }

    private void renderTile(final Graphics2D graphics, final LocalPoint dest, final Color color, final Color fillColor, final double borderWidth)
    {
        if (dest == null)
        {
            return;
        }

        final Polygon poly = Perspective.getCanvasTilePoly(client, dest);

        if (poly == null)
        {
            return;
        }

        OverlayUtil.renderPolygon(graphics, poly, color, fillColor, new BasicStroke((float) borderWidth));
    }
}


