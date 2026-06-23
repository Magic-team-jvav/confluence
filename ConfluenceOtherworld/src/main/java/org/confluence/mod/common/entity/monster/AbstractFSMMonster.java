package org.confluence.mod.common.entity.monster;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.mod.api.entity.ai.IFSMGeoMob;
import org.confluence.mod.common.entity.monster.prefab.AttributeBuilder;
import software.bernie.geckolib.core.animation.AnimatableManager;

public abstract class AbstractFSMMonster extends AbstractMonster implements IFSMGeoMob {

    protected ClientBoundAnimationMessage clientBoundAnimationMessage = new ClientBoundAnimationMessage();
    FSMGoal<?> fsmGoal;

    protected static final EntityDataAccessor<Integer> DATA_SKILL_INDEX = SynchedEntityData.defineId(AbstractFSMMonster.class, EntityDataSerializers.INT);

    public AbstractFSMMonster(EntityType<? extends Monster> type, Level level, AttributeBuilder builder) {
        super(type, level, builder);

        fsmGoal = createFSMGoal(DATA_SKILL_INDEX);
    }

    public AbstractFSMMonster(EntityType<? extends Monster> type, Level level) {
        this(type, level, new AttributeBuilder());
    }

    @Override
    protected void registerGoals() {

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));

    }

    protected abstract FSMGoal<?> createFSMGoal(EntityDataAccessor<Integer> data);

    @Override
    public void tick() {
         super.tick();
         if(level().isClientSide){
             this.getSkills().tick();
         }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SKILL_INDEX, 0);
    }
    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if(key == DATA_SKILL_INDEX) {
            syncSkills(key);
        }
    }


    @Override
    public void onAddedToLevel(){
        super.onAddedToLevel();

        if(!level().isClientSide){
            this.goalSelector.addGoal(0, fsmGoal);
        }
    }

    @Override
    public void addSkills() {
    }

    @Override
    public CircleMobSkills getSkills() {
        return fsmGoal.getSkills();
    }

    @Override
    public ClientBoundAnimationMessage getAnimationMessage() {
        return clientBoundAnimationMessage;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        IFSMGeoMob.super.registerControllers(controllers);
    }
}
