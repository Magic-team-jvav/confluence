package org.confluence.terra_furniture.api.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;

/**
 * 方块实体渲染功能钩子接口，用于在 {@link BaseFunctionalGeoBER} 的默认渲染逻辑前后插入自定义处理。
 *
 * <p>实现此接口可用于执行额外的渲染准备、状态修改、功能叠加或调试输出等操作，
 * 以增强或替代默认的 GeoBlockEntity 渲染行为。
 *
 * <p>该接口设计为与 {@link BaseFunctionalGeoBER.Builder#addRenderHook(IRenderFunctionHook)} 配合使用，
 * 支持多个钩子按注册顺序执行（前序）与逆序回调（后序），确保渲染逻辑的可组合性与可控性。
 *
 * @param <T> 方块实体类型，需同时实现 BlockEntity 与 GeoBlockEntity 接口
 * @apiNote 请确保严格有序注册，渲染时的操作依赖于注册顺序表现
 * @author MakerTechno
 */
public interface IRenderFunctionHook<T extends BlockEntity & GeoBlockEntity> {
    /**
     * 在默认渲染逻辑执行之前调用，用于执行定位及前渲染处理。
     */
    void processBefore(PoseStack poseStack, T animatable, MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer, float yaw, float partialTick, int packedLight);
    /**
     * 在默认渲染逻辑执行之后调用，用于执行释放及后渲染处理。
     */
    void processAfter(PoseStack poseStack, T animatable, MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer, float yaw, float partialTick, int packedLight);
}
