package com.visualmetronome;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import javax.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

public class visualmetronomeOverlay extends OverlayPanel
{
    private final Client client;
    private final visualmetronomeConfig config;
    private final visualmetronomePlugin plugin;

    @Inject
    private visualmetronomeOverlay(Client client, visualmetronomeConfig config, visualmetronomePlugin plugin)
    {
        this.plugin = plugin;
        this.client = client;
        this.config = config;
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        panelComponent.setPreferredSize(new Dimension(55, 0));
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        panelComponent.getChildren().clear();
        panelComponent.setBackgroundColor(plugin.CurrentColor);
        panelComponent.getChildren().add(LineComponent.builder()
                .left(plugin.Title)
                .leftColor(Color.WHITE)
                .build());

        return super.render(graphics);
    }
}