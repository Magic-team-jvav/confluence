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
import org.confluence.mod.common.entity.projectile.DeerclopsThrownIceProjectile;
import org.joml.Quaternionf;

/// 以蓝冰方块表现独眼巨鹿抛出的旋转冰块。
public final class DeerclopsThrownIceRenderer extends EntityRenderer<DeerclopsThrownIceProjectile> {
    public DeerclopsThrownIceRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(DeerclopsThrownIceProjectile entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    public void render(DeerclopsThrownIceProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        float angle = (entity.tickCount + partialTick) * entity.getRotationSpeed();
        poseStack.mulPose(new Quaternionf().rotationAxis(angle, entity.getRotationAxis()));
        poseStack.translate(-0.5F, -0.5F, -0.5F);
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(Blocks.BLUE_ICE.defaultBlockState(), poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
