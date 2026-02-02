package org.confluence.terraentity.client.entity.model;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.terraentity.api.entity.animation.IUseItemAnimatable;
import org.confluence.terraentity.client.animation.api.context.AnimatorContext;
import org.confluence.terraentity.client.animation.bone.GeoBoneAnimator;
import org.confluence.terraentity.client.animation.bone.animator.humanoid.LeftHandGeoBoneAnimator;
import org.confluence.terraentity.client.animation.bone.animator.humanoid.RightHandGeoBoneAnimator;
import org.confluence.terraentity.entity.animation.BoneStateMachine;
import org.confluence.terraentity.entity.animation.BoneStates;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;

import static org.confluence.terraentity.client.entity.layer.ArmorLayer.*;

/**
 * 人形怪的Geo模型
 * @param <T>
 */
public class GeoHumanoidModel<T extends LivingEntity & GeoEntity & IUseItemAnimatable<BoneStates>> extends AnimatorModel<T>{

    protected GeoBoneAnimator<T> rightArmAnimator;
    protected GeoBoneAnimator<T> leftArmAnimator;

    public GeoHumanoidModel(ResourceLocation path) {
        super(path);
    }

    public GeoHumanoidModel(ResourceLocation path, boolean turnsHead) {
        super(path, turnsHead);

    }

    @Override
    public void customAnimations(T animatable, long instanceId, AnimationState<T> animationState, float partialTick) {
        float usingTime = animatable.getTicksUsingItem() + partialTick;

        AnimatorContext context = new AnimatorContext(usingTime);
        if(rightArmAnimator != null) {
            handleBone(animatable.getRightArmBoneStateMachine(), animatable, rightArmAnimator, partialTick, context);
        }
        if(leftArmAnimator!= null) {
            handleBone(animatable.getLeftArmBoneStateMachine(), animatable, leftArmAnimator, partialTick, context);
        }
        if(animatable.getVehicle() != null) {
            this.getBone(LEFT_ARMOR_LEG).ifPresent(bone -> {
                bone.setRotX(1.5707963F);
            });
            this.getBone(RIGHT_ARMOR_LEG).ifPresent(bone -> {
                bone.setRotX(1.5707963F);
            });
        }
    }

    @Override
    public void initBoneAnimators(T animatable, BakedGeoModel model) {
        if(rightArmAnimator == null) {
            model.topLevelBones().stream().filter(b -> b.getName().equals(RIGHT_HAND)).findFirst().ifPresentOrElse(b -> {
                rightArmAnimator = new RightHandGeoBoneAnimator<>(b);
            }, () -> {
                GeoBone bone = model.searchForChildBone(model.topLevelBones().get(0), RIGHT_HAND);
                if(bone != null){
                    rightArmAnimator = new RightHandGeoBoneAnimator<>(bone);
                }
            });
        }
        if(leftArmAnimator == null) {
            model.topLevelBones().stream().filter(b->b.getName().equals(LEFT_HAND)).findFirst().ifPresentOrElse(b->{
                    leftArmAnimator = new LeftHandGeoBoneAnimator<>(b);
            }, ()->{
                GeoBone bone = model.searchForChildBone(model.topLevelBones().get(0), LEFT_HAND);
                if(bone != null){
                    leftArmAnimator = new LeftHandGeoBoneAnimator<>(bone);
                }
            });
        }
    }

    private void handleBone(BoneStateMachine<BoneStates> stateMachine, T animatable,
                            GeoBoneAnimator<T> animator, float partialTick, AnimatorContext context) {
        animator.updateState(stateMachine, animatable, partialTick, context);
        stateMachine.apply(partialTick, animator.getBone());
    }

}
