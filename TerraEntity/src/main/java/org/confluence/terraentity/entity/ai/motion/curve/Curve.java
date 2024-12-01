package org.confluence.terraentity.entity.ai.motion.curve;

import net.minecraft.world.phys.Vec3;

public interface Curve {
    Vec3 cal(double t);
}
