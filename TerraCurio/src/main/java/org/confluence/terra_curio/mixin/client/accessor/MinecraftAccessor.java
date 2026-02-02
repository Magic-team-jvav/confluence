package org.confluence.terra_curio.mixin.client.accessor;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface MinecraftAccessor {
    @Accessor
    int getMissTime();

    @Accessor
    void setMissTime(int missTime); // left click

    @Accessor
    int getRightClickDelay();

    @Accessor
    void setRightClickDelay(int rightClickDelay); // right click
}
