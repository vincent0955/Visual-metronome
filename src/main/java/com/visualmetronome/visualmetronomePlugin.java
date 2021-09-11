package com.visualmetronome;

import com.google.inject.Provides;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

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
    private ConfigManager configManager;

    @Inject
    private visualmetronomeTileOverlay tileOverlay;

    @Inject
    private FullResizableVisualMetronomeOverlay overlay;

    @Inject
    private visualmetronomeConfig config;

    private boolean CurrentTick = true;
    public int tickCounter = 0;
    public Color CurrentColor = Color.WHITE;

    public Dimension DEFAULT_SIZE = new Dimension(25, 25);

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
            if (tickCounter % config.tickCount() == 0)
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
            tickCounter++;
            return;
        }
        // changes color every tick
        if (tickCounter == config.colorCycle())
        {
            tickCounter = 0;
        }
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

    }
    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if (tickCounter > config.colorCycle())
        {
            tickCounter = 0;
        }
        DEFAULT_SIZE = new Dimension(config.boxWidth(), config.boxWidth());
    }

    @Override
    protected void startUp() throws Exception
    {
        overlayManager.add(overlay);
        overlay.setPreferredSize(DEFAULT_SIZE);
        overlayManager.add(tileOverlay);
    }

    @Override
    protected void shutDown() throws Exception
    {
        overlayManager.remove(overlay);
        overlayManager.remove(tileOverlay);
        tickCounter = 0;
    }
}
