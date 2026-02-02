package org.confluence.terraentity.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.confluence.terraentity.api.entity.IMinion;
import org.confluence.terraentity.entity.boss.QueenBee;
import org.confluence.terraentity.entity.monster.prefab.AbstractPrefab;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.Optional;
import java.util.UUID;

/**
 * 蜂后召唤的蜜蜂
 */
public class LittleHornet extends Hornet implements IMinion {
    QueenBee owner;

    public LittleHornet(EntityType<? extends Monster> type, Level level) {
        super(type, level, new AbstractPrefab()
                .getPrefab()
                .setNoAttachAttack()
                .setNoGravity()
        );
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 2, true));
        this.goalSelector.addGoal(9, new FloatGoal(this));
        this.goalSelector.addGoal(8, new BeeWanderGoal());

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_OWNERUUID_ID, Optional.empty());
    }


    @Override
    public void tick() {
        super.tick();
        if (level() instanceof ServerLevel  sl&& this.owner != null && (tickCount & 31) == 0 && this.isAlive()) {
            setTarget(this.owner.getTarget());
            if(distanceTo(this.owner) > 30 && sl.getBlockState(owner.blockPosition()).is(Blocks.AIR))
                setPos(this.owner.position().add(0,0.5,0));
        }
    }

    @Override
    public boolean hurt(DamageSource source, float pAmount) {
        if(source.getEntity() == this.owner)
            return false;
        return super.hurt(source, pAmount);
    }

    /* Minion API */

    protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID = SynchedEntityData.defineId(LittleHornet.class, EntityDataSerializers.OPTIONAL_UUID);;

    @Override
    public EntityDataAccessor<Optional<UUID>> getDATA_OWNER_UUID() {
        return DATA_OWNERUUID_ID;
    }

    @Override
    public void minion_setOwner(Entity owner){
        if(owner instanceof QueenBee queenBee) {
            minion_setOwnerUUID(owner.getUUID());
            this.owner = queenBee;
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        minion_saveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        minion_readData(compound);
    }

    RawAnimation wing = RawAnimation.begin().thenLoop("wing");
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this, "wing", state->{
            state.setAnimation(wing);
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public void onRemovedFromLevel() {
        super.onRemovedFromLevel();
        if(this.owner!=null){
            this.owner.minionCount --;
        }
    }
}


