// Adapted from minecraft-lightning skill. See META-INF/arc-surge-lightning-NOTICES.md.
package org.confluence.mod.client.effect.lightning;

import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.BlendMode;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import org.confluence.mod.Confluence;
import org.joml.Matrix4f;
import org.lwjgl.opengl.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

/// A path is a single soft stroke: union its coverage before adding it to the scene.
@EventBusSubscriber(modid = Confluence.MODID, value = Dist.CLIENT)
final class LightningHalo {
    private static ShaderInstance profile, composite;
    private static TextureTarget scratch;
    private static boolean depthReady;
    static boolean referenceDepthCopies; // Only the opt-in GPU probe toggles this baseline.
    static int depthCopies;

    static void beginBatch() {
        depthReady = false;
        depthCopies = 0;
    }

    private static final ByteBufferBuilder STORAGE = new ByteBufferBuilder(262144);
    private static final boolean PROBES = Boolean.getBoolean("confluence.arcSurge.Demo") || Boolean.getBoolean("confluence.arcSurge.GradientRegression") || Boolean.getBoolean("confluence.arcSurge.GradientClarityRegression") || Boolean.getBoolean("confluence.arcSurge.ImpactColorRegression");
    static int framePaths, frameBodyQuads, frameRoundQuads, frameGlError;
    static double frameCpuMillis;

    static void beginFrame() {
        framePaths = frameBodyQuads = frameRoundQuads = frameGlError = 0;
        frameCpuMillis = 0;
    }

    static void endFrame() {if (PROBES) frameGlError |= GL11.glGetError();}

    @SubscribeEvent
    public static void shaders(RegisterShadersEvent event) throws IOException {
        release();
        event.registerShader(new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath("confluence", "arc_surge_bolt_halo"), DefaultVertexFormat.POSITION_TEX_COLOR), shader -> profile = shader);
        event.registerShader(new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath("confluence", "arc_surge_bolt_halo_composite"), DefaultVertexFormat.POSITION_TEX), shader -> composite = shader);
    }

    static void release() {
        if (scratch != null) try (State state = new State()) {
            GlStateManager._activeTexture(GL13.GL_TEXTURE0);
            scratch.destroyBuffers();
            scratch = null;
        }
    }

    static void draw(List<List<HaloGeometry.Point>> paths, LightningRecipe recipe, HaloGeometry.Projection projection, Matrix4f pose, int width, int height) {
        drawLayer(paths, recipe, projection, pose, width, height, true);
    }

    static boolean drawCore(List<List<HaloGeometry.Point>> paths, LightningRecipe recipe, HaloGeometry.Projection projection, Matrix4f pose, int width, int height) {
        return drawLayer(paths, recipe, projection, pose, width, height, false);
    }

    private static boolean drawLayer(List<List<HaloGeometry.Point>> paths, LightningRecipe recipe, HaloGeometry.Projection projection, Matrix4f pose, int width, int height, boolean halo) {
        if (profile == null || composite == null || paths.isEmpty() || (halo ? recipe.glowGain : recipe.coreGain) <= 0)
            return false;
        var main = Minecraft.getInstance().getMainRenderTarget();
        // Leave alternate framebuffer owners to an explicit integration adapter.
        if (GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING) != main.frameBufferId || main.getDepthTextureId() < 0)
            return false;
        long started = PROBES ? System.nanoTime() : 0;
        try (State state = new State()) {
            if (state.vp[0] != 0 || state.vp[1] != 0 || width != main.width || height != main.height)
                return false;
            GlStateManager._activeTexture(GL13.GL_TEXTURE0);
            if (scratch == null || scratch.width != main.width || scratch.height != main.height || scratch.isStencilEnabled() != main.isStencilEnabled()) {
                release();
                depthReady = false;
                scratch = new TextureTarget(main.width, main.height, true, Minecraft.ON_OSX);
                if (main.isStencilEnabled()) scratch.enableStencil();
                scratch.setFilterMode(GL11.GL_NEAREST);
            }
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.depthMask(false);
            RenderSystem.disableCull();
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            if (!LightningShaderBridge.late && (!depthReady || referenceDepthCopies)) {
                GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, main.frameBufferId);
                GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, scratch.frameBufferId);
                GL30.glBlitFramebuffer(0, 0, main.width, main.height, 0, 0, scratch.width, scratch.height, GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);
                depthReady = true;
                depthCopies++;
            }
            for (var path : paths) {
                var mesh = HaloGeometry.build(path, projection, recipe, halo);
                var bounds = mesh.bounds();
                if (bounds.width() <= 0 || bounds.height() <= 0) continue;
                if (PROBES) {
                    framePaths++;
                    frameBodyQuads += mesh.body().size() / 4;
                    frameRoundQuads += mesh.round().size() / 4;
                }
                scratch.bindWrite(true);
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                GL11.glScissor(bounds.x(), bounds.y(), bounds.width(), bounds.height());
                // Clear only the bounds to be sampled in this path's composite.
                GL11.glClearColor(0, 0, 0, 0);
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
                if (LightningShaderBridge.late) RenderSystem.disableDepthTest();
                else RenderSystem.enableDepthTest();
                RenderSystem.depthFunc(GL11.GL_LEQUAL);
                RenderSystem.depthMask(false);
                LightningShaderBridge.uniforms(profile);
                profile.safeGetUniform("Gain").set((float) (halo ? recipe.glowGain : recipe.coreGain));
                profile.safeGetUniform("Core").set(halo ? 0f : 1f);
                profile.safeGetUniform("Vivid").set(!halo && LightningAppearance.vividCore(recipe) ? 1f : 0f);
                RenderSystem.setShader(() -> profile);
                profile.apply();
                RenderSystem.enableBlend();
                RenderSystem.blendFuncSeparate(GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ONE);
                GL20.glBlendEquationSeparate(GL14.GL_MAX, GL14.GL_MAX);
                profile.safeGetUniform("RoundJoint").set(0f);
                vertices(mesh.body(), pose);
                profile.safeGetUniform("RoundJoint").set(1f);
                vertices(mesh.round(), pose);
                main.bindWrite(true);
                RenderSystem.disableDepthTest();
                RenderSystem.depthMask(false);
                RenderSystem.setShader(() -> composite);
                RenderSystem.setShaderTexture(0, scratch.getColorTextureId());
                composite.apply();
                RenderSystem.enableBlend();
                GL20.glBlendEquationSeparate(GL14.GL_FUNC_ADD, GL14.GL_FUNC_ADD);
                RenderSystem.blendFuncSeparate(GL11.GL_ONE, !halo && LightningAppearance.vividCore(recipe) ? GL11.GL_ONE_MINUS_SRC_ALPHA : GL11.GL_ONE, GL11.GL_ZERO, GL11.GL_ONE);
                BufferBuilder buffer = new BufferBuilder(STORAGE, VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_TEX);
                buffer.addVertex(-1, -1, 0).setUv(0, 0);
                buffer.addVertex(3, -1, 0).setUv(2, 0);
                buffer.addVertex(-1, 3, 0).setUv(0, 2);
                BufferUploader.drawWithShader(buffer.buildOrThrow());
            }
        }
        if (PROBES) frameCpuMillis += (System.nanoTime() - started) / 1e6;
        return true;
    }

    private static void vertices(List<HaloGeometry.Vertex> vertices, Matrix4f pose) {
        if (vertices.isEmpty()) return;
        BufferBuilder buffer = new BufferBuilder(STORAGE, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        for (var v : vertices) {
            var p = v.p();
            var rgb = v.rgb();
            buffer.addVertex(pose, (float) p.x, (float) p.y, (float) p.z)
                    .setColor(rgb[0], rgb[1], rgb[2], v.alpha()).setUv(v.u(), v.v());
        }
        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }

    static final class State implements AutoCloseable {
        // NeoForge 1.21.1 uses the Mojang field name in development and production.
        private static final Field BLEND_CACHE = blendCacheField();
        final BlendMode previousBlend = readBlendCache();
        final int draw = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING), read = GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING), active = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        final int program = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM), func = GL11.glGetInteger(GL11.GL_DEPTH_FUNC);
        final int sr = GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB), dr = GL11.glGetInteger(GL14.GL_BLEND_DST_RGB), sa = GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA), da = GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA), eqr = GL11.glGetInteger(GL20.GL_BLEND_EQUATION_RGB), eqa = GL11.glGetInteger(GL20.GL_BLEND_EQUATION_ALPHA);
        final int[] vp = new int[4], scissorBox = new int[4];
        final float[] mask = new float[4], clear = new float[4];
        final int texture, shaderTexture = RenderSystem.getShaderTexture(0);
        final boolean depth = GL11.glIsEnabled(GL11.GL_DEPTH_TEST), write = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK), blend = GL11.glIsEnabled(GL11.GL_BLEND), cull = GL11.glIsEnabled(GL11.GL_CULL_FACE), scissor = GL11.glIsEnabled(GL11.GL_SCISSOR_TEST);
        final ShaderInstance previous = RenderSystem.getShader();
        final double clearDepth = GL11.glGetDouble(GL11.GL_DEPTH_CLEAR_VALUE);

        State() {
            GL11.glGetIntegerv(GL11.GL_VIEWPORT, vp);
            GL11.glGetIntegerv(GL11.GL_SCISSOR_BOX, scissorBox);
            GL11.glGetFloatv(GL11.GL_COLOR_WRITEMASK, mask);
            GL11.glGetFloatv(GL11.GL_COLOR_CLEAR_VALUE, clear);
            GlStateManager._activeTexture(GL13.GL_TEXTURE0);
            texture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
            GlStateManager._activeTexture(active);
        }

        public void close() {
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, draw);
            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, read);
            RenderSystem.viewport(vp[0], vp[1], vp[2], vp[3]);
            RenderSystem.setShaderTexture(0, shaderTexture);
            GlStateManager._activeTexture(GL13.GL_TEXTURE0);
            GlStateManager._bindTexture(texture);
            GlStateManager._activeTexture(active);
            RenderSystem.setShader(() -> previous);
            GlStateManager._glUseProgram(program);
            if (depth) RenderSystem.enableDepthTest();
            else RenderSystem.disableDepthTest();
            RenderSystem.depthMask(write);
            RenderSystem.depthFunc(func);
            if (blend) RenderSystem.enableBlend();
            else RenderSystem.disableBlend();
            RenderSystem.blendFuncSeparate(sr, dr, sa, da);
            GL20.glBlendEquationSeparate(eqr, eqa);
            if (cull) RenderSystem.enableCull();
            else RenderSystem.disableCull();
            GL11.glScissor(scissorBox[0], scissorBox[1], scissorBox[2], scissorBox[3]);
            if (scissor) GL11.glEnable(GL11.GL_SCISSOR_TEST);
            else GL11.glDisable(GL11.GL_SCISSOR_TEST);
            RenderSystem.colorMask(mask[0] > .5f, mask[1] > .5f, mask[2] > .5f, mask[3] > .5f);
            GL11.glClearColor(clear[0], clear[1], clear[2], clear[3]);
            GL11.glClearDepth(clearDepth);
            // RenderType may change GL blending without changing this shader cache.
            // Preserve the original object, including null, rather than infer it from GL.
            try {BLEND_CACHE.set(null, previousBlend);} catch (IllegalAccessException e) {
                throw new IllegalStateException("Cannot restore ShaderInstance blend cache", e);
            }
        }

        private static Field blendCacheField() {
            try {
                Field field = BlendMode.class.getDeclaredField("lastApplied");
                field.setAccessible(true);
                return field;
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Cannot locate ShaderInstance blend cache", e);
            }
        }

        private static BlendMode readBlendCache() {
            try {return (BlendMode) BLEND_CACHE.get(null);} catch (IllegalAccessException e) {
                throw new IllegalStateException("Cannot save ShaderInstance blend cache", e);
            }
        }
    }
}
