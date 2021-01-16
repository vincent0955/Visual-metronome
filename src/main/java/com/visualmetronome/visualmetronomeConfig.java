package com.visualmetronome;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import java.awt.Color;

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
		return false;
	}

	@Alpha
	@ConfigItem(
			position = 2,
			keyName = "tickColor",
			name = "Color used for tick",
			description = "Configures the color of tick"
	)
	default Color getTickColor()
	{
		return Color.WHITE;
	}

	@Alpha
	@ConfigItem(
			position = 3,
			keyName = "tockColor",
			name = "Color used for tock",
			description = "Configures the color of tock"
	)
	default Color getTockColor()
	{
		return Color.GRAY;
	}

	@ConfigItem(
			position = 4,
			keyName = "ResizeOff",
			name = "Enable fixed size",
			description = "Uses fixed size specified below instead of drag resizing. This allows the overlay to have a smaller size than previously possible."
	)
	default boolean ResizeOff()
	{
		return false;
	}

	@ConfigItem(
			position = 5,
			keyName = "boxWidth",
			name = "Configure width (Drag resizing above must be disabled)",
			description = "Sets fixed size for overlay."
	)
	default int boxWidth()
	{
		return 25;
	}
}

