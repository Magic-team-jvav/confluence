package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.block.natural.spreadable.ISpreadable;
import org.confluence.mod.common.init.ModFeatures;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.jetbrains.annotations.NotNull;

public class MushroomBlock extends BasePlantBlock implements ISpreadable, BonemealableBlock {
    private static final VoxelShape SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);
    public ISpreadable.Type type;

    public MushroomBlock(ISpreadable.Type type, Block... surviveBlock) {
        super(surviveBlock);
        this.type = type;
    }

    @Override
    @NotNull
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Vec3 vec3 = pState.getOffset(pLevel, pPos);
        return SHAPE.move(vec3.x, vec3.y, vec3.z);
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        if (state.is(NatureBlocks.GLOWING_MUSHROOM)) {
            return 7;
        }
        return 0;
    }


    @Override
    public ISpreadable.Type getSpreadType() {
        return type;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return state.is(NatureBlocks.GLOWING_MUSHROOM);
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        if (state.is(NatureBlocks.GLOWING_MUSHROOM)) return (double) random.nextFloat() < 0.4;
        return false;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        level.registryAccess().holder(ModFeatures.Configured.GLOWING_MUSHROOM_TREE).ifPresent(holder ->
                holder.value().place(level, level.getChunkSource().getGenerator(), random, pos)
        );
    }
}
