package org.confluence.terraentity.entity.summon;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.entity.PartEntity;
import org.confluence.terraentity.api.entity.IPartEntityTargetable;
import org.confluence.terraentity.entity.ai.goal.FlyRangeAttackGoal;
import org.confluence.terraentity.entity.proj.BaseProj;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.constant.DefaultAnimations;

import java.util.function.Supplier;

public class FlyRangeAttackSummonMob<P extends BaseProj<?>> extends AbstractSummonMob implements FlyingAnimal, RangedAttackMob {

    int _attackTicks;
    int delayAttackTicks = -1;
    int _delayAttackTicks;
    LivingEntity delayedTarget;
    float delayedVelocity;
    Supplier<EntityType<P>> projectileSupplier;

    int attackCooldown;
    int replaceCooldown;

    /**
     *
     * @param attackCooldown 攻击间隔
     * @param replaceCooldown 寻路间隔
     * @param attackTicks 攻击动作持续时间
     * @param delayAttackTicks 弹幕发射延迟
     * @param projectileSupplier 弹幕
     */
    public FlyRangeAttackSummonMob(EntityType<? extends FlyRangeAttackSummonMob<?>> entityType, Level level,
                                   int attackCooldown, int replaceCooldown,
                                   int attackTicks, int delayAttackTicks,
                                   Supplier<EntityType<P>> projectileSupplier
    ) {
        super(entityType, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.attackCooldown = attackCooldown;
        this.replaceCooldown = replaceCooldown;
        this._attackTicks = attackTicks;
        this._delayAttackTicks = delayAttackTicks;
        this.projectileSupplier = projectileSupplier;

        if(!this.level().isClientSide){
            this.goalSelector.addGoal(1, new FlyRangeAttackGoal<>(this, this.attackCooldown, this.replaceCooldown));

        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));

    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
    }

    @Override
    public int getMaxHeadXRot() {
        return 85;
    }

    protected void actualRangedAttack(LivingEntity target, float v) {
        BaseProj<?> proj = projectileSupplier.get().create(level());
        if (proj != null) {
            proj.setOwner(this);
            proj.setPos(this.position());
            proj.setDamage((float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));

            // 检查是否有 PartEntity 实际目标，如果有则使用 PartEntity 的位置
            Entity attackTarget = target;
            var actualTarget = this.getActualTargetEntity();
            if (actualTarget instanceof PartEntity<?>) {
                attackTarget = actualTarget;
            }

            double x = attackTarget.getX() - this.getX();
            double y = attackTarget.getY() + (attackTarget instanceof LivingEntity living ? living.getEyeHeight() * 0.5f : attackTarget.getBbHeight() * 0.5f) - this.getY();
            double z = attackTarget.getZ() - this.getZ();
            proj.shoot(x,y,z, 1F, v);
            level().addFreshEntity(proj);
        }
    }

    @Override
    public void performRangedAttack(LivingEntity target, float v) {
        this.swing(InteractionHand.MAIN_HAND);
        this.delayedTarget = target;
        this.delayedVelocity = v;
        this.delayAttackTicks = _delayAttackTicks;
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, level);
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    @Override
    public void tick() {
        super.tick();
        this.updateSwingTime();
        if(!level().isClientSide && --this.delayAttackTicks == 0 && this.delayedTarget != null){
            this.actualRangedAttack(this.delayedTarget, this.delayedVelocity);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Walk/Idle/Attack", 5, state -> {
            if (this.swinging)
                return state.setAndContinue(DefaultAnimations.ATTACK_CAST);
            return state.setAndContinue(state.isMoving() ? DefaultAnimations.WALK : DefaultAnimations.IDLE);
        }));
    }

    @Override
    public int getCurrentSwingDuration() {
        return this._attackTicks;
    }


    @Override
    public boolean isFlying() {
        return true;
    }

    @Override
    public boolean shouldDoCollision(){
        return false;
    }
}
