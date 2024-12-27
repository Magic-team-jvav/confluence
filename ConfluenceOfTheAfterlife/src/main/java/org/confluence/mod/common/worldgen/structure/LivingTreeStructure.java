package org.confluence.mod.common.worldgen.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.structures.EndCityPieces;
import org.confluence.mod.common.init.ModStructures;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class LivingTreeStructure extends Structure {
    public static final MapCodec<LivingTreeStructure> CODEC = simpleCodec(LivingTreeStructure::new);
    public static final int DIST_SQR = 700 * 700;

    public LivingTreeStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected @NotNull Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        Rotation rotation = Rotation.getRandom(context.random());
        BlockPos blockPos = getLowestYIn5by5BoxOffset7Blocks(context, rotation);
        if (blockPos.distSqr(Vec3i.ZERO) > DIST_SQR) {
            return Optional.of(new GenerationStub(blockPos, builder -> generatePieces(builder, blockPos, rotation, context)));
        }
        return Optional.empty();
    }

    private void generatePieces(StructurePiecesBuilder builder, BlockPos startPos, Rotation rotation, Structure.GenerationContext context) {
        List<StructurePiece> list = Lists.newArrayList();
        EndCityPieces.startHouseTower(context.structureTemplateManager(), startPos, rotation, list, context.random());
        list.forEach(builder::addPiece);
    }

    @Override
    public @NotNull StructureType<?> type() {
        return ModStructures.LIVING_TREE.get();
    }

    public static class TrunkPiece extends StructurePiece {
        public TrunkPiece(StructurePieceType type, int genDepth, BoundingBox boundingBox) {
            super(type, genDepth, boundingBox);
        }

        public TrunkPiece(StructurePieceSerializationContext context, CompoundTag tag) {
            super(ModStructures.LIVING_TREE_TRUNK.get(), tag);

        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {

        }

        @Override
        public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator, RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos pos) {

        }
    }
}
