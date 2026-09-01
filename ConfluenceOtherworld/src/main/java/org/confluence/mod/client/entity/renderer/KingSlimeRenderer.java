package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.entity.model.BaseSlimeModel;
import org.confluence.mod.client.entity.model.CrownOfKingSlimeModel;
import org.confluence.mod.common.entity.boss.KingSlime;

/// 史莱姆王专用渲染器。
///
/// 本体使用史莱姆内核、面部和半透明外壳，并在顶部单独绘制王冠。实体不继承
/// 原版史莱姆，因此这里复用本体已有的史莱姆模型，并直接读取同步的
/// 连续尺寸。不能用尖刺史莱姆骨骼代替身体，否则客户端只会看到王冠和少量装饰骨骼。
public final class KingSlimeRenderer extends MobRenderer<KingSlime, BaseSlimeModel<KingSlime>> {
    private final CrownOfKingSlimeModel crownModel;

    public KingSlimeRenderer(EntityRendererProvider.Context context) {
        super(context, new BaseSlimeModel<>(context.bakeLayer(BaseSlimeModel.INNER_LAYER)), 0.25F);
        addLayer(new BaseSlimeOuterLayer<>(this, context.getModelSet()));
        this.crownModel = new CrownOfKingSlimeModel(context.bakeLayer(CrownOfKingSlimeModel.LAYER_LOCATION));
    }

    @Override
    public net.minecraft.resources.ResourceLocation getTextureLocation(KingSlime slime) {
        return Confluence.asResource("textures/entity/slime/slime_blue.png");
    }

    @Override
    public void render(KingSlime slime, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        float visualScale = slime.getVisualScale(partialTick);
        float squish = Mth.lerp(partialTick, slime.getOldSquish(), slime.getSquish())
                / (visualScale * 0.5F + 1.0F);
        // 当前碰撞箱已经包含体型缩放，只需补上本帧挤压造成的纵向伸缩。
        float bodyHeight = slime.getDimensions(slime.getPose()).height * (squish + 1.0F);
        poseStack.translate(0.0F, bodyHeight + CrownOfKingSlimeModel.ATTACHMENT_BASE_OFFSET, 0.0F);
        float bodyRotation = Mth.lerp(partialTick, slime.yBodyRotO, slime.yBodyRot);
        poseStack.mulPose(CrownOfKingSlimeModelRenderer.FLIP_Y.rotateY(bodyRotation * Mth.DEG_TO_RAD + Mth.PI, new org.joml.Quaternionf()));
        poseStack.scale(CrownOfKingSlimeModel.RENDER_SCALE, CrownOfKingSlimeModel.RENDER_SCALE, CrownOfKingSlimeModel.RENDER_SCALE);
        crownModel.renderToBuffer(poseStack, buffer.getBuffer(CrownOfKingSlimeModel.RENDER_TYPE), packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
        super.render(slime, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    protected void scale(KingSlime slime, PoseStack poseStack, float partialTick) {
        float visualScale = slime.getVisualScale(partialTick);
        float squish = Mth.lerp(partialTick, slime.getOldSquish(), slime.getSquish())
                / (visualScale * 0.5F + 1.0F);
        float inverse = 1.0F / (squish + 1.0F);
        poseStack.scale(inverse * visualScale, visualScale / inverse, inverse * visualScale);
        shadowRadius = visualScale * 0.25F;
    }
}
