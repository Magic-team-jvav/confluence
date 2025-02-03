package org.confluence.mod.client.gui.hud;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.common.item.common.EverBeneficialItem;
import org.confluence.mod.util.ClientUtils;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class TerraStyleHud implements LayeredDraw.Layer {
    private static final ResourceLocation ICON = Confluence.asResource("textures/gui/hud/icon.png");
    private static final int SIZE = 128;

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

    public enum Armor {
        LEGACY {
            @Override
            public void render(GuiGraphics guiGraphics, Minecraft minecraft) {
                long armorNum = 0;
                long armorToughnessNum = 0;
                Player player = minecraft.player;
                if (player != null) {
                    armorNum = player.getArmorValue();
                    armorToughnessNum = (int) player.getAttribute(Attributes.ARMOR_TOUGHNESS).getValue();
                }
                String armor = Long.toString(armorNum);
                String armorToughness = Long.toString(armorToughnessNum);
                int widthArmor = guiGraphics.guiWidth() - 26;
                int heightArmor = guiGraphics.guiHeight() - 28;
                int textWidthArmor = minecraft.font.width(Component.literal(armor));
                int textWidthArmorToughness = minecraft.font.width(Component.literal(armorToughness));
                int textWidth = minecraft.font.width(Component.literal("|"));
                int textHeight = minecraft.font.lineHeight;
                int colorWhite = 0xFFFFFF;
                int colorArmor = 0xEEB354;
                int colorArmorToughness = 0x54E4EE;
                float widthOffset = (armorToughnessNum == 0) ? (11.5F + (textWidthArmor / 2.0F) - 22) : (13.5F + (textWidth / 2.0F) + textWidthArmorToughness - 22);
                widthOffset = (widthOffset > 0) ? widthOffset : 0;
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(-widthOffset, 0.0F, 0.0F);
                guiGraphics.blit(ICON, widthArmor, heightArmor, 0, 51, 23, 25, SIZE, SIZE);
                guiGraphics.pose().popPose();
                if (armorToughnessNum == 0) {
                    drawString(guiGraphics, minecraft.font, armor, widthArmor + 11.5F - (textWidthArmor / 2.0F) - widthOffset, heightArmor + 12.5F - (textHeight / 2.0F), colorArmor);
                } else {
                    drawString(guiGraphics, minecraft.font, "|", widthArmor + 11.5F - (textWidth / 2.0F) - widthOffset, heightArmor + 12.5F - (textHeight / 2.0F), colorWhite);
                    drawString(guiGraphics, minecraft.font, armor, widthArmor + 9.5F - (textWidth / 2.0F) - textWidthArmor - widthOffset, heightArmor + 12.5F - (textHeight / 2.0F), colorArmor);
                    drawString(guiGraphics, minecraft.font, armorToughness, widthArmor + 13.5F + (textWidth / 2.0F) - widthOffset, heightArmor + 12.5F - (textHeight / 2.0F), colorArmorToughness);
                }
            }

            public void drawString(GuiGraphics guiGraphics, Font font, @Nullable String text, float x, float y, int color) {
                guiGraphics.drawString(font, text, x + 1, y, 0x000000, false);
                guiGraphics.drawString(font, text, x - 1, y, 0x000000, false);
                guiGraphics.drawString(font, text, x, y + 1, 0x000000, false);
                guiGraphics.drawString(font, text, x, y - 1, 0x000000, false);
                guiGraphics.drawString(font, text, x, y, color, false);
            }
        };

        public abstract void render(GuiGraphics guiGraphics, Minecraft minecraft);
    }

    public enum Mana {
        LEGACY {
            @Override
            public void render(GuiGraphics guiGraphics, Minecraft minecraft) {
                int widthMana = guiGraphics.guiWidth() - 23;
                int currentMana = ClientPacketHandler.getCurrentMana();
                int maxManaCount = ClientPacketHandler.getMaxMana() / 20;
                int currentManaToBlit;
                float ts;
                for (int i = 0; i < maxManaCount; i++) {
                    currentManaToBlit = currentMana - (i + 1) * 20;
                    guiGraphics.blit(ICON, widthMana, 10 + i * 12, 0, 34, 17, 16, SIZE, SIZE);
                    if (currentManaToBlit >= 0) {
                        guiGraphics.blit(ICON, widthMana + 2, 10 + i * 12, 18, 34, 13, 16, SIZE, SIZE);
                    } else if (currentManaToBlit + 20 >= 0) {
                        ts = ((float) (currentManaToBlit + 20)) / 20.0F;
                        guiGraphics.pose().pushPose();
                        guiGraphics.pose().translate(widthMana + 2 + 6.5F * (1 - ts), 10 + i * 12 + 8.5F * (1 - ts), 0.0F);
                        guiGraphics.pose().scale(ts, ts, 1.0F);
                        guiGraphics.blit(ICON, 0, 0, 18, 34, 13, 16, SIZE, SIZE);
                        guiGraphics.pose().popPose();
                    }
                }
            }
        };

        public abstract void render(GuiGraphics guiGraphics, Minecraft minecraft);
    }

    public enum Health {
        LEGACY {
            @Override
            public void render(GuiGraphics guiGraphics, Minecraft minecraft) {
                int widthHealth = guiGraphics.guiWidth() - 130;
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
                int heightToAbsorption = blit(heartBuff, maxHealth, currentHealth, lifeFruitHealth, guiGraphics, widthHealth, 10, 0, yLine);
                blit(0, countHeartAbsorptionHealth * 4, absorptionHealth, 0, guiGraphics, widthHealth, heightToAbsorption, 1, yLine);
            }

            private static void blitHeart(GuiGraphics guiGraphics, int type, int typeHeart, float num, int x, int y, int heartBuff) {
                float heartNum = (type == 1) ? 5.0F : 4.0F;
                heartBuff = (typeHeart == 0) ? heartBuff : 0;
                int heightUV = (typeHeart * 17);
                if (num == heartNum) {
                    guiGraphics.blit(ICON, x, y, ((type == 1) ? 54 : 44) + heartBuff, heightUV, 9, 16, SIZE, SIZE);
                } else if (num > 0.0F) {
                    float ts = num / heartNum;
                    guiGraphics.pose().pushPose();
                    guiGraphics.pose().translate(x + 4.5F * (1.0F - ts), y + 9.0F * (1.0F - ts), 0.0F);
                    guiGraphics.pose().scale(ts, ts, 1.0F);
                    guiGraphics.blit(ICON, 0, 0, ((type == 1) ? 54 : 44) + heartBuff, heightUV, 9, 16, SIZE, SIZE);
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
                        guiGraphics.blit(ICON, width + blitX - 1 + blitXFirst, height + blitY, 0, heightUV, 1, 16, SIZE, SIZE);
                        if (countToBlit + 1 == countHeart) {
                            guiGraphics.blit(ICON, width + blitX - 3 + blitXFirst, height + blitY, 13, heightUV, 17, 16, SIZE, SIZE);
                        } else {
                            guiGraphics.blit(ICON, width + blitX + blitXFirst, height + blitY, 2, heightUV, 10, 16, SIZE, SIZE);
                        }
                    } else if (countToBlit + 1 == countHeart) {
                        guiGraphics.blit(ICON, width + blitX - 3 + blitXFirst, height + blitY, 13, heightUV, 17, 16, SIZE, SIZE);
                    } else if ((countToBlit + 1) % 10 == 0) {
                        guiGraphics.blit(ICON, width + blitX + blitXFirst, height + blitY, 31, heightUV, 12, 16, SIZE, SIZE);
                    } else {
                        guiGraphics.blit(ICON, width + blitX + blitXFirst, height + blitY, 2, heightUV, 10, 16, SIZE, SIZE);
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
        };

        public abstract void render(GuiGraphics guiGraphics, Minecraft minecraft);
    }
}
