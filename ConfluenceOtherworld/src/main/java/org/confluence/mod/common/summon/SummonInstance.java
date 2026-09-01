package org.confluence.mod.common.summon;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.mod.api.summon.OwnedSummon;
import org.confluence.mod.api.summon.SummonTargetCache;
import org.confluence.mod.api.whip.WhipTagTracker;
import org.confluence.mod.common.entity.boss.BaseBoss;
import org.confluence.mod.common.entity.boss.BaseWormBoss;
import org.confluence.mod.common.entity.monster.BaseWormMonster;
import org.confluence.mod.common.entity.projectile.ProjectileHitRules;
import org.confluence.mod.mixed.Immunity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

/// 由玩家持有的召唤物运行实例。
public abstract class SummonInstance implements OwnedSummon, Immunity {
    private UUID uuid = UUID.randomUUID();
    private final ResourceLocation type;
    private final ServerPlayer owner;
    private int slotCost;
    private final long summonedAt;
    private SummonStats stats;
    private final SummonGoalSelector goalSelector = new SummonGoalSelector();
    private SummonPath path;
    private SummonPose currentPose;
    private SummonPose previousPose;
    private SummonPose previousPreviousPose;
    private LivingEntity target;
    private Entity actualTarget;
    private Vec3 velocity = Vec3.ZERO;
    private boolean removed;
    private int tickCount;
    private boolean trackingOwnerRecovery;
    private int ownerRecoveryCooldown;
    private int order;
    private int sameTypeCount = 1;
    private final Map<UUID, Integer> nextPartHitTicks = new HashMap<>();

    protected SummonInstance(ResourceLocation type, ServerPlayer owner, int slotCost, SummonStats stats, SummonPose initialPose) {
        this.type = Objects.requireNonNull(type, "Summon type must not be null");
        this.owner = Objects.requireNonNull(owner, "Summon owner must not be null");
        this.stats = Objects.requireNonNull(stats, "Summon stats must not be null");
        if (slotCost <= 0) {
            throw new IllegalArgumentException("Summon slot cost must be positive");
        }
        this.slotCost = slotCost;
        this.summonedAt = owner.level().getGameTime();
        initializePose(initialPose);
    }

    public final void tick() {
        if (removed || !owner.isAlive() || owner.isRemoved()) {
            remove();
            return;
        }
        updateOwnerRecovery();
        LivingEntity previousTarget = target;
        target = findTarget();
        actualTarget = resolveActualTarget(target);
        onTargetChanged(previousTarget, target);
        beforeGoalTick();
        goalSelector.tick();
        afterGoalTick();
        advancePath();
        afterPathAdvance(previousPreviousPose, previousPose, currentPose);
        previousPreviousPose = previousPose;
        previousPose = currentPose;
        tickCount++;
    }

    protected abstract LivingEntity findTarget();

    protected void onTargetChanged(LivingEntity previousTarget, LivingEntity currentTarget) {}

    protected void beforeGoalTick() {}

    protected void afterGoalTick() {}

    /// 在不改变逻辑受伤本体的前提下，按部件注册顺序选择首个可视部件作为移动和瞄准目标。
    /// 伤害仍然结算到 {@link #target()}，避免多部件 Boss 被重复计算伤害。
    private Entity resolveActualTarget(LivingEntity logicalTarget) {
        if (logicalTarget == null) {
            return null;
        }
        List<? extends Entity> parts;
        if (logicalTarget instanceof BaseWormBoss wormBoss) {
            parts = wormBoss.getSegments();
        } else if (logicalTarget instanceof BaseWormMonster wormMonster) {
            parts = wormMonster.getSegments();
        } else if (logicalTarget instanceof BaseBoss boss) {
            parts = boss.getSubEntities();
        } else {
            return logicalTarget;
        }
        for (Entity part : parts) {
            Vec3 partCenter = part.getBoundingBox().getCenter();
            if (!part.isAlive() || !ProjectileHitRules.canHit(owner, part)
                    || ProjectileHitRules.logicalLivingTarget(part) != logicalTarget || !hasLineOfSight(partCenter)) {
                continue;
            }
            return part;
        }
        return logicalTarget;
    }

    private boolean hasLineOfSight(Vec3 targetPosition) {
        return owner.level().clip(new ClipContext(position(), targetPosition, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, owner)).getType() == HitResult.Type.MISS;
    }

    /// 返回超过该距离平方后需要拉回所有者附近的阈值。
    protected double ownerRecoveryDistanceSqr() {
        return 40.0 * 40.0;
    }

    protected int ownerRecoveryInterval() {
        return 10;
    }

    protected boolean usesOwnerRecovery() {
        return true;
    }

    /// 判断召唤物能否恢复到候选位置。
    /// 飞行召唤物与无碰撞召唤物默认允许，具有实体碰撞体积的召唤物由子类继续检查方块碰撞。
    protected boolean canRecoverAt(Vec3 position) {
        return true;
    }

    private void updateOwnerRecovery() {
        if (!usesOwnerRecovery()) return;
        double distanceSqr = position().distanceToSqr(owner.position());
        if (distanceSqr < 32.0 * 32.0) {
            trackingOwnerRecovery = false;
            return;
        }
        if (!trackingOwnerRecovery) {
            trackingOwnerRecovery = true;
            ownerRecoveryCooldown = 0;
        }
        if (--ownerRecoveryCooldown > 0) return;
        ownerRecoveryCooldown = Math.max(1, ownerRecoveryInterval());
        if (distanceSqr < ownerRecoveryDistanceSqr()) return;
        BlockPos ownerBlockPosition = owner.blockPosition();
        for (int attempt = 0; attempt < 10; attempt++) {
            int offsetX = owner.getRandom().nextIntBetweenInclusive(-3, 3);
            int offsetZ = owner.getRandom().nextIntBetweenInclusive(-3, 3);
            if (Math.abs(offsetX) < 2 && Math.abs(offsetZ) < 2) {
                continue;
            }
            int offsetY = owner.getRandom().nextIntBetweenInclusive(-1, 1);
            Vec3 candidate = new Vec3(ownerBlockPosition.getX() + offsetX + 0.5D, ownerBlockPosition.getY() + offsetY, ownerBlockPosition.getZ() + offsetZ + 0.5D);
            if (!canRecoverAt(candidate)) {
                continue;
            }
            initializePose(new SummonPose(candidate, owner.getYRot(), 0.0F, 0.0F));
            velocity = Vec3.ZERO;
            path = null;
            return;
        }
    }

    /// 路径推进后、上一刻姿态更新前调用。
    protected void afterPathAdvance(SummonPose previousPreviousPose, SummonPose previousPose, SummonPose currentPose) {}

    protected final void addGoal(int priority, SummonGoal<?> goal) {
        goalSelector.addGoal(priority, goal);
    }

    public final void setPath(SummonPath path) {
        this.path = Objects.requireNonNull(path, "Summon path must not be null");
    }

    public final void setPath(String identifier, List<SummonPose> poses) {
        setPath(new SummonPath(identifier, poses));
    }

    public final boolean isExecutingPath() {
        return path != null && !path.isFinished();
    }

    /// 直接推进一个游戏刻的姿态；多节点轨迹仍使用 {@link #setPath(SummonPath)}。
    protected final void advanceTo(SummonPose pose) {
        SummonPose next = Objects.requireNonNull(pose, "Next summon pose must not be null");
        path = null;
        velocity = next.position().subtract(currentPose.position());
        currentPose = next;
    }

    public final Vec3 currentVelocity() {
        return velocity.lengthSqr() > 1.0E-5 ? velocity.normalize() : Vec3.directionFromRotation(currentPose.pitch(), currentPose.yaw()).normalize();
    }

    public final Vec3 velocity() {
        return velocity;
    }

    public final Vec3 currentNormal() {
        Quaternionf rotation = new Quaternionf().rotateY((float) Math.toRadians(-currentPose.yaw()))
                .rotateX((float) Math.toRadians(currentPose.pitch())).rotateZ((float) Math.toRadians(currentPose.roll()));
        Vector3f normal = new Vector3f(0.0F, 1.0F, 0.0F).rotate(rotation);
        return new Vec3(normal.x(), normal.y(), normal.z()).normalize();
    }

    public final SummonPose poseFromAxes(Vec3 position, Vec3 direction, Vec3 normal) {
        Vec3 forward = direction.normalize();
        Vec3 up = normal.normalize();
        if (forward.lengthSqr() < 1.0E-6 || up.lengthSqr() < 1.0E-6) {
            throw new IllegalArgumentException("Summon pose axes must not be zero-length");
        }
        float yaw = (float) Math.toDegrees(Math.atan2(-forward.x, forward.z));
        float pitch = (float) Math.toDegrees(Math.asin(-forward.y));
        float pitchRadians = (float) Math.toRadians(pitch);
        float yawRadians = (float) Math.toRadians(yaw);
        Vec3 localUp = new Vec3(-Math.sin(yawRadians) * Math.sin(pitchRadians), Math.cos(pitchRadians), Math.cos(yawRadians) * Math.sin(pitchRadians));
        Vec3 projectedLocalUp = localUp.subtract(forward.scale(localUp.dot(forward))).normalize();
        Vec3 projectedUp = up.subtract(forward.scale(up.dot(forward))).normalize();
        float roll = (float) Math.toDegrees(Math.atan2(projectedLocalUp.cross(projectedUp).dot(forward), projectedLocalUp.dot(projectedUp)));
        return new SummonPose(position, yaw, pitch, roll);
    }

    /// 使用实例保存的基础伤害和主人当前召唤伤害结算命中，并由局部无敌帧限制同一实例的命中频率。
    protected final boolean hurtTarget(LivingEntity target, float damageMultiplier) {
        return hurtEntity(target, target, target, damageMultiplier);
    }

    protected final boolean hurtEntity(Entity damageRecipient, LivingEntity encounterOwner,
                                       Entity dedupeIdentity, float damageMultiplier) {
        Objects.requireNonNull(damageRecipient, "Summon damage recipient must not be null");
        Objects.requireNonNull(encounterOwner, "Summon encounter owner must not be null");
        Objects.requireNonNull(dedupeIdentity, "Summon dedupe identity must not be null");
        if (!Float.isFinite(damageMultiplier) || damageMultiplier < 0.0F) {
            throw new IllegalArgumentException("Summon damage multiplier must be finite and non-negative");
        }
        if (!SummonTargetCache.isValidTarget(owner, encounterOwner, Double.MAX_VALUE, true)) {
            return false;
        }
        float damage = stats.baseDamage() * (float) owner.getAttributeValue(LibAttributes.getSummonDamage());
        damage = WhipTagTracker.modifyDamage(owner, this, encounterOwner, damage * damageMultiplier);
        DamageSource source = LibDamageTypes.of(owner.level(), LibDamageTypes.SUMMONER, owner);
        if (damageRecipient instanceof LivingEntity living) {
            return Immunity.hurt(this, living, source, damage);
        }
        UUID identity = dedupeIdentity.getUUID();
        if (tickCount < nextPartHitTicks.getOrDefault(identity, Integer.MIN_VALUE)) {
            return false;
        }
        float resolvedDamage = damage;
        boolean hurt = Immunity.withCause(this, () -> damageRecipient.hurt(source, resolvedDamage));
        if (hurt) {
            nextPartHitTicks.put(identity, tickCount + confluence$getImmunityDuration(source));
        }
        return hurt;
    }

    /// 对指定范围内的全部合法目标结算接触伤害，命中频率仍由每个召唤实例的局部无敌帧控制。
    protected final boolean hurtTouchingTargets(AABB bounds, double targetRange, float damageMultiplier) {
        boolean hit = false;
        Set<UUID> hitEntities = new HashSet<>();
        for (Entity rawTarget : owner.level().getEntities((Entity) null, bounds, candidate -> ProjectileHitRules.canHit(owner, candidate))) {
            Entity damageRecipient = ProjectileHitRules.damageRecipient(rawTarget);
            Entity identity = ProjectileHitRules.dedupeIdentity(rawTarget);
            LivingEntity logicalTarget = ProjectileHitRules.logicalLivingTarget(rawTarget);
            if (logicalTarget == null || !hitEntities.add(identity.getUUID())) continue;
            if (logicalTarget != target && !SummonTargetCache.isValidTarget(owner, logicalTarget, position(), targetRange, false))
                continue;
            hit |= hurtEntity(damageRecipient, logicalTarget, identity, damageMultiplier);
        }
        return hit;
    }

    @Override
    public Type confluence$getImmunityType() {
        return Type.LOCAL;
    }

    @Override
    public int confluence$getImmunityDuration(DamageSource damageSource) {
        return 5;
    }

    private void advancePath() {
        if (path == null || path.isFinished()) {
            return;
        }
        SummonPose next = path.advance();
        if (next != null) {
            velocity = next.position().subtract(currentPose.position());
            currentPose = next;
        }
    }

    public final void initializePose(SummonPose pose) {
        currentPose = Objects.requireNonNull(pose, "Initial summon pose must not be null");
        previousPose = pose;
        previousPreviousPose = pose;
    }

    public final SummonPose renderPose(float partialTick) {
        return previousPose.interpolate(currentPose, partialTick);
    }

    public final void remove() {
        if (!removed) {
            removed = true;
            onRemoved();
        }
    }

    protected void onRemoved() {
        SummonTargetCache.invalidate(owner.serverLevel(), owner.getUUID(), uuid);
    }

    /// 返回当前客户端表现状态；不需要额外动作的召唤物使用默认值。
    public SummonVisualState visualState() {
        return SummonVisualState.DEFAULT;
    }

    /// 向同步批次追加纯客户端可视部件；星尘龙等复合召唤物可追加多个部件。
    public void appendRenderParts(List<SummonRenderPart> output) {
        output.add(new SummonRenderPart(uuid, type, currentPose, visualState(), order));
    }

    /// 同类运行实例可以覆盖此方法，把新增槽位合并进自身。
    public boolean canMergeAdditionalSummon() {
        return false;
    }

    public boolean tryMergeAdditionalSummon(int additionalSlots, SummonStats stats) {
        return false;
    }

    protected final void increaseSlotCost(int additionalSlots) {
        if (additionalSlots <= 0) {
            throw new IllegalArgumentException("Additional summon slots must be positive");
        }
        slotCost += additionalSlots;
    }

    protected final void replaceStats(SummonStats stats) {
        this.stats = Objects.requireNonNull(stats, "Summon stats must not be null");
    }

    public final UUID uuid() {
        return uuid;
    }

    final void restoreUuid(UUID uuid) {
        this.uuid = Objects.requireNonNull(uuid, "Restored summon UUID must not be null");
    }

    @Override
    public final UUID getSummonOwnerId() {
        return owner.getUUID();
    }

    public final ResourceLocation type() {
        return type;
    }

    /// 返回编队排序使用的分组标识；同一行为家族可覆盖它以共享连续序号。
    public ResourceLocation groupKey() {
        return type;
    }

    public final ServerPlayer owner() {
        return owner;
    }

    public final int slotCost() {
        return slotCost;
    }

    public final long summonedAt() {
        return summonedAt;
    }

    public final SummonStats stats() {
        return stats;
    }

    public final SummonGoalSelector goalSelector() {
        return goalSelector;
    }

    public final SummonPath path() {
        return path;
    }

    public final SummonPose currentPose() {
        return currentPose;
    }

    public final Vec3 position() {
        return currentPose.position();
    }

    public final LivingEntity target() {
        return target;
    }

    public final Entity actualTarget() {
        return actualTarget;
    }

    public final Vec3 targetPosition() {
        if (actualTarget == null) {
            return position();
        }
        return actualTarget == target ? target.getEyePosition() : actualTarget.getBoundingBox().getCenter();
    }

    public final Vec3 targetBasePosition() {
        if (actualTarget == null) {
            return position();
        }
        return actualTarget == target ? target.position() : actualTarget.getBoundingBox().getCenter();
    }

    public final AABB targetBounds() {
        return actualTarget == null ? AABB.ofSize(position(), 0.0, 0.0, 0.0) : actualTarget.getBoundingBox();
    }

    public final boolean isRemoved() {
        return removed;
    }

    public final int tickCount() {
        return tickCount;
    }

    public final int order() {
        return order;
    }

    public final int sameTypeCount() {
        return sameTypeCount;
    }

    final void updateGroupState(int order, int sameTypeCount) {
        this.order = order;
        this.sameTypeCount = sameTypeCount;
    }
}
