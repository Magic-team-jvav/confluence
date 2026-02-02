package org.confluence.terraentity.entity.util.trail;

import org.confluence.terraentity.entity.summon.SummonSword;

public class SummonSwordTrail extends PositionPoseTrail<SummonSword> {


    public SummonSwordTrail(int size, float widthScale, int color) {
        super(size, widthScale, color);
    }

    @Override
    public int getRgb(SummonSword holder, TrailProperties properties) {
        return holder.getRgb();
    }

    @Override
    public void generateTrail(SummonSword holder, int ticks) {
        if(trailsQueue.size() >= 8){
            trailsQueue.poll();
        }

        if(holder.getOwner() != null) {
            trailsQueue.add(new PositionPoseProperties(holder.position(), holder.getXRot(), holder.getYRot()));
        }
    }

}
