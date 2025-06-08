package org.confluence.mod.common.block.functional;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.init.ModEffects;

import java.util.stream.Stream;

public class SharpeningStationBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<SharpeningStationBlock> CODEC = simpleCodec(SharpeningStationBlock::new);
    private static final VoxelShape X_AXIS_SHAPE;
    private static final VoxelShape Z_AXIS_SHAPE;

    static {
        Z_AXIS_SHAPE = Stream.of(
                Block.box(2, 2, 5, 14, 14, 11),
                Block.box(0, 0, 2, 16, 4, 14)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

        X_AXIS_SHAPE = Stream.of(
                Block.box(5, 2, 2, 11, 14, 14),
                Block.box(2, 0, 0, 14, 4, 16)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        return direction.getAxis() == Direction.Axis.Z ? Z_AXIS_SHAPE : X_AXIS_SHAPE;
    }

    public SharpeningStationBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected MapCodec<SharpeningStationBlock> codec() {
        return CODEC;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            player.addEffect(new MobEffectInstance(ModEffects.SHARPENED, MobEffectInstance.INFINITE_DURATION));
        }
        return InteractionResult.SUCCESS;
    }
}
