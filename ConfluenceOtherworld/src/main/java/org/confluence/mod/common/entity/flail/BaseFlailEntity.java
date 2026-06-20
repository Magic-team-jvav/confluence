package org.confluence.mod.common.entity.flail;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.component.FlailComponent;
import org.confluence.mod.common.enchantment.TurbineEnchantments;

import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.item.flail.BaseFlailItem;
import org.confluence.mod.mixed.Immunity;
import org.confluence.mod.util.EnchantmentUtils;
import org.confluence.mod.util.HandPositionUtils;
import org.confluence.terraentity.utils.TEUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

/**
 * <h1>连枷弹射物基类</h1>
 * 支持五阶段状态机：SPIN->THROWN->STAY->RETRACT
 * <p>
 * SPIN：绕玩家肩部 Z 轴圆周挥舞，造成 60% 伤害<br>
 * THROWN：沿视线方向发射，造成 100% 伤害，无限穿透<br>
 * STAY：受重力停留地面，造成 50% 伤害<br>
 * RETRACT：飞回玩家并消失
 */
public class BaseFlailEntity extends Projectile implements Immunity, GeoAnimatable {
    // ── 状态常数 ─
    public static final int PHASE_SPIN = 0;
    public static final int PHASE_THROWN = 1;
    public static final int PHASE_STAY = 2;
    public static final int PHASE_RETRACT = 3;

    // ── 同步数据 ──
    private static final EntityDataAccessor<Integer> DATA_PHASE =
            SynchedEntityData.defineId(BaseFlailEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_SPIN_ANGLE =
            SynchedEntityData.defineId(BaseFlailEntity.class, EntityDataSerializers.FLOAT);

    // ── 运行时状态 ──
    public float spinAngle = 0.0F;
    private int hitCooldown = 0;
    /**
     * 风爆附魔独立冷却，防止每 tick 多次触发
     */
    private int windBurstCooldown = 0;
    private int stayDuration = 0;
    private int bounceCount = 0;
    private boolean playerDropped = false;
    /**
     * SPIN 阶段持续 tick 计数，供 {@link TurbineEnchantments} 等外部附魔效果查询
     */
    private int spinTickCounter = 0;
    /**
     * 上一帧的阶段，用于检测阶段切换
     */
    private int previousPhase = -1;
    /**
     * 渲染用：帧间平滑后的链条方向，由 {@code BaseFlailRenderer} 逐帧 lerp 更新
     */
    public Vec3 smoothedChainDir = null;
    @Nullable
    private FlailComponent cachedComponent;
    /**
     * 攻击策略，控制各阶段的额外攻击行为（如发射激光、追踪泡泡）。
     * 子类可在构造函数中覆盖以绑定专属策略。
     */
    protected FlailAttackStrategy attackStrategy = FlailAttackStrategy.NULL;
    private final AnimatableInstanceCache animatableCache = GeckoLibUtil.createInstanceCache(this);

    /**
     * 获取当前 SPIN 持续 tick 数
     */
    public int getSpinTickCounter() {
        return spinTickCounter;
    }

    /** 获取当前攻击策略 */
    @NotNull
    public FlailAttackStrategy getAttackStrategy() {
        return attackStrategy;
    }

    /** 设置攻击策略（通常在 {@link #init} 之前调用） */
    public void setAttackStrategy(@NotNull FlailAttackStrategy attackStrategy) {
        this.attackStrategy = attackStrategy;
    }

    public BaseFlailEntity(EntityType<? extends BaseFlailEntity> entityType, Level level) {
        super(entityType, level);
        setNoGravity(true);
    }

    /**
     * 由物品调用，设置弹射物所有者、连枷组件和初始位置
     */
    public void init(@NotNull Player owner, ItemStack weapon, @NotNull FlailComponent component) {
        setOwner(owner);
        this.cachedComponent = component;
        Vec3 palm = HandPositionUtils.getPalmPosition(owner, 1.0F);
        // 几何中心对齐手掌
        setPos(palm.x, palm.y - getBbHeight() * 0.5, palm.z);
        setPhase(PHASE_SPIN);
    }

    // ── 数据同步 ──
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_PHASE, PHASE_SPIN);
        builder.define(DATA_SPIN_ANGLE, 0.0F);
    }

    public int getPhase() {
        return entityData.get(DATA_PHASE);
    }

    public void setPhase(int phase) {
        entityData.set(DATA_PHASE, phase);
    }

    public float getSyncedSpinAngle() {
        return entityData.get(DATA_SPIN_ANGLE);
    }

    @Nullable
    public FlailComponent getComponent() {
        if (cachedComponent != null) return cachedComponent;
        Entity owner = getOwner();
        if (!(owner instanceof LivingEntity living)) {
            return cachedComponent;
        }
        ItemStack stack = living.getMainHandItem();
        cachedComponent = stack.get(ModDataComponentTypes.FLAIL);
        if (cachedComponent != null) return cachedComponent;
        stack = living.getOffhandItem();
        cachedComponent = stack.get(ModDataComponentTypes.FLAIL);
        return cachedComponent;
    }

    /**
     * 线段 a 的纯世界方向：玩家面朝水平方向，不含公转偏移
     */
    @NotNull
    public Vector3f getSpinAxis() {
        Entity owner = getOwner();
        if (owner == null) return new Vector3f(0, 0, 1);
        float yawRad = (float) Math.toRadians(owner.getYRot());
        return new Vector3f(-(float) Math.sin(yawRad), 0, (float) Math.cos(yawRad));
    }

    /**
     * 方块碰撞处理：RETRACT 阶段击中方块时直接落地
     */
    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        // THROWN 阶段的碰撞由 tickThrown 手动处理，此处忽略
        if (level().isClientSide() || getPhase() != PHASE_RETRACT) {
            return;
        }
        setPos(result.getLocation());
        playerDrop();
    }

    // ── Tick ──
    @Override
    public void tick() {
        Entity owner = getOwner();

        if (!level().isClientSide()) {
            if (owner == null || owner.isRemoved()
                    || !(owner instanceof Player player)
                    || !isHoldingFlail(player)
                    || position().distanceToSqr(owner.position()) > 40 * 40) {
                discard();
                return;
            }
        }

        super.tick();
        // 重新读取 phase：super.tick() 中的 onHitBlock 可能已改变阶段
        int phase = getPhase();

        // 涡轮附魔：退出 SPIN 时重置计数
        if (previousPhase == PHASE_SPIN && phase != PHASE_SPIN) {
            spinTickCounter = 0;
        }
        previousPhase = phase;

        if (!(owner instanceof Player player)) return;

        FlailComponent component = getComponent();
        if (component == null) return;

        if (hitCooldown > 0) hitCooldown--;
        if (windBurstCooldown > 0) windBurstCooldown--;

        this.noPhysics = false; // 默认启用碰撞，tickSpin 内部覆写为 true
        switch (phase) {
            case PHASE_SPIN -> {
                tickSpin(player, component);
                attackStrategy.onSpinTick(this, player, component);
            }
            case PHASE_THROWN -> {
                tickThrown(player, component);
                attackStrategy.onThrownTick(this, player, component);
            }
            case PHASE_STAY -> {
                tickStay(player, component);
                attackStrategy.onStayTick(this, player, component);
            }
            case PHASE_RETRACT -> {
                tickRetract(player, component);
                attackStrategy.onRetractTick(this, player, component);
            }
        }

        if (!level().isClientSide() && hitCooldown <= 0) {
            doCollisionCheck(player, phase, component);
        }

        // 所有阶段：玩家超出最大距离时自动收回（仅服务端判断）
        if (!level().isClientSide() && phase != PHASE_RETRACT
                && position().distanceToSqr(player.position()) > component.maxDistance() * component.maxDistance()) {
            if (phase == PHASE_THROWN) {
                attackStrategy.onThrownToRetract(this, player, component);
            }
            setPhase(PHASE_RETRACT);
        }

        entityData.set(DATA_SPIN_ANGLE, spinAngle);
    }

    private void tickSpin(Player player, FlailComponent component) {
        this.noPhysics = true;
        setNoGravity(true);
        spinTickCounter++;
        float turbineBonus = TurbineEnchantments.getBonus(player, spinTickCounter);
        spinAngle += component.getSpinSpeed(player) * (1.0F + turbineBonus);

        Vec3 pivot = HandPositionUtils.getPalmPosition(player, 1.0F, new Vec3(0.25, 0.25, -0.2));
        // 与 getPalmPosition 保持一致，使用身体朝向而非头部朝向
        float yawRad = (float) Math.toRadians(player.yBodyRot);
        float cosYaw = (float) Math.cos(yawRad);
        float sinYaw = (float) Math.sin(yawRad);
        double r = component.spinRadius();
        double localY = r * Math.sin(spinAngle);
        double localZ = r * Math.cos(spinAngle);
        // MC 坐标系：forward = (-sin(yaw), 0, cos(yaw))
        double x = pivot.x - localZ * sinYaw;
        double y = pivot.y + localY;
        double z = pivot.z + localZ * cosYaw;
        Vec3 targetPos = new Vec3(x, y - getBbHeight() * 0.5, z);
        Vec3 toTarget = targetPos.subtract(position());
        double dist = toTarget.length();
        double maxOrbitalSpeed = r * component.getSpinSpeed(player) * 1.5;
        double maxSpeed = maxOrbitalSpeed + 0.5;
        double k = 0.8;
        double speed = 0.0;
        if (dist > 1.0E-7) {
            Vec3 dir = toTarget.normalize();
            speed = Math.min(dist * k, maxSpeed);
            setDeltaMovement(dir.scale(speed));
            move(MoverType.SELF, getDeltaMovement());
        }
        setDeltaMovement(Vec3.ZERO);
    }

    /**
     * 射线检测前方方块碰撞，返回 null 表示无碰撞
     */
    @Nullable
    private BlockHitResult clipBlock(Vec3 motion) {
        Vec3 start = position();
        Vec3 end = start.add(motion);
        ClipContext context = new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this);
        BlockHitResult hit = level().clip(context);
        return hit.getType() == HitResult.Type.BLOCK ? hit : null;
    }

    /** 使实体面朝指定方向，从 deltaMovement 或 toOwner 向量计算 yaw/pitch */
    private void faceDirection(Vec3 dir) {
        double hDist = Math.sqrt(dir.x * dir.x + dir.z * dir.z);
        setYRot((float) Math.toDegrees(Math.atan2(dir.x, dir.z)));
        setXRot((float) Math.toDegrees(Math.atan2(-dir.y, hDist)));
    }

    private void tickThrown(Player player, FlailComponent component) {
        Vec3 motion = getDeltaMovement();

        // 速度过低直接收回（仅服务端判断）
        if (!level().isClientSide() && motion.lengthSqr() < 0.1) {
            attackStrategy.onThrownToRetract(this, player, component);
            setPhase(PHASE_RETRACT);
            return;
        }

        // 面朝飞行方向
        faceDirection(motion);

        BlockHitResult blockHit = clipBlock(motion);
        if (blockHit == null) {
            setDeltaMovement(motion);
            move(MoverType.SELF, motion);
            return;
        }

        Vec3 normal = Vec3.atLowerCornerOf(blockHit.getDirection().getNormal());
        setPos(blockHit.getLocation().add(normal.scale(0.1)));

        // 反射速度：仅当朝向墙面时反弹，防浅角度卡墙
        double dot = motion.dot(normal);
        if (dot < 0) {
            motion = motion.subtract(normal.scale(2.0 * dot));
        }
        motion = motion.scale(0.6);
        setDeltaMovement(motion);

        // 反弹次数耗尽 或 反射后速度过低 → 直接收回（仅服务端判断）
        if (!level().isClientSide() && (bounceCount >= component.maxBounces() || motion.lengthSqr() < 0.1)) {
            attackStrategy.onThrownToRetract(this, player, component);
            setPhase(PHASE_RETRACT);
            return;
        }

        if (!level().isClientSide()) {
            bounceCount++;
            setPhase(PHASE_STAY);
            stayDuration = 0;
        }
    }

    private void tickStay(Player player, FlailComponent component) {
        // 手动施加重力并使用 move() 进行碰撞位移
        Vec3 motion = getDeltaMovement().add(0, -component.gravity(), 0);
        setDeltaMovement(motion);

        // 速度过低 → 收回（仅服务端判断，玩家主动丢出时不收回）
        if (!level().isClientSide() && !playerDropped && getDeltaMovement().lengthSqr() < 0.1) {
            setPhase(PHASE_RETRACT);
            return;
        }

        move(MoverType.SELF, motion);

        // 着地时清零垂直速度 + 施加水平摩擦
        if (onGround()) {
            Vec3 vel = getDeltaMovement();
            setDeltaMovement(new Vec3(vel.x * 0.5, 0.0, vel.z * 0.5));
        }

        stayDuration++;
    }

    private void tickRetract(Player player, FlailComponent component) {
        setNoGravity(true);
        Vec3 target = HandPositionUtils.getPalmPosition(player, 1.0F);
        Vec3 toOwner = target.subtract(position());
        if (toOwner.lengthSqr() < 1) {
            if (!level().isClientSide()) {
                discard();
            }
            return;
        }
        // 面朝玩家
        faceDirection(toOwner);
        Vec3 dir = toOwner.normalize();
        Vec3 motion = dir.scale(component.retractSpeed());
        setDeltaMovement(motion);
        move(MoverType.SELF, motion);

        // 卡墙时瞬移绕过方块
        if (horizontalCollision || verticalCollision) {
            setPos(position().add(dir.scale(component.retractSpeed() * 2)));
        }
    }

    private void doCollisionCheck(Player player, int phase, FlailComponent component) {
        float damageMultiplier = switch (phase) {
            case PHASE_SPIN -> 0.6f;
            case PHASE_THROWN -> 1.0f;
            case PHASE_STAY -> 0.5f;
            case PHASE_RETRACT -> 1.0f;
            default -> 0.0f;
        };
        if (damageMultiplier <= 0) return;

        AABB checkBox = getBoundingBox().inflate(0.5);
        List<LivingEntity> entities = level().getEntitiesOfClass(LivingEntity.class, checkBox,
                e -> e != player && e.isAlive() && TEUtils.projectileCanHurtEntityTest.test(this, e));

        boolean anyHit = false;
        LivingEntity firstHit = null;
        for (LivingEntity target : entities) {
            float baseDamage = (float) (component.damageFactor() * player.getAttributeValue(LibAttributes.getAttackDamage()));
            float turbineBonus = TurbineEnchantments.getBonus(player, spinTickCounter);
            float finalDamage = baseDamage * damageMultiplier * (1.0F + turbineBonus);
            DamageSource source = ModDamageTypes.of(level(), ModDamageTypes.SWORD_PROJECTILE, this, player);

            if (target.hurt(source, finalDamage)) {
                if (!anyHit) firstHit = target;
                anyHit = true;
                VectorUtils.knockBackA2B(this, target, 0.3f, 0.15f);
                component.hitEffect().ifPresent(effect -> effect.applyAll(player, target));
                if (level() instanceof ServerLevel serverLevel) {
                    EnchantmentHelper.doPostAttackEffects(serverLevel, target, source);
                }
            }
        }

        if (!anyHit) {
            return;
        }

        // 通知策略：命中实体
        if (firstHit != null) {
            attackStrategy.onHitEntity(this, player, component, firstHit);
        }

        float turbineBonus = TurbineEnchantments.getBonus(player, spinTickCounter);
        hitCooldown = phase == PHASE_THROWN ? 3 : (int) Math.max(4, 8 / (1.0F + turbineBonus));
        if (!(player instanceof ServerPlayer serverPlayer) || windBurstCooldown > 0 || firstHit == null) {
            return;
        }
        EnchantmentUtils.processFlailWindBurst(serverPlayer, firstHit,
                ModDamageTypes.of(level(), ModDamageTypes.SWORD_PROJECTILE, this, player));
        windBurstCooldown = 60; // 3 秒冷却
        serverPlayer.getCooldowns().addCooldown(serverPlayer.getMainHandItem().getItem(), 60);
    }

    /**
     * SPIN 切换THROWN
     */
    public void launch(Player player) {
        FlailComponent component = getComponent();
        if (component == null) return;
        attackStrategy.onLaunch(this, player, component);

        setPhase(PHASE_THROWN);
        bounceCount = 0;
        playerDropped = false;

        Vec3 look = player.getViewVector(1.0F);
        Vec3 palm = HandPositionUtils.getPalmPosition(player, 1.0F);
        // 几何中心对齐手掌，与 tickSpin 和渲染器 ballPos 一致
        setPos(palm.x, palm.y - getBbHeight() * 0.5, palm.z);

        // 修正投掷方向：使几何中心在 maxDistance 处命中视角射线
        // C0 + dir × maxDist = eye + look × maxDist  ⇒  dir = normalize((eye - C0) + look × maxDist)
        Vec3 eye = player.getEyePosition(1.0F);
        Vec3 targetDir = look.scale(component.maxDistance()).add(eye.subtract(palm));
        Vec3 launchDir = targetDir.normalize();

        float velocity = component.getVelocity(player) * (1.0F + TurbineEnchantments.getBonus(player, spinTickCounter));
        setDeltaMovement(launchDir.scale(velocity));
    }

    /**
     * 落地进入 STAY，不因低速自动收回。
     * <p>用于玩家主动丢出（按 use）以及 RETRACT 途中撞墙落地。
     */
    public void playerDrop() {
        setPhase(PHASE_STAY);
        Vec3 motion = getDeltaMovement();
        setDeltaMovement(motion.scale(0.15));
        stayDuration = 0;
        playerDropped = true;
    }

    public void forceRetract() {
        setPhase(PHASE_RETRACT);
        playerDropped = false;
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        Entity owner = getOwner();
        if (!level().isClientSide() && owner instanceof Player player) {
            FlailComponent component = getComponent();
            if (component != null) {
                attackStrategy.onDiscard(this, player, component);
            }
        }
        super.remove(reason);
    }

    private boolean isHoldingFlail(@NotNull Player player) {
        ItemStack stack = player.getMainHandItem();
        return !stack.isEmpty()
                && stack.getItem() instanceof BaseFlailItem
                && stack.has(ModDataComponentTypes.FLAIL);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("Phase")) setPhase(tag.getInt("Phase"));
        if (tag.contains("SpinAngle")) spinAngle = tag.getFloat("SpinAngle");
        if (tag.contains("StayDuration")) stayDuration = tag.getInt("StayDuration");
        if (tag.contains("BounceCount")) bounceCount = tag.getInt("BounceCount");
        if (tag.contains("PlayerDropped")) playerDropped = tag.getBoolean("PlayerDropped");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Phase", getPhase());
        tag.putFloat("SpinAngle", spinAngle);
        tag.putInt("StayDuration", stayDuration);
        tag.putInt("BounceCount", bounceCount);
        tag.putBoolean("PlayerDropped", playerDropped);
    }

    @Override
    @NotNull
    public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.fixed(0.75f, 0.75f);
    }

    @Override
    public Type confluence$getImmunityType() {
        return Type.LOCAL;
    }

    @Override
    public int confluence$getImmunityDuration(DamageSource damageSource) {
        return 7;
    }

    // ── GeoAnimatable ──

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableCache;
    }

    @Override
    public double getTick(Object animatable) {
        return tickCount;
    }
}
