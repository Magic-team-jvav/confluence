package org.confluence.terraentity.client.animation.bone.animator.humanoid;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import org.confluence.terraentity.api.entity.animation.IUseItemAnimatable;
import org.confluence.terraentity.client.animation.api.context.AnimatorContext;
import org.confluence.terraentity.client.animation.bone.GeoBoneAnimator;
import org.confluence.terraentity.client.animation.bone.GeoBoneState;
import org.confluence.terraentity.entity.animation.BoneStateMachine;
import org.confluence.terraentity.entity.animation.BoneStates;
import software.bernie.geckolib.cache.object.GeoBone;

import java.util.EnumMap;

import static org.confluence.terraentity.utils.TEUtils.lerpMotion;


/**
 * 人形怪右手骨骼动画控制器
 * @param <T> 实体类型
 */
public class RightHandGeoBoneAnimator<T extends LivingEntity & IUseItemAnimatable<?>> extends GeoBoneAnimator<T> {

    public RightHandGeoBoneAnimator(GeoBone bone) {
        super(bone);
    }

    @Override
    protected void init() {
        states = new EnumMap<>(BoneStates.class);
        addState(BoneStates.IDLE, new IdleState());
        addState(BoneStates.CROSSBOW_CHARGING, new CrossbowChargingState());
        addState(BoneStates.PROJECTILE_USING, new ProjectileUsingState());
        addState(BoneStates.SWINGING, new SwingingState());
        addState(BoneStates.CROSSBOW_IDLE, new CrossbowIdleState());
        addState(BoneStates.HAND_ITEM_IDLE, new HandItemIdleState());
    }

    // 方便打断点
    @Override
    public void updateState(BoneStateMachine<BoneStates> state, T animatable, float partialTick, AnimatorContext context) {
        GeoBoneState<T> currentState = this.states.get(state.getState());
        if (currentState != null) {
            currentState.handle(state, animatable, partialTick, bone, context);
        }else{
            state.setState(defaultState());
        }
    }

    @Override
    protected BoneStates defaultState() {
        return BoneStates.IDLE;
    }


    class SwingingState implements GeoBoneState<T> {
        @Override
        public void updateTransition(BoneStateMachine<BoneStates> state, T animatable, float partialTick, GeoBone bone, AnimatorContext context) {
            float swingTime = animatable.swingTime + partialTick;
            float swingTicks = animatable.getCurrentSwingDuration();
            double lerpx = lerpMotion(swingTime, swingTicks, 0, 1f);

            double f = lerpx * (1 - lerpx) * 4 * 1.3f;
            float half = swingTicks * 0.5f;
            if (swingTime > half) {
                f = lerpMotion(swingTime - half, half, f, bone.getRotX()); // rightArmRotX
            }

            double f2 = lerpx * (1 - lerpx) * 4;
            float half2 = swingTicks * 0.5f;
            if (swingTime > half2) {
                f2 = lerpMotion(swingTime - half2, half2, f, 0);
            }

            state.updateState(6, f, f2, bone.getRotZ());
        }

        @Override
        public boolean shouldTransition(BoneStateMachine<BoneStates> state,  T animatable, AnimatorContext context) {
            return animatable.swingTime <= 0;
        }

        @Override
        public void transitionState(BoneStateMachine<BoneStates> state, T animatable, float partialTick, GeoBone bone, AnimatorContext context) {
            state.setState(BoneStates.IDLE);
        }
    }

    class CrossbowChargingState implements GeoBoneState<T> {
        @Override
        public void updateTransition(BoneStateMachine<BoneStates> state, T animatable, float partialTick, GeoBone bone, AnimatorContext context) {
            state.updateState(5, 0.6f, 0.8f, bone.getRotZ());
        }

        @Override
        public boolean shouldTransition(BoneStateMachine<BoneStates> state,  T animatable, AnimatorContext context) {
            return !(animatable.getUseItem().getItem() instanceof CrossbowItem) ||
                    !animatable.isChargingCrossbow();
        }

        @Override
        public void transitionState(BoneStateMachine<BoneStates> state, T animatable, float partialTick, GeoBone bone, AnimatorContext context) {
            state.setState(BoneStates.PROJECTILE_USING);
        }
    }

    class CrossbowIdleState implements GeoBoneState<T> {
        @Override
        public void updateTransition(BoneStateMachine<BoneStates> state, T animatable, float partialTick, GeoBone bone, AnimatorContext context) {
            state.updateState(5, 0.3f + 0.5F * bone.getRotX(), bone.getRotY(), bone.getRotZ());
        }

        @Override
        public boolean shouldTransition(BoneStateMachine<BoneStates> state,  T animatable, AnimatorContext context) {
            return animatable.isChargingCrossbow() || !(animatable.getMainHandItem().getItem() instanceof CrossbowItem);
        }

        @Override
        public void transitionState(BoneStateMachine<BoneStates> state, T animatable, float partialTick, GeoBone bone, AnimatorContext context) {
            if (animatable.isChargingCrossbow()) {
                state.setState(BoneStates.CROSSBOW_CHARGING);
            } else if (animatable.isUsingItem() && animatable.getUseItem().getItem() instanceof CrossbowItem) {
                state.setState(BoneStates.PROJECTILE_USING);
            } else {
                state.setState(BoneStates.IDLE);
            }
        }
    }

    class HandItemIdleState implements GeoBoneState<T> {
        @Override
        public void updateTransition(BoneStateMachine<BoneStates> state, T animatable, float partialTick, GeoBone bone, AnimatorContext context) {
            state.updateState(15, 0.3f + bone.getRotX() * 0.5F, bone.getRotY(), bone.getRotZ());
        }

        @Override
        public boolean shouldTransition(BoneStateMachine<BoneStates> state,  T animatable, AnimatorContext context) {
            return animatable.getMainHandItem().isEmpty() || animatable.isUsingItem() || animatable.swingTime > 0;
        }

        @Override
        public void transitionState(BoneStateMachine<BoneStates> state, T animatable, float partialTick, GeoBone bone, AnimatorContext context) {
            if(animatable.swingTime > 0){
                state.setState(BoneStates.SWINGING);
            }else if(animatable.isUsingItem()){
                state.setState(BoneStates.PROJECTILE_USING);
            }else {
                state.setState(BoneStates.IDLE);
            }
        }
    }

    class IdleState implements GeoBoneState<T> {
        @Override
        public void updateTransition(BoneStateMachine<BoneStates> state, T animatable, float partialTick, GeoBone bone, AnimatorContext context) {
            state.updateState(5, bone.getRotX(), bone.getRotY(), bone.getRotZ());
        }

        @Override
        public boolean shouldTransition(BoneStateMachine<BoneStates> state,  T animatable, AnimatorContext context) {
            return animatable.isUsingItem() || !animatable.getMainHandItem().isEmpty() || animatable.swingTime > 0;
        }

        @Override
        public void transitionState(BoneStateMachine<BoneStates> state, T animatable, float partialTick, GeoBone bone, AnimatorContext context) {
            if(animatable.swingTime > 0){
                state.setState(BoneStates.SWINGING);
            } else if (animatable.isUsingItem()) {
                state.setState(BoneStates.PROJECTILE_USING);
            } else {
                state.setState(BoneStates.HAND_ITEM_IDLE);
            }
        }
    }

    class ProjectileUsingState implements GeoBoneState<T> {
        @Override
        public void updateTransition(BoneStateMachine<BoneStates> state, T animatable, float partialTick, GeoBone bone, AnimatorContext context) {
            double lerpx = lerpMotion(context.usingTime, 5, 0, 1.5 - Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot()) * 0.017453292F);
            float lerpy = Mth.lerp(partialTick, animatable.yBodyRotO - animatable.yHeadRotO, animatable.yBodyRot - animatable.yHeadRot) * 0.017453292F;
            state.updateState(5, lerpx, lerpy, bone.getRotZ());
        }

        @Override
        public boolean shouldTransition(BoneStateMachine<BoneStates> state,  T animatable, AnimatorContext context) {
            return !animatable.isUsingItem() || animatable.isChargingCrossbow();
        }

        @Override
        public void transitionState(BoneStateMachine<BoneStates> state, T animatable, float partialTick, GeoBone bone, AnimatorContext context) {
            if (animatable.isChargingCrossbow()) {
                state.setState(BoneStates.CROSSBOW_CHARGING);
            } else {
                state.setState(BoneStates.IDLE);
            }
        }
    }
}
