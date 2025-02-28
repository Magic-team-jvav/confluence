package org.confluence.mod.common.worldgen.structure;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import org.confluence.mod.common.init.ModStructures;

import java.util.Optional;

public class LivingTreeStructure extends Structure {
    public static final MapCodec<LivingTreeStructure> CODEC = simpleCodec(LivingTreeStructure::new);
    public static final int DIST_SQR = 700 * 700;

    public LivingTreeStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        Rotation rotation = Rotation.getRandom(context.random());
        BlockPos blockPos = getLowestYIn5by5BoxOffset7Blocks(context, rotation);
        if (blockPos.distSqr(Vec3i.ZERO) > DIST_SQR) {
            return Optional.of(new GenerationStub(blockPos, builder -> {
                // todo
            }));
        }
        return Optional.empty();
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.LIVING_TREE.get();
    }
}
