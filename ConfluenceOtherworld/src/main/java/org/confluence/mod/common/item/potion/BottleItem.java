package org.confluence.mod.common.item.potion;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.confluence.mod.common.init.item.PotionItems;
import org.confluence.mod.common.init.item.SoulItems;

public class BottleItem extends Item {
    public BottleItem() {
        super(new Properties().stacksTo(16));
    }

    /// 优先级：先用玻璃瓶接水，只有在没有对着水源时才会去装魂。
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        BlockHitResult blockhitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (blockhitresult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockpos = blockhitresult.getBlockPos();
            if (level.mayInteract(player, blockpos) && level.getFluidState(blockpos).is(FluidTags.WATER)) {
                level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
                level.gameEvent(player, GameEvent.FLUID_PICKUP, blockpos);
                return InteractionResultHolder.sidedSuccess(ItemUtils.createFilledResult(itemstack, player, PotionItems.BOTTLED_WATER.toStack()), level.isClientSide);
            }
        }
        /// 魂瓶：没有可接的水源时，灵魂条状态下用玻璃瓶装魂。
        if (SoulItems.canFillSoulBottle(player)) {
            if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
                SoulItems.fillSoulBottle(serverPlayer, hand);
            }
            return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
        }
        return InteractionResultHolder.pass(itemstack);
    }
}
