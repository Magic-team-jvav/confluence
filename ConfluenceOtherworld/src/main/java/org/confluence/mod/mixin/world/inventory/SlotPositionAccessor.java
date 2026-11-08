package org.confluence.mod.mixin.world.inventory;

import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/// 泰拉风格界面需要在客户端重新摆放原版菜单（箱子等）的槽位。槽位坐标只用于客户端渲染与点击判定。
@Mixin(Slot.class)
public interface SlotPositionAccessor {
    @Mutable
    @Accessor("x")
    void confluence$setX(int x);

    @Mutable
    @Accessor("y")
    void confluence$setY(int y);
}
