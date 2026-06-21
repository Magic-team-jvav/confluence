package org.confluence.mod.common.item.common;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.init.ModFluids;
import org.confluence.terra_curio.common.init.TCEffects;

public class HoneyBucketItem extends BucketItem {
    public HoneyBucketItem() {
        super(ModFluids.HONEY.fluid(), new Properties().craftRemainder(Items.BUCKET).stacksTo(1));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity living) {
        if (!level.isClientSide) {
            living.addEffect(new MobEffectInstance(TCEffects.HONEY.get(), 900));
            living.removeEffect(MobEffects.POISON);
        }
        if (living instanceof ServerPlayer serverplayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverplayer, stack);
            serverplayer.awardStat(Stats.ITEM_USED.get(this));
        }
        if (living instanceof Player player && !player.hasInfiniteMaterials()) {
            stack.shrink(1);
        }
        return stack.isEmpty() ? new ItemStack(Items.BUCKET) : stack;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        InteractionResultHolder<ItemStack> use = super.use(pLevel, pPlayer, pHand);
        if (use.getResult() == InteractionResult.PASS) {
            return ItemUtils.startUsingInstantly(pLevel, pPlayer, pHand);
        }
        return use;
    }
}
