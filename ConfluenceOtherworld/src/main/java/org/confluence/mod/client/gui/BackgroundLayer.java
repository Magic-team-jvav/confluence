package org.confluence.mod.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.DimensionSpecialEffectsManager;
import org.confluence.lib.util.LibClientUtils;
import org.confluence.lib.util.LibRenderUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.util.AchievementUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public enum BackgroundLayer {
    SKY {
        private final ResourceLocation sprite = Confluence.asResource("background/sky_0");

        @Override
        public void render(GuiGraphics guiGraphics, float partialTick) {
            BackgroundLayer.renderStaticBackground(guiGraphics, sprite, 528, 336);

            float[] color = DimensionSpecialEffectsManager.getForType(BuiltinDimensionTypes.OVERWORLD_EFFECTS).getSunriseColor(timeOfDay, partialTick);
            if (color != null) {
                @Nullable ShaderInstance shader = RenderSystem.getShader();
                RenderSystem.setShaderColor(1, 1, 1, 1);
                RenderSystem.setShader(GameRenderer::getPositionColorShader);
                PoseStack poseStack = guiGraphics.pose();
                poseStack.pushPose();
                Matrix4f matrix4f = poseStack.last().pose();
                BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
                float minX = 0;
                float maxX = guiGraphics.guiWidth();
                float minY = 0;
                float maxY = guiGraphics.guiHeight();
                float r = color[0];
                float g = color[1];
                float b = color[2];
                float a = color[3];
                builder.addVertex(matrix4f, minX, minY, 0).setColor(r, g, b, 0);
                builder.addVertex(matrix4f, minX, maxY, 0).setColor(r, g, b, a);
                builder.addVertex(matrix4f, maxX, maxY, 0).setColor(r, g, b, a);
                builder.addVertex(matrix4f, maxX, minY, 0).setColor(r, g, b, 0);
                BufferUploader.drawWithShader(builder.buildOrThrow());
                poseStack.popPose();
                RenderSystem.setShaderColor(1, 1, 1, 1);
                if (shader != null) {
                    RenderSystem.setShader(() -> shader);
                }
            }
        }
    },
    STAR {
        private final RandomSource random = RandomSource.create();
        private long seed;

        @Override
        public void init(int width, int height) {
            this.seed = random.nextLong();
        }

        @Override
        public void render(GuiGraphics guiGraphics, float partialTick) {
            float f = (Mth.cos(timeOfDay * Mth.TWO_PI) + ORBIT_HORIZON) / STAR_FADE;
            f = Mth.clamp(f, 0.0F, 1.0F);
            f = f * f;
            if (f <= 0) return;
            @Nullable ShaderInstance shader = RenderSystem.getShader();
            RenderSystem.setShaderColor(f, f, f, f);
            RenderSystem.setShader(GameRenderer::getPositionShader);

            int width = guiGraphics.guiWidth();
            int height = guiGraphics.guiHeight();

            random.setSeed(seed);
            BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
            Matrix4f matrix4f = guiGraphics.pose().last().pose();
            Vector3f temp = new Vector3f();
            for (int j = 0; j < 50; j++) {
                float x = random.nextFloat() * width;
                float y = random.nextFloat() * height;
                float size = random.nextFloat() * 0.5F + 0.5F;
                Quaternionf rotation = Axis.ZP.rotation(random.nextFloat() * Mth.TWO_PI);
                temp.set(-size, -size, 0).rotate(rotation).add(x, y, 0);
                builder.addVertex(matrix4f, temp.x, temp.y, 0);
                temp.set(-size, size, 0).rotate(rotation).add(x, y, 0);
                builder.addVertex(matrix4f, temp.x, temp.y, 0);
                temp.set(size, size, 0).rotate(rotation).add(x, y, 0);
                builder.addVertex(matrix4f, temp.x, temp.y, 0);
                temp.set(size, -size, 0).rotate(rotation).add(x, y, 0);
                builder.addVertex(matrix4f, temp.x, temp.y, 0);
            }
            BufferUploader.drawWithShader(builder.buildOrThrow());
            RenderSystem.setShaderColor(1, 1, 1, 1);
            if (shader != null) {
                RenderSystem.setShader(() -> shader);
            }
        }
    },
    PLANET {
        private final ResourceLocation sun = Confluence.asResource("background/sun_0");
        private final ResourceLocation moon = Confluence.asResource("background/moon_0");
        /// 松手后过渡回轨道的时长。
        private static final long SETTLE_DURATION_MS = 300L;
        private int width;
        private int height;
        private int sunSize;
        private int moonSize;
        /// 轨道椭圆半径：水平取屏幕半宽，竖直取抬高后的值。
        /// 水平方向让屏幕左右边缘对应晨昏两端；竖直方向抬高轨道，
        /// 使太阳/月亮在晨昏过渡（日出日落光晕窗口）开始时就已经进入屏幕。
        private float radiusX;
        private float radiusY;
        private float sunCenterY;
        private float moonCenterY;
        /// 行星贴图中心点，绘制与命中判定共用同一坐标。
        private final Vector2f sunPos = new Vector2f();
        private final Vector2f moonPos = new Vector2f();
        private boolean draggedSun;
        private boolean draggedMoon;
        private boolean sunSettling;
        private boolean moonSettling;
        private long sunSettleStart;
        private long moonSettleStart;
        private float sunFromX;
        private float sunFromY;
        private float moonFromX;
        private float moonFromY;
        /// 松手瞬间的轨道角与最短旋转量，避免绕着 0/2π 转整圈。
        private float sunSettleAngle;
        private float sunSettleDelta;
        private float moonSettleAngle;
        private float moonSettleDelta;
        /// 当前实际绘制的旋转角，抓取/松手时作为旋转过渡的起点。
        private float sunDrawAngle;
        private float moonDrawAngle;
        private float sunSpinFrom;
        private float moonSpinFrom;
        private long sunSpinStart;
        private long moonSpinStart;

        @Override
        public void init(int width, int height) {
            this.width = width;
            this.height = height;
            this.sunSize = 78 / 2;
            this.moonSize = 61 / 2;
            this.radiusX = Math.max(width * 0.5F, 1.0F);
            this.radiusY = Math.max(height, 1.0F) / (1.0F + ORBIT_HORIZON);
            this.sunCenterY = height + sunSize * 0.5F - ORBIT_HORIZON * radiusY;
            this.moonCenterY = height + moonSize * 0.5F - ORBIT_HORIZON * radiusY;
            this.draggedSun = false;
            this.draggedMoon = false;
            this.sunSettling = false;
            this.moonSettling = false;
            this.sunDrawAngle = 0.0F;
            this.moonDrawAngle = 0.0F;
            this.sunSpinFrom = 0.0F;
            this.moonSpinFrom = 0.0F;
        }

        @Override
        public void render(GuiGraphics guiGraphics, float partialTick) {
            RenderSystem.setShaderColor(1, 1, 1, 1);
            long now = Util.getMillis();

            if (draggedSun) {
                float spin = Mth.clamp((now - sunSpinStart) / (float) SETTLE_DURATION_MS, 0.0F, 1.0F);
                this.sunDrawAngle = lerpAngle(spin * spin * (3.0F - 2.0F * spin), sunSpinFrom, 0.0F);
                drawPlanet(guiGraphics, sun, sunPos.x, sunPos.y, sunSize, sunDrawAngle);
                timeOfDay = planetTimeOfDay(sunPos.x, true);
                return;
            }
            if (draggedMoon) {
                float spin = Mth.clamp((now - moonSpinStart) / (float) SETTLE_DURATION_MS, 0.0F, 1.0F);
                this.moonDrawAngle = lerpAngle(spin * spin * (3.0F - 2.0F * spin), moonSpinFrom, 0.0F);
                drawPlanet(guiGraphics, moon, moonPos.x, moonPos.y, moonSize, moonDrawAngle);
                timeOfDay = planetTimeOfDay(moonPos.x, false);
                return;
            }

            float angle = Mth.TWO_PI * timeOfDay;
            float sin = Mth.sin(angle);
            float cos = Mth.cos(angle);

            float sunX = width * 0.5F - radiusX * sin;
            float sunY = sunCenterY + radiusY * cos;
            float sunAngle = orbitRotation(timeOfDay);
            if (sunSettling) {
                float progress = Mth.clamp((now - sunSettleStart) / (float) SETTLE_DURATION_MS, 0.0F, 1.0F);
                float eased = progress * progress * (3.0F - 2.0F * progress);
                sunX = Mth.lerp(eased, sunFromX, sunX);
                sunY = Mth.lerp(eased, sunFromY, sunY);
                sunAngle = sunSpinFrom + sunSettleDelta * eased + wrapAngle(orbitRotation(timeOfDay) - sunSettleAngle);
                if (progress >= 1.0F) {
                    sunSettling = false;
                }
            }
            this.sunDrawAngle = sunAngle;
            sunPos.set(sunX, sunY);

            float moonX = width * 0.5F + radiusX * sin;
            float moonY = moonCenterY - radiusY * cos;
            float moonAngle = orbitRotation(timeOfDay);
            if (moonSettling) {
                float progress = Mth.clamp((now - moonSettleStart) / (float) SETTLE_DURATION_MS, 0.0F, 1.0F);
                float eased = progress * progress * (3.0F - 2.0F * progress);
                moonX = Mth.lerp(eased, moonFromX, moonX);
                moonY = Mth.lerp(eased, moonFromY, moonY);
                moonAngle = moonSpinFrom + moonSettleDelta * eased + wrapAngle(orbitRotation(timeOfDay) - moonSettleAngle);
                if (progress >= 1.0F) {
                    moonSettling = false;
                }
            }
            this.moonDrawAngle = moonAngle;
            moonPos.set(moonX, moonY);

            drawPlanet(guiGraphics, sun, sunX, sunY, sunSize, sunAngle);
            drawPlanet(guiGraphics, moon, moonX, moonY, moonSize, moonAngle);
        }

        /// 以贴图中心点绘制，和 {@link #sunPos}/{@link #moonPos} 的坐标含义保持一致。
        private static void drawPlanet(GuiGraphics guiGraphics, ResourceLocation sprite, float centerX, float centerY, int size, float angle) {
            float half = size * 0.5F;
            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();
            poseStack.translate(centerX, centerY, 0);
            poseStack.mulPose(Axis.ZP.rotation(angle));
            poseStack.translate(-half, -half, 0);
            guiGraphics.blitSprite(sprite, 0, 0, size, size);
            poseStack.popPose();
        }

        /// 反解轨道：屏幕左右边缘分别对应太阳的日出/日落与月亮的月出/月落，拖动不出屏也能改变晨昏。
        private float planetTimeOfDay(float centerX, boolean sun) {
            double offset = (width * 0.5D - centerX) / Math.max(radiusX, 1.0F);
            double angle;
            if (sun) {
                angle = Math.PI - Math.asin(Mth.clamp(offset, -1.0D, 1.0D));
            } else {
                angle = Math.asin(Mth.clamp(-offset, -1.0D, 1.0D));
                if (centerX < width * 0.5F) {
                    angle += Mth.TWO_PI;
                }
            }
            return (float) (angle / Mth.TWO_PI);
        }

        /// 行星在轨道上的自转：限制在 ±45° 的周期性摆动，避免昼夜角差过大导致抓取/松手转半圈。
        private static float orbitRotation(float t) {
            return Mth.sin(Mth.TWO_PI * t) * (Mth.PI * 0.25F);
        }

        /// 以最短路径插值旋转角，避免横跨 0/2π 时绕整圈。
        private static float lerpAngle(float progress, float from, float to) {
            return from + shortestDelta(from, to) * progress;
        }

        private static float shortestDelta(float from, float to) {
            return wrapAngle(to - from);
        }

        private static float wrapAngle(float angle) {
            float wrapped = angle % Mth.TWO_PI;
            if (wrapped > Mth.PI) {
                wrapped -= Mth.TWO_PI;
            } else if (wrapped < -Mth.PI) {
                wrapped += Mth.TWO_PI;
            }
            return wrapped;
        }

        /// 命中判定按贴图矩形（外扩 1px），与绘制区域一致。
        private static boolean isWithin(Vector2f center, int size, double mouseX, double mouseY) {
            float half = size * 0.5F + 1.0F;
            return Math.abs(mouseX - center.x) <= half && Math.abs(mouseY - center.y) <= half;
        }

        @Override
        public boolean clicked(double mouseX, double mouseY) {
            if (isWithin(sunPos, sunSize, mouseX, mouseY)) {
                this.sunSpinFrom = this.sunDrawAngle;
                this.sunSpinStart = Util.getMillis();
                this.draggedSun = true;
                this.sunSettling = false;
                sunPos.set((float) mouseX, (float) mouseY);
                dragged = true;
                awardGoingOldschool();
                return true;
            }
            if (isWithin(moonPos, moonSize, mouseX, mouseY)) {
                this.moonSpinFrom = this.moonDrawAngle;
                this.moonSpinStart = Util.getMillis();
                this.draggedMoon = true;
                this.moonSettling = false;
                moonPos.set((float) mouseX, (float) mouseY);
                dragged = true;
                awardGoingOldschool();
                return true;
            }
            return false;
        }

        @Override
        public boolean drag(double mouseX, double mouseY, double dragX, double dragY) {
            if (draggedSun) {
                sunPos.set((float) mouseX, (float) mouseY);
                return true;
            }
            if (draggedMoon) {
                moonPos.set((float) mouseX, (float) mouseY);
                return true;
            }
            return false;
        }

        @Override
        public boolean released(double mouseX, double mouseY) {
            if (draggedSun) {
                this.draggedSun = false;
                this.sunSettling = true;
                this.sunSettleStart = Util.getMillis();
                this.sunSpinFrom = this.sunDrawAngle;
                this.sunSettleAngle = orbitRotation(timeOfDay);
                this.sunSettleDelta = shortestDelta(sunSpinFrom, sunSettleAngle);
                this.sunFromX = sunPos.x;
                this.sunFromY = sunPos.y;
                dragged = false;
                return true;
            }
            if (draggedMoon) {
                this.draggedMoon = false;
                this.moonSettling = true;
                this.moonSettleStart = Util.getMillis();
                this.moonSpinFrom = this.moonDrawAngle;
                this.moonSettleAngle = orbitRotation(timeOfDay);
                this.moonSettleDelta = shortestDelta(moonSpinFrom, moonSettleAngle);
                this.moonFromX = moonPos.x;
                this.moonFromY = moonPos.y;
                dragged = false;
                return true;
            }
            return false;
        }
    },
    CLOUD {
        private int width;
        private int height;
        private final RandomSource random = RandomSource.create(260210);
        private final ResourceLocation[] sprites = new ResourceLocation[]{
                Confluence.asResource("background/cloud_0"),
                Confluence.asResource("background/cloud_1"),
                Confluence.asResource("background/cloud_2"),
                Confluence.asResource("background/cloud_3"),
                Confluence.asResource("background/cloud_4"),
                Confluence.asResource("background/cloud_5")
        };
        private final int[][] size = {
                {33, 13},
                {164, 39},
                {128, 24},
                {115, 50},
                {71, 17},
                {71, 15}
        };
        private List<CloudSprite> clouds;

        @Override
        public void init(int width, int height) {
            this.width = width;
            this.height = height;
            this.clouds = new LinkedList<>();
            int amount = random.nextIntBetweenInclusive(10, 20);
            for (int i = 0; i < amount; i++) {
                int j = random.nextInt(sprites.length);
                CloudSprite sprite = new CloudSprite(sprites[j], size[j], random, width, height);
                clouds.add(sprite);
                sprite.fx = random.nextFloat() * width;
            }
        }

        @Override
        public void tick() {
            if (clouds.size() < 40 && random.nextInt(40) == 0) {
                int j = random.nextInt(sprites.length);
                clouds.add(new CloudSprite(sprites[j], size[j], random, width, height));
            }
            clouds.removeIf(sprite -> sprite.fx < -sprite.w);
        }

        @Override
        public void render(GuiGraphics guiGraphics, float partialTick) {
            boolean isBlendDisabled = !LibRenderUtils.isBlendEnabled();
            if (isBlendDisabled) {
                RenderSystem.enableBlend();
            }
            for (CloudSprite sprite : clouds) {
                sprite.render(guiGraphics, partialTick);
            }
            if (isBlendDisabled) {
                RenderSystem.disableBlend();
            }
        }

        private static class CloudSprite extends GuiSprite {
            private final float speed;
            private final float scale;
            private float fx;

            private CloudSprite(ResourceLocation path, int[] size, RandomSource random, int guiWidth, int guiHeight) {
                super(path, size[0], size[1]);
                this.speed = random.nextFloat() * 0.5F + 0.2F;
                this.scale = 1F / (random.nextInt(3) + 1);
                this.fx = guiWidth + 10;
                setPos(0, random.nextInt(guiHeight / 2) - size[1] / 2);
            }

            @Override
            public void render(GuiGraphics guiGraphics, float partialTick) {
                @Nullable ShaderInstance shader = RenderSystem.getShader();
                RenderSystem.setShaderColor(skyColor, skyColor, skyColor, Mth.lerp(scale, 0.4F, 1.0F));
                guiGraphics.pose().pushPose();
                float screenX = fx -= partialTick * speed * scale;
                float screenY = y * scale;
                guiGraphics.pose().translate(screenX, screenY, 0);
                guiGraphics.pose().scale(scale, scale, 1.0f);
                guiGraphics.blitSprite(path, textureW, textureH, u, v, 0, 0, 0, w, h);
                guiGraphics.pose().popPose();
                RenderSystem.setShaderColor(1, 1, 1, 1);
                if (shader != null) {
                    RenderSystem.setShader(() -> shader);
                }
            }
        }
    },
    ENVIRONMENT_0 {
        private final ResourceLocation sprite = Confluence.asResource("background/environment_0_0");
        private float x;

        @Override
        public void render(GuiGraphics guiGraphics, float partialTick) {
            this.x = BackgroundLayer.renderMovedBackground(guiGraphics, sprite, 512, 320, x + partialTick * 0.125F);
        }
    },
    ENVIRONMENT_1 {
        private final ResourceLocation sprite = Confluence.asResource("background/environment_1_0");
        private float x;

        @Override
        public void render(GuiGraphics guiGraphics, float partialTick) {
            this.x = BackgroundLayer.renderMovedBackground(guiGraphics, sprite, 528, 336, x + partialTick * 0.25F);
        }
    },
    ENVIRONMENT_2 {
        private final ResourceLocation sprite = Confluence.asResource("background/environment_2_0");
        private float x;

        @Override
        public void render(GuiGraphics guiGraphics, float partialTick) {
            this.x = BackgroundLayer.renderMovedBackground(guiGraphics, sprite, 528, 336, x + partialTick * 0.5F);
        }
    },
    ENVIRONMENT_3 {
        private final ResourceLocation sprite = Confluence.asResource("background/environment_3_0");
        private float x;

        @Override
        public void render(GuiGraphics guiGraphics, float partialTick) {
            this.x = BackgroundLayer.renderMovedBackground(guiGraphics, sprite, 528, 336, x + partialTick);
        }
    };

    public static final BackgroundLayer[] LAYERS = values();

    public void init(int width, int height) {}

    public void tick() {}

    public void render(GuiGraphics guiGraphics, float partialTick) {}

    public boolean clicked(double mouseX, double mouseY) {
        return false;
    }

    public boolean drag(double mouseX, double mouseY, double dragX, double dragY) {
        return false;
    }

    public boolean released(double mouseX, double mouseY) {
        return false;
    }

    private static void renderStaticBackground(GuiGraphics guiGraphics, ResourceLocation sprite, int textureWidth, int textureHeight) {
        int w = guiGraphics.guiHeight() * textureWidth / textureHeight;
        if ((float) guiGraphics.guiWidth() / guiGraphics.guiHeight() > (float) textureWidth / textureHeight) {
            int i = Mth.ceil((float) guiGraphics.guiWidth() / w);
            for (int j = 0; j < i; j++) {
                guiGraphics.blitSprite(sprite, w * j, 0, w, guiGraphics.guiHeight());
            }
        } else {
            guiGraphics.blitSprite(sprite, 0, 0, w, guiGraphics.guiHeight());
        }
    }

    private static float renderMovedBackground(GuiGraphics guiGraphics, ResourceLocation sprite, int textureWidth, int textureHeight, float x) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, 0, 0);
        int w = guiGraphics.guiHeight() * textureWidth / textureHeight;
        if (x > w) {
            x = 0;
        }
        if ((float) guiGraphics.guiWidth() / guiGraphics.guiHeight() > (float) textureWidth / textureHeight) {
            int i = Mth.ceil((float) guiGraphics.guiWidth() / w);
            for (int j = -1; j < i; j++) {
                guiGraphics.blitSprite(sprite, w * j, 0, w, guiGraphics.guiHeight());
            }
        } else {
            guiGraphics.blitSprite(sprite, 0, 0, w, guiGraphics.guiHeight());
        }
        guiGraphics.pose().popPose();
        return x;
    }

    private static boolean enabled;
    private static float timeOfDay;
    private static float skyColor;
    private static boolean dragged;
    private static boolean completedGoingOldSchool;
    /// 行星出现/消失时的 cos 阈值，星星也用它对齐月亮升落。
    private static final float ORBIT_HORIZON = 0.42F;
    /// 星星从月亮升起（cos=-ORBIT_HORIZON）到完全可见所需的 cos 跨度。
    private static final float STAR_FADE = 0.73F;

    public static void initLayers(int width, int height) {
        timeOfDay = (float) Math.random();
        for (BackgroundLayer layer : LAYERS) {
            layer.init(width, height);
        }
        enabled = true;
    }

    public static void tickLayers() {
        if (enabled) {
            for (BackgroundLayer layer : LAYERS) {
                layer.tick();
            }
        }
    }

    public static void renderLayers(GuiGraphics guiGraphics, float partialTick) {
        if (enabled) {
            if (!dragged) {
                timeOfDay += partialTick * 0.001F;
                if (timeOfDay > 1) {
                    timeOfDay = 0;
                }
            }
            skyColor = Mth.lerp(1 - Mth.clamp(Mth.cos(timeOfDay * Mth.TWO_PI) * 2.0F + 0.5F, 0.0F, 1.0F), 0.15F, 1.0F);
            boolean isBlendDisabled = !LibRenderUtils.isBlendEnabled();
            if (isBlendDisabled) {
                RenderSystem.enableBlend();
            }
            int[] blendFunc = LibRenderUtils.getBlendFunc();
            @Nullable ShaderInstance shader = RenderSystem.getShader();
            for (BackgroundLayer layer : LAYERS) {
                RenderSystem.setShaderColor(skyColor, skyColor, skyColor, 1);
                layer.render(guiGraphics, partialTick);
            }
            RenderSystem.setShaderColor(1, 1, 1, 1);
            if (shader != null) {
                RenderSystem.setShader(() -> shader);
            }
            RenderSystem.blendFuncSeparate(blendFunc[0], blendFunc[1], blendFunc[2], blendFunc[3]);
            if (isBlendDisabled) {
                RenderSystem.disableBlend();
            }
        }
    }

    public static boolean clickedLayers(double mouseX, double mouseY) {
        for (BackgroundLayer layer : LAYERS) {
            if (layer.clicked(mouseX, mouseY)) {
                return true;
            }
        }
        return false;
    }

    public static boolean dragLayers(double mouseX, double mouseY, double dragX, double dragY) {
        for (BackgroundLayer layer : LAYERS) {
            if (layer.drag(mouseX, mouseY, dragX, dragY)) {
                return true;
            }
        }
        return false;
    }

    public static boolean releasedLayers(double mouseX, double mouseY) {
        for (BackgroundLayer layer : LAYERS) {
            if (layer.released(mouseX, mouseY)) {
                return true;
            }
        }
        return false;
    }

    public static void closeLayers() {
        enabled = false;
    }

    private static void awardGoingOldschool() {
        if (completedGoingOldSchool) return;
        PlayerAdvancements.Data data = AchievementUtils.loadData(LibClientUtils.getGameProfile().getId());
        if (data.map().containsKey(AchievementUtils.GOING_OLDSCHOOL)) {
            completedGoingOldSchool = true;
        } else {
            completedGoingOldSchool = true;
            Map<ResourceLocation, AdvancementProgress> map = new LinkedHashMap<>(data.map());
            map.put(AchievementUtils.GOING_OLDSCHOOL, new AchievementProgress(Map.of("never", new CriterionProgress(Instant.now())), true));
            data = new PlayerAdvancements.Data(map);
            AchievementToast toast = new AchievementToast(
                    Confluence.asResource("textures/achievement/challenger/going_oldschool.png"),
                    new AchievementToast.Display(AdvancementType.CHALLENGE,
                            Component.translatable("achievements.confluence.going_oldschool.title"),
                            Component.translatable("achievements.confluence.going_oldschool.description")
                    ));
            toast.blitOffset = () -> ClientHooks.getGuiFarPlane() - 21000 + 1;
            Minecraft.getInstance().getToasts().addToast(toast);
            AchievementUtils.handleData(data);
            AchievementUtils.saveData();
        }
    }

    public static boolean isCompletedGoingOldSchool() {
        return completedGoingOldSchool;
    }
}
