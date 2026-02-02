package org.confluence.terraentity.entity.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.confluence.terraentity.entity.ai.keyframe.animation.KeyframeAnimation;

import java.util.Optional;

/**
 * 某些生物行为需要配合客户端动画硬编码实现，所以使用关键帧控制
 */
public class KeyframeAnimationCounter {
    float startTime;
    KeyframeAnimation animation;

    public static Codec<KeyframeAnimationCounter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("startTime").forGetter(ins-> Optional.of(ins.startTime)),
            KeyframeAnimation.CODEC.fieldOf("animation").forGetter(KeyframeAnimationCounter::getAnimation)
    ).apply(instance, (startTime, animation)-> startTime.map(aFloat ->
            new KeyframeAnimationCounter(aFloat, animation)).orElseGet(() -> new KeyframeAnimationCounter(animation))));

    public static StreamCodec<ByteBuf, KeyframeAnimationCounter> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    public float getStartTime() {
        return startTime;
    }

    public KeyframeAnimation getAnimation() {
        return animation;
    }

    public KeyframeAnimationCounter(KeyframeAnimation animation) {
        this.animation = animation;
    }

    public KeyframeAnimationCounter(float startTime, KeyframeAnimation animation) {
        this.startTime = startTime;
        this.animation = animation;
    }

    public double cal(int tick, float partialTick) {
        return animation.cal(tick + partialTick - startTime);
    }

    public void setStartTime(float startTime) {
        this.startTime = startTime;
    }

}
