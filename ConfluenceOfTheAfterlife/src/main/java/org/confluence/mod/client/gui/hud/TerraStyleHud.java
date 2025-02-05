package org.confluence.mod.client.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.TranslatableEnum;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.common.item.common.EverBeneficialItem;
import org.confluence.mod.util.ClientUtils;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Locale;
import java.util.Random;

@ParametersAreNonnullByDefault
public class TerraStyleHud implements LayeredDraw.Layer {
    private static final ResourceLocation LEGACY_TEXTURE = Confluence.asResource("textures/gui/hud/icon.png");
    private static final ResourceLocation OVERLAY_TEXTURE = Confluence.asResource("textures/gui/hud/overlay.png");
    private static final int LEGACY_SIZE = 128;
    private static final int OVERLAY_SIZE = 128;
    private static final int[] HEALTH = new int[]{0xab311e, 0x5d11ba, 0x436dd0, 0x37c438, 0xeed536};
    private static final int[] HEALTH_LOW = new int[]{0xab1f5d, 0x9d44ac, 0x5d11ba, 0x266c71, 0xf7b60b};
    private static final int[] HEALTH_HIGH = new int[]{0xffb5b5, 0xd6e7eb, 0xc5d4f8, 0xd6eead, 0xeff4ce};

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui || !ClientUtils.shouldDrawSurvivalElements(minecraft)) return;
        ClientUtils.setupOverlayRenderState(true, false);
        minecraft.getProfiler().push("terra_style_hud");

        if (ClientConfigs.terraStyleHealth) ClientConfigs.healthStyle.render(guiGraphics, minecraft);
        ClientConfigs.manaStyle.render(guiGraphics, minecraft);
        if (ClientConfigs.terraStyleArmor) ClientConfigs.armorStyle.render(guiGraphics, minecraft);

        minecraft.getProfiler().pop();
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

    private static void draw(int x, int y, GuiGraphics guiGraphics, RandomSource random, int count) {
        int R;
        int G;
        int B;
        do {
            R = random.nextInt(256);
            G = random.nextInt(256);
            B = random.nextInt(256) + 255 - R - G;
        } while (B > 255 || B < 0);
        int color = (R << 16) | (G << 8) | B;
        int colorHigh =  (colorHigh(R) << 16) | (colorHigh(G) << 8) | colorHigh(B);
        int colorLow =  (colorLow(R, random) << 16) | (colorLow(G, random) << 8) | colorLow(B, random);
        int countT = count / 2;
        int xT = x - 8;
        for (int i = 0; i < countT; i++) {
            xT = x + i * 8;
            drawColor(guiGraphics, xT, y, 0, 0, OVERLAY_TEXTURE, color, colorHigh, colorLow);
        } if (count - countT * 2 == 1) {
            drawColor(guiGraphics, xT + 8, y, 10, 0, OVERLAY_TEXTURE, color, colorHigh, colorLow);
        }
    }

    private static void draw(int x, int y, GuiGraphics guiGraphics, int count, int color, int colorHigh, int colorLow) {
        int countT = count / 2;
        int xT = x - 8;
        for (int i = 0; i < countT; i++) {
            xT = x + i * 8;
            drawColor(guiGraphics, xT, y, 0, 0, OVERLAY_TEXTURE, color, colorHigh, colorLow);
        } if (count - countT * 2 == 1) {
            drawColor(guiGraphics, xT + 8, y, 10, 0, OVERLAY_TEXTURE, color, colorHigh, colorLow);
        }
    }

    public enum Armor {
        LEGACY_HORIZONTAL {
            @Override
            public void render(GuiGraphics guiGraphics, Minecraft minecraft) {
                draw(guiGraphics, minecraft, 1);
            }
        },
        LEGACY_DIAGONAL {
            @Override
            public void render(GuiGraphics guiGraphics, Minecraft minecraft) {
                draw(guiGraphics, minecraft, 2);
            }
        },
        LEGACY_VERTICAL {
            @Override
            public void render(GuiGraphics guiGraphics, Minecraft minecraft) {
                draw(guiGraphics, minecraft, 3);
            }
        };

        public void draw(GuiGraphics guiGraphics, Minecraft minecraft, int type) {
            int armorNum = 0;
            int armorToughnessNum = 0;
            Player player = minecraft.player;
            if (player != null) {
                armorNum = player.getArmorValue();
                armorToughnessNum = (int) player.getAttribute(Attributes.ARMOR_TOUGHNESS).getValue();
            }
            String armor = Integer.toString(armorNum);
            String armorToughness = Integer.toString(armorToughnessNum);
            String text = switch (type) {
                case 1 -> "|";
                case 3 -> str("-", Math.max(armor.length(), armorToughness.length()));
                default -> "/";
            };
            int widthArmor = guiGraphics.guiWidth() - 26;
            int heightArmor = guiGraphics.guiHeight() - 28;
            int textWidthArmor = minecraft.font.width(Component.literal(armor));
            int textWidthArmorToughness = minecraft.font.width(Component.literal(armorToughness));
            int textWidth = minecraft.font.width(Component.literal(text));
            int textHeight = minecraft.font.lineHeight;
            int colorWhite = 0xFFFFFF;
            int colorArmor = 0xEEB354;
            int colorArmorToughness = 0x54E4EE;
            float widthOffset;
            if (armorToughnessNum == 0) {
                widthOffset = (11.5F + (textWidthArmor / 2.0F) - 22);
            } else {
                widthOffset = switch (type) {
                    case 1 -> (13.5F + (textWidth / 2.0F) + textWidthArmorToughness - 22);
                    case 3 -> (11.5F + (Math.max(textWidthArmor, textWidthArmorToughness) / 2.0F) - 22);
                    default -> (11.5F + (textWidth / 2.0F) + textWidthArmorToughness - 22);
                };
            }
            widthOffset = (widthOffset > 0) ? widthOffset : 0;
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(-widthOffset, 0.0F, 0.0F);
            guiGraphics.blit(LEGACY_TEXTURE, widthArmor, heightArmor, 0, 51, 23, 25, LEGACY_SIZE, LEGACY_SIZE);
            guiGraphics.pose().popPose();
            if (armorToughnessNum == 0) {
                drawString(guiGraphics, minecraft.font, armor, widthArmor + 11.5F - (textWidthArmor / 2.0F) - widthOffset, heightArmor + 12.5F - (textHeight / 2.0F), colorArmor);
            } else {
                drawString(guiGraphics, minecraft.font, text, widthArmor + 11.5F - (textWidth / 2.0F) - widthOffset, heightArmor + 12.5F - (textHeight / 2.0F), colorWhite);
                switch (type) {
                    case 1:
                        drawString(guiGraphics, minecraft.font, armor, widthArmor + 9.5F - (textWidth / 2.0F) - textWidthArmor - widthOffset, heightArmor + 12.5F - (textHeight / 2.0F), colorArmor);
                        drawString(guiGraphics, minecraft.font, armorToughness, widthArmor + 13.5F + (textWidth / 2.0F) - widthOffset, heightArmor + 12.5F - (textHeight / 2.0F), colorArmorToughness);
                        break;
                    case 3:
                        drawString(guiGraphics, minecraft.font, armor, widthArmor + 11.5F - (textWidthArmor / 2.0F) - widthOffset, heightArmor + 10.5F - textHeight, colorArmor);
                        drawString(guiGraphics, minecraft.font, armorToughness, widthArmor + 11.5F - (textWidthArmorToughness / 2.0F) - widthOffset, heightArmor + 14.5F, colorArmorToughness);
                        break;
                    default:
                        drawString(guiGraphics, minecraft.font, armor, widthArmor + 11.5F - (textWidth / 2.0F) - textWidthArmor - widthOffset, heightArmor + 9.5F - (textHeight / 2.0F), colorArmor);
                        drawString(guiGraphics, minecraft.font, armorToughness, widthArmor + 11.5F + (textWidth / 2.0F) - widthOffset, heightArmor + 15.5F - (textHeight / 2.0F), colorArmorToughness);
                }
            }
        }

        private static String str(String text, int count) {
            String str = "";
            for (int i = 0; i < count; i++) {
                str += text;
            }
            return str;
        }

        public abstract void render(GuiGraphics guiGraphics, Minecraft minecraft);
    }

    public enum Mana implements TranslatableEnum {
        LEGACY {
            @Override
            public void render(GuiGraphics guiGraphics, Minecraft minecraft) {
                int widthMana = guiGraphics.guiWidth() - 21;
                int heightMana = 4;
                int currentMana = ClientPacketHandler.getCurrentMana();
                int maxManaCount = ClientPacketHandler.getMaxMana() / 20;
                int currentManaToBlit;
                float ts;
                for (int i = 0; i < maxManaCount; i++) {
                    currentManaToBlit = currentMana - (i + 1) * 20;
                    guiGraphics.blit(LEGACY_TEXTURE, widthMana, heightMana + i * 12, 0, 34, 17, 16, LEGACY_SIZE, LEGACY_SIZE);
                    if (currentManaToBlit >= 0) {
                        guiGraphics.blit(LEGACY_TEXTURE, widthMana + 2, heightMana + i * 12, 18, 34, 13, 16, LEGACY_SIZE, LEGACY_SIZE);
                    } else if (currentManaToBlit + 20 >= 0) {
                        ts = ((float) (currentManaToBlit + 20)) / 20.0F;
                        guiGraphics.pose().pushPose();
                        guiGraphics.pose().translate(widthMana + 2 + 6.5F * (1 - ts), heightMana + i * 12 + 8.5F * (1 - ts), 0.0F);
                        guiGraphics.pose().scale(ts, ts, 1.0F);
                        guiGraphics.blit(LEGACY_TEXTURE, 0, 0, 18, 34, 13, 16, LEGACY_SIZE, LEGACY_SIZE);
                        guiGraphics.pose().popPose();
                    }
                }
            }
        };

        public abstract void render(GuiGraphics guiGraphics, Minecraft minecraft);

        @Override
        public Component getTranslatedName() {
            return Component.translatable("confluence.configuration.manaStyle." + name().toLowerCase(Locale.ROOT));
        }
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
                    AttributeInstance instance = player.getAttribute(Attributes.MAX_HEALTH);
                    AttributeModifier modifier = instance.getModifier(EverBeneficialItem.LIFE_FRUITS.id());
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
                int heightHealth = guiGraphics.guiHeight() - 39;
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
                for (int i = 0; i <= lineCount; i++) {
                    drawCount = (i == lineCount) ? (heartCount % 20) : 20;
                    if (i < HEALTH.length) {
                        draw(widthHealth, heightHealth, guiGraphics, drawCount, HEALTH[i], HEALTH_HIGH[i], HEALTH_LOW[i]);
                    } else {
                        draw(widthHealth, heightHealth, guiGraphics, random, drawCount);
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
        };

        public abstract void render(GuiGraphics guiGraphics, Minecraft minecraft);

        @Override
        public Component getTranslatedName() {
            return Component.translatable("confluence.configuration.healthStyle." + name().toLowerCase(Locale.ROOT));
        }
    }
}
