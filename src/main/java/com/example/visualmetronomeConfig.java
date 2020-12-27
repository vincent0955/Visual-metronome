package net.runelite.client.plugins.visualmetronome;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.ui.overlay.components.ComponentConstants;

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
		return false;
	}

	@ConfigItem(
			position = 2,
			keyName = "tickSymbol",
			name = "Text shown for tick",
			description = "Configures the symbol displayed on each tick"
	)
	default String tickSymbol() { return "⬤";}

	@ConfigItem(
			position = 3,
			keyName = "tockSymbol",
			name = "Text shown for tock",
			description = "Configures the symbol displayed on each tock"
	)
	default String tockSymbol() { return "⬤"; }

	@ConfigItem(
			position = 4,
			keyName = "tickColor",
			name = "Color used for tick",
			description = "Configures the color of tick symbol"
	)
	default Color getTickColor() { return Color.WHITE; }

	@ConfigItem(
			position = 5,
			keyName = "tockColor",
			name = "Color used for tock",
			description = "Configures the color of tock symbol"
	)
	default Color getTockColor() { return Color.BLACK; }

	@ConfigItem(
			position = 6,
			keyName = "toggleBackground",
			name = "Toggle transparent background",
			description = "Toggles the transparency of background of the overlay"
	)
	default boolean toggleBackground() { return true; }

	@Alpha
	@ConfigItem(
			position = 7,
			keyName = "backgroundColor",
			name = "Background color/opacity",
			description = "Select overlay background color and opacity"
	)
	default Color backgroundColor() { return ComponentConstants.STANDARD_BACKGROUND_COLOR; }

}

