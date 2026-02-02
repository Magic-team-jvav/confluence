package org.confluence.terraentity.api.entity.blur;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.function.Consumer;

/**
 * 渲染动作模糊
 * @param <T> 实体类型
 * @param <C> 模糊信息上下文类型
 */
@OnlyIn(Dist.CLIENT)
public interface IMotionBlurRenderer<T extends Entity & IMotionBlurHolder<C>, C extends IMotionBlurContext> {

    void renderBlur(PoseStack poseStack,T animatable, float partialTick, Consumer<Integer> renderCallback);

}
