package com.visualmetronome;

import com.google.inject.Provides;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.api.events.GameTick;
import java.awt.*;
import javax.inject.Inject;

@PluginDescriptor(
        name = "Visual Metronome",
        description = "Shows a visual cue on an overlay every game tick to help timing based activities",
        tags = {"timers", "overlays", "tick", "skilling"},
        enabledByDefault = false
)
public class visualmetronomePlugin extends Plugin
{
    @Inject
    private OverlayManager overlayManager;

    @Inject
    private visualmetronomeOverlay overlay;

    @Inject
    private visualmetronomeConfig config;

    //variables
    public boolean CurrentTick = true;
    public Color CurrentColor = Color.WHITE;
    public String Title = "Metronome";

    @Provides
    visualmetronomeConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(visualmetronomeConfig.class);
    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        // changes color every tick
        CurrentTick = !CurrentTick;
        if (CurrentTick) {
            CurrentColor = config.getTickColor();
        }
        else {
            CurrentColor = config.getTockColor();
        }
    }
    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        // hides title
        if (config.showTitle()) {
            Title = "Metronome";
        } else {
            Title = "";
        }
    }

    @Override
    protected void startUp() throws Exception
    {
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() throws Exception
    {
        overlayManager.remove(overlay);
    }
}
