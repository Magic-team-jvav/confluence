package org.confluence.mod.util;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public class StructureUtils {
    private static void boll8(BlockPos.MutableBlockPos posCheck, boolean replace, int x, int y, int z, int blockState, BlockPos centerPos, Object2IntMap<BlockPos> blockMap) {
        for (int i = 0; i < 8; i++) {
            posCheck.set(centerPos.getX() + (x * ((i < 4) ? 1 : -1)), centerPos.getY() + (y * ((i % 4 < 2) ? 1 : -1)), centerPos.getZ() + (z * ((i % 2 < 1) ? 1 : -1)));
            if (replace || !blockMap.containsKey(posCheck)) {
                blockMap.put(posCheck.immutable(), blockState);
            }
        }
    }

    private static void boll8(BlockPos.MutableBlockPos posCheck, boolean replace, int x, int y, int z, int blockState1, int blockState2, BlockPos centerPos, Object2IntMap<BlockPos> blockMap, int checkY) {
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

    private static void boll8(BlockPos.MutableBlockPos posCheck, boolean replace, int x, int y, int z, int blockState, BlockPos centerPos, Object2IntMap<BlockPos> blockMap, float placePer, WorldgenRandom random) {
        for (int i = 0; i < 8; i++) {
            if (placePer >= random.nextFloat()) {
                posCheck.set(centerPos.getX() + (x * ((i < 4) ? 1 : -1)), centerPos.getY() + (y * ((i % 4 < 2) ? 1 : -1)), centerPos.getZ() + (z * ((i % 2 < 1) ? 1 : -1)));
                if (replace || !blockMap.containsKey(posCheck)) {
                    blockMap.put(posCheck.immutable(), blockState);
                }
            }
        }
    }

    private static void list8(List<Vector3d> list, BlockPos centerPos, int x, int y, int z, WorldgenRandom random) {
        for (int i = 0; i < 8; i++) {
            if (0.125F >= random.nextFloat()) {
                list.add(new Vector3d(centerPos.getX() + (x * ((i < 4) ? 1 : -1)), centerPos.getY() + (y * ((i % 4 < 2) ? 1 : -1)), centerPos.getZ() + (z * ((i % 2 < 1) ? 1 : -1))));
            }
        }
    }

    public static void boll(double radiusD, BlockPos centerPos, int blockState, boolean replace, Object2IntMap<BlockPos> blockMap) {
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
                        boll8(posCheck, replace, x, y, z, blockState, centerPos, blockMap);
                    }
                }
            }
        }
    }

    public static void boll(double radiusD, BlockPos centerPos, int blockState1, int blockState2, boolean replace, Object2IntMap<BlockPos> blockMap, int checkY) {
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
                        boll8(posCheck, replace, x, y, z, blockState1, blockState2, centerPos, blockMap, checkY);
                    }
                }
            }
        }
    }

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
                        boll8(posCheck, replace, x, y, z, blockState, centerPos, blockMap);
                    }
                }
            }
        }
    }

    public static void boll(double radiusD, BlockPos centerPos, int blockState, boolean replace, Object2IntMap<BlockPos> blockMap, float placePer, WorldgenRandom random) {
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
                        boll8(posCheck, replace, x, y, z, blockState, centerPos, blockMap, placePer, random);
                    }
                }
            }
        }
    }

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
                        boll8(posCheck, replace, x, y, z, blockState, centerPos, blockMap, placePer, random);
                    }
                }
            }
        }
    }

    public static List<Vector3d> bollPos(double radiusD, BlockPos centerPos, float chance, WorldgenRandom random) {
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

    public static void boll(int radius, int wall, BlockPos centerPos, Object2IntMap<BlockPos> blockMap, float chance, WorldgenRandom random, int wallBlock, int airBlock) {
        List<Vector3d> list = bollPos(radius, centerPos, chance, random);
        lineSet(list, radius, radius, wallBlock, true, blockMap);
        lineSet(list, radius - wall, radius - wall, airBlock, true, blockMap);
    }

    public static void boll(int radius, int wall, BlockPos centerPos, Object2IntMap<BlockPos> blockMap, float chance, WorldgenRandom random, int wallBlock, int airBlock1, int airBlock2, int checkY) {
        List<Vector3d> list = bollPos(radius, centerPos, chance, random);
        lineSet(list, radius, radius, wallBlock, true, blockMap);
        lineSet(list, radius - wall, radius - wall, airBlock1, airBlock2, true, blockMap, checkY);
    }

    public static void lineSet(List<Vector3d> VctList, double rStart, double rEnd, int blockstate, boolean replace, Object2IntMap<BlockPos> blockMap) {
        Vector3d posPoint;
        double step = (rEnd - rStart) / VctList.size();
        for (int i = 0; i < VctList.size(); i++) {
            posPoint = VctList.get(i);
            boll(rStart + step * i, new BlockPos((int) posPoint.x, (int) posPoint.y, (int) posPoint.z), blockstate, replace, blockMap);
        }
    }

    public static void lineSet(List<Vector3d> VctList, double rStart, double rEnd, int blockstate1, int blockstate2, boolean replace, Object2IntMap<BlockPos> blockMap, int checkY) {
        Vector3d posPoint;
        double step = (rEnd - rStart) / VctList.size();
        for (int i = 0; i < VctList.size(); i++) {
            posPoint = VctList.get(i);
            boll(rStart + step * i, new BlockPos((int) posPoint.x, (int) posPoint.y, (int) posPoint.z), blockstate1, blockstate2, replace, blockMap, checkY);
        }
    }

    public static void lineSetEllipsoid(List<Vector3d> VctList, double radiusDX, double radiusDY, double radiusDZ, int blockstate, boolean replace, Object2IntMap<BlockPos> blockMap) {
        Vector3d posPoint;
        for (int i = 0; i < VctList.size(); i++) {
            posPoint = VctList.get(i);
            ellipsoid(radiusDX, radiusDY, radiusDZ, new BlockPos((int) posPoint.x, (int) posPoint.y, (int) posPoint.z), blockstate, replace, blockMap);
        }
    }

    public static void lineSet(List<Vector3d> VctList, double rStart, double rEnd, int blockstate, boolean replace, Object2IntMap<BlockPos> blockMap, float placePer, WorldgenRandom random) {
        Vector3d posPoint;
        double step = (rEnd - rStart) / VctList.size();
        for (int i = 0; i < VctList.size(); i++) {
            posPoint = VctList.get(i);
            boll(rStart + step * i, new BlockPos((int) posPoint.x, (int) posPoint.y, (int) posPoint.z), blockstate, replace, blockMap, placePer, random);
        }
    }

    public static void lineSetEllipsoid(List<Vector3d> VctList, double radiusDX, double radiusDY, double radiusDZ, int blockstate, boolean replace, Object2IntMap<BlockPos> blockMap, float placePer, WorldgenRandom random) {
        Vector3d posPoint;
        for (int i = 0; i < VctList.size(); i++) {
            posPoint = VctList.get(i);
            ellipsoid(radiusDX, radiusDY, radiusDZ, new BlockPos((int) posPoint.x, (int) posPoint.y, (int) posPoint.z), blockstate, replace, blockMap, placePer, random);
        }
    }

    public static void rectangular(BlockPos startPos, BlockPos endPos, int blockstate, Object2IntMap<BlockPos> blockMap, int replace) {
        int xLength = endPos.getX() - startPos.getX();
        int yLength = endPos.getY() - startPos.getY();
        int zLength = endPos.getZ() - startPos.getZ();
        BlockPos.MutableBlockPos posCheck = startPos.mutable();
        for (int x = 0; x <= xLength; x++) {
            for (int y = 0; y <= yLength; y++) {
                for (int z = 0; z <= zLength; z++) {
                    posCheck.set(startPos.getX() + x, startPos.getY() + y, startPos.getZ() + z);
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
}
