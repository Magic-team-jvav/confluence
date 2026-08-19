package org.confluence.mod.api.client.animation;

import software.bernie.geckolib.core.animation.Animation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/// 枪械所使用的一组动画控制器声明。
public final class HandAnimationProfile {
    private final List<HandAnimationChannel> channels;
    private final boolean locatorTransforms;

    private HandAnimationProfile(Builder builder) {
        if (builder.channels.isEmpty()) {
            throw new IllegalArgumentException("An animation profile needs at least one channel");
        }
        this.channels = List.copyOf(builder.channels);
        this.locatorTransforms = builder.locatorTransforms;
    }

    public List<HandAnimationChannel> channels() {
        return channels;
    }

    public boolean usesLocatorTransforms() {
        return locatorTransforms;
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

    /// 保留旧枪械资源所使用的单控制器动画命名。
    public static HandAnimationProfile legacy() {
        return builder()
                .channel(HandAnimationChannel.builder("gun")
                        .animation(HandAnimationAction.SHOOT, "fire")
                        .animation(HandAnimationAction.DRAW, "pick up")
                        .animation(HandAnimationAction.RELOAD, "reloading")
                        .build())
                .build();
    }

    /// 1.21 TerraGuns 新手枪资源使用的手部、枪体双通道配置。
    public static HandAnimationProfile handgun() {
        return builder()
                .locatorTransforms()
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
        private boolean locatorTransforms;

        public Builder locatorTransforms() {
            locatorTransforms = true;
            return this;
        }

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
