package org.confluence.mod.common.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.confluence.mod.common.block.natural.spreadable.ISpreadable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RefillBiomeHelper {
    public static Map<ChunkPos, Set<BlockPos>> map = Map.of();

    public static void start(MinecraftServer server) {
        ServerLevel overworld = server.overworld();
        BlockPos startPos = server.getWorldData().overworldData().getSpawnPos().atY(overworld.getMinBuildHeight());
        int height = overworld.getMaxBuildHeight() - overworld.getMinBuildHeight(), startRadius = 32, thickness = 64;
        map = conicalCylinder(startPos, height, startRadius, startRadius + height, thickness);

//        Iterator<Map.Entry<ChunkPos, Set<BlockPos>>> iterator = map.entrySet().iterator();
//        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
//
//        while (iterator.hasNext()) {
//            Map.Entry<ChunkPos, Set<BlockPos>> entry = iterator.next();
//            final ChunkPos chunkPos = entry.getKey();
//            final Set<BlockPos> blockPosSet = entry.getValue();
//
//            future = future.thenRunAsync(() -> {
//                boolean noForceBefore = !overworld.getForcedChunks().contains(chunkPos.toLong());
//                if (noForceBefore) overworld.setChunkForced(chunkPos.x, chunkPos.z, true);
//                refill(overworld, chunkPos, blockPosSet, evil);
//                if (noForceBefore) overworld.setChunkForced(chunkPos.x, chunkPos.z, false);
//                System.out.println("succeed");
//            }, Util.backgroundExecutor());
//
//            iterator.remove();
//        }
//
//        future.thenAccept(v -> {
//            System.out.println("completed");
//        });
    }

    public static boolean refill(ServerLevel overworld, ChunkPos chunkPos, Set<BlockPos> set) {
        ChunkAccess chunkAccess = overworld.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, false);
        if (chunkAccess == null) return false;
        Map<Block, Block> blockMap = ISpreadable.Type.HALLOW.getBlockMap();
        for (BlockPos blockPos : set) {
            Block block = blockMap.get(chunkAccess.getBlockState(blockPos).getBlock());
            if (block != null) {
                chunkAccess.setBlockState(blockPos, block.defaultBlockState(), false);
            }
        }
        return true;
    }

    private static Map<ChunkPos, Set<BlockPos>> conicalCylinder(BlockPos startPos, int height, int startRadius, int endRadius, int thickness) {
        float deltaRadius = (endRadius - startRadius) / (float) height;
        float currentRadius = startRadius;
        Map<ChunkPos, Set<BlockPos>> map = new HashMap<>();
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int y = 0; y < height; y++) {
            mutable.setY(y + startPos.getY());
            float startDiameter = (currentRadius + thickness) * 2 + 1;
            float currentOuterSqr = currentRadius * currentRadius;
            float currentInnerSqr = Mth.square(currentRadius - thickness);
            for (int x = 0; x < startDiameter; x++) {
                mutable.setX((int) (x - currentRadius + startPos.getX()));
                int cacheXSqr = (int) Mth.square(x - currentRadius);
                for (int z = 0; z < startDiameter; z++) {
                    mutable.setZ((int) (z - currentRadius + startPos.getZ()));
                    int cacheSqr = (int) (Mth.square(z - currentRadius) + cacheXSqr);
                    if (cacheSqr < currentOuterSqr && cacheSqr > currentInnerSqr) {
                        BlockPos blockPos = mutable.immutable();
                        map.computeIfAbsent(new ChunkPos(blockPos), pos -> new HashSet<>()).add(blockPos);
                    }
                }
            }
            currentRadius += deltaRadius;
        }
        return map;
    }
}
