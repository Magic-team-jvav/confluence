package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.client.entity.model.ExplicitGeoModel;
import org.confluence.mod.common.entity.monster.JellyFish;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;

/// 水母脉冲状态、游动朝向和发光表现的专用渲染器。
///
/// 服务端分别同步脉冲推进和专家模式带电状态；客户端使用相邻两次有效速度插值模型朝向，
/// 并持续绕自身纵轴缓慢旋转。发光重绘仅在带电阶段启用，避免普通难度水母错误发亮。
public final class JellyFishRenderer extends GeoNormalRenderer<JellyFish> {
    private final float originOffset;
    private final boolean luminous;

    public JellyFishRenderer(EntityRendererProvider.Context context, ExplicitGeoModel<JellyFish> model) {
        this(context, model, 0.25F, false);
    }

    public JellyFishRenderer(EntityRendererProvider.Context context, ExplicitGeoModel<JellyFish> model, float originOffset, boolean luminous) {
        super(context, model);
        this.originOffset = originOffset;
        this.luminous = luminous;
        addRenderLayer(new EntityGlowingGeoLayer<>(this) {
            @Override
            protected ResourceLocation getTextureResource(JellyFish animatable) {
                return JellyFishRenderer.this.getTextureLocation(animatable);
            }

            @Override
            protected RenderType getRenderType(JellyFish animatable) {
                return RenderType.entityTranslucentEmissive(getTextureResource(animatable));
            }

            @Override
            public void render(
                    PoseStack poseStack,
                    JellyFish animatable,
                    BakedGeoModel bakedModel,
                    RenderType renderType,
                    MultiBufferSource bufferSource,
                    VertexConsumer buffer,
                    float partialTick,
                    int packedLight,
                    int packedOverlay) {
                if (animatable.isElectrified()) {
                    super.render(poseStack, animatable, bakedModel, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
                }
            }
        });
    }

    @Override
    protected int getBlockLightLevel(JellyFish jellyfish, BlockPos pos) {
        return luminous ? 15 : super.getBlockLightLevel(jellyfish, pos);
    }

    @Override
    public void preRender(
            PoseStack poseStack,
            JellyFish jellyfish,
            BakedGeoModel model,
            MultiBufferSource buffers,
            VertexConsumer buffer,
            boolean reRender,
            float partialTick,
            int packedLight,
            int packedOverlay,
            float red,
            float green,
            float blue,
            float alpha) {
        poseStack.translate(0.0, originOffset, 0.0);
        if (!jellyfish.isAttackPhase()) {
            Vec3 direction = jellyfish.lastMovement.lerp(jellyfish.currentMovement, partialTick);
            if (direction.lengthSqr() > 1.0E-6) {
                Vector3f target = direction.normalize().toVector3f();
                poseStack.mulPose(new Quaternionf().rotationTo(new Vector3f(0.0F, 1.0F, 0.0F), target));
            }
        }
        poseStack.mulPose(Axis.YN.rotationDegrees((jellyfish.tickCount + partialTick) * 3.0F));
        super.preRender(poseStack, jellyfish, model, buffers, buffer, reRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
