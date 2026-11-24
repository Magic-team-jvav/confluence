package org.confluence.mod.client.summoner.renderer.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.EntityType;
import org.confluence.mod.client.summoner.AbstractAttachmentEntityRenderer;
import org.confluence.mod.client.summoner.ModelContext;
import org.confluence.mod.client.summoner.RenderContext;
import org.confluence.mod.client.summoner.model.virtual.VirtualEntityPose;
import org.confluence.mod.client.summoner.model.virtual.VirtualEntityRenderer;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.projectile.ImpFireball;

public class ImpFireballRenderer extends AbstractAttachmentEntityRenderer<ImpFireball> {

    @Override
    protected RenderContext<ImpFireball> createContext(ImpFireball fireball, PathNode visualNode, float partialTick, int packedLight) {
        return new RenderContext<>(fireball, visualNode, partialTick, packedLight)
                .model(new ModelContext().alphaDistanceFactor(1.5F));
    }

    @Override
    protected void renderModel(PoseStack poseStack, MultiBufferSource bufferSource) {
        VirtualEntityRenderer.render(EntityType.SMALL_FIREBALL, poseStack, bufferSource, context.partialTick, VirtualEntityPose.create().packedLight(context.packedLight).alpha(context.color.getAlphaFloat()));
    }
}
