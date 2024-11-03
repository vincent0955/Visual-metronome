package com.polyrhythmmetronome;

import com.google.inject.Provides;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.awt.Color;
import java.awt.Dimension;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;

@PluginDescriptor(
        name = "Poly Rhythm Metronome",
        description = "Shows visual cues on an overlay every game tick to help track different cycle lengths",
        tags = {"timers", "overlays", "tick", "skilling"}
)
public class PolyRhythmMetronomePlugin extends Plugin implements KeyListener {
    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ConfigManager configManager;

    @Inject
    private PolyRhythmMetronomeNumberOverlay numberOverlay;

    @Inject
    private PolyRhythmMetronomeConfig config;

    @Inject
    private KeyManager keyManager;


    protected int tickCounter = 0;
    protected int tickCounter2 = 0;
    protected int tickCounter3 = 0;
    protected int tickCounter4 = 0;
    protected int tickCounter5 = 0;
    protected Color currentColor = Color.WHITE;

    protected Dimension DEFAULT_SIZE = new Dimension(25, 25);

    @Provides
    PolyRhythmMetronomeConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(PolyRhythmMetronomeConfig.class);
    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        if (tickCounter % config.tickCount() == 0) {
            tickCounter = 0;
        }
        if (tickCounter2 % config.tickCount2() == 0) {
            tickCounter2 = 0;
        }
        if (tickCounter3 % config.tickCount3() == 0) {
            tickCounter3 = 0;
        }
        if (tickCounter4% config.tickCount3() == 0) {
            tickCounter4 = 0;
        }
        if (tickCounter5% config.tickCount3() == 0) {
            tickCounter5 = 0;
        }
        tickCounter++;
        tickCounter2++;
        tickCounter3++;
        tickCounter4++;
        tickCounter5++;
    }

    public void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals("polyrhythmmetronome")) {
            return;
        }


        if (tickCounter > config.tickCount()) {
            tickCounter = 0;
        }
        if (tickCounter2 > config.tickCount2()) {
            tickCounter2 = 0;
        }
        if (tickCounter3 > config.tickCount2()) {
            tickCounter3 = 0;
        }
        if (tickCounter4> config.tickCount2()) {
            tickCounter4 = 0;
        }
        if (tickCounter5> config.tickCount2()) {
            tickCounter5 = 0;
        }

    }

    @Override
    protected void startUp() throws Exception
    {
        overlayManager.add(numberOverlay);
        keyManager.registerKeyListener(this);
    }


    @Override
    protected void shutDown() throws Exception
    {
        overlayManager.remove(numberOverlay);
        tickCounter = 0;
        tickCounter2 = 0;
        tickCounter3 = 0;
        tickCounter4 = 0;
        tickCounter5 = 0;
        keyManager.unregisterKeyListener(this);
    }

    //hotkey settings
    @Override
    public void keyTyped(KeyEvent e)
    {
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        if (config.tickResetHotkey().matches(e))
        {
            tickCounter = 0;
            tickCounter2 = 0;
            tickCounter3 = 0;
            tickCounter4 = 0;
            tickCounter5 = 0;
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
    }
}
