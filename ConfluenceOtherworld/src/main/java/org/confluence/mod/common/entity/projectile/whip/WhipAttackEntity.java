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
import org.confluence.mod.api.whip.WhipDefinition;
import org.confluence.mod.api.whip.WhipDirectHitContext;
import org.confluence.mod.api.whip.WhipFriendlyHitContext;
import org.confluence.mod.api.whip.WhipTagTracker;
import org.confluence.mod.api.whip.curve.WhipCurveSampler;
import org.confluence.mod.api.whip.curve.WhipCurves;
import org.confluence.mod.common.entity.projectile.DamageSettableProjectile;
import org.confluence.mod.common.entity.projectile.ProjectileHitRules;
import org.confluence.mod.common.init.ModEnchantments;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.item.whip.BaseWhipItem;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

/// 一次鞭子挥动对应的短生命周期攻击实体。
///
/// <p>实体本身不飞行，发射瞬间的位置、视线方向、武器栈和战斗快照都会被冻结；
/// 之后即使玩家移动或切换物品，也不会改变本次挥动的伤害、暴击或轨迹。服务端碰撞和客户端渲染都调用
/// {@link #sampleWorldPoints(float)}，从根源上避免“看到的鞭子”和“实际命中区域”分离。</p>
public final class WhipAttackEntity extends DamageSettableProjectile {
    private static final EntityDataAccessor<ItemStack> WEAPON = SynchedEntityData.defineId(WhipAttackEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Float> DIRECTION_X = SynchedEntityData.defineId(WhipAttackEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DIRECTION_Y = SynchedEntityData.defineId(WhipAttackEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DIRECTION_Z = SynchedEntityData.defineId(WhipAttackEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> RIGHT_ARM = SynchedEntityData.defineId(WhipAttackEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DURATION_TICKS = SynchedEntityData.defineId(WhipAttackEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SWEEP_LEVEL = SynchedEntityData.defineId(WhipAttackEntity.class, EntityDataSerializers.INT);
    /// 1.21 的轨迹以 16 个局部单位表示，因此每点鞭距属性对应 1.6 格世界距离。
    private static final double RANGE_ATTRIBUTE_SCALE = 1.6;
    public static final double RENDER_SEGMENT_SPACING = 0.22;

    private final Map<UUID, Integer> nextHitTicks = new HashMap<>();
    private final Set<BlockPos> hitBlocks = new HashSet<>();
    private int successfulHits;
    private boolean durabilityConsumed;
    /// 本次挥鞭的服务端判定原点。
    ///
    /// <p>1.21 会让鞭子实体自身向前运动并在后半程收回，但伤害关键点始终以生成位置为基准。
    /// 因此这里单独保存判定原点，不能直接拿不断变化的实体坐标计算命中区域。</p>
    private Vec3 attackOrigin;

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
        initialize(weapon, direction, arm, item.definition().durationTicks());
    }

    /// 在服务端发射时冻结本次挥动实际使用的攻速时长。
    public void initialize(ItemStack weapon, Vec3 direction, HumanoidArm arm, int durationTicks) {
        if (!(weapon.getItem() instanceof BaseWhipItem)) {
            throw new IllegalArgumentException("Whip attack weapon must be a BaseWhipItem");
        }
        if (!Double.isFinite(direction.x) || !Double.isFinite(direction.y) || !Double.isFinite(direction.z) || direction.lengthSqr() <= 1.0E-12) {
            throw new IllegalArgumentException("Whip attack direction must be finite and non-zero");
        }
        Vec3 normalized = direction.normalize();
        entityData.set(WEAPON, weapon.copyWithCount(1));
        entityData.set(DIRECTION_X, (float) normalized.x);
        entityData.set(DIRECTION_Y, (float) normalized.y);
        entityData.set(DIRECTION_Z, (float) normalized.z);
        entityData.set(RIGHT_ARM, arm == HumanoidArm.RIGHT);
        if (durationTicks <= 0) {
            throw new IllegalArgumentException("Whip duration must be positive");
        }
        entityData.set(DURATION_TICKS, durationTicks);
        int enchantmentLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.WHIP_SWEEP.get(), weapon);
        entityData.set(SWEEP_LEVEL, enchantmentLevel > 0 && getRandom().nextFloat() < 0.2F ? enchantmentLevel : 0);
        setDeltaMovement(normalized.scale(0.05));
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
        entityData.define(SWEEP_LEVEL, 0);
    }

    @Override
    public void tick() {
        super.tick();
        if (isRemoved()) {
            return;
        }
        LivingEntity owner = getLivingOwner();
        WhipDefinition definition = definition();
        if (owner == null || definition == null || !owner.isAlive()) {
            if (!level().isClientSide) {
                discard();
            }
            return;
        }

        if (attackOrigin == null) {
            attackOrigin = position();
        }
        tickVisibleRootMotion(owner);
        if (tickCount > durationTicks()) {
            discard();
            return;
        }
        if (tickCount == (int) (durationTicks() * 0.3F)) {
            owner.playSound(ModSoundEvents.WHIP_ATTACK.get(), 0.6F + getRandom().nextFloat() * 0.2F, 1.0F);
        }
        if (!level().isClientSide) {
            hitAlongCurrentCurve(owner, definition);
        }
    }

    /// 还原 1.21 挥鞭实体的可见根部运动：前半程加速甩出，后半程逐渐收回玩家身边。
    /// 该位移只影响曲线外观，服务端命中仍由 {@link #attackOrigin} 固定在发射位置。
    private void tickVisibleRootMotion(LivingEntity owner) {
        Vec3 movement = getDeltaMovement();
        setPos(getX() + movement.x, getY() + movement.y, getZ() + movement.z);
        if (movement.lengthSqr() > 1.0E-12) {
            setDeltaMovement(movement.add(movement.normalize().scale(0.1)));
        }

        int drawBackTick = Math.max(1, durationTicks() / 2);
        if (tickCount <= drawBackTick) {
            return;
        }
        double denominator = Math.max(1, durationTicks() - drawBackTick);
        double progress = (tickCount - drawBackTick) / denominator;
        Vec3 target = new Vec3(owner.getX(), owner.getY() + owner.getEyeHeight() * 0.5, owner.getZ());
        Vec3 returnStep = position().lerp(target, progress).subtract(position()).scale(0.5);
        setDeltaMovement(Vec3.ZERO);
        setPos(getX() + returnStep.x, getY() + returnStep.y, getZ() + returnStep.z);
    }

    /// 按当前逻辑 tick 和局部帧插值生成世界坐标折线。
    ///
    /// @param partialTick 客户端渲染帧的局部 tick；服务端碰撞传 {@code 0}
    public List<Vec3> sampleWorldPoints(float partialTick) {
        WhipDefinition definition = definition();
        LivingEntity owner = getLivingOwner();
        if (definition == null || owner == null) {
            return List.of(position());
        }
        double progress = Mth.clamp((tickCount + partialTick) / durationTicks(), 0.0F, 1.0F);
        return sampleWorldPointsAtProgress(owner, definition, progress);
    }

    private List<Vec3> sampleWorldPointsAtProgress(LivingEntity owner, WhipDefinition definition, double progress) {
        List<Vec3> localPoints = WhipCurveSampler.sample(
                sweepLevel() > 0 ? WhipCurves.SWEEP : definition.curve(),
                progress,
                RANGE_ATTRIBUTE_SCALE * owner.getAttributeValue(ConfluenceMagicLib.WHIP_RANGE),
                RENDER_SEGMENT_SPACING);
        return transformLocalPointsLike121(localPoints);
    }

    private List<Vec3> transformLocalPointsLike121(List<Vec3> localPoints) {
        Vec3 direction = launchDirection();
        float yaw = (float) (Math.PI - Math.atan2(direction.z, direction.x));
        float pitch = (float) -Math.atan2(direction.y, direction.horizontalDistance());
        Quaternionf rotation = new Quaternionf().rotateY(yaw).rotateZ(pitch);
        Vec3 origin = attackOrigin == null ? position() : attackOrigin;
        ArrayList<Vec3> result = new ArrayList<>(localPoints.size());
        for (Vec3 point : localPoints) {
            Vector3f local = point.multiply(1.0, -1.0, 1.0).toVector3f();
            rotation.transform(local);
            result.add(origin.add(local.x(), local.y(), local.z()));
        }
        return List.copyOf(result);
    }

    /// 返回客户端显示使用的少量控制点。
    ///
    /// <p>服务端命中点需要冻结在发射时的手部位置；客户端显示则需要把后续控制点叠加
    /// 鞭实体的甩出/收回位移，再由渲染器把根部吸附到玩家当前手上。这样既保留
    /// 1.21 的“手部参与样条”的甩动观感，也不改变实际命中区域。</p>
    public List<Vec3> sampleRenderControlPoints(float partialTick) {
        WhipDefinition definition = definition();
        LivingEntity owner = getLivingOwner();
        if (definition == null || owner == null) {
            return List.of(position());
        }
        List<Vec3> points = sampleWorldControlPoints(owner, definition, partialTick);
        if (points.size() < 2) {
            return points;
        }
        Vec3 origin = attackOrigin == null ? position() : attackOrigin;
        Vec3 interpolatedPosition = new Vec3(Mth.lerp(partialTick, xOld, getX()), Mth.lerp(partialTick, yOld, getY()), Mth.lerp(partialTick, zOld, getZ()));
        Vec3 visibleOffset = interpolatedPosition.subtract(origin);
        ArrayList<Vec3> result = new ArrayList<>(points.size());
        result.add(points.get(0));
        for (int index = 1; index < points.size(); index++) {
            result.add(points.get(index).add(visibleOffset));
        }
        return List.copyOf(result);
    }

    /// 将当前动画的少量控制点转换到世界坐标，供 1.21 方块命中语义使用。
    private List<Vec3> sampleWorldControlPoints(LivingEntity owner, WhipDefinition definition) {
        return sampleWorldControlPoints(owner, definition, 0.0F);
    }

    private List<Vec3> sampleWorldControlPoints(LivingEntity owner, WhipDefinition definition, float partialTick) {
        double progress = Mth.clamp((tickCount + partialTick) / durationTicks(), 0.0, 1.0);
        List<Vec3> localPoints = (sweepLevel() > 0 ? WhipCurves.SWEEP : definition.curve()).controlPoints(progress).stream()
                .map(point -> point.scale(RANGE_ATTRIBUTE_SCALE * owner.getAttributeValue(ConfluenceMagicLib.WHIP_RANGE)))
                .toList();
        return transformLocalPointsLike121(localPoints);
    }

    private List<Vec3> transformLocalPoints(LivingEntity owner, List<Vec3> localPoints) {
        Vec3 forward = launchDirection();
        // Minecraft 面向 +Z 时，模型右手位于 -X，因此右向量必须使用 forward × up。
        Vec3 right = forward.cross(new Vec3(0.0, 1.0, 0.0));
        if (right.lengthSqr() <= 1.0E-8) {
            right = Vec3.directionFromRotation(0.0F, owner.getYRot() + 90.0F);
        } else {
            right = right.normalize();
        }
        Vec3 up = right.cross(forward).normalize();
        Vec3 fixedOrigin = attackOrigin == null ? position() : attackOrigin;
        Vec3 origin = fixedOrigin;
        ArrayList<Vec3> result = new ArrayList<>(localPoints.size());
        for (Vec3 point : localPoints) {
            result.add(origin.add(forward.scale(point.x)).add(up.scale(point.y)).add(right.scale(point.z)));
        }
        return List.copyOf(result);
    }

    public ItemStack weapon() {
        return entityData.get(WEAPON).copy();
    }

    /// 返回本次攻击实际使用的手臂，供第三人称手部锚点计算使用。
    public HumanoidArm attackArm() {
        return entityData.get(RIGHT_ARM) ? HumanoidArm.RIGHT : HumanoidArm.LEFT;
    }

    /// 返回本次挥动在发射瞬间冻结的完整时长。
    public int durationTicks() {
        return Math.max(1, entityData.get(DURATION_TICKS));
    }

    /// 返回本次挥动实际触发的横扫附魔等级；未触发时为零。
    public int sweepLevel() {
        return Math.max(0, entityData.get(SWEEP_LEVEL));
    }

    private void hitAlongCurrentCurve(LivingEntity owner, WhipDefinition definition) {
        hitBlocksAlongControlPoints(owner, definition);
        double radius = 1.5 + (sweepLevel() > 0 ? 0.5 : 0.0);
        List<Vec3> currentCurve = sampleWorldPoints(0.0F);
        List<Vec3> previousCurve = tickCount > 0
                ? sampleWorldPointsAtProgress(owner, definition, (double) (tickCount - 1) / durationTicks())
                : List.of();
        AABB bounds = curveBounds(currentCurve);
        if (!previousCurve.isEmpty()) {
            bounds = bounds.minmax(curveBounds(previousCurve));
        }
        List<Entity> candidates = level().getEntities(this, bounds.inflate(radius), rawTarget -> {
            Entity impacted = ProjectileHitRules.impactedEntity(rawTarget);
            return impacted instanceof LivingEntity living
                    && impacted != owner
                    && canHitAgain(impacted.getUUID())
                    && (ProjectileHitRules.canHit(owner, rawTarget)
                    || isFriendlySummon(owner, living, definition));
        });
        for (Entity rawTarget : candidates) {
            Entity impacted = ProjectileHitRules.impactedEntity(rawTarget);
            if (!(impacted instanceof LivingEntity target) || !canHitAgain(impacted.getUUID())) {
                continue;
            }
            if (!WhipCollisionGeometry.intersectsSweptCurve(previousCurve, currentCurve, rawTarget.getBoundingBox().inflate(radius))) {
                continue;
            }
            if (!definition.penetratesBlocks() && !canReachTarget(owner, rawTarget)) {
                delayNextHit(target.getUUID(), definition);
                continue;
            }
            if (applyFriendlyHit(owner, target, definition)) {
                delayNextHit(target.getUUID(), definition);
                continue;
            }
            hitTarget(owner, rawTarget, target, definition);
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

    /// 还原 1.21 鞭子对附近方块触发 {@code onProjectileHit} 的行为。
    ///
    /// <p>方块扫描使用原始动画控制点而不是渲染插值点，否则提高鞭节精度会意外放大服务端工作量。
    /// 同一次挥动内按方块坐标去重，避免同一方块在相邻帧和相邻控制点被重复触发。</p>
    private void hitBlocksAlongControlPoints(LivingEntity owner, WhipDefinition definition) {
        double radius = 1.5 + (sweepLevel() > 0 ? 0.5 : 0.0);
        Direction direction = Direction.getNearest((float) launchDirection().x, (float) launchDirection().y, (float) launchDirection().z);
        for (Vec3 point : sampleWorldControlPoints(owner, definition)) {
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

    private void hitTarget(LivingEntity owner, Entity rawTarget, LivingEntity target, WhipDefinition definition) {
        if (!canHitAgain(target.getUUID())) {
            return;
        }
        float baseDamage = getDamage() > 0.0F ? getDamage() : definition.baseDamage();
        float multiplier = Math.max(definition.minimumDamageMultiplier(), (float) Math.pow(definition.damageFalloff(), successfulHits));
        float damage = baseDamage * multiplier
                * (1.0F + sweepLevel() * 0.2F);
        delayNextHit(target.getUUID(), definition);
        int hitIndex = successfulHits++;
        if (owner instanceof Player player) {
            consumeDurabilityAfterFirstEnemyHit(player);
            player.setLastHurtMob(target);
            WhipTagTracker.apply(player, target, weapon(), definition.tagEffect().get());
            WhipDirectHitContext context = new WhipDirectHitContext(player, target, weapon(), damage, hitIndex);
            definition.directHitEffects().forEach(effect -> effect.apply(context));
        }
        LibDamageTypes.hurtWithoutKnockback(rawTarget, LibDamageTypes.of(level(), LibDamageTypes.SUMMON, this, owner), damage);
    }

    private boolean canHitAgain(UUID targetId) {
        return tickCount >= nextHitTicks.getOrDefault(targetId, Integer.MIN_VALUE);
    }

    private void delayNextHit(UUID targetId, WhipDefinition definition) {
        nextHitTicks.put(targetId, tickCount + definition.hitCooldownTicks());
    }

    /// 1.21 只在本次挥动首次命中合法敌人后消耗一点耐久。
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

    private boolean isFriendlySummon(LivingEntity owner, LivingEntity target, WhipDefinition definition) {
        if (definition.friendlyHitEffects().isEmpty() || !(owner instanceof Player player) || !(level() instanceof ServerLevel serverLevel) || !(target instanceof OwnedSummon summon)) {
            return false;
        }
        return summon.resolveSummonOwner(serverLevel) == player;
    }

    private boolean applyFriendlyHit(LivingEntity owner, LivingEntity target, WhipDefinition definition) {
        if (!isFriendlySummon(owner, target, definition) || !(owner instanceof Player player)) {
            return false;
        }
        WhipFriendlyHitContext context = new WhipFriendlyHitContext(player, target, weapon());
        definition.friendlyHitEffects().forEach(effect -> effect.apply(context));
        float baseDamage = getDamage() > 0.0F ? getDamage() : definition.baseDamage();
        LibDamageTypes.hurtWithoutKnockback(target, LibDamageTypes.of(level(), LibDamageTypes.SUMMON, this, owner), baseDamage * 0.2F);
        return true;
    }

    private boolean hasLineOfSight(Vec3 from, Vec3 to) {
        return level().clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS;
    }

    private boolean canReachTarget(LivingEntity owner, Entity target) {
        Vec3 eyes = owner.getEyePosition();
        AABB box = target.getBoundingBox();
        return hasLineOfSight(eyes, new Vec3(target.getX(), box.maxY, target.getZ()))
                || hasLineOfSight(eyes, target.position())
                || hasLineOfSight(eyes, new Vec3(target.getX(), box.getCenter().y, target.getZ()));
    }

    private WhipDefinition definition() {
        ItemStack stack = entityData.get(WEAPON);
        return stack.getItem() instanceof BaseWhipItem item ? item.definition() : null;
    }

    private Vec3 launchDirection() {
        Vec3 direction = new Vec3(entityData.get(DIRECTION_X), entityData.get(DIRECTION_Y), entityData.get(DIRECTION_Z));
        return direction.lengthSqr() <= 1.0E-12 ? new Vec3(0.0, 0.0, 1.0) : direction.normalize();
    }

    /// 一次挥鞭只在当前攻击时段存在，不能跨世界保存。
    ///
    /// <p>这与 1.21 的挥鞭实体一致，也避免重新载入时只恢复通用弹幕快照、却缺少挥动进度和
    /// 轨迹历史而生成残缺攻击。</p>
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
