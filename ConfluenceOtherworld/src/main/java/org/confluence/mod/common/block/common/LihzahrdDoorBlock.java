package org.confluence.mod.common.block.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.confluence.lib.common.block.StateProperties;
import org.confluence.mod.common.init.item.ToolItems;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.wrapper.world.entity.player.PortPlayer;

public class LihzahrdDoorBlock extends DoorBlock {
    public static final BooleanProperty UNLOCKED = StateProperties.UNLOCKED;

    public LihzahrdDoorBlock(Properties properties, BlockSetType type) {
        super(properties, type);
        registerDefaultState(defaultBlockState().setValue(UNLOCKED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(UNLOCKED));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) return null;
        if (context.getPlayer() == null || !context.getPlayer().isCreative()) {
            return state.setValue(UNLOCKED, true);
        }
        return state;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);

        // 手持钥匙解锁
        if (!level.isClientSide && stack.is(ToolItems.TEMPLE_KEY.get()) && !state.getValue(UNLOCKED)) {
            level.setBlock(pos, state.setValue(UNLOCKED, true), Block.UPDATE_IMMEDIATE | Block.UPDATE_CLIENTS);
            level.gameEvent(player, isOpen(state) ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
            if (!PortPlayer.hasInfiniteMaterials(player)) {
                stack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }

        // 已解锁时开关门
        if (state.getValue(UNLOCKED)) {
            state = state.cycle(OPEN);
            level.setBlock(pos, state, Block.UPDATE_IMMEDIATE | Block.UPDATE_CLIENTS);
            playSound(player, level, pos, state.getValue(OPEN));
            level.gameEvent(player, isOpen(state) ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }
}
