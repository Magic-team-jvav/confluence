package org.confluence.mod.client.summoner.renderer.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.client.summoner.AbstractAttachmentEntityRenderer;
import org.confluence.mod.client.summoner.ModelContext;
import org.confluence.mod.client.summoner.RenderContext;
import org.confluence.mod.client.summoner.model.LyraModelRenderer;
import org.confluence.mod.client.summoner.model.virtual.VirtualEntityPose;
import org.confluence.mod.common.entity.projectile.HornetStingerProjectile;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.projectile.HornetStinger;

public class HornetStingerRenderer extends AbstractAttachmentEntityRenderer<HornetStinger> {

    @Override
    protected RenderContext<HornetStinger> createContext(HornetStinger stinger, PathNode visualNode, float partialTick, int packedLight) {
        return new RenderContext<>(stinger, visualNode, partialTick, packedLight)
                .model(new ModelContext()
                        .scale(0.6F)
                        .translateOffset(0, -0.45F, 0.3F)
                        .rotationOffset(180, 0, 0)
                        .alphaDistanceFactor(1.5F));
    }

    @Override
    protected void renderModel(PoseStack poseStack, MultiBufferSource bufferSource) {
        LyraModelRenderer.virtualEntity(ModEntities.HORNET_STINGER.get(), context.partialTick)
                .pose(VirtualEntityPose.create()
                        .packedLight(context.packedLight)
                        .alpha(context.color.getAlphaFloat())
                        .customize(entity -> {
                            if (entity instanceof HornetStingerProjectile projectile) {
                                projectile.setDeltaMovement(Vec3.ZERO);
                            }
                        }))
                .render(poseStack, bufferSource);
    }
}
