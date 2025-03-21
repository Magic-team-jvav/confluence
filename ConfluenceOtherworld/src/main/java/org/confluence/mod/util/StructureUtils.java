package org.confluence.mod.util;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class StructureUtils {
    public static void ball8(BlockPos.MutableBlockPos posCheck, boolean replace, int x, int y, int z, int blockState, BlockPos centerPos, Object2IntMap<BlockPos> blockMap) {
        for (int i = 0; i < 8; i++) {
            posCheck.set(centerPos.getX() + (x * ((i < 4) ? 1 : -1)), centerPos.getY() + (y * ((i % 4 < 2) ? 1 : -1)), centerPos.getZ() + (z * ((i % 2 < 1) ? 1 : -1)));
            if (replace || !blockMap.containsKey(posCheck)) {
                blockMap.put(posCheck.immutable(), blockState);
            }
        }
    }

    public static void ball8(BlockPos.MutableBlockPos posCheck, boolean replace, int x, int y, int z, int blockState1, int blockState2, BlockPos centerPos, Object2IntMap<BlockPos> blockMap, int checkY) {
        for (int i = 0; i < 8; i++) {
            posCheck.set(centerPos.getX() + (x * ((i < 4) ? 1 : -1)), centerPos.getY() + (y * ((i % 4 < 2) ? 1 : -1)), centerPos.getZ() + (z * ((i % 2 < 1) ? 1 : -1)));
            if (replace || !blockMap.containsKey(posCheck)) {
                if (posCheck.getY() > checkY) {
                    blockMap.put(posCheck.immutable(), blockState1);
                } else {
                    blockMap.put(posCheck.immutable(), blockState2);
                }
            }
        }
    }

    public static void ball8(BlockPos.MutableBlockPos posCheck, boolean replace, int x, int y, int z, int blockState, BlockPos centerPos, Object2IntMap<BlockPos> blockMap, float placePer, WorldgenRandom random) {
        for (int i = 0; i < 8; i++) {
            if (placePer >= random.nextFloat()) {
                posCheck.set(centerPos.getX() + (x * ((i < 4) ? 1 : -1)), centerPos.getY() + (y * ((i % 4 < 2) ? 1 : -1)), centerPos.getZ() + (z * ((i % 2 < 1) ? 1 : -1)));
                if (replace || !blockMap.containsKey(posCheck)) {
                    blockMap.put(posCheck.immutable(), blockState);
                }
            }
        }
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
        Vector3d vectorAtoProjection = new Vector3d(projection);
        vectorAtoProjection.sub(pointA);
        Vector3d vectorAtoB = new Vector3d(pointB);
        vectorAtoB.sub(pointA);
        return vectorAtoProjection.dot(vectorAtoB) >= 0 && vectorAtoProjection.dot(vectorAtoProjection) <= vectorAtoB.dot(vectorAtoB);
    }

//填充方法
    //球体填充
    public static void ball(double radiusD, BlockPos centerPos, int blockState, boolean replace, Object2IntMap<BlockPos> blockMap) {
        int radius = (int) radiusD + 1;
        double radius2 = radiusD * radiusD;
        int x2;
        int y2;
        BlockPos.MutableBlockPos posCheck = centerPos.mutable();
        for (int x = 0; x < radius; x++) {
            x2 = x * x;
            for (int y = 0; y < radius; y++) {
                y2 = y * y;
                for (int z = 0; z < radius; z++) {
                    if ((x2 + y2 + z * z <= radius2)) {
                        ball8(posCheck, replace, x, y, z, blockState, centerPos, blockMap);
                    }
                }
            }
        }
    }

    //球体填充，带有指定y坐标上下不同种方块填充
    public static void ball(double radiusD, BlockPos centerPos, int blockState1, int blockState2, boolean replace, Object2IntMap<BlockPos> blockMap, int checkY) {
        int radius = (int) radiusD + 1;
        double radius2 = radiusD * radiusD;
        int x2;
        int y2;
        BlockPos.MutableBlockPos posCheck = centerPos.mutable();
        for (int x = 0; x < radius; x++) {
            x2 = x * x;
            for (int y = 0; y < radius; y++) {
                y2 = y * y;
                for (int z = 0; z < radius; z++) {
                    if ((x2 + y2 + z * z <= radius2)) {
                        ball8(posCheck, replace, x, y, z, blockState1, blockState2, centerPos, blockMap, checkY);
                    }
                }
            }
        }
    }

    //球体填充，带有随机比例
    public static void ball(double radiusD, BlockPos centerPos, int blockState, boolean replace, Object2IntMap<BlockPos> blockMap, float placePer, WorldgenRandom random) {
        int radius = (int) radiusD + 1;
        double radius2 = radiusD * radiusD;
        int x2;
        int y2;
        BlockPos.MutableBlockPos posCheck = centerPos.mutable();
        for (int x = 0; x < radius; x++) {
            x2 = x * x;
            for (int y = 0; y < radius; y++) {
                y2 = y * y;
                for (int z = 0; z < radius; z++) {
                    if ((x2 + y2 + z * z <= radius2)) {
                        ball8(posCheck, replace, x, y, z, blockState, centerPos, blockMap, placePer, random);
                    }
                }
            }
        }
    }

    //椭球体填充
    public static void ellipsoid(double radiusDX, double radiusDY, double radiusDZ, BlockPos centerPos, int blockState, boolean replace, Object2IntMap<BlockPos> blockMap) {
        int radiusX = (int) radiusDX + 1;
        int radiusY = (int) radiusDY + 1;
        int radiusZ = (int) radiusDZ + 1;
        double rX = radiusDX * radiusDX;
        double rY = radiusDY * radiusDY;
        double rZ = radiusDZ * radiusDZ;
        int x2;
        int y2;
        BlockPos.MutableBlockPos posCheck = centerPos.mutable();
        for (int x = 0; x < radiusX; x++) {
            x2 = x * x;
            for (int y = 0; y < radiusY; y++) {
                y2 = y * y;
                for (int z = 0; z < radiusZ; z++) {
                    if ((x2 / rX + y2 / rY + (z * z) / rZ) <= 1) {
                        ball8(posCheck, replace, x, y, z, blockState, centerPos, blockMap);
                    }
                }
            }
        }
    }

    //椭球体填充，带有指定y坐标上下不同种方块填充
    public static void ellipsoid(double radiusDX, double radiusDY, double radiusDZ, BlockPos centerPos, int blockState1, int blockState2, boolean replace, Object2IntMap<BlockPos> blockMap, int checkY) {
        int radiusX = (int) radiusDX + 1;
        int radiusY = (int) radiusDY + 1;
        int radiusZ = (int) radiusDZ + 1;
        double rX = radiusDX * radiusDX;
        double rY = radiusDY * radiusDY;
        double rZ = radiusDZ * radiusDZ;
        int x2;
        int y2;
        BlockPos.MutableBlockPos posCheck = centerPos.mutable();
        for (int x = 0; x < radiusX; x++) {
            x2 = x * x;
            for (int y = 0; y < radiusY; y++) {
                y2 = y * y;
                for (int z = 0; z < radiusZ; z++) {
                    if ((x2 / rX + y2 / rY + (z * z) / rZ) <= 1) {
                        ball8(posCheck, replace, x, y, z, blockState1, blockState2, centerPos, blockMap, checkY);
                    }
                }
            }
        }
    }

    //椭球体填充，带有随机比例
    public static void ellipsoid(double radiusDX, double radiusDY, double radiusDZ, BlockPos centerPos, int blockState, boolean replace, Object2IntMap<BlockPos> blockMap, float placePer, WorldgenRandom random) {
        int radiusX = (int) radiusDX + 1;
        int radiusY = (int) radiusDY + 1;
        int radiusZ = (int) radiusDZ + 1;
        double rX = radiusDX * radiusDX;
        double rY = radiusDY * radiusDY;
        double rZ = radiusDZ * radiusDZ;
        int x2;
        int y2;
        BlockPos.MutableBlockPos posCheck = centerPos.mutable();
        for (int x = 0; x < radiusX; x++) {
            x2 = x * x;
            for (int y = 0; y < radiusY; y++) {
                y2 = y * y;
                for (int z = 0; z < radiusZ; z++) {
                    if ((x2 / rX + y2 / rY + (z * z) / rZ) <= 1) {
                        ball8(posCheck, replace, x, y, z, blockState, centerPos, blockMap, placePer, random);
                    }
                }
            }
        }
    }

    //立方体填充
    public static void rectangular(BlockPos startPos, BlockPos endPos, int blockstate, Object2IntMap<BlockPos> blockMap, int replace) {
        int startX = Math.min(endPos.getX(), startPos.getX());
        int startY = Math.min(endPos.getY(), startPos.getY());
        int startZ = Math.min(endPos.getZ(), startPos.getZ());
        int endX = Math.max(endPos.getX(), startPos.getX());
        int endY = Math.max(endPos.getY(), startPos.getY());
        int endZ = Math.max(endPos.getZ(), startPos.getZ());
        int xLength = endX - startX;
        int yLength = endY - startY;
        int zLength = endZ - startZ;
        BlockPos.MutableBlockPos posCheck = startPos.mutable();
        for (int x = 0; x <= xLength; x++) {
            for (int y = 0; y <= yLength; y++) {
                for (int z = 0; z <= zLength; z++) {
                    posCheck.set(startX + x, startY + y, startZ + z);
                    if (replace == 0) {
                        blockMap.put(posCheck.immutable(), blockstate);
                    } else if (replace == 1 && blockMap.containsKey(posCheck.immutable())) {
                        blockMap.put(posCheck.immutable(), blockstate);
                    } else if (replace == 2 && !blockMap.containsKey(posCheck.immutable())) {
                        blockMap.put(posCheck.immutable(), blockstate);
                    }
                }
            }
        }
    }

    //任意角度圆台填充
    public static void frustumSet(Vector3d startPos, Vector3d endPos, double startRadius, double endRadius, int blockstate, Object2IntMap<BlockPos> blockMap) {
        int xStart0 = (int) startPos.x + (int) startRadius + 1;
        int xStart1 = (int) startPos.x - (int) startRadius - 1;
        int xEnd0 = (int) endPos.x + (int) endRadius + 1;
        int xEnd1 = (int) endPos.x - (int) endRadius - 1;
        int yStart0 = (int) startPos.y + (int) startRadius + 1;
        int yStart1 = (int) startPos.y - (int) startRadius - 1;
        int yEnd0 = (int) endPos.y + (int) endRadius + 1;
        int yEnd1 = (int) endPos.y - (int) endRadius - 1;
        int zStart0 = (int) startPos.z + (int) startRadius + 1;
        int zStart1 = (int) startPos.z - (int) startRadius - 1;
        int zEnd0 = (int) endPos.z + (int) endRadius + 1;
        int zEnd1 = (int) endPos.z - (int) endRadius - 1;

        int setStartX = Math.min(xStart1, xEnd1);
        int setEndX = Math.max(xStart0, xEnd0);
        int setStartY = Math.min(yStart1, yEnd1);
        int setEndY = Math.max(yStart0, yEnd0);
        int setStartZ = Math.min(zStart1, zEnd1);
        int setEndZ = Math.max(zStart0, zEnd0);

        Vector3d pointP;
        BlockPos pointPInt;
        double length = Math.sqrt(Math.pow(endPos.x - startPos.x, 2) + Math.pow(endPos.y - startPos.y, 2) + Math.pow(endPos.z - startPos.z, 2));
        double lengthGet;
        double lengthP;
        double x2;
        double y2;

        for (int x = setStartX; x <= setEndX; x++){
            x2 = Math.pow(endPos.x - x, 2);
            for (int y = setStartY; y <= setEndY; y++){
                y2 = Math.pow(endPos.y - y, 2);
                for (int z = setStartZ; z <= setEndZ; z++){
                    pointP = new Vector3d(x, y, z);
                    lengthGet = Math.sqrt(x2 + y2 + Math.pow(endPos.z - z, 2));
                    lengthP = lengthGet / length;
                    if (isProjectionBetweenPoints(startPos, endPos, pointP) && getDistanceToLineSegment(startPos, endPos, pointP) <= (startRadius * lengthP + endRadius * (1.0D - lengthP))) {
                        pointPInt = new BlockPos(x, y, z);
                        blockMap.put(pointPInt, blockstate);
                    }
                }
            }
        }
    }

//生成坐标列表
    //生成球体坐标列表，带有随机比例
    public static List<Vector3d> ballPos(double radiusD, BlockPos centerPos, float chance, WorldgenRandom random) {
        List<Vector3d> list = new ArrayList<>();
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
        List<Vector3d> list = new ArrayList<>();
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
        List<Vector3d> list = new ArrayList<>();
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
        List<Vector3d> poses = new ArrayList<>();
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
        int xStart0 = (int) startPos.x + (int) startRadius + 1;
        int xStart1 = (int) startPos.x - (int) startRadius - 1;
        int xEnd0 = (int) endPos.x + (int) endRadius + 1;
        int xEnd1 = (int) endPos.x - (int) endRadius - 1;
        int yStart0 = (int) startPos.y + (int) startRadius + 1;
        int yStart1 = (int) startPos.y - (int) startRadius - 1;
        int yEnd0 = (int) endPos.y + (int) endRadius + 1;
        int yEnd1 = (int) endPos.y - (int) endRadius - 1;
        int zStart0 = (int) startPos.z + (int) startRadius + 1;
        int zStart1 = (int) startPos.z - (int) startRadius - 1;
        int zEnd0 = (int) endPos.z + (int) endRadius + 1;
        int zEnd1 = (int) endPos.z - (int) endRadius - 1;

        int setStartX = Math.min(xStart1, xEnd1);
        int setEndX = Math.max(xStart0, xEnd0);
        int setStartY = Math.min(yStart1, yEnd1);
        int setEndY = Math.max(yStart0, yEnd0);
        int setStartZ = Math.min(zStart1, zEnd1);
        int setEndZ = Math.max(zStart0, zEnd0);

        Vector3d pointP;
        double length = Math.sqrt(Math.pow(endPos.x - startPos.x, 2) + Math.pow(endPos.y - startPos.y, 2) + Math.pow(endPos.z - startPos.z, 2));
        double lengthGet;
        double lengthP;
        double x2;
        double y2;

        List<Vector3d> list = new ArrayList<>();

        for (int x = setStartX; x <= setEndX; x++){
            x2 = Math.pow(endPos.x - x, 2);
            for (int y = setStartY; y <= setEndY; y++){
                y2 = Math.pow(endPos.y - y, 2);
                for (int z = setStartZ; z <= setEndZ; z++){
                    if (chance >= random.nextFloat()) {
                        pointP = new Vector3d(x, y, z);
                        lengthGet = Math.sqrt(x2 + y2 + Math.pow(endPos.z - z, 2));
                        lengthP = lengthGet / length;
                        if (isProjectionBetweenPoints(startPos, endPos, pointP) && getDistanceToLineSegment(startPos, endPos, pointP) <= (startRadius * lengthP + endRadius * (1.0D - lengthP))) {
                            list.add(pointP);
                        }
                    }
                }
            }
        }
        return list;
    }

//列表快捷填充
    //在整个坐标列表上填充球体，带有半径渐变
    public static void lineSet(List<Vector3d> VctList, double rStart, double rEnd, int blockstate, boolean replace, Object2IntMap<BlockPos> blockMap) {
        Vector3d posPoint;
        double step = (rEnd - rStart) / VctList.size();
        for (int i = 0; i < VctList.size(); i++) {
            posPoint = VctList.get(i);
            ball(rStart + step * i, new BlockPos((int) posPoint.x, (int) posPoint.y, (int) posPoint.z), blockstate, replace, blockMap);
        }
    }

    //在整个坐标列表上填充球体，带有指定y坐标上下不同种方块填充
    public static void lineSet(List<Vector3d> VctList, double rStart, double rEnd, int blockstate1, int blockstate2, boolean replace, Object2IntMap<BlockPos> blockMap, int checkY) {
        Vector3d posPoint;
        double step = (rEnd - rStart) / VctList.size();
        for (int i = 0; i < VctList.size(); i++) {
            posPoint = VctList.get(i);
            ball(rStart + step * i, new BlockPos((int) posPoint.x, (int) posPoint.y, (int) posPoint.z), blockstate1, blockstate2, replace, blockMap, checkY);
        }
    }

    //在整个坐标列表上填充椭球体
    public static void lineSetEllipsoid(List<Vector3d> VctList, double radiusDX, double radiusDY, double radiusDZ, int blockstate, boolean replace, Object2IntMap<BlockPos> blockMap) {
        Vector3d posPoint;
        for (Vector3d vector3d : VctList) {
            posPoint = vector3d;
            ellipsoid(radiusDX, radiusDY, radiusDZ, new BlockPos((int) posPoint.x, (int) posPoint.y, (int) posPoint.z), blockstate, replace, blockMap);
        }
    }

    //在整个坐标列表上填充球体，带有半径渐变、随机比例
    public static void lineSet(List<Vector3d> VctList, double rStart, double rEnd, int blockstate, boolean replace, Object2IntMap<BlockPos> blockMap, float placePer, WorldgenRandom random) {
        Vector3d posPoint;
        double step = (rEnd - rStart) / VctList.size();
        for (int i = 0; i < VctList.size(); i++) {
            posPoint = VctList.get(i);
            ball(rStart + step * i, new BlockPos((int) posPoint.x, (int) posPoint.y, (int) posPoint.z), blockstate, replace, blockMap, placePer, random);
        }
    }

    //在整个坐标列表上填充椭球体，带有随机比例
    public static void lineSetEllipsoid(List<Vector3d> VctList, double radiusDX, double radiusDY, double radiusDZ, int blockstate, boolean replace, Object2IntMap<BlockPos> blockMap, float placePer, WorldgenRandom random) {
        Vector3d posPoint;
        for (Vector3d vector3d : VctList) {
            posPoint = vector3d;
            ellipsoid(radiusDX, radiusDY, radiusDZ, new BlockPos((int) posPoint.x, (int) posPoint.y, (int) posPoint.z), blockstate, replace, blockMap, placePer, random);
        }
    }

    //在整个坐标列表上放置地物
    public static void lineSetFeature(List<Vector3d> list, Map<BlockPos, ResourceLocation> featureMap, ResourceLocation[] feature, WorldgenRandom random) {
        BlockPos pos;
        Vector3d vctPos;
        for (Vector3d vector3d : list) {
            vctPos = vector3d;
            pos = VectorUtils.fromVector3d(vctPos);
            featureMap.put(pos, feature[random.nextInt(feature.length)]);
        }
    }

//快捷方法整合
    //不规则球体填充，带有壁厚、随机比例
    public static void ball(int radius, int wall, BlockPos centerPos, Object2IntMap<BlockPos> blockMap, float chance, WorldgenRandom random, int wallBlock, int airBlock) {
        List<Vector3d> list = ballPos(radius, centerPos, chance, random);
        lineSet(list, radius, radius, wallBlock, true, blockMap);
        lineSet(list, radius - wall, radius - wall, airBlock, true, blockMap);
    }

    //不规则球体填充，带有壁厚、随机比例、指定y坐标上下不同种方块填充
    public static void ball(int radius, int wall, BlockPos centerPos, Object2IntMap<BlockPos> blockMap, float chance, WorldgenRandom random, int wallBlock, int airBlock1, int airBlock2, int checkY) {
        List<Vector3d> list = ballPos(radius, centerPos, chance, random);
        lineSet(list, radius, radius, wallBlock, true, blockMap);
        lineSet(list, radius - wall, radius - wall, airBlock1, airBlock2, true, blockMap, checkY);
    }

}
