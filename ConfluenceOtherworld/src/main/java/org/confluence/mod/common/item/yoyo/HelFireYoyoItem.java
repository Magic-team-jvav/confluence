package org.confluence.mod.common.item.yoyo;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.yoyo.YoyoEntity;
import org.confluence.mod.common.init.ModEffects;

public final class HelFireYoyoItem extends YoyoItem {
    public HelFireYoyoItem() {
        super(new Properties().unbreakable(), ModRarity.LIGHT_RED, 10.75F, 20.625F, 0xFFFFFFFF, 12 * 20, 4.5F);
    }

    @Override
    protected void onHitTarget(YoyoEntity yoyo, ServerPlayer owner, LivingEntity target) {
        target.addEffect(new MobEffectInstance(ModEffects.HELLFIRE.get(), 60 + owner.getRandom().nextInt(101)), owner);
    }

    @Override
    public boolean fullBright() {return true;}

    @Override
    protected String effectTooltip() {return "tooltip.confluence.yoyo.hellfire";}
}
