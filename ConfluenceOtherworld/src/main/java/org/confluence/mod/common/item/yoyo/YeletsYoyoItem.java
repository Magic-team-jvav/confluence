package org.confluence.mod.common.item.yoyo;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.yoyo.YoyoEntity;
import org.confluence.mod.common.init.ModEffects;

public final class YeletsYoyoItem extends YoyoItem {
    public YeletsYoyoItem() {
        super(new Properties().unbreakable(), ModRarity.PINK, 15, 18.125F, 0xFFFFFFFF, 14 * 20, 3.1F);
    }

    @Override
    protected void onHitTarget(YoyoEntity yoyo, ServerPlayer owner, LivingEntity target) {
        target.addEffect(new MobEffectInstance(ModEffects.ACID_VENOM.get(), 40), owner);
    }

    @Override
    protected String effectTooltip() {return "tooltip.confluence.yoyo.venom";}
}
