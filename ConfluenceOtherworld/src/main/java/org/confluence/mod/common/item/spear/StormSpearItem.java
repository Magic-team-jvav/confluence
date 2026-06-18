package org.confluence.mod.common.item.spear;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.component.SpearProjectileComponent;
import org.confluence.mod.common.entity.projectile.spear.StormSpearProjectile;
import org.confluence.mod.common.init.ModEntities;
import org.mesdag.portlib.wrapper.world.item.PortItem;
import software.bernie.geckolib.core.animation.EasingType;

public class StormSpearItem extends AbstractSpearItem {
    public static final double knockBackScale = 0.3;
    public static final double knockBackMotionY = 0.1;

    public StormSpearItem() {
        super(new PortItem.PortProperties().attributes(attributes(3, 7F)), ModRarity.BLUE, 15, 5, createKeyframes(
                K.of(0, 0, EasingType.LINEAR),
                K.of(0.25, 6, EasingType.EASE_OUT_BACK),
                K.of(0.5, -16, EasingType.EASE_IN_EXPO),
                K.of(0.75, 0, EasingType.LINEAR)
        ));
    }

    @Override
    protected void onHitEntity(DamageSource damageSource, LivingEntity owner, Entity victim) {
        hurtVictim(damageSource, owner, victim);
        LibEntityUtils.knockBackA2B(owner, victim, knockBackScale, knockBackMotionY);
    }

    @Override
    protected void onStingTick(ItemStack stack, ServerLevel level, LivingEntity owner, Vec3 tipPos, boolean last) {
        if (last) {
            SpearProjectileComponent component = SpearProjectileComponent.STORM_SPEAR_PROJ.get();
            StormSpearProjectile projectile = new StormSpearProjectile(
                    ModEntities.STORM_SPEAR_SHOT.get(), level);

            projectile.setOwner(owner);
            projectile.setWeapon(owner.getMainHandItem());
            // setProjComponent 自动从 owner 获取基础攻击伤害
            projectile.setProjComponent(component, owner);

            // 初始位置：矛尖与玩家之间约1/3处
            Vec3 spawnPos = owner.getEyePosition().add(tipPos.subtract(owner.getEyePosition()).scale(0.33));
            projectile.setPos(spawnPos.x, spawnPos.y, spawnPos.z);

            // 发射：设置方向、速度、击退，自动同步客户端
            projectile.fire(owner.getLookAngle(), component.getVelocity(owner), (float) knockBackScale);

            level.addFreshEntity(projectile);
        }
    }
}
