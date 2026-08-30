package org.confluence.mod.common.block.food;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import org.confluence.mod.common.init.ModEffects;

public class BoulderBreadBlock extends Block {
    public BoulderBreadBlock() {
        super(BlockBehaviour.Properties.of().pushReaction(PushReaction.DESTROY).strength(1.0f));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player.hasEffect(ModEffects.CHOKING)) {
            if (!level.isClientSide) {
                player.sendSystemMessage(Component.translatable("message.confluence.choking"));
            }
            return InteractionResult.FAIL;
        }
        if (!level.isClientSide) {
            InteractionResult result = eat(level, pos, player);
            if (result.consumesAction()) return InteractionResult.SUCCESS;
            if (player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.SUCCESS;
    }

    protected static InteractionResult eat(LevelAccessor level, BlockPos pos, Player player) {
        if (!player.canEat(false)) return InteractionResult.PASS;
        player.getFoodData().eat(20, 10.0F);
        player.addEffect(new MobEffectInstance(ModEffects.CHOKING, 6000));
        player.playSound(SoundEvents.GENERIC_EAT);
        level.gameEvent(player, GameEvent.EAT, pos);
        level.removeBlock(pos, false);
        level.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
        return InteractionResult.SUCCESS;
    }
}
