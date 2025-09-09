package com.visualmetronome;

import com.visualmetronome.panel.VisualMetronomePanel;
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


public class VisualMetronomeTileOverlay extends Overlay
{

    private final Client client;
    private final VisualMetronomePanel panel;
    private final VisualMetronomePlugin plugin;

    @Inject
    public VisualMetronomeTileOverlay(Client client, VisualMetronomePanel panel, VisualMetronomePlugin plugin)
    {
        super(plugin);
        this.client = client;
        this.panel = panel;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(OverlayPriority.MED);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (panel.isHighlightCurrentTile())
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

            if (panel.isChangeFillColor())
            {
                final Color fillColor = new Color(plugin.currentColor.getRed(), plugin.currentColor.getGreen(), plugin.currentColor.getBlue(), panel.getChangeFillColorOpacity());
                renderTile(graphics, playerPosLocal, plugin.currentColor, fillColor, panel.getCurrentTileBorderWidth());
            }
            else
            {
                renderTile(graphics, playerPosLocal, plugin.currentColor, panel.getCurrentTileFillColor(), panel.getCurrentTileBorderWidth());
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