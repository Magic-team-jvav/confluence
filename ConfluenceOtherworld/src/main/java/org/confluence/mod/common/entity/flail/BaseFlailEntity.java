package org.confluence.mod.common.entity.flail;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.component.FlailComponent;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.item.flail.BaseFlailItem;
import org.confluence.mod.mixed.Immunity;
import org.confluence.mod.util.HandPositionUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

/// # 连枷弹射物基类
/// 支持五阶段状态机：SPIN->THROWN->STAY->RETRACT
///
/// SPIN：绕玩家肩部 Z 轴圆周挥舞，造成 60% 伤害
/// THROWN：沿视线方向发射，造成 100% 伤害，无限穿透
/// STAY：受重力停留地面，造成 50% 伤害
/// RETRACT：飞回玩家并消失
public class BaseFlailEntity extends Projectile implements Immunity, GeoEntity {
    // ── 状态常量 ──
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
    private int stayDuration = 0;
    private int bounceCount = 0;
    private boolean playerDropped = false;
    @Nullable
    private FlailComponent cachedComponent;
    /// 客户端渲染链条时使用的平滑方向。
    ///
    /// <p>连枷阶段切换或服务端同步位置时，弹球位置可能在相邻帧内出现小幅跳变。如果链条
    /// 直接使用瞬时方向，会表现为手部端点弹动；该字段只参与客户端渲染，不保存到实体数据。</p>
    @Nullable
    public Vec3 smoothedChainDir;
    private final AnimatableInstanceCache animationCache =
            GeckoLibUtil.createInstanceCache(this);

    public BaseFlailEntity(EntityType<? extends BaseFlailEntity> entityType, Level level) {
        super(entityType, level);
        setNoGravity(true);
    }

    /// 由物品调用，设置弹射物所有者、连枷组件和初始位置
    public void init(Player owner, ItemStack weapon, FlailComponent component) {
        setOwner(owner);
        this.cachedComponent = component;
        Vec3 palm = HandPositionUtils.getPalmPosition(owner, 1.0F);
        setPos(palm.x, palm.y - getBbHeight() * 0.5, palm.z);
        setPhase(PHASE_SPIN);
        if (startsLaunched()) {
            launch(owner);
        }
        // 持续播放挖掘动画
        if (level().isClientSide()) {
            owner.swing(InteractionHand.MAIN_HAND);
        }
    }

    // ── 数据同步 ──
    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_PHASE, PHASE_SPIN);
        this.entityData.define(DATA_SPIN_ANGLE, 0.0F);
    }

    public int getPhase() {
        return entityData.get(DATA_PHASE);
    }

    public void setPhase(int phase) {
        int previous = entityData.get(DATA_PHASE);
        entityData.set(DATA_PHASE, phase);
        if (!level().isClientSide()
                && previous == PHASE_THROWN
                && phase == PHASE_RETRACT
                && getOwner() instanceof Player player) {
            FlailComponent component = getComponent();
            if (component != null) {
                onThrownToRetract(player, component);
            }
        }
    }

    public float getSyncedSpinAngle() {
        return entityData.get(DATA_SPIN_ANGLE);
    }

    @Nullable
    public FlailComponent getComponent() {
        if (cachedComponent != null) return cachedComponent;
        Entity owner = getOwner();
        if (owner instanceof LivingEntity living) {
            ItemStack stack = living.getMainHandItem();
            cachedComponent = stack.get(ModDataComponentTypes.FLAIL);
            if (cachedComponent != null) return cachedComponent;
            stack = living.getOffhandItem();
            cachedComponent = stack.get(ModDataComponentTypes.FLAIL);
        }
        return cachedComponent;
    }

    /// 线段 a 的纯世界方向：玩家面朝水平方向，不含公转偏移
    public Vector3f getSpinAxis() {
        Entity owner = getOwner();
        if (owner == null) return new Vector3f(0, 0, 1);
        float yawRad = (float) Math.toRadians(owner.getYRot());
        return new Vector3f(-(float) Math.sin(yawRad), 0, (float) Math.cos(yawRad));
    }

    /// 方块碰撞处理：RETRACT 阶段击中方块时直接落地
    @Override
    protected void onHitBlock(BlockHitResult result) {
        // THROWN 阶段的碰撞由 tickThrown 手动处理，此处忽略
        if (!level().isClientSide() && getPhase() == PHASE_RETRACT) {
            setPos(result.getLocation());
            playerDrop();
        }
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

        if (!(owner instanceof Player player)) return;

        FlailComponent component = getComponent();
        if (component == null) return;

        if (hitCooldown > 0) hitCooldown--;

        switch (phase) {
            case PHASE_SPIN -> tickSpin(player, component);
            case PHASE_THROWN -> tickThrown(player, component);
            case PHASE_STAY -> tickStay(player, component);
            case PHASE_RETRACT -> tickRetract(player, component);
        }
        tickSpecialBehavior(player, component, getPhase());

        if (!level().isClientSide() && hitCooldown <= 0) {
            /// 移动阶段可能因方块命中而切换为收回。碰撞伤害必须读取切换后的阶段，
            /// 否则直接发射型链锤撞墙后仍会在同一 tick 以投出伤害命中附近实体。
            doCollisionCheck(player, getPhase(), component);
        }

        // 所有阶段：玩家超出最大距离时自动收回（仅服务端判断）
        if (!level().isClientSide() && phase != PHASE_RETRACT
                && position().distanceToSqr(player.position()) > component.maxDistance() * component.maxDistance()) {
            setPhase(PHASE_RETRACT);
        }

        entityData.set(DATA_SPIN_ANGLE, spinAngle);
    }

    private void tickSpin(Player player, FlailComponent component) {
        this.noPhysics = true;
        setNoGravity(true);
        // 默认 SPIN：绕玩家肩部，在玩家面朝方向的竖直平面内圆周运动
        spinAngle += component.getSpinSpeed(player);
        Vec3 pivot = HandPositionUtils.getPalmPosition(
                player,
                1.0F,
                new Vec3(0.25, 0.25, -0.2));
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
        double distance = toTarget.length();
        if (distance > 1.0E-7) {
            double maxOrbitalSpeed = r * component.getSpinSpeed(player) * 1.5;
            double speed = Math.min(distance * 0.8, maxOrbitalSpeed + 0.5);
            setDeltaMovement(toTarget.normalize().scale(speed));
            move(MoverType.SELF, getDeltaMovement());
        }
        setDeltaMovement(Vec3.ZERO);
        faceDirection(new Vec3(
                sinYaw * Math.sin(spinAngle),
                Math.cos(spinAngle),
                -cosYaw * Math.sin(spinAngle)));

        // 客户端持续播放挥动动画
        if (level().isClientSide()) {
            player.swing(InteractionHand.MAIN_HAND);
        }
    }

    /// 射线检测前方方块碰撞，返回 null 表示无碰撞
    @Nullable
    private BlockHitResult clipBlock(Vec3 motion) {
        Vec3 start = position();
        Vec3 end = start.add(motion);
        HitResult hit = level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        return hit.getType() == HitResult.Type.BLOCK ? (BlockHitResult) hit : null;
    }

    private void tickThrown(Player player, FlailComponent component) {
        Vec3 motion = getDeltaMovement().add(
                0.0, -getThrownGravity(), 0.0);

        // 速度过低直接收回（仅服务端判断）
        if (!level().isClientSide() && motion.lengthSqr() < 0.1) {
            setPhase(PHASE_RETRACT);
            return;
        }

        faceDirection(motion);
        BlockHitResult blockHit = clipBlock(motion);
        if (blockHit != null) {
            Vec3 normal = Vec3.atLowerCornerOf(blockHit.getDirection().getNormal());
            setPos(blockHit.getLocation().add(normal.scale(0.1)));

            if (startsLaunched()) {
                if (!level().isClientSide()) {
                    onLaunchedBlockImpact(player, component, blockHit);
                    setPhase(PHASE_RETRACT);
                }
                return;
            }

            // 反射速度：仅当朝向墙面时反弹，防浅角度卡住
            double dot = motion.dot(normal);
            if (dot < 0) {
                motion = motion.subtract(normal.scale(2.0 * dot));
            }
            motion = motion.scale(component.bounceFactor());
            setDeltaMovement(motion);

            // 反弹次数耗尽 或 反射后速度过低 直接收回（仅服务端判断）
            if (!level().isClientSide() && (bounceCount >= component.maxBounces() || motion.lengthSqr() < 0.1)) {
                setPhase(PHASE_RETRACT);
                return;
            }

            if (!level().isClientSide()) {
                bounceCount++;
                setPhase(PHASE_STAY);
                stayDuration = 0;
            }
            return;
        }

        setDeltaMovement(motion);
        move(MoverType.SELF, motion);
    }

    private void tickStay(Player player, FlailComponent component) {
        setNoGravity(true);
        // 手动施加重力并使用 move() 进行碰撞位移
        Vec3 motion = getDeltaMovement().add(0, -component.gravity(), 0);
        setDeltaMovement(motion);

        // 速度过低 收回（仅服务端判断，玩家主动丢出时不收回）
        if (!level().isClientSide() && !playerDropped && getDeltaMovement().lengthSqr() < 0.1) {
            setPhase(PHASE_RETRACT);
            return;
        }
        move(MoverType.SELF, motion);

        // 着地时清零垂直速度 + 施加水平摩擦，防止重力累积导致永不收回
        if (onGround()) {
            Vec3 vel = getDeltaMovement();
            setDeltaMovement(new Vec3(vel.x * 0.5, vel.y, vel.z * 0.5));
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
        Vec3 dir = toOwner.normalize();
        Vec3 motion = dir.scale(component.retractSpeed());
        faceDirection(motion);
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
            default -> 0.0f;
        };
        if (damageMultiplier <= 0) return;

        AABB checkBox = getBoundingBox().inflate(1.5);
        var entities = level().getEntitiesOfClass(
                LivingEntity.class,
                checkBox,
                e -> e != player && e.isAlive());

        for (LivingEntity target : entities) {
            float baseDamage = (float) (component.damageFactor() * player.getAttributeValue(LibAttributes.getAttackDamage()));
            float finalDamage = baseDamage * damageMultiplier;
            DamageSource source = ModDamageTypes.of(level(), ModDamageTypes.SWORD_PROJECTILE, this, player);

            if (target.hurt(source, finalDamage)) {
                LibEntityUtils.knockBackA2B(this, target, 0.3f, 0.15f);
                ItemStack held = player.getMainHandItem();
                if (held.getItem() instanceof BaseFlailItem flailItem) {
                    flailItem.onFlailHit(player, target, this);
                }
                hitCooldown = phase == PHASE_THROWN ? 3 : 8;
                if (phase == PHASE_THROWN && startsLaunched()) {
                    setPhase(PHASE_RETRACT);
                    return;
                }
            }
        }
    }

    /// 返回当前连枷是否属于直接发射型。
    ///
    /// <p>普通链锤先旋转再投出；链刃、锚等直接发射型由实体子类覆盖，
    /// 其参数和行为无需再写入物品注册或额外定义表。</p>
    protected boolean startsLaunched() {
        return false;
    }

    /// 返回链锤头部是否按泰拉瑞亚原始平面精灵绘制。
    public boolean usesSpriteHead() {
        return false;
    }

    /// 返回投出阶段每 tick 额外施加的重力。
    protected double getThrownGravity() {
        return 0.0;
    }

    /// 按指定方向同步实体朝向。
    ///
    /// <p>渲染器会根据实体朝向决定球体姿态。旋转、投出和收回阶段都维护朝向后，
    /// 连枷不会再因为服务端位置变化而出现球体横躺、正反面乱跳或链条接到模型侧面的错觉。</p>
    private void faceDirection(Vec3 direction) {
        if (direction.lengthSqr() < 1.0E-7) {
            return;
        }
        double horizontal = Math.sqrt(
                direction.x * direction.x + direction.z * direction.z);
        setYRot((float) Math.toDegrees(
                Math.atan2(-direction.x, direction.z)));
        setXRot((float) Math.toDegrees(
                Math.atan2(-direction.y, horizontal)));
    }

    /// 在共享移动、碰撞和状态转换完成后执行具体武器行为。
    ///
    /// <p>特殊链锤只需覆盖此扩展点发射附属弹幕；普通链锤保持空实现。</p>
    protected void tickSpecialBehavior(
            Player player,
            FlailComponent component,
            int phase
    ) {
    }

    /// 直接发射型链锤撞击方块时的扩展点。
    protected void onLaunchedBlockImpact(
            Player player,
            FlailComponent component,
            BlockHitResult hit
    ) {
    }

    /// 投出阶段首次进入收回阶段时的扩展点。
    protected void onThrownToRetract(
            Player player,
            FlailComponent component
    ) {
    }

    /// SPIN 切换THROWN
    public void launch(Player player) {
        FlailComponent component = getComponent();
        if (component == null) return;

        setPhase(PHASE_THROWN);
        bounceCount = 0;
        playerDropped = false;

        Vec3 look = player.getViewVector(1.0F);
        Vec3 palm = HandPositionUtils.getPalmPosition(player, 1.0F);
        setPos(palm.x, palm.y - getBbHeight() * 0.5, palm.z);
        faceDirection(look);

        float velocity = component.getVelocity(player);
        setDeltaMovement(look.scale(velocity));
    }

    /// 落地进入 STAY，不因低速自动收回。
    ///
    /// 用于玩家主动丢出（按 use）以及 RETRACT 途中撞墙落地。
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

    private boolean isHoldingFlail(Player player) {
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

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }

    @Override
    public void registerControllers(
            AnimatableManager.ControllerRegistrar controllers
    ) {
    }
}
