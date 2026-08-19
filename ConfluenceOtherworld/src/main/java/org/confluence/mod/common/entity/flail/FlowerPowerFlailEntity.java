package org.confluence.mod.common.entity.flail;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.component.FlailComponent;
import org.confluence.mod.common.entity.projectile.flail.FlowerPowerPetalProjectile;
import org.confluence.mod.common.init.entity.ModEntities;

import java.util.Comparator;

/// 花之力链锤实体，定期向最近的有效目标发射花瓣。
public final class FlowerPowerFlailEntity extends BaseFlailEntity {
    private int shootTimer;

    public FlowerPowerFlailEntity(EntityType<? extends FlowerPowerFlailEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public boolean usesSpriteHead() {
        return true;
    }

    @Override
    protected void tickSpecialBehavior(Player player, FlailComponent component, int phase) {
        if (level().isClientSide()) {
            return;
        }
        int interval = phase == PHASE_STAY ? 5 : 10;
        if (++shootTimer < interval) {
            return;
        }
        shootTimer = 0;

        LivingEntity target = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(component.maxDistance()), candidate -> LibEntityUtils.canHitEntity(candidate, this))
                .stream()
                .min(Comparator.comparingDouble(this::distanceToSqr))
                .orElse(null);
        if (target == null) {
            return;
        }

        Vec3 direction = target.getBoundingBox().getCenter().subtract(position()).normalize();
        FlowerPowerPetalProjectile petal = ModEntities.FLOWER_POWER_PETAL.get().create(level());
        if (petal == null) {
            return;
        }
        petal.initialize(this, player, direction.scale(component.throwSpeed()), component.damageFactor() * (float) player.getAttributeValue(LibAttributes.getAttackDamage()) / 3.0F, 100);
        level().addFreshEntity(petal);
    }
}
