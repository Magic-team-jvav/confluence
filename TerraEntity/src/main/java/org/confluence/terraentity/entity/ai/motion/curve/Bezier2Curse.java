package org.confluence.terraentity.entity.ai.motion.curve;

import net.minecraft.world.phys.Vec3;

public class Bezier2Curse implements Curve {
    protected double[][] P;
    public Bezier2Curse(Vec3 start, Vec3 height, Vec3 end){
        P = new double[3][3];
        P[0][0] = start.x;  P[0][1] = start.y;  P[0][2] = start.z;
        P[1][0] = height.x; P[1][1] = height.y; P[1][2] = height.z;
        P[2][0] = end.x;    P[2][1] = end.y;    P[2][2] = end.z;
    }

    public Vec3 cal(double t){
        double oneMinusT = 1 - t;
        double b0 = oneMinusT * oneMinusT;  // (1-t)^2
        double b1 = 2 * oneMinusT * t;      // 2(1-t)t
        double b2 = t * t;                  // t^2

        double x = b0 * P[0][0] + b1 * P[1][0] + b2 * P[2][0];
        double y = b0 * P[0][1] + b1 * P[1][1] + b2 * P[2][1];
        double z = b0 * P[0][2] + b1 * P[1][2] + b2 * P[2][2];

        return new Vec3(x, y, z);
    }

}
