package org.confluence.mod.common.entity.flail;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibAttributes;
import org.confluence.mod.common.component.FlailComponent;
import org.confluence.mod.common.entity.projectile.flail.DripplerCripplerProjectile;
import org.confluence.mod.common.init.entity.ModEntities;

/**
 * 滴滴怪致残者链锤实体。
 */
public final class DripplerCripplerFlailEntity extends BaseFlailEntity {
    public DripplerCripplerFlailEntity(
            EntityType<? extends DripplerCripplerFlailEntity> type,
            Level level
    ) {
        super(type, level);
    }

    @Override
    public boolean usesSpriteHead() {
        return true;
    }

    @Override
    protected void onThrownToRetract(
            Player player,
            FlailComponent component
    ) {
        DripplerCripplerProjectile projectile =
                ModEntities.DRIPPLER_CRIPPLER_PROJECTILE.get().create(level());
        if (projectile == null) {
            return;
        }
        Vec3 velocity = getDeltaMovement();
        if (velocity.lengthSqr() < 1.0E-6) {
            velocity = player.getViewVector(1.0F);
        }
        projectile.initialize(
                this,
                player,
                velocity.normalize().scale(component.throwSpeed()),
                component.damageFactor()
                        * (float) player.getAttributeValue(
                        LibAttributes.getAttackDamage())
                        * 0.5F,
                200);
        level().addFreshEntity(projectile);
    }
}
