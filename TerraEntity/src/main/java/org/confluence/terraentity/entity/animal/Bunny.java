package org.confluence.terraentity.entity.animal;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.level.Level;
import org.confluence.terraentity.init.entity.TEAnimals;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

public class Bunny extends Rabbit implements GeoEntity {

    int idleTick = 0;
    int nextRandomWatch = 0;
    int watchCount = 20;
    int watchType = 0; // in client

    public Bunny(EntityType<? extends Bunny> entityType, Level level) {
        super(entityType, level);
        this.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(0.6f);
    }


    @Override
    public void tick() {
        super.tick();
        --this.watchCount;
        if(!level().isClientSide()) {
            if (!this.navigation.isDone()) {
                this.idleTick = 0;
            } else {
                if(++this.idleTick % 100 == 99){
                    this.nextRandomWatch = this.tickCount + this.random.nextInt(20);
                }
                if(tickCount == this.nextRandomWatch){
                    this.watchCount = 50 + 1000 * this.random.nextInt(2);
                    this.entityData.set(DATA_RANDOM_WATCH_COUNT, this.watchCount);
                }
            }

            if(this.watchCount >= 0){
                this.navigation.stop();
            }

        }

    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    static RawAnimation random_watch_1 = RawAnimation.begin().thenPlay("watch_1");
    static RawAnimation random_watch_2 = RawAnimation.begin().thenPlay("watch_2");

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Idle/Move", 5, state -> {

            if(this.watchCount > 0){
                if(this.watchType == 0){
                    return state.setAndContinue(random_watch_1);
                }else {
                    return state.setAndContinue(random_watch_2);
                }
            }
            return state.setAndContinue(state.isMoving() ? DefaultAnimations.WALK : DefaultAnimations.IDLE);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Nullable
    public Bunny getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return TEAnimals.BUNNY.get().create(level);
    }

    private static final EntityDataAccessor<Integer> DATA_RANDOM_WATCH_COUNT = SynchedEntityData.defineId(Bunny.class, EntityDataSerializers.INT);


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_RANDOM_WATCH_COUNT, 0);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if(key == DATA_RANDOM_WATCH_COUNT){
            this.watchCount = this.entityData.get(DATA_RANDOM_WATCH_COUNT);
            this.watchType = this.watchCount / 1000;
            this.watchCount %= 1000;
        }
    }


}
