package org.confluence.mod.common.item.potion;

import PortLib.extensions.net.minecraft.world.entity.player.Player.PortPlayerExtension;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.EffectThrownPotion;

import java.util.function.Supplier;

public class EffectThrowablePotionItem extends EffectPotionItem {
    public EffectThrowablePotionItem(Properties properties, Supplier<? extends MobEffect> mobEffect, int duration, int amplifier) {
        super(properties, mobEffect, duration, amplifier);
    }

    public EffectThrowablePotionItem(ModRarity rarity, Supplier<? extends MobEffect> mobEffect, int duration) {
        super(rarity, mobEffect, duration);
    }

    public EffectThrowablePotionItem(ModRarity rarity, Supplier<? extends MobEffect> mobEffect, int duration, int amplifier) {
        super(rarity, mobEffect, duration, amplifier);
    }

    public EffectThrowablePotionItem(Supplier<? extends MobEffect> mobEffect, int duration) {
        super(mobEffect, duration);
    }

    public EffectThrowablePotionItem(Supplier<? extends MobEffect> mobEffect, int duration, int amplifier) {
        super(mobEffect, duration, amplifier);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SPLASH_POTION_THROW, SoundSource.PLAYERS, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            EffectThrownPotion potion = new EffectThrownPotion(player, level);
            potion.setItem(stack);
            potion.shootFromRotation(player, player.getXRot(), player.getYRot(), -20.0F, 0.5F, 1.0F);
            level.addFreshEntity(potion);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!PortPlayerExtension.hasInfiniteMaterials(player)) {
            stack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
