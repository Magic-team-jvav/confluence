package org.confluence.terraentity.client.animation.multi_bone;

import org.confluence.terraentity.client.animation.api.context.AnimatorContext;
import org.confluence.terraentity.client.animation.api.state.BoneState;
import org.confluence.terraentity.entity.animation.BoneStates;
import org.confluence.terraentity.entity.animation.MultiBone;
import org.confluence.terraentity.entity.animation.MultiBoneStateMachine;

public interface MultiBoneState<T> extends BoneState<T, MultiBone, AnimatorContext, BoneStates, MultiBoneStateMachine<BoneStates>> {
}
