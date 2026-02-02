package org.confluence.terraentity.client.animation.multi_bone.animator;

import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.client.animation.api.context.AnimatorContext;
import org.confluence.terraentity.client.animation.multi_bone.MultiBoneAnimator;
import org.confluence.terraentity.client.animation.multi_bone.MultiBoneState;
import org.confluence.terraentity.entity.animation.BoneStates;
import org.confluence.terraentity.entity.animation.MultiBone;
import org.confluence.terraentity.entity.animation.MultiBoneStateMachine;
import org.confluence.terraentity.entity.boss.SkeletronHand;

import java.util.EnumMap;

public class SkeletronAnimator<T extends SkeletronHand> extends MultiBoneAnimator<T> {

    public SkeletronAnimator(MultiBone bone) {
        super(bone);
    }

    @Override
    protected void init() {
        this.states = new EnumMap<>(BoneStates.class);
        addState(BoneStates.IDLE, new Follow());
        addState(BoneStates.PROJECTILE_USING, new Forward());
    }

    @Override
    protected BoneStates defaultState() {
        return BoneStates.IDLE;
    }

    class Follow implements MultiBoneState<T>{

        @Override
        public boolean shouldTransition(MultiBoneStateMachine<BoneStates> state, T animatable, AnimatorContext context) {
            Vec3 selfPos = animatable.position();
            Vec3 rootPos = animatable.getRootPos();
            boolean flag = selfPos.y < rootPos.y - 2;
            return !flag;
//            return state.hand.partialTickTotal > 100;
        }

        @Override
        public void transitionState(MultiBoneStateMachine<BoneStates> state, T animatable, float partialTick, MultiBone bone, AnimatorContext context) {
            state.setState(BoneStates.PROJECTILE_USING);
        }

        @Override
        public void updateTransition(MultiBoneStateMachine<BoneStates> state, T animatable, float partialTick, MultiBone bone, AnimatorContext context) {

            Vec3 selfPos = animatable.position();
            Vec3 rootPos = animatable.getRootPos();
            float scale = 1;
            double a = Math.min(rootPos.distanceTo(selfPos), 10.4 * scale);
            double b = 5.2 * scale;
            double c = 5.2 * scale;
            double radC = Math.acos((a * a + b * b - c * c) / (2 * a * b)); // 余弦定理
            double armRot = radC;
            bone.arm1.setRotZ((float) armRot);
            bone.hand.setRotZ((float) armRot);
            double radA = Math.PI - Math.acos((b * b + c * c - a * a) / (2 * b * c));
            bone.arm2.setRotZ((float) (selfPos.y < rootPos.y - 1 ? -radA : radA));
            state.hand.updateState(40, bone.hand.getRotX(), bone.hand.getRotY(), bone.hand.getRotZ());
            state.arm1.updateState(40, bone.arm1.getRotX(), bone.arm1.getRotY(), bone.arm1.getRotZ());
            state.arm2.updateState(40, bone.arm2.getRotX(), bone.arm2.getRotY(), bone.arm2.getRotZ());
        }
    }

    class Forward implements MultiBoneState<T>{

        @Override
        public boolean shouldTransition(MultiBoneStateMachine<BoneStates> state, T animatable, AnimatorContext context) {
            Vec3 selfPos = animatable.position();
            Vec3 rootPos = animatable.getRootPos();
            return selfPos.y < rootPos.y - 2;
        }

        @Override
        public void transitionState(MultiBoneStateMachine<BoneStates> state, T animatable, float partialTick, MultiBone bone, AnimatorContext context) {
            state.setState(BoneStates.IDLE);
        }

        @Override
        public void updateTransition(MultiBoneStateMachine<BoneStates> state, T animatable, float partialTick, MultiBone bone, AnimatorContext context) {

            Vec3 selfPos = animatable.position();
            Vec3 rootPos = animatable.getRootPos();
            float scale = 1;
            double a = Math.min(rootPos.distanceTo(selfPos), 10.4 * scale);
            double b = 5.2 * scale;
            double c = 5.2 * scale;
            double radC = Math.acos((a * a + b * b - c * c) / (2 * a * b)); // 余弦定理
            double armRot =  -radC;
            bone.arm1.setRotZ((float) armRot);
            bone.hand.setRotZ((float) armRot);
            double radA = Math.PI - Math.acos((b * b + c * c - a * a) / (2 * b * c));
            bone.arm2.setRotZ((float) (selfPos.y < rootPos.y - 1 ? -radA : radA));
            state.hand.updateState(40, bone.hand.getRotX(), bone.hand.getRotY(), bone.hand.getRotZ());
            state.arm1.updateState(40, bone.arm1.getRotX(), bone.arm1.getRotY(), bone.arm1.getRotZ());
            state.arm2.updateState(40, bone.arm2.getRotX(), bone.arm2.getRotY(), bone.arm2.getRotZ());

        }
    }
}
