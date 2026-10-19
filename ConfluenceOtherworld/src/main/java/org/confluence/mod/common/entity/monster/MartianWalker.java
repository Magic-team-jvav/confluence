package org.confluence.mod.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import org.confluence.lib.common.entity.PartHitTarget;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;
import org.confluence.mod.common.entity.projectile.MonsterLaser;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.init.entity.MonsterEntities;
import software.bernie.geckolib.core.animation.AnimatableManager;

import java.util.*;

/**
 * 三足地面攻击者，左右两侧的 left_fort/right_fort 炮台可独立被摧毁。
 */
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
    private final Gait gait = new Gait();
    private final UUID[] weaponUUIDs = new UUID[2];
    private final WalkerWeapon[] weapons = new WalkerWeapon[2];
    private final int[] missingTicks = new int[2];
    private final float[] savedWeaponHealth = {-1, -1};
    private int attackCooldown;
    private int contactCooldown;
    private int meleePauseTicks;
    private int nextWeapon;
    private List<WalkerGeometry.Box> collisionBoxes = List.of();
    private final LegHitbox[] legHitboxes;
    private final BodyHitbox bodyHitbox;
    private final net.minecraftforge.entity.PartEntity<?>[] hitboxes;

    public MartianWalker(EntityType<? extends MartianWalker> type, Level level) {
        super(type, level);
        setMaxUpStep(1.0F);
        moveControl = new WalkerMoveControl(this);
        legHitboxes = new LegHitbox[30];
        for (int i = 0; i < legHitboxes.length; i++) legHitboxes[i] = new LegHitbox(this);
        bodyHitbox = new BodyHitbox(this);
        hitboxes = Arrays.copyOf(legHitboxes, legHitboxes.length + 1, net.minecraftforge.entity.PartEntity[].class);
        hitboxes[legHitboxes.length] = bodyHitbox;
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
    public net.minecraftforge.entity.PartEntity<?>[] getParts() {return hitboxes;}

    // 根包围盒仅用于地面寻路，不用于玩家碰撞或拾取。
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
        // 即使本体不可被推动，LivingEntity 仍会推动其他实体。
        updateCollisionGeometry();
        AABB bounds = collisionBounds();
        if (bounds == null || !isAlive()) return;
        var pushable = EntitySelector.pushableBy(this);
        for (Entity entity : level().getEntities(this, bounds,
                candidate -> candidate instanceof LivingEntity && pushable.test(candidate))) {
            if (level().isClientSide && !(entity instanceof net.minecraft.world.entity.player.Player))
                continue;
            if (entity instanceof WalkerWeapon weapon && weapon.belongsTo(this, weapon.slot()))
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
        // 玩家和生物由动画部件几何推挤，不将寻路包围盒作为实体障碍。
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
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DESTROYED, 0);
        entityData.define(WALKING, false);
        entityData.define(WALK_SPEED, 0.0F);
        entityData.define(FLIGHT_PHASE, 0);
        entityData.define(PHASE_STARTED, 0L);
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
    public EntityDimensions getDimensions(Pose pose) {
        EntityDimensions dimensions = super.getDimensions(pose);
        return isSpaceship() ? EntityDimensions.scalable(dimensions.width, dimensions.height * 0.55F) : dimensions;
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
        if (level().isClientSide || !isAlive() || entityData.get(FLIGHT_PHASE) != 0 || weaponsDestroyed()
                || getHealth() >= getMaxHealth() * 0.25F) return;
        entityData.set(PHASE_STARTED, level().getGameTime());
        entityData.set(FLIGHT_PHASE, 1);
        getNavigation().stop();
        setDeltaMovement(Vec3.ZERO);
        setNoGravity(true);
        fallDistance = 0;
        retreating = false;
    }

    private void tickFlightMotion() {
        getNavigation().stop();
        fallDistance = 0;
        if (isLaunching()) {
            setDeltaMovement(0, phaseTicks(0) >= 35 ? 0.08 : 0, 0);
            return;
        }
        LivingEntity target = getTarget();
        if (target == null || !target.isAlive() || !canAttack(target)) {
            setDeltaMovement(getDeltaMovement().scale(0.85));
            return;
        }
        getLookControl().setLookAt(target, 30, 30);
        Vec3 offset = target.position().subtract(position()).multiply(1, 0, 1);
        double distance = offset.horizontalDistance();
        double speed = 0;
        if (weaponsDestroyed()) speed = 0.65;
        else if (distance < 14) speed = -0.4;
        else if (distance > 24) speed = 0.5;
        Vec3 horizontal = distance > 1.0E-6 ? offset.scale(speed / distance) : Vec3.ZERO;
        double desiredY = target.getY() + (weaponsDestroyed() ? target.getBbHeight() * 0.5 : 8);
        double bodyY = bodyHitbox.getBoundingBox().getCenter().y;
        Vec3 desired = horizontal.add(0, Mth.clamp((desiredY - bodyY) * 0.12, -0.35, 0.35), 0);
        if (horizontalCollision) desired = desired.add(0, 0.2, 0);
        setDeltaMovement(getDeltaMovement().lerp(desired, 0.15));
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
        // 原版身体控制在停步后会追随头部，模型和碰撞几何也必须限制转速。
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
            // 优先选择可达的撤退点，然后尝试绕开地形障碍的两侧。
            for (double angle : new double[]{0, Math.PI / 3, -Math.PI / 3}) {
                Vec3 destination = position().add(away.yRot((float) angle).scale(10));
                BlockPos block = BlockPos.containing(destination);
                if (!level().isLoaded(block)) continue;
                var path = getNavigation().createPath(block, 0);
                if (path != null && path.canReach() && getNavigation().moveTo(path, 1.0)) return;
            }
            getNavigation().stop();
        } else if (tactic == CombatTactic.LEG_MELEE) {
            // 追踪稳定的玩家位置；动画脚掌偏移会随转向变化，不能反过来驱动寻路终点。
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
        resolveAndCreateWeapons(server);
        for (int slot = 0; slot < 2; slot++) {
            if (weapons[slot] != null && weapons[slot].isAlive()) {
                savedWeaponHealth[slot] = weapons[slot].getHealth();
                AABB bounds = weaponBounds(slot);
                if (bounds != null) weapons[slot].follow(bounds);
            }
        }
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
            if (!ModEntities.MONSTER_LASER.isPresent()) return;
            MonsterLaser laser = ModEntities.MONSTER_LASER.get().create(server);
            if (laser == null) return;
            laser.configureShot(this, target, weapon.getBoundingBox().getCenter(), MonsterLaser.Variant.MARTIAN_WALKER);
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
        // 每个立方体都跟随与客户端模型相同的动画骨骼变换。
        // 不使用外围三脚架包围盒：玩家可以站在腿部之间的空隙中。
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

    private void resolveAndCreateWeapons(ServerLevel server) {
        List<WalkerWeapon> created = new ArrayList<>();
        for (int slot = 0; slot < 2; slot++) {
            if (weaponDestroyed(slot)) continue;
            WalkerWeapon weapon = weapons[slot];
            if (weapon != null && !weapon.isRemoved()) continue;
            weapons[slot] = null;
            if (weaponUUIDs[slot] != null) {
                Entity restored = server.getEntity(weaponUUIDs[slot]);
                if (restored instanceof WalkerWeapon part && part.isAlive() && part.belongsTo(this, slot)) {
                    weapons[slot] = part;
                    missingTicks[slot] = 0;
                    continue;
                }
                if (++missingTicks[slot] <= WalkerWeapon.RESOLUTION_GRACE_TICKS) {
                    continue;
                }
                // 加载宽限结束后放弃旧身份，允许新炮台登记到该槽位。
                weaponUUIDs[slot] = null;
            }
            AABB bounds = weaponBounds(slot);
            if (bounds == null || !MonsterEntities.WALKER_WEAPON.isPresent()) continue;
            WalkerWeapon part = MonsterEntities.WALKER_WEAPON.get().create(server);
            if (part == null) {
                rollback(created);
                return;
            }
            if (!part.bind(this, slot)) {
                part.discard();
                rollback(created);
                return;
            }
            if (savedWeaponHealth[slot] > 0) {
                part.setHealth(Math.min(part.getMaxHealth(), savedWeaponHealth[slot]));
            }
            part.follow(bounds);
            if (!server.addFreshEntity(part)) {
                part.discard();
                rollback(created);
                return;
            }
            created.add(part);
        }
    }

    private void rollback(List<WalkerWeapon> created) {
        for (WalkerWeapon part : created) {
            part.discard();
        }
    }

    boolean acceptWeapon(WalkerWeapon part, int slot) {
        if (part == null || slot < 0 || slot > 1 || weaponDestroyed(slot) || !isAlive()
                || !part.isAlive() || !part.belongsTo(this, slot)) {
            return false;
        }
        if (part.getUUID().equals(weaponUUIDs[slot ^ 1])) {
            return false;
        }
        if (weaponUUIDs[slot] != null && !weaponUUIDs[slot].equals(part.getUUID())) {
            return false;
        }
        weapons[slot] = part;
        weaponUUIDs[slot] = part.getUUID();
        missingTicks[slot] = 0;
        return true;
    }

    void releaseWeapon(WalkerWeapon weapon, RemovalReason reason) {
        int slot = weapon.slot();
        if (weapons[slot] != weapon) {
            return;
        }
        if (weapon.isAlive() && !weaponDestroyed(slot)) {
            savedWeaponHealth[slot] = weapon.getHealth();
        }
        weapons[slot] = null;
        if (reason.shouldDestroy()) {
            weaponUUIDs[slot] = null;
            missingTicks[slot] = 0;
        }
    }

    public void destroyWeapon(int slot) {
        if (slot < 0 || slot > 1) return;
        entityData.set(DESTROYED, entityData.get(DESTROYED) | (1 << slot));
        weapons[slot] = null;
        weaponUUIDs[slot] = null;
        savedWeaponHealth[slot] = -1;
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

    private void clearWeapons() {
        if (!(level() instanceof ServerLevel server)) return;
        entityData.set(DESTROYED, 3);
        for (int i = 0; i < 2; i++) {
            Entity part = weapons[i] != null ? weapons[i] : weaponUUIDs[i] == null ? null : server.getEntity(weaponUUIDs[i]);
            if (part instanceof WalkerWeapon weapon && weapon.belongsTo(this, i)) weapon.discard();
        }
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (dead) {
            clearWeapons();
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        if (reason.shouldDestroy()) clearWeapons();
        super.remove(reason);
    }

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
        for (int i = 0; i < 2; i++) {
            if (weaponUUIDs[i] != null) tag.putUUID("Weapon" + i, weaponUUIDs[i]);
            float health = weapons[i] != null && weapons[i].isAlive() ? weapons[i].getHealth() : savedWeaponHealth[i];
            if (health > 0 && Float.isFinite(health)) tag.putFloat("WeaponHealth" + i, health);
        }
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
        nextWeapon = tag.contains("NextWeapon") ? Mth.clamp(tag.getInt("NextWeapon"), 0, 1) : 0;
        retreating = false;
        for (int i = 0; i < 2; i++) {
            weaponUUIDs[i] = tag.hasUUID("Weapon" + i) ? tag.getUUID("Weapon" + i) : null;
            weapons[i] = null;
            missingTicks[i] = 0;
            float health = tag.contains("WeaponHealth" + i) ? tag.getFloat("WeaponHealth" + i) : -1;
            savedWeaponHealth[i] = Float.isFinite(health) && health > 0 ? Mth.clamp(health, 0, 100000) : -1;
        }
        if (weaponUUIDs[0] != null && weaponUUIDs[0].equals(weaponUUIDs[1])) weaponUUIDs[1] = null;
        collisionBoxes = List.of();
        setTarget(null);
    }

    /**
     * 每个实体独立的瞬时步态时钟。渲染与碰撞使用相同的混合姿态。
     */
    public static final class Gait {
        private float weight, previousWeight, rate;
        private double walkSeconds, previousWalkSeconds;

        public void tick(boolean moving, float speed) {
            previousWeight = weight;
            previousWalkSeconds = walkSeconds;
            weight = Mth.clamp(weight + (moving ? 1.0F / 8 : -1.0F / 10), 0, 1);
            float targetRate = moving && Float.isFinite(speed) ? Mth.clamp(speed, 0.35F, 1.6F) : 0;
            rate += (targetRate - rate) * 0.25F;
            // 在淡出时完成当前步伐，而不是让抬起的脚冻结。
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

    /**
     * 上半身受击体，与本体基于脚部的寻路包围盒分离。
     */
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
        // Living entities are pushed by the owner; projectiles only perform damage collision.
        public boolean canBeCollidedWith() {return false;}

        @Override
        public boolean is(Entity entity) {return entity == this || entity == getParent();}

        @Override
        public boolean hurt(DamageSource source, float amount) {return getParent().isAlive() && getParent().hurt(source, amount);}

        @Override
        protected void defineSynchedData() {}

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {}

        @Override
        protected void readAdditionalSaveData(CompoundTag tag) {}

        @Override
        public boolean shouldBeSaved() {return false;}

        @Override
        public Entity damageRecipient() {return this;}

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
            // 大角度转弯时减速，避免越过近处的路径节点后持续绕圈。
            mob.setSpeed((float) Math.min(mob.getSpeed() * alignment, distance));
        }

        @Override
        protected float rotlerp(float current, float target, float maximumChange) {
            // 原版允许每 tick 转向 90 度，路径节点切换时会导致大型步行者急转。
            return super.rotlerp(current, target, Math.min(maximumChange, MAX_TURN_PER_TICK));
        }
    }

    /**
     * 腿部的一个立方体，将伤害转发给本体。两个炮台是独立的 LivingEntity。
     */
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
        protected void defineSynchedData() {}

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {}

        @Override
        protected void readAdditionalSaveData(CompoundTag tag) {}

        @Override
        public boolean shouldBeSaved() {return false;}

        @Override
        public Entity damageRecipient() {return this;}

        @Override
        public Entity encounterOwner() {return getParent();}

        @Override
        public Entity dedupeIdentity() {return getParent();}

        @Override
        public boolean acceptsDirectHit() {return cube != null && getParent().isAlive();}
    }
}
