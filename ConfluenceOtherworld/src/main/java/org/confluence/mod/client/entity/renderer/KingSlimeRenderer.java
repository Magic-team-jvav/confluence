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
/// 1.21 侧使用原版史莱姆的内核、面部和半透明外壳，再在顶部单独绘制王冠。1.20
/// 重写后的实体不再继承原版史莱姆，因此这里复用本体已有的史莱姆模型，并直接读取同步的
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
        poseStack.translate(0.0F, 0.9F + slime.getDimensions(slime.getPose()).height, 0.0F);
        float bodyRotation = Mth.lerp(partialTick, slime.yBodyRotO, slime.yBodyRot);
        poseStack.mulPose(CrownOfKingSlimeModelRenderer.FLIP_Y.rotateY(bodyRotation * Mth.DEG_TO_RAD + Mth.PI, new org.joml.Quaternionf()));
        poseStack.translate(0.0F, 1.0F, 0.0F);
        crownModel.renderToBuffer(poseStack, buffer.getBuffer(CrownOfKingSlimeModel.RENDER_TYPE), packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
        super.render(slime, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    protected void scale(KingSlime slime, PoseStack poseStack, float partialTick) {
        float visualSize = slime.getVisualSize(partialTick);
        float squish = Mth.lerp(partialTick, slime.getOldSquish(), slime.getSquish())
                / (visualSize * 0.5F + 1.0F);
        float inverse = 1.0F / (squish + 1.0F);
        poseStack.scale(inverse * visualSize, visualSize / inverse, inverse * visualSize);
        shadowRadius = visualSize * 0.25F;
    }
}
