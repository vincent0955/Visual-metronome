package com.visualmetronome;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Keybind;
import java.awt.Color;

@ConfigGroup("visualmetronome")
public interface VisualMetronomeConfig extends Config
{
	@Range(
			min = 1,
			max = 5
	)
	@ConfigItem(
			position = 1,
			keyName = "metronomeCount",
			name = "Metronome Count",
			description = "The number of cycle lengths to track"
	)
	default int metronomeCount()
	{
		return 1;
	}

	@Range(
			min = 1
	)
	@ConfigItem(
			position = 2,
			keyName = "tickCount",
			name = "Main Cycle Length",
			description = "Tick length for main cycle"
	)
	default int tickCount()
	{
		return 1;
	}

	@Range(
			min = 1
	)
	@ConfigItem(
			position = 3,
			keyName = "tickCount2",
			name = "Cycle 2 Length",
			description = "Tick length for cycle 2"
	)
	default int tickCount2()
	{
		return 1;
	}

	@Range(
			min = 1
	)
	@ConfigItem(
			position = 4,
			keyName = "tickCount3",
			name = "Cycle 3 Length",
			description = "Tick length for cycle 3"
	)
	default int tickCount3()
	{
		return 1;
	}

	@Range(
			min = 1
	)
	@ConfigItem(
			position = 5,
			keyName = "tickCount4",
			name = "Cycle 4 Length",
			description = "Tick length for cycle 4"
	)
	default int tickCount4()
	{
		return 1;
	}

	@Range(
			min = 1
	)
	@ConfigItem(
			position = 6,
			keyName = "tickCount5",
			name = "Cycle 5 Length",
			description = "Tick length for cycle 5"
	)
	default int tickCount5()
	{
		return 1;
	}

	@ConfigSection(
			name = "Tick Number Settings",
			description = "Change Tick Number settings",
			position = 7
	)
	String TickNumberSettings = "Tick Number Settings";


	@Range(
			min = 8,
			max = 50
	)
	@ConfigItem(
			position = 8,
			keyName = "fontSize",
			name = "Font Size (Overhead Tick Only)",
			description = "Change the font size of the overhead Tick Number",
			section = TickNumberSettings
	)
	default int fontSize()
	{
		return 15;
	}

	@ConfigItem(
			position = 9,
			keyName = "countColor",
			name = "Tick Number Color",
			description = "Configures the color of tick number",
			section = TickNumberSettings
	)
	default Color NumberColor()
	{
		return Color.CYAN;
	}

	@ConfigItem(
			position = 10,
			keyName = "fontType",
			name = "Font Type",
			description = "Change the font of the Tick Number",
			section = TickNumberSettings
	)
	default FontTypes fontType() { return FontTypes.REGULAR; }


	@ConfigSection(
			name = "Hotkey Settings",
			description = "Settings that use hotkeys",
			position = 11
	)
	String HotkeySettings = "Hotkey Settings";

	@ConfigItem(
			position = 12,
			keyName = "tickResetHotkey",
			name = "Tick Cycle Reset Hotkey",
			description = "Hotkey to reset the tick cycle back to 0",
			section = HotkeySettings
	)
	default Keybind tickResetHotkey() {
		return Keybind.NOT_SET;
	}

}

