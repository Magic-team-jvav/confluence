package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.client.entity.model.ExplicitGeoModel;
import org.confluence.mod.common.entity.monster.JellyFish;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

/// 水母脉冲状态、游动朝向和发光表现的专用渲染器。
///
/// 服务端只同步当前是否进入脉冲阶段；客户端使用相邻两次有效速度插值模型朝向，
/// 并持续绕自身纵轴缓慢旋转。发光重绘仅在脉冲阶段启用，避免待机水母始终全亮。
public final class JellyFishRenderer extends GeoNormalRenderer<JellyFish> {
    public JellyFishRenderer(EntityRendererProvider.Context context, ExplicitGeoModel<JellyFish> model) {
        super(context, model);
        addRenderLayer(new AutoGlowingGeoLayer<>(this) {
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
                if (animatable.isAttackPhase()) {
                    super.render(poseStack, animatable, bakedModel, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
                }
            }
        });
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
        poseStack.translate(0.0, 0.25, 0.0);
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
