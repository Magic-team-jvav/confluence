package org.confluence.mod.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import org.confluence.mod.common.entity.MartianReinforcement;

public final class MartianReinforcementRenderer extends EntityRenderer<MartianReinforcement> {
    private static final int COLOR = 0xFFFF0808;

    public MartianReinforcementRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(MartianReinforcement entity, float yaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int light) {
        if (!entity.isFalling()) {
            poseStack.pushPose();
            poseStack.translate(-0.5, 0, -0.5);
            BeaconRenderer.renderBeaconBeam(poseStack, buffer, BeaconRenderer.BEAM_LOCATION, partialTick,
                    1.0F, entity.level().getGameTime(), 0, 64, COLOR, 0.09F, 0.2F);
            poseStack.popPose();
        }
    }

    @Override
    public boolean shouldRender(MartianReinforcement entity, Frustum frustum, double x, double y, double z) {
        return !entity.isFalling() && entity.distanceToSqr(x, y, z) < 256 * 256;
    }

    @Override
    public ResourceLocation getTextureLocation(MartianReinforcement entity) {
        return BeaconRenderer.BEAM_LOCATION;
    }
}
