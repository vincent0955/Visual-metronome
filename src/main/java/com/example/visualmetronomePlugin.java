package net.runelite.client.plugins.visualmetronome;

import com.google.inject.Provides;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
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
    public String CurrentTick = "0";
    public Color CurrentColor = Color.WHITE;
    public String TitleStatus = "";
    public String TitleLength = "";

    @Provides
    visualmetronomeConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(visualmetronomeConfig.class);
    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        if (config.tickCount() == 0) {
            return;
        }
        if (config.tickCount() == 1) {
            // changes symbols of ticks
            if (CurrentTick == (config.tickSymbol())) {
                CurrentTick = config.tockSymbol();
            } else {
                CurrentTick = config.tickSymbol();
            }
            // changes color of ticks
            if (CurrentTick == (config.tickSymbol())) {
                CurrentColor = config.getTickColor();
            }
            else {
                CurrentColor = config.getTockColor();
            }
            // hides title if showTitle option is off
            if (!config.showTitle()) {
                TitleStatus = "";
            }
            else {TitleStatus = "Metronome";}

            // finds the longest string for overlay size
            if ((config.tickSymbol().length() >= TitleStatus.length()) || (config.tockSymbol().length() >= TitleStatus.length())) {
                if (config.tickSymbol().length() >= config.tockSymbol().length()) {
                    TitleLength = config.tickSymbol();
                    }
                else {TitleLength = config.tockSymbol();}
                }
            else {TitleLength = TitleStatus;}
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
