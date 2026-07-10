package com.visualmetronome.fullresizableoverlay;

import com.visualmetronome.VisualMetronomeConfig;
import com.visualmetronome.VisualMetronomePlugin;
import javax.inject.Inject;
import java.awt.Color;

public class DefaultCycleVisualMetronomeOverlay extends FullResizableVisualMetronomeOverlay
{
    @Inject
    public DefaultCycleVisualMetronomeOverlay(VisualMetronomeConfig config, VisualMetronomePlugin plugin)
    {
        super(config, plugin);
    }

    @Override
    protected boolean isVisible()
    {
        return config.enableMetronome();
    }

    @Override
    protected String getTickText()
    {
        return config.tickCount() == 1
            ? String.valueOf(plugin.getCurrentColorIndex())
            : String.valueOf(plugin.getTickCounter());
    }

    @Override
    protected Color getTextColor()
    {
        return config.NumberColor();
    }

    @Override
    protected boolean showText()
    {
        return config.showTick();
    }
}
