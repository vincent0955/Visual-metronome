package com.visualmetronome;

import com.visualmetronome.panel.VisualMetronomePanel;

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


public class VisualMetronomeNumberOverlay extends Overlay {

    private final Client client;
    private final VisualMetronomePlugin plugin;
    private final VisualMetronomePanel panel;

    @Inject
    public VisualMetronomeNumberOverlay(Client client, VisualMetronomePanel panel, VisualMetronomePlugin plugin) {
        super(plugin);
        this.client = client;
        this.plugin = plugin;
        this.panel = panel;

        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.UNDER_WIDGETS);
        setPriority(OverlayPriority.MED);

    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (panel.isShowPlayerTick()) {
            if (panel.getFontType().equals(FontTypes.REGULAR.name())) {
                graphics.setFont(new Font(FontManager.getRunescapeFont().getName(), Font.PLAIN, panel.getFontSize()));
            } else {
                graphics.setFont(new Font(panel.getFontType(), Font.PLAIN, panel.getFontSize()));
            }

            // Set which color to use
            Color numberColor;
            if (panel.isOverheadUseCurrentColor()) {
                numberColor = plugin.currentColor;
            } else {
                numberColor = panel.getNumberColor();
            }

            final int height = client.getLocalPlayer().getLogicalHeight() + panel.getOverheadHeight();
            final LocalPoint localLocation = client.getLocalPlayer().getLocalLocation();
            final Point playerPoint = Perspective.localToCanvas(client, localLocation, client.getPlane(), height);
            final int valueX = playerPoint.getX() + panel.getOverheadXCenterOffset();
            final int valueY = playerPoint.getY();
            final Point tickPoint = new Point(valueX, valueY);

            if (panel.getTickCount() == 1) {
                OverlayUtil.renderTextLocation(graphics, tickPoint, String.valueOf(plugin.currentColorIndex), numberColor);
            } else {
                OverlayUtil.renderTextLocation(graphics, tickPoint, String.valueOf(plugin.tickCounter), numberColor);
            }

            if (panel.isEnableCycle2()) {
                final int valueX2 = valueX - panel.getOverheadCyclesGapDistance();
                final Point tick2Point = new Point(valueX2, valueY);
                OverlayUtil.renderTextLocation(graphics, tick2Point, String.valueOf(plugin.tickCounter2), panel.getCycle2Color());
            }

            if (panel.isEnableCycle3()) {
                final int valueX3 = valueX + panel.getOverheadCyclesGapDistance();
                final Point tick3Point = new Point(valueX3, valueY);
                OverlayUtil.renderTextLocation(graphics, tick3Point, String.valueOf(plugin.tickCounter3), panel.getCycle3Color());
            }

        }
        return null;
    }
}


