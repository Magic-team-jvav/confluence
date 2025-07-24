package org.confluence.mod.common.block.natural.spreadable;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class ConversionTable {
    private final Map<BlockState, BlockState> cache = new Object2ObjectOpenHashMap<>();
    private BlockState lastCheck;
    private BlockState lastTarget;

    public @Nullable BlockState get(BlockState blockState) {
        if (lastTarget != null && blockState == lastCheck) return lastTarget;
        BlockState computed = cache.computeIfAbsent(blockState, this::getTargetState);
        if (blockState != lastCheck) {
            this.lastCheck = blockState;
            this.lastTarget = computed;
        }
        return computed;
    }

    protected abstract @Nullable Block getTarget(BlockState source);

    @SuppressWarnings("unchecked")
    protected <T extends Comparable<T>, V extends T> @Nullable BlockState getTargetState(BlockState source) {
        Block target = getTarget(source);
        if (target == null) return null;
        BlockState targetState = target.defaultBlockState();
        for (Map.Entry<Property<?>, Comparable<?>> entry1 : source.getValues().entrySet()) {
            if (targetState.hasProperty(entry1.getKey())) {
                targetState = targetState.setValue((Property<T>) entry1.getKey(), (V) entry1.getValue());
            }
        }
        return targetState;
    }

    public void clear() {
        cache.clear();
    }
}
