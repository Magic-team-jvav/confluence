package org.confluence.terraentity.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.confluence.terraentity.client.entity.model.CrownOfKingSlimeModel;
import org.confluence.terraentity.entity.model.CrownOfKingSlimeModelEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public class CrownOfKingSlimeModelRenderer extends EntityRenderer<CrownOfKingSlimeModelEntity> {
    public static final Quaternionf FLIP_Y = Axis.ZP.rotation(Mth.PI);
    private final CrownOfKingSlimeModel model;

    public CrownOfKingSlimeModelRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new CrownOfKingSlimeModel(pContext.bakeLayer(CrownOfKingSlimeModel.LAYER_LOCATION));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull CrownOfKingSlimeModelEntity pEntity) {
        return CrownOfKingSlimeModel.TEXTURE;
    }

    @Override
    public void render(@NotNull CrownOfKingSlimeModelEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        pPoseStack.mulPose(Axis.YP.rotation(Mth.lerp(pPartialTick, pEntity.rotateO2, pEntity.rotate2)));
        pPoseStack.translate(0.0F, -0.5F, pEntity.radius);
        pPoseStack.mulPose(pEntity.quaternionf.rotationXYZ(
                Mth.lerp(pPartialTick, pEntity.rotO.x, pEntity.rot.x),
                Mth.lerp(pPartialTick, pEntity.rotO.y, pEntity.rot.y),
                Mth.lerp(pPartialTick, pEntity.rotO.z, pEntity.rot.z)
        ));
        pPoseStack.mulPose(Axis.YN.rotation(Mth.lerp(pPartialTick, pEntity.rotateO1, pEntity.rotate1)));
        pPoseStack.translate(0.0F, 1.9375F + pEntity.height, 0.0F);
        pPoseStack.mulPose(FLIP_Y);
        model.renderToBuffer(pPoseStack, pBuffer.getBuffer(CrownOfKingSlimeModel.RENDER_TYPE), pPackedLight, OverlayTexture.NO_OVERLAY);
        pPoseStack.popPose();
    }

    @Override
    public boolean shouldRender(@NotNull CrownOfKingSlimeModelEntity pLivingEntity, @NotNull Frustum pCamera, double pCamX, double pCamY, double pCamZ) {
        return true;
    }
}
