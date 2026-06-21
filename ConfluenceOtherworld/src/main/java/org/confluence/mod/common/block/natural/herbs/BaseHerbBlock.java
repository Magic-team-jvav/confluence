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
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author voila1106
 */
public abstract class BaseHerbBlock extends CropBlock implements EntityBlock {
    public static final int MAX_AGE = BlockStateProperties.MAX_AGE_2;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_2;
    protected static final VoxelShape[] SHAPE_BY_AGE;

    static {
        VoxelShape[] shapes = new VoxelShape[4];
        for (int i = 0; i < 4; i++) {
            shapes[i] = box(0, 0, 0, 16, 2 * (i + 1), 16);
        }
        SHAPE_BY_AGE = shapes;
    }

    public static final Supplier<Map<Block, Set<Block>>> HERB_GROUND_MAP = Suppliers.memoize(() -> new ImmutableMap.Builder<Block, Set<Block>>()
            .put(ModBlocks.DAYBLOOM.get(), Set.of(Blocks.GRASS_BLOCK, NatureBlocks.HALLOW_GRASS_BLOCK.get()))
            .put(ModBlocks.MOONGLOW.get(), Set.of(Blocks.GRASS_BLOCK, Blocks.MOSS_BLOCK, NatureBlocks.JUNGLE_GRASS_BLOCK.get()))
            .put(NatureBlocks.STELLAR_BLOSSOM.get(), Set.of(NatureBlocks.CLOUD_BLOCK.get(), NatureBlocks.RAIN_CLOUD_BLOCK.get()))
            .put(ModBlocks.SHIVERTHORN.get(), Set.of(Blocks.GRASS_BLOCK, Blocks.ICE, NatureBlocks.RED_ICE.get(), NatureBlocks.RED_PACKED_ICE.get(), NatureBlocks.PINK_PACKED_ICE.get(), NatureBlocks.PINK_ICE.get(), NatureBlocks.PURPLE_ICE.get(), NatureBlocks.PURPLE_PACKED_ICE.get()))
            .put(ModBlocks.BLINKROOT.get(), Set.of(Blocks.DIRT, Blocks.MUD, Blocks.STONE, Blocks.DEEPSLATE))
            .put(ModBlocks.DEATHWEED.get(), Set.of(NatureBlocks.CORRUPT_GRASS_BLOCK.get(), NatureBlocks.EBONSTONE.get(), NatureBlocks.CRIMSON_GRASS_BLOCK.get(), NatureBlocks.CRIMSTONE.get()))
            .put(ModBlocks.WATERLEAF.get(), Set.of(Blocks.SAND, Blocks.RED_SAND, NatureBlocks.PEARLSAND.get()))
            .put(ModBlocks.FIREBLOSSOM.get(), Set.of(NatureBlocks.ASH_BLOCK.get(), NatureBlocks.ASH_GRASS_BLOCK.get()))
            .build());

    public BaseHerbBlock() {
        super(Properties.copy(Blocks.DANDELION).randomTicks());
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
        if (!level.isAreaLoaded(pos, 1)) return;
        int i = getAge(state);
        if (i < getMaxAge()) {
            if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(level, pos, state, random.nextInt((int) (25.0F / 0.7F) + 1) == 0)) {
                level.setBlockAndUpdate(pos, getStateForAge(i + 1));
                net.minecraftforge.common.ForgeHooks.onCropsGrowPost(level, pos, state);
            }
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        return false;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockpos = pos.below();
        BlockState groundState = level.getBlockState(blockpos);
        return this.mayPlaceOn(groundState, level, blockpos);
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return Items.AIR;
    }

    protected IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    public int getMaxAge() {
        return MAX_AGE;
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_BY_AGE[this.getAge(state)];
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : LibUtils.getTicker(type, ModBlocks.HERBS_ENTITY.get(), (level1, pos, state1, entity) -> {
            if (level1.getGameTime() % 20 == 2) { // 每秒判断能不能开花
                int age = getAge(state1);
                if (age < MAX_AGE - 1) return;
                if (canBloom((ServerLevel) level1, state1)) {
                    if (age != MAX_AGE) {
                        level1.setBlockAndUpdate(pos, state1.setValue(AGE, MAX_AGE));
                    }
                } else if (age == MAX_AGE) { // 如果不能开花且已经开花
                    level1.setBlockAndUpdate(pos, state1.setValue(AGE, MAX_AGE - 1));
                }
            }
        });
    }

    public static class BEntity extends BlockEntity {
        public BEntity(BlockPos pos, BlockState state) {
            super(ModBlocks.HERBS_ENTITY.get(), pos, state);
        }
    }
}
