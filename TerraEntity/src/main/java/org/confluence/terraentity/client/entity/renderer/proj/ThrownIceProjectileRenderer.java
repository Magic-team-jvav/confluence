package org.confluence.terraentity.client.entity.renderer.proj;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import org.confluence.terraentity.entity.proj.ThrownIceProjectile;

public class ThrownIceProjectileRenderer  extends EntityRenderer<ThrownIceProjectile> {

    public ThrownIceProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(ThrownIceProjectile entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }


    @Override
    public void render(ThrownIceProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {

        poseStack.pushPose();
        poseStack.mulPose(Axis.of(entity.axis).rotation((entity.tickCount + partialTick) * entity.rotSpeed));
        poseStack.translate(-0.5f, -0.5f, -0.5f);
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(Blocks.BLUE_ICE.defaultBlockState(), poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

    }
}
