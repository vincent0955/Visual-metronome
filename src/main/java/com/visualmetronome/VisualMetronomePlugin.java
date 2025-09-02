package com.visualmetronome;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.awt.Color;
import java.awt.Dimension;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.party.PartyService;
import net.runelite.client.party.WSClient;
import net.runelite.client.party.events.UserJoin;
import net.runelite.client.party.events.UserPart;
import com.visualmetronome.messages.TickSyncMessage;
import com.visualmetronome.messages.TickRequestMessage;
import net.runelite.client.party.PartyMember;
import net.runelite.client.util.ImageUtil;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    @Inject
    private ClientToolbar clientToolbar;

    private PartySyncPanel partySyncPanel;
    private NavigationButton navButton;

    private List<PartyMember> members = Collections.emptyList();
    private boolean hasRespondedThisTick = false;
    private PartyMember localPlayer;
    private String syncTarget;
    private static final BufferedImage ICON = ImageUtil.loadImageResource(PartySyncPanel.class,"/com.visualmetronome/Ice.png");

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

        //party sync
        hasRespondedThisTick = false;
        if (!config.enablePartySync())
        {
            return;
        }
        if (syncTarget != null && !syncTarget.isEmpty())
        {
            //filter the local player and party members who have left
            if (partySyncPanel != null)
            {
                partySyncPanel.updateMembers(
                        members.stream()
                                .map(PartyMember::getDisplayName)
                                .filter(name -> name != null && !name.equalsIgnoreCase("<unknown>"))
                                .filter(name -> localPlayer == null || !name.equalsIgnoreCase(localPlayer.getDisplayName()))
                                .collect(Collectors.toList())
                );

            }
            partyService.send(new TickRequestMessage(syncTarget));
        }
    }

    @Subscribe
    public void onTickRequestMessage(TickRequestMessage reqMsg)
    {
        if (localPlayer == null)
        {
            localPlayer = partyService.getLocalMember();
        }

        if (hasRespondedThisTick)
        {
            return;
        }

        String reqTarget = reqMsg.getTarget();

        if (!localPlayer.getDisplayName().equalsIgnoreCase(reqTarget))
        {
            return;
        }

        hasRespondedThisTick = true;

        TickSyncMessage syncMsg = new TickSyncMessage(
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
        partyService.send(syncMsg);
    }

    @Subscribe
    public void onTickSyncMessage(TickSyncMessage syncMsg)
    {
        if (!config.enablePartySync() || syncTarget == null)
        {
            return;
        }

        String sender = syncMsg.getlocalSender();

        if (!sender.equalsIgnoreCase(syncTarget))
        {
            return;
        }

        //  Apply received counters
        this.tickCounter = syncMsg.getTickCounter();
        this.tickCounter2 = syncMsg.getTickCounter2();
        this.tickCounter3 = syncMsg.getTickCounter3();

        this.currentColorIndex = syncMsg.getColorIndex();
        setCurrentColorByColorIndex(this.currentColorIndex);

        //  Update config so UI reflects remote tickCount
        configManager.setConfiguration(CONFIG_GROUP, "tickCount", syncMsg.getTickCount());
        configManager.setConfiguration(CONFIG_GROUP, "tickCount2", syncMsg.getTickCount2());
        configManager.setConfiguration(CONFIG_GROUP, "tickCount3", syncMsg.getTickCount3());
        configManager.setConfiguration(CONFIG_GROUP, "colorCycle", syncMsg.getConfigColorIndex());
    }

    @Subscribe
    public void onUserJoin(UserJoin event)
    {
        members = partyService.getMembers();
        localPlayer = partyService.getLocalMember();
        if (partySyncPanel != null)
        {
            partySyncPanel.updateMembers(
                    members.stream()
                            .map(PartyMember::getDisplayName)
                            .collect(Collectors.toList())
            );
        }
    }

    @Subscribe
    public void onUserPart(UserPart event)
    {
        members = partyService.getMembers();
        if (partySyncPanel != null)
        {
            partySyncPanel.updateMembers(
                    members.stream()
                            .map(PartyMember::getDisplayName)
                            .collect(Collectors.toList())
            );
        }
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

        partySyncPanel = new PartySyncPanel();
        partySyncPanel.loadFromConfig(config);

        navButton = NavigationButton.builder()
                .tooltip("Visual Metronome")
                .icon(ICON)
                .priority(10)
                .panel(partySyncPanel)
                .build();

        clientToolbar.addNavigation(navButton)
;
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
        members = Collections.emptyList();
        localPlayer = null;
        clientToolbar.removeNavigation(navButton);
        partySyncPanel = null;
        navButton = null;
        syncTarget = null;
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
                resetValue = (config.tickResetStartTick() >= config.tickCount()) ? 0 : config.tickResetStartTick();
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
