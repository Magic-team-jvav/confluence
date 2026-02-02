package nowebsite.makertechno.the_trackers.core.track.states;

import net.minecraft.world.entity.Entity;
import nowebsite.makertechno.the_trackers.client.gui.cursors.TRenderCursor;

public class TickLimitedTrackerState extends ControllableTrackerState{
    private final int maxTicked;

    private int ticking = 0;
    public TickLimitedTrackerState(String identifyName, TRenderCursor component, int maxTicked, boolean isVisible) {
        super(identifyName, component, isVisible, false);
        this.maxTicked = maxTicked;
    }

    @Override
    public void setPosEntity(Entity entity) {
        super.setPosEntity(entity);
        if (ticking < maxTicked) ticking++;
        else this.close();
    }
}
