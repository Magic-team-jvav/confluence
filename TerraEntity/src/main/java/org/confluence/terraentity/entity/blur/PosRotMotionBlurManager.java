package org.confluence.terraentity.entity.blur;

import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.api.entity.blur.IMotionBlurHolder;

public class PosRotMotionBlurManager extends MotionBlurManager<PosRotMotionBlurContext> {

    public PosRotMotionBlurManager(int maxTrailLength) {
        super(maxTrailLength);
    }

    public void update(IMotionBlurHolder<PosRotMotionBlurContext> entity, Vec3 position, float xRot, float yRot) {
        this.update( entity, new PosRotMotionBlurContext(position, xRot, yRot));
    }

}
