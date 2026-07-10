package com.visualmetronome.fullresizableoverlay;

import com.visualmetronome.VisualMetronomeConfig;
import com.visualmetronome.VisualMetronomePlugin;
import javax.inject.Inject;
import java.awt.Color;

public class Cycle2VisualMetronomeOverlay extends FullResizableVisualMetronomeOverlay
{
    @Inject
    public Cycle2VisualMetronomeOverlay(VisualMetronomeConfig config, VisualMetronomePlugin plugin)
    {
        super(config, plugin);
    }

    @Override
    protected boolean isVisible()
    {
        return config.enableCycle2() && config.showCycle2Overlay();
    }

    @Override
    protected String getTickText()
    {
        return String.valueOf(plugin.getTickCounter2());
    }

    @Override
    protected Color getTextColor()
    {
        return config.cycle2Color();
    }

    @Override
    protected boolean showBackgroundColor()
    {
        return false;
    }
}
