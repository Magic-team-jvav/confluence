package org.confluence.terra_furniture.common.block.misc;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.terra_furniture.common.block.func.be.SimpleModelGeoBE;
import org.confluence.terra_furniture.common.init.TFBlocks;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animation.AnimatableManager;

import java.util.Random;

public class PinWheel extends HorizontalDirectionalBlock implements EntityBlock {
    public static final VoxelShape SHAPE = Shapes.box(0.46875, 0, 0.46875, 0.53125, 0.5625, 0.53125);
    public PinWheel(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }
    public static final MapCodec<PinWheel> CODEC = simpleCodec(PinWheel::new);
    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN && !this.canSurvive(state, level, pos)) return Blocks.AIR.defaultBlockState();
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos groundingPos = pos.below();
        return level.getBlockState(groundingPos).isFaceSturdy(level, groundingPos, Direction.UP);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BEntity(pos, state);
    }
    public static class BEntity extends SimpleModelGeoBE implements GeoBlockEntity {
        private static final float STEP = 0.07f;
        private static final Random r = new Random();
        private float rotate;
        public BEntity(BlockPos pos, BlockState blockState) {
            super(TFBlocks.PIN_WHEEL_ENTITY.get(), pos, blockState, false);
            rotate = r.nextFloat(0, Mth.PI * 2);
        }
        @Override
        public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}
        public float getStepNext() {
            if (rotate <= -Mth.PI*24) rotate = 0;
            else rotate -= STEP;
            return rotate;
        }
    }
}
