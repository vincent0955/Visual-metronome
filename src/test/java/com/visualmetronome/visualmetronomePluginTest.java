package com.visualmetronome;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class visualmetronomePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(visualmetronomePlugin.class);
		RuneLite.main(args);
	}
}
