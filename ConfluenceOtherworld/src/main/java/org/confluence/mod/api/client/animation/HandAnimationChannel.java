package org.confluence.mod.api.client.animation;

import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class HandAnimationChannel {
    private final String name;
    private final HandAnimationClip idle;
    private final Map<HandAnimationAction, HandAnimationClip> animations;

    private HandAnimationChannel(Builder builder) {
        this.name = builder.name;
        this.idle = builder.idle;
        this.animations = Map.copyOf(builder.animations);
    }

    public String name() {
        return name;
    }

    public Optional<HandAnimationClip> idle() {
        return Optional.ofNullable(idle);
    }

    public Map<HandAnimationAction, HandAnimationClip> animations() {
        return animations;
    }

    public Optional<HandAnimationClip> clip(HandAnimationAction action) {
        return Optional.ofNullable(animations.get(Objects.requireNonNull(action, "Animation action must not be null")));
    }

    /// Build the raw animation used by GeckoLib's trigger system.
    ///
    /// <p>GeckoLib does not evaluate the controller's new idle animation until
    /// the next animation tick after a play-once stage stops. That leaves one
    /// tick in which its bone reset pass can restore the model's initial
    /// snapshot. Keep the hand-off in the same raw-animation queue so the
    /// next stage is evaluated during the completion tick itself.</p>
    public RawAnimation triggeredAnimation(HandAnimationAction action) {
        HandAnimationClip clip = animations.get(Objects.requireNonNull(action, "Animation action must not be null"));
        if (clip == null) {
            throw new IllegalArgumentException("No animation " + action + " configured for channel " + name);
        }

        RawAnimation animation = clip.rawAnimation();
        if (clip.loopType() == Animation.LoopType.PLAY_ONCE) {
            if (idle != null) {
                animation.thenLoop(idle.animation());
            } else {
                animation.thenWait(1);
            }
        }
        return animation;
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public static final class Builder {
        private final String name;
        private final EnumMap<HandAnimationAction, HandAnimationClip> animations = new EnumMap<>(HandAnimationAction.class);
        private HandAnimationClip idle;

        private Builder(String name) {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Animation channel name cannot be blank");
            }
            this.name = name;
        }

        public Builder idle(String animation) {
            return idle(HandAnimationClip.loop(animation));
        }

        public Builder idle(HandAnimationClip animation) {
            this.idle = Objects.requireNonNull(animation, "Idle animation must not be null");
            return this;
        }

        public Builder animation(HandAnimationAction action, String animation) {
            return animation(action, new HandAnimationClip(animation, Animation.LoopType.DEFAULT));
        }

        public Builder animation(HandAnimationAction action, String animation, Animation.LoopType loopType) {
            return animation(action, new HandAnimationClip(animation, loopType));
        }

        public Builder animation(HandAnimationAction action, HandAnimationClip animation) {
            action = Objects.requireNonNull(action, "Animation action must not be null");
            if (action == HandAnimationAction.IDLE) {
                throw new IllegalArgumentException("IDLE must be configured with idle()");
            }
            animations.put(action, Objects.requireNonNull(animation, "Animation clip must not be null"));
            return this;
        }

        public HandAnimationChannel build() {
            return new HandAnimationChannel(this);
        }
    }
}
