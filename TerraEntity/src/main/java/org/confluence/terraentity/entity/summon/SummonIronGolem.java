package org.confluence.terraentity.entity.summon;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.entity.PartEntity;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.api.entity.IPartEntityTargetable;
import org.confluence.terraentity.api.entity.ISummonMob;
import org.confluence.terraentity.entity.ai.goal.summon.SummonMeleeAttackGoal;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class SummonIronGolem extends IronGolem implements ISummonMob , IPartEntityTargetable {

    @Nullable
    private Entity actualTargetEntity;

    static ResourceLocation moveSpeedModify = TerraEntity.space("summon");
    public SummonIronGolem(EntityType<? extends IronGolem> entityType, Level level) {
        super(entityType, level);
        if(!this.getAttribute(Attributes.MOVEMENT_SPEED).hasModifier(moveSpeedModify))
            this.getAttribute(Attributes.MOVEMENT_SPEED).addTransientModifier(new AttributeModifier(moveSpeedModify, 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SummonMeleeAttackGoal<>(this, 1.0, true));
        this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9, 32.0F));

        summon_registerCommonGoals();
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        this.attackAnimationTick = 10;
        this.level().broadcastEntityEvent(this, (byte)4);
        boolean flag = summon_doHurtTarget(entity);
        if (flag) {
            double resistance;
            if (entity instanceof LivingEntity living) {
                resistance = living.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
            } else {
                resistance = 0.0;
            }
            double d1 = Math.max(0.0, 1.0 - resistance);
            entity.setDeltaMovement(entity.getDeltaMovement().add(0.0, 0.4000000059604645 * d1, 0.0));
            Level var11 = this.level();
            if (var11 instanceof ServerLevel serverLevel) {
                EnchantmentHelper.doPostAttackEffects(serverLevel, entity, this.damageSources().mobAttack(this));
            }
        }

        this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        return flag;
    }

    /* Summon API */
    /* 以下是通用写法 */

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

    protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID = SynchedEntityData.defineId(SummonIronGolem.class, EntityDataSerializers.OPTIONAL_UUID);

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_OWNERUUID_ID, Optional.empty());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);this.registerGoals();
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
    public void tick() {
        super.tick();
        cleanupInvalidActualTarget();
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        if(target == summon_getOwner()) return false;
        return super.canAttack(target);
    }

    @Override
    public boolean canBeSeenAsEnemy() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return source.is(DamageTypes.GENERIC_KILL) && super.hurt(source, amount);
    }

    @Override
    public boolean isPickable() {
        return false;
    }

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
