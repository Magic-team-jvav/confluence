package org.confluence.mod.api.client.animation;

import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.Objects;

/// 一个动画名称及其 GeckoLib 循环方式。
public record HandAnimationClip(String animation, Animation.LoopType loopType) {
    public HandAnimationClip {
        if (animation == null || animation.isBlank()) {
            throw new IllegalArgumentException("Animation name cannot be blank");
        }
        loopType = Objects.requireNonNull(loopType, "Animation loop type must not be null");
    }

    public static HandAnimationClip playOnce(String animation) {
        return new HandAnimationClip(animation, Animation.LoopType.PLAY_ONCE);
    }

    public static HandAnimationClip loop(String animation) {
        return new HandAnimationClip(animation, Animation.LoopType.LOOP);
    }

    public RawAnimation rawAnimation() {
        return RawAnimation.begin().then(animation, loopType);
    }
}
