package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.EnemyDamageRules;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModSoundEvents;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** 使用随模型移动的电矛进行近战攻击，每十 tick 最多攻击一次。 */
public class Gigazapper extends MartianHumanoidMonster {
    public enum AttackType { SPEAR }

    public static final int ATTACK_INTERVAL = 10;
    private static final double MODEL_UNIT = 1.3 / 16.0;
    private static final EntityDataAccessor<Boolean> AIMING = SynchedEntityData.defineId(Gigazapper.class, EntityDataSerializers.BOOLEAN);
    private static final RawAnimation STAND = RawAnimation.begin().thenLoop("Stand");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("Walk");
    private static final RawAnimation STAND_AIM = RawAnimation.begin().thenLoop("Stand Aim");
    private static final RawAnimation WALK_AIM = RawAnimation.begin().thenLoop("Walk Aim");
    private final Set<UUID> spearHitTargets = new HashSet<>();
    private int attackCooldown;
    private int spearAttackTicks;
    private float spearDamage;

    public Gigazapper(EntityType<? extends Gigazapper> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(AIMING, false);
    }

    @Override
    protected boolean hasEntityContactAttack() {
        return true;
    }

    public boolean canUseSpear() {
        return true;
    }

    protected final boolean isAiming() {
        return entityData.get(AIMING);
    }

    private boolean isUsableTarget(LivingEntity target) {
        return target != null && target.isAlive() && canAttack(target);
    }

    @Override
    protected BTNode createLandBehavior() {
        BTNode combatBehavior = new BTNode() {
            @Override
            public boolean canStart() {
                return isUsableTarget(getTarget());
            }

            @Override
            public BTStatus execute() {
                LivingEntity target = getTarget();
                if (!isUsableTarget(target)) {
                    return BTStatus.FAILURE;
                }

                getLookControl().setLookAt(target, 30, 30);
                if (!withinSpearReach(target) || !hasLineOfSight(target)) {
                    getNavigation().moveTo(target, 1);
                } else {
                    getNavigation().stop();
                }

                return BTStatus.RUNNING;
            }

            @Override
            public void stop() {
                getNavigation().stop();
            }

        };
        return SelectorNode.of(combatBehavior,
                new VanillaGoalAction(new WaterAvoidingRandomStrollGoal(this, 0.8)),
                new VanillaGoalAction(new RandomLookAroundGoal(this)));
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide || !isAlive()) {
            return;
        }

        if (attackCooldown > 0) {
            attackCooldown--;
        }

        if (!canUseSpear() || isNoAi()) {
            if (spearAttackTicks > 0) {
                stopTriggeredAnimation("gigazapper", "strike");
            }
            spearAttackTicks = 0;
            spearHitTargets.clear();
            entityData.set(AIMING, false);
            return;
        }

        LivingEntity target = getTarget();
        if (isUsableTarget(target)) {
            faceSpearTarget(target);
        }
        boolean aiming = isUsableTarget(target) && withinSpearReach(target) && hasLineOfSight(target);
        entityData.set(AIMING, aiming);
        if (aiming && attackCooldown == 0) {
            attackCooldown = ATTACK_INTERVAL;
            spearAttackTicks = ATTACK_INTERVAL;
            spearDamage = specialAttackDamage(AttackType.SPEAR);
            spearHitTargets.clear();
            triggerAnim("gigazapper", "strike");
            playSound(ModSoundEvents.ITEM_LASER_SHOOT.get(), 0.8F, 1.0F);
        }
        if (spearAttackTicks > 0) {
            tickSpearAttack(ATTACK_INTERVAL - spearAttackTicks);
            if (--spearAttackTicks == 0) {
                spearHitTargets.clear();
            }
        }
    }

    /** Model shaft [-6,8,-9] + [1,1,12], attack arm offset [1,4,0]. */
    private Vec3 spearPoint(double z) {
        double yaw = Math.toRadians(yBodyRot);
        double unit = MODEL_UNIT * getScale();
        double x = -4.5 * unit;
        return position().add(x * Math.cos(yaw) + z * unit * Math.sin(yaw),
                12.5 * unit, x * Math.sin(yaw) - z * unit * Math.cos(yaw));
    }

    private boolean withinSpearReach(LivingEntity target) {
        return intersectsSpear(target.getBoundingBox(), spearPoint(-1), spearPoint(-13));
    }

    private boolean intersectsSpear(AABB bounds, Vec3 start, Vec3 end) {
        AABB expanded = bounds.inflate(0.55 * MODEL_UNIT * getScale());
        return expanded.contains(start) || expanded.contains(end) || expanded.clip(start, end).isPresent();
    }

    private void faceSpearTarget(LivingEntity target) {
        Vec3 direction = target.position().subtract(position());
        // Align the right-hand shaft rather than the body's centre with the victim.
        double offset = Math.asin(Math.min(1.0,
                4.5 * MODEL_UNIT * getScale() / Math.max(direction.horizontalDistance(), 0.001)));
        faceCombatDirection(direction.yRot((float) offset), 30, 0);
    }

    private void tickSpearAttack(int age) {
        // attack.spear: extend during ticks 0-2, hold to tick 5, retract during ticks 5-7.
        if (age > 7) {
            return;
        }
        double extension;
        if (age < 2) {
            extension = age * 2.0;
        } else if (age <= 5) {
            extension = 4.0;
        } else {
            extension = (7 - age) * 2.0;
        }
        Vec3 start = spearPoint(3 - extension);
        Vec3 end = spearPoint(-9 - extension);
        var wall = level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (wall.getType() != HitResult.Type.MISS) {
            end = wall.getLocation();
        }
        AABB bounds = new AABB(start, end).inflate(0.55 * MODEL_UNIT * getScale());
        for (LivingEntity victim : level().getEntitiesOfClass(LivingEntity.class, bounds)) {
            if (victim == this || !victim.isAlive() || !canAttack(victim) || EnemyDamageRules.isProtectedEnemy(victim)
                    || spearHitTargets.contains(victim.getUUID()) || !intersectsSpear(victim.getBoundingBox(), start, end)) {
                continue;
            }
            spearHitTargets.add(victim.getUUID());
            if (victim.hurt(damageSources().mobAttack(this), spearDamage)) {
                setLastHurtMob(victim);
                victim.addEffect(new MobEffectInstance(ModEffects.ELECTRIFIED.get(), 80 + random.nextInt(81)));
            }
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.GIGAZAPPER_FREE.get();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<Gigazapper> controller = new AnimationController<>(this, "gigazapper", 0, state -> {
            RawAnimation movement = state.isMoving() ? WALK : STAND;
            RawAnimation aiming = state.isMoving() ? WALK_AIM : STAND_AIM;
            return state.setAndContinue(entityData.get(AIMING) ? aiming : movement);
        });
        controller.triggerableAnim("strike", RawAnimation.begin().thenPlay("attack.spear"));
        controllers.add(controller);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("ShootingCooldown", attackCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        attackCooldown = Mth.clamp(tag.getInt("ShootingCooldown"), 0, ATTACK_INTERVAL);
        // Unfinished melee strikes must not resume and hit a victim again after loading.
        spearAttackTicks = 0;
        spearHitTargets.clear();
        entityData.set(AIMING, false);
    }
}
