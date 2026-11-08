package org.confluence.mod.client.graphics;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.ClientConfigs;

import java.io.IOException;

/// Confluence 画质（高于原版）：在世界和手都画完之后、GUI 之前做一组后处理——
/// 泛光（亮处挑出来 → 逐级缩小 → 逐级放大叠加，双重 Kawase）、柔和的高光压缩、对比度、饱和度、暗角，
/// 以及一个色调参数（之后给泰拉式的生物群系/事件滤镜用）。
/// 天空深度在 AFTER_LEVEL 时先复制下来（画手时深度会被清掉），泛光里天空只取一点点。
/// 开着光影包（Iris/Oculus）时整个跳过，交给光影包。
public final class TerraGraphics {
    private static final ResourceLocation BRIGHT_ID = Confluence.asResource("terra_bloom_bright");
    private static final ResourceLocation DOWN_ID = Confluence.asResource("terra_bloom_down");
    private static final ResourceLocation UP_ID = Confluence.asResource("terra_bloom_up");
    private static final ResourceLocation COMPOSITE_ID = Confluence.asResource("terra_composite");
    /// 泛光金字塔层数：1/2、1/4、1/8、1/16、1/32
    private static final int LEVELS = 5;

    private static ShaderInstance bright;
    private static ShaderInstance down;
    private static ShaderInstance up;
    private static ShaderInstance composite;

    private static TextureTarget scene;
    private static TextureTarget depth;
    private static final TextureTarget[] DOWN = new TextureTarget[LEVELS];
    private static final TextureTarget[] UP = new TextureTarget[LEVELS];
    private static boolean depthCaptured;

    /// 色调（之后由生物群系/事件滤镜设置）：颜色和强度 0..1
    private static float tintR = 1.0F, tintG = 1.0F, tintB = 1.0F, tintStrength;

    private TerraGraphics() {}

    @FunctionalInterface
    public interface RegisterConsumer {
        void register(ShaderInstance shader, java.util.function.Consumer<ShaderInstance> onLoad);
    }

    public static void register(ResourceProvider provider, RegisterConsumer consumer) throws IOException {
        consumer.register(new ShaderInstance(provider, BRIGHT_ID, DefaultVertexFormat.BLIT_SCREEN), shader -> bright = shader);
        consumer.register(new ShaderInstance(provider, DOWN_ID, DefaultVertexFormat.BLIT_SCREEN), shader -> down = shader);
        consumer.register(new ShaderInstance(provider, UP_ID, DefaultVertexFormat.BLIT_SCREEN), shader -> up = shader);
        consumer.register(new ShaderInstance(provider, COMPOSITE_ID, DefaultVertexFormat.BLIT_SCREEN), shader -> composite = shader);
    }

    public static void setTint(float r, float g, float b, float strength) {
        tintR = r;
        tintG = g;
        tintB = b;
        tintStrength = strength;
    }

    private static boolean active() {
        return ClientConfigs.enhancedGraphics && bright != null && down != null && up != null && composite != null
                && !ShaderPackDetector.shaderPackInUse();
    }

    /// AFTER_LEVEL：世界画完时的深度（天空 = 1.0），画手之前复制下来
    public static void captureDepth() {
        depthCaptured = false;
        if (!active()) return;
        RenderTarget main = Minecraft.getInstance().getMainRenderTarget();
        depth = ensure(depth, main.width, main.height, true);
        depth.copyDepthFrom(main);
        main.bindWrite(false);
        depthCaptured = true;
    }

    /// GameRenderer.renderLevel 结束时（世界 + 手都画好，GUI 还没画）
    public static void apply() {
        if (!active() || !depthCaptured) return;
        depthCaptured = false;
        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget main = minecraft.getMainRenderTarget();
        int width = main.width;
        int height = main.height;
        if (width < 32 || height < 32) return;

        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);

        // 场景复制一份（合成时要读它、同时写主画面）
        scene = ensure(scene, width, height, false);
        scene.bindWrite(true);
        main.blitToScreen(width, height, true);

        float bloom = ClientConfigs.bloomStrength / 100.0F;
        if (bloom > 0.0F) {
            // 1) 亮处 → 1/2
            int w = Math.max(1, width / 2);
            int h = Math.max(1, height / 2);
            DOWN[0] = ensureLinear(DOWN[0], w, h);
            DOWN[0].bindWrite(true);
            bright.setSampler("Sampler0", scene.getColorTextureId());
            bright.setSampler("DepthSampler", depth.getDepthTextureId());
            bright.safeGetUniform("Threshold").set(ClientConfigs.bloomThreshold / 100.0F);
            bright.safeGetUniform("SkyBloom").set(0.15F);
            draw(bright);
            // 2) 逐级缩小
            for (int i = 1; i < LEVELS; i++) {
                w = Math.max(1, w / 2);
                h = Math.max(1, h / 2);
                DOWN[i] = ensureLinear(DOWN[i], w, h);
                DOWN[i].bindWrite(true);
                down.setSampler("Sampler0", DOWN[i - 1].getColorTextureId());
                down.safeGetUniform("TexelSize").set(1.0F / DOWN[i - 1].width, 1.0F / DOWN[i - 1].height);
                draw(down);
            }
            // 3) 逐级放大，每级叠加同一级缩小的结果
            RenderTarget source = DOWN[LEVELS - 1];
            for (int i = LEVELS - 2; i >= 0; i--) {
                UP[i] = ensureLinear(UP[i], DOWN[i].width, DOWN[i].height);
                UP[i].bindWrite(true);
                up.setSampler("Sampler0", source.getColorTextureId());
                up.setSampler("BaseSampler", DOWN[i].getColorTextureId());
                up.safeGetUniform("TexelSize").set(1.0F / source.width, 1.0F / source.height);
                draw(up);
                source = UP[i];
            }
        }

        // 4) 合成回主画面
        main.bindWrite(true);
        composite.setSampler("Sampler0", scene.getColorTextureId());
        composite.setSampler("BloomSampler", bloom > 0.0F && UP[0] != null ? UP[0].getColorTextureId() : blackTexture());
        composite.safeGetUniform("BloomIntensity").set(bloom);
        composite.safeGetUniform("Contrast").set(ClientConfigs.graphicsContrast / 100.0F);
        composite.safeGetUniform("Saturation").set(ClientConfigs.graphicsSaturation / 100.0F);
        composite.safeGetUniform("Vignette").set(ClientConfigs.graphicsVignette / 100.0F);
        composite.safeGetUniform("Tint").set(tintR, tintG, tintB);
        composite.safeGetUniform("TintStrength").set(tintStrength);
        composite.safeGetUniform("Aspect").set(width / (float) height);
        draw(composite);

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    private static int blackTexture() {
        // 泛光关掉时给合成一张全黑的小图（用最小一级的缩小缓冲，清成黑色）
        DOWN[LEVELS - 1] = ensureLinear(DOWN[LEVELS - 1], 1, 1);
        DOWN[LEVELS - 1].setClearColor(0, 0, 0, 1);
        DOWN[LEVELS - 1].clear(Minecraft.ON_OSX);
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
        return DOWN[LEVELS - 1].getColorTextureId();
    }

    private static void draw(ShaderInstance shader) {
        shader.apply();
        BufferBuilder buffer = RenderSystem.renderThreadTesselator().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLIT_SCREEN);
        buffer.addVertex(0.0F, 0.0F, 0.0F);
        buffer.addVertex(1.0F, 0.0F, 0.0F);
        buffer.addVertex(1.0F, 1.0F, 0.0F);
        buffer.addVertex(0.0F, 1.0F, 0.0F);
        BufferUploader.draw(buffer.buildOrThrow());
        shader.clear();
    }

    private static TextureTarget ensure(TextureTarget target, int width, int height, boolean useDepth) {
        if (target == null) return new TextureTarget(width, height, useDepth, Minecraft.ON_OSX);
        if (target.width != width || target.height != height) target.resize(width, height, Minecraft.ON_OSX);
        return target;
    }

    private static TextureTarget ensureLinear(TextureTarget target, int width, int height) {
        boolean fresh = target == null || target.width != width || target.height != height;
        target = ensure(target, width, height, false);
        if (fresh) target.setFilterMode(GlConst.GL_LINEAR);
        return target;
    }
}
