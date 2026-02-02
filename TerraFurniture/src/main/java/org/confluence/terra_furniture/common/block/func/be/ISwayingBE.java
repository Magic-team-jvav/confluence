package org.confluence.terra_furniture.common.block.func.be;

import net.minecraft.world.phys.Vec3;

/**
 * 吊挂类物品摇晃方法的根基。
 */
public interface ISwayingBE {
    void applyDelta(Vec3 input);
    float getSwingX();
    float getSwingZ();
    Vec3 getAnchorPoint();
    void trigController();
}