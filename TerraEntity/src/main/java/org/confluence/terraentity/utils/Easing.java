package org.confluence.terraentity.utils;

import com.mojang.serialization.Codec;
import org.confluence.terraentity.data.codec.TECodecs;

import java.util.function.DoubleUnaryOperator;

/**
 * 缓动函数
 */
public enum Easing {

    /**
     * 线性缓动
     */
    LINEAR(t -> t),

    /**
     * 二次缓动
     */
    EASE_IN_QUAD(t -> t * t),
    EASE_OUT_QUAD(t -> 1 - (1 - t) * (1 - t)),
    EASE_IN_OUT_QUAD(t -> t < 0.5 ? 2 * t * t : 1 - Math.pow(-2 * t + 2, 2) / 2),

    /**
     * 三次缓动
     */
    EASE_IN_CUBIC(t -> t * t * t),
    EASE_OUT_CUBIC(t -> 1 - Math.pow(1 - t, 3)),
    EASE_IN_OUT_CUBIC(t -> t < 0.5 ? 4 * t * t * t : 1 - Math.pow(-2 * t + 2, 3) / 2),

    /**
     * 四次缓动
     */
    EASE_IN_QUART(t -> Math.pow(t, 4)),
    EASE_OUT_QUART(t -> 1 - Math.pow(1 - t, 4)),
    EASE_IN_OUT_QUART(t -> t < 0.5 ? 8 * Math.pow(t, 4) : 1 - Math.pow(-2 * t + 2, 4) / 2),

    /**
     * 正弦缓动
     */
    EASE_IN_SINE(t -> 1 - Math.cos((t * Math.PI) / 2)),
    EASE_OUT_SINE(t -> Math.sin((t * Math.PI) / 2)),
    EASE_IN_OUT_SINE(t -> -(Math.cos(Math.PI * t) - 1) / 2),

    /**
     * 指数缓动
     */
    EASE_IN_EXPO(t -> t == 0 ? 0 : Math.pow(2, 10 * t - 10)),
    EASE_OUT_EXPO(t -> t == 1 ? 1 : 1 - Math.pow(2, -10 * t)),
    EASE_IN_OUT_EXPO(t -> t == 0 || t == 1 ? t : t < 0.5 ? Math.pow(2, 20 * t - 10) / 2 : (2 - Math.pow(2, -20 * t + 10)) / 2),

    /**
     * 圆形缓动
     */
    EASE_IN_CIRC(t -> 1 - Math.sqrt(1 - Math.pow(t, 2))),
    EASE_OUT_CIRC(t -> Math.sqrt(1 - Math.pow(t - 1, 2))),
    EASE_IN_OUT_CIRC(t -> t < 0.5 ? (1 - Math.sqrt(1 - Math.pow(2 * t, 2))) / 2 : (Math.sqrt(1 - Math.pow(-2 * t + 2, 2)) + 1) / 2),

    /**
     * 弹性缓动
     */
    EASE_IN_ELASTIC(t -> {
        final double amplitude = 0.1;
        final double period = 0.4;
        if (t == 0 || t == 1) return t;
        double s = period / (2 * Math.PI) * Math.asin(1 / amplitude);
        return -(amplitude * Math.pow(2, 10 * t - 10) * Math.sin((t - 1 - s) * (2 * Math.PI) / period));
    }),
    
    EASE_OUT_ELASTIC(t -> {
        final double amplitude = 0.1;
        final double period = 0.4;
        if (t == 0 || t == 1) return t;
        double s = period / (2 * Math.PI) * Math.asin(1 / amplitude);
        return amplitude * Math.pow(2, -10 * t) * Math.sin((t - s) * (2 * Math.PI) / period) + 1;
    }),

    /**
     * 反弹缓动
     */
    EASE_OUT_BOUNCE(t -> {
        if (t < 1 / 2.75) {
            return 7.5625 * t * t;
        } else if (t < 2 / 2.75) {
            t -= 1.5 / 2.75;
            return 7.5625 * t * t + 0.75;
        } else if (t < 2.5 / 2.75) {
            t -= 2.25 / 2.75;
            return 7.5625 * t * t + 0.9375;
        } else {
            t -= 2.625 / 2.75;
            return 7.5625 * t * t + 0.984375;
        }
    }),
    
    EASE_IN_BOUNCE(t -> 1 - EASE_OUT_BOUNCE.ease(1 - t)),
    EASE_IN_OUT_BOUNCE(t -> t < 0.5 
        ? (1 - EASE_OUT_BOUNCE.ease(1 - 2 * t)) / 2 
        : (1 + EASE_OUT_BOUNCE.ease(2 * t - 1)) / 2);

    private final DoubleUnaryOperator easingFunction;

    Easing(DoubleUnaryOperator easingFunction) {
        this.easingFunction = easingFunction;
    }

    public static Codec<Easing> CODEC = TECodecs.createEnumCodec(Easing.class);

    /**
     * 应用缓动函数 (输入输出范围 0-1)
     * @param t 输入值 (0-1)
     * @return 缓动后的值 (0-1)
     */
    public double ease(double t) {
        // 确保输入在0-1范围内
        t = Math.max(0, Math.min(1, t));
        return easingFunction.applyAsDouble(t);
    }

    /**
     * 将输入值映射到指定范围后再应用缓动函数
     * @param value 输入值
     * @param inMin 输入范围最小值
     * @param inMax 输入范围最大值
     * @return 缓动后的值 (0-1)
     */
    public double ease(double value, double inMin, double inMax) {
        double t = mapTo01(value, inMin, inMax);
        return ease(t);
    }

    /**
     * 应用缓动函数并将结果映射到指定范围
     * @param t 输入值 (0-1)
     * @param outMin 输出范围最小值
     * @param outMax 输出范围最大值
     * @return 映射到指定范围的缓动值
     */
    public double easeToRange(double t, double outMin, double outMax) {
        double eased = ease(t);
        return mapFrom01(eased, outMin, outMax);
    }

    /**
     * 完整变换：映射输入范围->应用缓动->映射输出范围
     * @param value 输入值
     * @param inMin 输入范围最小值
     * @param inMax 输入范围最大值
     * @param outMin 输出范围最小值
     * @param outMax 输出范围最大值
     * @return 完整变换后的值
     */
    public double transform(double value, double inMin, double inMax, double outMin, double outMax) {
        double eased = ease(value, inMin, inMax);
        return mapFrom01(eased, outMin, outMax);
    }

    // ================= 静态辅助方法 =================
    
    /**
     * 将值从输入范围映射到0-1范围
     * @param value 输入值
     * @param inMin 输入范围最小值
     * @param inMax 输入范围最大值
     * @return 映射到0-1范围的值
     */
    public static double mapTo01(double value, double inMin, double inMax) {
        return (value - inMin) / (inMax - inMin);
    }

    /**
     * 将0-1范围的值映射到指定范围
     * @param t 0-1范围的值
     * @param outMin 输出范围最小值
     * @param outMax 输出范围最大值
     * @return 映射后的值
     */
    public static double mapFrom01(double t, double outMin, double outMax) {
        return outMin + t * (outMax - outMin);
    }

    /**
     * 反转缓动函数 (从结束到开始)
     * @param easing 原始缓动函数
     * @return 反转后的缓动函数
     */
    public static DoubleUnaryOperator reverse(DoubleUnaryOperator easing) {
        return t -> easing.applyAsDouble(1 - t);
    }

    /**
     * 创建自定义弹性缓动函数
     * @param amplitude 振幅 (建议 >1)
     * @param period 周期
     * @param in 是否为缓入效果
     * @return 自定义弹性缓动函数
     */
    public static DoubleUnaryOperator createElastic(double amplitude, double period, boolean in) {
        return t -> {
            if (t == 0 || t == 1) return t;
            
            double s = period / (2 * Math.PI) * Math.asin(1 / amplitude);
            if (in) {
                return -(amplitude * Math.pow(2, 10 * t - 10) * 
                        Math.sin((t - 1 - s) * (2 * Math.PI) / period));
            } else {
                return amplitude * Math.pow(2, -10 * t) * 
                       Math.sin((t - s) * (2 * Math.PI) / period) + 1;
            }
        };
    }


}