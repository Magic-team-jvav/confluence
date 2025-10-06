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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.init.item.PotionItems;
import org.jetbrains.annotations.Nullable;

public class KegBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<KegBlock> CODEC = simpleCodec(KegBlock::new);
    private static final VoxelShape X_AXIS_SHAPE = Shapes.or(
            box(1, 5.5, 3, 15, 15.5, 13),
            box(10.5, 5, 2.5, 12.5, 9, 13.5),
            box(10.5, 0, 10, 12.5, 5, 12),
            box(10.5, 0, 4, 12.5, 5, 6),
            box(11, 1.5, 6, 12, 3.5, 10),
            box(3.5, 5, 2.5, 5.5, 9, 13.5),
            box(3.5, 0, 10, 5.5, 5, 12),
            box(3.5, 0, 4, 5.5, 5, 6),
            box(4, 1.5, 6, 5, 3.5, 10)
    );
    private static final VoxelShape Z_AXIS_SHAPE = Shapes.or(
            box(3, 5.5, 1, 13, 15.5, 15),
            box(2.5, 5, 10.5, 13.5, 9, 12.5),
            box(4, 0, 10.5, 6, 5, 12.5),
            box(10, 0, 10.5, 12, 5, 12.5),
            box(6, 1.5, 11, 10, 3.5, 12),
            box(2.5, 5, 3.5, 13.5, 9, 5.5),
            box(4, 0, 3.5, 6, 5, 5.5),
            box(10, 0, 3.5, 12, 5, 5.5),
            box(6, 1.5, 4, 10, 3.5, 5)
    );

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
