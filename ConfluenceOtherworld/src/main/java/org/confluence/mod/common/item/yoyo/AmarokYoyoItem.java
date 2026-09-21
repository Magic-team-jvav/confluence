package org.confluence.mod.common.item.yoyo;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.yoyo.YoyoEntity;
import org.confluence.mod.common.init.ModEffects;

public final class AmarokYoyoItem extends YoyoItem {
    public AmarokYoyoItem() {
        super(new Properties().unbreakable(), ModRarity.LIGHT_RED, 21F, 16.875F, 0xFFFFFFFF, 15 * 20, 2.8F);
    }

    @Override
    protected void onHitTarget(YoyoEntity yoyo, ServerPlayer owner, LivingEntity target) {
        if (owner.getRandom().nextInt(3) == 0) return;
        target.addEffect(new MobEffectInstance(ModEffects.FROSTBITE.get(), 40 + owner.getRandom().nextInt(61)), owner);
    }

    @Override
    public boolean fullBright() {return true;}

    @Override
    protected String effectTooltip() {return "tooltip.confluence.yoyo.frostbite";}
}
