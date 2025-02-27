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
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.ScatteredFeaturePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.material.FluidState;
import org.confluence.mod.common.init.ModStructures;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.util.HomingUtils;
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

            HillPiece hillPiece = new HillPiece(chunkPos, lowestY, Util.getRandom(ModUtils.HORIZONTAL, random));
            builder.addPiece(hillPiece);
        });
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.CRIMSON_CAVE.get();
    }

    public static class HillPiece extends ScatteredFeaturePiece {
        private final ChunkPos startPos;
        private BlockPos tunnelEndPos;
        private Direction tunnelEndFace;

        public HillPiece(ChunkPos startPos, int y, Direction orientation) {
            super(ModStructures.CRIMSON_CAVE_HILL.get(), startPos.getMinBlockX(), y, startPos.getMinBlockZ(), 64, 24, 64, orientation);
            this.startPos = startPos;
        }

        public HillPiece(CompoundTag tag) {
            super(ModStructures.CRIMSON_CAVE_HILL.get(), tag);

            this.startPos = new ChunkPos(tag.getLong("StartPos"));
            this.tunnelEndPos = NbtUtils.readBlockPos(tag, "TunnelEndPos").orElseThrow();
            this.tunnelEndFace = Direction.byName(tag.getString("TunnelEndFace"));
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
            super.addAdditionalSaveData(context, tag);

            tag.putLong("StartPos", startPos.toLong());
            tag.put("TunnelEndPos", NbtUtils.writeBlockPos(tunnelEndPos));
            tag.putString("TunnelEndFace", tunnelEndFace.getSerializedName());
        }

        @Override
        public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator, RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos pos) {
            WorldgenRandom worldgenRandom = new WorldgenRandom(new LegacyRandomSource(20250225L));
            worldgenRandom.setLargeFeatureSeed(level.getSeed(), startPos.x, startPos.z);
            random = worldgenRandom;
            pos = startPos.getMiddleBlockPosition(pos.getY());

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
                                    if (level.setBlock(mutable, air, 2)) {
                                        FluidState fluidstate = level.getFluidState(mutable);
                                        if (!fluidstate.isEmpty()) {
                                            level.scheduleTick(mutable, fluidstate.getType(), 0);
                                        }
                                    } else break;
                                } else {
                                    if (!level.setBlock(mutable, stone, 2)) break;
                                }
                            }
                        }
                    }
                }
            }

            BlockPos tunnelStart = pos.offset(random.nextInt(radius) - radius / 2, -radius + 4, random.nextInt(radius) - radius / 2);
            List<Vector3d> tunnelNodes = Lists.newArrayList(HomingUtils.toVector3d(tunnelStart));
            int depth = pos.getY() / 16 - 1;
            if (depth % 2 == 0) depth++;
            int stepX = random.nextBoolean() ? -7 : 7;
            int stepZ = random.nextBoolean() ? -7 : 7;
            for (int i = 1; i < depth; i++) {
                tunnelStart = tunnelStart.offset(i * stepX, -16, i * stepZ);
                tunnelNodes.add(HomingUtils.toVector3d(tunnelStart));
                stepX = -stepX;
                stepZ = -stepZ;
            }
            tunnelNodes.add(HomingUtils.toVector3d(startPos.getMiddleBlockPosition(0)));
            ModUtils.lightningPathList(tunnelNodes, 2.5, 8, random);
            Vector3d delta = tunnelNodes.getLast().sub(tunnelNodes.get(tunnelNodes.size() - 2));
            this.tunnelEndFace = Direction.getNearest(delta.x, delta.y, delta.z);
            List<BlockPos> list = Lists.newArrayList(tunnelNodes.stream().map(HomingUtils::fromVector3d).toArray(BlockPos[]::new));
            list.removeLast();
            for (BlockPos nodePos : list) {
                for (BlockPos blockPos : BlockPos.betweenClosed(nodePos.offset(-3, -2, -3), nodePos.offset(3, 2, 3))) {
                    if (!level.setBlock(blockPos, stone, 2)) break;
                }
            }
            for (BlockPos nodePos : list) {
                for (BlockPos blockPos : BlockPos.betweenClosed(nodePos.offset(-2, -1, -2), nodePos.offset(2, 2, 2))) {
                    if (!level.setBlock(blockPos, air, 2)) break;
                }
            }

            this.tunnelEndPos = list.getLast().relative(tunnelEndFace, 2);

            BlockState blockState = Blocks.AIR.defaultBlockState();
            for (int i = -9; i < 55; i++) {
                for (int j = -8; j < 40; j++) {
                    for (int k = -9; k < 55; k++) {
                        BlockPos offset = tunnelEndPos.offset(i, -j, k);
                        if (!level.setBlock(offset, blockState, 2)) break;
                    }
                }
            }
        }
    }
}
