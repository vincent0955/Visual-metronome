package net.runelite.client.plugins.visualmetronome;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ComponentConstants;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import javax.inject.Inject;
import java.awt.*;

public class visualmetronomeOverlay extends Overlay {
    private final Client client;
    private final visualmetronomeConfig config;
    private final PanelComponent panelComponent = new PanelComponent();

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
        // Set the size of the overlay (width)
        panelComponent.setPreferredSize(new Dimension(config.boxWidth(), 0));
        panelComponent.setBackgroundColor(plugin.CurrentColor);
        // Build overlay title
        panelComponent.getChildren().add(TitleComponent.builder()
                .text(plugin.Title)
                .color(Color.white)
                .build());

        if (config.showTitle()) {

        }

        return panelComponent.render(graphics);
    }
}