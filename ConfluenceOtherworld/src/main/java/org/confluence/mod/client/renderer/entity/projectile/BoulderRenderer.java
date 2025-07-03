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
import org.confluence.mod.common.entity.projectile.boulder.BoulderEntity;

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
    public void render(BoulderEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(entity.getYRot() - 90.0F));
        float radius = entity.radius;
        poseStack.translate(0, radius, 0);
        poseStack.mulPose(Axis.ZP.rotation(-Mth.lerp(partialTick, entity.rotateO, entity.rotate)));
        poseStack.translate(-radius, -radius, -radius);
        if (radius != 0.5F) {
            float scale = radius * 2;
            poseStack.scale(scale, scale, scale);
        }
        dispatcher.renderSingleBlock(entity.getBlockState(), poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}
