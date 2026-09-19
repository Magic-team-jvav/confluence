package org.confluence.mod.client.summoner.renderer.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.EntityType;
import org.confluence.mod.client.summoner.AbstractAttachmentEntityRenderer;
import org.confluence.mod.client.summoner.RenderContext;
import org.confluence.mod.client.summoner.model.LyraModelRenderer;
import org.confluence.mod.client.summoner.model.virtual.VirtualEntityPose;
import org.confluence.mod.client.summoner.trail.ModelConfig;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.projectile.ImpFireball;

public class ImpFireballRenderer extends AbstractAttachmentEntityRenderer<ImpFireball> {

    @Override
    protected RenderContext<ImpFireball> createContext(ImpFireball fireball, float partialTick) {
        return RenderContext.<ImpFireball>builder()
                .model(new ModelConfig<ImpFireball>().alphaDistanceFactor(1.5F))
                .build();
    }

    @Override
    protected void render(ImpFireball fireball, PoseStack poseStack, MultiBufferSource bufferSource, PathNode visualNode, RenderContext<ImpFireball> context, float partialTick, int packedLight, float alpha) {
        LyraModelRenderer.virtualEntity(EntityType.SMALL_FIREBALL, partialTick)
                .pose(VirtualEntityPose.create().packedLight(packedLight).alpha(alpha))
                .render(poseStack, bufferSource);
    }
}
