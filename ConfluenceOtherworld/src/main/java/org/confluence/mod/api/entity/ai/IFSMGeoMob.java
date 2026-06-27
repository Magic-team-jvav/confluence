package org.confluence.mod.api.entity.ai;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import org.confluence.mod.util.entity.ai.fsm.CircleMobSkills;
import org.confluence.mod.util.entity.ai.fsm.MobSkill;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

/// 适配Geo动画的状态机接口
public interface IFSMGeoMob extends GeoEntity {

    CircleMobSkills getSkills();

    ClientBoundAnimationMessage getAnimationMessage();

    void addSkills();

    default void addSkill(MobSkill mobSkill) {
        getSkills().pushSkill(mobSkill);
    }

    default void syncSkills(EntityDataAccessor<?> key) {
        CircleMobSkills skills = getSkills();
        Entity self = (Entity) this;
        if(self.level().isClientSide() && skills!= null && key == skills.skillIndexData){
            skills.index = self.getEntityData().get(skills.skillIndexData);
            getAnimationMessage().lastSkillTick = self.tickCount;
            skills.tick = 0;
        }
    }

    default AnimationController<GeoAnimatable> fsmAnimationController() {
        return new AnimationController<>(this, getTransitionTick(), state -> {
            Entity entity =  state.getData(DataTickets.ENTITY);
            if (!entity.isAlive()) return PlayState.STOP;
            if (getSkills().count() == 0) return PlayState.STOP;

            RawAnimation pose = getSkills().getCurAnim();
            if(pose == null) return PlayState.STOP;
            state.setAnimation(pose);
            if (getAnimationMessage().lastAnimIndex != getSkills().index) {
                getAnimationMessage().lastAnimIndex = getSkills().index;
                state.resetCurrentAnimation();

                return PlayState.STOP;
            }
            return PlayState.CONTINUE;
        });
    }

    default int getTransitionTick(){
        return 5;
    }

    default void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(fsmAnimationController());
    }



    class ClientBoundAnimationMessage {
        public int lastAnimIndex;
        public int lastSkillTick;
    }
}
