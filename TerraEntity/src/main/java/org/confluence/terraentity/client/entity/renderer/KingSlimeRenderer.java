package org.confluence.terraentity.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Slime;

import org.confluence.terraentity.client.entity.model.CrownOfKingSlimeModel;
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
        pPoseStack.translate(0.0F, 1.0625F + pEntity.getDimensions(pEntity.getPose()).height(), 0.0F);
        float f = Mth.lerp(pPartialTicks, pEntity.yBodyRotO, pEntity.yBodyRot);
        pPoseStack.mulPose(CrownOfKingSlimeModelRenderer.FLIP_Y.rotateY(f * Mth.DEG_TO_RAD + Mth.PI, new Quaternionf()));
        pPoseStack.translate(0,1,0);
        model.renderToBuffer(pPoseStack, pBuffer.getBuffer(CrownOfKingSlimeModel.RENDER_TYPE), pPackedLight, OverlayTexture.NO_OVERLAY);
        pPoseStack.popPose();

        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }
}
