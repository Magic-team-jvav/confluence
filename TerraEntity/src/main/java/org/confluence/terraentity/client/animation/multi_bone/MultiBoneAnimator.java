package org.confluence.terraentity.client.animation.multi_bone;

import org.confluence.terraentity.client.animation.api.animator.AbstractAnimator;
import org.confluence.terraentity.client.animation.api.context.AnimatorContext;
import org.confluence.terraentity.entity.animation.BoneStates;
import org.confluence.terraentity.entity.animation.MultiBone;
import org.confluence.terraentity.entity.animation.MultiBoneStateMachine;

public abstract class MultiBoneAnimator<T> extends AbstractAnimator<T, MultiBone, AnimatorContext, BoneStates, MultiBoneStateMachine<BoneStates>, MultiBoneState<T>> {


    public MultiBoneAnimator(MultiBone bone) {
        super(bone);
    }
}
