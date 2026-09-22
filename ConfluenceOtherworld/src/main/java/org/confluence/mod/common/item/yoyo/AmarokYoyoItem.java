package org.confluence.mod.common.item.yoyo;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.yoyo.YoyoEntity;
import org.confluence.mod.common.init.ModEffects;

public final class AmarokYoyoItem extends YoyoItem {
    private final int procDenominator;
    private final int minEffectTicks;
    private final int maxEffectTicks;

    public AmarokYoyoItem(ModRarity rarity, float damage, float range, int lifetimeTicks, float knockback, int procDenominator, int minEffectTicks, int maxEffectTicks) {
        super(new Properties().unbreakable(), rarity, damage, range, lifetimeTicks, knockback);
        this.procDenominator = procDenominator;
        this.minEffectTicks = minEffectTicks;
        this.maxEffectTicks = maxEffectTicks;
    }

    @Override
    protected void onHitTarget(YoyoEntity yoyo, ServerPlayer owner, LivingEntity target) {
        if (owner.getRandom().nextInt(procDenominator) == 0) return;
        target.addEffect(new MobEffectInstance(ModEffects.FROSTBITE.get(), minEffectTicks + owner.getRandom().nextInt(maxEffectTicks - minEffectTicks + 1)), owner);
    }

    @Override
    public boolean fullBright() {return true;}

    @Override
    protected Component effectTooltip() {return Component.translatable("tooltip.confluence.yoyo.frostbite");}
}
