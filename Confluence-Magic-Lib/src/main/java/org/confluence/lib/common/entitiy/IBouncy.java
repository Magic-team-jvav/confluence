package org.confluence.lib.common.entitiy;

import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IBouncy {
    default void bounce(BiConsumer<MoverType, Vec3> mover, Supplier<Vec3> vecGetter, Consumer<Vec3> vecSetter, double gravity, double friction) {
        Vec3 vec3 = vecGetter.get();
        mover.accept(MoverType.SELF, vec3.add(0.0, -gravity, 0.0));
        Vec3 motion = vecGetter.get();
        if (!vec3.equals(motion)) {
            if (motion.x != vec3.x) motion = new Vec3(-vec3.x, vec3.y, vec3.z);
            if (motion.y != vec3.y) motion = new Vec3(vec3.x, -vec3.y, vec3.z);
            if (motion.z != vec3.z) motion = new Vec3(vec3.x, vec3.y, -vec3.z);
        }
        vecSetter.accept(motion.scale(friction).add(0.0, -gravity, 0.0));
    }
}
