package com.visualmetronome;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import javax.inject.Inject;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPriority;


public class visualmetronomeTileOverlay extends Overlay
{

    private final Client client;
    private final visualmetronomeConfig config;
    private final visualmetronomePlugin plugin;

    @Inject
    public visualmetronomeTileOverlay(Client client, visualmetronomeConfig config, visualmetronomePlugin plugin)
    {
        super(plugin);
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(OverlayPriority.MED);
    }

    @Override
    public Dimension render(Graphics2D graphics) {

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

            //renderTile(graphics, playerPosLocal, plugin.CurrentColor, config.currentTileFillColor(), 2);
            renderTile(graphics, playerPosLocal, plugin.CurrentColor, config.currentTileFillColor(), config.currentTileBorderWidth());
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


