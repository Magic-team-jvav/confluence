package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StemGrownBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import org.confluence.lib.common.block.SupStemBlock;
import org.confluence.mod.common.init.block.NatureBlocks;

import java.util.function.Supplier;

public class BalloonStemBlock extends SupStemBlock {
    public BalloonStemBlock(Supplier<? extends StemGrownBlock> fruitSup, Supplier<Item> seedSupplier) {
        super(fruitSup, seedSupplier,
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.PLANT)
                        .noCollission()
                        .randomTicks()
                        .instabreak()
                        .sound(SoundType.HARD_CROP)
                        .pushReaction(PushReaction.DESTROY));
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos belowPos = pos.below();
        return this.mayPlaceOn(level.getBlockState(belowPos), level, belowPos);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(NatureBlocks.CLOUD_BLOCK.get()) || state.is(NatureBlocks.RAIN_CLOUD_BLOCK.get());
    }
}
