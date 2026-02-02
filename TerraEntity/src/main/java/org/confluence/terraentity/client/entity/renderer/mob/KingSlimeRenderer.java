package org.confluence.terraentity.client.entity.renderer.mob;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Slime;
import org.confluence.terraentity.client.entity.model.CrownOfKingSlimeModel;
import org.confluence.terraentity.client.entity.renderer.CrownOfKingSlimeModelRenderer;
import org.confluence.terraentity.entity.boss.KingSlime;
import org.joml.Quaternionf;

public class KingSlimeRenderer extends CustomSlimeRenderer {
    private final CrownOfKingSlimeModel model;

    public KingSlimeRenderer(EntityRendererProvider.Context context) {
        super(context, "king");
        this.model = new CrownOfKingSlimeModel(context.bakeLayer(CrownOfKingSlimeModel.LAYER_LOCATION));
    }

    @Override
    public void render(Slime pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        float offset = pEntity.getType().getDimensions().scale(((KingSlime)pEntity).getClientSize(pPartialTicks)).height();
        pPoseStack.translate(0.0F, 0.9f + offset, 0.0F);
        float f = Mth.lerp(pPartialTicks, pEntity.yBodyRotO, pEntity.yBodyRot);
        pPoseStack.mulPose(CrownOfKingSlimeModelRenderer.FLIP_Y.rotateY(f * Mth.DEG_TO_RAD + Mth.PI, new Quaternionf()));
        pPoseStack.translate(0,1,0);
        model.renderToBuffer(pPoseStack, pBuffer.getBuffer(CrownOfKingSlimeModel.RENDER_TYPE), pPackedLight, OverlayTexture.NO_OVERLAY);
        pPoseStack.popPose();

        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    protected void scale(Slime livingEntity, PoseStack poseStack, float partialTickTime) {
        float f = 0.999F;
        poseStack.scale(f,f,f);
        poseStack.translate(0.0F, 0.001F, 0.0F);
        float f1 = ((KingSlime)livingEntity).getClientSize(partialTickTime);
        float f2 = Mth.lerp(partialTickTime, livingEntity.oSquish, livingEntity.squish) / (f1 * 0.5F + 1.0F);
        float f3 = 1.0F / (f2 + 1.0F);
        poseStack.scale(f3 * f1, 1.0F / f3 * f1, f3 * f1);
    }
}
