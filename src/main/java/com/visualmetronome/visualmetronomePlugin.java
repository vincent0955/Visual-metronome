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
    private int tickCounter = 0;
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
        if (config.tickCount() != 1)
        {
            if (++tickCounter % config.tickCount() == 0)
            {
                tickCounter = 0;
                CurrentTick = !CurrentTick;
                if (CurrentTick)
                {
                    CurrentColor = config.getTickColor();
                }
                else
                    {
                    CurrentColor = config.getTockColor();
                }
            }
            return;
        }
        // changes color every tick
        switch (++tickCounter)
        {
            case 1:
                CurrentColor = config.getTickColor();
                break;
            case 2:
                CurrentColor = config.getTockColor();
                break;
            case 3:
                CurrentColor = config.getTick3Color();
                break;
            case 4:
                CurrentColor = config.getTick4Color();
                break;
            case 5:
                CurrentColor = config.getTick5Color();
                break;
            case 6:
                CurrentColor = config.getTick6Color();
                break;
            case 7:
                CurrentColor = config.getTick7Color();
                break;
            case 8:
                CurrentColor = config.getTick8Color();
                break;
            case 9:
                CurrentColor = config.getTick9Color();
                break;
            case 10:
                CurrentColor = config.getTick10Color();
        }
        if (tickCounter == config.colorCycle())
        {
            tickCounter = 0;
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
