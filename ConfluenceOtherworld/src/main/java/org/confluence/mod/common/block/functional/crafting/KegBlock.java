package org.confluence.mod.common.block.functional.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import org.confluence.mod.common.init.item.PotionItems;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class KegBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<KegBlock> CODEC = simpleCodec(KegBlock::new);
    private static final VoxelShape X_AXIS_SHAPE;
    private static final VoxelShape Z_AXIS_SHAPE;

    static {
        X_AXIS_SHAPE = Stream.of(
                Block.box(1, 5.5, 3, 15, 15.5, 13),
                Block.box(3.5, 0, 4, 13.5, 6, 12)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

        Z_AXIS_SHAPE = Stream.of(
                Block.box(3, 5.5, 1, 13, 15.5, 15),
                Block.box(4, 0, 3.5, 12, 6, 13.5)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        return direction.getAxis() == Direction.Axis.Z ? Z_AXIS_SHAPE : X_AXIS_SHAPE;
    }


    public KegBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(PotionItems.MUG)) {
            if (level.isClientSide) return ItemInteractionResult.SUCCESS;
            player.addItem(PotionItems.ALE.toStack());
            if (!player.hasInfiniteMaterials()) {
                stack.shrink(1);
            }
            return ItemInteractionResult.CONSUME;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected MapCodec<KegBlock> codec() {
        return CODEC;
    }
}
