package org.confluence.mod.mixin.world;

import net.minecraft.world.Container;
import org.confluence.lib.util.LibUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * 让原版容器接受 MagicLib 配置的扩展物品堆叠上限。
 * 这里只放宽容器自身的 64 上限，具体物品是否能够堆叠以及堆叠数量仍由物品属性决定。
 */
@Mixin(value = Container.class, priority = 1100)
public interface ContainerMixin {
    /**
     * @author Confluence Team
     * @reason 1.20.1 的接口注入器无法作用于 Container 默认方法，只能在这里覆盖同一个默认值入口。
     */
    @Overwrite
    default int getMaxStackSize() {
        return LibUtils.getMaxStackSize(64);
    }
}
