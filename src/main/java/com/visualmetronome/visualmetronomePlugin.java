package com.visualmetronome;

import com.google.inject.Provides;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.api.events.GameTick;
import java.awt.Color;
import javax.inject.Inject;

@PluginDescriptor(
        name = "Visual Metronome",
        description = "Shows a visual cue on an overlay every game tick to help timing based activities",
        tags = {"timers", "overlays", "tick", "skilling"}
)
public class visualmetronomePlugin extends Plugin
{
    @Inject
    private OverlayManager overlayManager;

    @Inject
    private visualmetronomeOverlay overlay;

    @Inject
    private visualmetronomeConfig config;

    private boolean CurrentTick = true;
    private int tickCounter;
    public Color CurrentColor = Color.WHITE;
    public String Title;

    @Provides
    visualmetronomeConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(visualmetronomeConfig.class);
    }

    @Subscribe
    public void onGameTick(GameTick tick)
    {
        // changes color every tick
        if (config.colorCycle() == 2)
        {
            CurrentTick = !CurrentTick;
            if (CurrentTick)
            {
                CurrentColor = config.getTickColor();
            }
            else {
                CurrentColor = config.getTockColor();
            }
        }
        else if (config.colorCycle() == 3)
        {
            if (tickCounter == 0)
            {
                CurrentColor = config.getTickColor();
            }
            else if (tickCounter == 1)
            {
                CurrentColor = config.getTockColor();
            }
            else if (tickCounter == 2)
            {
                CurrentColor = config.getTick3Color();
                tickCounter = -1;
            }
            tickCounter ++;
        }
        else if (config.colorCycle() == 4)
        {
            if (tickCounter == 0)
            {
                CurrentColor = config.getTickColor();
            }
            else if (tickCounter == 1)
            {
                CurrentColor = config.getTockColor();
            }
            else if (tickCounter == 2)
            {
                CurrentColor = config.getTick3Color();
            }
            else if (tickCounter == 3)
            {
                CurrentColor = config.getTick4Color();
                tickCounter = -1;
            }
            tickCounter ++;

        }
    }
    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        // hides title
        if (config.showTitle())
        {
            Title = "Metronome";
        }
        else {
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
        tickCounter = 0;
    }
}
