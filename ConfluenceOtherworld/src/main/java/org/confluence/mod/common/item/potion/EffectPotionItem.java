package org.confluence.mod.common.item.potion;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.MobEffectInstanceData;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.item.storage.VoidBagItem;

import java.util.ArrayList;
import java.util.List;

public class EffectPotionItem extends AbstractPotionItem {
    public final MobEffectInstanceData data;

    public EffectPotionItem(Properties properties, Holder<MobEffect> mobEffect, int duration, int amplifier) {
        super(properties);
        this.data = new MobEffectInstanceData(mobEffect, duration, amplifier);
    }

    public EffectPotionItem(ModRarity rarity, Holder<MobEffect> mobEffect, int duration) {
        this(new Properties().component(ConfluenceMagicLib.MOD_RARITY, rarity), mobEffect, duration, 0);
    }

    public EffectPotionItem(ModRarity rarity, Holder<MobEffect> mobEffect, int duration, int amplifier) {
        this(new Properties().component(ConfluenceMagicLib.MOD_RARITY, rarity), mobEffect, duration, amplifier);
    }

    public EffectPotionItem(Holder<MobEffect> mobEffect, int duration) {
        this(new Properties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.BLUE), mobEffect, duration, 0);
    }

    public EffectPotionItem(Holder<MobEffect> mobEffect, int duration, int amplifier) {
        this(new Properties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.BLUE), mobEffect, duration, amplifier);
    }

    /// 服务端使用副手、背包及已开启虚空袋中的增益药水与食物，已有同级或更强效果时不重复消耗。
    public static void quickBuff(Player player) {
        if (player.level().isClientSide || !player.isAlive() || player.isSpectator() || player.isUsingItem())
            return;
        List<ItemStack> candidates = new ArrayList<>();
        candidates.add(player.getOffhandItem());
        VoidBagItem.accessibleItems(player).forEach(candidates::add);
        ItemStack bestFood = ItemStack.EMPTY;
        int bestFoodLevel = 0;
        boolean used = false;
        boolean drank = false;
        for (ItemStack stack : candidates) {
            if (stack.isEmpty() || player.getCooldowns().isOnCooldown(stack.getItem())) continue;
            if (stack.getItem() instanceof EffectPotionItem potion) {
                if (!potion.canUse(stack, player.level(), player)) continue;
                MobEffectInstance effect = potion.data.create();
                if (!effect.getEffect().value().isBeneficial() || effect.getEffect().value().isInstantenous() || player.hasEffect(effect.getEffect()) || !player.canBeAffected(effect))
                    continue;
                drank = true;
            } else {
                List<MobEffectInstance> effects = quickFoodEffects(stack, player);
                if (!canQuickEat(effects, player)) continue;
                int foodLevel = quickFoodPriority(effects);
                if (foodLevel > 0) {
                    if (foodLevel > bestFoodLevel) {
                        bestFood = stack;
                        bestFoodLevel = foodLevel;
                    }
                    continue;
                }
            }
            finishQuickUse(stack, player);
            used = true;
        }
        /// 其他食物可能改变饥饿值或增益，食用候选前重新确认实际可用性。
        if (!bestFood.isEmpty() && !player.getCooldowns().isOnCooldown(bestFood.getItem())
                && canQuickEat(quickFoodEffects(bestFood, player), player)) {
            finishQuickUse(bestFood, player);
            used = true;
        }
        if (used) {
            if (drank)
                player.level().playSound(null, player.blockPosition(), SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 1.0F, 1.0F);
            player.getInventory().setChanged();
        }
    }

    /// 仅处理标签内食物，读取必定生效的持续增益并排除负面效果。
    private static List<MobEffectInstance> quickFoodEffects(ItemStack stack, Player player) {
        if (!stack.is(ModTags.Items.QUICK_BUFF_FOODS)) return List.of();
        var food = stack.getFoodProperties(player);
        if (food == null || !player.canEat(food.canAlwaysEat())) return List.of();
        List<MobEffectInstance> effects = new ArrayList<>();
        for (var possible : food.effects()) {
            MobEffectInstance effect = possible.effect();
            if (possible.probability() <= 0) continue;
            if (!effect.getEffect().value().isBeneficial()) return List.of();
            if (possible.probability() >= 1 && !effect.getEffect().value().isInstantenous())
                effects.add(effect);
        }
        return effects;
    }

    /// 饱食效果按等级优先；其他增益食物保持原背包顺序。
    private static int quickFoodPriority(List<MobEffectInstance> effects) {
        for (MobEffectInstance effect : effects) {
            if (effect.getEffect() == ModEffects.EXQUISITELY_STUFFED)
                return effect.getAmplifier() + 1;
        }
        return 0;
    }

    /// 同级或更高的饱食效果不会重复吃；其他食物必须至少能新增或升级一个可接受的持续增益。
    private static boolean canQuickEat(List<MobEffectInstance> effects, Player player) {
        for (MobEffectInstance effect : effects) {
            if (effect.getEffect() == ModEffects.EXQUISITELY_STUFFED) {
                MobEffectInstance current = player.getEffect(effect.getEffect());
                if (current != null && current.getAmplifier() >= effect.getAmplifier())
                    return false;
            }
        }
        return effects.stream().anyMatch(effect -> {
            MobEffectInstance current = player.getEffect(effect.getEffect());
            return player.canBeAffected(effect) && (current == null || current.getAmplifier() < effect.getAmplifier());
        });
    }

    @Override
    protected void apply(ItemStack itemStack, Level level, LivingEntity living) {
        if (level.isClientSide) return;
        living.addEffect(data.create());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        int duration = data.duration();
        String minute = duration % 1200 == 0 ? Integer.toString(duration / 1200) : Float.toString(duration / 1200F);
        tooltipComponents.add(Component.translatable("tooltip.confluence.effect_duration", minute).withStyle(ChatFormatting.GRAY));
    }
}
