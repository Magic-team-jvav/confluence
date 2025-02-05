package org.confluence.mod.client.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.TranslatableEnum;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.common.item.common.EverBeneficialItem;
import org.confluence.mod.util.ClientUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TerraStyleHealthHud implements LayeredDraw.Layer {
    private static final ResourceLocation LEGACY_TEXTURE = Confluence.asResource("textures/gui/hud/icon.png");
    private static final ResourceLocation OVERLAY_TEXTURE = Confluence.asResource("textures/gui/hud/overlay.png");
    private static final int LEGACY_SIZE = 128;
    private static final int OVERLAY_SIZE = 128;
    private static final int[] HEALTH = new int[]{0xab311e, 0x5d11ba, 0x436dd0, 0x37c438, 0xeed536};
    private static final int[] HEALTH_LOW = new int[]{0xab1f5d, 0x9d44ac, 0x5d11ba, 0x266c71, 0xf7b60b};
    private static final int[] HEALTH_HIGH = new int[]{0xffb5b5, 0xd6e7eb, 0xc5d4f8, 0xd6eead, 0xeff4ce};

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (!ClientConfigs.terraStyleHealth) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui || !ClientUtils.shouldDrawSurvivalElements(minecraft)) return;
        ClientUtils.setupOverlayRenderState(true, false);
        minecraft.getProfiler().push("terra_style_hud");

        ClientConfigs.healthStyle.render(guiGraphics, minecraft);

        minecraft.getProfiler().pop();
    }

    public enum Health implements TranslatableEnum {
        LEGACY {
            @Override
            public void render(GuiGraphics guiGraphics, Minecraft minecraft) {
                int widthHealth = guiGraphics.guiWidth() - 128;
                int heightHealth = 4;
                float maxHealth = 0.0F;
                float currentHealth = 0.0F;
                int heartBuff = 0;
                int lifeFruitHealth = 0; // 生命果
                float absorptionHealth = 0.0F;

                Player player = minecraft.player;
                if (player != null) {
                    heartBuff = (player.getTicksFrozen() >= 140) ? 1 : heartBuff;
                    heartBuff = (player.hasEffect(MobEffects.WITHER)) ? 2 : heartBuff;
                    heartBuff = (player.hasEffect(MobEffects.POISON)) ? 3 : heartBuff;
                    absorptionHealth = player.getAbsorptionAmount();
                    maxHealth = player.getMaxHealth();
                    currentHealth = player.getHealth();
                    AttributeModifier modifier = player.getAttribute(Attributes.MAX_HEALTH).getModifier(EverBeneficialItem.LIFE_FRUITS.id());
                    if (modifier != null) {
                        lifeFruitHealth = (int) modifier.amount();
                    }
                }
                int countHeart = (int) ((maxHealth - (float) lifeFruitHealth) / 4);
                int countHeartAbsorptionHealth = (int) (absorptionHealth / 4.0F) + ((((int) absorptionHealth) % 4 == 0) ? 0 : 1);
                int yLine = 4 + (int) (6.0F / (float) ((countHeart - 1) / 20 + (countHeartAbsorptionHealth - 1) / 20 + 1));
                int heightToAbsorption = blit(heartBuff, maxHealth, currentHealth, lifeFruitHealth, guiGraphics, widthHealth, heightHealth, 0, yLine);
                blit(0, countHeartAbsorptionHealth * 4, absorptionHealth, 0, guiGraphics, widthHealth, heightToAbsorption, 1, yLine);
            }

            private static void blitHeart(GuiGraphics guiGraphics, int type, int typeHeart, float num, int x, int y, int heartBuff) {
                float heartNum = (type == 1) ? 5.0F : 4.0F;
                heartBuff = (typeHeart == 0) ? heartBuff : 0;
                int heightUV = (typeHeart * 17);
                if (num == heartNum) {
                    guiGraphics.blit(LEGACY_TEXTURE, x, y, ((type == 1) ? 54 : 44) + heartBuff, heightUV, 9, 16, LEGACY_SIZE, LEGACY_SIZE);
                } else if (num > 0.0F) {
                    float ts = num / heartNum;
                    guiGraphics.pose().pushPose();
                    guiGraphics.pose().translate(x + 4.5F * (1.0F - ts), y + 9.0F * (1.0F - ts), 0.0F);
                    guiGraphics.pose().scale(ts, ts, 1.0F);
                    guiGraphics.blit(LEGACY_TEXTURE, 0, 0, ((type == 1) ? 54 : 44) + heartBuff, heightUV, 9, 16, LEGACY_SIZE, LEGACY_SIZE);
                    guiGraphics.pose().popPose();
                }
            }

            private static int blit(int heartBuff, float maxHealth, float currentHealth, int lifeFruitHealth, GuiGraphics guiGraphics, int width, int height, int typeHeart, int yLine) {
                heartBuff *= 20;
                int heightUV = (typeHeart * 17);
                int countHeart = (int) ((maxHealth - (float) lifeFruitHealth) / 4);
                int countLine;
                int type;
                int blitX;
                int blitY = 0;
                int blitXFirst;
                FloatArrayList heartNumList = getHeartNumList(currentHealth, lifeFruitHealth, countHeart);
                for (int countToBlit = 0; countToBlit < countHeart; countToBlit++) {
                    countLine = countToBlit / 10;
                    blitY = yLine * countLine;
                    blitX = countToBlit * 10 - countLine * 100;
                    blitXFirst = (countHeart / 10 == 0) ? 100 - (countHeart - (countHeart / 10) * 10) * 10 : 0;
                    if (countToBlit % 10 == 0) {
                        guiGraphics.blit(LEGACY_TEXTURE, width + blitX - 1 + blitXFirst, height + blitY, 0, heightUV, 1, 16, LEGACY_SIZE, LEGACY_SIZE);
                        if (countToBlit + 1 == countHeart) {
                            guiGraphics.blit(LEGACY_TEXTURE, width + blitX - 3 + blitXFirst, height + blitY, 13, heightUV, 17, 16, LEGACY_SIZE, LEGACY_SIZE);
                        } else {
                            guiGraphics.blit(LEGACY_TEXTURE, width + blitX + blitXFirst, height + blitY, 2, heightUV, 10, 16, LEGACY_SIZE, LEGACY_SIZE);
                        }
                    } else if (countToBlit + 1 == countHeart) {
                        guiGraphics.blit(LEGACY_TEXTURE, width + blitX - 3 + blitXFirst, height + blitY, 13, heightUV, 17, 16, LEGACY_SIZE, LEGACY_SIZE);
                    } else if ((countToBlit + 1) % 10 == 0) {
                        guiGraphics.blit(LEGACY_TEXTURE, width + blitX + blitXFirst, height + blitY, 31, heightUV, 12, 16, LEGACY_SIZE, LEGACY_SIZE);
                    } else {
                        guiGraphics.blit(LEGACY_TEXTURE, width + blitX + blitXFirst, height + blitY, 2, heightUV, 10, 16, LEGACY_SIZE, LEGACY_SIZE);
                    }
                    type = (countToBlit < lifeFruitHealth) ? 1 : 0;
                    blitHeart(guiGraphics, type, typeHeart, heartNumList.getFloat(countToBlit), width + blitX + blitXFirst + 1, height + blitY, heartBuff);
                }
                return (height + blitY + yLine);
            }

            private static FloatArrayList getHeartNumList(float currentHealth, int lifeFruitHealth, int countHeart) {
                FloatArrayList heartNumList = new FloatArrayList();
                float currentHealthToType = currentHealth;
                float heartNum;
                boolean ifLifeFruit;
                for (int i = 0; i < countHeart; i++) {
                    ifLifeFruit = i < lifeFruitHealth;
                    heartNum = ifLifeFruit ? 5.0F : 4.0F;
                    currentHealthToType -= heartNum;
                    if (currentHealthToType >= 0) {
                        heartNumList.add(heartNum);
                    } else if (currentHealthToType + heartNum >= 0) {
                        heartNumList.add(currentHealthToType + heartNum);
                    } else {
                        heartNumList.add(0.0F);
                    }
                }
                return heartNumList;
            }
        },
        OVERLAY {
            @Override
            public void render(GuiGraphics guiGraphics, Minecraft minecraft) {
                float maxHealth = 0.0F;
                float currentHealth = 0.0F;
                Player player = minecraft.player;
                if (player != null) {
                    maxHealth = player.getMaxHealth();
                    currentHealth = player.getHealth();
                }
                int widthHealth = guiGraphics.guiWidth() / 2 - 91;
                int heightHealth = guiGraphics.guiHeight() - minecraft.gui.leftHeight;
                minecraft.gui.leftHeight += 10;
                RandomSource random = RandomSource.create(114514);
                int backCount = (int) (maxHealth / 2);
                int heartCount = (int) (currentHealth);
                if (maxHealth / 2 > (float) backCount) {backCount++;}
                if (currentHealth > (float) heartCount) {heartCount++;}
                for (int i = 0; i < backCount && i < 10; i++) {
                    guiGraphics.blit(OVERLAY_TEXTURE, widthHealth + i * 8, heightHealth, 60, 0, 9, 9, OVERLAY_SIZE, OVERLAY_SIZE);
                }
                int lineCount = heartCount / 20;
                int drawCount;
                int lineCountDraw = lineCount;
                Vector3i color = new Vector3i(0, 0, 0);
                for (int i = 0; i <= lineCount; i++) {
                    drawCount = (i == lineCount) ? (heartCount % 20) : 20;
                    if (i < HEALTH.length) {
                        if (lineCount - i < 3) {
                            draw(widthHealth, heightHealth, guiGraphics, drawCount, HEALTH[i], HEALTH_HIGH[i], HEALTH_LOW[i]);
                        }
                    } else {
                        color = COLOR.computeIfAbsent(i, k -> color(random));
                        if (lineCount - i < 3) {
                            draw(widthHealth, heightHealth, guiGraphics, drawCount, color.x, color.y, color.z);
                        }
                    }
                    if (drawCount != 20 && drawCount != 0) {
                        lineCountDraw = lineCount + 1;
                    }
                }
                String drawString = Integer.toString(lineCountDraw);
                if (lineCountDraw > 1) {
                    drawString(guiGraphics, minecraft.font, drawString, widthHealth - 3 - minecraft.font.width(Component.literal(drawString)), heightHealth + 1, 0xFFFFFF);
                }
            }

            private static void drawString(GuiGraphics guiGraphics, Font font, @Nullable String text, float x, float y, int color) {
                guiGraphics.drawString(font, text, x + 1, y, 0x000000, false);
                guiGraphics.drawString(font, text, x - 1, y, 0x000000, false);
                guiGraphics.drawString(font, text, x, y + 1, 0x000000, false);
                guiGraphics.drawString(font, text, x, y - 1, 0x000000, false);
                guiGraphics.drawString(font, text, x, y, color, false);
            }

            private static void drawColor(GuiGraphics guiGraphics, int x, int y, int iconX, int iconY, ResourceLocation icon, int color, int colorHigh, int colorLow) {
                float red = ((color >> 16) & 0xFF) / 255.0F;
                float green = ((color >> 8) & 0xFF) / 255.0F;
                float blue = (color & 0xFF) / 255.0F;
                float redHigh = ((colorHigh >> 16) & 0xFF) / 255.0F;
                float greenHigh = ((colorHigh >> 8) & 0xFF) / 255.0F;
                float blueHigh = (colorHigh & 0xFF) / 255.0F;
                float redLow = ((colorLow >> 16) & 0xFF) / 255.0F;
                float greenLow = ((colorLow >> 8) & 0xFF) / 255.0F;
                float blueLow = (colorLow & 0xFF) / 255.0F;
                RenderSystem.setShaderColor(red, green, blue, 1.0F);
                guiGraphics.blit(icon, x, y, iconX, iconY, 9, 9, OVERLAY_SIZE, OVERLAY_SIZE);
                RenderSystem.setShaderColor(redLow, greenLow, blueLow, 1.0F);
                guiGraphics.blit(icon, x, y, iconX + 20, iconY, 9, 9, OVERLAY_SIZE, OVERLAY_SIZE);
                RenderSystem.setShaderColor(redHigh, greenHigh, blueHigh, 1.0F);
                guiGraphics.blit(icon, x, y, iconX + 40, iconY, 9, 9, OVERLAY_SIZE, OVERLAY_SIZE);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            }

            private static int colorHigh(int color) {
                return (color / 255 * 55 + 200);
            }

            private static int colorLow(int color, RandomSource random) {
                int colorT = (color - 60 + random.nextInt(121));
                if (colorT < 0) {colorT = 0;}
                if (colorT > 255) {colorT = 255;}
                return colorT;
            }

            private static Vector3i color(RandomSource random) {
                int R;
                int G;
                int B;
                do {
                    R = random.nextInt(256);
                    G = random.nextInt(256);
                    B = random.nextInt(256) + 255 - R - G;
                } while (B > 255 || B < 0);
                int color = (R << 16) | (G << 8) | B;
                int colorHigh = (colorHigh(R) << 16) | (colorHigh(G) << 8) | colorHigh(B);
                int colorLow = (colorLow(R, random) << 16) | (colorLow(G, random) << 8) | colorLow(B, random);
                return new Vector3i(color, colorHigh, colorLow);
            }

            private static void draw(int x, int y, GuiGraphics guiGraphics, int count, int color, int colorHigh, int colorLow) {
                int countT = count / 2;
                int xT = x - 8;
                for (int i = 0; i < countT; i++) {
                    xT = x + i * 8;
                    drawColor(guiGraphics, xT, y, 0, 0, OVERLAY_TEXTURE, color, colorHigh, colorLow);
                }
                if (count - countT * 2 == 1) {
                    drawColor(guiGraphics, xT + 8, y, 10, 0, OVERLAY_TEXTURE, color, colorHigh, colorLow);
                }
            }
        };

        private static Map<Integer, Vector3i> COLOR = new HashMap<>();

        public abstract void render(GuiGraphics guiGraphics, Minecraft minecraft);

        @Override
        public Component getTranslatedName() {
            return Component.translatable("confluence.configuration.healthStyle." + name().toLowerCase(Locale.ROOT));
        }
    }
}
