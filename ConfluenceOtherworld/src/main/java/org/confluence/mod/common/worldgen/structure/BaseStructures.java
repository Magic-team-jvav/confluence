package org.confluence.mod.common.worldgen.structure;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

import static org.confluence.lib.util.StructureUtils.*;
import static org.confluence.lib.util.VectorUtils.ellipsoidPos;
import static org.confluence.lib.util.VectorUtils.lightningPathList;

public class BaseStructures {
    public static BlockPos livingTree(
            BlockPos centerPos,
            Object2IntMap<BlockPos> blockMap,
            WorldgenRandom random,
            int treeTrunkHeight,
            int treeTrunkEndOffset,
            double treeTrunkStartRadius,
            double treeTrunkEndRadius,
            int treeTrunkBlocks,
            int treeBranchLength,
            int treeBranchLengthRandomAddition,
            int treeBranchEndOffset,
            int treeBranchStartRadius,
            int treeBranchStartRadiusRandomAddition,
            int treeBranchEndRadius,
            int treeBranchBlocks,
            int treeBranchCount,
            int treeBranchCountRandomAddition,
            int treeRootLength,
            int treeRootLengthRandomAddition,
            int treeRootEndOffset,
            int treeRootStartRadius,
            int treeRootStartRadiusRandomAddition,
            int treeRootEndRadius,
            int treeRootBlocks,
            int treeRootCount,
            int treeRootCountRandomAddition,
            boolean isGenerateTreeRoot,
            int largeTreeRootLength,
            int largeTreeRootEndOffset,
            double largeTreeRootStartRadius,
            double largeTreeRootEndRadius,
            int largeTreeRootBlocks,
            boolean isGenerateLargeTreeRoot,
            double branchLeafGenerationRadiusXZ,
            double branchLeafGenerationRadiusY,
            double trunkLeafGenerationRadiusXZ,
            double trunkLeafGenerationRadiusY,
            double leafBlobGenerationRadiusXZ,
            double leafBlobGenerationRadiusY,
            float leafPosDensity,
            float leafDensity,
            int leafBlocks,
            int rootEndBlocks
    ) {
        List<Vector3d> locationList = new ArrayList<>();
        Vector3d locationStart = new Vector3d();
        Vector3d locationEnd = new Vector3d();

        locationStart.x = (centerPos.getX());
        locationStart.y = (centerPos.getY());
        locationStart.z = (centerPos.getZ());
        locationEnd.x = (centerPos.getX() + random.nextInt(-treeTrunkEndOffset, treeTrunkEndOffset + 1));
        locationEnd.y = (centerPos.getY() + treeTrunkHeight);
        locationEnd.z = (centerPos.getZ() + random.nextInt(-treeTrunkEndOffset, treeTrunkEndOffset + 1));

        locationList.add(locationStart);
        locationList.add(locationEnd);
        List<Vector3d> leavesTop = ellipsoidPos(trunkLeafGenerationRadiusXZ, trunkLeafGenerationRadiusY, trunkLeafGenerationRadiusXZ, new BlockPos((int) locationEnd.x, (int) locationEnd.y, (int) locationEnd.z), leafPosDensity, random);
        lineSetEllipsoid(leavesTop, leafBlobGenerationRadiusXZ, leafBlobGenerationRadiusY, leafBlobGenerationRadiusXZ, leafBlocks, true, blockMap, leafDensity, random);
        lightningPathList(locationList, 1, 8, random);

        lineSet(locationList, treeTrunkStartRadius, treeTrunkEndRadius, treeTrunkBlocks, true, blockMap);

        stick(
                random,
                locationList,
                blockMap,
                true,
                treeBranchLength,
                treeBranchLengthRandomAddition,
                treeBranchEndOffset,
                treeBranchStartRadius,
                treeBranchStartRadiusRandomAddition,
                treeBranchEndRadius,
                treeBranchBlocks,
                treeBranchCount,
                treeBranchCountRandomAddition,
                branchLeafGenerationRadiusXZ,
                branchLeafGenerationRadiusY,
                leafBlobGenerationRadiusXZ,
                leafBlobGenerationRadiusY,
                leafPosDensity,
                leafDensity,
                leafBlocks,
                rootEndBlocks
        );
        if (isGenerateTreeRoot) {
            stick(
                    random,
                    locationList,
                    blockMap,
                    false,
                    treeRootLength,
                    treeRootLengthRandomAddition,
                    treeRootEndOffset,
                    treeRootStartRadius,
                    treeRootStartRadiusRandomAddition,
                    treeRootEndRadius,
                    treeRootBlocks,
                    treeRootCount,
                    treeRootCountRandomAddition,
                    branchLeafGenerationRadiusXZ,
                    branchLeafGenerationRadiusY,
                    leafBlobGenerationRadiusXZ,
                    leafBlobGenerationRadiusY,
                    leafPosDensity,
                    leafDensity,
                    leafBlocks,
                    rootEndBlocks
            );
        }

        if (isGenerateLargeTreeRoot) {
            locationList.clear();
            locationEnd.x = (centerPos.getX() + random.nextInt(-largeTreeRootEndOffset, largeTreeRootEndOffset + 1));
            locationEnd.y = (centerPos.getY() - largeTreeRootLength);
            locationEnd.z = (centerPos.getZ() + random.nextInt(-largeTreeRootEndOffset, largeTreeRootEndOffset + 1));
            locationList.add(locationStart);
            locationList.add(locationEnd);
            lightningPathList(locationList, 1, 12, random);
            lineSet(locationList, largeTreeRootStartRadius, largeTreeRootEndRadius, largeTreeRootBlocks, true, blockMap);
            ball(4.9, centerPos, 0, true, blockMap);
            lineSet(locationList, largeTreeRootStartRadius * 2.0D / 5.0D, largeTreeRootEndRadius - 0.1D, 0, true, blockMap);
        }
        Vector3d room = locationList.get(locationList.size() / 2 + random.nextInt(-locationList.size() / 4, locationList.size() / 4 + 1));
        centerPos = new BlockPos((int) room.x, (int) room.y, (int) room.z);
        return centerPos;
    }

    private static void stick(
            WorldgenRandom random,
            List<Vector3d> locationList,
            Object2IntMap<BlockPos> blockMap,
            boolean branch,
            int len,
            int lenRandom,
            int offset,
            int startRadius,
            int startRadiusRandomAddition,
            int endRadius,
            int blocks,
            int count,
            int countRandomAddition,
            double branchLeafGenerationRadiusXZ,
            double branchLeafGenerationRadiusY,
            double leafBlobGenerationRadiusXZ,
            double leafBlobGenerationRadiusY,
            float leafPosDensity,
            float leafDensity,
            int leafBlocks,
            int endBlocks
    ) {
        List<Vector3d> leavesTop = new ArrayList<>();
        int stickCount = count + random.nextInt(countRandomAddition);
        int length;
        double anCs;
        double everyA;
        double everyB;
        double endX;
        double endY;
        double endZ;
        Vector3d rootEnd;
        for (int stickPlace = 0; stickPlace < stickCount; stickPlace++) {
            anCs = 360.0 / stickCount;
            everyA = anCs * stickPlace / 180 * Math.PI;
            everyB = ((((double) random.nextInt(110) - 20) * Math.pow((double) random.nextInt(101) / 100, 3)) / 180 * Math.PI);
            length = len + random.nextInt(lenRandom);
            endX = length * Math.cos(everyA) * Math.cos(everyB);
            endY = length * Math.sin(everyB);
            endZ = length * Math.sin(everyA) * Math.cos(everyB);
            Vector3d stickStart = locationList.get(branch ? Math.max((locationList.size() - (locationList.size() / 11 * 7) - random.nextInt(locationList.size() / 9)), 0) : (random.nextInt(locationList.size() / 9)));
            Vector3d stickEnd = new Vector3d();
            stickEnd.x = branch ? (locationList.getLast().x + endX) : (locationList.getFirst().x + endX / 2);
            stickEnd.y = branch ? (locationList.getLast().y + endY + offset) : (locationList.getFirst().y - endY + offset);
            stickEnd.z = branch ? (locationList.getLast().z + endZ) : (locationList.getFirst().z + endZ / 2);
            List<Vector3d> stickList = new ArrayList<>();
            stickList.add(stickStart);
            stickList.add(stickEnd);
            lightningPathList(stickList, 1.0, 8, random);

            lineSet(stickList, startRadius + random.nextInt(startRadiusRandomAddition), endRadius, blocks, true, blockMap);
            if (branch) {
                leavesTop.clear();
                leavesTop = ellipsoidPos(branchLeafGenerationRadiusXZ, branchLeafGenerationRadiusY, branchLeafGenerationRadiusXZ, new BlockPos((int) stickEnd.x, (int) stickEnd.y, (int) stickEnd.z), leafPosDensity, random);
                lineSetEllipsoid(leavesTop, leafBlobGenerationRadiusXZ, leafBlobGenerationRadiusY, leafBlobGenerationRadiusXZ, leafBlocks, false, blockMap, leafDensity, random);
            } else {
                rootEnd = stickList.getLast();
                ball(endRadius, new BlockPos((int) rootEnd.x, (int) rootEnd.y, (int) rootEnd.z), endBlocks, true, blockMap);
            }
        }
    }
}
