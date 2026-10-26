package org.confluence.mod.client.summoner;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class AfterimageManager {

    private static final List<Image> IMAGES = new ArrayList<>();
    private static final Map<RenderType, BufferBuilder> MEMORY = new IdentityHashMap<>();
    private static final Matrix4f INVERSE_POSE = new Matrix4f();
    private static final Matrix3f INVERSE_NORMAL = new Matrix3f();
    private static RenderContext<?> context;
    private static Vec3 cameraPosition;
    private static double time;
    private static boolean recording;
    private static boolean capturing;

    private AfterimageManager() {
    }

    static RenderContext<?> setContext(RenderContext<?> value) {
        RenderContext<?> previous = context;
        context = value;
        return previous;
    }

    public static void renderModel(PoseStack poseStack, MultiBufferSource original, float alpha, Consumer<MultiBufferSource> draw) {
        RenderContext<?> current = context;
        AfterimageContext settings = current == null ? null : current.afterimage;
        if (settings != null && settings.lifetime > 0 && settings.alpha > 0 && settings.color.getAlphaFloat() > 0 && recording && !capturing && !AttachmentEntityRenderDispatcher.isShadowPass()) {
            int color = settings.color.argbInt();
            float opacity = settings.alpha * settings.color.getAlphaFloat();
            int lifetime = settings.lifetime;
            Vec3 position = current.visualNode.pos();
            WholeModelTransparency.render(poseStack, original, alpha, source -> {
                Capture capture = new Capture(source);
                capturing = true;
                draw.accept(capture);
                capturing = false;
                List<Mesh> meshes = new ArrayList<>(capture.builders.size());
                // 网格以世界锚点为原点保存，移除写入时的摄像机变换。
                Matrix4f toWorld = new Matrix4f().translation((float) (cameraPosition.x - position.x),
                        (float) (cameraPosition.y - position.y), (float) (cameraPosition.z - position.z)).mul(INVERSE_POSE);
                for (var entry : capture.builders.entrySet()) {
                    BufferBuilder.RenderedBuffer mesh = entry.getValue().endOrDiscardIfEmpty();
                    if (mesh != null) {
                        transform(mesh, toWorld, INVERSE_NORMAL);
                        byte[] data = new byte[mesh.vertexBuffer().remaining()];
                        mesh.vertexBuffer().get(data);
                        meshes.add(new Mesh(entry.getKey(), mesh.drawState(), data));
                        mesh.release();
                    }
                }
                if (!meshes.isEmpty()) {
                    IMAGES.add(new Image(position, meshes, color, opacity, lifetime, time));
                }
            });
        } else {
            WholeModelTransparency.render(poseStack, original, alpha, draw);
        }
    }

    public static void render(ClientLevel level, Camera camera, PoseStack poseStack, float partialTick) {
        if (!AttachmentEntityRenderDispatcher.isShadowPass()) {
            double frameTime = level.getGameTime() + (double) partialTick;
            recording = cameraPosition == null || frameTime != time;
            cameraPosition = camera.getPosition();
            time = frameTime;
            INVERSE_POSE.set(poseStack.last().pose()).invert();
            INVERSE_NORMAL.set(poseStack.last().normal()).invert();
            var images = IMAGES.iterator();
            while (images.hasNext()) {
                Image image = images.next();
                double age = time - image.born;
                if (age >= image.lifetime) {
                    images.remove();
                } else {
                    float progress = (float) (age / image.lifetime);
                    float alpha = image.alpha * (1f - progress);
                    float scale = 0.97f - 0.02f * progress;
                    poseStack.pushPose();
                    poseStack.translate(image.position.x - cameraPosition.x, image.position.y - cameraPosition.y, image.position.z - cameraPosition.z);
                    poseStack.scale(scale, scale, scale);
                    WholeModelTransparency.renderAfterimage(poseStack, image.meshes, alpha, image.color);
                    poseStack.popPose();
                }
            }
        }
    }

    static void transform(BufferBuilder.RenderedBuffer mesh, Matrix4f pose, Matrix3f normal) {
        var state = mesh.drawState();
        var format = state.format();
        long address = MemoryUtil.memAddress(mesh.vertexBuffer());
        int positionOffset = WholeModelTransparency.offset(format, VertexFormatElement.Usage.POSITION);
        int normalOffset = WholeModelTransparency.offset(format, VertexFormatElement.Usage.NORMAL);
        Vector3f value = new Vector3f();
        for (int i = 0; i < state.vertexCount(); i++) {
            long p = address + positionOffset;
            pose.transformPosition(MemoryUtil.memGetFloat(p), MemoryUtil.memGetFloat(p + 4), MemoryUtil.memGetFloat(p + 8), value);
            MemoryUtil.memPutFloat(p, value.x);
            MemoryUtil.memPutFloat(p + 4, value.y);
            MemoryUtil.memPutFloat(p + 8, value.z);
            if (normalOffset >= 0) {
                long n = address + normalOffset;
                normal.transform(MemoryUtil.memGetByte(n) / 127f, MemoryUtil.memGetByte(n + 1) / 127f, MemoryUtil.memGetByte(n + 2) / 127f, value);
                MemoryUtil.memPutByte(n, (byte) Mth.clamp(Math.round(value.x * 127), -127, 127));
                MemoryUtil.memPutByte(n + 1, (byte) Mth.clamp(Math.round(value.y * 127), -127, 127));
                MemoryUtil.memPutByte(n + 2, (byte) Mth.clamp(Math.round(value.z * 127), -127, 127));
            }
            address += format.getVertexSize();
        }
    }

    public static void close() {
        IMAGES.clear();
        MEMORY.values().forEach(WholeModelTransparency::release);
        MEMORY.clear();
        context = null;
        cameraPosition = null;
        recording = false;
        capturing = false;
    }

    record Mesh(RenderType type, BufferBuilder.DrawState state, byte[] vertices) {
    }

    private record Image(Vec3 position, List<Mesh> meshes, int color, float alpha, int lifetime, double born) {
    }

    private static class Capture implements MultiBufferSource {
        final MultiBufferSource original;
        final Map<RenderType, BufferBuilder> builders = new LinkedHashMap<>();

        Capture(MultiBufferSource original) {
            this.original = original;
        }

        @Override
        public @NotNull VertexConsumer getBuffer(@NotNull RenderType type) {
            BufferBuilder builder = builders.computeIfAbsent(type, key -> {
                BufferBuilder memory = MEMORY.computeIfAbsent(key, ignored -> new BufferBuilder(Math.max(16384, key.bufferSize())));
                memory.begin(key.mode(), key.format());
                return memory;
            });
            return VertexMultiConsumer.create(original.getBuffer(type), builder);
        }
    }
}
