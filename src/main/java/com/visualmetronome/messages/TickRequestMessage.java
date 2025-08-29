package com.visualmetronome.messages;

import net.runelite.client.party.messages.PartyMemberMessage;

public class TickRequestMessage extends PartyMemberMessage
{
    private String target;

    public TickRequestMessage() {}

    public TickRequestMessage(String target)
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
