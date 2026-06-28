package org.confluence.mod.common.entity.projectile.spear;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.mesdag.particlestorm.data.molang.MolangExp;
import org.mesdag.particlestorm.network.EmitterCreationPacketS2C;

/**
 * <h1>叶绿长戟孢子云弹射物</h1>
 * <p>
 * 绿色孢子云，可穿透多个怪物实体，每隔一定时间造成伤害。
 * 速度呈反比例函数衰减：v(t) = v0 / (1 + k * t)，接近0时弹射物消失。
 */
public class SporeCloudProjectile extends SpearProjectile {
    /**
     * 记录每个实体上次受伤的 tick 时间
     */
    private final Object2IntMap<Entity> lastHitTicks = new Object2IntOpenHashMap<>();
    /**
     * 速度衰减系数
     */
    public float decayK = 0.12f;
    /**
     * 最小速度阈值
     */
    public float minSpeed = 0.01f;
    /**
     * 伤害间隔 tick
     */
    public int damageInterval = 10;

    public SporeCloudProjectile(EntityType<? extends SporeCloudProjectile> entityType, Level level) {
        super(entityType, level);
        this.collisionProperties = new CollisionProperties(1, 1, 0.65F);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (!level().isClientSide) {
            EmitterCreationPacketS2C.sendToAll(
                    Confluence.asResource("spore_cloud"),
                    position().toVector3f(),
                    MolangExp.EMPTY,
                    this);
        }
    }

    /**
     * 反比例速度衰减：v(t) = v0 / (1 + k * t)
     * 当速度低于阈值时销毁弹射物。
     */
    @Override
    protected void updateMotion() {
        float speed = (float) velocity.length();
        if (speed <= minSpeed) {
            discard();
            return;
        }
        // 保存当前方向，避免低速度时 normalize() 产生不稳定方向
        Vec3 direction = velocity.normalize();
        float newSpeed = speed / (1.0f + decayK);
        if (newSpeed <= minSpeed) {
            discard();
            return;
        }
        velocity = direction.scale(newSpeed);
    }

    /**
     * 返回初始速度方向向量，scale 初始速度 0.6f。
     */
    @Override
    protected Vec3 initVelocity(LivingEntity owner, Vec3 direction, float speed) {
        return direction.scale(0.6f);
    }

    /**
     * 孢子云始终可命中（穿透不受 pierceRemaining 限制）。
     */
    @Override
    protected boolean canHitEntity(Entity target) {
        return target.isAlive()
                && target != getOwner()
                && target.isPickable()
                && TEUtils.projectileCanHitEntityTest.test(this, target);
    }

    /**
     * 检查伤害间隔后调用父类伤害逻辑。
     */
    @Override
    protected boolean doHurt(Entity target) {
        int currentTick = tickCount;
        int lastTick = lastHitTicks.getOrDefault(target, -damageInterval);
        if (currentTick - lastTick < damageInterval) {
            return false;
        }
        lastHitTicks.put(target, currentTick);
        return super.doHurt(target);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            // 清理已死亡实体
            lastHitTicks.keySet().removeIf(e -> !e.isAlive());
        }
    }

    /**
     * 撞墙时不销毁。
     */
    @Override
    protected void onHitBlock(BlockHitResult result) {
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }
}
