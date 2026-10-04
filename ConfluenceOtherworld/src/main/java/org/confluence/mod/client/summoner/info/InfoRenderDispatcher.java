package org.confluence.mod.client.summoner.info;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summoner.attachment.InfoData;
import org.confluence.mod.common.summoner.register.SummonerAttachmentTypes;
import org.joml.Quaternionf;

public final class InfoRenderDispatcher {

    private InfoRenderDispatcher() {}

    public static void render(Level level, Camera camera, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick) {
        InfoData data = level.getData(SummonerAttachmentTypes.INFO);
        if (!data.isEmpty()) {
            Vec3 camPos = camera.getPosition();
            Quaternionf baseRotation = new Quaternionf(camera.rotation()).mul(Axis.XN.rotationDegrees(180.0F), new Quaternionf());
            if (!data.getNumbers().isEmpty()) {
                VertexConsumer consumer = bufferSource.getBuffer(NumberInfo.renderType());
                for (NumberInfo info : data.getNumbers()) {
                    info.render(consumer, poseStack, baseRotation, camPos, partialTick);
                }
            }
            for (TextInfo info : data.getTexts()) {
                info.render(bufferSource, poseStack, baseRotation, camPos, partialTick);
            }
        }
    }
}
