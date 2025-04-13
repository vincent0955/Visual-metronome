package com.visualmetronome;

import com.google.inject.Provides;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.awt.Color;
import java.awt.Dimension;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;

@PluginDescriptor(
        name = "Visual Metronome",
        description = "Shows a visual cue on an overlay every game tick to help timing based activities",
        tags = {"timers", "overlays", "tick", "skilling"}
)
public class VisualMetronomePlugin extends Plugin implements KeyListener
{
    // Region IDs
    private static final int VERZIK_REGION = 12613;
    private static final int OLM_REGION = 12889;
    private static final int FORTIS_REGION = 14595;

    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ConfigManager configManager;

    @Inject
    private VisualMetronomeTileOverlay tileOverlay;

    @Inject
    private VisualMetronomeNumberOverlay numberOverlay;

    @Inject
    private FullResizableVisualMetronomeOverlay overlay;

    @Inject
    private VisualMetronomeConfig config;

    @Inject
    private KeyManager keyManager;

    protected int currentColorIndex = 0;
    protected int tickCounter = 0;
    protected int tickCounter2 = 0;
    protected int tickCounter3 = 0;
    protected Color currentColor = Color.WHITE;
    protected Dimension DEFAULT_SIZE = new Dimension(25, 25);
    private int currentRegion = -1;
    private boolean wasInRegion = false;

    @Provides
    VisualMetronomeConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(VisualMetronomeConfig.class);
    }

    private boolean isInRegion(int regionId)
    {
        if (client.getLocalPlayer() == null)
        {
            return false;
        }
        return client.getLocalPlayer().getWorldLocation().getRegionID() == regionId;
    }

    private void checkRegionChange()
    {
        if (!config.enableRegionBased())
        {
            return;
        }

        boolean inVerzik = isInRegion(VERZIK_REGION);
        boolean inOlm = isInRegion(OLM_REGION);
        boolean inFortis = isInRegion(FORTIS_REGION);

        if (inVerzik && config.enableVerzik())
        {
            if (!wasInRegion)
            {
                client.addChatMessage(Text.fromMarkdown("Visual Metronome activated in Verzik's room with " + config.verzikTickCount() + " tick cycle"));
                tickCounter = 0;
                currentColorIndex = 0;
                setCurrentColorByColorIndex(1);
            }
            currentRegion = VERZIK_REGION;
            wasInRegion = true;
        }
        else if (inOlm && config.enableOlm())
        {
            if (!wasInRegion)
            {
                client.addChatMessage(Text.fromMarkdown("Visual Metronome activated in Olm's room with " + config.olmTickCount() + " tick cycle"));
                tickCounter = 0;
                currentColorIndex = 0;
                setCurrentColorByColorIndex(1);
            }
            currentRegion = OLM_REGION;
            wasInRegion = true;
        }
        else if (inFortis && config.enableFortis())
        {
            if (!wasInRegion)
            {
                client.addChatMessage(Text.fromMarkdown("Visual Metronome activated in Fortis Colosseum with " + config.fortisTickCount() + " tick cycle"));
                tickCounter = 0;
                currentColorIndex = 0;
                setCurrentColorByColorIndex(1);
            }
            currentRegion = FORTIS_REGION;
            wasInRegion = true;
        }
        else
        {
            if (wasInRegion)
            {
                client.addChatMessage(Text.fromMarkdown("Visual Metronome deactivated - left region"));
            }
            currentRegion = -1;
            wasInRegion = false;
        }
    }

    @Subscribe
    public void onGameTick(GameTick tick)
    {
        checkRegionChange();

        if (!config.enableRegionBased() || currentRegion != -1)
        {
            int currentTickCount = config.tickCount();
            if (currentRegion == VERZIK_REGION)
            {
                currentTickCount = config.verzikTickCount();
            }
            else if (currentRegion == OLM_REGION)
            {
                currentTickCount = config.olmTickCount();
            }
            else if (currentRegion == FORTIS_REGION)
            {
                currentTickCount = config.fortisTickCount();
            }

            if (tickCounter % currentTickCount == 0)
            {
                tickCounter = 0;
                if (currentColorIndex == config.colorCycle())
                {
                    currentColorIndex = 0;
                }
                setCurrentColorByColorIndex(++currentColorIndex);
            }
            tickCounter++;
        }

        if (tickCounter2 % config.tickCount2() == 0){
            tickCounter2 = 0;
        }
        tickCounter2++;
        if (tickCounter3 % config.tickCount3() == 0){
            tickCounter3 = 0;
        }
        tickCounter3++;
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        if (event.getGameState() == GameState.LOGGED_IN)
        {
            checkRegionChange();
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if (!event.getGroup().equals("visualmetronome"))
        {
            return;
        }

        if (currentColorIndex > config.colorCycle())
        {
            currentColorIndex = 0;
        }

        if (tickCounter > config.tickCount())
        {
            tickCounter = 0;
        }
        if (tickCounter2 > config.tickCount2())
        {
            tickCounter2 = 0;
        }
        if (tickCounter3 > config.tickCount3())
        {
            tickCounter3 = 0;
        }

        DEFAULT_SIZE = new Dimension(config.boxWidth(), config.boxWidth());
        
        // Check region on config change
        checkRegionChange();
    }

    @Override
    protected void startUp() throws Exception
    {
        DEFAULT_SIZE = new Dimension(config.boxWidth(), config.boxWidth());
        overlay.setPreferredSize(DEFAULT_SIZE);
        overlayManager.add(overlay);
        overlayManager.add(tileOverlay);
        overlayManager.add(numberOverlay);
        keyManager.registerKeyListener(this);
    }

    @Override
    protected void shutDown() throws Exception
    {
        overlayManager.remove(overlay);
        overlayManager.remove(tileOverlay);
        overlayManager.remove(numberOverlay);
        tickCounter = 0;
        tickCounter2 = 0;
        tickCounter3 = 0;
        currentColorIndex = 0;
        currentColor = config.getTickColor();
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
            int resetValue = 0;

            // Reset Cycle 1
            if (config.tickCount() > 1)
            {
                // Prevent out of bounds by setting to 0 if reset start is above tick count
                resetValue = (config.tickResetStartTick() >= config.tickCount()) ? 0 : config.tickResetStartTick();
                // If resetting to 0, set color index to 0 as well so that the color is set to the first color next
                // onGameTick
                currentColorIndex = resetValue == 0 ? 0 : 1;
            }
            else
            {
                resetValue = (config.tickResetStartTick() >= config.colorCycle()) ? 0 : config.tickResetStartTick();
                currentColorIndex = resetValue;
            }
            tickCounter = resetValue;
            setCurrentColorByColorIndex(currentColorIndex);

            tickCounter2 = (config.tickResetStartTick() >= config.tickCount2()) ? 0 : config.tickResetStartTick();
            tickCounter3 = (config.tickResetStartTick() >= config.tickCount3()) ? 0 : config.tickResetStartTick();
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
    }

    private void setCurrentColorByColorIndex(int currentColorIndex)
    {
        switch (currentColorIndex)
        {
            case 1:
                currentColor = config.getTickColor();
                break;
            case 2:
                currentColor = config.getTockColor();
                break;
            case 3:
                currentColor = config.getTick3Color();
                break;
            case 4:
                currentColor = config.getTick4Color();
                break;
            case 5:
                currentColor = config.getTick5Color();
                break;
            case 6:
                currentColor = config.getTick6Color();
                break;
            case 7:
                currentColor = config.getTick7Color();
                break;
            case 8:
                currentColor = config.getTick8Color();
                break;
            case 9:
                currentColor = config.getTick9Color();
                break;
            case 10:
                currentColor = config.getTick10Color();
        }
    }
}
