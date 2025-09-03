package com.visualmetronome;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;
import net.runelite.client.config.ConfigSection;

import java.awt.Color;

@ConfigGroup("visualmetronomePruned")
public interface VisualMetronomePrunedConfig extends Config
{
    @ConfigItem(
            position = 1,
            keyName = "enableMetronome",
            name = "Visual Metronome Pruned",
            description = "Enable visual metronome"
    )
    default boolean enableMetronome()
    {
        return true;
    }

    @ConfigItem(
            position = 2,
            keyName = "highlightCurrentTile",
            name = "Enable True Tile Overlay",
            description = "Highlights true player tile using the metronome colors (replacement for tile indicator plugin setting)"
    )
    default boolean highlightCurrentTile()
    {
        return false;
    }


    @Range(
            min = 16
    )
    @ConfigItem(
            position = 3,
            keyName = "boxWidth",
            name = "Default Box Size (Alt + Right Click Box)",
            description = "Configure the default length and width of the box. Use alt + right click on the box to reset to the size specified"
    )
    default int boxWidth()
    {
        return 25;
    }

    @Range(
            min = 1
    )
    @ConfigItem(
            position = 4,
            keyName = "tickCount",
            name = "Tick Count",
            description = "The tick on which the color changes"
    )
    default int tickCount()
    {
        return 1;
    }

    @ConfigSection(
            name = "Tick Number Settings",
            description = "Change Tick Number settings",
            position = 5
    )
    String TickNumberSettings = "Tick Number Settings";

    @ConfigItem(
            position = 1,
            keyName = "showTick",
            name = "Show Metronome Tick Number",
            description = "Shows current tick number on the metronome",
            section = TickNumberSettings
    )
    default boolean showTick()
    {
        return false;
    }

    @ConfigItem(
            position = 2,
            keyName = "showPlayerTick",
            name = "Show Tick Number Above Player",
            description = "Shows current tick number above the player",
            section = TickNumberSettings
    )
    default boolean showPlayerTick()
    {
        return false;
    }

    @ConfigItem(
            position = 3,
            keyName = "disableFontScaling",
            name = "Disable Font Size Scaling (Metronome Tick Only)",
            description = "Disables font size scaling for metronome tick number",
            section = TickNumberSettings
    )
    default boolean disableFontScaling()
    {
        return false;
    }

    @Range(
            min = 8,
            max = 50
    )
    @ConfigItem(
            position = 4,
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
            position = 5,
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
            position = 6,
            keyName = "fontType",
            name = "Font Type",
            description = "Change the font of the Tick Number",
            section = TickNumberSettings
    )
    default FontTypes fontType() { return FontTypes.REGULAR; }

    @ConfigSection(
            name = "True Tile Overlay Settings",
            description = "Settings only applied to True Tile Overlay",
            position = 6
    )
    String TileSettings = "True Tile Overlay Settings";

    @Alpha
    @ConfigItem(
            position = 1,
            keyName = "currentTileFillColor",
            name = "True Tile Fill Color",
            description = "Fill color of the true tile overlay",
            section = TileSettings
    )
    default Color currentTileFillColor()
    {
        return new Color(0, 0, 0, 50);
    }

    @ConfigItem(
            position = 2,
            keyName = "currentTileBorderWidth",
            name = "True Tile Border Width",
            description = "Border size of the true tile overlay",
            section = TileSettings
    )
    default double currentTileBorderWidth()
    {
        return 2;
    }

    @ConfigItem(
            position = 3,
            keyName = "changeFillColor",
            name = "Enable Tile Fill Color Metronome",
            description = "Makes the tile fill color change with the metronome",
            section = TileSettings
    )
    default boolean changeFillColor()
    {
        return false;
    }

    @Range(
            min = 0,
            max = 255
    )
    @ConfigItem(
            position = 4,
            keyName = "changeFillColorOpacity",
            name = "Fill Color Metronome Opacity",
            description = "Opacity of the tile fill metronome color if the option above is enabled. Otherwise, the opacity is determined by the True Tile Fill Color setting",
            section = TileSettings
    )
    default int changeFillColorOpacity()
    {
        return 50;
    }


    @ConfigSection(
            name = "Color Settings",
            description = "Change the colors and number of colors to cycle through",
            position = 8
    )
    String ColorSettings = "Color Settings";

    @Alpha
    @ConfigItem(
            position = 2,
            keyName = "tickColor",
            name = "Tick Color",
            description = "Configures the color of tick",
            section = ColorSettings
    )
    default Color getTickColor()
    {
        return Color.WHITE;
    }

    @Alpha
    @ConfigItem(
            position = 3,
            keyName = "tockColor",
            name = "Tock Color",
            description = "Configures the color of tock",
            section = ColorSettings
    )
    default Color getTockColor()
    {
        return Color.GRAY;
    }

}

