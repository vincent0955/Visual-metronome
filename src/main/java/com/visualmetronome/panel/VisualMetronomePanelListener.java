package com.visualmetronome.panel;

import net.runelite.client.party.PartyMember;

import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.stream.Collectors;

public class VisualMetronomePanelListener {
    private final VisualMetronomeConfigHandler configHandler;
    private final VisualMetronomePanel panel;

    public VisualMetronomePanelListener(VisualMetronomePanel panel, VisualMetronomeConfigHandler configHandler) {
        this.panel = panel;
        this.configHandler = configHandler;
        setupListeners();
    }

    private void setupListeners() {
        ActionListener updateAction = e -> configHandler.updateConfigThrottled();
        ChangeListener updateChange = e -> configHandler.updateConfigThrottled();

        // --- General Metronome ---
        panel.enableMetronome.addActionListener(updateAction);
        panel.highlightCurrentTile.addActionListener(updateAction);
        panel.boxWidth.addChangeListener(updateChange);
        panel.tickCount.addChangeListener(updateChange);

        // --- Tick Number ---
        panel.showTick.addActionListener(updateAction);
        panel.showPlayerTick.addActionListener(updateAction);
        panel.disableFontScaling.addActionListener(updateAction);
        panel.fontSize.addChangeListener(updateChange);
        panel.fontType.addActionListener(updateAction);
        panel.numberColorBtn.addColorChangeListener(c -> configHandler.updateConfigThrottled());

        // --- True Tile Overlay ---
        panel.currentTileFillColorBtn.addColorChangeListener(c -> configHandler.updateConfigThrottled());
        panel.currentTileBorderWidth.addChangeListener(updateChange);
        panel.changeFillColor.addActionListener(updateAction);
        panel.changeFillColorOpacity.addChangeListener(updateChange);

        // --- Party Sync ---
        panel.enablePartySync.addActionListener(updateAction);
        panel.memberDropdown.addActionListener(e -> {
            String selected = (String) panel.memberDropdown.getSelectedItem();
            if (selected != null && !selected.equals(panel.lastSelectedMember)) {
                panel.lastSelectedMember = selected;

                if (panel.partyService != null) {
                    List<PartyMember> membersList = panel.partyService.getMembers();
                    List<String> memberNames = membersList.stream()
                            .map(PartyMember::getDisplayName)
                            .filter(name -> !"<unknown>".equals(name))
                            .collect(Collectors.toList());
                    SwingUtilities.invokeLater(() -> panel.updateMembers(memberNames, panel.config, panel.configManager));
                }
            }
        });

        // --- Color Settings ---
        panel.colorCycleSpinner.addChangeListener(updateChange);
        for (ColorButtonPanel btn : panel.tickColorBtns) {
            btn.addColorChangeListener(c -> configHandler.updateConfigThrottled());
        }

        // --- Hotkeys ---
        panel.tickResetStartTick.addChangeListener(updateChange);
        //tickResetHotkeyBtn.addActionListener(updateAction);

        // --- Mouse Following ---
        panel.mouseFollowingTick.addActionListener(updateAction);
        panel.mouseOffsetX.addChangeListener(updateChange);
        panel.mouseOffsetY.addChangeListener(updateChange);

        // --- Additional Overhead Cycles ---
        panel.enableCycle2.addActionListener(updateAction);
        panel.tickCount2.addChangeListener(updateChange);
        panel.cycle2ColorBtn.addColorChangeListener(c -> configHandler.updateConfigThrottled());

        panel.enableCycle3.addActionListener(updateAction);
        panel.tickCount3.addChangeListener(updateChange);
        panel.cycle3ColorBtn.addColorChangeListener(c -> configHandler.updateConfigThrottled());

        panel.overheadCyclesGapDistance.addChangeListener(updateChange);
        panel.overheadHeight.addChangeListener(updateChange);
        panel.overheadXCenterOffset.addChangeListener(updateChange);
        panel.overheadUseCurrentColor.addActionListener(updateAction);
    }
}
