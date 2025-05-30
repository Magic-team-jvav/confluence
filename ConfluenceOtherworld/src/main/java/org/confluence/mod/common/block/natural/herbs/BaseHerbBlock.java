package org.confluence.mod.common.block.natural.herbs;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author voila1106
 */
public abstract class BaseHerbBlock extends CropBlock implements EntityBlock {
    public static final int MAX_AGE = BlockStateProperties.MAX_AGE_2;
    public static final int BRIGHTNESS = 3;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_2;
    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D)};

    public static final Supplier<Map<Block, Set<Block>>> HERB_GROUND_MAP = Suppliers.memoize(() -> new ImmutableMap.Builder<Block, Set<Block>>()
            .put(ModBlocks.DAYBLOOM.get(), Set.of(Blocks.GRASS_BLOCK, NatureBlocks.HALLOW_GRASS_BLOCK.get()))
            .put(ModBlocks.MOONGLOW.get(), Set.of(Blocks.GRASS_BLOCK, Blocks.MOSS_BLOCK, NatureBlocks.JUNGLE_GRASS_BLOCK.get()))
            .put(NatureBlocks.STELLAR_BLOSSOM.get(), Set.of(NatureBlocks.CLOUD_BLOCK.get(), NatureBlocks.RAIN_CLOUD_BLOCK.get()))
            .put(ModBlocks.SHIVERTHORN.get(), Set.of(Blocks.GRASS_BLOCK, Blocks.ICE, NatureBlocks.RED_ICE.get(), NatureBlocks.RED_PACKED_ICE.get(), NatureBlocks.PINK_PACKED_ICE.get(), NatureBlocks.PINK_ICE.get(), NatureBlocks.PURPLE_ICE.get(), NatureBlocks.PURPLE_PACKED_ICE.get()))
            .put(ModBlocks.BLINKROOT.get(), Set.of(Blocks.DIRT, Blocks.MUD, Blocks.STONE, Blocks.DEEPSLATE))
            .put(ModBlocks.DEATHWEED.get(), Set.of(NatureBlocks.CORRUPT_GRASS_BLOCK.get(), NatureBlocks.EBONSTONE.get(), NatureBlocks.TR_CRIMSON_GRASS_BLOCK.get(), NatureBlocks.CRIMSTONE.get()))
            .put(ModBlocks.WATERLEAF.get(), Set.of(Blocks.SAND, Blocks.RED_SAND, NatureBlocks.PEARLSAND.get()))
            .put(ModBlocks.FIREBLOSSOM.get(), Set.of(NatureBlocks.ASH_BLOCK.get(), NatureBlocks.ASH_GRASS_BLOCK.get()))
            .build());

    public BaseHerbBlock() {
        super(Properties.ofFullCopy(Blocks.DANDELION).randomTicks());
    }

    public BaseHerbBlock(Properties prop) {
        super(prop);
    }

    @Override
    public boolean mayPlaceOn(BlockState groundState, BlockGetter worldIn, BlockPos pos) {
        Set<Block> blocks = HERB_GROUND_MAP.get().get(this);
        return blocks != null && blocks.contains(groundState.getBlock());
    }

    public boolean canBloom(ServerLevel world, BlockState state) {
        return false;
    }

    // 重写，不检查光照，不检查合理密植，抄父方法
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light
        int i = this.getAge(state);
        if (i < this.getMaxAge()) {
            float growthSpeed = 0.7f;
            if (net.neoforged.neoforge.common.CommonHooks.canCropGrow(level, pos, state, random.nextInt((int) (25.0F / growthSpeed) + 1) == 0)) {
                level.setBlock(pos, this.getStateForAge(i + 1), 2);
                net.neoforged.neoforge.common.CommonHooks.fireCropGrowPost(level, pos, state);
            }
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader pLevel, BlockPos pPos, BlockState pState) {
        return false;
    }

    @Override
    public boolean canSurvive(BlockState blockstate, LevelReader worldIn, BlockPos pos) {
        BlockPos blockpos = pos.below();
        BlockState groundState = worldIn.getBlockState(blockpos);
        return this.mayPlaceOn(groundState, worldIn, blockpos);
    }

    @Override
    @NotNull
    protected ItemLike getBaseSeedId() {
        return Items.AIR;
    }

    @NotNull
    protected IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(AGE);
    }

    public int getMaxAge() {
        return MAX_AGE;
    }

    @NotNull
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE_BY_AGE[this.getAge(pState)];
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new Entity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide ? null : LibUtils.getTicker(pBlockEntityType, ModBlocks.HERBS_ENTITY.get(), (level, blockPos, blockState, e) -> {
            if (level.getGameTime() % 20 == 2) { // 每秒判断能不能开花
                int age = getAge(blockState);
                if (age < MAX_AGE - 1) return;
                if (canBloom((ServerLevel) level, blockState)) {
                    if (age != MAX_AGE) {
                        level.setBlockAndUpdate(blockPos, blockState.setValue(AGE, MAX_AGE));
                    }
                } else if (age == MAX_AGE) { // 如果不能开花且已经开花
                    level.setBlockAndUpdate(blockPos, blockState.setValue(AGE, MAX_AGE - 1));
                }
            }
        });
    }

    public static class Entity extends BlockEntity {
        public Entity(BlockPos pPos, BlockState pBlockState) {
            super(ModBlocks.HERBS_ENTITY.get(), pPos, pBlockState);
        }
    }
}
