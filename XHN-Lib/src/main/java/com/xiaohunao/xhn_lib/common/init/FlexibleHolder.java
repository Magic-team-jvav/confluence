package com.xiaohunao.xhn_lib.common.init;

import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

public class FlexibleHolder<R, T extends R> extends DeferredHolder<R, T> {
    // 保存对实际值的直接引用，在动态注册时立即可用
    private final T directValue;

    /**
     * 创建一个新的FlexibleHolder
     * @param key 资源键
     * @param value 直接可用的值对象
     */
    public FlexibleHolder(ResourceKey<R> key, T value) {
        super(key);
        this.directValue = value;
    }

    @Override
    public @NotNull T value() {
        // 优先使用直接引用，避免依赖父类的bind机制
        if (directValue != null) {
            return directValue;
        }
        // 如果没有直接值，则回退到父类行为
        return super.value();
    }

    @Override
    public @NotNull T get() {
        return value();
    }

    @Override
    public boolean isBound() {
        // 如果有直接值，则认为始终已绑定
        return directValue != null || super.isBound();
    }

    /**
     * 获取此持有者包含的直接值引用
     */
    public T getDirectValue() {
        return directValue;
    }
}
