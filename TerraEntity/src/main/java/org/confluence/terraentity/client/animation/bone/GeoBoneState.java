package org.confluence.terraentity.client.animation.bone;

import org.confluence.terraentity.client.animation.api.context.AnimatorContext;
import org.confluence.terraentity.client.animation.api.state.BoneState;
import org.confluence.terraentity.entity.animation.BoneStateMachine;
import org.confluence.terraentity.entity.animation.BoneStates;
import software.bernie.geckolib.cache.object.GeoBone;

public interface GeoBoneState <T> extends BoneState<T, GeoBone, AnimatorContext,BoneStates,  BoneStateMachine<BoneStates>> {
}
