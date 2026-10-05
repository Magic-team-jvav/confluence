package org.confluence.mod.common.worldgen.structure;

import PortLib.extensions.java.util.List.PortListExtension;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import org.confluence.lib.util.LibMathUtils;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.confluence.lib.util.LibGeometryUtils.ellipsoidPos;
import static org.confluence.lib.util.LibGeometryUtils.lightningPathList;
import static org.confluence.lib.util.LibStructureUtils.*;

public class BaseStructures {
    /// 根系深入地下的末端改用 endBlocks（生命红木为孢根）填充的路径点数，调大该值即可提高孢根密度
    private static final int ROOT_END_SEGMENT = 5;

    public static BlockPos livingTree(
            BlockPos centerPos,
            Object2IntMap<BlockPos> blockMap,
            WorldgenRandom random,
            int treeTrunkHeight,
            int treeTrunkEndOffset,
            float treeTrunkStartRadius,
            float treeTrunkEndRadius,
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
            float largeTreeRootStartRadius,
            float largeTreeRootEndRadius,
            int largeTreeRootBlocks,
            boolean isGenerateLargeTreeRoot,
            float branchLeafGenerationRadiusXZ,
            float branchLeafGenerationRadiusY,
            float trunkLeafGenerationRadiusXZ,
            float trunkLeafGenerationRadiusY,
            float leafBlobGenerationRadiusXZ,
            float leafBlobGenerationRadiusY,
            float leafPosDensity,
            float leafDensity,
            int leafBlocks,
            int rootEndBlocks,
            int rootEndWrapCount
    ) {
        List<Vector3f> locationList = new ArrayList<>();
        Vector3f locationStart = new Vector3f();
        Vector3f locationEnd = new Vector3f();

        locationStart.x = (centerPos.getX());
        locationStart.y = (centerPos.getY());
        locationStart.z = (centerPos.getZ());
        locationEnd.x = (centerPos.getX() + random.nextInt(-treeTrunkEndOffset, treeTrunkEndOffset + 1));
        locationEnd.y = (centerPos.getY() + treeTrunkHeight);
        locationEnd.z = (centerPos.getZ() + random.nextInt(-treeTrunkEndOffset, treeTrunkEndOffset + 1));

        locationList.add(locationStart);
        locationList.add(locationEnd);
        List<Vector3f> leavesTop = ellipsoidPos(trunkLeafGenerationRadiusXZ, trunkLeafGenerationRadiusY, trunkLeafGenerationRadiusXZ, LibMathUtils.fromVector3f(locationEnd), leafPosDensity, random);
        lineSetEllipsoid(leavesTop, leafBlobGenerationRadiusXZ, leafBlobGenerationRadiusY, leafBlobGenerationRadiusXZ, leafBlocks, true, blockMap, leafDensity, random);
        lightningPathList(locationList, 1, 0.125F, random);

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
                rootEndBlocks,
                0
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
                    rootEndBlocks,
                    rootEndWrapCount
            );
        }

        if (isGenerateLargeTreeRoot) {
            locationList.clear();
            locationEnd.x = (centerPos.getX() + random.nextInt(-largeTreeRootEndOffset, largeTreeRootEndOffset + 1));
            locationEnd.y = (centerPos.getY() - largeTreeRootLength);
            locationEnd.z = (centerPos.getZ() + random.nextInt(-largeTreeRootEndOffset, largeTreeRootEndOffset + 1));
            locationList.add(locationStart);
            locationList.add(locationEnd);
            lightningPathList(locationList, 1, 0.083F, random);
            lineSet(locationList, largeTreeRootStartRadius, largeTreeRootEndRadius, largeTreeRootBlocks, true, blockMap);
            ball(4.9F, centerPos, 0, true, blockMap);
            lineSet(locationList, largeTreeRootStartRadius * 2 / 5, largeTreeRootEndRadius - 0.1F, 0, true, blockMap);
        }
        return LibMathUtils.fromVector3f(locationList.get(locationList.size() / 2 + random.nextInt(-locationList.size() / 4, locationList.size() / 4 + 1)));
    }

    private static void stick(
            WorldgenRandom random,
            List<Vector3f> locationList,
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
            float branchLeafGenerationRadiusXZ,
            float branchLeafGenerationRadiusY,
            float leafBlobGenerationRadiusXZ,
            float leafBlobGenerationRadiusY,
            float leafPosDensity,
            float leafDensity,
            int leafBlocks,
            int endBlocks,
            int rootEndWrapCount
    ) {
        List<Vector3f> leavesTop = new ArrayList<>();
        int stickCount = count + random.nextInt(countRandomAddition);
        int length;
        float anCs;
        float everyA;
        float everyB;
        float endX;
        float endY;
        float endZ;
        Vector3f rootEnd;
        for (int stickPlace = 0; stickPlace < stickCount; stickPlace++) {
            anCs = 360.0F / stickCount;
            everyA = anCs * stickPlace * Mth.DEG_TO_RAD;
            everyB = ((((float) random.nextInt(110) - 20) * (float) Math.pow(random.nextDouble(), 3)) * Mth.DEG_TO_RAD);
            length = len + random.nextInt(lenRandom);
            endX = length * Mth.cos(everyA) * Mth.cos(everyB);
            endY = length * Mth.sin(everyB);
            endZ = length * Mth.sin(everyA) * Mth.cos(everyB);
            Vector3f stickStart = locationList.get(branch ? Math.max((locationList.size() - (locationList.size() / 11 * 7) - random.nextInt(locationList.size() / 9)), 0) : (random.nextInt(locationList.size() / 9)));
            Vector3f stickEnd = new Vector3f();
            if (branch) {
                Vector3f last = PortListExtension.getLast(locationList);
                stickEnd.set(last.x + endX, last.y + endY + offset, last.z + endZ);
            } else {
                Vector3f first = PortListExtension.getFirst(locationList);
                stickEnd.set(first.x + endX * 0.5F, first.y - endY + offset, first.z + endZ * 0.5F);
            }
            List<Vector3f> stickList = new ArrayList<>();
            stickList.add(stickStart);
            stickList.add(stickEnd);
            lightningPathList(stickList, 1.0F, 0.125F, random);

            lineSet(stickList, startRadius + random.nextInt(startRadiusRandomAddition), endRadius, blocks, true, blockMap);
            if (branch) {
                leavesTop.clear();
                leavesTop = ellipsoidPos(branchLeafGenerationRadiusXZ, branchLeafGenerationRadiusY, branchLeafGenerationRadiusXZ, LibMathUtils.fromVector3f(stickEnd), leafPosDensity, random);
                lineSetEllipsoid(leavesTop, leafBlobGenerationRadiusXZ, leafBlobGenerationRadiusY, leafBlobGenerationRadiusXZ, leafBlocks, false, blockMap, leafDensity, random);
            } else {
                // 根系深入地下的末端改用 endBlocks（生命红木为孢根）填充，提高孢根产量
                if (endBlocks != blocks) {
                    int from = Math.max(0, stickList.size() - ROOT_END_SEGMENT);
                    List<Vector3f> rootEndSegment = stickList.subList(from, stickList.size());
                    lineSet(rootEndSegment, endRadius, endRadius, endBlocks, true, blockMap);
                    // 再用 blocks（生命红木）把靠树干一侧的孢根包起来，只留最末端裸露；孢根数量不变
                    int wrapCount = Mth.clamp(rootEndWrapCount, 0, rootEndSegment.size() - 1);
                    BlockPos.MutableBlockPos posCheck = new BlockPos.MutableBlockPos();
                    for (int i = 0; i < wrapCount; i++) {
                        BlockPos center = LibMathUtils.fromVector3f(rootEndSegment.get(i));
                        ball8(posCheck, false, 1, 0, 0, blocks, center, blockMap);
                        ball8(posCheck, false, 0, 1, 0, blocks, center, blockMap);
                        ball8(posCheck, false, 0, 0, 1, blocks, center, blockMap);
                    }
                }
                rootEnd = PortListExtension.getLast(stickList);
                ball(endRadius, LibMathUtils.fromVector3f(rootEnd), endBlocks, true, blockMap);
            }
        }
    }
}
