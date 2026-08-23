package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.entity.model.CritterGeoModel;
import org.confluence.mod.common.entity.animal.Fealing;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import java.util.List;

/// 飞灵专用渲染器。
///
/// 妖精模型把外层发光立方体和内部实体分别存放在两组骨骼中。主渲染阶段只绘制内部骨骼，
/// 第二阶段只绘制外层骨骼，避免同一几何被重复提交。与通用的 {@code eyes} 发光层不同，
/// 外层使用保留深度写入和背面剔除的裁切渲染，因此它表现为有明确表面的发光立方体，
/// 不会形成粒子式的半透明叠加。
public final class FealingRenderer extends FairyRenderer<Fealing> {
    private static final int FULL_BRIGHT = 0xF000F0;

    public FealingRenderer(EntityRendererProvider.Context context) {
        super(context, new CritterGeoModel<>(Confluence.asResource("geo/animal/dummy")));
        setBoneToGlow(List.of("Outline", "Outline2", "Outline3", "Outline4", "Outline5"), List.of("Body", "Internal", "Internal2", "Internal3", "Internal4"));
    }

    /// 裁切通道会丢弃完全透明的纹理像素，同时保留立方体的深度、遮挡和背面剔除语义。
    @Override
    protected RenderType getGlowRenderType(Fealing animatable, ResourceLocation texture) {
        return RenderType.entityCutout(texture);
    }

    /// 内部骨骼与外层立方体采用相同的实体表面语义，避免主阶段继续继承半透明轮廓通道。
    @Override
    public RenderType getRenderType(Fealing animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(texture);
    }

    /// 普通实体裁切着色器仍会读取顶点光照，因此两个骨骼阶段都必须显式传入全亮光照。
    @Override
    public void actuallyRender(
            PoseStack poseStack,
            Fealing animatable,
            BakedGeoModel model,
            RenderType renderType,
            MultiBufferSource bufferSource,
            VertexConsumer buffer,
            boolean isReRender,
            float partialTick,
            int packedLight,
            int packedOverlay,
            float red,
            float green,
            float blue,
            float alpha) {
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, FULL_BRIGHT, packedOverlay, red, green, blue, alpha);
    }
}
