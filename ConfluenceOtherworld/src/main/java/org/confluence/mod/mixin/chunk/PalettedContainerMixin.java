package org.confluence.mod.mixin.chunk;

import net.minecraft.core.IdMap;
import net.minecraft.world.level.chunk.PalettedContainer;
import org.confluence.mod.mixed.IPalettedContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PalettedContainer.class)
public abstract class PalettedContainerMixin<T> implements IPalettedContainer<T> {
    @Shadow
    @Final
    private IdMap<T> registry;
    @Shadow
    @Final
    private PalettedContainer.Strategy strategy;

    @Override
    public PalettedContainer<T> confluence$recreateSingle(T ele) {
        return new PalettedContainer<>(this.registry, ele, this.strategy);
    }
}
