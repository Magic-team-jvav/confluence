package org.confluence.mod.common.item.potion;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.MobEffectInstanceData;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class EffectPotionItem extends AbstractPotionItem {
    public final MobEffectInstanceData data;

    public EffectPotionItem(Properties properties, Supplier<? extends MobEffect> mobEffect, int duration, int amplifier) {
        super(properties);
        this.data = new MobEffectInstanceData((Supplier<MobEffect>) mobEffect, duration, amplifier);
    }

    public EffectPotionItem(ModRarity rarity, Supplier<? extends MobEffect> mobEffect, int duration) {
        this(new Properties().component(ConfluenceMagicLib.MOD_RARITY, rarity), mobEffect, duration, 0);
    }

    public EffectPotionItem(ModRarity rarity, Supplier<? extends MobEffect> mobEffect, int duration, int amplifier) {
        this(new Properties().component(ConfluenceMagicLib.MOD_RARITY, rarity), mobEffect, duration, amplifier);
    }

    public EffectPotionItem(Supplier<? extends MobEffect> mobEffect, int duration) {
        this(new Properties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.BLUE), mobEffect, duration, 0);
    }

    public EffectPotionItem(Supplier<? extends MobEffect> mobEffect, int duration, int amplifier) {
        this(new Properties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.BLUE), mobEffect, duration, amplifier);
    }

    @Override
    protected void apply(ItemStack itemStack, Level level, LivingEntity living) {
        if (level.isClientSide) return;
        living.addEffect(data.create());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
        int duration = data.duration();
        String minute = duration % 1200 == 0 ? Integer.toString(duration / 1200) : Float.toString(duration / 1200F);
        tooltipComponents.add(Component.translatable("tooltip.confluence.effect_duration", minute).withStyle(ChatFormatting.GRAY));
    }
}
