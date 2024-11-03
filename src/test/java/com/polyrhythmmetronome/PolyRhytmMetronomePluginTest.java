package com.polyrhythmmetronome;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class PolyRhytmMetronomePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(PolyRhythmMetronomePlugin.class);
		RuneLite.main(args);
	}
}
