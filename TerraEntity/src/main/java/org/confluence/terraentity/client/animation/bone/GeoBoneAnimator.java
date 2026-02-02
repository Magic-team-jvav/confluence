package org.confluence.terraentity.client.animation.bone;

import org.confluence.terraentity.client.animation.api.animator.AbstractAnimator;
import org.confluence.terraentity.client.animation.api.context.AnimatorContext;
import org.confluence.terraentity.entity.animation.BoneStateMachine;
import org.confluence.terraentity.entity.animation.BoneStates;
import software.bernie.geckolib.cache.object.GeoBone;

/**
 * Geo骨骼硬编码动画控制器
 * @param <T> 实体类型
 */
public abstract class GeoBoneAnimator<T> extends AbstractAnimator<T, GeoBone, AnimatorContext,BoneStates, BoneStateMachine<BoneStates>, GeoBoneState<T>> {

    public GeoBoneAnimator(GeoBone bone) {
        super(bone);
    }
}
