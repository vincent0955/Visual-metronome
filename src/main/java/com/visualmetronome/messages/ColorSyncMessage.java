package com.visualmetronome.messages;

import net.runelite.client.party.messages.PartyMemberMessage;
import java.awt.Color;

public class ColorSyncMessage extends PartyMemberMessage
{
    // Core metronome colors
    private int colorCycle;
    private int tickColor;
    private int tockColor;
    private int tick3Color;
    private int tick4Color;
    private int tick5Color;
    private int tick6Color;
    private int tick7Color;
    private int tick8Color;
    private int tick9Color;
    private int tick10Color;

    // Tick number / overhead settings
    private int numberColor;
    private boolean overheadUseCurrentColor;
    private int cycle2Color;
    private int cycle3Color;

    // Tile overlay colors
    private int currentTileFillColor;
    private boolean changeFillColor;
    private int changeFillColorOpacity;

    private String reqSender;

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
            String reqSender
    )
    {
        this.colorCycle = colorCycle;
        this.tickColor = tickColor.getRGB();
        this.tockColor = tockColor.getRGB();
        this.tick3Color = tick3Color.getRGB();
        this.tick4Color = tick4Color.getRGB();
        this.tick5Color = tick5Color.getRGB();
        this.tick6Color = tick6Color.getRGB();
        this.tick7Color = tick7Color.getRGB();
        this.tick8Color = tick8Color.getRGB();
        this.tick9Color = tick9Color.getRGB();
        this.tick10Color = tick10Color.getRGB();
        this.numberColor = numberColor.getRGB();
        this.overheadUseCurrentColor = overheadUseCurrentColor;
        this.cycle2Color = cycle2Color.getRGB();
        this.cycle3Color = cycle3Color.getRGB();
        this.currentTileFillColor = currentTileFillColor.getRGB();
        this.changeFillColor = changeFillColor;
        this.changeFillColorOpacity = changeFillColorOpacity;
        this.reqSender = reqSender;
    }

    public int getColorCycle() { return colorCycle; }
    public Color getTickColor() { return new Color(tickColor, true); }
    public Color getTockColor() { return new Color(tockColor, true); }
    public Color getTick3Color() { return new Color(tick3Color, true); }
    public Color getTick4Color() { return new Color(tick4Color, true); }
    public Color getTick5Color() { return new Color(tick5Color, true); }
    public Color getTick6Color() { return new Color(tick6Color, true); }
    public Color getTick7Color() { return new Color(tick7Color, true); }
    public Color getTick8Color() { return new Color(tick8Color, true); }
    public Color getTick9Color() { return new Color(tick9Color, true); }
    public Color getTick10Color() { return new Color(tick10Color, true); }

    public Color getNumberColor() { return new Color(numberColor, true); }
    public boolean isOverheadUseCurrentColor() { return overheadUseCurrentColor; }
    public Color getCycle2Color() { return new Color(cycle2Color, true); }
    public Color getCycle3Color() { return new Color(cycle3Color, true); }

    public Color getCurrentTileFillColor() { return new Color(currentTileFillColor, true); }
    public boolean isChangeFillColor() { return changeFillColor; }
    public int getChangeFillColorOpacity() { return changeFillColorOpacity; }

    public String getReqSender() { return reqSender; }
}
