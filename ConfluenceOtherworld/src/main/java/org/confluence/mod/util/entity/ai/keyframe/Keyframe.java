package org.confluence.mod.util.entity.ai.keyframe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public class Keyframe {
    public double time;
    public double value;
    public double k0 = 0;
    public double k1 = 0;
    public double tension0 =0.5;
    public double tension1 =0.5;
    public boolean isInterpolated = false;

    public static Codec<Keyframe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("time").forGetter(ins->ins.time),
            Codec.DOUBLE.fieldOf("value").forGetter(ins->ins.value),
            Codec.DOUBLE.optionalFieldOf("k0").forGetter(ins->ins.isInterpolated? Optional.of(ins.k0) : Optional.empty()),
            Codec.DOUBLE.optionalFieldOf("t0").forGetter(ins->ins.isInterpolated? Optional.of(ins.tension0 ): Optional.empty()),
            Codec.DOUBLE.optionalFieldOf("k1").forGetter(ins->ins.isInterpolated? Optional.of(ins.k1): Optional.empty()),
            Codec.DOUBLE.optionalFieldOf("t1").forGetter(ins->ins.isInterpolated? Optional.of(ins.tension1):Optional.empty()),
            Codec.BOOL.optionalFieldOf("isInterpolated", false).forGetter(ins->ins.isInterpolated)
    ).apply(instance, (t, v, k0, t0, k1, t1, isInterpolated)->{
        if(isInterpolated) {
            return new Keyframe(t, v, k0.get(), t0.get(), k1.get(), t1.get());
        }
        return new Keyframe(t, v);
    }));

    public Keyframe setValue(double value){
        this.value = value;
        return this;
    }

    public Keyframe copy(){
        return new Keyframe(time, value, k0, tension0, k1, tension1);
    }

    public Keyframe(double time, double value) {
        this.time = time;
        this.value = value;
    }

    public Keyframe(double time, double value, double k, double tension) {
        this.time = time;
        this.value = value;
        this.k0 = k;
        this.k1 = k;
        this.tension0 = tension;
        this.tension1 = tension;
        this.isInterpolated = true;
    }

    public Keyframe(double time, double value, double k0, double tension0, double k1, double tension1) {
        this.time = time;
        this.value = value;
        this.k0 = k0;
        this.k1 = k1;
        this.tension0 = tension0;
        this.tension1 = tension1;
        this.isInterpolated = true;
    }

    public Keyframe setTime(double time){
        this.time = time;
        return this;
    }

    public String toString(){
        if(!isInterpolated){
            return "Keyframe{" +
                    "time=" + time +
                    ", value=" + value +
                    '}';
        }
        return "Keyframe{" +
                "time=" + time +
                ", value=" + value +
                ", k0=" + k0 +
                ", tension0=" + tension0 +
                ", k1=" + k1 +
                ", tension1=" + tension1 +
                ", isInterpolated=" + isInterpolated +
                '}';
    }

    public static KeyframeBuilder builder(double time, double value){
        return new KeyframeBuilder(time, value);
    }

    public static class KeyframeBuilder{
        private final double time;
        private double value;
        private double k0;
        private double tension1;
        private double k1;
        private double tension2;
        private boolean isInterpolated = false;

        /**
         * 用于多维度关键帧
         * @param time 时间
         */
        public KeyframeBuilder(double time){
            this.time = time;
        }

        public KeyframeBuilder(double time, double value){
            this.time = time;
            this.value = value;
        }

        /**
         * 设置两边的斜率
         * @param k 斜率
         * @param tension 张力
         */
        public KeyframeBuilder setK(double k, double tension){
            this.k0 = k;
            this.k1 = k;
            this.tension1 = tension;
            this.tension2 = tension;
            this.isInterpolated = true;
            return this;
        }

        /**
         * 设置左侧的斜率
         * @param k0 斜率
         * @param tension1 张力
         */
        public KeyframeBuilder setK0(double k0, double tension1){
            this.k0 = k0;
            this.tension1 = tension1;
            this.isInterpolated = true;
            return this;
        }

        /**
         * 设置右侧的斜率
         * @param k1 斜率
         * @param tension2 张力
         */
        public KeyframeBuilder setK1(double k1, double tension2){
            this.k1 = k1;
            this.tension2 = tension2;
            this.isInterpolated = true;
            return this;
        }

        public Keyframe build(){
            Keyframe keyframe = new Keyframe(time, value, k0, tension1, k1, tension2);
            keyframe.isInterpolated = isInterpolated;
            return keyframe;
        }
    }
}
