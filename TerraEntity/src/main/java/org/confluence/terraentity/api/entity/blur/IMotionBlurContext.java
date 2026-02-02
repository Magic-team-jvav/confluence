package org.confluence.terraentity.api.entity.blur;

import net.minecraft.world.phys.Vec3;

/**
 * 模糊信息上下文。运动模糊需要处理的数据
 */
public interface IMotionBlurContext {

    Vec3 pos();

    float xRot();

    float yRot();

}
