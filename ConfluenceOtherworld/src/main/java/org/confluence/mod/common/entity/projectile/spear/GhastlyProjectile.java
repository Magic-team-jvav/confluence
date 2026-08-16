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

/**
 * <h1>恶魂弹射物</h1>
 * 从锁定目标周围圆形区域（半径5）生成，沿水平方向飞向目标。
 * 可无限穿透、穿墙，每 4 tick 对同一实体最多造成一次伤害。
 */
public class GhastlyProjectile extends SpearProjectile {
    /**
     * 对同一实体的伤害冷却间隔（tick）
     */
    private static final int DAMAGE_INTERVAL = 4;

    /**
     * 记录每个实体上次受伤的 tick 时间，用于伤害冷却
     */
    private final Object2IntMap<Entity> lastHitTicks = new Object2IntOpenHashMap<>();
    /**
     * 是否已穿过目标，穿过不再索敌折返
     */
    private boolean hasPassedTarget = false;

    public GhastlyProjectile(EntityType<? extends GhastlyProjectile> entityType, Level level) {
        super(entityType, level);
        this.knockBack = 0.0f;
        this.baseKnockBack = 0.0f;
    }

    /// 在统一派生弹幕事务提交实体前设置锁定目标。
    ///
    /// <p>目标配置早于实体加入世界，因此不会先触发基类自动索敌，也不依赖生成后的可变状态
    /// 修补。锁定目标只控制运动方向，战斗数值仍完全来自服务端冻结快照。</p>
    public void setLockedTarget(LivingEntity target) {
        this.target = target;
    }

    /**
     * 水平飞向锁定目标，y 轴速度恒为零。
     * 穿过目标不再折返，继续沿原方向飞行。
     */
    @Override
    protected void updateMotion() {
        if (!hasPassedTarget && target != null && target.isAlive()) {
            Vec3 toTarget = target.position().subtract(position());
            double horizontalDistSqr = toTarget.x * toTarget.x + toTarget.z * toTarget.z;
            if (horizontalDistSqr > 0.25) {
                float speed = projComponent != null ? projComponent.baseSpeed() : 0.5f;
                Vec3 horizontalDir = new Vec3(toTarget.x, 0.0, toTarget.z).normalize();
                velocity = horizontalDir.scale(speed);
            }
            // 接近目标（≤1 格）时标记已穿透，之保持原方向不再索敌
            if (horizontalDistSqr <= 1.0) {
                hasPassedTarget = true;
            }
        }
    }

    @Override
    protected Vec3 initVelocity(LivingEntity owner, Vec3 direction, float speed) {
        return direction.scale(speed);
    }

    // ===== 穿透与穿墙 =====

    /**
     * 无限穿透 — 不减少穿透次数，不销毁
     */
    @Override
    protected void applyPenetration() {
        // 空实现：无限穿透
    }

    /**
     * 穿墙 — 撞墙时不销毁
     */
    @Override
    protected void onHitBlock(BlockHitResult result) {
        // 空实现：穿墙
    }

    // ===== 伤害与碰撞 =====

    /**
     * 始终可命中存活、可拾取的非主人实体
     */
    @Override
    protected boolean canHitEntity(Entity target) {
        return target.isAlive()
                && target.isPickable()
                && super.canHitEntity(target);
    }

    /// 恶魂弹幕按自身 4 tick 冷却重复伤害，不采用普通弹幕的永久 UUID 去重。
    @Override
    protected boolean allowsRepeatedHits() {
        return true;
    }

    /**
     * 检查伤害冷却后调用父类伤害逻辑。
     * 同一实体每 {@link #DAMAGE_INTERVAL} tick 最多受伤一次。
     */
    @Override
    protected boolean doHurt(Entity target) {
        int currentTick = tickCount;
        int lastTick = lastHitTicks.getOrDefault(target, -DAMAGE_INTERVAL);
        if (currentTick - lastTick < DAMAGE_INTERVAL) {
            return false;
        }
        boolean hurt = super.doHurt(target);
        if (hurt) {
            lastHitTicks.put(target, currentTick);
        }
        return hurt;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            // 清理已死亡或已移除的实体记录
            lastHitTicks.keySet().removeIf(e -> !e.isAlive() || e.isRemoved());
        }
    }

    /**
     * 免疫所有外部伤害
     */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    // ===== 渲染元数据 =====

    @Override
    public net.minecraft.resources.ResourceLocation getProjTexture() {
        return Confluence.asResource("textures/entity/ghastly_projectile.png");
    }

}
