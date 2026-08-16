package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.boss.DeerClops;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

/// 独眼巨鹿专用渲染器。
///
/// <p>常态使用基础纹理；超距无敌期间额外叠加 1.21 侧已经提供的发光纹理，
/// 使“当前无法受到普通伤害”成为明确的视觉反馈。</p>
public final class DeerclopsRenderer extends BossGeoRenderer<DeerClops> {
    private static final ResourceLocation INVULNERABLE_TEXTURE = Confluence.asResource(
            "textures/entity/boss/deerclops_invulnerable_state.png");

    public DeerclopsRenderer(EntityRendererProvider.Context context) {
        super(context, Confluence.asResource("boss/deerclops"));
        addRenderLayer(new AutoGlowingGeoLayer<>(this) {
            @Override
            protected ResourceLocation getTextureResource(DeerClops animatable) {
                return INVULNERABLE_TEXTURE;
            }

            @Override
            protected RenderType getRenderType(DeerClops animatable) {
                return RenderType.entityTranslucentEmissive(INVULNERABLE_TEXTURE);
            }

            @Override
            public void render(
                    PoseStack poseStack,
                    DeerClops animatable,
                    BakedGeoModel bakedModel,
                    RenderType renderType,
                    MultiBufferSource bufferSource,
                    VertexConsumer buffer,
                    float partialTick,
                    int packedLight,
                    int packedOverlay) {
                if (animatable.isFarForInvulnerable()) {
                    super.render(
                            poseStack,
                            animatable,
                            bakedModel,
                            renderType,
                            bufferSource,
                            buffer,
                            partialTick,
                            packedLight,
                            packedOverlay);
                }
            }
        });
    }
}
