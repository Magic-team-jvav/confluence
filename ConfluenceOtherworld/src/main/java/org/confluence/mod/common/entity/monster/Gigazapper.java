package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;
import org.confluence.mod.common.entity.projectile.ElectricSpear;
import org.confluence.mod.common.gameevent.MartianEventHelper;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.entity.ModEntities;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;

/// 使用随模型移动的电矛进行近战攻击，每十个游戏刻最多攻击一次。
public final class Gigazapper extends MartianHumanoidMonster {
    public static final int ATTACK_INTERVAL = 10;
    private static final EntityDataAccessor<Boolean> AIMING = SynchedEntityData.defineId(Gigazapper.class, EntityDataSerializers.BOOLEAN);
    private static final RawAnimation STAND = RawAnimation.begin().thenLoop("Stand");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("Walk");
    private static final RawAnimation STAND_AIM = RawAnimation.begin().thenLoop("Stand Aim");
    private static final RawAnimation WALK_AIM = RawAnimation.begin().thenLoop("Walk Aim");
    private int attackCooldown;

    public Gigazapper(EntityType<? extends Gigazapper> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(AIMING, false);
    }

    @Override
    protected boolean hasEntityContactAttack() {
        return true;
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
                if (!ElectricSpear.withinReach(Gigazapper.this, target) || !hasLineOfSight(target)) {
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

        LivingEntity target = getTarget();
        if (isUsableTarget(target)) {
            ElectricSpear.faceTarget(this, target);
        }

        boolean aiming = isUsableTarget(target) && ElectricSpear.withinReach(this, target) && hasLineOfSight(target);
        entityData.set(AIMING, aiming);
        if (!aiming || attackCooldown > 0) {
            return;
        }

        ElectricSpear spear = ModEntities.ELECTRIC_SPEAR.get().create(level());
        if (spear == null) {
            return;
        }

        spear.configure(this);
        MartianEventHelper.inheritEventMembership(this, spear);
        if (level().addFreshEntity(spear)) {
            attackCooldown = ATTACK_INTERVAL;
            triggerAnim("gigazapper", "strike");
            playSound(ModSoundEvents.ITEM_LASER_SHOOT.get(), 0.8F, 1.0F);
        } else {
            spear.discard();
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
        entityData.set(AIMING, false);
    }
}
