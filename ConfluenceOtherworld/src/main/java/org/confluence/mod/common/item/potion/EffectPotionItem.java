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

    // 服务端依次使用背包、副手及已开启虚空袋中的增益药水，已有的效果不重复消耗。
    public static void quickBuff(Player player) {
        if (player.level().isClientSide || !player.isAlive() || player.isSpectator() || player.isUsingItem())
            return;
        List<ItemStack> candidates = new ArrayList<>();
        candidates.add(player.getOffhandItem());
        VoidBagItem.accessibleItems(player).forEach(candidates::add);
        boolean used = false;
        for (ItemStack stack : candidates) {
            if (stack.isEmpty() || !(stack.getItem() instanceof EffectPotionItem potion)
                    || player.getCooldowns().isOnCooldown(potion)
                    || !potion.canUse(stack, player.level(), player)) continue;
            MobEffectInstance effect = potion.data.create();
            if (!effect.getEffect().value().isBeneficial() || effect.getEffect().value().isInstantenous()
                    || player.hasEffect(effect.getEffect()) || !player.canBeAffected(effect))
                continue;
            finishQuickUse(stack, player);
            used = true;
        }
        if (used) {
            player.level().playSound(null, player.blockPosition(), SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 1.0F, 1.0F);
            player.getInventory().setChanged();
        }
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
