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
        super(ModFluids.HONEY.fluid().get(), new Properties().craftRemainder(Items.BUCKET).stacksTo(1));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving) {
        if (!pLevel.isClientSide) {
            pEntityLiving.addEffect(new MobEffectInstance(TCEffects.HONEY, 900));
            pEntityLiving.removeEffect(MobEffects.POISON);
        }
        if (pEntityLiving instanceof ServerPlayer serverplayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverplayer, pStack);
            serverplayer.awardStat(Stats.ITEM_USED.get(this));
        }
        if (pEntityLiving instanceof Player && !((Player) pEntityLiving).hasInfiniteMaterials()) {
            pStack.shrink(1);
        }
        return pStack.isEmpty() ? new ItemStack(Items.BUCKET) : pStack;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
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
