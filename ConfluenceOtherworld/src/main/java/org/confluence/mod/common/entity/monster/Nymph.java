package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.confluence.mod.common.entity.monster.prefab.AbstractPrefab;
import org.confluence.mod.common.init.entity.MonstersEntities;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * 宁芙
 */
public class Nymph extends AbstractMonster {

    int delayTime = 0;
    int recoverTime = 0;
    int _recoverTime;
    boolean isTamed = false;
    NymphLookAtPlayerGoal lookAtPlayerGoal;
    private UUID conversionStarter;
    private int conversionTime;

    private static final EntityDataAccessor<Boolean> DATA_CONVERTING_ID = SynchedEntityData.defineId(Nymph.class, EntityDataSerializers.BOOLEAN);;
    private static final EntityDataAccessor<Boolean> DATA_TRIGGER =  SynchedEntityData.defineId(Nymph.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_TAMED =  SynchedEntityData.defineId(Nymph.class, EntityDataSerializers.BOOLEAN);

    public Nymph(EntityType<? extends Monster> type, Level level) {
        super(type, level, new AbstractPrefab().getPrefab());
        this.xpReward = 20;
        _recoverTime = 75 + getRandom().nextInt(50);
    }

    @Override
    protected EntityDimensions getDefaultDimensions(Pose pose) {
        if(!this.isTrigger() && !isTamed) {
            return super.getDefaultDimensions(pose).scale(1, 0.75f).withEyeHeight(1.05f);
        }
        return super.getDefaultDimensions(pose);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TRIGGER, false);
        this.entityData.define(DATA_CONVERTING_ID, false);
        this.entityData.define(DATA_TAMED, false);
    }


    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("isTamed", isTamed);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        isTamed = tag.getBoolean("isTamed");
        this.setTamed(isTamed);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if(key == DATA_TRIGGER) {
            refreshDimensions();
        }else if(key == DATA_TAMED){
            this.isTamed = this.entityData.get(DATA_TAMED);
            refreshDimensions();
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 0.6D, true){
            @Override
            public boolean canUse() {
                return !Nymph.this.isTamed && super.canUse() && delayTime > 20;
            }
        });
        this.lookAtPlayerGoal = new NymphLookAtPlayerGoal(this, Player.class, 10f);
        this.goalSelector.addGoal(5, new RandomStrollGoal(this,1.0f,10,true){
            @Override
            public boolean canUse() {
                return Nymph.this.isTamed && super.canUse();
            }
        });


        this.goalSelector.addGoal(8, lookAtPlayerGoal);
//        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0){
//            @Override
//            public boolean canUse() {
//                return Nymph.this.isTamed || super.canUse() && isTrigger();
//            }
//        });

        this.targetSelector.addGoal(1,new AccelerateOnSeeingGoal(this,0.25f){
            @Override
            public boolean canUse() {
                return !Nymph.this.isTamed && super.canUse() && delayTime > 20;
            }
        });
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class,false, LivingEntity::canBeSeenAsEnemy){
            @Override
            public boolean canUse() {
                return super.canUse() && tickCount > 50;
            }
        });
    }

    public static class NymphLookAtPlayerGoal extends LookAtPlayerGoal {

        public NymphLookAtPlayerGoal(Mob mob, Class<? extends LivingEntity> lookAtType, float lookDistance) {
            super(mob, lookAtType, lookDistance, 1);
        }

        private Entity getLook(){
            return lookAt;
        }

    }

    protected Entity getLook(){
        return lookAtPlayerGoal == null? null : lookAtPlayerGoal.getLook();
    }


    public boolean isTrigger() {
        return this.entityData.get(DATA_TRIGGER);
    }

    public void setTrigger(boolean trigger) {
        this.entityData.set(DATA_TRIGGER, trigger);
    }

    @Override
    public void tick(){
        super.tick();

        if(!level().isClientSide){
            if(getTarget() != null && tickCount > 50){
                // 有目标且距离小于5时准备变异                if(distanceToSqr(getTarget()) < 25)
                    setTrigger(!isTamed);
            }
            if(!this.isTrigger()) {
                this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(5);
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25f);
                // 未变异时眼睛朝向
                if (getLook() == null && !isTamed) {
                    this.getLookControl().setLookAt(getEyePosition().add(getForward().scale(5)).add(0, -0.1f, 0));
                    delayTime = 0;

                } else {
                    if(!isTamed())
                        setYRot(getYHeadRot());
                }
                setSprinting(false);
            }else{
                this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(32);

                delayTime++;

                // 变异后恢复计数                if(getTarget() == null){
                    recoverTime++;
                    if(recoverTime > _recoverTime){
                        setTrigger(false);
                        recoverTime = 0;
                        delayTime = 0;
                    }
                }
            }
        }

        if (!this.level().isClientSide && this.isAlive() && this.isConverting()) {
            // 变异转化
            int i = this.getConversionProgress();
            this.conversionTime -= i;
            if (this.conversionTime <= 0 && ForgeEventFactory.canLivingConvert(this, MonstersEntities.NYMPH.get(), (timer) -> {
                this.conversionTime = timer;
            })) {
                this.finishConversion((ServerLevel)this.level());
            }
        }

    }

    private int getConversionProgress() {
        return 1;
    }

    private void finishConversion(ServerLevel serverLevel) {
        this.entityData.set(DATA_CONVERTING_ID, false);

        this.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
        if (!this.isSilent()) {
            serverLevel.levelEvent(null, 1027, this.blockPosition(), 0);
        }
        this.setTamed(true);
        setTrigger( false);
        ForgeEventFactory.onLivingConvert(this, this);
    }

    public void setTamed(boolean tamed) {
        this.isTamed = tamed;
        this.entityData.set(DATA_TAMED, tamed);
    }

    public boolean isTamed() {
        return isTamed;
    }

    public boolean isConverting() {
        return this.getEntityData().get(DATA_CONVERTING_ID);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.is(Items.GOLDEN_APPLE)) {
            if (this.hasEffect(MobEffects.WEAKNESS) && !this.isTamed) {
                itemstack.consume(1, player);
                if (!this.level().isClientSide) {
                    this.startConverting(player.getUUID(), initConversionTime());
                }

                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.CONSUME;
            }
        } else {
            return super.mobInteract(player, hand);
        }
    }

    protected int initConversionTime() {
        return this.random.nextInt(500) + 2000;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        this.entityData.set(DATA_TAMED, false, true);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    private void startConverting(@Nullable UUID conversionStarter, int villagerConversionTime) {
        this.conversionStarter = conversionStarter;
        this.conversionTime = villagerConversionTime;
        this.getEntityData().set(DATA_CONVERTING_ID, true);
        this.removeEffect(MobEffects.WEAKNESS);
        this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, villagerConversionTime, Math.min(this.level().getDifficulty().getId() - 1, 0)));
        this.level().broadcastEntityEvent(this, (byte)16);
    }


    RawAnimation sit = RawAnimation.begin().thenLoop("sit");
    RawAnimation dash = RawAnimation.begin().thenLoop("dash");

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this, "controller", 10, state->{
            if(isTamed){

                return state.setAndContinue(state.isMoving() ? DefaultAnimations.WALK : DefaultAnimations.IDLE);
            }
            if(!isTrigger()){
                return state.setAndContinue(sit);
            }
            return state.setAndContinue(dash);
        }));
    }

    @Override
    public float getScale() {
        return super.getScale();
    }


    @Override
    public boolean isPreventingPlayerRest(Player player) {
        return !isTamed;
    }

    @Override
    public boolean canBeLeashed() {
        return isTamed;
    }

    @Override
    public boolean canBeSeenAsEnemy() {
        return super.canBeSeenAsEnemy() && !isTamed;
    }

    protected Vec3 getLeashOffset() {
        return new Vec3(-0.3, this.getEyeHeight() * 0.5f, this.getBbWidth() * 0.1F);
    }
}
