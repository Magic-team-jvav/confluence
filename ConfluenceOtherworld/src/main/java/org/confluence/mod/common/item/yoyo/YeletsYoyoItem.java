package org.confluence.mod.common.item.yoyo;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.yoyo.YoyoEntity;
import org.confluence.mod.common.init.ModEffects;

public final class YeletsYoyoItem extends YoyoItem {
    private final int effectTicks;

    public YeletsYoyoItem(ModRarity rarity, float damage, float range, int lifetimeTicks, float knockback, int effectTicks) {
        super(new Properties().unbreakable(), rarity, damage, range, lifetimeTicks, knockback);
        this.effectTicks = effectTicks;
    }

    @Override
    protected void onHitTarget(YoyoEntity yoyo, ServerPlayer owner, LivingEntity target) {
        target.addEffect(new MobEffectInstance(ModEffects.ACID_VENOM.get(), effectTicks), owner);
    }

    @Override
    protected Component effectTooltip() {return Component.translatable("tooltip.confluence.yoyo.venom");}
}
