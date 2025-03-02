package org.confluence.mod.util;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public class StructureUtils {
    public static boolean radiusCheck(double radius, BlockPos centerPos, BlockPos blockPos) {
        int x = (centerPos.getX() - blockPos.getX());
        int y = (centerPos.getY() - blockPos.getY());
        int z = (centerPos.getZ() - blockPos.getZ());
        float dis = x * x + y * y + z * z;
        return ((radius * radius) >= dis);
    }

    public static void boll(double radiusD, BlockPos centerPos, int blockState, boolean replace, Object2IntMap<BlockPos> blockMap) {
        int radius = (int) radiusD + 1;
        BlockPos pos = centerPos.offset(-radius, -radius, -radius);
        BlockPos posCheck;
        for (int x = 0; x < (2 * radius); x++) {
            for (int y = 0; y < (2 * radius); y++) {
                for (int z = 0; z < (2 * radius); z++) {
                    posCheck = pos.offset(x, y, z);
                    if (radiusCheck(radiusD, centerPos, posCheck) && (replace || !blockMap.containsKey(posCheck))) {
                        blockMap.put(posCheck, blockState);
                    }
                }
            }
        }
    }

    public static void ellipsoid(double radiusDX, double radiusDY, double radiusDZ, BlockPos centerPos, int blockState, boolean replace, Object2IntMap<BlockPos> blockMap) {
        int radiusX = (int) radiusDX + 1;
        int radiusY = (int) radiusDY + 1;
        int radiusZ = (int) radiusDZ + 1;
        BlockPos pos = centerPos.offset(-radiusX, -radiusY, -radiusZ);
        BlockPos posCheck;
        for (int x = 0; x < (2 * radiusX); x++) {
            for (int y = 0; y < (2 * radiusY); y++) {
                for (int z = 0; z < (2 * radiusZ); z++) {
                    posCheck = pos.offset(x, y, z);
                    if (((Math.pow(posCheck.getX() - centerPos.getX(), 2) / (radiusDX * radiusDX) + Math.pow(posCheck.getY() - centerPos.getY(), 2) / (radiusDY * radiusDY) + Math.pow(posCheck.getZ() - centerPos.getZ(), 2) / (radiusDZ * radiusDZ)) <= 1) && (replace || !blockMap.containsKey(posCheck))) {
                        blockMap.put(posCheck, blockState);
                    }
                }
            }
        }
    }

    public static void boll(double radiusD, BlockPos centerPos, int blockState, boolean replace, Object2IntMap<BlockPos> blockMap, float placePer, WorldgenRandom random) {
        int radius = (int) radiusD + 1;
        BlockPos pos = centerPos.offset(-radius, -radius, -radius);
        BlockPos posCheck;
        for (int x = 0; x < (2 * radius); x++) {
            for (int y = 0; y < (2 * radius); y++) {
                for (int z = 0; z < (2 * radius); z++) {
                    posCheck = pos.offset(x, y, z);
                    if (radiusCheck(radiusD, centerPos, posCheck) && (replace || !blockMap.containsKey(posCheck)) && (placePer >= random.nextFloat())) {
                        blockMap.put(posCheck, blockState);
                    }
                }
            }
        }
    }

    public static void ellipsoid(double radiusDX, double radiusDY, double radiusDZ, BlockPos centerPos, int blockState, boolean replace, Object2IntMap<BlockPos> blockMap, float placePer, WorldgenRandom random) {
        int radiusX = (int) radiusDX + 1;
        int radiusY = (int) radiusDY + 1;
        int radiusZ = (int) radiusDZ + 1;
        BlockPos pos = centerPos.offset(-radiusX, -radiusY, -radiusZ);
        BlockPos posCheck;
        for (int x = 0; x < (2 * radiusX); x++) {
            for (int y = 0; y < (2 * radiusY); y++) {
                for (int z = 0; z < (2 * radiusZ); z++) {
                    posCheck = pos.offset(x, y, z);
                    if (((Math.pow(posCheck.getX() - centerPos.getX(), 2) / (radiusDX * radiusDX) + Math.pow(posCheck.getY() - centerPos.getY(), 2) / (radiusDY * radiusDY) + Math.pow(posCheck.getZ() - centerPos.getZ(), 2) / (radiusDZ * radiusDZ)) <= 1) && (replace || !blockMap.containsKey(posCheck)) && (placePer >= random.nextFloat())) {
                        blockMap.put(posCheck, blockState);
                    }
                }
            }
        }
    }

    public static List<Vector3d> bollPos(double radiusD, BlockPos centerPos, float chance, WorldgenRandom random) {
        List<Vector3d> list = new ArrayList<>();
        int radius = (int) radiusD + 1;
        BlockPos pos = centerPos.offset(-radius, -radius, -radius);
        BlockPos posCheck;
        for (int x = 0; x < (2 * radius); x++) {
            for (int y = 0; y < (2 * radius); y++) {
                for (int z = 0; z < (2 * radius); z++) {
                    posCheck = pos.offset(x, y, z);
                    if (radiusCheck(radiusD, centerPos, posCheck) && (chance >= random.nextFloat())) {
                        list.add(new Vector3d(posCheck.getX(), posCheck.getY(), posCheck.getZ()));
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
        BlockPos pos = centerPos.offset(-radiusX, -radiusY, -radiusZ);
        BlockPos posCheck;
        for (int x = 0; x < (2 * radiusX); x++) {
            for (int y = 0; y < (2 * radiusY); y++) {
                for (int z = 0; z < (2 * radiusZ); z++) {
                    posCheck = pos.offset(x, y, z);
                    if (((Math.pow(posCheck.getX() - centerPos.getX(), 2) / (radiusDX * radiusDX) + Math.pow(posCheck.getY() - centerPos.getY(), 2) / (radiusDY * radiusDY) + Math.pow(posCheck.getZ() - centerPos.getZ(), 2) / (radiusDZ * radiusDZ)) <= 1) && (chance >= random.nextFloat())) {
                        list.add(new Vector3d(posCheck.getX(), posCheck.getY(), posCheck.getZ()));
                    }
                }
            }
        }
        return list;
    }

    public static void boll(int radius, int wall, BlockPos centerPos, Object2IntMap<BlockPos> blockMap, float chance, WorldgenRandom random) {
        BlockPos pos = centerPos.offset(-radius, -radius, -radius);
        BlockPos posCheck;
        List<BlockPos> posList = new ArrayList<>();
        for (int x = 0; x < (2 * radius); x++) {
            for (int y = 0; y < (2 * radius); y++) {
                for (int z = 0; z < (2 * radius); z++) {
                    posCheck = pos.offset(x, y, z);
                    if (radiusCheck(radius, centerPos, posCheck) && (chance >= random.nextFloat())) {
                        posList.add(posCheck);
                    }
                }
            }
        }
        for (BlockPos blockPos : posList) {
            boll(radius, blockPos, 1, true, blockMap);
        }
        for (BlockPos blockPos : posList) {
            boll(radius - wall, blockPos, 0, true, blockMap);
        }
    }

    public static void lineSet(List<Vector3d> VctList, double rStart, double rEnd, int blockstate, boolean replace, Object2IntMap<BlockPos> blockMap) {
        Vector3d posPoint;
        double step = (rEnd - rStart) / VctList.size();
        for (int i = 0; i < VctList.size(); i++) {
            posPoint = VctList.get(i);
            boll(rStart + step * i, new BlockPos((int) posPoint.x, (int) posPoint.y, (int) posPoint.z), blockstate, replace, blockMap);
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
}
