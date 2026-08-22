package org.confluence.mod.api.client.animation;

import software.bernie.geckolib.core.animation.Animation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record HandAnimationProfile(List<HandAnimationChannel> channels) {
    public HandAnimationProfile(Builder channels) {
        this(validateAndCopy(channels));
    }

    private static List<HandAnimationChannel> validateAndCopy(Builder channels) {
        if (channels.channels.isEmpty()) {
            throw new IllegalArgumentException("An animation profile needs at least one channel");
        }
        return List.copyOf(channels.channels);
    }

    public boolean isAnimation(HandAnimationAction action, String animationName) {
        if (animationName == null) {
            return false;
        }
        return channels.stream().flatMap(channel -> channel.clip(action).stream()).anyMatch(clip -> clip.animation().equals(animationName));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static HandAnimationProfile legacy() {
        return builder()
                .channel(HandAnimationChannel.builder("gun")
                        .animation(HandAnimationAction.SHOOT, "fire")
                        .animation(HandAnimationAction.DRAW, "pick up")
                        .animation(HandAnimationAction.RELOAD, "reloading")
                        .build())
                .build();
    }

    public static HandAnimationProfile handgun() {
        return builder()
                .channel(HandAnimationChannel.builder("hand_pose")
                        .idle("static_idle")
                        .animation(HandAnimationAction.DRAW, "draw", Animation.LoopType.PLAY_ONCE)
                        .animation(HandAnimationAction.PUT_AWAY, "put_away", Animation.LoopType.PLAY_ONCE)
                        .animation(HandAnimationAction.INSPECT, "inspect", Animation.LoopType.PLAY_ONCE)
                        .build())
                .channel(HandAnimationChannel.builder("weapon_action")
                        .animation(HandAnimationAction.SHOOT, "shoot", Animation.LoopType.PLAY_ONCE)
                        .build())
                .build();
    }

    public static final class Builder {
        private final List<HandAnimationChannel> channels = new ArrayList<>();

        public Builder channel(HandAnimationChannel channel) {
            HandAnimationChannel candidate = Objects.requireNonNull(channel, "Animation channel must not be null");
            if (channels.stream().anyMatch(existing -> existing.name().equals(candidate.name()))) {
                throw new IllegalArgumentException("Duplicate animation channel: " + candidate.name());
            }
            channels.add(candidate);
            return this;
        }

        public HandAnimationProfile build() {
            return new HandAnimationProfile(this);
        }
    }
}
