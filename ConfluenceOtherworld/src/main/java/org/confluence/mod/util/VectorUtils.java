package org.confluence.mod.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

import java.util.List;
import java.util.function.ToDoubleFunction;

public final class VectorUtils {
    /**
     * 最简单的追踪机制，计算过程如下：<br>
     * 1. 将当前的弹幕方向进行缩放 <br>
     * 2. 将目标方向（即追踪弹幕想要去的方向）以一定权重加入缩放后的弹幕方向 <br>
     * 3. 调整最终方向向量长度返回 <br>
     * 优点：计算量少，逻辑简单 <br>
     * 缺点：在allowLowerSpd为false  且  移动方向与想要弹幕追踪到的方向相反时，追踪能力极为有限；
     * interpolateBasis方法以更多的计算量为代价提供更加平滑和可调整的追踪弹道。
     *
     * @param currDir            当前的弹幕方向向量
     * @param targetDir          弹幕追踪目标方向向量
     * @param currDirScaleFactor 当前弹幕方向计算前的缩放比例
     * @param homingPower        弹幕追踪时根据目标方向调整的量
     * @param maxSpeed           弹幕追踪时最大速度; 取值范围 - [0, inf)
     * @param minSpeed           弹幕追踪时最低速度; 取值范围 - [0, maxSpeed]
     * @param defaultDir         若更新完毕的弹幕方向为0，但最低速度要求不为0时，返回defaultDir方向（长度会更新为minSpeed）。
     *                           *警告*：此向量不要为0
     * @return 最终更新完毕的方向向量
     */
    public static Vec3 interpolateSimple(Vec3 currDir, Vec3 targetDir, double currDirScaleFactor, double homingPower,
                                         double maxSpeed, double minSpeed, Vec3 defaultDir) {
        Vec3 result = currDir;
        // 若当前方向和目标方向的方向向量均不为0时才考虑进行方向调整
        // 为了方向的准确性，我们将不考虑长度 < 0.001 (即lengthSqr < 1e-9) 的向量
        if (currDir.lengthSqr() > 1e-9 && targetDir.lengthSqr() > 1e-9) {
            result = currDir.multiply(currDirScaleFactor, currDirScaleFactor, currDirScaleFactor);
            // normalize不会造成NaN，此处长度 > 0.001
            Vec3 targetComponent = targetDir.normalize().multiply(homingPower, homingPower, homingPower);
            result = result.add(targetComponent);
        }
        // 最后，根据最大最小速度的要求更改向量长度
        double vecLen = result.length();
        if (vecLen > maxSpeed) {
            double factor = maxSpeed / vecLen;
            // 不使用normalize以减少一次计算向量长度带来的sqrt运算
            result = result.multiply(factor, factor, factor);
        } else if (vecLen < minSpeed) {
            // 若结果向量过短，使用defaultDir
            if (vecLen < 1e-5) {
                result = defaultDir;
                vecLen = result.length();
            }
            double factor = minSpeed / vecLen;
            // 不使用normalize以减少一次计算向量长度带来的sqrt运算
            result = result.multiply(factor, factor, factor);
        }
        return result;
    }

    /**
     * 将currDir方向的单位向量记为v1, 我们使用向量投影的方式构造单位向量v2，使得v1与v2为currDir, targetDir平面上的基，且v1垂直于v2. <br>
     * 将currDir的长度写为c，则currDir=cv1+0v2；另，有a,b使得targetDir可被写成av1+bv2. <br>
     * 注意到，[c,0]转换到[a,b]需要一个旋转+缩放；同样的，对于基{v1,v2}中，这一系数的变换也将currDir变换到targetDir. <br>
     * 然而，一般而言，我们希望追踪弹幕每次只进行这一变换的一部分以获得更合理的弹道。 <br>
     * 因此，我们对旋转角和缩放长度进行插值。至此，我们即可获得最终的方向向量。 <br>
     * 注：若我们把v1看做x轴，v2看做y轴，则角度插值和角度均应在一二象限。 <br>
     * 另，从以上过程中可知，**targetDir的方向和长度都至关重要**。<br>
     *
     * @param currDir            当前弹幕的方向向量
     * @param targetDir          方向向量，记录了追踪的最终方向与长度（想要达到的速度）
     * @param angleInterpolator      提供角度插值；输入为当前方向和追踪方向的角度差，输出为追踪所变换的角度
     * @param lengthInterpolator 提供向量长度（即速度）插值；输入为当前方向和追踪方向的长度差，输出为追踪所变换的向量长度
     * @return 变换完毕的向量
     */
    public static Vec3 interpolateBasis(Vec3 currDir, Vec3 targetDir,
                                        ToDoubleFunction<Double> angleInterpolator, ToDoubleFunction<Double> lengthInterpolator) {
        double currDirLen = currDir.length();
        double targetDirLen = targetDir.length();
        // 以下多次用到仅单次使用的乘数的设计，干脆公用同一个变量
        double multi;
        // 起始向量与目标向量均为0，直接返回原向量
        if (currDirLen < 1e-5 && targetDirLen < 1e-5) {
            return currDir;
        }
        // 若仅有起始速度为0，则直接返回0向量到目标向量的插值
        if (currDir.lengthSqr() < 1e-9) {
            multi = lengthInterpolator.applyAsDouble(targetDirLen) / targetDirLen;
            return targetDir.multiply(multi, multi, multi);
        }
        // 若仅有终止速度为0，则直接返回起始向量到0向量的线性插值，即起始向量*(1-进度)。
        if (targetDir.lengthSqr() < 1e-9) {
            multi = 1 - (lengthInterpolator.applyAsDouble(currDirLen) / currDirLen);
            return currDir.multiply(multi, multi, multi);
        }
        // 此时，起始终止速度均不为0，后续操作不会造成NaN值。按照注释中的步骤获得结果。
        multi = 1 / currDirLen;
        Vec3 v1 = currDir.multiply(multi, multi, multi);
        Vec3 v1Component = vectorProjection(targetDir, v1);
        Vec3 v2 = targetDir.subtract(v1Component);
        double a, b; // 此处的a,b见上方的方法注释中说明
        double v1CompLen = v1Component.length();
        double v2Len = v2.length();
        // 夹角大于pi/2时，即cos(theta)<0或v1·v1Component<0时，a是负数
        a = v1CompLen * Math.signum(v1.dot(v1Component));
        // 此处的v2方向正确，但尚未转化为单位向量；若v2近似地为0, 即v1与v2共线。
        if (v2Len < 1e-5) {
            b = 0;
        } else {
            b = v2Len;
            multi = 1 / v2Len;
            v2 = v2.multiply(multi, multi, multi);
        }
        // targetDir = [a,b]·[v1,v2]; angleRad = angle([1,0], [a,b]) = atan2(b,a)
        double angleRad = Math.atan2(b, a);
        // 计算角度插值
        double angleDelta = angleInterpolator.applyAsDouble(angleRad);
        // 获得旋转后的方向；此时方向向量为单位向量。
        multi = Math.cos(angleDelta);
        Vec3 result = v1.multiply(multi, multi, multi);
        multi = Math.sin(angleDelta);
        result = result.add(v2.multiply(multi, multi, multi));
        // 计算长度插值
        double length = currDirLen + lengthInterpolator.applyAsDouble(targetDirLen - currDirLen);
        return result.multiply(length, length, length);
    }

    /**
     * 返回一个可以被interpolateBasis作为angleInterpolator或lengthInterpolator使用的线性插值。<br>
     * 即，若progress为0，则插值一定提供0，在追踪中表现为不追踪；<br>
     * 若progress为1，则插值一定提供全额变化值，在追踪中表现为瞬间完全调整方向。<br>
     * 例：progress为0.5，则插值一定提供变化值的一半，在追踪中表现为方向（弧度）/速度 *误差越大，调整速度越快*。<br>
     *
     * @param progress 插值强度；越接近0越弱，越接近1越强。取值范围 - [0, 1]
     * @return 插值ToDoubleFunction
     */
    public static ToDoubleFunction<Double> getLerp(double progress) {
        return x -> x * progress;
    }

    /**
     * 返回一个可以被interpolateBasis作为angleInterpolator或lengthInterpolator使用的阈值式插值。<br>
     * 即，若progress为0，则插值一定提供0，在追踪中表现为不追踪；<br>
     * 否则，插值提供 变化值 与 阈值 中更小的一者，在追踪中表现为方向（弧度）/速度的误差以 *恒定的效率* 被修正。<br>
     * **再次注意：方向（弧度）的插值单位为弧度而非角度！**<br>
     *
     * @param efficiency 插值强度；越接近0越弱，越高越强。取值范围 - [0, inf)
     * @return 插值ToDoubleFunction
     */
    public static ToDoubleFunction<Double> getThresholdInterpolator(double efficiency) {
        return x -> Math.min(x, efficiency);
    }

    /**
     * 向量投影；**toProjectOnto不可以为0向量**！
     *
     * @param vector        被投影的向量
     * @param toProjectOnto 投影的目标向量
     * @return 投影结果
     */
    public static Vec3 vectorProjection(Vec3 vector, Vec3 toProjectOnto) {
        // toProjectOnto.lengthSqr = toProjectOnto.dot(toProjectOnto)
        double factor = toProjectOnto.dot(vector) / toProjectOnto.lengthSqr();
        return toProjectOnto.multiply(factor, factor, factor);
    }

    /**
     * 把向量转成角度
     *
     * @return [yaw, pitch]
     */
    public static float[] dirToRot(Vec3 vec, boolean toDeg) {
        double x = vec.x;
        double y = vec.y;
        double z = vec.z;
        double h = vec.horizontalDistance();
        float yaw = (float) Mth.atan2(-x, z);
        float pitch = (float) Mth.atan2(-y, h);
        if (toDeg) {
            return new float[]{yaw * Mth.RAD_TO_DEG, pitch * Mth.RAD_TO_DEG};
        }
        return new float[]{yaw, pitch};
    }

    /**
     * 获得从实体A到实体B的单位向量，即A→B
     *
     * @param a 实体A
     * @param b 实体B
     * @return A→B的单位向量
     */
    public static Vec3 getVectorA2B(Entity a, Entity b) {
        return b.position().subtract(a.position()).normalize();
    }

    /**
     * 给予实体B一个击退动量，方向为A→B
     *
     * @param a       实体A
     * @param b       实体B
     * @param scale   击退动量的缩放
     * @param motionY 击退的Y轴动量
     */
    public static void knockBackA2B(Entity a, Entity b, double scale, double motionY) {
        if (b instanceof LivingEntity living) {
            AttributeInstance instance = living.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
            if (instance != null) scale *= (1.0 - instance.getValue());
        }
        if (scale > 0.0) {
            if (a instanceof LivingEntity living) {
                AttributeInstance instance = living.getAttribute(Attributes.ATTACK_KNOCKBACK);
                if (instance != null) scale *= (1.0 + instance.getValue());
            }
            b.addDeltaMovement(getVectorA2B(a, b).scale(scale).add(0.0, motionY, 0.0));
        }
    }

    public static Direction[] directionsInAxis(Direction.Axis axis) {
        return switch (axis) {
            case X -> new Direction[]{Direction.EAST, Direction.WEST};
            case Y -> new Direction[]{Direction.UP, Direction.DOWN};
            default -> new Direction[]{Direction.SOUTH, Direction.NORTH};
        };
    }

    /**
     * 将输入的向量的某个轴乘一个缩放
     *
     * @param vec3  输入的向量
     * @param axis  某个轴
     * @param scale 缩放
     * @return 新向量
     */
    public static Vec3 relativeScale(Vec3 vec3, Direction.Axis axis, double scale) {
        double x = axis == Direction.Axis.X ? scale * vec3.x : vec3.x;
        double y = axis == Direction.Axis.Y ? scale * vec3.y : vec3.y;
        double z = axis == Direction.Axis.Z ? scale * vec3.z : vec3.z;
        return new Vec3(x, y, z);
    }

    public static Vector3d toVector3d(BlockPos blockPos) {
        Vec3 center = blockPos.getCenter();
        return new Vector3d(center.x, center.y, center.z);
    }

    public static BlockPos fromVector3d(Vector3d vector3d) {
        return new BlockPos(Mth.floor(vector3d.x), Mth.floor(vector3d.y), Mth.floor(vector3d.z));
    }

    public static void lightningPathList(List<Vector3d> locationList, double dist, int move, RandomSource random) {
        double distSqr = dist * dist;
        boolean refined;
        do {
            refined = false;
            for (int i = 0; i < locationList.size() - 1; i++) {
                Vector3d point1 = locationList.get(i);
                Vector3d point2 = locationList.get(i + 1);
                double distanceSqr = point2.distanceSquared(point1);
                if (distanceSqr > distSqr) {
                    Vector3d midpoint = new Vector3d();
                    point1.add(point2, midpoint).mul(0.5);
                    double offset = Math.sqrt(distanceSqr) / move;
                    double twoOffset = offset * 2;
                    midpoint.x = midpoint.x + (random.nextDouble() - 0.5) * twoOffset;
                    midpoint.y = midpoint.y + (random.nextDouble() - 0.5) * twoOffset;
                    midpoint.z = midpoint.z + (random.nextDouble() - 0.5) * twoOffset;
                    locationList.add(i + 1, midpoint);
                    refined = true;
                }
            }
        } while (refined);
    }
}
