package org.confluence.mod.client.summoner.renderer.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import org.confluence.mod.client.effect.ModRenderTypes;
import org.confluence.mod.client.summoner.AbstractAttachmentEntityRenderer;
import org.confluence.mod.client.summoner.ModelContext;
import org.confluence.mod.client.summoner.RenderContext;
import org.confluence.mod.client.summoner.rendererHelper.LaserRendererHelper;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.projectile.DestructionOmen;
import org.joml.Matrix4f;

import java.util.concurrent.ThreadLocalRandom;

public class DestructionOmenRenderer extends AbstractAttachmentEntityRenderer<DestructionOmen> {
    @Override
    protected RenderContext<DestructionOmen> createContext(DestructionOmen entity, PathNode visualNode, float partialTick, int packedLight) {
        return super.createContext(entity, visualNode, partialTick, packedLight)
                .model(new ModelContext().alphaDistanceFactor(0));
    }

    @Override
    protected void render(PoseStack poseStack, MultiBufferSource bufferSource) {
        DestructionOmen beam = context.entity;
        AABB hitbox = beam.getHitbox();
        float length = (float) hitbox.getZsize();
        float radius = (float) hitbox.getXsize() * 0.5F;
        float age = beam.getTickCount() + context.partialTick;
        float opening = Mth.clamp(age / 2, 0, 1);
        float end = length * (1 - 2.0F / 3.0F * (1 - opening) * (1 - opening));
        float start = length * Mth.clamp((age - beam.getMaxTickCount() + 4) / 4, 0, 1);
        if (end > start) {
            float pulse = (1 + 0.06F * Mth.sin(age * 0.15F)) / 1.06F;
            float[][][] offsets = LaserRendererHelper.randomEndpointOffsets(6, radius / 18, ThreadLocalRandom.current());
            for (float[][] endpoint : offsets) {
                for (float[] offset : endpoint) {
                    offset[1] = 0;
                }
            }
            float endRadius = (radius - radius / 12) * pulse;
            float startRadius = endRadius * (2.0F / 3.0F);
            float fadeLength = length / 8;
            poseStack.pushPose();
            poseStack.mulPose(Axis.XP.rotationDegrees(90));
            Matrix4f matrix = poseStack.last().pose();
            VertexConsumer outer = bufferSource.getBuffer(ModRenderTypes.TRANSLUCENT_LASER);
            LaserRendererHelper.renderPrismLayer(outer, matrix, length, start, end, startRadius, endRadius, offsets, fadeLength, 0, 90, 255, 125, true);
            LaserRendererHelper.renderPrismLayer(outer, matrix, length, start, end, startRadius * (2.0F / 3.0F), endRadius * (2.0F / 3.0F), offsets, fadeLength, 110, 185, 255, 185, true);
            VertexConsumer core = bufferSource.getBuffer(ModRenderTypes.TRANSLUCENT_LASER_NO_CULL);
            LaserRendererHelper.renderPrismLayer(core, matrix, length, start, end, startRadius * (0.53F / 1.35F), endRadius * (0.53F / 1.35F), offsets, fadeLength, 255, 255, 255, 255, false);
            poseStack.popPose();
        }
    }
}
