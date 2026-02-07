package org.confluence.mod.common.block.natural;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.jetbrains.annotations.Nullable;

public class MycelialDirtBlock extends Block implements BonemealableBlock {
    public static final MapCodec<MycelialDirtBlock> CODEC = simpleCodec(MycelialDirtBlock::new);

    public MycelialDirtBlock(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<MycelialDirtBlock> codec() {
        return CODEC;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos pos, BlockState state) {
        return levelReader.getBlockState(pos.below()).isAir();
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource randomSource, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource randomSource, BlockPos pos, BlockState state) {
        serverLevel.setBlockAndUpdate(pos.below(), NatureBlocks.HANGING_MYCELIUM.get().defaultBlockState());
    }

    @Override
    public BlockPos getParticlePos(BlockPos pos) {
        return pos.below();
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        Level level = context.getLevel();
        if (itemAbility == ItemAbilities.HOE_TILL) {
            if (!simulate) {
                Block.popResource(level, context.getClickedPos(), NatureBlocks.HANGING_MYCELIUM.toStack());
                level.playSound(context.getPlayer(), context.getClickedPos(), SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return Blocks.DIRT.defaultBlockState();
        }
        return null;
    }
}
