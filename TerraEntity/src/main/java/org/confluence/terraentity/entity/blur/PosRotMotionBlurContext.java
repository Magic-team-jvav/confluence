package org.confluence.terraentity.entity.blur;

import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.api.entity.blur.IMotionBlurContext;

/**
 * 简单的位置和旋转模糊上下文
 */
public record PosRotMotionBlurContext(Vec3 pos, float xRot, float yRot) implements IMotionBlurContext {

}
