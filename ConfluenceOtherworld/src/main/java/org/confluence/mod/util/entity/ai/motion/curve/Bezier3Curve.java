package org.confluence.mod.util.entity.ai.motion.curve;

import net.minecraft.world.phys.Vec3;

public class Bezier3Curve extends UniformedCurve {
    protected double[][] P;

    public Bezier3Curve(Vec3 start, Vec3 control, Vec3 end) {
        P = new double[3][3];
        P[0][0] = start.x;  P[0][1] = start.y;  P[0][2] = start.z;
        P[1][0] = control.x; P[1][1] = control.y; P[1][2] = control.z;
        P[2][0] = end.x;    P[2][1] = end.y;    P[2][2] = end.z;
        precomputeArcLengths();
    }

    // 根据参数t计算曲线上的点（原始贝塞尔公式）
    @Override
    public Vec3 cal(double t) {
        double oneMinusT = 1 - t;
        double b0 = oneMinusT * oneMinusT;
        double b1 = 2 * oneMinusT * t;
        double b2 = t * t;
        double x = b0 * P[0][0] + b1 * P[1][0] + b2 * P[2][0];
        double y = b0 * P[0][1] + b1 * P[1][1] + b2 * P[2][1];
        double z = b0 * P[0][2] + b1 * P[1][2] + b2 * P[2][2];
        return new Vec3(x, y, z);
    }

}
