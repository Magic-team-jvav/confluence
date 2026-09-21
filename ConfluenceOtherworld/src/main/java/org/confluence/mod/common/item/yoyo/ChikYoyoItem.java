package org.confluence.mod.common.item.yoyo;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.yoyo.YoyoEffectProjectile;
import org.confluence.mod.common.entity.yoyo.YoyoEntity;

public final class ChikYoyoItem extends YoyoItem {
    public ChikYoyoItem() {
        super(new Properties().unbreakable(), ModRarity.LIGHT_RED, 8.5F, 17.1875F, 0xFFFFFFFF, 16 * 20, 3.3F);
    }

    @Override
    protected void onHitTarget(YoyoEntity yoyo, ServerPlayer owner, LivingEntity target) {
        YoyoSession session = YoyoSession.of(owner);
        if (session.isSpecialHit(3)) {
            for (int i = 0; i < 5; i++) {
                Vec3 direction = new Vec3(owner.getRandom().nextGaussian(), owner.getRandom().nextGaussian() * 0.4, owner.getRandom().nextGaussian());
                YoyoEffectProjectile.shoot(yoyo, YoyoEffectProjectile.Kind.CRYSTAL, direction, target);
            }
        }
        session.countSpecialHit();
    }

    @Override
    public float hitMultiplier(ServerPlayer owner) {return YoyoSession.of(owner).isSpecialHit(3) ? 2 : 1;}

    @Override
    public boolean fullBright() {return true;}

    @Override
    protected String effectTooltip() {return "tooltip.confluence.yoyo.crystals";}
}
