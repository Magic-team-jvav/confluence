package org.confluence.mod.common.block.functional;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.common.block.HorizontalDirectionalWithVerticalFourPartBlock;
import org.confluence.lib.common.block.StateProperties;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BewitchingTableBlock extends HorizontalDirectionalWithVerticalFourPartBlock implements EntityBlock {
    public static final MapCodec<BewitchingTableBlock> CODEC = simpleCodec(BewitchingTableBlock::new);
    private static final VoxelShape BASE_SHAPE_SOUTH = Shapes.or(box(5, 3, 2, 16, 12, 14), box(2, 0, 1, 16, 3, 15), box(0, 12, 0, 16, 16, 16));
    private static final VoxelShape BASE_SHAPE_WEST = Shapes.or(box(2, 3, 5, 14, 12, 16), box(1, 0, 2, 15, 3, 16), box(0, 12, 0, 16, 16, 16));
    private static final VoxelShape BASE_SHAPE_NORTH = Shapes.or(box(0, 3, 2, 11, 12, 14), box(0, 0, 1, 14, 3, 15), box(0, 12, 0, 16, 16, 16));
    private static final VoxelShape BASE_SHAPE_EAST = Shapes.or(box(2, 3, 0, 14, 12, 11), box(1, 0, 0, 15, 3, 14), box(0, 12, 0, 16, 16, 16));
    private static final VoxelShape RIGHT_SHAPE_SOUTH = Shapes.or(box(0, 3, 2, 11, 12, 14), box(0, 0, 1, 14, 3, 15), box(0, 12, 0, 16, 16, 16));
    private static final VoxelShape RIGHT_SHAPE_WEST = Shapes.or(box(2, 3, 0, 14, 12, 11), box(1, 0, 0, 15, 3, 14), box(0, 12, 0, 16, 16, 16));
    private static final VoxelShape RIGHT_SHAPE_NORTH = Shapes.or(box(5, 3, 2, 16, 12, 14), box(2, 0, 1, 16, 3, 15), box(0, 12, 0, 16, 16, 16));
    private static final VoxelShape RIGHT_SHAPE_EAST = Shapes.or(box(2, 3, 5, 14, 12, 16), box(1, 0, 2, 15, 3, 16), box(0, 12, 0, 16, 16, 16));
    private static final VoxelShape UP_SHAPE_SOUTH = box(0, 0, 0, 16, 0.02, 16);
    private static final VoxelShape UP_SHAPE_WEST = box(0, 0, 0, 16, 0.02, 16);
    private static final VoxelShape UP_SHAPE_NORTH = box(0, 0, 0, 16, 0.02, 16);
    private static final VoxelShape UP_SHAPE_EAST = box(0, 0, 0, 16, 0.02, 16);
    private static final VoxelShape RIGHT_UP_SHAPE_SOUTH = box(0, 0, 0, 16, 0.02, 16);
    private static final VoxelShape RIGHT_UP_SHAPE_WEST = box(0, 0, 0, 16, 0.02, 16);
    private static final VoxelShape RIGHT_UP_SHAPE_NORTH = box(0, 0, 0, 16, 0.02, 16);
    private static final VoxelShape RIGHT_UP_SHAPE_EAST = box(0, 0, 0, 16, 0.02, 16);
    private static final VoxelShape[] BASE_SHAPES = new VoxelShape[]{BASE_SHAPE_SOUTH, BASE_SHAPE_WEST, BASE_SHAPE_NORTH, BASE_SHAPE_EAST};
    private static final VoxelShape[] RIGHT_SHAPES = new VoxelShape[]{RIGHT_SHAPE_SOUTH, RIGHT_SHAPE_WEST, RIGHT_SHAPE_NORTH, RIGHT_SHAPE_EAST};
    private static final VoxelShape[] UP_SHAPES = new VoxelShape[]{UP_SHAPE_SOUTH, UP_SHAPE_WEST, UP_SHAPE_NORTH, UP_SHAPE_EAST};
    private static final VoxelShape[] RIGHT_UP_SHAPES = new VoxelShape[]{RIGHT_UP_SHAPE_SOUTH, RIGHT_UP_SHAPE_WEST, RIGHT_UP_SHAPE_NORTH, RIGHT_UP_SHAPE_EAST};

    public BewitchingTableBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(StateProperties.DRIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(StateProperties.DRIVE));
    }

    @Override
    protected MapCodec<BewitchingTableBlock> codec() {
        return CODEC;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide && !state.getValue(StateProperties.DRIVE)) {
            player.addEffect(new MobEffectInstance(ModEffects.BEWITCHED, MobEffectInstance.INFINITE_DURATION));
            StateProperties.VerticalFourPart part = state.getValue(PART);
            BlockPos basePos = part.isBase() ? pos : StateProperties.VerticalFourPart.getRelatives(part, state.getValue(FACING), pos).get(StateProperties.VerticalFourPart.BASE);
            level.setBlockAndUpdate(basePos, state.setValue(StateProperties.DRIVE, true));
            level.scheduleTick(basePos, this, 110);
            return InteractionResult.SUCCESS_NO_ITEM_USED;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getBlockState(pos).getValue(StateProperties.DRIVE)) {
            level.setBlockAndUpdate(pos, state.setValue(StateProperties.DRIVE, false));
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        int index = pState.getValue(FACING).get2DDataValue();
        return switch (pState.getValue(PART)) {
            case BASE -> BASE_SHAPES[index];
            case RIGHT -> RIGHT_SHAPES[index];
            case UP -> UP_SHAPES[index];
            case RIGHT_UP -> RIGHT_UP_SHAPES[index];
        };
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new Entity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    public static class Entity extends BlockEntity implements GeoBlockEntity {
        private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
        public final boolean isBase;

        public Entity(BlockPos pos, BlockState blockState) {
            super(FunctionalBlocks.BEWITCHING_TABLE_ENTITY.get(), pos, blockState);
            this.isBase = blockState.getValue(PART).isBase();
        }

        @Override
        public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
            RawAnimation driving = RawAnimation.begin().thenPlay("driving");
            RawAnimation idling = RawAnimation.begin().thenLoop("idling");
            controllers.add(new AnimationController<>(this, state -> {
                if (state.getAnimatable().getBlockState().getValue(StateProperties.DRIVE)) {
                    if (state.isCurrentAnimation(idling)) {
                        return state.setAndContinue(driving);
                    }
                }
                return state.setAndContinue(idling);
            }));
        }

        @Override
        public AnimatableInstanceCache getAnimatableInstanceCache() {
            return cache;
        }
    }
}
