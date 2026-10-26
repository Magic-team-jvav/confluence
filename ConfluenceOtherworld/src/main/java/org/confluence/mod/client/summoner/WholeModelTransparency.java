package org.confluence.mod.client.summoner;

import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class WholeModelTransparency {

    // 模型保留独立深度，图集装满后按距离合成并复用，不复制世界深度。
    private static final List<Model> MODELS = new ArrayList<>();
    private static final Map<RenderType, Material> MATERIALS = new IdentityHashMap<>();
    private static final Capture CAPTURE = new Capture();
    private static final Matrix4f PROJECTION = new Matrix4f();
    private static final Matrix4f VIEW = new Matrix4f();
    private static final Matrix4f CLIP = new Matrix4f();
    private static ByteBufferBuilder vertices;
    private static ByteBufferBuilder indices;
    private static long[] quadOrder = new long[0];
    private static Capture active;
    private static ShaderInstance shader;
    private static Page atlas;
    private static int width;
    private static int height;

    private WholeModelTransparency() {
    }

    public static void setShader(ShaderInstance value) {
        shader = value;
    }

    public static void render(PoseStack poseStack, MultiBufferSource original, float alpha, Consumer<MultiBufferSource> draw) {
        if (alpha > 0) {
            if (AttachmentEntityRenderDispatcher.isShadowPass()) {
                draw.accept(new ShadowBufferSource(original, alpha));
            } else if (active != null) {
                draw.accept(new ColorBufferSource(active).setAlpha(alpha));
            } else if (alpha >= 1) {
                draw.accept(original);
            } else {
                prepareFrame();
                active = CAPTURE;
                draw.accept(CAPTURE);
                active = null;
                List<Batch> batches = new ArrayList<>(CAPTURE.materials.size());
                Vector3f min = new Vector3f(Float.POSITIVE_INFINITY);
                Vector3f max = new Vector3f(Float.NEGATIVE_INFINITY);
                for (Material material : CAPTURE.materials) {
                    MeshData mesh = material.builder.build();
                    material.builder = null;
                    if (mesh != null) {
                        batches.add(new Batch(material, mesh));
                        bounds(mesh, min, max);
                    }
                }
                CAPTURE.materials.clear();
                enqueue(poseStack, batches, min, max, alpha, -1);
            }
        }
    }

    static void renderAfterimage(PoseStack poseStack, List<AfterimageManager.Mesh> meshes, float alpha, int tint) {
        if (alpha > 0) {
            prepareFrame();
            List<Batch> batches = new ArrayList<>(meshes.size());
            Vector3f min = new Vector3f(Float.POSITIVE_INFINITY);
            Vector3f max = new Vector3f(Float.NEGATIVE_INFINITY);
            for (AfterimageManager.Mesh saved : meshes) {
                Material material = MATERIALS.computeIfAbsent(saved.type(), Material::new);
                long address = material.memory.reserve(saved.vertices().length);
                MemoryUtil.memByteBuffer(address, saved.vertices().length).put(saved.vertices());
                MeshData mesh = new MeshData(material.memory.build(), saved.state());
                AfterimageManager.transform(mesh, poseStack.last().pose(), poseStack.last().normal());
                batches.add(new Batch(material, mesh));
                bounds(mesh, min, max);
            }
            enqueue(poseStack, batches, min, max, alpha, tint);
        }
    }

    private static void prepareFrame() {
        if (MODELS.isEmpty()) {
            PROJECTION.set(RenderSystem.getProjectionMatrix());
            VIEW.set(RenderSystem.getModelViewMatrix());
            CLIP.set(PROJECTION).mul(VIEW);
            width = GlStateManager.Viewport.width();
            height = GlStateManager.Viewport.height();
        }
    }

    private static void enqueue(PoseStack poseStack, List<Batch> batches, Vector3f min, Vector3f max, float alpha, int tint) {
        if (!batches.isEmpty()) {
            Rect rect = rectangle(min, max);
            if (rect.width > 0 && rect.height > 0) {
                for (Batch batch : batches) {
                    batch.material.batches.add(batch);
                }
                var pose = poseStack.last().pose();
                double distance = (double) pose.m30() * pose.m30() + (double) pose.m31() * pose.m31() + (double) pose.m32() * pose.m32();
                MODELS.add(new Model(batches, rect, alpha, tint, distance));
            } else {
                batches.forEach(batch -> batch.mesh.close());
            }
        }
    }

    public static void finishFrame() {
        if (!MODELS.isEmpty()) {
            Minecraft minecraft = Minecraft.getInstance();
            var main = minecraft.getMainRenderTarget();
            if (width == main.width && height == main.height) {
                State state = new State();
                Matrix4f projection = new Matrix4f(RenderSystem.getProjectionMatrix());
                var sorting = RenderSystem.getVertexSorting();
                var view = RenderSystem.getModelViewStack();
                view.pushMatrix().set(VIEW);
                RenderSystem.applyModelViewMatrix();
                RenderSystem.setProjectionMatrix(PROJECTION, sorting);
                prepareAtlas();
                if (vertices == null) {
                    vertices = new ByteBufferBuilder(65536);
                    indices = new ByteBufferBuilder(16384);
                }
                for (Material material : MATERIALS.values()) {
                    if (!material.batches.isEmpty()) {
                        material.upload();
                    }
                }
                shader.setSampler("ModelColor", atlas.target.getColorTextureId());
                shader.setSampler("ModelDepth", atlas.target.getDepthTextureId());
                atlas.clear();
                MODELS.sort(Comparator.comparingDouble(Model::distance).reversed());
                BufferBuilder quads = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
                Material current = null;
                for (Model model : MODELS) {
                    if (!atlas.fits(model.rect)) {
                        if (current != null) {
                            current.end();
                            current = null;
                        }
                        composite(quads);
                        atlas.clear();
                        quads = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
                    }
                    Slot slot = atlas.reserve(model.rect);
                    for (Batch batch : model.batches) {
                        if (current != batch.material) {
                            if (current != null) {
                                current.end();
                            }
                            current = batch.material;
                            current.begin();
                        }
                        RenderSystem.viewport(slot.x - model.rect.x, slot.y - model.rect.y, width, height);
                        RenderSystem.enableScissor(slot.x, slot.y, model.rect.width, model.rect.height);
                        GlStateManager._drawElements(current.type.mode().asGLMode, batch.mesh.drawState().indexCount(),
                                GL11.GL_UNSIGNED_INT, (long) batch.indexOffset * Integer.BYTES);
                    }
                    quad(quads, model, slot);
                }
                if (current != null) {
                    current.end();
                }
                composite(quads);
                RenderSystem.setProjectionMatrix(projection, sorting);
                view.popMatrix();
                RenderSystem.applyModelViewMatrix();
                state.restore();
            } else {
                MODELS.forEach(model -> model.batches.forEach(batch -> batch.mesh.close()));
            }
            MODELS.clear();
            for (Material material : MATERIALS.values()) {
                material.batches.clear();
            }
        }
    }

    private static void prepareAtlas() {
        int minWidth = Math.max(1024, width);
        int minHeight = Math.max(1024, height);
        int atlasWidth = Math.max(minWidth, atlas == null ? 0 : atlas.target.width);
        int atlasHeight = Math.max(minHeight, atlas == null ? 0 : atlas.target.height);
        int limit = Math.min(4096, RenderSystem.maxSupportedTextureSize());
        long budget = Math.max(8L * 1024 * 1024, (long) minWidth * minHeight);
        if ((long) atlasWidth * atlasHeight > budget) {
            atlasWidth = minWidth;
            atlasHeight = minHeight;
        }
        long area = 0;
        for (Model model : MODELS) {
            area += (long) model.rect.width * model.rect.height;
        }
        area = Math.min(budget, area + area / 4);
        while ((long) atlasWidth * atlasHeight < area) {
            int nextWidth = (int) Math.min(Math.min((long) atlasWidth * 2, limit), budget / atlasHeight);
            int nextHeight = (int) Math.min(Math.min((long) atlasHeight * 2, limit), budget / atlasWidth);
            if (nextWidth > atlasWidth && (atlasWidth <= atlasHeight || nextHeight <= atlasHeight)) {
                atlasWidth = nextWidth;
            } else if (nextHeight > atlasHeight) {
                atlasHeight = nextHeight;
            } else {
                break;
            }
        }
        if (atlas == null || atlas.target.width != atlasWidth || atlas.target.height != atlasHeight) {
            if (atlas != null) {
                atlas.target.destroyBuffers();
            }
            atlas = new Page(atlasWidth, atlasHeight);
        }
    }

    private static void composite(BufferBuilder quads) {
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
        RenderSystem.disableScissor();
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.colorMask(true, true, true, false);
        RenderSystem.setShader(() -> shader);
        BufferUploader.drawWithShader(quads.buildOrThrow());
    }

    private static void quad(BufferBuilder quads, Model model, Slot slot) {
        Rect rect = model.rect;
        for (int i = 0; i < 4; i++) {
            int corner = i ^ (i >> 1);
            int x = (corner & 1) == 0 ? 0 : rect.width;
            int y = (corner & 2) == 0 ? 0 : rect.height;
            quads.addVertex(2f * (rect.x + x) / width - 1, 2f * (rect.y + y) / height - 1, 0)
                    .setUv(model.alpha, 0).setColor(model.tint).setUv2(slot.x + x, slot.y + y);
        }
    }

    private static void bounds(MeshData mesh, Vector3f min, Vector3f max) {
        VertexFormat format = mesh.drawState().format();
        long address = MemoryUtil.memAddress(mesh.vertexBuffer()) + format.getOffset(VertexFormatElement.POSITION);
        float minX = min.x;
        float minY = min.y;
        float minZ = min.z;
        float maxX = max.x;
        float maxY = max.y;
        float maxZ = max.z;
        for (int i = 0; i < mesh.drawState().vertexCount(); i++) {
            float x = MemoryUtil.memGetFloat(address);
            float y = MemoryUtil.memGetFloat(address + 4);
            float z = MemoryUtil.memGetFloat(address + 8);
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            minZ = Math.min(minZ, z);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
            maxZ = Math.max(maxZ, z);
            address += format.getVertexSize();
        }
        min.set(minX, minY, minZ);
        max.set(maxX, maxY, maxZ);
    }

    private static void writeIndices(MeshData mesh, boolean sorted, VertexSorting sorting, long address, int vertexOffset) {
        var state = mesh.drawState();
        if (state.mode() == VertexFormat.Mode.QUADS || state.mode() == VertexFormat.Mode.LINES) {
            int count = state.vertexCount() / 4;
            sorted &= state.mode() == VertexFormat.Mode.QUADS;
            if (sorted) {
                sortQuads(mesh, sorting, count);
            }
            for (int i = 0; i < count; i++) {
                int vertex = vertexOffset + (sorted ? (int) quadOrder[i] : i) * 4;
                MemoryUtil.memPutInt(address, vertex);
                MemoryUtil.memPutInt(address + 4, vertex + 1);
                MemoryUtil.memPutInt(address + 8, vertex + 2);
                MemoryUtil.memPutInt(address + 12, vertex + (state.mode() == VertexFormat.Mode.QUADS ? 2 : 3));
                MemoryUtil.memPutInt(address + 16, vertex + (state.mode() == VertexFormat.Mode.QUADS ? 3 : 2));
                MemoryUtil.memPutInt(address + 20, vertex + (state.mode() == VertexFormat.Mode.QUADS ? 0 : 1));
                address += 24;
            }
        } else {
            for (int i = 0; i < state.indexCount(); i++) {
                MemoryUtil.memPutInt(address, vertexOffset + i);
                address += Integer.BYTES;
            }
        }
    }

    private static void sortQuads(MeshData mesh, VertexSorting sorting, int count) {
        if (quadOrder.length < count) {
            quadOrder = new long[Math.max(count, quadOrder.length * 2)];
        }
        int stride = mesh.drawState().format().getVertexSize();
        long address = MemoryUtil.memAddress(mesh.vertexBuffer()) + mesh.drawState().format().getOffset(VertexFormatElement.POSITION);
        boolean distanceSort = sorting == VertexSorting.DISTANCE_TO_ORIGIN || sorting == VertexSorting.ORTHOGRAPHIC_Z;
        Vector3f[] centers = distanceSort ? null : new Vector3f[count];
        Vector3f center = new Vector3f();
        for (int i = 0; i < count; i++) {
            long opposite = address + stride * 2L;
            float x = (MemoryUtil.memGetFloat(address) + MemoryUtil.memGetFloat(opposite)) / 2f;
            float y = (MemoryUtil.memGetFloat(address + 4) + MemoryUtil.memGetFloat(opposite + 4)) / 2f;
            float z = (MemoryUtil.memGetFloat(address + 8) + MemoryUtil.memGetFloat(opposite + 8)) / 2f;
            if (distanceSort) {
                float distance = sorting == VertexSorting.ORTHOGRAPHIC_Z ? -z : center.set(x, y, z).distanceSquared(0, 0, 0);
                int bits = Float.floatToIntBits(distance);
                int key = ~(bits ^ ((bits >> 31) & Integer.MAX_VALUE));
                // 距离降序，同距时保留原顺序，避免共面的反向外壳改变覆盖关系。
                quadOrder[i] = ((long) key << 32) | (i & 0xFFFFFFFFL);
            } else {
                centers[i] = new Vector3f(x, y, z);
            }
            address += stride * 4L;
        }
        if (distanceSort) {
            Arrays.sort(quadOrder, 0, count);
        } else {
            int[] order = sorting.sort(centers);
            for (int i = 0; i < count; i++) {
                quadOrder[i] = order[i];
            }
        }
    }

    private static Rect rectangle(Vector3f min, Vector3f max) {
        float left = 1;
        float right = -1;
        float bottom = 1;
        float top = -1;
        Vector4f point = new Vector4f();
        boolean near = false;
        boolean behind = true;
        for (int i = 0; i < 8; i++) {
            CLIP.transform(point.set((i & 1) == 0 ? min.x : max.x, (i & 2) == 0 ? min.y : max.y, (i & 4) == 0 ? min.z : max.z, 1));
            near |= point.w <= 0.001f;
            behind &= point.w <= 0.001f;
            left = Math.min(left, point.x / point.w);
            right = Math.max(right, point.x / point.w);
            bottom = Math.min(bottom, point.y / point.w);
            top = Math.max(top, point.y / point.w);
        }
        int x0 = near ? 0 : Math.clamp((int) Math.floor((left * 0.5 + 0.5) * width) - 2, 0, width);
        int y0 = near ? 0 : Math.clamp((int) Math.floor((bottom * 0.5 + 0.5) * height) - 2, 0, height);
        int x1 = near ? width : Math.clamp((int) Math.ceil((right * 0.5 + 0.5) * width) + 2, 0, width);
        int y1 = near ? height : Math.clamp((int) Math.ceil((top * 0.5 + 0.5) * height) + 2, 0, height);
        return behind ? new Rect(0, 0, 0, 0) : new Rect(x0, y0, x1 - x0, y1 - y0);
    }

    public static void close() {
        MODELS.forEach(model -> model.batches.forEach(batch -> batch.mesh.close()));
        MODELS.clear();
        if (atlas != null) {
            atlas.target.destroyBuffers();
            atlas = null;
        }
        for (Material material : MATERIALS.values()) {
            material.memory.close();
            if (material.buffer != null) {
                material.buffer.close();
            }
        }
        MATERIALS.clear();
        if (vertices != null) {
            vertices.close();
            indices.close();
            vertices = null;
            indices = null;
        }
        quadOrder = new long[0];
    }

    private record Rect(int x, int y, int width, int height) {
    }

    private record Slot(int x, int y) {
    }

    private static class Batch {
        final Material material;
        final MeshData mesh;
        final VertexSorting sorting = RenderSystem.getVertexSorting();
        int indexOffset;

        Batch(Material material, MeshData mesh) {
            this.material = material;
            this.mesh = mesh;
        }
    }

    private record Model(List<Batch> batches, Rect rect, float alpha, int tint, double distance) {
    }

    private record Blend(boolean additive, int source) {
        static Blend current() {
            boolean additive = GL11.glIsEnabled(GL11.GL_BLEND) && GL11.glGetInteger(GL14.GL_BLEND_DST_RGB) == GL11.GL_ONE;
            return new Blend(additive, additive ? GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB) : GL11.GL_SRC_ALPHA);
        }
    }

    private static class Material {
        final RenderType type;
        final ByteBufferBuilder memory;
        final List<Batch> batches = new ArrayList<>();
        BufferBuilder builder;
        VertexBuffer buffer;
        ShaderInstance shader;
        Blend blend;

        Material(RenderType type) {
            this.type = type;
            memory = new ByteBufferBuilder(Math.max(16384, type.bufferSize()));
        }

        // 同材质整帧只上传一次，每个模型保留自己的索引范围。
        void upload() {
            VertexFormat format = batches.getFirst().mesh.drawState().format();
            int vertexCount = 0;
            int indexCount = 0;
            for (Batch batch : batches) {
                vertexCount += batch.mesh.drawState().vertexCount();
                indexCount += batch.mesh.drawState().indexCount();
            }
            long vertexAddress = vertices.reserve(vertexCount * format.getVertexSize());
            long indexAddress = indices.reserve(indexCount * Integer.BYTES);
            int vertexOffset = 0;
            int indexOffset = 0;
            for (Batch batch : batches) {
                var source = batch.mesh.vertexBuffer();
                MemoryUtil.memCopy(MemoryUtil.memAddress(source), vertexAddress, source.remaining());
                batch.indexOffset = indexOffset;
                writeIndices(batch.mesh, type.sortOnUpload(), batch.sorting, indexAddress, vertexOffset);
                vertexAddress += source.remaining();
                vertexOffset += batch.mesh.drawState().vertexCount();
                indexAddress += (long) batch.mesh.drawState().indexCount() * Integer.BYTES;
                indexOffset += batch.mesh.drawState().indexCount();
                batch.mesh.close();
            }
            if (buffer == null) {
                buffer = new VertexBuffer(VertexBuffer.Usage.DYNAMIC);
            }
            buffer.bind();
            buffer.upload(new MeshData(vertices.build(), new MeshData.DrawState(format, vertexCount, indexCount, type.mode(), VertexFormat.IndexType.INT)));
            if (indexCount > 0) {
                buffer.uploadIndexBuffer(indices.build());
            }
        }

        void begin() {
            // CULL 的默认状态不会主动开启剔除，NO_CULL 再由材质关闭。
            RenderSystem.enableCull();
            type.setupRenderState();
            atlas.target.bindWrite(false);
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
            if (blend == null) {
                blend = Blend.current();
            }
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(blend.source, blend.additive ? GL11.GL_ONE : GL11.GL_ONE_MINUS_SRC_ALPHA,
                    blend.additive ? GL11.GL_ZERO : GL11.GL_ONE, blend.additive ? GL11.GL_ONE : GL11.GL_ONE_MINUS_SRC_ALPHA);
            buffer.bind();
            shader = RenderSystem.getShader();
            shader.setDefaultUniforms(type.mode(), VIEW, PROJECTION, Minecraft.getInstance().getWindow());
            shader.apply();
        }

        void end() {
            shader.clear();
            type.clearRenderState();
        }
    }

    private static class Page {
        final TextureTarget target;
        int x;
        int y;
        int rowHeight;

        Page(int width, int height) {
            target = new TextureTarget(width, height, true, Minecraft.ON_OSX);
        }

        boolean fits(Rect rect) {
            return rect.width <= target.width && (x + rect.width <= target.width ? y + Math.max(rowHeight, rect.height) : y + rowHeight + rect.height) <= target.height;
        }

        Slot reserve(Rect rect) {
            if (x + rect.width > target.width) {
                y += rowHeight;
                x = 0;
                rowHeight = 0;
            }
            Slot slot = new Slot(x, y);
            x += rect.width;
            rowHeight = Math.max(rowHeight, rect.height);
            return slot;
        }

        void clear() {
            x = 0;
            y = 0;
            rowHeight = 0;
            RenderSystem.disableScissor();
            RenderSystem.depthMask(true);
            RenderSystem.colorMask(true, true, true, true);
            target.bindWrite(true);
            RenderSystem.clearColor(0, 0, 0, 0);
            RenderSystem.clearDepth(1);
            RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);
        }
    }

    private static class Capture implements MultiBufferSource {
        final List<Material> materials = new ArrayList<>();

        @Override
        public @NotNull VertexConsumer getBuffer(@NotNull RenderType type) {
            Material material = MATERIALS.computeIfAbsent(type, Material::new);
            if (material.builder == null) {
                material.builder = new BufferBuilder(material.memory, type.mode(), type.format());
                materials.add(material);
            }
            return material.builder;
        }
    }

    private static class State {
        final int framebuffer = GlStateManager.getBoundFramebuffer();
        final int x = GlStateManager.Viewport.x();
        final int y = GlStateManager.Viewport.y();
        final int width = GlStateManager.Viewport.width();
        final int height = GlStateManager.Viewport.height();
        final ShaderInstance shader = RenderSystem.getShader();

        void restore() {
            GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);
            RenderSystem.viewport(x, y, width, height);
            RenderSystem.disableScissor();
            RenderSystem.enableDepthTest();
            RenderSystem.depthFunc(GL11.GL_LEQUAL);
            RenderSystem.depthMask(true);
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.setShader(() -> shader);
        }
    }
}
