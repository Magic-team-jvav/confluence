package org.confluence.terraentity.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import org.confluence.terraentity.api.entity.blur.IMotionBlurContext;
import org.confluence.terraentity.api.entity.blur.IMotionBlurHolder;
import org.confluence.terraentity.api.entity.blur.IMotionBlurRenderer;
import org.confluence.terraentity.client.entity.model.GeoNormalModel;
import org.confluence.terraentity.client.event.RenderEvent;
import org.confluence.terraentity.config.ClientConfig;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;

import java.util.function.Function;

public class GeoMotionBlurRenderer<T extends Mob & GeoEntity & IMotionBlurHolder<C>, C extends IMotionBlurContext> extends GeoNormalRenderer<T> {

    IMotionBlurRenderer<T, C> motionBlurRenderer;
    boolean isBlur = false;

    public GeoMotionBlurRenderer(EntityRendererProvider.Context renderManager, ResourceLocation path) {
        this(renderManager, path, false,1,0);
    }

    public GeoMotionBlurRenderer(EntityRendererProvider.Context renderManager, ResourceLocation path, boolean ifRotX) {
        this(renderManager, path, ifRotX,1,0);
    }

    public GeoMotionBlurRenderer(EntityRendererProvider.Context renderManager, ResourceLocation path, boolean ifRotX, float scale, float offsetY) {
        this(renderManager, new GeoNormalModel<>(path), ifRotX,scale,offsetY);
    }

    public GeoMotionBlurRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model, boolean ifRotX, float scale, float offsetY) {
        super(renderManager, model, ifRotX, scale, offsetY);
    }

    public GeoMotionBlurRenderer<T, C> setMotionBlurRenderer(Function<GeoMotionBlurRenderer<T, C >, IMotionBlurRenderer<T, C>> motionBlurRendererFunction) {
        this.motionBlurRenderer = motionBlurRendererFunction.apply(this);
        return this;
    }


    public void defaultRender(PoseStack poseStack, T animatable, MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer,
                              float yaw, float partialTick, int packedLight) {

        // 初始化模板测试
//        GL11.glEnable(GL11.GL_STENCIL_TEST);
//        RenderSystem.stencilMask(0xFF); // 启用模板写入
//        RenderSystem.clear(GL11.GL_STENCIL_BUFFER_BIT, Minecraft.ON_OSX); // 清除模板缓冲区
//        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF); // 总是通过测试
//        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE); // 通过时写入1

        // 渲染主体（会写入模板值1）
        super.defaultRender(poseStack, animatable, bufferSource, renderType, buffer, yaw, partialTick, packedLight);
//        Minecraft.getInstance().renderBuffers().bufferSource().endBatch();

    }

    @Override
    public void renderFinal(PoseStack poseStack, T animatable, BakedGeoModel model, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight,
                            int packedOverlay, int colour) {

        super.renderFinal(poseStack, animatable, model, bufferSource, buffer, partialTick, packedLight, packedOverlay, colour);
//        Minecraft.getInstance().renderBuffers().bufferSource().endBatch();
//        // 关闭模板写入（后续操作不影响模板缓冲区）
//        RenderSystem.stencilMask(0x00);

        // 控制光影不渲染运动残影
        if (RenderEvent.isAfterSky && ClientConfig.ENABLE_ENTITY_MOTION_BLUR.get()) {
            this.isBlur = true;

//            // 配置模板测试：仅渲染模板值不为1的区域
//            GL11.glStencilFunc(GL11.GL_NOTEQUAL, 1, 0xFF); // 不等于1时通过
//            GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP); // 保持模板值不变

            IMotionBlurRenderer<T, C> renderer = this.motionBlurRenderer;
            if (renderer != null) {
                renderer.renderBlur(poseStack, animatable, partialTick, (color) -> {
                    // 渲染残影（仅在不等于1的区域）
                    super.actuallyRender(poseStack, animatable, model,
                            RenderType.entityTranslucent(getTextureLocation(animatable)),
                            bufferSource, buffer, false, partialTick, packedLight, packedOverlay, color);
                });
            }

            this.isBlur = false;
        }
    }

    @Override
    public boolean shouldRender(T livingEntity, Frustum camera, double camX, double camY, double camZ) {
        if(!livingEntity.getMotionBlurManager().isEmpty()){
            return true;
        }
        return super.shouldRender(livingEntity, camera, camX, camY, camZ);
    }

    @Override
    protected void applyRotations(T animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        if(!this.isBlur){ // 防止重复旋转x轴
            super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick, nativeScale);
        }
    }

    @Override
    public RenderType getRenderType(T animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}
