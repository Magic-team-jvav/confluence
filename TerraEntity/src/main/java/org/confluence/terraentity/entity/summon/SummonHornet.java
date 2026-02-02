package org.confluence.terraentity.entity.summon;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.terraentity.api.entity.IPartEntityTargetable;
import org.confluence.terraentity.api.entity.ISummonMob;
import org.confluence.terraentity.entity.ai.goal.summon.SummonFlyFlowOwnerGoal;
import org.confluence.terraentity.entity.monster.Hornet;
import org.confluence.terraentity.entity.monster.prefab.FlyMonsterPrefab;
import org.confluence.terraentity.entity.proj.LineProj;
import org.confluence.terraentity.init.entity.TEProjectileEntities;
import org.confluence.terraentity.utils.TEUtils;
import org.jetbrains.annotations.Nullable;
import net.neoforged.neoforge.entity.PartEntity;

import java.util.Optional;
import java.util.UUID;

public class SummonHornet extends Hornet implements ISummonMob, IPartEntityTargetable {

    @Nullable
    private Entity actualTargetEntity;

    public SummonHornet(EntityType<? extends Monster> type, Level level) {
        super(type, level, FlyMonsterPrefab.BEE_BUILDER.get());
        this.attackInternal = 20;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new BeeShootGoal(this, 0, 10){
            @Override
            protected boolean canShoot(Entity target) {
                return true;
            }

            @Override
            public void tick() {
                Entity actualTarget = getActualTarget();
                if (actualTarget != null && actualTarget.isAlive()) {
                    bee.lookAt(actualTarget, 10, 89);
                    bee.getLookControl().setLookAt(actualTarget);
                } else {
                    super.tick();
                }
            }

            @Override
            public void stop() {
                Entity actualTarget = getActualTarget();
                if (actualTarget != null) {
                    bee.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
                    LineProj proj = createProj();
                    if (proj != null) {
                        proj.setOwner(bee);
                        proj.setPos(bee.position());
                        proj.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.POISON, 100, 0));
                        double x = actualTarget.getX() - bee.getX();
                        double y = actualTarget.getY() + (actualTarget instanceof LivingEntity living ? living.getEyeHeight() * 0.5f : 0) - bee.getY();
                        double z = actualTarget.getZ() - bee.getZ();
                        proj.shoot(x, y, z, 1, 0);
                        level().addFreshEntity(proj);
                    }
                    timeToShoot = SHOOT_TIME;
                } else {
                    super.stop();
                }
            }

            private Entity getActualTarget() {
                if (bee instanceof IPartEntityTargetable targetable) {
                    Entity actualTarget = targetable.getActualTargetEntity();
                    if (actualTarget != null) return actualTarget;
                }
                return bee.getTarget();
            }
        });
        this.goalSelector.addGoal(2, new BeeKeepOnTargetGoal(this));
        this.goalSelector.addGoal(9, new FloatGoal(this));

        registerTargetGoal(this.targetSelector);
    }

    @Override
    public void tick() {
        super.tick();
        cleanupInvalidActualTarget();

        Entity target = getActualTargetEntity();
        if (target == null) {
            target = getTarget();
        }
        if (target != null) {
            this.lookAt(target, 10, 85);
        }
    }

    protected void registerTargetGoal(GoalSelector targetSelector){
        summon_registerCommonGoals();
    }

    protected LineProj createProj(){
        return TEProjectileEntities.SUMMON_BEE_STICK_PROJ.get().create(level()).setDamage((float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
    }

    public void summon_registerMoveGoal(){
        goalSelector.addGoal(6, new SummonFlyFlowOwnerGoal<>(this, 1.0,  2.0F));
    }


    public boolean ignoreAttributeModify(){
        return true;
    }

    protected boolean shouldDespawnInPeaceful() {
        return false;
    }


    /* Summon API */
    int cost;

    @Override
    public EntityDataAccessor<Optional<UUID>> get_DATA_OWNERUUID_ID() {
        return DATA_OWNERUUID_ID;
    }

    @Override
    public int getCost() {
        return cost;
    }

    @Override
    public void setCost(int cost) {
        this.cost = cost;
    }

    protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID = SynchedEntityData.defineId(SummonHornet.class, EntityDataSerializers.OPTIONAL_UUID);

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_OWNERUUID_ID, Optional.empty());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        this.summon_addData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.summon_readData(compound);
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        summon_onAddedToLevel();
    }

    @Override
    public void onRemovedFromLevel() {
        super.onRemovedFromLevel();
        summon_onRemovedFromLevel();
    }


    @Override
    public boolean canAttack(LivingEntity target) {
        if(target == summon_getOwner()) return false;
        return target.canBeSeenAsEnemy() && TEUtils.attackTamableTest.test(summon_getOwner(), target);
    }

    @Override
    public boolean canBeSeenAsEnemy() {
        return false;
    }

    @Override
    public boolean canBeSeenByAnyone() {
        return false;
    }

    public boolean isPreventingPlayerRest(Player player) {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return source.is(DamageTypes.GENERIC_KILL) && super.hurt(source, amount);
    }

    @Override
    public boolean summon_canFlyToOwner(){
        return true;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean shouldDoCollision() {
        return false;
    }

    /* IPartEntityTargetable API */

    @Override
    @Nullable
    public Entity getActualTargetEntity() {
        return actualTargetEntity;
    }

    @Override
    public void setActualTargetEntity(@Nullable Entity entity) {
        this.actualTargetEntity = entity;
    }

    @Override
    public boolean canAttackTarget(Entity target) {
        LivingEntity entity = null;
        if(target instanceof PartEntity<?> part && part.getParent() instanceof LivingEntity parent){
            entity = parent;
        }else if(target instanceof LivingEntity living) {
            entity = living;
        }

        return entity != null && this.canAttack(entity);
    }
}


