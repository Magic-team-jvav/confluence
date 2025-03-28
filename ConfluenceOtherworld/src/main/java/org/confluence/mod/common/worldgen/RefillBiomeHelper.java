package org.confluence.mod.common.worldgen;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import org.confluence.mod.util.VectorUtils;
import org.joml.Vector3d;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.confluence.mod.util.StructureUtils.getDistanceToLineSegment;
import static org.confluence.mod.util.StructureUtils.isProjectionBetweenPoints;

public class RefillBiomeHelper {
    public static void start(MinecraftServer server) {
        ServerLevel overworld = server.overworld();
        BlockPos startPos = server.getWorldData().overworldData().getSpawnPos().atY(overworld.getMinBuildHeight());
        ChunkPos startChunk = new ChunkPos(startPos);
        int height = overworld.getMaxBuildHeight() - overworld.getMinBuildHeight();
        Vector3d vector3d = VectorUtils.toVector3d(startPos);
        for (Vector3d pos : frustumSetPos(vector3d, new Vector3d(vector3d).add(0, height, 0), 1, height)) {
            BlockPos blockPos = VectorUtils.fromVector3d(pos);
        }
        CompletableFuture.runAsync(() -> {

        }, Util.backgroundExecutor()).thenAccept(v -> {

        });
    }

    private static List<Vector3d> frustumSetPos(Vector3d startPos, Vector3d endPos, double startRadius, double endRadius) {
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

        Vector3d pointP = new Vector3d();
        double length = Math.sqrt(Mth.square(endPos.x - startPos.x) + Mth.square(endPos.y - startPos.y) + Mth.square(endPos.z - startPos.z));
        double lengthGet;
        double lengthP;
        double x2;
        double y2;

        List<Vector3d> list = new LinkedList<>();

        for (int x = setStartX; x <= setEndX; x++) {
            x2 = Mth.square(endPos.x - x);
            pointP.x = x;
            for (int y = setStartY; y <= setEndY; y++) {
                y2 = Mth.square(endPos.y - y) + x2;
                pointP.y = y;
                for (int z = setStartZ; z <= setEndZ; z++) {
                    pointP.z = z;
                    if (!isProjectionBetweenPoints(startPos, endPos, pointP)) continue;
                    lengthGet = Math.sqrt(y2 + Mth.square(endPos.z - z));
                    lengthP = lengthGet / length;
                    if (getDistanceToLineSegment(startPos, endPos, pointP) <= (startRadius * lengthP + endRadius * (1.0D - lengthP))) {
                        list.add(new Vector3d(pointP));
                    }
                }
            }
        }
        return list;
    }
}
