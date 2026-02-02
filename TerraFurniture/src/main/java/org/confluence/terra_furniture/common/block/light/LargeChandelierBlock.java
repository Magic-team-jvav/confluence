package org.confluence.terra_furniture.common.block.light;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.common.block.HorizontalDirectionalWithHorizontalTenPartBlock;
import org.confluence.lib.common.block.StateProperties;
import org.confluence.terra_furniture.api.client.model.CacheItemRefBlockModel;
import org.confluence.terra_furniture.api.client.renderer.item.BaseGeoItemRendererProvider;
import org.confluence.terra_furniture.common.block.func.be.BaseSwayingBE;
import org.confluence.terra_furniture.common.init.TFBlocks;
import org.confluence.terra_furniture.network.s2c.PlayerCrossDeltaS2C;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * 庞大的多方块吊灯
 */
public class LargeChandelierBlock extends HorizontalDirectionalWithHorizontalTenPartBlock implements EntityBlock, SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public LargeChandelierBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(PART, StateProperties.HorizontalTenPart.UP).setValue(FACING, Direction.NORTH).setValue(BlockStateProperties.WATERLOGGED, Boolean.FALSE).setValue(LIT, Boolean.TRUE));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        BlockPos base = state.getValue(PART).toBase(pos, state.getValue(FACING), false);
        if (level.isClientSide && level.getBlockEntity(base) instanceof BEntity blockEntity) {
            blockEntity.applyMovingAffectedDelta(entity.getDeltaMovement());
        } else if (!level.isClientSide && entity instanceof Player) {
            PacketDistributor.sendToPlayersTrackingEntity(entity, new PlayerCrossDeltaS2C(entity.getPosition(1).subtract(entity.getPosition(0)), base));
        }
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() && state.getValue(PART).equals(StateProperties.HorizontalTenPart.UP) ? (level1, pos, blockState, t) -> {
            if (t instanceof BEntity blockEntity) {
                blockEntity.tickAtClient();
            }
        } : null;
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED, LIT);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState resultState = super.getStateForPlacement(context);
        if (resultState != null) {
            LevelAccessor levelaccessor = context.getLevel();
            BlockPos blockpos = context.getClickedPos();
            return resultState
                .setValue(WATERLOGGED, levelaccessor.getFluidState(blockpos).getType() == Fluids.WATER)
                .setValue(LIT, true);
        }
        return null;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        if (blockState.getValue(PART).equals(StateProperties.HorizontalTenPart.UP)) return new BEntity(blockPos, blockState);
        else return null;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        if (!state.getValue(PART).equals(StateProperties.HorizontalTenPart.UP) || neighborBlock instanceof LargeChandelierBlock) return;
        if (level instanceof ServerLevel serverlevel) {
            this.checkAndFlip(state, serverlevel, pos);
        }
    }
    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public void checkAndFlip(BlockState state, ServerLevel level, BlockPos pos) {
        boolean flag = level.hasNeighborSignal(pos);
        if (flag) {
            level.playSound(
                null, pos, !state.getValue(LIT) ? SoundEvents.COPPER_BULB_TURN_ON : SoundEvents.COPPER_BULB_TURN_OFF, SoundSource.BLOCKS
            );
            StateProperties.HorizontalTenPart.getAllExcept(state.getValue(FACING), pos, null)
                    .forEach((horizontalTenPart, pos1) -> level.setBlock(pos1, level.getBlockState(pos1).cycle(LIT), 19));
        }
    }

    @Override
    protected @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction direction, @NotNull BlockState neighborState, @NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos neighborPos) {
        if (direction == Direction.UP && !this.canSurvive(state, level, pos)) return Blocks.AIR.defaultBlockState();
        if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return true;
    }

    public static final class BEntity extends BaseSwayingBE implements GeoBlockEntity {
        private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
        private static final Vec3 ANCHOR = new Vec3(0.5, 1, 0.5);

        public BEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
            super(type, pos, blockState);
        }

        public BEntity(BlockPos pos, BlockState state) {
            super(TFBlocks.LARGE_CHANDELIER_ENTITY.get(), pos, state);
        }

        @Override
        public Vec3 getAnchorPoint() {
            return ANCHOR;
        }

        @Override
        public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
            controllers.add(new AnimationController<>(this, "controller", state ->
                state.setAndContinue(RawAnimation.begin().thenLoop("idle")))
            );
        }

        @Override
        public AnimatableInstanceCache getAnimatableInstanceCache() {return this.cache;}
    }

    public static class BItem extends BlockItem implements GeoItem {
        private final AnimatableInstanceCache CACHE = GeckoLibUtil.createInstanceCache(this);
        public BItem(LargeChandelierBlock block, Properties properties) {
            super(block, properties);
        }

        @Override
        public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
            consumer.accept(new BaseGeoItemRendererProvider<BItem>(new CacheItemRefBlockModel<>(), true){
                @Override
                public void process(BakedGeoModel model) {
                    model.topLevelBones().getFirst().getChildBones().stream()
                        .map(GeoBone::getChildBones).flatMap(Collection::stream)
                        .map(GeoBone::getChildBones).flatMap(Collection::stream)
                        .forEach(geoBone -> {
                            if (geoBone.getName().startsWith("flame")) geoBone.setHidden(false);
                        });
                }
            });
        }

        @Override
        public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

        @Override
        public AnimatableInstanceCache getAnimatableInstanceCache() {
            return CACHE;
        }
    }
}
