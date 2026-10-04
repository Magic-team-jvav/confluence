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
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import org.confluence.mod.common.entity.PartHitTarget;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;
import org.confluence.mod.common.entity.projectile.MonsterLaser;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.joml.Matrix4f;
import org.joml.Vector3f;
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
    private final Gait gait = new Gait();
    private final UUID[] weaponUUIDs = new UUID[2];
    private final WalkerWeapon[] weapons = new WalkerWeapon[2];
    private final int[] missingTicks = new int[2];
    private final float[] savedWeaponHealth = {-1, -1};
    private int attackCooldown;
    private int contactCooldown;
    private int nextWeapon;
    private List<WalkerGeometry.Box> collisionBoxes = List.of();
    private final LegHitbox[] legHitboxes;
    private final BodyHitbox bodyHitbox;
    private final net.minecraftforge.entity.PartEntity<?>[] hitboxes;

    public MartianWalker(EntityType<? extends MartianWalker> type, Level level) {
        super(type, level);
        setMaxUpStep(1.0F);
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

    public AABB softCollisionBounds() {
        return bodyHitbox == null || collisionBoxes.isEmpty() ? null : bodyHitbox.getBoundingBox();
    }

    @Override
    protected void pushEntities() {
        // 即使本体不可被推动，LivingEntity 仍会推动其他实体。
        updateCollisionGeometry();
        AABB bounds = softCollisionBounds();
        if (bounds == null || !isAlive()) return;
        for (Entity entity : level().getEntities(this, bounds, EntitySelector.pushableBy(this))) {
            if (level().isClientSide && !(entity instanceof net.minecraft.world.entity.player.Player))
                continue;
            if (entity instanceof net.minecraftforge.entity.PartEntity<?> part && part.getParent() == this)
                continue;
            if (entity instanceof WalkerWeapon weapon && weapon.belongsTo(this, weapon.slot()))
                continue;
            doPush(entity);
        }
    }

    @Override
    protected void doPush(Entity entity) {
        AABB bounds = softCollisionBounds();
        if (entity != null && bounds != null && bounds.intersects(entity.getBoundingBox()))
            super.doPush(entity);
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        // 硬碰撞由动画部件提供，而非外围的三脚架包围盒。
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
            } else if (box.bone().equals("main") || box.bone().equals("canopy") || box.bone().equals("body") || box.bone().equals("head")) {
                body = body == null ? box.bounds() : body.minmax(box.bounds());
            }
        }
        if (body != null && bodyHitbox != null) bodyHitbox.follow(body);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DESTROYED, 0);
        entityData.define(WALKING, false);
        entityData.define(WALK_SPEED, 0.0F);
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

    public WalkerGeometry.Pose animationPose(float partialTick) {return gait.pose(animationSeconds(partialTick), partialTick);}

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                WaterAvoidingRandomStrollGoal stroll = new WaterAvoidingRandomStrollGoal(MartianWalker.this, 0.65);
                stroll.setInterval(40);
                stroll.trigger();
                return SelectorNode.of(new BTNode() {
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
            WalkerGeometry.Box foot = collisionBoxes.stream()
                    .filter(box -> box.bone().matches("(front|left|right)_leg4"))
                    .min(Comparator.comparingDouble(box -> box.bounds().getCenter().subtract(target.position()).horizontalDistanceSqr()))
                    .orElse(null);
            if (foot == null) {
                getNavigation().moveTo(target, 1.25);
                return;
            }
            if (collisionBoxes.stream().anyMatch(box -> box.leg() && box.intersects(target.getBoundingBox()))) {
                getNavigation().stop();
                setDeltaMovement(getDeltaMovement().multiply(0, 1, 0));
            } else {
                Vec3 offset = target.position().subtract(foot.bounds().getCenter());
                getNavigation().moveTo(getX() + offset.x, target.getY(), getZ() + offset.z, 1.25);
            }
        } else if (tactic == CombatTactic.APPROACH) getNavigation().moveTo(target, 1.0);
    }

    @Override
    public void tick() {
        Vec3 beforeMove = position();
        super.tick();
        if (!isAlive()) return;
        if (!level().isClientSide) {
            double distance = position().subtract(beforeMove).horizontalDistance();
            boolean moving = onGround() && distance > (walking() ? 0.005 : 0.015) * getScale();
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
        if (weaponsDestroyed() || attackCooldown != 0 || !onGround() || target == null || !target.isAlive()
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
        for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class,
                new AABB(position(), position().add(0, 9 * getScale(), 0)).inflate(5 * getScale()), this::canAttack)) {
            if (target == this || !canContactAttack(target)) continue;
            if (intersectsContactVolume(target.getBoundingBox()))
                hit |= doContactHurtTarget(target);
        }
        if (hit) contactCooldown = 20;
    }

    public boolean intersectsContactVolume(AABB bounds) {
        return bounds != null && collisionBoxes.stream().anyMatch(box ->
                (weaponsDestroyed() ? box.leg() : !box.bone().endsWith("fort")) && box.intersects(bounds));
    }

    public AABB weaponBounds(int slot) {
        if (slot < 0 || slot > 1 || weaponDestroyed(slot)) return null;
        String bone = slot == 0 ? "left_fort" : "right_fort";
        AABB result = null;
        for (WalkerGeometry.Box box : collisionBoxes)
            if (box.bone().equals(bone))
                result = result == null ? box.bounds() : result.minmax(box.bounds());
        return result;
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
                if (++missingTicks[slot] <= 200) continue;
            }
            AABB bounds = weaponBounds(slot);
            if (bounds == null || !MonsterEntities.WALKER_WEAPON.isPresent()) continue;
            WalkerWeapon part = MonsterEntities.WALKER_WEAPON.get().create(server);
            if (part == null) {
                rollback(created);
                return;
            }
            part.bind(this, slot);
            if (savedWeaponHealth[slot] > 0)
                part.setHealth(Math.min(part.getMaxHealth(), savedWeaponHealth[slot]));
            part.follow(bounds);
            if (!server.addFreshEntity(part)) {
                part.discard();
                rollback(created);
                return;
            }
            weapons[slot] = part;
            weaponUUIDs[slot] = part.getUUID();
            missingTicks[slot] = 0;
            created.add(part);
        }
    }

    private void rollback(List<WalkerWeapon> created) {
        for (WalkerWeapon part : created) {
            int slot = part.slot();
            part.discard();
            weapons[slot] = null;
            weaponUUIDs[slot] = null;
        }
    }

    public boolean acceptWeapon(WalkerWeapon part, int slot) {
        if (part == null || slot < 0 || slot > 1 || weaponDestroyed(slot) || !isAlive() || !part.belongsTo(this, slot))
            return false;
        if (part.getUUID().equals(weaponUUIDs[slot ^ 1])) return false;
        if (weaponUUIDs[slot] != null && !weaponUUIDs[slot].equals(part.getUUID())) return false;
        weapons[slot] = part;
        weaponUUIDs[slot] = part.getUUID();
        missingTicks[slot] = 0;
        return true;
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
        if (result) delayLaserAttack();
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
        clearWeapons();
        super.die(source);
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
        gait.reset();
        attackCooldown = tag.contains("AttackCooldown") ? Mth.clamp(tag.getInt("AttackCooldown"), 0, 200) : 0;
        contactCooldown = tag.contains("ContactCooldown") ? Mth.clamp(tag.getInt("ContactCooldown"), 0, 20) : 0;
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
        public boolean canBeCollidedWith() {return getParent().isAlive();}

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

    /**
     * 腿部的一个立方体，将伤害转发给本体。两个炮台是独立的 LivingEntity。
     */
    public static final class LegHitbox extends PartEntity<MartianWalker> implements PartHitTarget {
        private WalkerGeometry.Box cube;
        private int followedTick = Integer.MIN_VALUE;

        public LegHitbox(MartianWalker parent) {super(parent);}

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
        public boolean isPickable() {return getParent().isAlive();}

        @Override
        public boolean canBeCollidedWith() {return getParent().isAlive();}

        @Override
        public boolean is(Entity entity) {return entity == this || entity == getParent();}

        @Override
        public boolean hurt(DamageSource source, float amount) {
            if (cube == null || !getParent().isAlive()) return false;
            Entity direct = source.getDirectEntity();
            if (direct != null) {
                Vec3 start = direct instanceof Projectile ? direct.position() : direct.getEyePosition();
                Vec3 end = direct instanceof Projectile ? start.add(direct.getDeltaMovement().scale(2))
                        : start.add(direct.getViewVector(1).scale(6));
                Matrix4f inverse = new Matrix4f(cube.transform()).invert();
                Vector3f a = inverse.transformPosition(start.toVector3f()), b = inverse.transformPosition(end.toVector3f());
                Vector3f half = cube.halfSize();
                AABB local = new AABB(-half.x, -half.y, -half.z, half.x, half.y, half.z);
                if (!local.contains(a.x, a.y, a.z) && local.clip(new Vec3(a.x, a.y, a.z), new Vec3(b.x, b.y, b.z)).isEmpty())
                    return false;
            }
            return getParent().hurt(source, amount);
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
        public boolean acceptsDirectHit() {return getParent().isAlive();}
    }
}
