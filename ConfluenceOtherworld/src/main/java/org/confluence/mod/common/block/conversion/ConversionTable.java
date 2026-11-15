package org.confluence.mod.common.block.conversion;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
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

    private boolean cachedHardmode;

    // 缓存同时受困难模式状态约束；空结果也缓存，避免反复扫描不适用方块。
    public @Nullable BlockState get(BlockState source, boolean hardmode) {
        if (!allowsAir && source.isAir()) return null;
        if (cachedHardmode != hardmode) {
            clear();
            cachedHardmode = hardmode;
        }
        if (source == lastCheck) return lastTarget;
        if (!cache.containsKey(source)) cache.put(source, getTargetState(source, hardmode));
        lastCheck = source;
        lastTarget = cache.get(source);
        return lastTarget;
    }

    // 需要邻接地形的显式转换可覆写此入口；自然感染继续使用原来的缓存入口。
    public @Nullable BlockState get(BlockState source, boolean hardmode, LevelReader level, BlockPos pos) {
        return get(source, hardmode);
    }

    // 子表直接定义转换条件；返回空值表示该来源没有匹配规则。
    protected abstract @Nullable Block getTarget(BlockState source, boolean hardmode);

    // 从目标默认状态复制双方共有的属性，原方块不变时返回空值。
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

    // 标签重载或阶段变化后清理全部缓存，包括最近一次查询结果。
    public void clear() {
        cache.clear();
        lastCheck = null;
        lastTarget = null;
    }
}
