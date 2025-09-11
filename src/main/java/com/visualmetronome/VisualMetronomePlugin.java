package com.visualmetronome;

import com.google.inject.Provides;
import com.visualmetronome.messages.ColorRequestMessage;
import com.visualmetronome.messages.ColorSyncMessage;
import com.visualmetronome.panel.VisualMetronomePanel;
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
import javax.swing.SwingUtilities;

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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@PluginDescriptor(
        name = "Visual Metronome",
        description = "Shows a visual cue on an overlay every game tick to help timing based activities",
        tags = {"timers", "overlays", "tick", "skilling", "party"}
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

    private VisualMetronomePanel visualMetronomePanel;
    private NavigationButton navButton;

    private List<PartyMember> members = Collections.emptyList();
    private boolean hasRespondedThisTick = false;
    private PartyMember localPlayer;
    private String syncTarget;
    private static final BufferedImage ICON = ImageUtil.loadImageResource(VisualMetronomePanel.class,"/com.visualmetronome/vismetro.png");

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
        if (tickCounter % visualMetronomePanel.getTickCount() == 0)
        {
            tickCounter = 0;
            if (currentColorIndex == visualMetronomePanel.getColorCycle())
            {
                currentColorIndex = 0;
            }
            setCurrentColorByColorIndex(++currentColorIndex);
        }
        tickCounter++;
        if (tickCounter2 % visualMetronomePanel.getTickCount2() == 0){
            tickCounter2 = 0;
        }
        tickCounter2++;
        if (tickCounter3 % visualMetronomePanel.getTickCount3() == 0){
            tickCounter3 = 0;
        }
        tickCounter3++;

        //party sync
        hasRespondedThisTick = false;
        if (!visualMetronomePanel.isEnablePartySync())
        {
            return;
        }

        syncTarget = visualMetronomePanel.getSelectedMember();
        if (syncTarget != null && !syncTarget.isEmpty()) {
            if (localPlayer != null) {
                partyService.send(new TickRequestMessage(syncTarget));
            }
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

        TickSyncMessage syncMsg = visualMetronomePanel.toTickSyncMessage(
                tickCounter, tickCounter2, tickCounter3, currentColorIndex, localPlayer.getDisplayName()
        );
        partyService.send(syncMsg);
    }

    @Subscribe
    public void onTickSyncMessage(TickSyncMessage syncMsg)
    {
        if (!visualMetronomePanel.isEnablePartySync() || syncTarget == null)
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

        visualMetronomePanel.applyTickSyncMessage(syncMsg);
    }

    @Subscribe
    public void onColorRequestMessage(ColorRequestMessage colorReqMsg)
    {
        if (localPlayer == null)
        {
            localPlayer = partyService.getLocalMember();
        }

        String reqTarget = colorReqMsg.getTarget();
        String reqSender = colorReqMsg.getRequester();

        if (!localPlayer.getDisplayName().equalsIgnoreCase(reqTarget))
        {
            return;
        }

        partyService.send(visualMetronomePanel.toColorSyncMessage(reqSender));

    }

    @Subscribe
    public void onColorSyncMessage(ColorSyncMessage syncMsg)
    {
        if (!visualMetronomePanel.isEnablePartySync() || syncTarget == null)
        {
            return;
        }
        if(!localPlayer.getDisplayName().equalsIgnoreCase(syncMsg.getReqSender()))
        {
            return;
        }

        visualMetronomePanel.applyColorSyncMessage(syncMsg);
    }

    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    @Subscribe
    public void onUserJoin(UserJoin event)
    {
        //delay party service call to allow time for memberlist to be populated
        scheduler.schedule(() -> {
            members = partyService.getMembers();
            localPlayer = partyService.getLocalMember();

            if (visualMetronomePanel != null)
            {
                List<String> memberNames = members.stream()
                        .map(PartyMember::getDisplayName)
                        .filter(name -> !"<unknown>".equals(name))  // filter out <unknown>
                        .collect(Collectors.toList());

                SwingUtilities.invokeLater(() ->
                        visualMetronomePanel.updateMembers(memberNames, config, configManager)
                );
            }
        }, 200, TimeUnit.MILLISECONDS);
    }

    @Subscribe
    public void onUserPart(UserPart event)
    {
        //delay party service call to allow time for memberlist to be populated
        scheduler.schedule(() -> {
            members = partyService.getMembers();

            if (visualMetronomePanel != null)
            {
                List<String> memberNames = members.stream()
                        .map(PartyMember::getDisplayName)
                        .filter(name -> !"<unknown>".equals(name))
                        .collect(Collectors.toList());

                SwingUtilities.invokeLater(() ->
                        visualMetronomePanel.updateMembers(memberNames, config, configManager)
                );
            }
        }, 200, TimeUnit.MILLISECONDS);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if (!event.getGroup().equals("visualmetronome"))
            return;

        if (visualMetronomePanel == null)
            return;

        visualMetronomePanel.configHandler.loadFromConfig();

        // Existing plugin logic
        if (currentColorIndex > visualMetronomePanel.getColorCycle())
            currentColorIndex = 0;

        if (tickCounter > visualMetronomePanel.getTickCount())
            tickCounter = 0;
        if (tickCounter2 > visualMetronomePanel.getTickCount2())
            tickCounter2 = 0;
        if (tickCounter3 > visualMetronomePanel.getTickCount3())
            tickCounter3 = 0;

        DEFAULT_SIZE = new Dimension(visualMetronomePanel.getBoxWidth(), visualMetronomePanel.getBoxWidth());
    }


    @Override
    protected void startUp() throws Exception
    {
        visualMetronomePanel = new VisualMetronomePanel(configManager, config, partyService);
        DEFAULT_SIZE = new Dimension(visualMetronomePanel.getBoxWidth(), visualMetronomePanel.getBoxWidth());
        overlay.setPreferredSize(DEFAULT_SIZE);
        overlayManager.add(overlay);
        overlayManager.add(tileOverlay);
        overlayManager.add(numberOverlay);
        overlayManager.add(mouseFollowingOverlay);
        keyManager.registerKeyListener(this);
        wsClient.registerMessage(TickSyncMessage.class);
        wsClient.registerMessage(TickRequestMessage.class);
        wsClient.registerMessage(ColorSyncMessage.class);
        wsClient.registerMessage(ColorRequestMessage.class);

        visualMetronomePanel.configHandler.loadFromConfig();

        navButton = NavigationButton.builder()
                .tooltip("Visual Metronome")
                .icon(ICON)
                .priority(10)
                .panel(visualMetronomePanel)
                .build();

        clientToolbar.addNavigation(navButton);
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
        currentColor = visualMetronomePanel.getTickColor(1);
        overlayManager.remove(mouseFollowingOverlay);
        keyManager.unregisterKeyListener(this);
        wsClient.unregisterMessage(TickSyncMessage.class);
        wsClient.unregisterMessage(TickRequestMessage.class);
        wsClient.unregisterMessage(ColorSyncMessage.class);
        wsClient.unregisterMessage(ColorRequestMessage.class);
        members = Collections.emptyList();
        localPlayer = null;
        clientToolbar.removeNavigation(navButton);
        visualMetronomePanel = null;
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
        if (visualMetronomePanel.getTickResetHotkey().matches(e))
        {
            int resetValue = 0;

            // Reset Cycle 1
            if (visualMetronomePanel.getTickCount() > 1)
            {
                resetValue = (visualMetronomePanel.getTickResetStartTick() >= visualMetronomePanel.getTickCount()) ? 0 : visualMetronomePanel.getTickResetStartTick();
                currentColorIndex = resetValue == 0 ? 0 : 1;
            }
            else
            {
                resetValue = (visualMetronomePanel.getTickResetStartTick() >= visualMetronomePanel.getColorCycle()) ? 0 : visualMetronomePanel.getTickResetStartTick();
                currentColorIndex = resetValue;
            }
            tickCounter = resetValue;
            setCurrentColorByColorIndex(currentColorIndex);

            tickCounter2 = (visualMetronomePanel.getTickResetStartTick() >= visualMetronomePanel.getTickCount2()) ? 0 : visualMetronomePanel.getTickResetStartTick();
            tickCounter3 = (visualMetronomePanel.getTickResetStartTick() >= visualMetronomePanel.getTickCount3()) ? 0 : visualMetronomePanel.getTickResetStartTick();
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
    }

    private void setCurrentColorByColorIndex(int currentColorIndex)
    {
        if (currentColorIndex >= 1 && currentColorIndex <= 10)
        {
            currentColor = visualMetronomePanel.getTickColor(currentColorIndex);
        }
    }
}
