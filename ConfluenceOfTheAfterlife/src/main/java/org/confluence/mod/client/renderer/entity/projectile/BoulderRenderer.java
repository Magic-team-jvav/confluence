package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.BoulderEntity;

public class BoulderRenderer extends EntityRenderer<BoulderEntity> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/normal_boulder.png");
    private final BlockRenderDispatcher dispatcher;

    public BoulderRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.dispatcher = pContext.getBlockRenderDispatcher();
    }

    @Override
    public ResourceLocation getTextureLocation(BoulderEntity pEntity) {
        return TEXTURE;
    }

    @Override
    public void render(BoulderEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        pPoseStack.mulPose(Axis.YP.rotationDegrees(pEntity.getYRot() - 90.0F));
        pPoseStack.translate(0.0F, 0.5F, 0.0F);
        pPoseStack.mulPose(Axis.ZP.rotation(-Mth.lerp(pPartialTick, pEntity.rotateO, pEntity.rotate)));
        pPoseStack.translate(-0.5F, -0.5F, -0.5F);
        dispatcher.renderSingleBlock(pEntity.getBlockState(), pPoseStack, pBuffer, pPackedLight, OverlayTexture.NO_OVERLAY);
        pPoseStack.popPose();
    }
}
