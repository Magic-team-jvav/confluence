package org.confluence.mod.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.client.effect.RenderStateShardAccessor;
import org.confluence.mod.client.summoner.rendererHelper.LaserRendererHelper;
import org.confluence.mod.common.entity.boss.DestroyerSideBeams;
import org.confluence.mod.common.entity.boss.TheDestroyer;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ThreadLocalRandom;

/// 毁灭者大激光的三层六棱台，方向与头部姿态及其余粒子一致。
public final class DestroyerBeamRenderer {
    private static final float BEAM_LENGTH = (float) TheDestroyer.LASER_RANGE;
    private static final float FADE_LENGTH = BEAM_LENGTH / 8.0F;
    private static final Map<TheDestroyer, Integer> ACTIVE_BEAMS = new WeakHashMap<>();

    private DestroyerBeamRenderer() {}

    public static void render(TheDestroyer boss, PoseStack poses, MultiBufferSource buffers, Vec3 camera, float partialTick) {
        renderSideBeams(boss, poses, buffers, camera, partialTick);
        int tick = boss.getBeamTick();
        if (tick < 220 || tick >= 424) {
            ACTIVE_BEAMS.remove(boss);
            return;
        }
        int frame = ACTIVE_BEAMS.getOrDefault(boss, tick < 222 ? 0 : 4);
        boolean firstFrame = frame == 0 && tick < 222;

        Vector3f forward = boss.getBody().getRenderRotation(0, partialTick).transform(new Vector3f(0, 0, -1));
        Vec3 direction = new Vec3(forward.x(), forward.y(), forward.z()).normalize();
        Vec3 origin = boss.getBody().getRenderPosition(0, partialTick)
                .add(0, TheDestroyer.BODY_HEIGHT / 2.0, 0).add(direction.scale(10));
        Vec3 end = origin.add(direction.scale(BEAM_LENGTH));
        Vec3 hit = boss.level().clip(new ClipContext(origin, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, boss)).getLocation();
        float length = (float) origin.distanceTo(hit);
        if (length <= 0.01F) return;

        float time = tick + partialTick;
        float opening = Mth.clamp((time - 220.0F) / 2.0F, 0.0F, 1.0F);
        float grow = firstFrame ? 1.0F / 3.0F : 1.0F / 3.0F + 2.0F / 3.0F * (1.0F - (1.0F - opening) * (1.0F - opening));
        float retract = Mth.clamp((time - 420.0F) / 4.0F, 0.0F, 1.0F);
        float visibleEnd = length * grow;
        float visibleStart = length * retract;
        if (visibleEnd <= visibleStart) return;
        ACTIVE_BEAMS.put(boss, Math.min(frame + 1, 4));

        // 只有首尾顶点每帧取随机偏移；三层共享偏移，截面之间始终线性插值。
        float[][][] offsets = LaserRendererHelper.randomEndpointOffsets(6, 0.25F, ThreadLocalRandom.current());
        float pulse = 1.0F + 0.06F * Mth.sin(time * 0.15F);
        // 首帧的截面顶角为 60 度，之后按反比例迅速收束到正常末端半径。
        float convergenceProgress = frame / 4.0F;
        float convergence = (1.0F / (1.0F + 12.0F * convergenceProgress) - 1.0F / 13.0F) * 13.0F / 12.0F;
        float startRadius = 3.0F * pulse;
        float endRadius = Mth.lerp(convergence, 4.5F * pulse, startRadius + length * 0.577350269F);

        poses.pushPose();
        poses.translate(origin.x - camera.x, origin.y - camera.y, origin.z - camera.z);
        poses.mulPose(new Quaternionf().rotationTo(new Vector3f(0, 1, 0), new Vector3f((float) direction.x, (float) direction.y, (float) direction.z)));
        Matrix4f matrix = poses.last().pose();
        drawBeam(buffers, matrix, length, visibleStart, visibleEnd, startRadius, endRadius, offsets);
        poses.popPose();
    }

    private static void renderSideBeams(TheDestroyer boss, PoseStack poses, MultiBufferSource buffers, Vec3 camera, float partialTick) {
        int tick = boss.getBeamTick();
        if (tick < 100 || tick >= 424) return;
        for (int ordinal = 0; ordinal < DestroyerSideBeams.COUNT; ordinal++) {
            int startTick = DestroyerSideBeams.startTick(boss, ordinal);
            if (tick < startTick || tick >= startTick + 204) continue;
            Vec3 origin = DestroyerSideBeams.origin(boss, ordinal, partialTick);
            Vec3 direction = DestroyerSideBeams.direction(boss, ordinal, partialTick);
            Vec3 end = origin.add(direction.scale(BEAM_LENGTH));
            Vec3 hit = boss.level().clip(new ClipContext(origin, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, boss)).getLocation();
            float length = (float) origin.distanceTo(hit);
            if (length <= 0.01F) continue;
            float time = tick + partialTick;
            float opening = Mth.clamp((time - startTick) / 2.0F, 0.0F, 1.0F);
            float visibleEnd = length * (1.0F / 3.0F + 2.0F / 3.0F * (1.0F - (1.0F - opening) * (1.0F - opening)));
            float visibleStart = length * Mth.clamp((time - (startTick + 200.0F)) / 4.0F, 0.0F, 1.0F);
            if (visibleEnd <= visibleStart) continue;
            float[][][] offsets = LaserRendererHelper.randomEndpointOffsets(6, 0.25F, ThreadLocalRandom.current());
            float pulse = 1.0F + 0.06F * Mth.sin(time * 0.15F);
            float startRadius = 1.5F * pulse, endRadius = 2.25F * pulse;
            poses.pushPose();
            poses.translate(origin.x - camera.x, origin.y - camera.y, origin.z - camera.z);
            poses.mulPose(new Quaternionf().rotationTo(new Vector3f(0, 1, 0), new Vector3f((float) direction.x, (float) direction.y, (float) direction.z)));
            Matrix4f matrix = poses.last().pose();
            drawBeam(buffers, matrix, length, visibleStart, visibleEnd, startRadius, endRadius, offsets);
            poses.popPose();
        }
    }

    private static void drawBeam(MultiBufferSource buffers, Matrix4f matrix, float length, float visibleStart, float visibleEnd,
                                 float startRadius, float endRadius, float[][][] offsets) {
        VertexConsumer outer = buffers.getBuffer(RenderStateShardAccessor.TRANSLUCENT_LASER);
        LaserRendererHelper.renderPrismLayer(outer, matrix, length, visibleStart, visibleEnd, startRadius, endRadius, offsets, FADE_LENGTH, 255, 0, 0, 125, true);
        LaserRendererHelper.renderPrismLayer(outer, matrix, length, visibleStart, visibleEnd, startRadius * (2.0F / 3.0F), endRadius * (2.0F / 3.0F), offsets, FADE_LENGTH, 255, 110, 110, 185, true);
        VertexConsumer core = buffers.getBuffer(RenderStateShardAccessor.TRANSLUCENT_LASER_NO_CULL);
        LaserRendererHelper.renderPrismLayer(core, matrix, length, visibleStart, visibleEnd, startRadius * (0.53F / 1.35F), endRadius * (0.53F / 1.35F), offsets, FADE_LENGTH, 255, 255, 255, 255, false);
    }


}
