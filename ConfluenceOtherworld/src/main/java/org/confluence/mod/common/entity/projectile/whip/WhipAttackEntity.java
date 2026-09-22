package org.confluence.mod.common.entity.projectile.whip;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.mod.api.summon.OwnedSummon;
import org.confluence.mod.api.whip.WhipDirectHitContext;
import org.confluence.mod.api.whip.WhipFriendlyHitContext;
import org.confluence.mod.api.whip.curve.RetractingWhipCurve;
import org.confluence.mod.api.whip.curve.WhipCurveSampler;
import org.confluence.mod.api.whip.curve.WhipCurves;
import org.confluence.mod.common.entity.projectile.DamageSettableProjectile;
import org.confluence.mod.common.entity.projectile.ProjectileHitRules;
import org.confluence.mod.common.init.ModEnchantments;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.item.whip.BaseWhipItem;
import org.confluence.mod.common.summoner.register.SummonerAttachmentTypes;
import org.confluence.mod.common.summoner.summonMark.SummonMarkType;
import org.confluence.mod.mixed.Immunity;

import java.util.*;

/// 一次鞭子挥动对应的短生命周期攻击实体。
///
/// 发射瞬间的位置、视线方向、武器栈和伤害快照保持独立，挥动进度随当前攻速推进。
/// 服务端碰撞和客户端渲染都调用
/// {@link #sampleWorldPoints(float)}，从根源上避免“看到的鞭子”和“实际命中区域”分离。
public final class WhipAttackEntity extends DamageSettableProjectile implements Immunity {
    private static final EntityDataAccessor<ItemStack> WEAPON = SynchedEntityData.defineId(WhipAttackEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Float> DIRECTION_X = SynchedEntityData.defineId(WhipAttackEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DIRECTION_Y = SynchedEntityData.defineId(WhipAttackEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DIRECTION_Z = SynchedEntityData.defineId(WhipAttackEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> RIGHT_ARM = SynchedEntityData.defineId(WhipAttackEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DURATION_TICKS = SynchedEntityData.defineId(WhipAttackEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> RANGE_ATTRIBUTE = SynchedEntityData.defineId(WhipAttackEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> SWEEP_LEVEL = SynchedEntityData.defineId(WhipAttackEntity.class, EntityDataSerializers.INT);
    /// 轨迹以 16 个局部单位表示，因此每点鞭距属性对应 1.6 格世界距离。
    private static final double RANGE_ATTRIBUTE_SCALE = 1.6;
    public static final double RENDER_SEGMENT_SPACING = 0.22;
    private static final int MINIMUM_CLIENT_RETURN_TICKS = 8;

    private static final EntityDataAccessor<Float> SWING_PROGRESS = SynchedEntityData.defineId(WhipAttackEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> PREVIOUS_PROGRESS = SynchedEntityData.defineId(WhipAttackEntity.class, EntityDataSerializers.FLOAT);
    private final Set<UUID> completedTargets = new HashSet<>();
    private final Set<BlockPos> hitBlocks = new HashSet<>();
    private int successfulHits;
    private boolean durabilityConsumed;
    private float clientProgress;
    private float previousClientProgress;
    private boolean clientProgressInitialized;
    private int completionTicks;
    private float clientProgressStep;
    private boolean playbackComplete;
    private boolean completionReported;

    public WhipAttackEntity(EntityType<? extends WhipAttackEntity> type, Level level) {
        super(type, level);
        setNoGravity(true);
        noPhysics = true;
    }

    /// 在统一发射事务预构建阶段写入渲染和轨迹所需的不可变输入。
    public void initialize(ItemStack weapon, Vec3 direction, HumanoidArm arm) {
        if (!(weapon.getItem() instanceof BaseWhipItem item)) {
            throw new IllegalArgumentException("Whip attack weapon must be a BaseWhipItem");
        }
        initialize(weapon, direction, arm, item.durationTicks());
    }

    /// 设置初始时长；玩家攻击随后按实时攻速推进。
    public void initialize(ItemStack weapon, Vec3 direction, HumanoidArm arm, int durationTicks) {
        LivingEntity owner = getLivingOwner();
        float range = owner == null
                ? (float) ConfluenceMagicLib.WHIP_RANGE.value().getDefaultValue()
                : (float) owner.getAttributeValue(ConfluenceMagicLib.WHIP_RANGE);
        initialize(weapon, direction, arm, durationTicks, range);
    }

    /// 保存本次挥动的初始时长与鞭距。
    public void initialize(ItemStack weapon, Vec3 direction, HumanoidArm arm, int durationTicks, float rangeAttribute) {
        if (!(weapon.getItem() instanceof BaseWhipItem)) {
            throw new IllegalArgumentException("Whip attack weapon must be a BaseWhipItem");
        }
        if (direction.lengthSqr() <= 1.0E-12) {
            throw new IllegalArgumentException("Whip attack direction must be non-zero");
        }
        Vec3 normalized = direction.normalize();
        setYRot(getOwner() == null ? (float) (Mth.atan2(-normalized.x, normalized.z) * Mth.RAD_TO_DEG) : getOwner().getYRot());
        entityData.set(WEAPON, weapon.copyWithCount(1));
        entityData.set(DIRECTION_X, (float) normalized.x);
        entityData.set(DIRECTION_Y, (float) normalized.y);
        entityData.set(DIRECTION_Z, (float) normalized.z);
        entityData.set(RIGHT_ARM, arm == HumanoidArm.RIGHT);
        if (durationTicks <= 0) {
            throw new IllegalArgumentException("Whip duration must be positive");
        }
        entityData.set(DURATION_TICKS, durationTicks);
        if (rangeAttribute <= 0.0F) {
            throw new IllegalArgumentException("Whip range must be positive");
        }
        entityData.set(RANGE_ATTRIBUTE, rangeAttribute);
        int enchantmentLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.WHIP_SWEEP.get(), weapon);
        entityData.set(SWEEP_LEVEL, enchantmentLevel > 0 && getRandom1211().nextFloat() < 0.2F ? enchantmentLevel : 0);
        setDeltaMovement(normalized);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(WEAPON, ItemStack.EMPTY);
        entityData.define(DIRECTION_X, 0.0F);
        entityData.define(DIRECTION_Y, 0.0F);
        entityData.define(DIRECTION_Z, 1.0F);
        entityData.define(RIGHT_ARM, true);
        entityData.define(DURATION_TICKS, 1);
        entityData.define(RANGE_ATTRIBUTE, 1.0F);
        entityData.define(SWEEP_LEVEL, 0);
        entityData.define(SWING_PROGRESS, 0.0F);
        entityData.define(PREVIOUS_PROGRESS, 0.0F);
    }

    @Override
    public void tick() {
        super.tick();
        if (isRemoved()) {
            return;
        }
        LivingEntity owner = getLivingOwner();
        BaseWhipItem whip = whipItem();
        if (owner == null || whip == null || !owner.isAlive()) {
            if (!level().isClientSide) {
                discard();
            }
            return;
        }

        if (owner instanceof Player player)
            setPos(BaseWhipItem.handPosition(player, attackArm(), 1.0F));
        if (level().isClientSide) {
            advanceClientProgress();
            return;
        }
        if (!level().isClientSide) {
            float previous = entityData.get(SWING_PROGRESS);
            if (previous >= 1.0F) {
                // 正常等待持有者播放完成；非玩家来源及异常客户端保留有界清理。
                int timeout = owner instanceof net.minecraft.server.level.ServerPlayer ? 100 : 2;
                if (playbackComplete || ++completionTicks >= timeout) discard();
                return;
            }
            float step = owner instanceof Player player ? BaseWhipItem.swingStep(player) : 1.0F / durationTicks();
            float progress = Math.min(1.0F, previous + step);
            if (progress >= 1.0F - 1.0E-6F) progress = 1.0F;
            entityData.set(PREVIOUS_PROGRESS, previous);
            entityData.set(SWING_PROGRESS, progress);
            float soundProgress = sweepLevel() > 0 ? WhipCurves.SWEEP_SOUND_PROGRESS : WhipCurves.SNAP_PROGRESS;
            if (previous < soundProgress && progress >= soundProgress) {
                owner.playSound(ModSoundEvents.WHIP_ATTACK.get(), 0.6F + getRandom1211().nextFloat() * 0.2F, 1.0F);
            }
            // 高攻速下也走过中间轨迹，避免一个 tick 跨过整段伸展而漏判。
            int samples = Mth.ceil((progress - previous) / 0.05F);
            for (int sample = 1; sample <= samples; sample++) {
                entityData.set(PREVIOUS_PROGRESS, Mth.lerp((sample - 1) / (float) samples, previous, progress));
                entityData.set(SWING_PROGRESS, Mth.lerp(sample / (float) samples, previous, progress));
                hitAlongCurrentCurve(owner, whip);
            }
            entityData.set(PREVIOUS_PROGRESS, previous);
            entityData.set(SWING_PROGRESS, progress);
        }
    }

    public float swingProgress(float partialTick) {
        return level().isClientSide ? Mth.lerp(partialTick, previousClientProgress, clientProgress) : entityData.get(SWING_PROGRESS);
    }

    public void confirmPlaybackComplete(net.minecraft.server.level.ServerPlayer player) {
        if (getOwner() == player && entityData.get(SWING_PROGRESS) >= 1.0F) playbackComplete = true;
    }

    public boolean reportPlaybackComplete() {
        if (!level().isClientSide || completionReported || entityData.get(SWING_PROGRESS) < 1.0F || previousClientProgress < 1.0F)
            return false;
        completionReported = true;
        return true;
    }

    private void advanceClientProgress() {
        float serverProgress = entityData.get(SWING_PROGRESS);
        float serverPrevious = entityData.get(PREVIOUS_PROGRESS);
        float step = Math.max(0.0F, serverProgress - serverPrevious);
        if (!clientProgressInitialized) {
            // 出生数据尚未到齐时不能按默认的一 tick 时长播放完整次挥动。
            if (step <= 0.0F) return;
            clientProgress = 0.0F;
            clientProgressInitialized = true;
        }
        previousClientProgress = clientProgress;
        if (step > 0.0F && (serverProgress < 1.0F || clientProgressStep == 0.0F))
            clientProgressStep = step;
        if (serverProgress >= 1.0F) {
            // 逻辑攻击已结束，但显示仍需走完剩余回收，不能直接跳到终帧。
            clientProgress = limitClientReturnProgress(Math.min(1.0F, clientProgress + clientProgressStep));
            return;
        }
        float predicted = clientProgress + step;
        // 插值区间从当前服务端进度向前预测一 tick，避免实体移除时仍滞后在回收段。
        float targetProgress = Math.min(1.0F, serverProgress + step);
        float correction = Mth.clamp(targetProgress - predicted, -step * 0.5F, step * 0.5F);
        float predictionLimit = Math.max(clientProgress, targetProgress);
        clientProgress = limitClientReturnProgress(Mth.clamp(predicted + correction, clientProgress, predictionLimit));
    }

    private float limitClientReturnProgress(float next) {
        BaseWhipItem whip = whipItem();
        if (sweepLevel() > 0 || whip == null || !(whip.curve() instanceof RetractingWhipCurve curve))
            return next;
        float start = (float) curve.returnStart();
        // 去程到回收的边界至少显示一帧，回收期间不允许网络追赶压缩播放时间。
        if (clientProgress < start) return Math.min(next, start);
        float maximumStep = (1.0F - start) / MINIMUM_CLIENT_RETURN_TICKS;
        return Math.min(next, Math.min(1.0F, clientProgress + maximumStep));
    }

    @Override
    public Type confluence$getImmunityType() {return Type.LOCAL;}

    @Override
    public int confluence$getImmunityDuration(DamageSource source) {
        BaseWhipItem whip = whipItem();
        return whip == null ? 0 : whip.hitCooldownTicks();
    }

    /// 按当前逻辑 tick 和局部帧插值生成世界坐标折线。
    ///
    /// @param partialTick 客户端渲染帧的局部 tick；服务端碰撞传 {@code 0}
    public List<Vec3> sampleWorldPoints(float partialTick) {
        BaseWhipItem whip = whipItem();
        LivingEntity owner = getLivingOwner();
        if (whip == null || owner == null) {
            return List.of(position());
        }
        double progress = swingProgress(partialTick);
        return sampleWorldPointsAtProgress(owner, whip, progress, partialTick);
    }

    private List<Vec3> sampleWorldPointsAtProgress(LivingEntity owner, BaseWhipItem whip, double progress, float partialTick) {
        List<Vec3> localPoints = WhipCurveSampler.sample(
                sweepLevel() > 0 ? WhipCurves.SWEEP : whip.curve(),
                progress,
                RANGE_ATTRIBUTE_SCALE * entityData.get(RANGE_ATTRIBUTE),
                RENDER_SEGMENT_SPACING);
        return transformLocalPoints(localPoints, partialTick);
    }

    private List<Vec3> transformLocalPoints(List<Vec3> localPoints, float partialTick) {
        Vec3 forward = launchDirection();
        // 以发射视线建立坐标系，垂直瞄准时仍保留左右手方向。
        Vec3 right = swingRight();
        Vec3 up = right.cross(forward).normalize();
        int side = attackArm() == HumanoidArm.RIGHT ? 1 : -1;
        Vec3 origin = curveOrigin(partialTick);
        ArrayList<Vec3> result = new ArrayList<>(localPoints.size());
        for (Vec3 point : localPoints) {
            result.add(origin.add(forward.scale(-point.x)).add(up.scale(point.y)).add(right.scale(point.z * side)));
        }
        return result;
    }

    private Vec3 curveOrigin(float partialTick) {
        return getOwner() instanceof Player player ? BaseWhipItem.handPosition(player, attackArm(), level().isClientSide ? partialTick : 1.0F) : position();
    }

    public Vec3 swingRight() {
        Vec3 forward = launchDirection();
        Vec3 right = new Vec3(-forward.z, 0.0, forward.x);
        // 普通瞄准完全由同一份发射方向建立正交坐标系，避免实体 yaw 与方向不同步。
        return right.lengthSqr() > 1.0E-10 ? right.normalize() : Vec3.directionFromRotation(0.0F, getYRot() + 90.0F);
    }

    /// 按固定间距采样攻击曲线，供方块命中检测使用。
    private List<Vec3> sampleWorldControlPoints(LivingEntity owner, BaseWhipItem whip) {
        return sampleWorldControlPoints(owner, whip, 0.0F);
    }

    private List<Vec3> sampleWorldControlPoints(LivingEntity owner, BaseWhipItem whip, float partialTick) {
        double progress = swingProgress(partialTick);
        List<Vec3> localPoints = WhipCurveSampler.sample(sweepLevel() > 0 ? WhipCurves.SWEEP : whip.curve(), progress, RANGE_ATTRIBUTE_SCALE * entityData.get(RANGE_ATTRIBUTE), 1.0);
        return transformLocalPoints(localPoints, partialTick);
    }

    public ItemStack weapon() {
        return entityData.get(WEAPON).copy();
    }

    public boolean representsHeldWeapon(Player player, ItemStack stack, HumanoidArm arm) {
        return !isRemoved() && getOwner() == player && attackArm() == arm && ItemStack.isSameItemSameTags(entityData.get(WEAPON), stack);
    }

    /// 返回本次攻击实际使用的手臂，供第三人称手部锚点计算使用。
    public HumanoidArm attackArm() {
        return entityData.get(RIGHT_ARM) ? HumanoidArm.RIGHT : HumanoidArm.LEFT;
    }

    /// 返回初始时长，非玩家来源使用它作为进度回退值。
    public int durationTicks() {
        return Math.max(1, entityData.get(DURATION_TICKS));
    }

    /// 返回本次挥动实际触发的横扫附魔等级；未触发时为零。
    public int sweepLevel() {
        return Math.max(0, entityData.get(SWEEP_LEVEL));
    }

    private void hitAlongCurrentCurve(LivingEntity owner, BaseWhipItem whip) {
        hitBlocksAlongControlPoints(owner, whip);
        double radius = 1.5 + (sweepLevel() > 0 ? 0.5 : 0.0);
        List<Vec3> currentCurve = sampleWorldPoints(0.0F);
        List<Vec3> previousCurve = tickCount > 0
                ? sampleWorldPointsAtProgress(owner, whip, entityData.get(PREVIOUS_PROGRESS), 0.0F)
                : List.of();
        AABB bounds = curveBounds(currentCurve);
        if (!previousCurve.isEmpty()) {
            bounds = bounds.minmax(curveBounds(previousCurve));
        }
        List<Entity> candidates = level().getEntities(this, bounds.inflate(radius), rawTarget -> {
            Entity identity = ProjectileHitRules.dedupeIdentity(rawTarget);
            LivingEntity logicalTarget = ProjectileHitRules.logicalLivingTarget(rawTarget);
            return logicalTarget != null
                    && logicalTarget != owner
                    && canHitAgain(identity.getUUID())
                    && (ProjectileHitRules.canHit(owner, rawTarget)
                    || isFriendlySummon(owner, logicalTarget, whip));
        });
        candidates.sort(Comparator.comparingDouble(rawTarget -> WhipCollisionGeometry.firstContactProgress(
                previousCurve, currentCurve, rawTarget.getBoundingBox().inflate(radius))));
        for (Entity rawTarget : candidates) {
            Entity damageRecipient = ProjectileHitRules.damageRecipient(rawTarget);
            Entity identity = ProjectileHitRules.dedupeIdentity(rawTarget);
            LivingEntity logicalTarget = ProjectileHitRules.logicalLivingTarget(rawTarget);
            if (logicalTarget == null || !canHitAgain(identity.getUUID())) {
                continue;
            }
            if (!WhipCollisionGeometry.intersectsSweptCurve(previousCurve, currentCurve, rawTarget.getBoundingBox().inflate(radius))) {
                continue;
            }
            if (!whip.penetratesBlocks() && !canReachTarget(owner, rawTarget)) {
                continue;
            }
            if (applyFriendlyHit(owner, logicalTarget, whip)) {
                completedTargets.add(identity.getUUID());
                continue;
            }
            hitTarget(owner, damageRecipient, logicalTarget, identity, whip);
        }
    }

    private static AABB curveBounds(List<Vec3> curve) {
        Vec3 first = curve.get(0);
        AABB result = new AABB(first, first);
        for (int index = 1; index < curve.size(); index++) {
            Vec3 point = curve.get(index);
            result = result.minmax(new AABB(point, point));
        }
        return result;
    }

    /// 让鞭子对附近方块触发 {@code onProjectileHit} 行为。
    ///
    /// 方块扫描独立使用一格的采样间距，不随模型分段精度增加工作量。
    /// 同一次挥动内按方块坐标去重，避免同一方块在相邻帧和相邻控制点被重复触发。
    private void hitBlocksAlongControlPoints(LivingEntity owner, BaseWhipItem whip) {
        double radius = 1.5 + (sweepLevel() > 0 ? 0.5 : 0.0);
        Direction direction = Direction.getNearest((float) launchDirection().x, (float) launchDirection().y, (float) launchDirection().z);
        for (Vec3 point : sampleWorldControlPoints(owner, whip)) {
            BlockPos min = BlockPos.containing(point.x - radius, point.y - radius, point.z - radius);
            BlockPos max = BlockPos.containing(point.x + radius, point.y + radius, point.z + radius);
            for (BlockPos candidate : BlockPos.betweenClosed(min, max)) {
                BlockPos blockPos = candidate.immutable();
                if (!hitBlocks.add(blockPos)) {
                    continue;
                }
                BlockState state = level().getBlockState(blockPos);
                state.onProjectileHit(level(), state, new BlockHitResult(blockPos.getCenter(), direction, blockPos, true), this);
            }
        }
    }

    private void hitTarget(LivingEntity owner, Entity damageRecipient, LivingEntity logicalTarget,
                           Entity identity, BaseWhipItem whip) {
        if (!canHitAgain(identity.getUUID())) {
            return;
        }
        float baseDamage = getDamage() > 0.0F ? getDamage() : whip.baseDamage();
        float multiplier = Math.max(whip.minimumDamageMultiplier(), (float) Math.pow(whip.damageFalloff(), successfulHits));
        float damage = baseDamage * multiplier
                * (1.0F + sweepLevel() * 0.2F);
        if (Immunity.isActive(this, logicalTarget)) return;
        if (!Immunity.withCause(this, () -> LibDamageTypes.hurtWithoutKnockback(damageRecipient,
                LibDamageTypes.of(level(), LibDamageTypes.SUMMON, this, owner), damage))) {
            return;
        }
        completedTargets.add(identity.getUUID());
        int hitIndex = successfulHits++;
        if (owner instanceof Player player) {
            consumeDurabilityAfterFirstEnemyHit(player);
            player.setLastHurtMob(logicalTarget);
            if (weapon().getItem() instanceof BaseWhipItem baseWhipItem && baseWhipItem.shouldApplyTag(hitIndex) && baseWhipItem.getSummonMarkType() != null) {
                SummonMarkType markType = baseWhipItem.getSummonMarkType();
                player.getData(SummonerAttachmentTypes.SUMMON_MARK_DATA).tracker(logicalTarget, markType.createInstance(player, logicalTarget));
            }
            WhipDirectHitContext context = new WhipDirectHitContext(player, logicalTarget, weapon(), damage, hitIndex);
            whip.onDirectHit(context);
        }
    }

    private boolean canHitAgain(UUID targetId) {
        return !completedTargets.contains(targetId);
    }

    /// 只在本次挥动首次命中合法敌人后消耗一点耐久。
    private void consumeDurabilityAfterFirstEnemyHit(Player player) {
        if (durabilityConsumed) {
            return;
        }
        HumanoidArm attackArm = entityData.get(RIGHT_ARM)
                ? HumanoidArm.RIGHT : HumanoidArm.LEFT;
        InteractionHand hand = attackArm == player.getMainArm()
                ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        ItemStack liveWeapon = player.getItemInHand(hand);
        ItemStack firedWeapon = weapon();
        if (!liveWeapon.isDamageableItem() || !ItemStack.isSameItemSameTags(liveWeapon, firedWeapon)) {
            return;
        }
        EquipmentSlot slot = hand == InteractionHand.MAIN_HAND
                ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
        liveWeapon.hurtAndBreak(1, player, broken -> broken.broadcastBreakEvent(slot));
        durabilityConsumed = true;
    }

    private boolean isFriendlySummon(LivingEntity owner, LivingEntity target, BaseWhipItem whip) {
        if (!whip.canHitFriendlySummons() || !(owner instanceof Player player) || !(level() instanceof ServerLevel serverLevel) || !(target instanceof OwnedSummon summon)) {
            return false;
        }
        return summon.resolveSummonOwner(serverLevel) == player;
    }

    private boolean applyFriendlyHit(LivingEntity owner, LivingEntity target, BaseWhipItem whip) {
        if (!isFriendlySummon(owner, target, whip) || !(owner instanceof Player player)) {
            return false;
        }
        if (Immunity.isActive(this, target)) return false;
        WhipFriendlyHitContext context = new WhipFriendlyHitContext(player, target, weapon());
        whip.onFriendlyHit(context);
        float baseDamage = getDamage() > 0.0F ? getDamage() : whip.baseDamage();
        Immunity.withCause(this, () -> LibDamageTypes.hurtWithoutKnockback(target, LibDamageTypes.of(level(), LibDamageTypes.SUMMON, this, owner), baseDamage * 0.2F));
        return true;
    }

    private boolean hasLineOfSight(Vec3 from, Vec3 to) {
        return level().clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS;
    }

    private boolean canReachTarget(LivingEntity owner, Entity target) {
        Vec3 eyes = curveOrigin(0.0F);
        AABB box = target.getBoundingBox();
        return hasLineOfSight(eyes, new Vec3(target.getX(), box.maxY, target.getZ()))
                || hasLineOfSight(eyes, target.position())
                || hasLineOfSight(eyes, new Vec3(target.getX(), box.getCenter().y, target.getZ()));
    }

    private BaseWhipItem whipItem() {
        ItemStack stack = entityData.get(WEAPON);
        return stack.getItem() instanceof BaseWhipItem item ? item : null;
    }

    private Vec3 launchDirection() {
        Vec3 direction = new Vec3(entityData.get(DIRECTION_X), entityData.get(DIRECTION_Y), entityData.get(DIRECTION_Z));
        return direction.lengthSqr() <= 1.0E-12 ? new Vec3(0.0, 0.0, 1.0) : direction.normalize();
    }

    /// 一次挥鞭只在当前攻击时段存在，不能跨世界保存。
    ///
    /// 这样也避免重新载入时只恢复通用弹幕快照、却缺少挥动进度和
    /// 轨迹历史而生成残缺攻击。
    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        Entity owner = getOwner();
        int ownerId = owner == null ? 0 : owner.getId();
        return new ClientboundAddEntityPacket(getId(), getUUID(), getX(), getY(), getZ(), getXRot(), getYRot(), getType(), ownerId, getDeltaMovement(), 0.0);
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        Vec3 direction = new Vec3(packet.getXa(), packet.getYa(), packet.getZa());
        if (direction.lengthSqr() > 1.0E-12) {
            entityData.set(DIRECTION_X, (float) direction.x);
            entityData.set(DIRECTION_Y, (float) direction.y);
            entityData.set(DIRECTION_Z, (float) direction.z);
        }
    }
}
