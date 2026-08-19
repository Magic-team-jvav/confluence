package org.confluence.mod.common.entity.flail;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.util.LibEntityUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/// 守卫者链锤的实体实现。
///
/// <p>锁定目标、攻击周期和普通/远古变体都属于当前实体，物品只负责通过组件选择实体类型。
/// 目标实体 ID 使用原版同步数据传给客户端，因此不需要额外网络包或全局缓存。</p>
public class GuardianFlailEntity extends BaseFlailEntity {
    private static final int ATTACK_INTERVAL = 40;
    private static final EntityDataAccessor<Integer> TARGET_ONE = SynchedEntityData.defineId(GuardianFlailEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TARGET_TWO = SynchedEntityData.defineId(GuardianFlailEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TARGET_THREE = SynchedEntityData.defineId(GuardianFlailEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ATTACK_TIME = SynchedEntityData.defineId(GuardianFlailEntity.class, EntityDataSerializers.INT);

    private final boolean elder;
    private int attackTime;

    public GuardianFlailEntity(EntityType<? extends GuardianFlailEntity> type, Level level, boolean elder) {
        super(type, level);
        this.elder = elder;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(TARGET_ONE, 0);
        entityData.define(TARGET_TWO, 0);
        entityData.define(TARGET_THREE, 0);
        entityData.define(ATTACK_TIME, 0);
    }

    @Override
    public void tick() {
        super.tick();
        if (isRemoved() || level().isClientSide()) {
            return;
        }
        if (getPhase() != PHASE_STAY || !(getOwner() instanceof Player player)) {
            clearTargets();
            attackTime = 0;
            entityData.set(ATTACK_TIME, 0);
            return;
        }

        boolean hadTargets = hasTargetIds();
        List<LivingEntity> targets = getBeamTargets();
        if (hadTargets) {
            targets.removeIf(target -> !canContinueTargeting(target));
            syncTargets(targets);
            if (targets.isEmpty()) {
                resetAttack();
                return;
            }
        } else {
            targets = findTargets(player);
            syncTargets(targets);
            resetAttack();
            // 与 1.21 一致：完成首次锁定的当刻只同步光束，
            // 攻击计时从下一次服务端 tick 开始。
            return;
        }

        attackTime++;
        entityData.set(ATTACK_TIME, attackTime);
        if (attackTime % ATTACK_INTERVAL != 0) {
            return;
        }

        if (getComponent() == null) {
            return;
        }
        // 1.21 的守卫光束只取玩家攻击属性的六分之一；连枷组件基础伤害已经用于
        // 链球本体，不能在光束上再次相乘，否则会把同一武器数值重复放大。
        float damage = (float) player.getAttributeValue(LibAttributes.getAttackDamage()) / 6.0F;
        for (LivingEntity target : targets) {
            target.hurt(level().damageSources().mobAttack(player), damage);
            level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.GUARDIAN_ATTACK, SoundSource.HOSTILE, 1.0F, 1.0F);
        }
    }

    private List<LivingEntity> findTargets(Player player) {
        double range = elder ? 20.0 : 15.0;
        AABB searchBox = getBoundingBox().inflate(range);
        List<LivingEntity> candidates = level().getEntitiesOfClass(LivingEntity.class, searchBox, target -> target != player && target.isAlive() && LibEntityUtils.canHitEntity(target, this) && canSee(target));
        candidates.sort(Comparator.comparingDouble(this::distanceToSqr));
        int count = Math.min(elder ? 3 : 1, candidates.size());
        return new ArrayList<>(candidates.subList(0, count));
    }

    private boolean canContinueTargeting(LivingEntity target) {
        double range = elder ? 20.0 : 15.0;
        return target.isAlive()
                && distanceToSqr(target) <= range * range
                && canSee(target);
    }

    private boolean canSee(Entity target) {
        Vec3 start = position().add(0.0, 0.25, 0.0);
        Vec3 end = target.getBoundingBox().getCenter();
        return level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS;
    }

    private void syncTargets(List<LivingEntity> targets) {
        entityData.set(TARGET_ONE, targetId(targets, 0));
        entityData.set(TARGET_TWO, targetId(targets, 1));
        entityData.set(TARGET_THREE, targetId(targets, 2));
    }

    private static int targetId(List<LivingEntity> targets, int index) {
        return index < targets.size() ? targets.get(index).getId() : 0;
    }

    private void clearTargets() {
        entityData.set(TARGET_ONE, 0);
        entityData.set(TARGET_TWO, 0);
        entityData.set(TARGET_THREE, 0);
    }

    private boolean hasTargetIds() {
        return entityData.get(TARGET_ONE) != 0
                || entityData.get(TARGET_TWO) != 0
                || entityData.get(TARGET_THREE) != 0;
    }

    private void resetAttack() {
        attackTime = 0;
        entityData.set(ATTACK_TIME, 0);
    }

    /// 返回客户端当前可以解析的光束目标。
    public List<LivingEntity> getBeamTargets() {
        List<LivingEntity> targets = new ArrayList<>(3);
        addTarget(targets, entityData.get(TARGET_ONE));
        addTarget(targets, entityData.get(TARGET_TWO));
        addTarget(targets, entityData.get(TARGET_THREE));
        return targets;
    }

    private void addTarget(List<LivingEntity> targets, int entityId) {
        Entity entity = level().getEntity(entityId);
        if (entity instanceof LivingEntity living && living.isAlive()) {
            targets.add(living);
        }
    }

    public boolean isElder() {
        return elder;
    }

    /// 返回客户端光束由暗到亮的暖机进度。
    public float getAttackProgress(float partialTick) {
        return Math.min((entityData.get(ATTACK_TIME) + partialTick) / 20.0F, 1.0F);
    }
}
