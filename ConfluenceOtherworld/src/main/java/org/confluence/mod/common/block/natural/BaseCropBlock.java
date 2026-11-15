package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.neoforged.neoforge.common.CommonHooks;
import org.confluence.mod.common.init.block.NatureBlocks;

import java.util.Set;

import static net.neoforged.neoforge.common.CommonHooks.fireCropGrowPost;

public abstract class BaseCropBlock extends CropBlock {
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 7);
    public static final int MAX_AGE = 7;
    /// 空岛作物的统一生长加成：生长速度 +80%。
    public static final float SKYLAND_GROWTH_BONUS = 1.8F;
    /// 种在雨中云（{@code RAIN_CLOUD_BLOCK}）上时的额外加速倍率。
    public static final float RAIN_CLOUD_GROWTH_MULTIPLIER = 3.0F;

    public BaseCropBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected abstract ItemLike getBaseSeedId();

    @Override
    protected IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(AGE);
    }

    public abstract Set<Block> getCanPlaceBlocks();

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return getCanPlaceBlocks().contains(state.getBlock());
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        return mayPlaceOn(belowState, level, below);
    }

    /// 空岛作物的生长速度：原版速度 × 空岛加成，脚下是雨中云时再乘雨中云倍率。
    ///
    /// 星辰花、云织草、飘飘麦与气球瓜茎共用这一处，改数值只改这里。
    public static float getSkylandGrowthSpeed(BlockState state, BlockGetter level, BlockPos pos) {
        float f = getGrowthSpeed(state, level, pos) * SKYLAND_GROWTH_BONUS;
        if (level.getBlockState(pos.below()).is(NatureBlocks.RAIN_CLOUD_BLOCK.get())) {
            f *= RAIN_CLOUD_GROWTH_MULTIPLIER;
        }
        return f;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 3)) return;
        if (level.getRawBrightness(pos, 0) >= 9) {
            int i = getAge(state);
            if (i < getMaxAge()) {
                float f = getSkylandGrowthSpeed(state, level, pos);
                if (CommonHooks.canCropGrow(level, pos, state, random.nextInt((int) (25.0F / f) + 1) == 0)) {
                    level.setBlock(pos, getStateForAge(i + 1), 2);
                    fireCropGrowPost(level, pos, state);
                }
            }
        }
    }
}
