package org.confluence.mod.client.summoner.model.bbmodel;

import net.minecraft.util.Mth;

import java.util.List;

final class BBModelClip {

    private final String name;
    private final double lengthTicks;
    private final boolean loop;
    private final List<BoneClip> boneClips;

    BBModelClip(String name, double lengthTicks, boolean loop, List<BoneClip> boneClips) {
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

    void sample(double tick, BBModelModel model) {
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
            BBModelBone bone = model.getBone(boneClip.boneName);
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
        private final List<Segment> wrapX;
        private final List<Segment> wrapY;
        private final List<Segment> wrapZ;
        private final double wrapStart;
        private final double wrapLength;
        private final double firstKeyframe;
        private final double animationLength;

        Channel(
                List<Segment> x,
                List<Segment> y,
                List<Segment> z,
                List<Segment> wrapX,
                List<Segment> wrapY,
                List<Segment> wrapZ,
                double wrapStart,
                double wrapLength,
                double firstKeyframe,
                double animationLength
        ) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.wrapX = wrapX;
            this.wrapY = wrapY;
            this.wrapZ = wrapZ;
            this.wrapStart = wrapStart;
            this.wrapLength = wrapLength;
            this.firstKeyframe = firstKeyframe;
            this.animationLength = animationLength;
        }

        float[] sample(double elapsed) {
            return new float[]{
                    (float) sampleAxis(x, wrapX, elapsed),
                    (float) sampleAxis(y, wrapY, elapsed),
                    (float) sampleAxis(z, wrapZ, elapsed)
            };
        }

        private double sampleAxis(List<Segment> segments, List<Segment> wrapSegments, double elapsed) {
            if (wrapLength > 1.0E-6
                    && !wrapSegments.isEmpty()
                    && (elapsed < firstKeyframe || elapsed >= wrapStart)) {
                double local = elapsed < firstKeyframe
                        ? elapsed + animationLength - wrapStart
                        : elapsed - wrapStart;
                return sampleSegments(wrapSegments, local);
            }
            return sampleSegments(segments, elapsed);
        }

        private double sampleSegments(List<Segment> segments, double elapsed) {
            if (segments == null || segments.isEmpty()) {
                return 0;
            }

            double segmentStart = 0;
            int last = segments.size() - 1;
            for (int i = 0; i < segments.size(); i++) {
                Segment segment = segments.get(i);
                double segmentEnd = segmentStart + segment.length();
                if (elapsed < segmentEnd || i == last) {
                    double local = elapsed - segmentStart;
                    if (segment.length() <= 0 || local >= segment.length()) {
                        return segment.endValue();
                    }
                    double t = local / segment.length();
                    double eased = BBModelEasing.apply(segment.easing(), t);
                    return Mth.lerp(eased, segment.startValue(), segment.endValue());
                }
                segmentStart = segmentEnd;
            }

            return segments.get(segments.size() - 1).endValue();
        }
    }

    record Segment(double length, double startValue, double endValue, String easing) {
    }
}
