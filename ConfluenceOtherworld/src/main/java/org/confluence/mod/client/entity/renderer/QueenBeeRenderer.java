package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.boss.QueenBee;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

/// 蜂王基础模型向上校正到碰撞箱中心；离开丛林进入愤怒状态时，
/// 眼部纹理以自发光通道叠加，普通状态不绘制该层。
public final class QueenBeeRenderer extends BossGeoRenderer<QueenBee> {
    private static final ResourceLocation EYE_TEXTURE = Confluence.asResource("textures/entity/boss/queen_bee_eye.png");

    public QueenBeeRenderer(EntityRendererProvider.Context context) {
        super(context, Confluence.asResource("boss/queen_bee"), false, 1.0F, 0.75F);
        addRenderLayer(new AutoGlowingGeoLayer<>(this) {
            @Override
            protected ResourceLocation getTextureResource(QueenBee animatable) {
                return EYE_TEXTURE;
            }

            @Override
            protected RenderType getRenderType(QueenBee animatable) {
                return RenderType.entityTranslucentEmissive(EYE_TEXTURE);
            }

            @Override
            public void render(PoseStack poseStack, QueenBee animatable, BakedGeoModel bakedModel,
                               RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
                               float partialTick, int packedLight, int packedOverlay) {
                if (animatable.isAngry()) {
                    super.render(poseStack, animatable, bakedModel, renderType, bufferSource, buffer,
                            partialTick, packedLight, packedOverlay);
                }
            }
        });
    }
}
