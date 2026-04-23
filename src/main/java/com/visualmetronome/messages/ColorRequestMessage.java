package com.visualmetronome.messages;

import net.runelite.client.party.messages.PartyMemberMessage;

public class ColorRequestMessage extends PartyMemberMessage
{
    private String target;
    private String requester; // new field

    public ColorRequestMessage() {}

    public ColorRequestMessage(String target, String requester)
    {
        this.target = target;
        this.requester = requester;
    }

    public String getTarget()
    {
        return target;
    }

    public void setTarget(String target)
    {
        this.target = target;
    }

    public String getRequester()
    {
        return requester;
    }

    public void setRequester(String requester)
    {
        this.requester = requester;
    }
}
