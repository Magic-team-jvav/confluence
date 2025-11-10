package org.confluence.mod.common.block.natural.spreadable.conversion_table;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class ConversionTable {
    protected final Map<BlockState, BlockState> cache = new Object2ObjectOpenHashMap<>();
    protected boolean allowsAir = false;
    protected BlockState lastCheck;
    protected BlockState lastTarget;

    public @Nullable BlockState get(BlockState source) {
        if (!allowsAir && source.isAir()) return null;
        if (lastTarget != null && source == lastCheck) return lastTarget;
        BlockState computed = cache.computeIfAbsent(source, this::getTargetState);
        if (source != lastCheck) {
            this.lastCheck = source;
            this.lastTarget = computed;
        }
        return computed;
    }

    protected abstract @Nullable Block getTarget(BlockState source);

    @SuppressWarnings("unchecked")
    protected <T extends Comparable<T>, V extends T> @Nullable BlockState getTargetState(BlockState source) {
        Block target = getTarget(source);
        if (target == null || source.is(target)) return null;
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
