package org.confluence.mod.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import org.confluence.lib.common.entity.PartHitTarget;
import org.confluence.mod.common.entity.EnemyDamageRules;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;
import org.confluence.mod.common.entity.projectile.MonsterLaser;
import org.confluence.mod.common.gameevent.MartianEventHelper;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.mixed.Immunity;
import software.bernie.geckolib.animation.AnimatableManager;

import java.util.Arrays;
import java.util.List;

/// 三足地面攻击者，左右两侧的 left_fort/right_fort 炮台可独立被摧毁。
public final class MartianWalker extends BaseMonster {
    public enum CombatTactic {IDLE, RETREAT, APPROACH, RANGED, LEG_MELEE}

    private boolean retreating;
    private static final EntityDataAccessor<Integer> DESTROYED = SynchedEntityData.defineId(MartianWalker.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> WALKING = SynchedEntityData.defineId(MartianWalker.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> WALK_SPEED = SynchedEntityData.defineId(MartianWalker.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> FLIGHT_PHASE = SynchedEntityData.defineId(MartianWalker.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Long> PHASE_STARTED = SynchedEntityData.defineId(MartianWalker.class, EntityDataSerializers.LONG);
    private static final int LAUNCH_TICKS = 105;
    private static final float MAX_TURN_PER_TICK = 10.0F;
    private static final double COMBAT_CRUISE_RADIUS = 20.0;
    private static final double IDLE_CRUISE_RADIUS = 12.0;
    private static final double CRUISE_SPEED = 0.5;
    private static final double FLIGHT_LOOKAHEAD_TICKS = 6.0;
    private static final float FLIGHT_TURN_PER_TICK = 4.0F;
    private final Gait gait = new Gait();
    private static final float WEAPON_HEALTH_RATIO = 0.25F;
    private static final EntityDataAccessor<Float> LEFT_WEAPON_HEALTH = SynchedEntityData.defineId(MartianWalker.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> RIGHT_WEAPON_HEALTH = SynchedEntityData.defineId(MartianWalker.class, EntityDataSerializers.FLOAT);
    private final WalkerWeapon[] weapons = new WalkerWeapon[2];
    private int attackCooldown;
    private int contactCooldown;
    private int meleePauseTicks;
    private final int cruiseDirection = random.nextBoolean() ? 1 : -1;
    private Vec3 idleCruiseCenter;
    private Vec3 flightAvoidance = Vec3.ZERO;
    private int flightAvoidanceTicks;
    private int nextWeapon;
    private List<WalkerGeometry.Box> collisionBoxes = List.of();
    private final LegHitbox[] legHitboxes;
    private final BodyHitbox bodyHitbox;
    private final PartEntity<?>[] hitboxes;

    public MartianWalker(EntityType<? extends MartianWalker> type, Level level) {
        super(type, level);
        getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.0);
        moveControl = new WalkerMoveControl(this);
        legHitboxes = new LegHitbox[30];
        for (int i = 0; i < legHitboxes.length; i++) legHitboxes[i] = new LegHitbox(this);
        bodyHitbox = new BodyHitbox(this);
        hitboxes = Arrays.copyOf(legHitboxes, legHitboxes.length + 3, PartEntity[].class);
        hitboxes[legHitboxes.length] = bodyHitbox;
        for (int slot = 0; slot < weapons.length; slot++) {
            weapons[slot] = new WalkerWeapon(this, slot);
            hitboxes[legHitboxes.length + 1 + slot] = weapons[slot];
        }
        setId(ENTITY_COUNTER.getAndAdd(hitboxes.length + 1) + 1);
    }

    @Override
    public void setId(int id) {
        super.setId(id);
        if (hitboxes != null)
            for (int i = 0; i < hitboxes.length; i++) hitboxes[i].setId(id + i + 1);
    }

    @Override
    public boolean isMultipartEntity() {return true;}

    @Override
    public PartEntity<?>[] getParts() {return hitboxes;}

    /// 根包围盒仅用于地面寻路，不用于玩家碰撞或拾取。
    @Override
    public boolean canBeCollidedWith() {return false;}

    @Override
    public boolean isPickable() {return false;}

    @Override
    public boolean isPushable() {return false;}

    @Override
    public void push(Entity entity) {}

    @Override
    protected void pushEntities() {
        /// 即使本体不可被推动，LivingEntity 仍会推动其他实体。
        updateCollisionGeometry();
        AABB bounds = collisionBounds();
        if (bounds == null || !isAlive()) return;
        var pushable = EntitySelector.pushableBy(this);
        for (Entity entity : level().getEntities(this, bounds,
                candidate -> candidate instanceof LivingEntity && pushable.test(candidate))) {
            if (level().isClientSide && !(entity instanceof net.minecraft.world.entity.player.Player))
                continue;
            doPush(entity);
        }
    }

    @Override
    protected void doPush(Entity entity) {
        if (entity instanceof LivingEntity && collisionBoxes.stream().anyMatch(box -> box.intersects(entity.getBoundingBox())))
            super.doPush(entity);
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        /// 玩家和生物由动画部件几何推挤，不将寻路包围盒作为实体障碍。
        return false;
    }

    @Override
    public void move(MoverType type, Vec3 movement) {
        super.move(type, movement);
        updateCollisionGeometry();
    }

    private void updateCollisionGeometry() {
        collisionBoxes = WalkerGeometry.boxes(this, animationPose(1));
        int index = 0;
        AABB body = null;
        for (WalkerGeometry.Box box : collisionBoxes) {
            if (box.leg()) {
                if (legHitboxes != null && index < legHitboxes.length)
                    legHitboxes[index++].follow(box);
            } else if (bodyBox(box)) {
                body = body == null ? box.bounds() : body.minmax(box.bounds());
            }
        }
        if (legHitboxes != null) {
            while (index < legHitboxes.length) legHitboxes[index++].deactivate();
        }
        if (body != null && bodyHitbox != null) bodyHitbox.follow(body);
        if (weapons != null) {
            for (int slot = 0; slot < weapons.length; slot++) {
                if (weapons[slot] != null) weapons[slot].follow(weaponBounds(slot));
            }
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DESTROYED, 0);
        builder.define(LEFT_WEAPON_HEALTH, -1.0F);
        builder.define(RIGHT_WEAPON_HEALTH, -1.0F);
        builder.define(WALKING, false);
        builder.define(WALK_SPEED, 0.0F);
        builder.define(FLIGHT_PHASE, 0);
        builder.define(PHASE_STARTED, 0L);
    }

    public boolean weaponDestroyed(int slot) {return slot < 0 || slot > 1 || (entityData.get(DESTROYED) & (1 << slot)) != 0;}

    public boolean walking() {return entityData.get(WALKING);}

    public boolean weaponsDestroyed() {return weaponDestroyed(0) && weaponDestroyed(1);}

    public CombatTactic combatTactic(LivingEntity target) {
        if (target == null || !target.isAlive()) return CombatTactic.IDLE;
        if (weaponsDestroyed()) return CombatTactic.LEG_MELEE;
        double distance = target.position().subtract(position()).horizontalDistanceSqr();
        if (distance < 14 * 14 || retreating && distance < 20 * 20) return CombatTactic.RETREAT;
        return distance > 28 * 28 || !hasLineOfSight(target) ? CombatTactic.APPROACH : CombatTactic.RANGED;
    }

    public double animationSeconds(float partialTick) {return (level().getGameTime() + partialTick) / 20.0;}

    public boolean isLaunching() {
        return entityData.get(FLIGHT_PHASE) == 1;
    }

    public boolean isSpaceship() {
        return entityData.get(FLIGHT_PHASE) == 2;
    }

    @Override
    public EntityDimensions getDefaultDimensions(Pose pose) {
        EntityDimensions dimensions = super.getDefaultDimensions(pose);
        return isSpaceship() ? EntityDimensions.scalable(dimensions.width(), dimensions.height() * 0.55F) : dimensions;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
        if (FLIGHT_PHASE.equals(accessor)) refreshDimensions();
    }

    private double phaseTicks(float partialTick) {
        float partial = Float.isFinite(partialTick) ? Mth.clamp(partialTick, 0, 1) : 0;
        return Math.max(0, level().getGameTime() - entityData.get(PHASE_STARTED) + partial);
    }

    public WalkerGeometry.Pose animationPose(float partialTick) {
        if (isLaunching() || isSpaceship()) {
            return new WalkerGeometry.Pose(0, 0, 0, isLaunching() ? "Catapult launch" : "Spaceship",
                    phaseTicks(partialTick) / 20.0);
        }
        return gait.pose(animationSeconds(partialTick), partialTick);
    }

    private void tryStartLaunch() {
        if (isNoAi() || isPassenger()) return;
        if (level().isClientSide || !isAlive() || entityData.get(FLIGHT_PHASE) != 0 || weaponsDestroyed()
                || getHealth() >= getMaxHealth() * 0.25F) return;
        entityData.set(PHASE_STARTED, level().getGameTime());
        entityData.set(FLIGHT_PHASE, 1);
        getNavigation().stop();
        setDeltaMovement(Vec3.ZERO);
        setNoGravity(true);
        fallDistance = 0;
        retreating = false;
        resetCruise();
    }

    private void tickFlightMotion() {
        getNavigation().stop();
        fallDistance = 0;
        if (isLaunching()) {
            setDeltaMovement(0, phaseTicks(0) >= 35 ? 0.08 : 0, 0);
            return;
        }
        LivingEntity target = getTarget();
        Vec3 bodyCenter = bodyHitbox.getBoundingBox().getCenter();
        Vec3 horizontal;
        double desiredY;
        if (target != null && target.isAlive() && canAttack(target)) {
            idleCruiseCenter = null;
            if (weaponsDestroyed()) {
                horizontal = target.position().subtract(bodyCenter).multiply(1, 0, 1).normalize().scale(0.65);
                desiredY = target.getY() + target.getBbHeight() * 0.5;
            } else {
                horizontal = cruiseMotion(bodyCenter, target.position(), COMBAT_CRUISE_RADIUS);
                desiredY = target.getY() + 8;
            }
        } else {
            if (idleCruiseCenter == null) idleCruiseCenter = bodyCenter;
            horizontal = cruiseMotion(bodyCenter, idleCruiseCenter, IDLE_CRUISE_RADIUS).scale(0.7);
            desiredY = idleCruiseCenter.y;
        }
        double halfHeight = bodyHitbox.getBoundingBox().getYsize() * 0.5;
        desiredY = Mth.clamp(desiredY, level().getMinBuildHeight() + halfHeight + 1,
                level().getMaxBuildHeight() - halfHeight - 1);
        Vec3 desired = horizontal.add(0, Mth.clamp((desiredY - bodyCenter.y) * 0.12, -0.35, 0.35), 0);
        Vec3 velocity = getDeltaMovement().lerp(avoidFlightObstacles(desired), 0.15);
        setDeltaMovement(velocity);
        if (velocity.horizontalDistanceSqr() > 1.0E-4) {
            float yaw = (float) (Mth.atan2(velocity.z, velocity.x) * Mth.RAD_TO_DEG) - 90.0F;
            setYRot(Mth.approachDegrees(getYRot(), yaw, FLIGHT_TURN_PER_TICK));
            setYHeadRot(getYRot());
        }
    }

    // 切向速度维持巡航，径向修正让飞船逐渐回到目标附近的轨道。
    private Vec3 cruiseMotion(Vec3 bodyCenter, Vec3 center, double radius) {
        Vec3 offset = bodyCenter.subtract(center).multiply(1, 0, 1);
        double distance = offset.horizontalDistance();
        Vec3 radial = distance > 1.0E-6 ? offset.scale(1 / distance)
                : Vec3.directionFromRotation(0, getYRot());
        Vec3 tangent = new Vec3(-radial.z, 0, radial.x).scale(cruiseDirection);
        double radialSpeed = Mth.clamp((radius - distance) * 0.06, -CRUISE_SPEED, CRUISE_SPEED * 0.8);
        double tangentSpeed = CRUISE_SPEED * Mth.clamp(1 - Math.abs(distance - radius) / radius, 0.2, 1);
        Vec3 velocity = radial.scale(radialSpeed).add(tangent.scale(tangentSpeed));
        return velocity.lengthSqr() > CRUISE_SPEED * CRUISE_SPEED
                ? velocity.normalize().scale(CRUISE_SPEED) : velocity;
    }

    private Vec3 avoidFlightObstacles(Vec3 desired) {
        AABB hull = collisionBounds();
        if (hull == null) hull = getBoundingBox();
        if (flightAvoidanceTicks > 0) {
            flightAvoidanceTicks--;
            if (flightPathClear(hull, flightAvoidance)) return flightAvoidance;
            flightAvoidanceTicks = 0;
        }
        if (flightPathClear(hull, desired)) return desired;

        // 有限方向探测并保持绕行一秒，避免撞墙时每刻左右换向。
        Vec3 forward = desired.multiply(1, 0, 1);
        if (forward.lengthSqr() < 1.0E-6) forward = Vec3.directionFromRotation(0, getYRot()).scale(CRUISE_SPEED);
        Vec3 side = new Vec3(-forward.z, 0, forward.x).normalize().scale(CRUISE_SPEED * cruiseDirection);
        for (Vec3 candidate : new Vec3[]{new Vec3(0, 0.35, 0), side.add(0, 0.15, 0),
                side.scale(-1).add(0, 0.15, 0), forward.scale(-0.7).add(0, 0.15, 0)}) {
            if (!flightPathClear(hull, candidate)) continue;
            flightAvoidance = candidate;
            flightAvoidanceTicks = 20;
            return candidate;
        }
        return Vec3.ZERO;
    }

    private boolean flightPathClear(AABB hull, Vec3 velocity) {
        AABB swept = hull.expandTowards(velocity.scale(FLIGHT_LOOKAHEAD_TICKS)).inflate(0.15);
        return swept.minY >= level().getMinBuildHeight() && swept.maxY < level().getMaxBuildHeight()
                && level().hasChunksAt(BlockPos.containing(swept.minX, swept.minY, swept.minZ),
                        BlockPos.containing(swept.maxX, swept.maxY, swept.maxZ))
                && !level().getBlockCollisions(this, swept).iterator().hasNext();
    }

    private void resetCruise() {
        idleCruiseCenter = null;
        flightAvoidance = Vec3.ZERO;
        flightAvoidanceTicks = 0;
    }

    @Override
    public void travel(Vec3 input) {
        if (!isLaunching() && !isSpaceship()) {
            super.travel(input);
            return;
        }
        if (isEffectiveAi()) move(MoverType.SELF, getDeltaMovement());
        fallDistance = 0;
        calculateEntityAnimation(false);
    }

    @Override
    protected float tickHeadTurn(float yaw, float animationStep) {
        float previousBodyYaw = yBodyRot;
        float result = super.tickHeadTurn(yaw, animationStep);
        /// 原版身体控制在停步后会追随头部，模型和碰撞几何也必须限制转速。
        yBodyRot = Mth.approachDegrees(previousBodyYaw, yBodyRot, MAX_TURN_PER_TICK);
        return result;
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        return !isLaunching() && !isSpaceship() && super.causeFallDamage(distance, multiplier, source);
    }

    private BTNode flightBehavior() {
        return new BTNode() {
            @Override
            public boolean canStart() {
                return isLaunching() || isSpaceship();
            }

            @Override
            public BTStatus execute() {
                if (!canStart()) return BTStatus.FAILURE;
                tickFlightMotion();
                return BTStatus.RUNNING;
            }
        };
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                WaterAvoidingRandomStrollGoal stroll = new WaterAvoidingRandomStrollGoal(MartianWalker.this, 0.65);
                stroll.setInterval(40);
                stroll.trigger();
                return SelectorNode.of(flightBehavior(), new BTNode() {
                            @Override
                            public boolean canStart() {
                                LivingEntity target = getTarget();
                                return target != null && target.isAlive() && canAttack(target);
                            }

                            @Override
                            public BTStatus execute() {
                                if (!canStart()) return BTStatus.FAILURE;
                                LivingEntity target = getTarget();
                                if (target == null) return BTStatus.FAILURE;
                                getLookControl().setLookAt(target, 30, 30);
                                tickCombatNavigation(target);
                                return BTStatus.RUNNING;
                            }

                            @Override
                            public void stop() {
                                retreating = false;
                                getNavigation().stop();
                            }
                        }, new VanillaGoalAction(stroll),
                        new VanillaGoalAction(new RandomLookAroundGoal(MartianWalker.this)));
            }
        };
    }

    private void tickCombatNavigation(LivingEntity target) {
        CombatTactic tactic = combatTactic(target);
        boolean changedRetreat = retreating != (tactic == CombatTactic.RETREAT);
        retreating = tactic == CombatTactic.RETREAT;
        if (tactic == CombatTactic.LEG_MELEE && (meleePauseTicks > 0
                || collisionBoxes.stream().anyMatch(box -> box.leg() && box.intersects(target.getBoundingBox())))) {
            if (meleePauseTicks == 0) meleePauseTicks = 10;
            getNavigation().stop();
            setDeltaMovement(getDeltaMovement().multiply(0, 1, 0));
            return;
        }
        if (tactic == CombatTactic.RANGED) {
            getNavigation().stop();
            setDeltaMovement(getDeltaMovement().multiply(0, 1, 0));
            return;
        }
        if (!changedRetreat && tickCount % 8 != 0 && !getNavigation().isDone()) return;
        if (tactic == CombatTactic.RETREAT) {
            Vec3 away = position().subtract(target.position()).multiply(1, 0, 1);
            if (away.lengthSqr() < 1.0E-6) away = new Vec3(1, 0, 0);
            away = away.normalize();
            /// 优先选择可达的撤退点，然后尝试绕开地形障碍的两侧。
            for (double angle : new double[]{0, Math.PI / 3, -Math.PI / 3}) {
                Vec3 destination = position().add(away.yRot((float) angle).scale(10));
                BlockPos block = BlockPos.containing(destination);
                if (!level().isLoaded(block)) continue;
                var path = getNavigation().createPath(block, 0);
                if (path != null && path.canReach() && getNavigation().moveTo(path, 1.0)) return;
            }
            getNavigation().stop();
        } else if (tactic == CombatTactic.LEG_MELEE) {
            /// 追踪稳定的玩家位置；动画脚掌偏移会随转向变化，不能反过来驱动寻路终点。
            getNavigation().moveTo(target, 1.25);
        } else if (tactic == CombatTactic.APPROACH) getNavigation().moveTo(target, 1.0);
    }

    @Override
    public void tick() {
        Vec3 beforeMove = position();
        super.tick();
        if (!isAlive()) return;
        if (meleePauseTicks > 0) meleePauseTicks--;
        if (!level().isClientSide) {
            tryStartLaunch();
            if (isLaunching() && phaseTicks(0) >= LAUNCH_TICKS) {
                entityData.set(PHASE_STARTED, level().getGameTime());
                entityData.set(FLIGHT_PHASE, 2);
                heal(getMaxHealth() * 0.25F);
            }
            double distance = position().subtract(beforeMove).horizontalDistance();
            boolean moving = !isLaunching() && !isSpaceship() && onGround()
                    && distance > (walking() ? 0.005 : 0.015) * getScale();
            entityData.set(WALKING, moving);
            entityData.set(WALK_SPEED, moving ? (float) Mth.clamp(distance / Math.max(0.01, 0.15 * getScale()), 0.35, 1.6) : 0);
        }
        gait.tick(walking(), entityData.get(WALK_SPEED));
        updateCollisionGeometry();
        if (!(level() instanceof ServerLevel server)) return;
        /// 部件与本体共同加载和卸载，不生成独立实体。
        if (isNoAi() || isPassenger()) return;
        tickContact();
        if (attackCooldown > 0) attackCooldown--;
        LivingEntity target = getTarget();
        if (weaponsDestroyed() || attackCooldown != 0 || isLaunching() || !isSpaceship() && !onGround() || target == null || !target.isAlive()
                || !canAttack(target) || distanceToSqr(target) > 43.75 * 43.75 || !hasLineOfSight(target))
            return;
        for (int attempt = 0; attempt < 2; attempt++) {
            int slot = (nextWeapon + attempt) & 1;
            WalkerWeapon weapon = weapons[slot];
            if (weaponDestroyed(slot) || weapon == null || !weapon.isAlive()) continue;
            if (!ModEntities.MONSTER_LASER.isBound()) return;
            MonsterLaser laser = ModEntities.MONSTER_LASER.get().create(server);
            if (laser == null) return;
            laser.configureShot(this, target, weapon.getBoundingBox().getCenter(), MonsterLaser.Variant.MARTIAN_WALKER);
            MartianEventHelper.inheritEventMembership(this, laser);
            if (!server.addFreshEntity(laser)) {
                laser.discard();
                return;
            }
            nextWeapon = slot ^ 1;
            attackCooldown = 5;
            break;
        }
    }

    private void tickContact() {
        if (contactCooldown > 0) {
            contactCooldown--;
            return;
        }
        /// 每个立方体都跟随与客户端模型相同的动画骨骼变换。
        /// 不使用外围三脚架包围盒：玩家可以站在腿部之间的空隙中。
        boolean hit = false;
        AABB bounds = collisionBounds();
        if (bounds == null) return;
        for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, bounds, this::canAttack)) {
            if (target == this || !canContactAttack(target)) continue;
            if (intersectsContactVolume(target.getBoundingBox()))
                hit |= doContactHurtTarget(target);
        }
        if (hit) contactCooldown = 20;
    }

    public boolean intersectsContactVolume(AABB bounds) {
        return bounds != null && collisionBoxes.stream().anyMatch(box ->
                (weaponsDestroyed() && !isSpaceship() ? box.leg() : !box.bone().endsWith("fort")) && box.intersects(bounds));
    }

    public AABB collisionBounds() {
        AABB result = null;
        for (WalkerGeometry.Box box : collisionBoxes) {
            result = result == null ? box.bounds() : result.minmax(box.bounds());
        }
        return result;
    }

    private static boolean bodyBox(WalkerGeometry.Box box) {
        return switch (box.bone()) {
            case "main", "canopy", "body", "head" -> true;
            default -> false;
        };
    }

    public AABB weaponBounds(int slot) {
        if (slot < 0 || slot > 1 || weaponDestroyed(slot)) return null;
        return WalkerGeometry.weaponBounds(this, collisionBoxes, slot);
    }

    /// 首次生成时按本体血量缩放炮台血量；旧存档继续读取 WeaponHealth0/1。
    public float weaponHealth(int slot) {
        if (weaponDestroyed(slot)) return 0;
        float health = entityData.get(slot == 0 ? LEFT_WEAPON_HEALTH : RIGHT_WEAPON_HEALTH);
        return health < 0 ? getMaxHealth() * WEAPON_HEALTH_RATIO : health;
    }

    public WalkerWeapon weapon(int slot) {return weapons[slot];}

    /// 炮台独立扣血，不调用本体 hurt，也不触发本体死亡、掉落或入侵计分。
    boolean hurtWeapon(int slot, DamageSource source, float amount) {
        if (level().isClientSide || !isAlive() || weaponDestroyed(slot) || isInvulnerableTo(source)
                || EnemyDamageRules.blocks(this, source) || !Float.isFinite(amount) || amount <= 0)
            return false;
        Immunity cause = Immunity.getCause(source);
        if (cause != null ? Immunity.isActive(cause, this) : invulnerableTime > 10) return false;
        float damage = getDamageAfterMagicAbsorb(source, getDamageAfterArmorAbsorb(source, amount));
        if (!Float.isFinite(damage) || damage <= 0) return false;
        float remaining = Math.max(0, weaponHealth(slot) - damage);
        entityData.set(slot == 0 ? LEFT_WEAPON_HEALTH : RIGHT_WEAPON_HEALTH, remaining);
        if (cause != null) Immunity.apply(cause, source, this);
        else invulnerableTime = 20;
        delayLaserAttack();
        if (source.getEntity() instanceof LivingEntity attacker) setLastHurtByMob(attacker);
        playSound(ModSoundEvents.METAL_HURT.get(), 1, 1);
        if (remaining <= 0) destroyWeapon(slot);
        return true;
    }

    public void destroyWeapon(int slot) {
        if (slot < 0 || slot >= weapons.length || weaponDestroyed(slot)) return;
        AABB bounds = weapons[slot].getBoundingBox();
        entityData.set(slot == 0 ? LEFT_WEAPON_HEALTH : RIGHT_WEAPON_HEALTH, 0.0F);
        entityData.set(DESTROYED, entityData.get(DESTROYED) | (1 << slot));
        weapons[slot].follow(null);
        if (level() instanceof ServerLevel server) {
            Vec3 center = bounds.getCenter();
            server.sendParticles(ParticleTypes.EXPLOSION, center.x, center.y, center.z, 8, 0.3, 0.3, 0.3, 0);
        }
        if (weaponsDestroyed()) {
            retreating = false;
            getNavigation().stop();
            attackCooldown = 0;
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean result = super.hurt(source, amount);
        if (result) {
            delayLaserAttack();
            tryStartLaunch();
        }
        return result;
    }

    public void delayLaserAttack() {attackCooldown = Math.max(attackCooldown, 8);}

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    protected SoundEvent getAmbientSound() {return ModSoundEvents.MARTIAN_WALKER_FREE.get();}

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {return ModSoundEvents.METAL_HURT.get();}

    @Override
    protected SoundEvent getDeathSound() {return ModSoundEvents.METAL_DEATH.get();}

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("DestroyedWeapons", entityData.get(DESTROYED));
        tag.putInt("AttackCooldown", attackCooldown);
        tag.putInt("ContactCooldown", contactCooldown);
        tag.putInt("NextWeapon", nextWeapon);
        tag.putInt("FlightPhase", entityData.get(FLIGHT_PHASE));
        tag.putInt("FlightPhaseTicks", (int) Math.min(Integer.MAX_VALUE, phaseTicks(0)));
        for (int i = 0; i < weapons.length; i++) tag.putFloat("WeaponHealth" + i, weaponHealth(i));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(DESTROYED, tag.contains("DestroyedWeapons") ? Mth.clamp(tag.getInt("DestroyedWeapons"), 0, 3) : 0);
        entityData.set(WALKING, false);
        entityData.set(WALK_SPEED, 0.0F);
        int phase = Mth.clamp(tag.getInt("FlightPhase"), 0, 2);
        int elapsed = Mth.clamp(tag.getInt("FlightPhaseTicks"), 0, phase == 1 ? LAUNCH_TICKS : Integer.MAX_VALUE);
        entityData.set(PHASE_STARTED, level().getGameTime() - elapsed);
        entityData.set(FLIGHT_PHASE, phase);
        setNoGravity(phase != 0);
        gait.reset();
        attackCooldown = tag.contains("AttackCooldown") ? Mth.clamp(tag.getInt("AttackCooldown"), 0, 200) : 0;
        contactCooldown = tag.contains("ContactCooldown") ? Mth.clamp(tag.getInt("ContactCooldown"), 0, 20) : 0;
        meleePauseTicks = 0;
        resetCruise();
        nextWeapon = tag.contains("NextWeapon") ? Mth.clamp(tag.getInt("NextWeapon"), 0, 1) : 0;
        retreating = false;
        for (int i = 0; i < weapons.length; i++) {
            float health = tag.contains("WeaponHealth" + i) ? tag.getFloat("WeaponHealth" + i) : -1;
            health = Float.isFinite(health) ? Mth.clamp(health, -1, getMaxHealth() * WEAPON_HEALTH_RATIO) : -1;
            entityData.set(i == 0 ? LEFT_WEAPON_HEALTH : RIGHT_WEAPON_HEALTH, health);
            if (health == 0) entityData.set(DESTROYED, entityData.get(DESTROYED) | (1 << i));
        }
        collisionBoxes = List.of();
        setTarget(null);
    }

    /// 每个实体独立的瞬时步态时钟。渲染与碰撞使用相同的混合姿态。
    public static final class Gait {
        private float weight, previousWeight, rate;
        private double walkSeconds, previousWalkSeconds;

        public void tick(boolean moving, float speed) {
            previousWeight = weight;
            previousWalkSeconds = walkSeconds;
            weight = Mth.clamp(weight + (moving ? 1.0F / 8 : -1.0F / 10), 0, 1);
            float targetRate = moving && Float.isFinite(speed) ? Mth.clamp(speed, 0.35F, 1.6F) : 0;
            rate += (targetRate - rate) * 0.25F;
            /// 在淡出时完成当前步伐，而不是让抬起的脚冻结。
            if (weight > 0 || previousWeight > 0) walkSeconds += rate / 20.0;
            else {
                rate = 0;
                walkSeconds = 0;
                previousWalkSeconds = 0;
            }
        }

        public WalkerGeometry.Pose pose(double standSeconds, float partialTick) {
            float partial = Float.isFinite(partialTick) ? Mth.clamp(partialTick, 0, 1) : 0;
            float blend = Mth.lerp(partial, previousWeight, weight);
            blend = blend * blend * (3 - 2 * blend);
            return new WalkerGeometry.Pose(standSeconds, Mth.lerp(partial, previousWalkSeconds, walkSeconds), blend);
        }

        public void reset() {
            weight = previousWeight = rate = 0;
            walkSeconds = previousWalkSeconds = 0;
        }
    }

    /// 上半身受击体，与本体基于脚部的寻路包围盒分离。
    public static final class BodyHitbox extends PartEntity<MartianWalker> implements PartHitTarget {
        private int followedTick = Integer.MIN_VALUE;

        public BodyHitbox(MartianWalker parent) {super(parent);}

        public void follow(AABB bounds) {
            boolean first = followedTick == Integer.MIN_VALUE;
            if (followedTick != getParent().tickCount) {
                setOldPosAndRot();
                followedTick = getParent().tickCount;
            }
            setPos(bounds.getCenter().x, bounds.minY, bounds.getCenter().z);
            setBoundingBox(bounds);
            if (first) setOldPosAndRot();
        }

        @Override
        public boolean isPickable() {return getParent().isAlive();}

        @Override
        /// 生物推挤由本体处理，弹射物只参与受击碰撞。
        public boolean canBeCollidedWith() {return false;}

        @Override
        public boolean is(Entity entity) {return entity == this || entity == getParent();}

        @Override
        public boolean hurt(DamageSource source, float amount) {return getParent().isAlive() && getParent().hurt(source, amount);}

        @Override
        protected void defineSynchedData(SynchedEntityData.Builder builder) {}

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {}

        @Override
        protected void readAdditionalSaveData(CompoundTag tag) {}

        @Override
        public boolean shouldBeSaved() {return false;}

        @Override
        public Entity encounterOwner() {return getParent();}

        @Override
        public Entity dedupeIdentity() {return getParent();}

        @Override
        public boolean acceptsDirectHit() {return getParent().isAlive();}
    }

    private static final class WalkerMoveControl extends MoveControl {
        private WalkerMoveControl(MartianWalker walker) {
            super(walker);
        }

        @Override
        public void tick() {
            boolean moving = operation == Operation.MOVE_TO;
            super.tick();
            if (!moving || !mob.onGround()) return;
            double dx = wantedX - mob.getX();
            double dz = wantedZ - mob.getZ();
            double distance = Math.sqrt(dx * dx + dz * dz);
            float targetYaw = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
            float alignment = Math.max(0.0F, Mth.cos(Mth.wrapDegrees(targetYaw - mob.getYRot()) * Mth.DEG_TO_RAD));
            /// 大角度转弯时减速，避免越过近处的路径节点后持续绕圈。
            mob.setSpeed((float) Math.min(mob.getSpeed() * alignment, distance));
        }

        @Override
        protected float rotlerp(float current, float target, float maximumChange) {
            /// 原版允许每 tick 转向 90 度，路径节点切换时会导致大型步行者急转。
            return super.rotlerp(current, target, Math.min(maximumChange, MAX_TURN_PER_TICK));
        }
    }

    /// 腿部的一个立方体，与身体共享本体血量。
    public static final class LegHitbox extends PartEntity<MartianWalker> implements PartHitTarget {
        private WalkerGeometry.Box cube;
        private int followedTick = Integer.MIN_VALUE;

        public LegHitbox(MartianWalker parent) {super(parent);}

        public void deactivate() {
            cube = null;
            setPos(getParent().position());
            setBoundingBox(new AABB(position(), position()));
        }

        public void follow(WalkerGeometry.Box box) {
            cube = box;
            var bounds = box.bounds();
            boolean first = followedTick == Integer.MIN_VALUE;
            if (followedTick != getParent().tickCount) {
                setOldPosAndRot();
                followedTick = getParent().tickCount;
            }
            setPos(bounds.getCenter().x, bounds.minY, bounds.getCenter().z);
            setBoundingBox(bounds);
            if (first) setOldPosAndRot();
        }

        @Override
        public boolean isPickable() {return cube != null && getParent().isAlive();}

        @Override
        public boolean canBeCollidedWith() {return false;}

        @Override
        public boolean is(Entity entity) {return entity == this || entity == getParent();}

        @Override
        public boolean hurt(DamageSource source, float amount) {
            return acceptsDirectHit() && getParent().hurt(source, amount);
        }

        @Override
        protected void defineSynchedData(SynchedEntityData.Builder builder) {}

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {}

        @Override
        protected void readAdditionalSaveData(CompoundTag tag) {}

        @Override
        public boolean shouldBeSaved() {return false;}

        @Override
        public Entity encounterOwner() {return getParent();}

        @Override
        public Entity dedupeIdentity() {return getParent();}

        @Override
        public boolean acceptsDirectHit() {return cube != null && getParent().isAlive();}
    }
}
