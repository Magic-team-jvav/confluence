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

/// # 连枷弹射物基类
/// 支持五阶段状态机：SPIN->THROWN->STAY->RETRACT
///
/// SPIN：绕玩家肩部 Z 轴圆周挥舞，造成 60% 伤害
/// THROWN：沿视线方向发射，造成 100% 伤害，无限穿透
/// STAY：受重力停留地面，造成 50% 伤害
/// RETRACT：飞回玩家并消失
public class BaseFlailEntity extends Projectile implements Immunity {
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

    public BaseFlailEntity(EntityType<? extends BaseFlailEntity> entityType, Level level) {
        super(entityType, level);
        setNoGravity(true);
    }

    /// 由物品调用，设置弹射物所有者、连枷组件和初始位置
    public void init(Player owner, ItemStack weapon, FlailComponent component) {
        setOwner(owner);
        this.cachedComponent = component;
        Vec3 palm = HandPositionUtils.getPalmPosition(owner, 1.0F);
        setPos(palm);
        setPhase(PHASE_SPIN);
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
        entityData.set(DATA_PHASE, phase);
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

        if (!level().isClientSide() && hitCooldown <= 0) {
            doCollisionCheck(player, phase, component);
        }

        // 所有阶段：玩家超出最大距离时自动收回（仅服务端判断）
        if (!level().isClientSide() && phase != PHASE_RETRACT
                && position().distanceToSqr(player.position()) > component.maxDistance() * component.maxDistance()) {
            setPhase(PHASE_RETRACT);
        }

        entityData.set(DATA_SPIN_ANGLE, spinAngle);
    }

    private void tickSpin(Player player, FlailComponent component) {
        setNoGravity(true);
        // 默认 SPIN：绕玩家肩部，在玩家面朝方向的竖直平面内圆周运动
        spinAngle += component.getSpinSpeed(player);
        Vec3 pivot = HandPositionUtils.getPalmPosition(player, 1.0F);
        float yawRad = (float) Math.toRadians(player.getYRot());
        float cosYaw = (float) Math.cos(yawRad);
        float sinYaw = (float) Math.sin(yawRad);
        double r = component.spinRadius();
        double localY = r * Math.sin(spinAngle);
        double localZ = r * Math.cos(spinAngle);
        // MC 坐标系：forward = (-sin(yaw), 0, cos(yaw))
        double x = pivot.x - localZ * sinYaw;
        double y = pivot.y + localY;
        double z = pivot.z + localZ * cosYaw;
        setPos(x, y, z);

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
        Vec3 motion = getDeltaMovement();

        // 速度过低直接收回（仅服务端判断）
        if (!level().isClientSide() && motion.lengthSqr() < 0.1) {
            setPhase(PHASE_RETRACT);
            return;
        }

        BlockHitResult blockHit = clipBlock(motion);
        if (blockHit != null) {
            Vec3 normal = Vec3.atLowerCornerOf(blockHit.getDirection().getNormal());
            setPos(blockHit.getLocation().add(normal.scale(0.1)));

            // 反射速度：仅当朝向墙面时反弹，防浅角度卡住
            double dot = motion.dot(normal);
            if (dot < 0) {
                motion = motion.subtract(normal.scale(2.0 * dot));
            }
            motion = motion.scale(0.6);
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
        var entities = level().getEntitiesOfClass(LivingEntity.class, checkBox,
                e -> e != player && e.isAlive() && TEUtils.projectileCanHurtEntityTest.test(this, e));

        for (LivingEntity target : entities) {
            float baseDamage = (float) (component.damageFactor() * player.getAttributeValue(LibAttributes.getAttackDamage()));
            float finalDamage = baseDamage * damageMultiplier;
            DamageSource source = ModDamageTypes.of(level(), ModDamageTypes.SWORD_PROJECTILE, this, player);

            if (target.hurt(source, finalDamage)) {
                LibEntityUtils.knockBackA2B(this, target, 0.3f, 0.15f);
                component.hitEffect().ifPresent(effect -> effect.applyAll(player, target));
                hitCooldown = phase == PHASE_THROWN ? 3 : 8;
            }
        }
    }

    /// SPIN 切换THROWN
    public void launch(Player player) {
        FlailComponent component = getComponent();
        if (component == null) return;

        setPhase(PHASE_THROWN);
        bounceCount = 0;
        playerDropped = false;

        Vec3 look = player.getViewVector(1.0F);
        setPos(HandPositionUtils.getPalmPosition(player, 1.0F));

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
}
