package org.confluence.mod.common.entity.projectile.spear;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshot;
import org.confluence.lib.api.projectile.ProjectileLaunch;
import org.confluence.lib.api.projectile.ServerProjectileFireService;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModParticleTypes;
import org.confluence.mod.common.init.entity.ModEntities;

import java.util.List;

/**
 * <h1>北极矛弹射物</h1>
 * 直线弹射物，有重力，不可穿墙，间歇生成子弹射物。
 */
public class NorthPoleProjectile extends SpearProjectile {
    /**
     * 子弹射物生成间隔（tick）
     */
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
        if (!(getOwner() instanceof ServerPlayer player)) {
            return;
        }
        ProjectileCombatSnapshot snapshot = getProjectileCombatSnapshot();
        if (snapshot == null) {
            return;
        }

        NorthPoleSubProjectile sub = new NorthPoleSubProjectile(
                ModEntities.NORTH_POLE_SUB.get(), level());
        if (projComponent != null) {
            sub.setProjComponent(projComponent, player);
        }

        // 用有效的向下方向描述“零速生成”：零倍率令事务写入零初速，随后仍由子弹幕自己的
        // 重力逻辑开始下落。快照沿用母弹幕，绝不在周期生成时重新读取玩家当前属性。
        ServerProjectileFireService.spawnDerived(
                player,
                snapshot,
                List.of(new ProjectileLaunch(
                        sub,
                        position(),
                        new Vec3(0.0, -1.0, 0.0),
                        0.0F)));
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
