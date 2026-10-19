package org.confluence.mod.common.entity.flail;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.lib.util.LibEntityUtils.TargetPolicy;
import org.confluence.lib.util.LibEntityUtils.WeaponTargeting;
import org.confluence.mod.common.entity.*;
import org.confluence.mod.common.entity.projectile.flail.FlowerPowerPetalProjectile;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.item.flail.BaseFlailItem.Parameters;

import java.util.Comparator;

/// 花之力链锤实体，定期向最近的有效目标发射花瓣。
public final class FlowerPowerFlailEntity extends BaseFlailEntity {
    private static final WeaponTargeting TARGETING = new WeaponTargeting(TargetPolicy.ENEMY_FACTION);

    private int shootTimer;

    public FlowerPowerFlailEntity(EntityType<? extends FlowerPowerFlailEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public boolean usesSpriteHead() {
        return true;
    }

    @Override
    protected void tickSpecialBehavior(Player player, Parameters parameters, int phase) {
        if (level().isClientSide()) {
            return;
        }
        int interval = phase == PHASE_STAY ? 5 : 10;
        if (--shootTimer > 0) {
            return;
        }
        shootTimer = interval;

        Entity target = LibEntityUtils.nearest(
                TARGETING.hitTargets(level(), this, player, getBoundingBox().inflate(parameters.maxDistance()), candidate -> true),
                candidate -> TARGETING.canAcquire(player, candidate),
                Comparator.comparingDouble(this::distanceToSqr));
        if (target == null) {
            return;
        }

        Vec3 direction = target.getBoundingBox().getCenter().subtract(position()).normalize();
        FlowerPowerPetalProjectile petal = ModEntities.FLOWER_POWER_PETAL.get().create(level());
        if (petal == null) {
            return;
        }
        petal.initialize(this, player, direction.scale(parameters.throwSpeed()), parameters.damageFactor() * (float) player.getAttributeValue(LibAttributes.getAttackDamage()) / 3.0F, 100);
        level().addFreshEntity(petal);
    }
}
