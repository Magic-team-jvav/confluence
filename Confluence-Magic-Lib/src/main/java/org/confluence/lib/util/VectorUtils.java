package org.confluence.lib.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector3i;

import java.util.*;
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
     * @param angleInterpolator  提供角度插值；输入为当前方向和追踪方向的角度差，输出为追踪所变换的角度
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
        double sqr = toProjectOnto.lengthSqr();
        if (sqr == 0.0) throw new IllegalArgumentException("Length of toProjectOnto could not be zero");
        return toProjectOnto.scale(toProjectOnto.dot(vector) / sqr);
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
            LivingEntity living = null;
            if (a instanceof TraceableEntity traceable && traceable.getOwner() instanceof LivingEntity living1) living = living1;
            else if (a instanceof LivingEntity living1) living = living1;
            if (living != null) {
                AttributeInstance instance = living.getAttribute(Attributes.ATTACK_KNOCKBACK);
                if (instance != null) scale *= (1.0 + instance.getValue());
            }
            b.addDeltaMovement(getVectorA2B(a, b).scale(scale).add(0.0, motionY, 0.0));
        }
    }

    /**
     * 给予实体一个击退动量，方向为vector
     *
     * @param attacker 击退者
     * @param victim   被击退者
     * @param vector   向量
     */
    public static void knockBack(LivingEntity attacker, Entity victim, Vec3 vector) {
        double scale = 1.0;
        if (victim instanceof LivingEntity living) {
            AttributeInstance instance = living.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
            if (instance != null) scale *= (1.0 - instance.getValue());
        }
        if (scale > 0.0) {
            LivingEntity living;
            if (attacker instanceof TraceableEntity traceable && traceable.getOwner() instanceof LivingEntity living1) living = living1;
            else living = attacker;
            AttributeInstance instance = living.getAttribute(Attributes.ATTACK_KNOCKBACK);
            if (instance != null) scale *= (1.0 + instance.getValue());
            victim.addDeltaMovement(vector.scale(scale));
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

    public static void lightningPathList(List<Vector3d> locationList, double dist, float move, RandomSource random) {
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
                    double offset = Math.sqrt(distanceSqr) * move;
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

    public static List<List<Vector3d>> lightningPathList(List<Vector3d> initialLocationList, double dist, float move, RandomSource random, int layer, float branchPercent) {
        List<List<Vector3d>> listOfLightning = new ArrayList<>();

        List<List<Vector3d>> currentLayerPaths = new ArrayList<>();
        currentLayerPaths.add(new ArrayList<>(initialLocationList));

        for (int i = 0; i < layer; i++) {
            List<List<Vector3d>> nextLayerPaths = new ArrayList<>();

            for (List<Vector3d> path : currentLayerPaths) {
                List<Vector3d> refinedPath = new ArrayList<>(path);
                lightningPathList(refinedPath, dist, move, random);
                listOfLightning.add(refinedPath);

                if (refinedPath.size() < 2) continue;

                Vector3d vctBefore = refinedPath.getFirst();
                Vector3d lastVct = refinedPath.getLast();

                for (int j = 1; j < refinedPath.size(); j++) {
                    Vector3d vct = refinedPath.get(j);

                    double percent = 0.02 * (1 - ((double) j / refinedPath.size()));
                    if (random.nextDouble() < percent) {
                        Vector3d branchDirection = new Vector3d(vct).sub(vctBefore).normalize().mul(vctBefore.distance(lastVct) * (branchPercent + random.nextDouble() * 0.1));

                        Vector3d randomOffset = new Vector3d(random.nextDouble() - 0.5, random.nextDouble() - 0.5, random.nextDouble() - 0.5)
                                .normalize().mul(branchDirection.length() * 0.5);

                        Vector3d branchEnd = new Vector3d(vctBefore).add(branchDirection).add(randomOffset);

                        List<Vector3d> newBranch = new ArrayList<>();
                        newBranch.add(new Vector3d(vctBefore));
                        newBranch.add(branchEnd);
                        nextLayerPaths.add(newBranch);
                    }
                    vctBefore = vct;
                }
            }
            currentLayerPaths = nextLayerPaths;
            if (currentLayerPaths.isEmpty()) {
                break;
            }
        }

        return listOfLightning;
    }


    public static Map<Vector3d, BooleanStorage4> mazePos(Vector3d centerPos, double distance, int layer, WorldgenRandom random, float difficulty) {
        Map<Vector3i, BooleanStorage4> nowMap = new HashMap<>();
        Map<Vector3i, BooleanStorage4> thanMap = new HashMap<>();
        Map<Vector3i, BooleanStorage4> setMap = new HashMap<>();
        Map<Vector3d, BooleanStorage4> outMap = new HashMap<>();
        int x;
        int z;
        double dX;
        double dZ;
        int xOffset;
        int zOffset;
        int layer2 = layer * 2;
        Vector3i key;
        Vector3i thanKey;
        thanMap.put(new Vector3i(), new BooleanStorage4());
        int maxCount = (layer2 + 1) * (layer2 + 1);
        while (setMap.size() < maxCount) {
            nowMap.clear();
            nowMap.putAll(thanMap);
            nowMap.putAll(setMap);
            thanMap.clear();
            for (Map.Entry<Vector3i, BooleanStorage4> entry : nowMap.entrySet()) {
                key = entry.getKey();
                x = key.x;
                z = key.z;
                BooleanStorage4 a = entry.getValue().copy();
                BooleanStorage4 b = a.copy();
                for (int i = 0; i < 4; i++) {
                    xOffset = (int) Mth.cos(i * Mth.HALF_PI) + x;
                    zOffset = (int) Mth.sin(i * Mth.HALF_PI) + z;
                    thanKey = new Vector3i(xOffset, 0, zOffset);
                    b.set(i, true);
                    if (((1.0F - 0.5F * difficulty) > random.nextFloat()) && (xOffset <= layer) && (xOffset >= -layer) && (zOffset <= layer) && (zOffset >= -layer) && !setMap.containsKey(thanKey) && !nowMap.containsKey(thanKey) && !thanMap.containsKey(thanKey)) {
                        a.set(i, true);
                        BooleanStorage4 thenList = new BooleanStorage4();
                        thenList.set((i + 2) % 4, true);
                        thanMap.put(thanKey, thenList);
                    }
                }
                setMap.put(key, a);
            }
        }
        for (Map.Entry<Vector3i, BooleanStorage4> entry : setMap.entrySet()) {
            key = entry.getKey();
            x = key.x;
            z = key.z;
            dX = x * distance + centerPos.x;
            dZ = z * distance + centerPos.z;
            BooleanStorage4 outList = entry.getValue().copy();
            outMap.put(new Vector3d(dX, centerPos.y, dZ), outList);
        }
        return outMap;
    }

    public static Integer listRandom(BooleanStorage4 list, WorldgenRandom random) {
        boolean aBoolean = true;
        int listW = 0;
        for (int i = 0; (i < 100) && aBoolean; i++) {
            listW = random.nextInt(list.size());
            aBoolean = list.get(listW);
        }
        return listW;
    }

    public static void list8(List<Vector3d> list, BlockPos centerPos, int x, int y, int z, WorldgenRandom random) {
        for (int i = 0; i < 8; i++) {
            if (0.125F >= random.nextFloat()) {
                list.add(new Vector3d(centerPos.getX() + (x * ((i < 4) ? 1 : -1)), centerPos.getY() + (y * ((i % 4 < 2) ? 1 : -1)), centerPos.getZ() + (z * ((i % 2 < 1) ? 1 : -1))));
            }
        }
    }

    public static void list8(List<Vector3d> list, BlockPos centerPos, int x, int y, int z, WorldgenRandom random, int checkY) {
        Vector3d pos;
        for (int i = 0; i < 8; i++) {
            if (0.125F >= random.nextFloat()) {
                pos = new Vector3d(centerPos.getX() + (x * ((i < 4) ? 1 : -1)), centerPos.getY() + (y * ((i % 4 < 2) ? 1 : -1)), centerPos.getZ() + (z * ((i % 2 < 1) ? 1 : -1)));
                if (pos.y < checkY) {
                    list.add(pos);
                }
            }
        }
    }

    // 计算点到线段的投影位置
    public static Vector3d getProjectionOnLineSegment(Vector3d pointA, Vector3d pointB, Vector3d pointP) {
        Vector3d direction = new Vector3d(pointB);
        direction.sub(pointA);

        Vector3d pointToP = new Vector3d(pointP);
        pointToP.sub(pointA);

        double dotProduct = pointToP.dot(direction);
        double directionLengthSquared = direction.dot(direction);

        double t = dotProduct / directionLengthSquared;

        Vector3d projection = new Vector3d(direction);
        projection = new Vector3d(projection.x * t, projection.y * t, projection.z * t);
        projection.add(pointA);

        return projection;
    }

    // 计算点到线段的距离
    public static double getDistanceToLineSegment(Vector3d pointA, Vector3d pointB, Vector3d pointP) {
        Vector3d projection = getProjectionOnLineSegment(pointA, pointB, pointP);
        Vector3d distanceVector = new Vector3d(pointP);
        distanceVector.sub(projection);
        return distanceVector.length();
    }

    // 判断垂足是否在线段上
    public static boolean isProjectionBetweenPoints(Vector3d pointA, Vector3d pointB, Vector3d projection) {
        Vector3d point2 = getProjectionOnLineSegment(pointA, pointB, projection);
        double xMax = Math.max(pointA.x, pointB.x) + 0.5;
        double xMin = Math.min(pointA.x, pointB.x) - 0.5;
        double yMax = Math.max(pointA.y, pointB.y) + 0.5;
        double yMin = Math.min(pointA.y, pointB.y) - 0.5;
        double zMax = Math.max(pointA.z, pointB.z) + 0.5;
        double zMin = Math.min(pointA.z, pointB.z) - 0.5;
        return ((point2.x < xMax) && (point2.x > xMin) && (point2.y < yMax) && (point2.y > yMin) && (point2.z < zMax) && (point2.z > zMin));
    }

    //生成坐标列表
    //生成球体坐标列表，带有随机比例
    public static List<Vector3d> ballPos(double radiusD, BlockPos centerPos, float chance, WorldgenRandom random) {
        List<Vector3d> list = new LinkedList<>();
        int radius = (int) radiusD + 1;
        double radius2 = radiusD * radiusD;
        int x2;
        int y2;
        float chance8 = chance * 8;
        for (int x = 0; x < radius; x++) {
            x2 = x * x;
            for (int y = 0; y < radius; y++) {
                y2 = y * y;
                for (int z = 0; z < radius; z++) {
                    if (chance8 >= random.nextFloat() && (x2 + y2 + z * z <= radius2)) {
                        list8(list, centerPos, x, y, z, random);
                    }
                }
            }
        }
        return list;
    }

    //生成椭球体坐标列表，带有随机比例
    public static List<Vector3d> ellipsoidPos(double radiusDX, double radiusDY, double radiusDZ, BlockPos centerPos, float chance, WorldgenRandom random) {
        List<Vector3d> list = new LinkedList<>();
        int radiusX = (int) radiusDX + 1;
        int radiusY = (int) radiusDY + 1;
        int radiusZ = (int) radiusDZ + 1;
        double rX = radiusDX * radiusDX;
        double rY = radiusDY * radiusDY;
        double rZ = radiusDZ * radiusDZ;
        int x2;
        int y2;
        float chance8 = chance * 8;
        for (int x = 0; x < radiusX; x++) {
            x2 = x * x;
            for (int y = 0; y < radiusY; y++) {
                y2 = y * y;
                for (int z = 0; z < radiusZ; z++) {
                    if (chance8 >= random.nextFloat() && (x2 / rX + y2 / rY + (z * z) / rZ) <= 1) {
                        list8(list, centerPos, x, y, z, random);
                    }
                }
            }
        }
        return list;
    }

    //生成椭球体坐标列表，带有内径、随机比例、最大y坐标
    public static List<Vector3d> ellipsoidPos(double radiusDXIn, double radiusDYIn, double radiusDZIn, double radiusDXOut, double radiusDYOut, double radiusDZOut, BlockPos centerPos, float chance, WorldgenRandom random, int checkY) {
        List<Vector3d> list = new LinkedList<>();
        int radiusX = (int) radiusDXOut + 1;
        int radiusY = (int) radiusDYOut + 1;
        int radiusZ = (int) radiusDZOut + 1;
        double rXOut = radiusDXOut * radiusDXOut;
        double rYOut = radiusDYOut * radiusDYOut;
        double rZOut = radiusDZOut * radiusDZOut;
        double rXIn = radiusDXIn * radiusDXIn;
        double rYIn = radiusDYIn * radiusDYIn;
        double rZIn = radiusDZIn * radiusDZIn;
        int x2;
        int y2;
        float chance8 = chance * 8;
        for (int x = 0; x < radiusX; x++) {
            x2 = x * x;
            for (int y = 0; y < radiusY; y++) {
                y2 = y * y;
                for (int z = 0; z < radiusZ; z++) {
                    if (chance8 >= random.nextFloat() && (x2 / rXOut + y2 / rYOut + (z * z) / rZOut) <= 1 && (x2 / rXIn + y2 / rYIn + (z * z) / rZIn) >= 1) {
                        list8(list, centerPos, x, y, z, random, checkY);
                    }
                }
            }
        }
        return list;
    }

    //生成螺旋形坐标列表
    public static List<Vector3d> rotateCloudPos(float rotate, float rotateStep, double length, double lengthStep, int count, BlockPos centerPos) {
        List<Vector3d> poses = new LinkedList<>();
        Vector3d vctPos;
        for (int i = 0; i < count; i++) {
            vctPos = new Vector3d(centerPos.getX() + (length + lengthStep * i) * Mth.cos(rotate + rotateStep * i), centerPos.getY(), centerPos.getZ() + (length + lengthStep * i) * Mth.sin(rotate + rotateStep * i));
            poses.add(vctPos);
        }
        return poses;
    }

    //生成环形坐标列表
    public static void roundPos(BlockPos centerPos, double radius, WorldgenRandom random, List<Vector3d> list, int offset, int rotate, float start) {
        float rStep = Mth.TWO_PI / rotate;
        BlockPos pos;
        for (int i = 0; i < rotate; i++) {
            pos = centerPos.offset(((int) (Mth.cos(rStep * i + start) * radius) + random.nextInt(-offset, offset + 1)), 0, ((int) (Mth.sin(rStep * i + start) * radius) + random.nextInt(-offset, offset + 1)));
            list.add(VectorUtils.toVector3d(pos));
        }
    }

    //生成任意角度圆台坐标列表
    public static List<Vector3d> frustumSetPos(Vector3d startPos, Vector3d endPos, double startRadius, double endRadius, float chance, WorldgenRandom random) {
        int xStart0 = (int) (startPos.x + startRadius + 1);
        int xStart1 = (int) (startPos.x - startRadius - 1);
        int xEnd0 = (int) (endPos.x + endRadius + 1);
        int xEnd1 = (int) (endPos.x - endRadius - 1);
        int yStart0 = (int) (startPos.y + startRadius + 1);
        int yStart1 = (int) (startPos.y - startRadius - 1);
        int yEnd0 = (int) (endPos.y + endRadius + 1);
        int yEnd1 = (int) (endPos.y - endRadius - 1);
        int zStart0 = (int) (startPos.z + startRadius + 1);
        int zStart1 = (int) (startPos.z - startRadius - 1);
        int zEnd0 = (int) (endPos.z + endRadius + 1);
        int zEnd1 = (int) (endPos.z - endRadius - 1);

        int setStartX = Math.min(xStart1, xEnd1);
        int setEndX = Math.max(xStart0, xEnd0);
        int setStartY = Math.min(yStart1, yEnd1);
        int setEndY = Math.max(yStart0, yEnd0);
        int setStartZ = Math.min(zStart1, zEnd1);
        int setEndZ = Math.max(zStart0, zEnd0);

        Vector3d pointP;
        Vector3d pointP2;
        double length = startPos.distance(endPos);
        double lengthGet;
        double lengthP;

        List<Vector3d> list = new ArrayList<>();

        for (int x = setStartX; x <= setEndX; x++) {
            for (int y = setStartY; y <= setEndY; y++) {
                for (int z = setStartZ; z <= setEndZ; z++) {
                    if (chance > random.nextFloat()) {
                        pointP = new Vector3d(x, y, z);
                        if (!isProjectionBetweenPoints(startPos, endPos, pointP)) continue;
                        pointP2 = getProjectionOnLineSegment(startPos, endPos, pointP);
                        lengthGet = pointP2.distance(endPos);//0;//Math.sqrt(y2 + Mth.square(endPos.z - z) - getDistanceToLineSegment(startPos, endPos, pointP));
                        lengthP = lengthGet / length;
                        if (pointP.distance(pointP2) <= (startRadius * lengthP + endRadius * (1.0D - lengthP))) {
                            list.add(pointP);
                        }
                    }
                }
            }
        }
        return list;
    }

    //立方体坐标列表，带有随机比例
    public static List<Vector3d> rectangularPos(BlockPos startPos, BlockPos endPos, float chance, WorldgenRandom random) {
        int startX = Math.min(endPos.getX(), startPos.getX());
        int startY = Math.min(endPos.getY(), startPos.getY());
        int startZ = Math.min(endPos.getZ(), startPos.getZ());
        int endX = Math.max(endPos.getX(), startPos.getX());
        int endY = Math.max(endPos.getY(), startPos.getY());
        int endZ = Math.max(endPos.getZ(), startPos.getZ());
        int xLength = endX - startX;
        int yLength = endY - startY;
        int zLength = endZ - startZ;
        List<Vector3d> list = new ArrayList<>();
        for (int x = 0; x <= xLength; x++) {
            for (int y = 0; y <= yLength; y++) {
                for (int z = 0; z <= zLength; z++) {
                    Vector3d vct3 = new Vector3d(startX + x, startY + y, startZ + z);
                    if (chance >= random.nextFloat()) list.add(vct3);
                }
            }
        }
        return list;
    }

    public static void findVerticalPlane(Vector3d point, Vector3d before, Vector3d after, double side, List<Vector3d> returnList) {

        double l0 = point.distance(before);
        double l1 = point.distance(after);

        double hSide = side / 2;

        double x0 = ((point.x - before.x) * l1 / l0 + (after.x - point.x)) / 2;
        double y0 = ((point.y - before.y) * l1 / l0 + (after.y - point.y)) / 2;
        double z0 = ((point.z - before.z) * l1 / l0 + (after.z - point.z)) / 2;

        Vector3d nX = new Vector3d(0, 0, 0);
        Vector3d nY = new Vector3d(x0, y0, z0);
        Vector3d nZ = new Vector3d(0, 0, 0);
        Vector2d nY2 = new Vector2d(x0, y0);

        double l2 = nY.length();
        double lToY2 = nY2.length();

        if (x0 != 0) {
            nX = new Vector3d(y0 / lToY2, -x0 / lToY2, 0);
            double lX = nX.length();
            nX = new Vector3d(nX.x / lX, nX.y / lX, nX.z / lX);
        }
        if (z0 != 0) {
            nZ = new Vector3d((-z0 * x0) / (l2 * lToY2), (-z0 * y0) / (l2 * lToY2), lToY2 / l2);
            double lZ = nZ.length();
            nZ = new Vector3d(nZ.x / lZ, nZ.y / lZ, nZ.z / lZ);
        }

        Vector3d point0 = new Vector3d((nX.x + nZ.x) * hSide + point.x, (nX.y + nZ.y) * hSide + point.y, (nX.z + nZ.z) * hSide + point.z);
        Vector3d point1 = new Vector3d((nX.x - nZ.x) * hSide + point.x, (nX.y - nZ.y) * hSide + point.y, (nX.z - nZ.z) * hSide + point.z);
        Vector3d point2 = new Vector3d((-nX.x - nZ.x) * hSide + point.x, (-nX.y - nZ.y) * hSide + point.y, (-nX.z - nZ.z) * hSide + point.z);
        Vector3d point3 = new Vector3d((-nX.x + nZ.x) * hSide + point.x, (-nX.y + nZ.y) * hSide + point.y, (-nX.z + nZ.z) * hSide + point.z);
        returnList.add(point0);
        returnList.add(point1);
        returnList.add(point2);
        returnList.add(point3);
    }
}
