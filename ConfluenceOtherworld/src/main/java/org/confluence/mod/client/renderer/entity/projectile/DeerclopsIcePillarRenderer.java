package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import org.confluence.mod.common.entity.projectile.DeerclopsIcePillarProjectile;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/// 逐段升起并收回蓝冰方块，表现独眼巨鹿的地面冰柱。
public final class DeerclopsIcePillarRenderer extends EntityRenderer<DeerclopsIcePillarProjectile> {
    public DeerclopsIcePillarRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(DeerclopsIcePillarProjectile entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    public void render(DeerclopsIcePillarProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(-0.5F, -0.5F, -0.5F);
        poseStack.mulPose(new Quaternionf().rotationTo(new Vector3f(0.0F, 1.0F, 0.0F), entity.getAxis()));
        for (int blockIndex = 0; blockIndex < entity.getVisibleBlockCount(); blockIndex++) {
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(Blocks.BLUE_ICE.defaultBlockState(), poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);
            poseStack.translate(0.0F, 1.0F, 0.0F);
        }
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
