package org.confluence.mod.common.entity.projectile.spear;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModParticleTypes;
import org.confluence.mod.common.init.entity.ModEntities;

/// 北极矛弹射物。
/// 直线飞行、受重力影响、不可穿墙，并间歇生成子弹射物。
public class NorthPoleProjectile extends SpearProjectile {
    /// 子弹射物生成间隔（tick）。
    private static final int SUB_SPAWN_INTERVAL = 5;

    private int subSpawnTimer = 0;

    public NorthPoleProjectile(EntityType<? extends NorthPoleProjectile> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void updateMotion() {
        if (projComponent != null) {
            velocity = velocity.scale(projComponent.acceleration());
        }
    }

    @Override
    protected Vec3 initVelocity(LivingEntity owner, Vec3 direction, float speed) {
        return direction.scale(1.0f);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            subSpawnTimer++;
            if (subSpawnTimer >= SUB_SPAWN_INTERVAL) {
                subSpawnTimer = 0;
                spawnSubProjectile();
            }
        }
    }

    private void spawnSubProjectile() {
        NorthPoleSubProjectile sub = new NorthPoleSubProjectile(ModEntities.NORTH_POLE_SUB.get(), level());
        sub.setOwner(getOwner());
        sub.setWeapon(getWeaponItem());
        if (projComponent != null) {
            sub.applyComponent(projComponent);
            sub.baseAttackDamage = baseAttackDamage;
        }
        sub.setPos(getX(), getY(), getZ());
        // 子射物初始速度为零,仅受重力下落
        sub.velocity = new Vec3(0, 0, 0);
        sub.setDeltaMovement(Vec3.ZERO);
        level().addFreshEntity(sub);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!level().isClientSide) {
            discard();
        }
    }

    // ===== 渲染元数据 =====

    @Override
    public net.minecraft.resources.ResourceLocation getProjTexture() {
        return Confluence.asResource("textures/entity/storm_spear_shot_projectile.png");
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        if (!level().isClientSide && level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ModParticleTypes.SNOW.get(),
                    getX(), getY(), getZ(), 10, 0.5, 0.5, 0.5, 0.05);
        }
    }
}
