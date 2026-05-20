package org.confluence.mod.common.item.spear;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.component.SpearProjectileComponent;
import org.confluence.mod.common.entity.projectile.spear.NorthPoleProjectile;
import org.confluence.mod.common.init.ModEntities;
import software.bernie.geckolib.animation.EasingType;

public class NorthPoleItem extends AbstractSpearItem {
    public NorthPoleItem() {
        super(new Properties().attributes(attributes(3, 20F)), ModRarity.PINK, 20, 5, createKeyframes(
                K.of(0, 0, EasingType.LINEAR),
                K.of(0.25, 6, EasingType.EASE_OUT_BACK),
                K.of(0.5, -16, EasingType.EASE_IN_EXPO),
                K.of(0.75, 0, EasingType.LINEAR)
        ));
    }

    @Override
    protected void onHitEntity(DamageSource damageSource, LivingEntity owner, Entity victim) {
        hurtVictim(damageSource, owner, victim);
        VectorUtils.knockBackA2B(owner, victim, 0.3, 0.2);
    }

    @Override
    protected void onStingTick(ItemStack stack, ServerLevel level, LivingEntity owner, Vec3 tipPos, boolean last) {
        if (last) {
            SpearProjectileComponent component = SpearProjectileComponent.NORTH_POLE_PROJ.get();
            NorthPoleProjectile projectile = new NorthPoleProjectile(
                    ModEntities.NORTH_POLE_PROJECTILE.get(), level);
            projectile.setOwner(owner);
            projectile.setWeapon(owner.getMainHandItem());
            projectile.setProjComponent(component, owner);

            Vec3 spawnPos = owner.getEyePosition().add(tipPos.subtract(owner.getEyePosition()).scale(0.33));
            projectile.setPos(spawnPos.x, spawnPos.y, spawnPos.z);

            projectile.fire(owner.getLookAngle(), component.getVelocity(owner), 0.1f);

            level.addFreshEntity(projectile);
        }
    }
}
