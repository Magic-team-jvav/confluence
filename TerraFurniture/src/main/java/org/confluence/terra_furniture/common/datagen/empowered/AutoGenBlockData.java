package org.confluence.terra_furniture.common.datagen.empowered;

import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public interface AutoGenBlockData<T extends Block> {
    @Nullable BlockDataGenerator<? super T> getGenerator();
}
