package com.visualmetronome;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;
import java.awt.Color;

@ConfigGroup("visualmetronome")
public interface visualmetronomeConfig extends Config
{
	@Range(
			min = 2,
			max = 4
	)
	@ConfigItem(
			position = 1,
			keyName = "colorCycle",
			name = "Number of Colors",
			description = "The number of colors it cycles through (2 for default)"
	)
	default int colorCycle()
	{
		return 2;
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

	@Alpha
	@ConfigItem(
			position = 4,
			keyName = "tick3Color",
			name = "Color used for 3rd tick",
			description = "Configures the color of 3rd tick if enabled"
	)
	default Color getTick3Color()
	{
		return Color.DARK_GRAY;
	}
	@Alpha
	@ConfigItem(
			position = 5,
			keyName = "tick4Color",
			name = "Color used for 4th tick",
			description = "Configures the color of the 4th tick if enabled"
	)
	default Color getTick4Color()
	{
		return Color.BLACK;
	}

	@Range(
			min = 1
	)
	@ConfigItem(
			position = 6,
			keyName = "tickCount",
			name = "Tick Count",
			description = "The tick on which the color changes (Only supports two colors)"
	)
	default int tickCount()
	{
		return 1;
	}

	@ConfigItem(
			position = 7,
			keyName = "ResizeOff",
			name = "Enable fixed size",
			description = "Uses fixed size specified below instead of drag resizing. This allows the overlay to have a smaller size than previously possible."
	)
	default boolean ResizeOff()
	{
		return false;
	}

	@ConfigItem(
			position = 8,
			keyName = "boxWidth",
			name = "Configure width (fixed sizing above must be enabled)",
			description = "Sets fixed size for overlay."
	)
	default int boxWidth()
	{
		return 25;
	}

	@ConfigItem(
			position = 9,
			keyName = "showTitle",
			name = "Show title",
			description = "Toggle display of the title on overlay"
	)
	default boolean showTitle()
	{
		return false;
	}

}

