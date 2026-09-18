package org.confluence.mod.client.summoner.model.geo;

import net.minecraft.util.Mth;

import java.util.List;

/**
 * 骨骼 keyframe 动画片段。
 * <p>
 * 时间单位为 tick：JSON 中 animation_length / keyframe 时间按秒书写，
 * 加载时统一乘以 20，与旧 {@code GeoSideloader} 的 tick 域保持一致。
 * </p>
 */
final class AnimatedClip {

    private final String name;
    private final double lengthTicks;
    private final boolean loop;
    private final List<BoneClip> boneClips;

    AnimatedClip(String name, double lengthTicks, boolean loop, List<BoneClip> boneClips) {
        this.name = name;
        this.lengthTicks = lengthTicks;
        this.loop = loop;
        this.boneClips = boneClips;
    }

    String getName() {
        return name;
    }

    double lengthTicks() {
        return lengthTicks;
    }

    void sample(double tick, AnimatedGeoModel model) {
        double elapsed;
        if (lengthTicks > 0 && loop) {
            elapsed = tick - Math.floor(tick / lengthTicks) * lengthTicks;
        } else if (lengthTicks > 0) {
            elapsed = Math.min(tick, lengthTicks);
        } else {
            elapsed = tick;
        }
        if (!loop && elapsed < 0) {
            elapsed = 0;
        }

        for (BoneClip boneClip : boneClips) {
            AnimatedBone bone = model.getBone(boneClip.boneName);
            if (bone == null) {
                continue;
            }
            if (boneClip.rotation != null) {
                float[] value = boneClip.rotation.sample(elapsed);
                bone.setRotation(value[0], value[1], value[2]);
            }
            if (boneClip.position != null) {
                float[] value = boneClip.position.sample(elapsed);
                bone.setPosition(value[0], value[1], value[2]);
            }
            if (boneClip.scale != null) {
                float[] value = boneClip.scale.sample(elapsed);
                bone.setScale(value[0], value[1], value[2]);
            }
        }
    }

    record BoneClip(String boneName, Channel rotation, Channel position, Channel scale) {
    }

    static final class Channel {

        private final List<Segment> x;
        private final List<Segment> y;
        private final List<Segment> z;

        Channel(List<Segment> x, List<Segment> y, List<Segment> z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        float[] sample(double elapsed) {
            return new float[]{
                    (float) sampleAxis(x, elapsed),
                    (float) sampleAxis(y, elapsed),
                    (float) sampleAxis(z, elapsed)
            };
        }

        private double sampleAxis(List<Segment> segments, double elapsed) {
            if (segments == null || segments.isEmpty()) {
                return 0;
            }

            double segmentStart = 0;
            int last = segments.size() - 1;
            for (int i = 0; i < segments.size(); i++) {
                Segment segment = segments.get(i);
                double segmentEnd = segmentStart + segment.length;
                if (elapsed < segmentEnd || i == last) {
                    double local = elapsed - segmentStart;
                    if (segment.length <= 0 || local >= segment.length) {
                        return segment.endValue;
                    }
                    double t = local / segment.length;
                    double eased = GeoEasing.apply(segment.easing, t);
                    return Mth.lerp(eased, segment.startValue, segment.endValue);
                }
                segmentStart = segmentEnd;
            }

            return segments.get(segments.size() - 1).endValue;
        }
    }

    record Segment(double length, double startValue, double endValue, String easing) {
    }
}
