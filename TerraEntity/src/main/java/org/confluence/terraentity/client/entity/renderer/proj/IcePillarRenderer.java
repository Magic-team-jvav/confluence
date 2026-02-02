package org.confluence.terraentity.client.entity.renderer.proj;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import org.confluence.terraentity.entity.proj.IcePillar;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class IcePillarRenderer extends EntityRenderer<IcePillar> {

    public IcePillarRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(IcePillar entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }


    @Override
    public void render(IcePillar entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {

        poseStack.pushPose();
//        poseStack.mulPose(Axis.of(entity.axis).rotation((entity.tickCount + partialTick) * entity.rotSpeed));
        poseStack.translate(-0.5f, -0.5f, -0.5f);
        poseStack.mulPose(new Quaternionf().rotateTo(new Vector3f(0,1,0), entity.axis));

        for (int i = 0; i < Math.min(4, entity.tickCount * (entity.getLifetime() - entity.tickCount) / 75); i++) {

            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(Blocks.BLUE_ICE.defaultBlockState(), poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);
            poseStack.translate(0,1,0);
        }
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

    }
}
