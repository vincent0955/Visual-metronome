package com.visualmetronome;

import com.google.inject.Provides;
import net.runelite.api.Point;
import net.runelite.api.Client;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import net.runelite.client.party.PartyService;
import net.runelite.client.party.WSClient;
import net.runelite.client.party.events.UserJoin;
import net.runelite.client.party.events.UserPart;
import com.visualmetronome.messages.TickSyncMessage;
import com.visualmetronome.messages.TickRequestMessage;
import net.runelite.client.party.PartyMember;

import java.util.Collections;
import java.util.List;


@PluginDescriptor(
        name = "Visual Metronome",
        description = "Shows a visual cue on an overlay every game tick to help timing based activities",
        tags = {"timers", "overlays", "tick", "skilling"}
)
public class VisualMetronomePlugin extends Plugin implements KeyListener
{

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

    @Inject
    private Client client;

    @Inject
    private MouseFollowingOverlay mouseFollowingOverlay;

    @Inject
    private PartyService partyService;

    @Inject
    private WSClient wsClient;

    List<PartyMember> members = Collections.emptyList();
    private boolean hasRespondedThisTick = false;

    private static final String CONFIG_GROUP = "visualmetronome";
    protected int currentColorIndex = 0;
    protected int tickCounter = 0;
    protected int tickCounter2 = 0;
    protected int tickCounter3 = 0;
    protected Color currentColor = Color.WHITE;
    protected Dimension DEFAULT_SIZE = new Dimension(25, 25);

    @Provides
    VisualMetronomeConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(VisualMetronomeConfig.class);
    }

    @Subscribe
    public void onGameTick(GameTick tick)
    {
        hasRespondedThisTick = false;

        if (tickCounter % config.tickCount() == 0)
        {
            tickCounter = 0;
            if (currentColorIndex == config.colorCycle())
            {
                currentColorIndex = 0;
            }
            setCurrentColorByColorIndex(++currentColorIndex);
        }
        tickCounter++;
        if (tickCounter2 % config.tickCount2() == 0){
            tickCounter2 = 0;
        }
        tickCounter2++;
        if (tickCounter3 % config.tickCount3() == 0){
            tickCounter3 = 0;
        }
        tickCounter3++;

        members = partyService.getMembers();

        //party sync
        if (config.enablePartySync())
        {
            if (!members.isEmpty())
            {
                String targetName = config.syncTarget();
                partyService.send(new TickRequestMessage(targetName));
            }
        }

    }

    @Subscribe
    public void onTickRequestMessage(TickRequestMessage target)
    {
        String syncTarget = target.getTarget();
        PartyMember localPlayer = partyService.getLocalMember();

        if (!localPlayer.getDisplayName().equalsIgnoreCase(syncTarget))
        {
            return;
        }

        if (hasRespondedThisTick)
        {
            return;
        }
        hasRespondedThisTick = true;

        TickSyncMessage msg = new TickSyncMessage(
                tickCounter,
                tickCounter2,
                tickCounter3,
                currentColorIndex,
                config.colorCycle(),
                config.tickCount(),
                config.tickCount2(),
                config.tickCount3(),
                localPlayer.getDisplayName()
        );
        partyService.send(msg);
    }

    @Subscribe
    public void onTickSyncMessage(TickSyncMessage msg)
    {
        if (!config.enablePartySync())
        {
            return;
        }

        String Sender = msg.getlocalSender();
        String targetName = config.syncTarget();

        if (!Sender.equalsIgnoreCase(targetName))
        {
            return;
        }

        //  Apply received counters
        this.tickCounter = msg.getTickCounter();
        this.tickCounter2 = msg.getTickCounter2();
        this.tickCounter3 = msg.getTickCounter3();

        this.currentColorIndex = msg.getColorIndex();
        setCurrentColorByColorIndex(this.currentColorIndex);

        //  Update config so UI reflects remote tickCount
        configManager.setConfiguration(
                CONFIG_GROUP,
                "tickCount",
                msg.getTickCount()
        );
        configManager.setConfiguration(
                CONFIG_GROUP,
                "tickCount2",
                msg.getTickCount2()
        );
        configManager.setConfiguration(
                CONFIG_GROUP,
                "tickCount3",
                msg.getTickCount3()
        );
        configManager.setConfiguration(
                CONFIG_GROUP,
                "colorCycle",
                msg.getConfigColorIndex()
        );
    }

    @Subscribe
    public void onUserJoin(UserJoin event)
    {
        members = partyService.getMembers();
    }

    @Subscribe
    public void onUserPart(UserPart event)
    {
        members = partyService.getMembers();
    }


    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
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
    }

    @Override
    protected void startUp() throws Exception
    {
        DEFAULT_SIZE = new Dimension(config.boxWidth(), config.boxWidth());
        overlay.setPreferredSize(DEFAULT_SIZE);
        overlayManager.add(overlay);
        overlayManager.add(tileOverlay);
        overlayManager.add(numberOverlay);
        overlayManager.add(mouseFollowingOverlay);
        keyManager.registerKeyListener(this);
        wsClient.registerMessage(TickSyncMessage.class);
        wsClient.registerMessage(TickRequestMessage.class);

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
        overlayManager.remove(mouseFollowingOverlay);
        keyManager.unregisterKeyListener(this);
        wsClient.unregisterMessage(TickSyncMessage.class);
        wsClient.unregisterMessage(TickRequestMessage.class);

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
