package org.confluence.mod.common.entity.projectile.flail;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.init.ModDamageTypes;

/**
 * 花之力发射的直线花瓣。
 */
public final class FlowerPowerPetalProjectile
        extends FlailAuxiliaryProjectile {
    public FlowerPowerPetalProjectile(
            EntityType<? extends FlowerPowerPetalProjectile> type,
            Level level
    ) {
        super(type, level);
        setNoGravity(true);
    }

    @Override
    protected void onHitLiving(LivingEntity target) {
        if (!(getOwner() instanceof Player player)) {
            discard();
            return;
        }
        if (target.hurt(
                ModDamageTypes.of(
                        level(),
                        ModDamageTypes.SWORD_PROJECTILE,
                        this,
                        player),
                damage)) {
            LibEntityUtils.knockBackA2B(this, target, 0.15F, 0.08F);
        }
        discard();
    }
}
