package org.confluence.terra_furniture.common.block.func;

import net.minecraft.world.level.block.Block;

public interface BlockSetGetter<T extends Block> {
    /**
     * 方块所属的材质类型
     */
    TFBlockSetType getType();
    boolean hasParticle(T block);
    default boolean isSpecialParticleTexture() {
        return false;
    }
    default boolean isLayeredItemTexture() {
        return false;
    }
}
