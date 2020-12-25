package net.runelite.client.plugins.visualmetronome;

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
			name = "Show the title of plugin on the overlay",
			description = "Toggle the display of the title"
	)
	default boolean showTitle()
	{
		return true;
	}

	@ConfigItem(
			position = 2,
			keyName = "tickCount",
			name = "Tick count",
			description = "Configures the tick on which a sound will be played."
	)
	default int tickCount()
	{
		return 1;
	}

	@ConfigItem(
			position = 3,
			keyName = "tickSymbol",
			name = "Symbol used for tick",
			description = "Configures the symbol displayed on each tick"
	)
	default String tickSymbol() { return "⬤";}

	@ConfigItem(
			position = 4,
			keyName = "tockSymbol",
			name = "Symbol used for tock",
			description = "Configures the symbol displayed on each tock"
	)
	default String tockSymbol() { return "⬤"; }

	@Alpha
	@ConfigItem(
			position = 5,
			keyName = "tickColor",
			name = "color used for tick",
			description = "Configures the color displayed on each tick"
	)
	default Color getTickColor() { return Color.GREEN;}

	@Alpha
	@ConfigItem(
			position = 6,
			keyName = "tockColor",
			name = "color used for tock",
			description = "Configures the color displayed on each tock"
	)
	default Color getTockColor() { return Color.RED; }
}

