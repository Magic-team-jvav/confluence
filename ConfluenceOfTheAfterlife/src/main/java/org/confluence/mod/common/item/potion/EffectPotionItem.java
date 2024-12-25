package org.confluence.mod.common.item.potion;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terra_curio.common.init.TCDataComponentTypes;

public class EffectPotionItem extends AbstractPotionItem {
    public final Holder<MobEffect> mobEffect;
    public final int duration;
    public final int amplifier;

    public EffectPotionItem(Properties properties, Holder<MobEffect> mobEffect, int duration, int amplifier) {
        super(properties);
        this.mobEffect = mobEffect;
        this.duration = duration;
        this.amplifier = amplifier;
    }

    public EffectPotionItem(Rarity rarity, Holder<MobEffect> mobEffect, int duration) {
        this(new Properties().rarity(rarity), mobEffect, duration, 0);
    }

    public EffectPotionItem(Rarity rarity, Holder<MobEffect> mobEffect, int duration, int amplifier) {
        this(new Properties().rarity(rarity), mobEffect, duration, amplifier);
    }

    public EffectPotionItem(Holder<MobEffect> mobEffect, int duration) {
        this(new Properties().component(TCDataComponentTypes.MOD_RARITY, ModRarity.BLUE), mobEffect, duration, 0);
    }

    public EffectPotionItem(Holder<MobEffect> mobEffect, int duration, int amplifier) {
        this(new Properties().component(TCDataComponentTypes.MOD_RARITY, ModRarity.BLUE), mobEffect, duration, amplifier);
    }

    @Override
    protected void apply(ItemStack itemStack, Level level, LivingEntity living) {
        if (level.isClientSide) return;
        living.addEffect(new MobEffectInstance(mobEffect, duration, amplifier));
    }
}
