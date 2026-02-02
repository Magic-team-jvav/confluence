package org.confluence.terraentity.entity.util.trail;

import org.confluence.terraentity.api.entity.trail.ITrail;
import org.confluence.terraentity.entity.proj.BoomerangProjectile;

public class BoomerangTrail implements ITrail<BoomerangProjectile> {
    TrailProperties properties;

    public BoomerangTrail(int size, int colorFrom, int colorTo) {
        properties = new TrailProperties(size, 0.15f, 10, colorFrom, colorTo);
    }

    @Override
    public void generateTrail(BoomerangProjectile entity, int ticks) {
        double f = ticks * 0.8F;
        float w = 0.8f;
        if(!entity.isBacking) {
            entity.trailQueue.add(entity.position().add(Math.sin(f) * w, 0, Math.cos(f) * w));
            entity.trailQueue2.add(entity.position().add(Math.cos(f) * w, 0, Math.sin(f) * w));

        }
        if (entity.trailQueue.size() > 40 || entity.isBacking) {
            entity.trailQueue.poll();
            entity.trailQueue2.poll();
        }

    }

    @Override
    public TrailProperties getTrailProperties() {
        return properties;
    }

}
