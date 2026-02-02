package org.confluence.terraentity.entity.ai.motion.curve;

import net.minecraft.world.phys.Vec3;

public class Bezier4Curve extends UniformedCurve {
    protected double[][] P;
    public Bezier4Curve(Vec3 start, Vec3 control1, Vec3 control2, Vec3 end){
        P = new double[4][4];
        P[0][0] = start.x;      P[0][1] = start.y;      P[0][2] = start.z;
        P[1][0] = control1.x;   P[1][1] = control1.y;   P[1][2] = control1.z;
        P[2][0] = control2.x;   P[2][1] = control2.y;   P[2][2] = control2.z;
        P[3][0] = end.x;        P[3][1] = end.y;        P[3][2] = end.z;
    }

    public Vec3 cal(double t){
        double oneMinusT = 1 - t;
        double b0 = oneMinusT * oneMinusT * oneMinusT;
        double b1 = 3 * t * oneMinusT * oneMinusT;
        double b2 = 3 * t * t * oneMinusT;
        double b3 = t * t * t;

        double x = b0 * P[0][0] + b1 * P[1][0] + b2 * P[2][0] + b3 * P[3][0];
        double y = b0 * P[0][1] + b1 * P[1][1] + b2 * P[2][1] + b3 * P[3][1];
        double z = b0 * P[0][2] + b1 * P[1][2] + b2 * P[2][2] + b3 * P[3][2];

        return new Vec3(x, y, z);
    }
}
