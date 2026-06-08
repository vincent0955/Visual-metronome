package com.visualmetronome;

import net.runelite.api.Client;
import net.runelite.api.Constants;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.ColorUtil;
import javax.inject.Inject;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPriority;


// Corner rendering taken from Corner Tile Indicators plugin
public class VisualMetronomeTileOverlay extends Overlay
{

    private final Client client;
    private final VisualMetronomeConfig config;
    private final VisualMetronomePlugin plugin;

    private static final int CORNER_SIZE_DIVISOR = 4;

    private WorldPoint lastPlayerPosition = new WorldPoint(0, 0, 0);
    private int lastTickPlayerMoved = 0;
    private long lastTimePlayerStoppedMoving = 0;

    @Inject
    public VisualMetronomeTileOverlay(Client client, VisualMetronomeConfig config, VisualMetronomePlugin plugin)
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
    public Dimension render(Graphics2D graphics)
    {
        if (!config.highlightCurrentTile())
        {
            return null;
        }

        final WorldPoint playerPos = client.getLocalPlayer().getWorldLocation();
        if (playerPos == null)
        {
            return null;
        }

        if (!playerPos.equals(lastPlayerPosition))
        {
            lastTickPlayerMoved = client.getTickCount();
        }
        else if (lastTickPlayerMoved + 1 == client.getTickCount())
        {
            lastTimePlayerStoppedMoving = System.currentTimeMillis();
        }

        lastPlayerPosition = playerPos;

        final LocalPoint playerPosLocal = LocalPoint.fromWorld(client, playerPos);
        if (playerPosLocal == null)
        {
            return null;
        }

        final Color color = plugin.currentColor;
        final Color fillColor;
        if (config.changeFillColor())
        {
            fillColor = new Color(plugin.currentColor.getRed(), plugin.currentColor.getGreen(), plugin.currentColor.getBlue(), config.changeFillColorOpacity());
        }
        else
        {
            fillColor = config.currentTileFillColor();
        }

        // When not fading out, or when it has been 1 game tick or less since the player last moved, draw at full
        // opacity. Drawing at full opacity for 1 game tick prevents the indicator from fading out when moving on
        // consecutive ticks.
        if (!config.trueTileFadeout() || client.getTickCount() - lastTickPlayerMoved <= 1)
        {
            renderTile(graphics, playerPosLocal, color, fillColor, config.currentTileBorderWidth(), config.currentTileCornersOnly());
        }
        else
        {
            // It is more than 1 game tick after the player stopped moving, so fade out the tile.
            final long timeSinceLastMove = System.currentTimeMillis() - lastTimePlayerStoppedMoving;
            // The fadeout does not begin for 1 game tick, so subtract that.
            final int fadeoutTime = config.trueTileFadeoutTime() - Constants.GAME_TICK_LENGTH;
            if (fadeoutTime != 0 && timeSinceLastMove < fadeoutTime)
            {
                final double opacity = 1.0d - Math.pow(timeSinceLastMove / (double) fadeoutTime, 2);
                renderTile(graphics, playerPosLocal,
                    ColorUtil.colorWithAlpha(color, (int) (opacity * color.getAlpha())),
                    ColorUtil.colorWithAlpha(fillColor, (int) (opacity * fillColor.getAlpha())),
                    config.currentTileBorderWidth(), config.currentTileCornersOnly());
            }
        }

        return null;
    }

    private void renderTile(final Graphics2D graphics, final LocalPoint dest, final Color color, final Color fillColor, final double borderWidth, final boolean cornersOnly)
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

        if (cornersOnly)
        {
            renderPolygonCorners(graphics, poly, color, fillColor, new BasicStroke((float) borderWidth), CORNER_SIZE_DIVISOR);
        }
        else
        {
            OverlayUtil.renderPolygon(graphics, poly, color, fillColor, new BasicStroke((float) borderWidth));
        }
    }

    public static void renderPolygonCorners(Graphics2D graphics, Polygon poly, Color color, Color fillColor, Stroke borderStroke, int divisor)
    {
        graphics.setColor(color);
        final Stroke originalStroke = graphics.getStroke();
        graphics.setStroke(borderStroke);

        for (int i = 0; i < poly.npoints; i++)
        {
            int ptx = poly.xpoints[i];
            int pty = poly.ypoints[i];
            int prev = (i - 1) < 0 ? (poly.npoints - 1) : (i - 1);
            int next = (i + 1) > (poly.npoints - 1) ? 0 : (i + 1);
            int ptxN = ((poly.xpoints[next]) - ptx) / divisor + ptx;
            int ptyN = ((poly.ypoints[next]) - pty) / divisor + pty;
            int ptxP = ((poly.xpoints[prev]) - ptx) / divisor + ptx;
            int ptyP = ((poly.ypoints[prev]) - pty) / divisor + pty;
            graphics.drawLine(ptx, pty, ptxN, ptyN);
            graphics.drawLine(ptx, pty, ptxP, ptyP);
        }

        graphics.setColor(fillColor);
        graphics.fill(poly);

        graphics.setStroke(originalStroke);
    }
}
