package com.visualmetronome.panel;

import com.visualmetronome.VisualMetronomeConfig;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.Keybind;

import javax.swing.SwingUtilities;
import java.util.Arrays;

public class VisualMetronomeConfigHandler
{
    private final ConfigManager configManager;
    private final VisualMetronomeConfig config;
    private final VisualMetronomePanel panel;
    private boolean updatingFromConfig = false;
    private boolean updatePending = false;

    public VisualMetronomeConfigHandler(ConfigManager configManager,
                                        VisualMetronomeConfig config,
                                        VisualMetronomePanel panel)
    {
        this.configManager = configManager;
        this.config = config;
        this.panel = panel;
    }

    public void loadFromConfig()
    {
        updatingFromConfig = true;

        panel.enableMetronome.setSelected(config.enableMetronome());
        panel.highlightCurrentTile.setSelected(config.highlightCurrentTile());
        panel.boxWidth.setValue((double) config.boxWidth());
        panel.tickCount.setValue((double) config.tickCount());

        panel.showTick.setSelected(config.showTick());
        panel.showPlayerTick.setSelected(config.showPlayerTick());
        panel.disableFontScaling.setSelected(config.disableFontScaling());
        panel.fontSize.setValue((double) config.fontSize());
        panel.numberColorBtn.setColor(config.NumberColor());
        panel.fontType.setSelectedItem(config.fontType().name());

        panel.currentTileFillColorBtn.setColor(config.currentTileFillColor());
        panel.currentTileBorderWidth.setValue(config.currentTileBorderWidth());
        panel.changeFillColor.setSelected(config.changeFillColor());
        panel.changeFillColorOpacity.setValue((double) config.changeFillColorOpacity());

        panel.enablePartySync.setSelected(config.enablePartySync());
        panel.lastSelectedMember = config.syncTarget();
        panel.updateMembers(null, config, configManager);

        panel.colorCycleSpinner.setValue((double) config.colorCycle());
        panel.tickColorBtns[0].setColor(config.getTickColor());
        panel.tickColorBtns[1].setColor(config.getTockColor());
        panel.tickColorBtns[2].setColor(config.getTick3Color());
        panel.tickColorBtns[3].setColor(config.getTick4Color());
        panel.tickColorBtns[4].setColor(config.getTick5Color());
        panel.tickColorBtns[5].setColor(config.getTick6Color());
        panel.tickColorBtns[6].setColor(config.getTick7Color());
        panel.tickColorBtns[7].setColor(config.getTick8Color());
        panel.tickColorBtns[8].setColor(config.getTick9Color());
        panel.tickColorBtns[9].setColor(config.getTick10Color());

        panel.tickResetHotkey = config.tickResetHotkey();
        if (panel.tickResetHotkey != null && panel.tickResetHotkey != Keybind.NOT_SET) {
            panel.tickResetHotkeyBtn.setText("Hotkey: " + panel.tickResetHotkey.toString());
        } else {
            panel.tickResetHotkeyBtn.setText("Set Reset Hotkey");
        }

        panel.tickResetStartTick.setValue((double) config.tickResetStartTick());
        panel.mouseFollowingTick.setSelected(config.mouseFollowingTick());
        panel.mouseOffsetX.setValue((double) config.mouseOffsetX());
        panel.mouseOffsetY.setValue((double) config.mouseOffsetY());

        panel.enableCycle2.setSelected(config.enableCycle2());
        panel.tickCount2.setValue((double) config.tickCount2());
        panel.cycle2ColorBtn.setColor(config.cycle2Color());

        panel.enableCycle3.setSelected(config.enableCycle3());
        panel.tickCount3.setValue((double) config.tickCount3());
        panel.cycle3ColorBtn.setColor(config.cycle3Color());

        panel.overheadCyclesGapDistance.setValue((double) config.overheadCyclesGapDistance());
        panel.overheadHeight.setValue((double) config.overheadHeight());
        panel.overheadXCenterOffset.setValue((double) config.overheadXCenterOffset());
        panel.overheadUseCurrentColor.setSelected(config.overheadUseCurrentColor());

        updatingFromConfig = false;
    }

    public void updateConfig()
    {
        if (updatingFromConfig) return;

        configManager.setConfiguration("visualmetronome", "enableMetronome", panel.isEnableMetronome());
        configManager.setConfiguration("visualmetronome", "highlightCurrentTile", panel.isHighlightCurrentTile());
        configManager.setConfiguration("visualmetronome", "boxWidth", panel.getBoxWidth());
        configManager.setConfiguration("visualmetronome", "tickCount", panel.getTickCount());

        configManager.setConfiguration("visualmetronome", "showTick", panel.isShowTick());
        configManager.setConfiguration("visualmetronome", "showPlayerTick", panel.isShowPlayerTick());
        configManager.setConfiguration("visualmetronome", "disableFontScaling", panel.isDisableFontScaling());
        configManager.setConfiguration("visualmetronome", "fontSize", panel.getFontSize());
        configManager.setConfiguration("visualmetronome", "countColor", panel.numberColorBtn.getColor());
        configManager.setConfiguration("visualmetronome", "fontType", panel.getFontType());

        configManager.setConfiguration("visualmetronome", "currentTileFillColor", panel.currentTileFillColorBtn.getColor());
        configManager.setConfiguration("visualmetronome", "currentTileBorderWidth", panel.getCurrentTileBorderWidth());
        configManager.setConfiguration("visualmetronome", "changeFillColor", panel.isChangeFillColor());
        configManager.setConfiguration("visualmetronome", "changeFillColorOpacity", panel.getChangeFillColorOpacity());

        panel.lastSelectedMember = (String) panel.memberDropdown.getSelectedItem();
        configManager.setConfiguration("visualmetronome", "enablePartySync", panel.isEnablePartySync());
        configManager.setConfiguration("visualmetronome", "syncTarget", panel.lastSelectedMember);

        configManager.setConfiguration("visualmetronome", "colorCycle", panel.getColorCycle());
        for (int i = 0; i < 10; i++) {
            configManager.setConfiguration("visualmetronome", "tick" + (i + 1) + "Color", panel.tickColorBtns[i].getColor());
        }

        configManager.setConfiguration("visualmetronome", "tickResetStartTick", panel.getTickResetStartTick());
        configManager.setConfiguration("visualmetronome", "tickResetHotkey", panel.getTickResetHotkey());

        configManager.setConfiguration("visualmetronome", "mouseFollowingTick", panel.isMouseFollowingTick());
        configManager.setConfiguration("visualmetronome", "mouseOffsetX", panel.getMouseOffsetX());
        configManager.setConfiguration("visualmetronome", "mouseOffsetY", panel.getMouseOffsetY());

        configManager.setConfiguration("visualmetronome", "showSecondCycle", panel.isEnableCycle2());
        configManager.setConfiguration("visualmetronome", "tickCount2", panel.getTickCount2());
        configManager.setConfiguration("visualmetronome", "cycle2Color", panel.cycle2ColorBtn.getColor());

        configManager.setConfiguration("visualmetronome", "showThirdCycle", panel.isEnableCycle3());
        configManager.setConfiguration("visualmetronome", "tickCount3", panel.getTickCount3());
        configManager.setConfiguration("visualmetronome", "cycle3Color", panel.cycle3ColorBtn.getColor());

        configManager.setConfiguration("visualmetronome", "overheadCyclesGapDistance", panel.getOverheadCyclesGapDistance());
        configManager.setConfiguration("visualmetronome", "overheadHeight", panel.getOverheadHeight());
        configManager.setConfiguration("visualmetronome", "overheadXCenterOffset", panel.getOverheadXCenterOffset());
        configManager.setConfiguration("visualmetronome", "overheadUseCurrentColor", panel.isOverheadUseCurrentColor());
    }

    public void updateConfigThrottled()
    {
        if (updatePending) return;
        updatePending = true;
        SwingUtilities.invokeLater(() -> {
            updateConfig();
            updatePending = false;
        });
    }
}
