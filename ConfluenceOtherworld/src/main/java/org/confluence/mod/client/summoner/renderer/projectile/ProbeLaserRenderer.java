package org.confluence.mod.client.summoner.renderer.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.AABB;
import org.confluence.mod.client.summoner.AbstractAttachmentEntityRenderer;
import org.confluence.mod.client.summoner.rendererHelper.LaserRendererHelper;
import org.confluence.mod.common.summoner.projectile.ProbeLaser;

public class ProbeLaserRenderer extends AbstractAttachmentEntityRenderer<ProbeLaser> {
    @Override
    protected void render(PoseStack poseStack, MultiBufferSource bufferSource) {
        AABB hitbox = context.entity.getHitbox();
        float radius = (float) hitbox.getXsize() * 0.5f;
        LaserRendererHelper.builder()
                .length((float) hitbox.getZsize())
                .radius(radius, radius)
                .layers(4)
                .segments(4)
                .color(0x3399FF)
                .alpha(0.95f)
                .innerRatio(0.15f)
                .render(poseStack, bufferSource);
    }
}
