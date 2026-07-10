package com.visualmetronome.fullresizableoverlay;

import com.visualmetronome.VisualMetronomeConfig;
import com.visualmetronome.VisualMetronomePlugin;
import javax.inject.Inject;
import java.awt.Color;

public class Cycle3VisualMetronomeOverlay extends FullResizableVisualMetronomeOverlay
{
    @Inject
    public Cycle3VisualMetronomeOverlay(VisualMetronomeConfig config, VisualMetronomePlugin plugin)
    {
        super(config, plugin);
    }

    @Override
    protected boolean isVisible()
    {
        return config.enableCycle3() && config.showCycle3Overlay();
    }

    @Override
    protected String getTickText()
    {
        return String.valueOf(plugin.getTickCounter3());
    }

    @Override
    protected Color getTextColor()
    {
        return config.cycle3Color();
    }

    @Override
    protected boolean showBackgroundColor()
    {
        return false;
    }
}
