package org.confluence.mod.common.worldgen.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.material.FluidState;
import org.confluence.mod.common.init.ModStructures;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.util.ModUtils;
import org.joml.Vector3d;

import java.util.List;
import java.util.Optional;

public class CrimsonCaveStructure extends Structure {
    public static final MapCodec<CrimsonCaveStructure> CODEC = simpleCodec(CrimsonCaveStructure::new);

    protected CrimsonCaveStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        int lowestY = getLowestY(context, 16, 16);
        if (lowestY < context.chunkGenerator().getSeaLevel() - 16) {
            return Optional.empty();
        }
        return onTopOfChunkCenter(context, Heightmap.Types.WORLD_SURFACE_WG, builder -> {
            ChunkPos chunkPos = context.chunkPos();
            WorldgenRandom random = context.random();

            HillPiece hillPiece = new HillPiece(chunkPos.getMinBlockX(), lowestY, chunkPos.getMinBlockZ(), Util.getRandom(ModUtils.HORIZONTAL, random));
            builder.addPiece(hillPiece);

            builder.addPiece(new PalmPiece(hillPiece));
        });
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.CRIMSON_CAVE.get();
    }

    public static class HillPiece extends ScatteredFeaturePiece {
        private BlockPos tunnelEndPos;
        private Direction tunnelEndFace;
        private boolean placed = false;

        // [width, height, depth] -> [offsetX, offsetY, offsetZ]
        public HillPiece(int x, int y, int z, Direction orientation) {
            super(ModStructures.CRIMSON_CAVE_HILL.get(), x, y, z, 24, 24, 24, orientation);
        }

        public HillPiece(CompoundTag tag) {
            super(ModStructures.CRIMSON_CAVE_HILL.get(), tag);
            this.placed = tag.getBoolean("Placed");

            this.tunnelEndPos = NbtUtils.readBlockPos(tag, "TunnelEndPos").orElseThrow();
            this.tunnelEndFace = Direction.byName(tag.getString("TunnelEndFace"));
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
            super.addAdditionalSaveData(context, tag);
            tag.putBoolean("Placed", placed);

            tag.put("TunnelEndPos", NbtUtils.writeBlockPos(tunnelEndPos));
            tag.putString("TunnelEndFace", tunnelEndFace.getSerializedName());
        }

        @Override
        public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator, RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos pos) {
            if (placed) return;
            this.placed = true;

            int radius = random.nextInt(12, 16);
            int diameter = radius * 2 + 1;
            int airRadius = radius - 4;
            BlockState stone = NatureBlocks.TR_CRIMSON_STONE.get().defaultBlockState();
            BlockState air = Blocks.AIR.defaultBlockState();
            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

            int radiusSqr = radius * radius;
            int airRadiusSqr = airRadius * airRadius;
            for (int i = 0; i < diameter; i++) {
                int x = i - radius;
                for (int j = 0; j < diameter; j++) {
                    int y = j - radius;
                    for (int k = 0; k < diameter; k++) {
                        int z = k - radius;
                        int sqr = x * x + y * y + z * z;
                        if (sqr <= radiusSqr) {
                            BlockState blockState = level.getBlockState(mutable.setWithOffset(pos, x, y, z));
                            if (isReplaceableByStructures(blockState) || blockState.is(BlockTags.OVERWORLD_CARVER_REPLACEABLES)) {
                                if (sqr < airRadiusSqr) {
                                    level.setBlock(mutable, air, 2);
                                    FluidState fluidstate = level.getFluidState(mutable);
                                    if (!fluidstate.isEmpty()) {
                                        level.scheduleTick(mutable, fluidstate.getType(), 0);
                                    }
                                } else {
                                    level.setBlock(mutable, stone, 2);
                                }
                            }
                        }
                    }
                }
            }

            BlockPos tunnelStart = pos.offset(random.nextInt(radius) - radius / 2, -radius + 4, random.nextInt(radius) - radius / 2);
            List<Vector3d> tunnelNodes = Lists.newArrayList(ModUtils.toVector3d(tunnelStart));
            int depth = pos.getY() / 16;
            if (depth % 2 == 0) depth++;
            int stepX = random.nextBoolean() ? -7 : 7;
            int stepZ = random.nextBoolean() ? -7 : 7;
            for (int i = 1; i < depth; i++) {
                tunnelStart = tunnelStart.offset(i * stepX, -16, i * stepZ);
                tunnelNodes.add(ModUtils.toVector3d(tunnelStart));
                stepX = -stepX;
                stepZ = -stepZ;
            }
            ModUtils.lightningPathList(tunnelNodes, 2.5, 8, random);
            Vector3d delta = tunnelNodes.getLast().sub(tunnelNodes.get(tunnelNodes.size() - 2));
            this.tunnelEndFace = Direction.getNearest(delta.x, delta.y, delta.z);
            List<BlockPos> list = Lists.newArrayList(tunnelNodes.stream().map(ModUtils::fromVector3d).toArray(BlockPos[]::new));
            list.removeLast();
            for (BlockPos nodePos : list) {
                for (BlockPos blockPos : BlockPos.betweenClosed(nodePos.offset(-3, -2, -3), nodePos.offset(3, 2, 3))) {
                    level.setBlock(blockPos, stone, 2);
                }
            }
            for (BlockPos nodePos : list) {
                for (BlockPos blockPos : BlockPos.betweenClosed(nodePos.offset(-2, -1, -2), nodePos.offset(2, 2, 2))) {
                    level.setBlock(blockPos, air, 2);
                }
            }

            this.tunnelEndPos = list.getLast().relative(tunnelEndFace, 2);
        }
    }

    public static class PalmPiece extends StructurePiece {
        private HillPiece hillPiece;
        private boolean placed = false;

        public PalmPiece(HillPiece hillPiece) {
            super(ModStructures.CRIMSON_CAVE_PALM.get(), 0, hillPiece.getBoundingBox().inflatedBy(32));
            this.hillPiece = hillPiece;
        }

        public PalmPiece(CompoundTag tag) {
            super(ModStructures.CRIMSON_CAVE_PALM.get(), tag);
            this.placed = tag.getBoolean("Placed");
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
            tag.putBoolean("Placed", placed);
        }

        @Override
        public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator, RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos pos) {
            if (placed || hillPiece == null || hillPiece.tunnelEndPos == null || hillPiece.tunnelEndFace == null) return;
            this.placed = true;

//            BlockState blockState = Blocks.AIR.defaultBlockState();
//            for (int i = -63; i < 64; i++) {
//                for (int k = -63; k < 64; k++) {
//                    BlockPos offset = hillPiece.tunnelEndPos.offset(i, 0, k);
//                    level.setBlock(offset, blockState, 2);
//                }
//            }
        }
    }
}
