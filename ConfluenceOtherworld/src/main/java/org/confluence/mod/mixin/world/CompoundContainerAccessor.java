package org.confluence.mod.mixin.world;

import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/// 大箱子由两半组成；重命名时两半都改名（原版标题取有名字的那一半）。
@Mixin(CompoundContainer.class)
public interface CompoundContainerAccessor {
    @Accessor("container1")
    Container confluence$getContainer1();

    @Accessor("container2")
    Container confluence$getContainer2();
}
