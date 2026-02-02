package org.confluence.terraentity.entity.ai.keyframe.animation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.confluence.terraentity.api.entity.animation.IKeyframeAnimation;
import org.confluence.terraentity.api.entity.animation.IKeyframeBaker;
import org.confluence.terraentity.entity.ai.keyframe.Keyframe;
import org.confluence.terraentity.entity.ai.keyframe.baker.BakerEnum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p> 1D 单值关键帧插值管理类
 * <p> 将一组关键帧的每个区间进行插值
 */
public class KeyframeAnimation implements IKeyframeAnimation<Double> {

    public List<Keyframe> keyframes;
    private final double length;
    List<IKeyframeBaker> interpolators;
    private final float endTime;

    public static Codec<KeyframeAnimation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Keyframe.CODEC.listOf().fieldOf("keyframes").forGetter(KeyframeAnimation::getKeyframes)
    ).apply(instance, KeyframeAnimation::new));

    public static StreamCodec<ByteBuf, KeyframeAnimation> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    private List<Keyframe> getKeyframes() {
        return keyframes;
    }

    public KeyframeAnimation(List<Keyframe> keyframes) {
        if (keyframes == null || keyframes.size() < 2) {
            throw new IllegalArgumentException("Keyframes list must not be null and must contain at least 2 keyframes.");
        }
        // Sort keyframes by time
        this.keyframes = new ArrayList<>(keyframes);
        this.keyframes.sort(Comparator.comparingDouble(kf -> kf.time));

        interpolators = new ArrayList<>(this.keyframes.size() - 1);
        for (int i = 1; i < this.keyframes.size(); i++) {
            Keyframe kf1 = keyframes.get(i - 1);
            Keyframe kf2 = keyframes.get(i);
            if(kf1.isInterpolated || kf2.isInterpolated)
                interpolators.add(BakerEnum.PIECEWISE_BEZIER_SPLINE2.getBaker());
            else{
                interpolators.add(BakerEnum.LINER.getBaker());
            }
        }
        AtomicInteger ii = new AtomicInteger();
        interpolators.forEach(interpolator -> {
            interpolator.init(this.keyframes, ii.get()+1);
//            this.keyframes.addAll(interpolator.bakedKeyframes);
            ii.getAndIncrement();
        });
        this.length = keyframes.getLast().time;
        this.endTime = (float) keyframes.getLast().time;
    }

    /**
     * 计算 t 处的值
     * @param t 时间
     * @return t 处的值
     */
    public Double cal(double t){
        KeyframeInterval interval = findKeyframeInterval(t);
        if (interval.isBoundary()) {
            return interval.boundaryValue();
        }
        if(interval.insertPoint < 0) return keyframes.get(-interval.insertPoint - 1).value;
        int index = interval.insertPoint - 1;
        IKeyframeBaker interpolator = interpolators.get(index);
        return interpolator.calculate(t);
    }

    public double getEndTime(){
        return endTime;
    }

    @Override
    public double getLength() {
        return length;
    }

    // 辅助方法：查找关键帧区间
    private KeyframeInterval findKeyframeInterval(double t) {
        if (t <= keyframes.get(0).time) {
            return new KeyframeInterval(0, keyframes.get(0).value, true);
        }
        if (t >= keyframes.get(keyframes.size() - 1).time) {
            return new KeyframeInterval(keyframes.size() - 1, keyframes.get(keyframes.size() - 1).value, true);
        }

        // 使用二分查找找到 t 所在的区间
        int index = Collections.binarySearch(keyframes, new Keyframe(t, 0), Comparator.comparingDouble(kf -> kf.time));
        int insertPoint = -(index + 1);

        return new KeyframeInterval(insertPoint, 0, false);
    }

    // 辅助类：表示关键帧区间
    record KeyframeInterval(int insertPoint, double boundaryValue, boolean isBoundary) {
    }


    /**
     * 创建 Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder 类
     */
    public static class Builder {
        private final List<Keyframe> keyframes = new ArrayList<>();

        Builder(){
        }

        /**
         * 添加关键帧
         */
        public Builder addKeyframe(double time, double value) {
            keyframes.add(new Keyframe(time, value));
            return this;
        }

        /**
         * 添加关键帧
         */
        public Builder addKeyframe(Keyframe keyframe) {
            keyframes.add(keyframe);
            return this;
        }

        public KeyframeAnimation build() {
            return new KeyframeAnimation(keyframes);
        }
    }

}