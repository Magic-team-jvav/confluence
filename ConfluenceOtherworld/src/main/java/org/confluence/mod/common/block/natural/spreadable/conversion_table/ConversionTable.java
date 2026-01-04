package org.confluence.mod.common.block.natural.spreadable.conversion_table;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class ConversionTable {
    protected final Map<BlockState, BlockState> cache = new Object2ObjectOpenHashMap<>();
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

    @Deprecated(forRemoval = true, since = "1.2.0")
    @ApiStatus.ScheduledForRemoval(inVersion = "1.3.0")
    public @Nullable BlockState get(BlockState source) {
        return get(source, true);
    }

    @ApiStatus.OverrideOnly
    protected @Nullable Block getTarget(BlockState source, boolean hardmode) {
        return getTarget(source);
    }

    @Deprecated(forRemoval = true, since = "1.2.0")
    @ApiStatus.ScheduledForRemoval(inVersion = "1.3.0")
    protected @Nullable Block getTarget(BlockState source) {
        return null;
    }

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

    @Deprecated(forRemoval = true, since = "1.2.0")
    @ApiStatus.ScheduledForRemoval(inVersion = "1.3.0")
    protected <T extends Comparable<T>, V extends T> @Nullable BlockState getTargetState(BlockState source) {
        return getTargetState(source, true);
    }

    public void clear() {
        cache.clear();
    }
}
