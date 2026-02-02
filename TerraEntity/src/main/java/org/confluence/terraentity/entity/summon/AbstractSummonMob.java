
package org.confluence.terraentity.entity.summon;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.entity.PartEntity;
import org.confluence.terraentity.api.entity.ICollisionAttackEntity;
import org.confluence.terraentity.api.entity.IPartEntityTargetable;
import org.confluence.terraentity.api.entity.ISummonMob;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;
import java.util.UUID;

public abstract class AbstractSummonMob extends TamableAnimal implements GeoEntity, ISummonMob, ICollisionAttackEntity, IPartEntityTargetable {

    protected float distanceToOwner;
    @Nullable
    private Entity actualTargetEntity; // 实际目标实体（可以是 PartEntity）

    public AbstractSummonMob(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    /* Collision Attack API */

    CollisionProperties collisionProperties = new CollisionProperties(5, 5, 0.75f);

    public CollisionProperties getCollisionProperties() {
        return collisionProperties;
    }

    @Override
    public boolean shouldDoCollision() {
        return getTarget() != null || actualTargetEntity != null;
    }

    @Override
    public void tick() {
        super.tick();
        if (summon_discardWhenOwnerDie()) return;

        cleanupInvalidActualTarget();

        doCollisionAttack(this::canAttackTarget, this::doHurtTarget);

        if (this.getOwner() != null) {
            this.distanceToOwner = this.distanceTo(this.getOwner());
        }
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

    /* Summon API */

    public int cost;

    @Override
    public int getCost() {
        return cost;
    }

    @Override
    public void setCost(int cost) {
        this.cost = cost;
    }

    @Override
    protected void registerGoals() {
        summon_registerCommonGoals();
    }

    @Deprecated
    @Override
    public final boolean canAttack(LivingEntity living) {
        return super.canAttack(living);
    }

    public boolean canAttackTarget(Entity target) {
        // 如果目标是 PartEntity，检查父实体是否可以攻击
        if (target instanceof PartEntity<?> partEntity) {
            Entity parent = partEntity.getParent();
            if (parent instanceof LivingEntity living) {
                // 如果 actualTargetEntity 就是这个 PartEntity，或者父实体是 Enemy，则可以攻击
                return living.canBeSeenAsEnemy() && canAttack(living) && (actualTargetEntity == target || parent instanceof Enemy && !(parent instanceof NeutralMob) || parent == getTarget());
            }
            return false;
        }
        if (target instanceof LivingEntity living) {
            return  living.canBeSeenAsEnemy() &&  canAttack(living) && (target instanceof Enemy && !(target instanceof NeutralMob) || target == getTarget());
        }
        return false;
    }

    @Override
    public boolean canBeSeenAsEnemy() {
        return false;
    }

    @Override
    public void onRemovedFromLevel() {
        summon_onRemovedFromLevel();
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        summon_onAddedToLevel();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("cost", cost);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        cost = compound.getInt("cost");
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return source.is(DamageTypes.GENERIC_KILL) && super.hurt(source, amount);
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        return summon_doHurtTarget(entity);
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public EntityDataAccessor<Optional<UUID>> get_DATA_OWNERUUID_ID() {
        return DATA_OWNERUUID_ID;
    }

    /* Geo API */

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    /* super API */

    @Override
    public boolean isFood(ItemStack itemStack) {
        return false;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE)
                .add(Attributes.MAX_HEALTH)
                .add(Attributes.ARMOR)
                .add(Attributes.MOVEMENT_SPEED, 0.5f)
                .add(Attributes.FOLLOW_RANGE, 16)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE)
                .add(Attributes.KNOCKBACK_RESISTANCE)
                .add(Attributes.ATTACK_KNOCKBACK)
                .add(Attributes.ATTACK_SPEED)
                .add(Attributes.FLYING_SPEED)
                .add(Attributes.JUMP_STRENGTH, 0.8f)
                ;
    }
}
