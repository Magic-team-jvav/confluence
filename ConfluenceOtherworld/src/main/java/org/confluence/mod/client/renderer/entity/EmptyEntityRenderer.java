package org.confluence.mod.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.client.render.visual_effects.ThunderboltVFX;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.entity.EmptyEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.confluence.lib.util.VectorUtils.lightningPathList;

public class EmptyEntityRenderer extends EntityRenderer<EmptyEntity> {
    private final ThunderboltVFX thunderboltVFX;

    public EmptyEntityRenderer(final EntityRendererProvider.Context context) {
        super(context);
        this.thunderboltVFX = new ThunderboltVFX();
    }


    @Override
    public void render(final EmptyEntity entity, final float entityYaw, final float partialTick, final PoseStack poseStack, final MultiBufferSource bufferSource, final int packedLight) {
        this.thunderboltVFX.render(entity.getPosition(partialTick), entity.getRandom(), poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(final EmptyEntity entity) {
        return ResourceLocation.parse("textures/block/white_concrete.png");
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
