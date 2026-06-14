package org.confluence.mod.common.block.functional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipBlockItem;
import org.confluence.lib.util.LibUtils;
import org.confluence.lib.util.MobEffectInstanceData;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EffectiveCandleBlock extends AbstractMechanicalBlock {
    private static final VoxelShape SHAPE = Shapes.or(
            box(6, 1, 6, 10, 9, 10),
            box(4, 0, 4, 12, 2, 12)
    );
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    private final float scopeSqr;
    private final MobEffectInstanceData[] effectData;

    public EffectiveCandleBlock(Properties properties, float scope, MobEffectInstanceData... effectData) {
        super(properties);
        this.scopeSqr = scope * scope;
        this.effectData = effectData;
        registerDefaultState(stateDefinition.any()
                .setValue(LIT, true)
                .setValue(FACING, Direction.WEST));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public void onExecute(BlockState state, ServerLevel level, BlockPos pos, int color, INetworkEntity networkEntity) {
        switchStatus(level, state, pos);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        switchStatus(level, state, pos);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        switchStatus(level, state, pos);
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    private static void switchStatus(Level level, BlockState state, BlockPos pos) {
        if (!level.isClientSide) {
            level.setBlockAndUpdate(pos, state.cycle(LIT));
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : LibUtils.getTicker(blockEntityType, FunctionalBlocks.MECHANICAL_BLOCK_ENTITY.get(), (level1, pos, state1, blockEntity) -> {
            if (level1 instanceof ServerLevel serverLevel && level.getGameTime() % 40 == 5) {
                double x = pos.getX() + 0.5;
                double y = pos.getY() + 0.5;
                double z = pos.getZ() + 0.5;
                for (ServerPlayer player : serverLevel.players()) {
                    if (player.distanceToSqr(x, y, z) <= scopeSqr) {
                        awardEffect(player, effectData);
                    }
                }
            }
        });
    }

    private static void awardEffect(LivingEntity living, MobEffectInstanceData[] dat) {
        for (MobEffectInstanceData data : dat) {
            MobEffectInstance instance = living.getActiveEffectsMap().get(data.effect().get());
            if (instance == null || instance.getDuration() < 50) {
                living.addEffect(data.create());
            }
        }
    }

    public static class BItem extends TooltipBlockItem {
        public final MobEffectInstanceData[] effectData;

        public BItem(Block block, ModRarity rarity, List<Component> tooltips, MobEffectInstanceData... effectData) {
            super(block, new Properties(), rarity, tooltips);
            this.effectData = effectData;
        }

        @Override
        public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
            if (isSelected && entity instanceof LivingEntity living) {
                awardEffect(living, effectData);
            }
        }
    }
}
