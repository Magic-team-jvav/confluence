package org.confluence.mod.client.renderer.entity;

import PortLib.extensions.net.minecraft.world.entity.Entity.PortEntityExtension;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import org.confluence.lib.client.render.visual_effects.ThunderboltVFX;
import org.confluence.lib.common.entitiy.EmptyEntity;

public class EmptyEntityRenderer extends EntityRenderer<EmptyEntity> {
    private final ThunderboltVFX thunderboltVFX;

    public EmptyEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.thunderboltVFX = new ThunderboltVFX();
    }

    @Override
    public void render(EmptyEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        this.thunderboltVFX.render(entity.getPosition(partialTick), PortEntityExtension.getRandom(entity), poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(EmptyEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    public boolean shouldRender(EmptyEntity entity, Frustum frustum, double camX, double camY, double camZ) {
        if (super.shouldRender(entity, frustum, camX, camY, camZ)) {
            return true;
        }

        double maxLightningDistance = 55.0;
        AABB extendedBox = entity.getBoundingBox().inflate(maxLightningDistance);

        return frustum.isVisible(extendedBox);
    }
}
