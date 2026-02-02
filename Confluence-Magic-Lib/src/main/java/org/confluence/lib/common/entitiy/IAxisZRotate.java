package org.confluence.lib.common.entitiy;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public interface IAxisZRotate {
    default void rotateZ(Rotate rotate, Supplier<Vec3> vecGetter, double gravity, float radius) {
        float velocity = (float) vecGetter.get().length();
        if (velocity > Mth.EPSILON + Mth.EPSILON + gravity) {
            float r = velocity / radius;
            if (rotate.neo > Mth.TWO_PI) rotate.neo -= Mth.TWO_PI;
            rotate.old = rotate.neo;
            rotate.neo += r / Mth.PI;
        } else {
            rotate.old = rotate.neo;
        }
    }

    class Rotate {
        public float neo = 0;
        public float old = 0;

        public boolean different() {
            return neo != old;
        }

        @Override
        public String toString() {
            return "Rotate{" +
                    "neo=" + neo +
                    ", old=" + old +
                    '}';
        }
    }
}
