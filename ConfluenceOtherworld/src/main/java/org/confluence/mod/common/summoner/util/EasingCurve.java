package org.confluence.mod.common.summoner.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 缓动曲线工具类，提供 0~1 → 0~1 的非线性映射。
 * <p>
 * 过 (0,0) 和 (1,1) 点，用于动画进度重映射。支持预置缓动类型和贝塞尔控制点链式构建。
 * </p>
 *
 * <h3>预置缓动</h3>
 * <pre>
 * float t = EasingCurve.EASE_OUT_QUAD.apply(0.5f); // ≈ 0.75
 * </pre>
 *
 * <h3>贝塞尔曲线</h3>
 * <pre>
 * // 仅指定 Y，X 自动均分
 * EasingCurve ease = EasingCurve.bezier()
 *     .control(0.1f)
 *     .control(1.0f)
 *     .build();
 *
 * // 指定 X 和 Y，X 必须单调递增
 * EasingCurve ease = EasingCurve.bezier()
 *     .control(0.25f, 0.1f)
 *     .control(0.75f, 1.0f)
 *     .build();
 * </pre>
 */
@FunctionalInterface
public interface EasingCurve {

    /**
     * 将线性进度映射为缓动进度。
     *
     * @param progress 线性进度 [0, 1]
     * @return 缓动后的进度 [0, 1]
     */
    float apply(float progress);

    // ===================== 预置缓动 =====================

    /** 线性，无缓动 */
    EasingCurve LINEAR = t -> t;

    /** 二次方缓入：慢启动，逐渐加速 */
    EasingCurve EASE_IN_QUAD = t -> t * t;
    /** 二次方缓出：快启动，逐渐减速 */
    EasingCurve EASE_OUT_QUAD = t -> t * (2 - t);
    /** 二次方缓入缓出：慢启动和结束，中间加速 */
    EasingCurve EASE_IN_OUT_QUAD = t -> t < 0.5f ? 2 * t * t : -1 + (4 - 2 * t) * t;

    /** 三次方缓入：慢启动，加速更明显 */
    EasingCurve EASE_IN_CUBIC = t -> t * t * t;
    /** 三次方缓出：快启动，减速更明显 */
    EasingCurve EASE_OUT_CUBIC = t -> { float t1 = t - 1; return t1 * t1 * t1 + 1; };
    /** 三次方缓入缓出：更显著的慢启动和结束 */
    EasingCurve EASE_IN_OUT_CUBIC = t -> t < 0.5f ? 4 * t * t * t : (t - 1) * (2 * t - 2) * (2 * t - 2) + 1;

    /** 回弹缓入：启动前先向反方向回拉 */
    EasingCurve EASE_IN_BACK = t -> { float s = 1.70158f; return t * t * ((s + 1) * t - s); };
    /** 回弹缓出：结束前越过目标再回弹 */
    EasingCurve EASE_OUT_BACK = t -> {
        float s = 1.70158f;
        float t1 = t - 1;
        return t1 * t1 * ((s + 1) * t1 + s) + 1;
    };
    /** 回弹缓入缓出：两端均有回拉/回弹效果 */
    EasingCurve EASE_IN_OUT_BACK = t -> {
        float s = 1.70158f * 1.525f;
        if (t < 0.5f) {
            float t2 = 2 * t;
            return 0.5f * (t2 * t2 * ((s + 1) * t2 - s));
        } else {
            float t2 = 2 * t - 2;
            return 0.5f * (t2 * t2 * ((s + 1) * t2 + s) + 2);
        }
    };

    /** 弹跳缓出：到达终点前多次弹跳，模拟落体弹跳 */
    EasingCurve EASE_OUT_BOUNCE = t -> {
        float n = 7.5625f, d = 2.75f;
        if (t < 1 / d) return n * t * t;
        if (t < 2 / d) { float t1 = t - 1.5f / d; return n * t1 * t1 + 0.75f; }
        if (t < 2.5 / d) { float t1 = t - 2.25f / d; return n * t1 * t1 + 0.9375f; }
        float t1 = t - 2.625f / d;
        return n * t1 * t1 + 0.984375f;
    };

    /** 弹性缓出：到达终点前弹性振荡，模拟弹簧效果 */

    EasingCurve EASE_OUT_ELASTIC = t -> {
        if (t == 0 || t == 1) return t;
        return (float) Math.pow(2, -10 * t) * (float) Math.sin((t - 0.075f) * (2 * Math.PI) / 0.3f) + 1;
    };

    // ===================== 组合方法 =====================

    /**
     * 将此缓动的输出作为另一个缓动的输入（嵌套组合）。
     * <p>
     * 结果：outer(this(t))
     * </p>
     */
    default EasingCurve compose(EasingCurve outer) {
        return t -> outer.apply(this.apply(t));
    }

    /**
     * 将另一个缓动的输出作为此缓动的输入（串联组合）。
     * <p>
     * 结果：this(after(t))
     * </p>
     */
    default EasingCurve andThen(EasingCurve after) {
        return t -> this.apply(after.apply(t));
    }

    // ===================== 贝塞尔构建器 =====================

    /**
     * 创建贝塞尔曲线构建器。
     * <p>
     * 自动在首尾插入 (0,0) 和 (1,1)，用户只需添加中间控制点。
     * 控制点 X 必须单调递增（构建时自动排序），Y 允许任意值（支持过冲/回弹）。
     * </p>
     */
    static BezierBuilder bezier() {
        return new BezierBuilder();
    }

    /**
     * 贝塞尔缓动曲线构建器。
     * <p>
     * 使用 De Casteljau 算法求值。由于控制点 X 单调递增，贝塞尔曲线的 X 分量也单调，
     * 可直接通过查找表线性扫描从 progress(x) 反查 y，无需二分搜索。
     * </p>
     */
    final class BezierBuilder {

        private static final int TABLE_SIZE = 256;

        private final List<Float> controlY = new ArrayList<>();
        private boolean uniformX = true;
        private final List<Float> explicitX = new ArrayList<>();

        BezierBuilder() {
        }

        /**
         * 添加贝塞尔控制点，X 自动均分。
         * <p>
         * 控制点沿 X 轴均匀分布，用户只需指定 Y 值。
         * 例如 2 个控制点时 X 分别为 1/3 和 2/3。
         * Y 值允许超出 [0,1]，产生过冲/回弹效果。
         * </p>
         *
         * @param y 控制点 Y 坐标
         * @return 自身，支持链式调用
         */
        public BezierBuilder control(float y) {
            uniformX = true;
            controlY.add(y);
            return this;
        }

        /**
         * 添加贝塞尔控制点，指定 X 和 Y。
         * <p>
         * X 值必须大于已添加的所有控制点的 X（单调递增），否则抛出异常。
         * Y 值允许超出 [0,1]，产生过冲/回弹效果。
         * </p>
         *
         * @param x 控制点 X 坐标，必须单调递增且在 (0,1) 范围内
         * @param y 控制点 Y 坐标
         * @return 自身，支持链式调用
         * @throws IllegalArgumentException 如果 X 不满足单调递增
         */
        public BezierBuilder control(float x, float y) {
            uniformX = false;
            if (!explicitX.isEmpty() && x <= explicitX.get(explicitX.size() - 1)) {
                throw new IllegalArgumentException(
                        "控制点 X 必须单调递增：当前 x=" + x + "，前一个 x=" + explicitX.get(explicitX.size() - 1));
            }
            explicitX.add(x);
            controlY.add(y);
            return this;
        }

        /**
         * 构建 EasingCurve 实例。
         * <p>
         * 自动在首尾插入 (0,0) 和 (1,1)，构建查找表后返回。
         * 无控制点时返回 LINEAR。
         * </p>
         *
         * @return 缓动曲线
         */
        public EasingCurve build() {
            if (controlY.isEmpty()) {
                return LINEAR;
            }

            int n = controlY.size() + 2;
            float[] px = new float[n];
            float[] py = new float[n];
            px[0] = 0;
            py[0] = 0;
            if (uniformX) {
                for (int i = 0; i < controlY.size(); i++) {
                    px[i + 1] = (float) (i + 1) / (controlY.size() + 1);
                    py[i + 1] = controlY.get(i);
                }
            } else {
                for (int i = 0; i < controlY.size(); i++) {
                    px[i + 1] = explicitX.get(i);
                    py[i + 1] = controlY.get(i);
                }
            }
            px[n - 1] = 1;
            py[n - 1] = 1;

            float[] tableX = new float[TABLE_SIZE + 1];
            float[] tableY = new float[TABLE_SIZE + 1];
            for (int i = 0; i <= TABLE_SIZE; i++) {
                float t = (float) i / TABLE_SIZE;
                float[] point = deCasteljau(px, py, t);
                tableX[i] = point[0];
                tableY[i] = point[1];
            }

            return progress -> {
                if (progress <= 0) return 0;
                if (progress >= 1) return 1;
                return lookupY(tableX, tableY, progress);
            };
        }

        /**
         * De Casteljau 算法求贝塞尔曲线上的点。
         */
        private static float[] deCasteljau(float[] px, float[] py, float t) {
            int n = px.length;
            float[] x = px.clone();
            float[] y = py.clone();
            float invT = 1 - t;
            for (int k = n - 1; k > 0; k--) {
                for (int i = 0; i < k; i++) {
                    x[i] = x[i] * invT + x[i + 1] * t;
                    y[i] = y[i] * invT + y[i + 1] * t;
                }
            }
            return new float[]{x[0], y[0]};
        }

        /**
         * 在查找表中根据 x 值查找对应的 y 值。
         * <p>
         * 控制点 X 单调递增，贝塞尔曲线 X 分量也单调，查找表天然有序，线性扫描即可。
         * </p>
         */
        private static float lookupY(float[] tableX, float[] tableY, float x) {
            for (int i = 1; i < tableX.length; i++) {
                if (tableX[i] >= x) {
                    float x0 = tableX[i - 1], x1 = tableX[i];
                    float y0 = tableY[i - 1], y1 = tableY[i];
                    float denom = x1 - x0;
                    if (denom < 1e-8f) return (y0 + y1) * 0.5f;
                    float t = (x - x0) / denom;
                    return y0 + t * (y1 - y0);
                }
            }
            return tableY[tableY.length - 1];
        }
    }
}
