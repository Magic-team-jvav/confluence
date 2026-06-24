package org.confluence.mod.common.entity.animation;

import com.mojang.serialization.Codec;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.keyframe.animation.Vec3KeyframeAnimation;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

public class ModelPositionTable implements Iterable<Map.Entry<String, Vec3KeyframeAnimation>> {
    public static final Codec<ModelPositionTable> CODEC = Codec.unboundedMap(Codec.STRING, Vec3KeyframeAnimation.CODEC).xmap(ModelPositionTable::new, i -> i.positions);

    private final Map<String, Vec3KeyframeAnimation> positions;

    public ModelPositionTable(Map<String, Vec3KeyframeAnimation> positions) {
        this.positions = positions;
    }

    public Vec3KeyframeAnimation getPositions(String name) {
        return positions.get(name);
    }

    @Override
    public @NotNull Iterator<Map.Entry<String, Vec3KeyframeAnimation>> iterator() {
        return positions.entrySet().iterator();
    }

    public void put(String name, Vec3KeyframeAnimation animation) {
        positions.put(name, animation);
    }

    public static class Builder {
        private final Map<String, Vec3KeyframeAnimation.Builder> positionsBuilder;

        public Builder() {
            positionsBuilder = new java.util.HashMap<>();
        }

        public Builder addPosition(String name, int tick, Vec3 position) {
            Vec3KeyframeAnimation.Builder builder;
            if (!positionsBuilder.containsKey(name)) {
                builder = Vec3KeyframeAnimation.builder();
                positionsBuilder.put(name, builder);
            } else {
                builder = positionsBuilder.get(name);
            }
            if (!builder.hasTime(tick)) {
//                builder.deleteKeyframe(tick);
                builder.addKeyframe(tick, position);
            }

            return this;
        }

        public Builder replacePosition(String name, int tick, Vec3 position) {
            Vec3KeyframeAnimation.Builder builder;
            if (!positionsBuilder.containsKey(name)) {
                builder = Vec3KeyframeAnimation.builder();
                positionsBuilder.put(name, builder);
            } else {
                builder = positionsBuilder.get(name);
            }
            if (builder.hasTime(tick)) {
                builder.deleteKeyframe(tick);
            }
            builder.addKeyframe(tick, position);

            return this;
        }

        public ModelPositionTable build() {
            return new ModelPositionTable(this.positionsBuilder.entrySet()
                    .stream()
                    .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().build()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

        }
    }
}
