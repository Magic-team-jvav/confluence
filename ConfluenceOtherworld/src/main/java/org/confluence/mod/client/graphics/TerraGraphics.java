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
/// 以及一个色调参数（之后给泰拉式的生物群系/事件滤镜用）；
/// 大气：随距离变浓、带天空颜色的薄雾，清晨贴地的晨雾（按深度还原世界坐标，只作用于世界像素，不碰手和天空）；
/// 光束：太阳/月亮在屏幕上的位置附近，沿天空像素做径向累积，树叶和山崖挡住的地方形成光柱；
/// 泰拉瑞亚式的生物群系/事件/深度画面（色调、浓雾、热浪扭曲）由 TerraEffects 算好，在合成里一起用上。
/// 天空深度在 AFTER_LEVEL 时先复制下来（画手时深度会被清掉），泛光里天空只取一点点。
/// 开着光影包（Iris/Oculus）时整个跳过，交给光影包。
public final class TerraGraphics {
    private static final ResourceLocation BRIGHT_ID = Confluence.asResource("terra_bloom_bright");
    private static final ResourceLocation DOWN_ID = Confluence.asResource("terra_bloom_down");
    private static final ResourceLocation UP_ID = Confluence.asResource("terra_bloom_up");
    private static final ResourceLocation COMPOSITE_ID = Confluence.asResource("terra_composite");
    private static final ResourceLocation RAYS_ID = Confluence.asResource("terra_rays");
    private static final ResourceLocation POINT_RAYS_ID = Confluence.asResource("terra_point_rays");
    /// 泛光金字塔层数：1/2、1/4、1/8、1/16、1/32
    private static final int LEVELS = 5;

    private static ShaderInstance bright;
    private static ShaderInstance down;
    private static ShaderInstance up;
    private static ShaderInstance composite;
    private static ShaderInstance rays;
    private static ShaderInstance pointRays;

    /// 合成结果先画到这里，再整块拷回主画面（主画面的颜色和深度在合成时直接当贴图读，不必先复制）
    private static TextureTarget output;
    private static TextureTarget depth;
    private static TextureTarget raysTarget;
    private static TextureTarget pointRaysTarget;
    /// 灯光光束：最多 4 个点光源在屏幕上的位置 (u, v, 深度, 强度) 和光晕半径
    private static final float[] POINT = new float[16];
    private static final float[] POINT_RADIUS = new float[4];
    private static final org.joml.Vector4f POINT_CLIP = new org.joml.Vector4f();
    private static boolean pointRaysActive;
    private static final TextureTarget[] DOWN = new TextureTarget[LEVELS];
    private static final TextureTarget[] UP = new TextureTarget[LEVELS];
    private static boolean depthCaptured;
    /// AFTER_LEVEL 时记下的相机、光源和雾的参数
    private static final org.joml.Matrix4f INV_VIEW_PROJ = new org.joml.Matrix4f();
    /// 每帧复用，避免分配
    private static final org.joml.Matrix4f VIEW_PROJ = new org.joml.Matrix4f();
    private static final org.joml.Vector4f SUN_CLIP = new org.joml.Vector4f();
    private static float cameraY;
    private static float sunU, sunV, raysStrength;
    private static float raysR, raysG, raysB;
    private static float fogR, fogG, fogB;
    private static float aerialStrength, aerialDistance, groundFog, fogBaseY;
    /// 热浪扭曲用的时间（秒）
    private static float effectTime;

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
        consumer.register(new ShaderInstance(provider, RAYS_ID, DefaultVertexFormat.BLIT_SCREEN), shader -> rays = shader);
        consumer.register(new ShaderInstance(provider, POINT_RAYS_ID, DefaultVertexFormat.BLIT_SCREEN), shader -> pointRays = shader);
    }

    private static boolean active() {
        return ClientConfigs.enhancedGraphics && bright != null && down != null && up != null && composite != null && rays != null
                && pointRays != null
                && !ShaderPackDetector.shaderPackInUse();
    }

    /// AFTER_LEVEL：世界画完时的深度（天空 = 1.0），画手之前复制下来；同时记下相机矩阵、日月位置和雾的参数
    public static void captureDepth(net.neoforged.neoforge.client.event.RenderLevelStageEvent event) {
        depthCaptured = false;
        if (!active()) return;
        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget main = minecraft.getMainRenderTarget();
        depth = ensure(depth, main.width, main.height, true);
        depth.copyDepthFrom(main);
        main.bindWrite(false);
        depthCaptured = true;
        captureAtmosphere(minecraft, event);
    }

    private static void captureAtmosphere(Minecraft minecraft, net.neoforged.neoforge.client.event.RenderLevelStageEvent event) {
        org.joml.Matrix4f viewProj = VIEW_PROJ.set(event.getProjectionMatrix()).mul(event.getModelViewMatrix());
        viewProj.invert(INV_VIEW_PROJ);
        net.minecraft.client.multiplayer.ClientLevel level = minecraft.level;
        net.minecraft.client.Camera camera = event.getCamera();
        cameraY = (float) camera.getPosition().y;
        float[] fog = RenderSystem.getShaderFogColor();
        fogR = fog[0];
        fogG = fog[1];
        fogB = fog[2];
        aerialStrength = 0.0F;
        groundFog = 0.0F;
        raysStrength = 0.0F;
        pointRaysActive = false;
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        if (level != null) TerraEffects.update(minecraft, level, camera, partialTick);
        boolean overworld = level != null && level.dimension() == net.minecraft.world.level.Level.OVERWORLD;
        boolean inAir = camera.getFluidInCamera() == net.minecraft.world.level.material.FogType.NONE;
        if (level == null || !inAir) return;
        effectTime = (float) (Math.floorMod(level.getGameTime(), 72000L) + partialTick) / 20.0F;
        capturePointLights(level, camera, viewProj, partialTick);
        int skyLight = level.getBrightness(net.minecraft.world.level.LightLayer.SKY, camera.getBlockPosition());
        float openSky = skyLight / 15.0F;
        float rain = level.getRainLevel(partialTick);
        // 远处的薄雾（地狱等有雾的维度也加一点）
        aerialStrength = ClientConfigs.atmosphericFog / 100.0F * 0.45F * (overworld ? 1.0F : 0.6F) * TerraEffects.get(TerraEffects.AERIAL);
        aerialDistance = Math.max(64.0F, minecraft.options.getEffectiveRenderDistance() * 16.0F * 1.6F);
        if (!overworld) return;
        // 晨雾：日出前后（世界时间 22000 ~ 3000），越低越浓，只在露天
        long time = Math.floorMod(level.getDayTime(), 24000L);
        float morning = (float) Math.floorMod(time + 2000L, 24000L);
        float morningFactor = morning < 5000.0F ? net.minecraft.util.Mth.sin(morning / 5000.0F * net.minecraft.util.Mth.PI) : 0.0F;
        groundFog = ClientConfigs.atmosphericFog / 100.0F * 0.75F * morningFactor * openSky * (1.0F - rain * 0.5F);
        fogBaseY = level.getSeaLevel() + 2.0F;
        // 光束：太阳（夜里月亮）的方向投到屏幕上
        float angle = level.getTimeOfDay(partialTick) * net.minecraft.util.Mth.TWO_PI;
        float sunX = -net.minecraft.util.Mth.sin(angle);
        float sunY = net.minecraft.util.Mth.cos(angle);
        boolean sunUp = sunY > -0.05F;
        float dirX = sunUp ? sunX : -sunX;
        float dirY = sunUp ? sunY : -sunY;
        org.joml.Vector4f clip = viewProj.transform(SUN_CLIP.set(dirX, dirY, 0.0F, 0.0F));
        if (clip.w <= 0.0F) return;
        sunU = clip.x / clip.w * 0.5F + 0.5F;
        sunV = clip.y / clip.w * 0.5F + 0.5F;
        if (sunU < -0.4F || sunU > 1.4F || sunV < -0.4F || sunV > 1.4F) return;
        float height = net.minecraft.util.Mth.clamp((dirY + 0.05F) / 0.2F, 0.0F, 1.0F);
        raysStrength = ClientConfigs.lightShafts / 100.0F * 0.55F * height * (sunUp ? 1.0F : 0.35F)
                * (1.0F - rain) * net.minecraft.util.Mth.clamp(openSky * 1.5F, 0.0F, 1.0F);
        if (sunUp) {
            float noon = net.minecraft.util.Mth.clamp((dirY - 0.1F) / 0.4F, 0.0F, 1.0F);
            raysR = 1.0F;
            raysG = net.minecraft.util.Mth.lerp(noon, 0.72F, 0.95F);
            raysB = net.minecraft.util.Mth.lerp(noon, 0.42F, 0.82F);
        } else {
            raysR = 0.55F;
            raysG = 0.65F;
            raysB = 0.9F;
        }
    }

    /// 灯光光束：最近的 4 个灯笼投到屏幕上；在屏幕外太远、在相机后面的不要。天越黑越强
    private static void capturePointLights(net.minecraft.client.multiplayer.ClientLevel level, net.minecraft.client.Camera camera,
                                           org.joml.Matrix4f viewProj, float partialTick) {
        pointRaysActive = false;
        java.util.Arrays.fill(POINT, 0.0F);
        float strength = ClientConfigs.lightShafts / 100.0F;
        if (strength <= 0.0F) return;
        PointLights.select(camera.getPosition());
        float darkness = PointLights.darkness(level, partialTick);
        for (int i = 0; i < PointLights.count; i++) {
            org.joml.Vector4f clip = viewProj.transform(POINT_CLIP.set(PointLights.x[i], PointLights.y[i], PointLights.z[i], 1.0F));
            if (clip.w <= 0.05F) continue;
            float u = clip.x / clip.w * 0.5F + 0.5F, v = clip.y / clip.w * 0.5F + 0.5F;
            if (u < -0.3F || u > 1.3F || v < -0.3F || v > 1.3F) continue;
            POINT[i * 4] = u;
            POINT[i * 4 + 1] = v;
            POINT[i * 4 + 2] = Math.min(clip.z / clip.w * 0.5F + 0.5F, 0.99999F);
            POINT[i * 4 + 3] = strength * darkness * PointLights.fade(i);
            // 越远光晕在屏幕上越小
            POINT_RADIUS[i] = net.minecraft.util.Mth.clamp(1.3F / Math.max(PointLights.distance[i], 1.0F), 0.012F, 0.25F);
            pointRaysActive = true;
        }
    }

    /// 原版地形雾的起点往近处挪一些，远处和天空衔接得更柔和（只改普通地形雾，不碰水下、岩浆和其他效果改过的雾）
    public static void softenFog(net.neoforged.neoforge.client.event.ViewportEvent.RenderFog event) {
        if (!active() || event.getMode() != net.minecraft.client.renderer.FogRenderer.FogMode.FOG_TERRAIN
                || event.getType() != net.minecraft.world.level.material.FogType.NONE) return;
        float far = event.getFarPlaneDistance();
        float near = Math.min(event.getNearPlaneDistance(), far * 0.72F);
        event.setNearPlaneDistance(near);
        event.setCanceled(true);
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

        net.minecraft.util.profiling.ProfilerFiller profiler = minecraft.getProfiler();
        profiler.push("confluence_graphics");
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        // 主画面此刻没有绑定为输出，它的颜色和深度可以直接当贴图读：不需要先复制一份场景，
        // 主画面的深度在画手之前被清空过，所以它本身就是“手的深度”
        int sceneColor = main.getColorTextureId();
        int sceneDepth = main.getDepthTextureId();

        float bloom = ClientConfigs.bloomStrength / 100.0F;
        if (bloom > 0.0F) {
            // 1) 亮处 → 1/2
            int w = Math.max(1, width / 2);
            int h = Math.max(1, height / 2);
            DOWN[0] = ensureLinear(DOWN[0], w, h);
            DOWN[0].bindWrite(true);
            bright.setSampler("Sampler0", sceneColor);
            bright.setSampler("DepthSampler", depth.getDepthTextureId());
            bright.safeGetUniform("Threshold").set(ClientConfigs.bloomThreshold / 100.0F);
            // 天空只取一点泛光；灯笼夜天上的灯笼也画在“天空”里，那时让它们完整发光（夜空本身很暗，不会跟着亮）
            bright.safeGetUniform("SkyBloom").set(org.confluence.mod.client.gameevent.ClientGameEventSystem.isEventStarted(
                    org.confluence.mod.common.gameevent.LanternNightGameEvent.KEY) ? 0.75F : 0.15F);
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

        // 光束（1/4 分辨率：光柱本身是模糊的，双线性放大看不出区别，像素只有半分辨率的 1/4）
        boolean shafts = raysStrength > 0.001F;
        if (shafts) {
            raysTarget = ensureLinear(raysTarget, Math.max(1, width / 4), Math.max(1, height / 4));
            raysTarget.bindWrite(true);
            rays.setSampler("DepthSampler", depth.getDepthTextureId());
            rays.safeGetUniform("SunPos").set(sunU, sunV);
            rays.safeGetUniform("Aspect").set(width / (float) height);
            draw(rays);
        }
        // 灯光光束（1/4 分辨率）
        if (pointRaysActive) {
            pointRaysTarget = ensureLinear(pointRaysTarget, Math.max(1, width / 4), Math.max(1, height / 4));
            pointRaysTarget.bindWrite(true);
            pointRays.setSampler("DepthSampler", depth.getDepthTextureId());
            pointRays.safeGetUniform("Light0").set(POINT[0], POINT[1], POINT[2], POINT[3]);
            pointRays.safeGetUniform("Light1").set(POINT[4], POINT[5], POINT[6], POINT[7]);
            pointRays.safeGetUniform("Light2").set(POINT[8], POINT[9], POINT[10], POINT[11]);
            pointRays.safeGetUniform("Light3").set(POINT[12], POINT[13], POINT[14], POINT[15]);
            pointRays.safeGetUniform("Radius").set(POINT_RADIUS[0], POINT_RADIUS[1], POINT_RADIUS[2], POINT_RADIUS[3]);
            pointRays.safeGetUniform("Color").set(PointLights.RED, PointLights.GREEN, PointLights.BLUE);
            pointRays.safeGetUniform("Aspect").set(width / (float) height);
            draw(pointRays);
        }

        // 4) 合成到 output，再整块拷回主画面（glBlitFramebuffer，比画一遍全屏便宜）
        output = ensure(output, width, height, false);
        output.bindWrite(true);
        composite.setSampler("Sampler0", sceneColor);
        composite.setSampler("BloomSampler", bloom > 0.0F && UP[0] != null ? UP[0].getColorTextureId() : blackTexture());
        composite.safeGetUniform("BloomIntensity").set(bloom);
        composite.safeGetUniform("Contrast").set(ClientConfigs.graphicsContrast / 100.0F);
        composite.safeGetUniform("Saturation").set(ClientConfigs.graphicsSaturation / 100.0F * TerraEffects.get(TerraEffects.SATURATION));
        composite.safeGetUniform("Vignette").set(Math.min(1.0F, ClientConfigs.graphicsVignette / 100.0F + TerraEffects.get(TerraEffects.VIGNETTE)));
        composite.safeGetUniform("Tint").set(TerraEffects.get(TerraEffects.TINT_R), TerraEffects.get(TerraEffects.TINT_G), TerraEffects.get(TerraEffects.TINT_B));
        composite.safeGetUniform("TintStrength").set(TerraEffects.get(TerraEffects.TINT));
        composite.safeGetUniform("Exposure").set(TerraEffects.get(TerraEffects.EXPOSURE));
        composite.safeGetUniform("HazeColor").set(TerraEffects.get(TerraEffects.HAZE_R), TerraEffects.get(TerraEffects.HAZE_G), TerraEffects.get(TerraEffects.HAZE_B));
        composite.safeGetUniform("Haze").set(TerraEffects.get(TerraEffects.HAZE));
        composite.safeGetUniform("HazeDistance").set(Math.max(4.0F, TerraEffects.get(TerraEffects.HAZE_DISTANCE)));
        composite.safeGetUniform("Distortion").set(TerraEffects.get(TerraEffects.DISTORTION));
        composite.safeGetUniform("Time").set(effectTime);
        composite.safeGetUniform("ShadowColor").set(TerraEffects.get(TerraEffects.SHADOW_R), TerraEffects.get(TerraEffects.SHADOW_G), TerraEffects.get(TerraEffects.SHADOW_B));
        composite.safeGetUniform("ShadowStrength").set(TerraEffects.get(TerraEffects.SHADOW));
        composite.safeGetUniform("HighlightColor").set(TerraEffects.get(TerraEffects.HIGHLIGHT_R), TerraEffects.get(TerraEffects.HIGHLIGHT_G), TerraEffects.get(TerraEffects.HIGHLIGHT_B));
        composite.safeGetUniform("HighlightStrength").set(TerraEffects.get(TerraEffects.HIGHLIGHT));
        composite.safeGetUniform("EdgeColor").set(TerraEffects.get(TerraEffects.EDGE_R), TerraEffects.get(TerraEffects.EDGE_G), TerraEffects.get(TerraEffects.EDGE_B));
        composite.safeGetUniform("Pulse").set(TerraEffects.get(TerraEffects.PULSE));
        composite.safeGetUniform("Aspect").set(width / (float) height);
        composite.setSampler("DepthSampler", depth.getDepthTextureId());
        composite.setSampler("HandDepthSampler", sceneDepth);
        composite.setSampler("RaysSampler", shafts ? raysTarget.getColorTextureId() : blackTexture());
        composite.setSampler("PointRaysSampler", pointRaysActive ? pointRaysTarget.getColorTextureId() : blackTexture());
        composite.safeGetUniform("InvViewProj").set(INV_VIEW_PROJ);
        composite.safeGetUniform("CameraY").set(cameraY);
        composite.safeGetUniform("FogColor").set(fogR, fogG, fogB);
        composite.safeGetUniform("AerialStrength").set(aerialStrength);
        composite.safeGetUniform("AerialDistance").set(aerialDistance);
        composite.safeGetUniform("GroundFog").set(groundFog);
        composite.safeGetUniform("FogBaseY").set(fogBaseY);
        composite.safeGetUniform("RaysStrength").set(shafts ? raysStrength : 0.0F);
        composite.safeGetUniform("RaysColor").set(raysR, raysG, raysB);
        draw(composite);
        com.mojang.blaze3d.platform.GlStateManager._glBindFramebuffer(org.lwjgl.opengl.GL30.GL_READ_FRAMEBUFFER, output.frameBufferId);
        com.mojang.blaze3d.platform.GlStateManager._glBindFramebuffer(org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER, main.frameBufferId);
        com.mojang.blaze3d.platform.GlStateManager._glBlitFrameBuffer(0, 0, width, height, 0, 0, width, height,
                org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT, org.lwjgl.opengl.GL11.GL_NEAREST);
        main.bindWrite(true);

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        profiler.pop();
    }

    private static TextureTarget black;

    /// 关掉的效果给合成一张全黑的 1×1 图
    private static int blackTexture() {
        if (black == null) {
            black = new TextureTarget(1, 1, false, Minecraft.ON_OSX);
            black.setClearColor(0, 0, 0, 1);
            black.clear(Minecraft.ON_OSX);
            Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
        }
        return black.getColorTextureId();
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
