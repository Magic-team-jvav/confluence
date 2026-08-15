package org.confluence.mod.mixin.client.accessor;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * 仅向本体的放置速度实现开放原版右键冷却。
 */
@Mixin(Minecraft.class)
public interface MinecraftAccessor {
    @Accessor("rightClickDelay")
    int confluence$getRightClickDelay();

    @Accessor("rightClickDelay")
    void confluence$setRightClickDelay(int rightClickDelay);
}
