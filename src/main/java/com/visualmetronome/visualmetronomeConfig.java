package com.visualmetronome;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("visualmetronome")
public interface visualmetronomeConfig extends Config
{
	@ConfigItem(
			position = 1,
			keyName = "showTitle",
			name = "Show title",
			description = "Toggle display of the title on overlay"
	)
	default boolean showTitle()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
			position = 2,
			keyName = "tickColor",
			name = "Color used for tick",
			description = "Configures the color of tick"
	)
	default Color getTickColor() { return Color.WHITE; }

	@Alpha
	@ConfigItem(
			position = 3,
			keyName = "tockColor",
			name = "Color used for tock",
			description = "Configures the color of tock"
	)
	default Color getTockColor() { return Color.GRAY; }

	@ConfigItem(
			position = 4,
			keyName = "boxWidth",
			name = "Box width",
			description = "Configure width of overlay"
	)
	default int boxWidth() { return 70; }

}

