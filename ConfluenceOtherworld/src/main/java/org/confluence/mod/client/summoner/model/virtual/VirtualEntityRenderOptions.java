package org.confluence.mod.client.summoner.model.virtual;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.EntityType;

import java.util.function.Consumer;

/**
 * Strongly typed request for rendering a registered entity renderer without
 * adding a real entity to the client level.
 */
public final class VirtualEntityRenderOptions {

    private final EntityType<?> entityType;
    private final float partialTick;
    private VirtualEntityPose pose = VirtualEntityPose.create();

    public VirtualEntityRenderOptions(EntityType<?> entityType, float partialTick) {
        this.entityType = entityType;
        this.partialTick = partialTick;
    }

    public VirtualEntityRenderOptions pose(VirtualEntityPose pose) {
        if (pose != null) {
            this.pose = pose;
        }
        return this;
    }

    public VirtualEntityRenderOptions pose(Consumer<VirtualEntityPose> configurator) {
        if (configurator != null) {
            configurator.accept(pose);
        }
        return this;
    }

    public boolean render(PoseStack poseStack, MultiBufferSource bufferSource) {
        return VirtualEntityRenderer.render(entityType, poseStack, bufferSource, partialTick, pose);
    }

}
