package com.visualmetronome.messages;

import net.runelite.client.party.messages.PartyMemberMessage;
import java.awt.Color;

public class ColorSyncMessage extends PartyMemberMessage
{
    // Core metronome colors
    private int colorCycle;
    private Color tickColor;
    private Color tockColor;
    private Color tick3Color;
    private Color tick4Color;
    private Color tick5Color;
    private Color tick6Color;
    private Color tick7Color;
    private Color tick8Color;
    private Color tick9Color;
    private Color tick10Color;

    // Tick number / overhead settings
    private Color numberColor;
    private boolean overheadUseCurrentColor;
    private Color cycle2Color;
    private Color cycle3Color;

    // Tile overlay colors
    private Color currentTileFillColor;
    private boolean changeFillColor;
    private int changeFillColorOpacity;

    private String displayName;
    private String localSender;

    public ColorSyncMessage() {}

    public ColorSyncMessage(
            int colorCycle,
            Color tickColor,
            Color tockColor,
            Color tick3Color,
            Color tick4Color,
            Color tick5Color,
            Color tick6Color,
            Color tick7Color,
            Color tick8Color,
            Color tick9Color,
            Color tick10Color,
            Color numberColor,
            boolean overheadUseCurrentColor,
            Color cycle2Color,
            Color cycle3Color,
            Color currentTileFillColor,
            boolean changeFillColor,
            int changeFillColorOpacity,
            String localSender
    )
    {
        this.colorCycle = colorCycle;
        this.tickColor = tickColor;
        this.tockColor = tockColor;
        this.tick3Color = tick3Color;
        this.tick4Color = tick4Color;
        this.tick5Color = tick5Color;
        this.tick6Color = tick6Color;
        this.tick7Color = tick7Color;
        this.tick8Color = tick8Color;
        this.tick9Color = tick9Color;
        this.tick10Color = tick10Color;
        this.numberColor = numberColor;
        this.overheadUseCurrentColor = overheadUseCurrentColor;
        this.cycle2Color = cycle2Color;
        this.cycle3Color = cycle3Color;
        this.currentTileFillColor = currentTileFillColor;
        this.changeFillColor = changeFillColor;
        this.changeFillColorOpacity = changeFillColorOpacity;
        this.localSender = localSender;
    }

    public int getColorCycle() { return colorCycle; }
    public Color getTickColor() { return tickColor; }
    public Color getTockColor() { return tockColor; }
    public Color getTick3Color() { return tick3Color; }
    public Color getTick4Color() { return tick4Color; }
    public Color getTick5Color() { return tick5Color; }
    public Color getTick6Color() { return tick6Color; }
    public Color getTick7Color() { return tick7Color; }
    public Color getTick8Color() { return tick8Color; }
    public Color getTick9Color() { return tick9Color; }
    public Color getTick10Color() { return tick10Color; }

    public Color getNumberColor() { return numberColor; }
    public boolean isOverheadUseCurrentColor() { return overheadUseCurrentColor; }
    public Color getCycle2Color() { return cycle2Color; }
    public Color getCycle3Color() { return cycle3Color; }

    public Color getCurrentTileFillColor() { return currentTileFillColor; }
    public boolean isChangeFillColor() { return changeFillColor; }
    public int getChangeFillColorOpacity() { return changeFillColorOpacity; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getLocalSender() { return localSender; }
    public void setLocalSender(String localSender) { this.localSender = localSender; }
}
