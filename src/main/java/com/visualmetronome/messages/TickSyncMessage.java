package com.visualmetronome.messages;

import net.runelite.client.party.messages.PartyMemberMessage;

public class TickSyncMessage extends PartyMemberMessage
{
    private int tickCounter;
    private int tickCounter2;
    private int tickCounter3;
    private int colorIndex;
    private int configColorIndex;
    private int tickCount;
    private int tickCount2;
    private int tickCount3;

    private String displayName;
    private String localSender;

    public TickSyncMessage() {}

    public TickSyncMessage(int tickCounter, int tickCounter2, int tickCounter3, int colorIndex, int configColorIndex, int tickCount, int tickCount2, int tickCount3, String localSender)
    {
        this.tickCounter = tickCounter;
        this.tickCounter2 = tickCounter2;
        this.tickCounter3 = tickCounter3;
        this.colorIndex = colorIndex;
        this.configColorIndex = configColorIndex;
        this.tickCount = tickCount;
        this.tickCount2 = tickCount2;
        this.tickCount3 = tickCount3;
        this.localSender = localSender;

    }

    public String getlocalSender()
    {
        return localSender;
    }

    public void setlocalSender(String localSender)
    {
        this.localSender = localSender;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    public int getTickCount()
    {
        return tickCount;
    }

    public int getTickCount2()
    {
        return tickCount2;
    }

    public int getTickCount3()
    {
        return tickCount3;
    }

    public int getTickCounter()
    {
        return tickCounter;
    }

    public int getTickCounter2()
    {
        return tickCounter2;
    }

    public int getTickCounter3()
    {
        return tickCounter3;
    }

    public int getColorIndex()
    {
        return colorIndex;
    }

    public int getConfigColorIndex()
    {
        return configColorIndex;
    }
}
