package org.confluence.mod.common.block.natural;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.init.block.NatureBlocks;

public class MycelialDirtBlock extends Block implements BonemealableBlock {
    public static final MapCodec<MycelialDirtBlock> CODEC = simpleCodec(MycelialDirtBlock::new);

    public MycelialDirtBlock(Properties properties) {
        super(properties);
    }

    public MapCodec<MycelialDirtBlock> codec() {
        return CODEC;
    }

    public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos pos, BlockState state) {
        return levelReader.getBlockState(pos.below()).isAir();
    }

    public boolean isBonemealSuccess(Level level, RandomSource randomSource, BlockPos pos, BlockState state) {
        return true;
    }

    public void performBonemeal(ServerLevel serverLevel, RandomSource randomSource, BlockPos pos, BlockState state) {
        serverLevel.setBlockAndUpdate(pos.below(), NatureBlocks.HANGING_MYCELIUM.get().defaultBlockState());
    }

    public BlockPos getParticlePos(BlockPos pos) {
        return pos.below();
    }
}
