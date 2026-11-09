package org.confluence.mod.client.graphics;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.client.ClientConfigs;
import org.lwjgl.opengl.GL20;

import java.util.regex.Pattern;

/// 水面（Confluence 画质）：在原版 translucent 地形片元着色器里给水加上
/// - 菲涅尔天空反射：越是斜着看越像镜子，正上方看下去是透明的水；只在露天（天空光）才有，洞里的水不映天空；
/// - 太阳（夜里月亮）的高光：一条闪烁的光带；
/// - 细小的流动波纹：只改法线（不改几何），反射和高光随之起伏。
/// 只作用于用水的纹理画出来的片元（按方块图集里 water_still / water_flow 的 UV 范围认），别的半透明方块不受影响。
public final class WaterSurface {
    private static final Pattern FRAG_COLOR = Pattern.compile("fragColor\\s*=\\s*linear_fog\\(");
    private static final ResourceLocation STILL = ResourceLocation.withDefaultNamespace("block/water_still");
    private static final ResourceLocation FLOW = ResourceLocation.withDefaultNamespace("block/water_flow");

    private static float stillU0, stillV0, stillU1, stillV1, flowU0, flowV0, flowU1, flowV1;
    private static float sunX, sunY, sunZ, sunStrength, skyR, skyG, skyB, strength;
    /// 灯笼在水面上的倒影：最近 4 个，(相对相机的位置, 强度)
    private static final float[] POINTS = new float[16];

    private WaterSurface() {}

    /// AFTER_SKY，每帧：太阳/月亮方向、天空颜色、水纹理在图集里的位置、强度
    static void prepareFrame(Camera camera) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        strength = level == null || ShaderPackDetector.shaderPackInUse() ? 0.0F : ClientConfigs.waterReflections / 100.0F;
        if (strength <= 0.0F) return;
        float partialTick = minecraft.getTimer().getGameTimeDeltaPartialTick(false);
        float angle = level.getTimeOfDay(partialTick) * Mth.TWO_PI;
        float x = -Mth.sin(angle), y = Mth.cos(angle);
        float rain = level.getRainLevel(partialTick);
        if (y > -0.05F) {
            sunX = x;
            sunY = y;
            sunStrength = Mth.clamp((y + 0.05F) / 0.2F, 0.0F, 1.0F) * (1.0F - rain);
        } else {
            sunX = -x;
            sunY = -y;
            sunStrength = 0.3F * Mth.clamp((-y - 0.05F) / 0.2F, 0.0F, 1.0F) * (1.0F - rain);
        }
        sunZ = 0.0F;
        java.util.Arrays.fill(POINTS, 0.0F);
        PointLights.select(camera.getPosition());
        float darkness = PointLights.darkness(level, partialTick);
        for (int i = 0; i < PointLights.count; i++) {
            POINTS[i * 4] = PointLights.x[i];
            POINTS[i * 4 + 1] = PointLights.y[i];
            POINTS[i * 4 + 2] = PointLights.z[i];
            POINTS[i * 4 + 3] = darkness * PointLights.fade(i);
        }
        Vec3 sky = level.getSkyColor(camera.getPosition(), partialTick);
        skyR = (float) sky.x;
        skyG = (float) sky.y;
        skyB = (float) sky.z;
        TextureAtlas atlas = minecraft.getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS);
        TextureAtlasSprite still = atlas.getSprite(STILL), flow = atlas.getSprite(FLOW);
        stillU0 = still.getU0();
        stillV0 = still.getV0();
        stillU1 = still.getU1();
        stillV1 = still.getV1();
        flowU0 = flow.getU0();
        flowV0 = flow.getV0();
        flowU1 = flow.getU1();
        flowV1 = flow.getV1();
    }

    private static final String FUNCTIONS = """
            uniform vec4 CfWaterStill;
            uniform vec4 CfWaterFlow;
            uniform vec3 CfSunDir;
            uniform float CfSunStrength;
            uniform vec3 CfSkyColor;
            uniform vec3 CfAnchor;
            uniform vec3 CfCameraOffset;
            uniform float CfTime;
            uniform float CfWaterFx;
            uniform vec4 CfPoint0;
            uniform vec4 CfPoint1;
            uniform vec4 CfPoint2;
            uniform vec4 CfPoint3;
            uniform vec3 CfPointColor;
            in vec3 cfViewPos;
            in float cfSkyLight;
            in float cfTop;

            bool cfInRect(vec2 uv, vec4 r) {
                return uv.x >= r.x && uv.x <= r.z && uv.y >= r.y && uv.y <= r.w;
            }

            // a lantern's reflection: sharp glint plus a soft warm streak, fading with distance
            vec3 cfPointGlint(vec4 light, vec3 r) {
                if (light.w <= 0.0) return vec3(0.0);
                vec3 lv = light.xyz - cfViewPos;
                float d2 = dot(lv, lv);
                float rl = max(dot(r, lv * inversesqrt(d2)), 0.0);
                return CfPointColor * (pow(rl, 180.0) * 2.5 + pow(rl, 16.0) * 0.3) * light.w / (1.0 + d2 * 0.015);
            }

            // Confluence water: Fresnel sky reflection, sun glint and small moving ripples (normal only)
            vec4 cfWaterShade(vec4 color) {
                if (CfWaterFx <= 0.0) return color;
                if (!cfInRect(texCoord0, CfWaterStill) && !cfInRect(texCoord0, CfWaterFlow)) return color;
                // top surface = all its vertices sit on fractional heights (marked in the vertex shader);
                // the normal itself is straight up plus ripples, so block-to-block slopes never show as seams
                float up = smoothstep(0.97, 1.0, cfTop);
                if (up <= 0.0) return color;
                vec3 view = normalize(-cfViewPos);
                if (view.y <= 0.0) return color;
                // water gets one light value per block: soften it so reflection strength does not step at block edges
                float skyLight = smoothstep(0.45, 0.95, cfSkyLight);
                vec2 p = (cfViewPos + CfCameraOffset + CfAnchor).xz;
                float t = CfTime;
                vec2 g = vec2(cos(p.x * 1.7 + t * 1.3), cos(p.y * 1.9 + t * 1.1)) * 0.035;
                g += vec2(cos((p.x + p.y) * 3.1 - t * 2.0), cos((p.x - p.y) * 2.7 + t * 1.7)) * 0.018;
                g += vec2(cos(p.x * 6.3 + p.y * 2.1 + t * 3.1), cos(p.y * 5.7 - p.x * 1.9 + t * 2.6)) * 0.008;
                vec3 n = normalize(vec3(-g.x, 1.0, -g.y));
                float ndv = clamp(dot(n, view), 0.0, 1.0);
                float fresnel = 0.02 + 0.98 * pow(1.0 - ndv, 5.0);
                vec3 r = reflect(-view, n);
                vec3 sky = mix(FogColor.rgb, CfSkyColor, smoothstep(0.0, 0.35, r.y));
                float amount = clamp(fresnel * skyLight * up * CfWaterFx, 0.0, 0.85);
                color.rgb = mix(color.rgb, sky, amount);
                color.a = mix(color.a, 1.0, amount * 0.7);
                float spec = pow(max(dot(r, CfSunDir), 0.0), 220.0) * CfSunStrength * skyLight * up * CfWaterFx;
                color.rgb += vec3(1.0, 0.95, 0.85) * spec * 2.0;
                color.a = max(color.a, min(1.0, spec));
                vec3 points = (cfPointGlint(CfPoint0, r) + cfPointGlint(CfPoint1, r) + cfPointGlint(CfPoint2, r) + cfPointGlint(CfPoint3, r))
                        * up * CfWaterFx;
                color.rgb += points;
                color.a = max(color.a, min(1.0, dot(points, vec3(0.33))));
                return color;
            }
            """;

    /// 只改原版 translucent 片元着色器：在雾之前给水的颜色加上反射、高光
    public static String patchFragment(String source, String name) {
        String key = name.startsWith("minecraft:") ? name.substring(10) : name;
        if (!key.equals("rendertype_translucent") || source.contains("cfWaterShade")) return source;
        if (!source.contains("in vec2 texCoord0;") || !source.contains("uniform vec4 FogColor;")) return source;
        var fragColor = FRAG_COLOR.matcher(source);
        int main = source.indexOf("void main(");
        if (!fragColor.find() || main < 0 || main > fragColor.start()) return source;
        return source.substring(0, main) + FUNCTIONS + "\n" + source.substring(main, fragColor.start())
                + "color = cfWaterShade(color);\n    " + source.substring(fragColor.start());
    }

    /// 每个着色器自己的 uniform 位置（solid 里没有这些，位置是 -1，直接跳过）
    static final class Uniforms {
        private final int still, flow, sunDir, sunStrengthLocation, skyColor, anchor, fx, pointColor;
        private final int[] points = new int[4];

        Uniforms(ShaderInstance shader) {
            int id = shader.getId();
            still = GL20.glGetUniformLocation(id, "CfWaterStill");
            flow = GL20.glGetUniformLocation(id, "CfWaterFlow");
            sunDir = GL20.glGetUniformLocation(id, "CfSunDir");
            sunStrengthLocation = GL20.glGetUniformLocation(id, "CfSunStrength");
            skyColor = GL20.glGetUniformLocation(id, "CfSkyColor");
            anchor = GL20.glGetUniformLocation(id, "CfAnchor");
            fx = GL20.glGetUniformLocation(id, "CfWaterFx");
            pointColor = GL20.glGetUniformLocation(id, "CfPointColor");
            for (int i = 0; i < 4; i++) points[i] = GL20.glGetUniformLocation(id, "CfPoint" + i);
        }

        void apply(int anchorX, int anchorY, int anchorZ) {
            if (fx < 0) return;
            GL20.glUniform1f(fx, strength);
            if (strength <= 0.0F) return;
            if (still >= 0) GL20.glUniform4f(still, stillU0, stillV0, stillU1, stillV1);
            if (flow >= 0) GL20.glUniform4f(flow, flowU0, flowV0, flowU1, flowV1);
            if (sunDir >= 0) GL20.glUniform3f(sunDir, sunX, sunY, sunZ);
            if (sunStrengthLocation >= 0) GL20.glUniform1f(sunStrengthLocation, sunStrength);
            if (skyColor >= 0) GL20.glUniform3f(skyColor, skyR, skyG, skyB);
            if (anchor >= 0) GL20.glUniform3f(anchor, anchorX, anchorY, anchorZ);
            if (pointColor >= 0) GL20.glUniform3f(pointColor, PointLights.RED, PointLights.GREEN, PointLights.BLUE);
            for (int i = 0; i < 4; i++) {
                if (points[i] >= 0) GL20.glUniform4f(points[i], POINTS[i * 4], POINTS[i * 4 + 1], POINTS[i * 4 + 2], POINTS[i * 4 + 3]);
            }
        }
    }
}
