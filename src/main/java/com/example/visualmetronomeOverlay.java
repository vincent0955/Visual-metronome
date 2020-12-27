package net.runelite.client.plugins.visualmetronome;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ComponentConstants;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import javax.inject.Inject;
import java.awt.*;

public class visualmetronomeOverlay extends Overlay {
    private final Client client;
    private final visualmetronomeConfig config;
    private final PanelComponent panelComponent = new PanelComponent();
    private Color transparent = new Color(0,0,0,0);

    @Inject
    private visualmetronomeOverlay(Client client, visualmetronomeConfig config) {
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        this.client = client;
        this.config = config;
    }
    @Inject
    private visualmetronomePlugin plugin;

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().clear();
        if (config.toggleBackground()) {panelComponent.setBackgroundColor(transparent);}
        else {panelComponent.setBackgroundColor(config.backgroundColor());}
        String overlayTitle = plugin.TitleStatus;
        // Build overlay title
        if (config.showTitle()) {
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text(overlayTitle)
                    .color(Color.white)
                    .build());
        }
        // Set the size of the overlay (width)
        panelComponent.setPreferredSize(new Dimension(
                graphics.getFontMetrics().stringWidth(plugin.TitleLength) + 5,
                0));

        // Add a line on the overlay for world number
        panelComponent.getChildren().add(LineComponent.builder()
                .right(plugin.CurrentTick)
                .rightColor(plugin.CurrentColor)
                .build());

        return panelComponent.render(graphics);

    }
}