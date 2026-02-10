package org.confluence.mod.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.neoforged.neoforge.client.DimensionSpecialEffectsManager;
import org.confluence.mod.Confluence;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;

import java.util.LinkedList;
import java.util.List;

public enum BackgroundLayer {
    SKY {
        private GuiSprite sprite;

        @Override
        public void init(int width, int height) {
            this.sprite = new GuiSprite(Confluence.asResource("background/sky_0"), width, height, 0, 0, 528, 336);
        }

        @Override
        public void render(GuiGraphics guiGraphics, float partialTick) {
            sprite.render(guiGraphics);
            RenderSystem.setShaderColor(1, 1, 1, 1);

            float[] color = DimensionSpecialEffectsManager.getForType(BuiltinDimensionTypes.OVERWORLD_EFFECTS).getSunriseColor(timeOfDay, partialTick);
            if (color != null) {
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
            }
        }
    },
    STAR,
    PLANET {
        private final ResourceLocation sun = Confluence.asResource("background/sun_0");
        private final ResourceLocation moon = Confluence.asResource("background/moon_0");
        private int width;
        private int height;
        private int sunSize;
        private int moonSize;
        private final Vector2f sunPos = new Vector2f();
        private final Vector2f moonPos = new Vector2f();
        private boolean draggedSun;
        private boolean draggedMoon;

        @Override
        public void init(int width, int height) {
            this.width = width;
            this.height = height;
            this.sunSize = 78 / 2;
            this.moonSize = 61 / 2;
        }

        @Override
        public void render(GuiGraphics guiGraphics, float partialTick) {
            RenderSystem.setShaderColor(1, 1, 1, 1);
            PoseStack poseStack = guiGraphics.pose();

            if (draggedSun || draggedMoon) {
                if (draggedSun) {
                    poseStack.pushPose();
                    float halfSunSize = sunSize * 0.5F;
                    poseStack.translate(sunPos.x - halfSunSize, sunPos.y - halfSunSize, 0);
                    guiGraphics.blitSprite(sun, 0, 0, sunSize, sunSize);
                    timeOfDay = Mth.lerp(sunPos.x / width, 0.25F, 0.75F);
                    poseStack.popPose();
                } else {
                    poseStack.pushPose();
                    float halfMoonSize = moonSize * 0.5F;
                    poseStack.translate(moonPos.x - halfMoonSize, moonPos.y - halfMoonSize, 0);
                    guiGraphics.blitSprite(moon, 0, 0, moonSize, moonSize);
                    float delta = moonPos.x / width;
                    if (delta < 0.5F) {
                        timeOfDay = Mth.lerp(delta * 2, 0.75F, 1);
                    } else {
                        timeOfDay = Mth.lerp(delta * 2 - 1, 0, 0.25F);
                    }
                    poseStack.popPose();
                }
            } else {
                Quaternionf rotation = Axis.ZP.rotation(Mth.TWO_PI * timeOfDay);
                poseStack.pushPose();
                float halfSunSize = sunSize * 0.5F;
                poseStack.translate(width * 0.5F, height + halfSunSize, 0);
                poseStack.mulPose(rotation);
                poseStack.translate(-halfSunSize, height - halfSunSize, 0);
                guiGraphics.blitSprite(sun, 0, 0, sunSize, sunSize);
                Matrix4f pose = poseStack.last().pose();
                sunPos.set(pose.m30(), pose.m31());
                poseStack.popPose();

                poseStack.pushPose();
                float halfMoonSize = moonSize * 0.5F;
                poseStack.translate(width * 0.5F, height + halfMoonSize, 0);
                poseStack.mulPose(rotation);
                poseStack.translate(-halfMoonSize, -halfMoonSize - height, 0);
                guiGraphics.blitSprite(moon, 0, 0, moonSize, moonSize);
                pose = poseStack.last().pose();
                moonPos.set(pose.m30(), pose.m31());
                poseStack.popPose();
            }
        }

        @Override
        public boolean clicked(double mouseX, double mouseY) {
            if (sunPos.distance((float) mouseX, (float) mouseY) < sunSize) {
                this.draggedSun = true;
                sunPos.set((float) mouseX, (float) mouseY);
                dragged = true;
                return true;
            }
            if (moonPos.distance((float) mouseX, (float) mouseY) < moonSize) {
                this.draggedMoon = true;
                moonPos.set((float) mouseX, (float) mouseY);
                dragged = true;
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
                dragged = false;
                return true;
            }
            if (draggedMoon) {
                this.draggedMoon = false;
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
            for (CloudSprite sprite : clouds) {
                sprite.render(guiGraphics, partialTick);
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
                RenderSystem.setShaderColor(skyColor, skyColor, skyColor, Mth.lerp(scale, 0.4F, 1.0F));
                guiGraphics.pose().pushPose();
                float screenX = fx -= partialTick * speed * scale;
                float screenY = y * scale;
                guiGraphics.pose().translate(screenX, screenY, 0);
                guiGraphics.pose().scale(scale, scale, 1.0f);
                guiGraphics.blitSprite(path, textureW, textureH, u, v, 0, 0, 0, w, h);
                guiGraphics.pose().popPose();
            }
        }
    },
    ENVIRONMENT_0 {
        private final ResourceLocation sprite = Confluence.asResource("background/environment_0_0");

        @Override
        public void render(GuiGraphics guiGraphics, float partialTick) {
            guiGraphics.blitSprite(sprite, 0, 0, guiGraphics.guiWidth(), guiGraphics.guiHeight());
        }
    },
    ENVIRONMENT_1 {
        private final ResourceLocation[] sprites = new ResourceLocation[]{
                Confluence.asResource("background/environment_1_0"),
                Confluence.asResource("background/environment_1_1")
        };
        private ResourceLocation sprite;

        @Override
        public void init(int width, int height) {
            this.sprite = sprites[(int) (Math.random() * 2)];
        }

        @Override
        public void render(GuiGraphics guiGraphics, float partialTick) {
            guiGraphics.blitSprite(sprite, 0, 0, guiGraphics.guiWidth(), guiGraphics.guiHeight());
        }
    },
    ENVIRONMENT_2 {
        private final ResourceLocation sprite = Confluence.asResource("background/environment_2_0");

        @Override
        public void render(GuiGraphics guiGraphics, float partialTick) {
            guiGraphics.blitSprite(sprite, 0, 0, guiGraphics.guiWidth(), guiGraphics.guiHeight());
        }
    },
    ENVIRONMENT_3 {
        private final ResourceLocation sprite = Confluence.asResource("background/environment_3_0");

        @Override
        public void render(GuiGraphics guiGraphics, float partialTick) {
            guiGraphics.blitSprite(sprite, 0, 0, guiGraphics.guiWidth(), guiGraphics.guiHeight());
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

    private static boolean enabled;
    private static float timeOfDay;
    private static float skyColor;
    private static boolean dragged;

    public static void initLayers(int width, int height) {
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
            RenderSystem.enableBlend();
            for (BackgroundLayer layer : LAYERS) {
                RenderSystem.setShaderColor(skyColor, skyColor, skyColor, 1);
                layer.render(guiGraphics, partialTick);
            }
            RenderSystem.setShaderColor(1, 1, 1, 1);
            RenderSystem.disableBlend();
        }
    }

    public static boolean clickedLayers(double mouseX, double mouseY) {
        return PLANET.clicked(mouseX, mouseY);
    }

    public static boolean dragLayers(double mouseX, double mouseY, double dragX, double dragY) {
        return PLANET.drag(mouseX, mouseY, dragX, dragY);
    }

    public static boolean releasedLayers(double mouseX, double mouseY) {
        return PLANET.released(mouseX, mouseY);
    }

    public static void closeLayers() {
        enabled = false;
    }
}
