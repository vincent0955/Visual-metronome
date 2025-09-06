package com.visualmetronome.messages;

import net.runelite.client.party.messages.PartyMemberMessage;

public class ColorRequestMessage extends PartyMemberMessage
{
    private String target;

    public ColorRequestMessage() {}

    public ColorRequestMessage(String target)
    {
        this.target = target;
    }

    public String getTarget()
    {
        return target;
    }

    public void setTarget(String target)
    {
        this.target = target;
    }
}