package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.confluence.mod.client.entity.model.CrownOfKingSlimeModel;
import org.confluence.mod.common.entity.model.CrownOfKingSlimeModelEntity;
import org.joml.Quaternionf;

/// 渲染史莱姆王传送时抛出的独立王冠。
public class CrownOfKingSlimeModelRenderer extends EntityRenderer<CrownOfKingSlimeModelEntity> {
    // 模型导出坐标系与实体渲染坐标系上下相反，统一绕 Z 轴翻转 180°。
    public static final Quaternionf FLIP_Y = Axis.ZP.rotation(Mth.PI);
    private final CrownOfKingSlimeModel model;

    public CrownOfKingSlimeModelRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new CrownOfKingSlimeModel(context.bakeLayer(CrownOfKingSlimeModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(CrownOfKingSlimeModelEntity entity) {
        return CrownOfKingSlimeModel.TEXTURE;
    }

    @Override
    public void render(CrownOfKingSlimeModelEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotation(Mth.lerp(partialTick, entity.rotateO2, entity.rotate2)));
        poseStack.translate(0.0F, -0.5F, entity.radius);
        poseStack.mulPose(entity.quaternion.rotationXYZ(Mth.lerp(partialTick, entity.rotO.x, entity.rot.x), Mth.lerp(partialTick, entity.rotO.y, entity.rot.y), Mth.lerp(partialTick, entity.rotO.z, entity.rot.z)));
        poseStack.mulPose(Axis.YN.rotation(Mth.lerp(partialTick, entity.rotateO1, entity.rotate1)));
        poseStack.translate(0.0F, 1.9375F + entity.height, 0.0F);
        poseStack.mulPose(FLIP_Y);
        poseStack.scale(CrownOfKingSlimeModel.RENDER_SCALE, CrownOfKingSlimeModel.RENDER_SCALE, CrownOfKingSlimeModel.RENDER_SCALE);
        model.renderToBuffer(poseStack, buffer.getBuffer(CrownOfKingSlimeModel.RENDER_TYPE), packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public boolean shouldRender(CrownOfKingSlimeModelEntity entity, Frustum camera, double camX, double camY, double camZ) {
        return true;
    }
}
