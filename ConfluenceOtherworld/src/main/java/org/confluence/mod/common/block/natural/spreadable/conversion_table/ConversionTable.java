package org.confluence.mod.common.block.natural.spreadable.conversion_table;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class ConversionTable {
    protected final Map<BlockState, BlockState> cache = new Reference2ObjectOpenHashMap<>();
    protected boolean allowsAir = false;
    protected BlockState lastCheck;
    protected BlockState lastTarget;

    public @Nullable BlockState get(BlockState source, boolean hardmode) {
        if (!allowsAir && source.isAir()) return null;
        if (lastTarget != null && source == lastCheck) return lastTarget;
        BlockState computed = cache.computeIfAbsent(source, state -> getTargetState(state, hardmode));
        if (source != lastCheck) {
            this.lastCheck = source;
            this.lastTarget = computed;
        }
        return computed;
    }

    protected abstract @Nullable Block getTarget(BlockState source, boolean hardmode);

    @SuppressWarnings("unchecked")
    protected <T extends Comparable<T>, V extends T> @Nullable BlockState getTargetState(BlockState source, boolean hardmode) {
        Block target = getTarget(source, hardmode);
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
