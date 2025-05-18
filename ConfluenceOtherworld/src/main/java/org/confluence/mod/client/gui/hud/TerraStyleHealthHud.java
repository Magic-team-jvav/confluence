package org.confluence.mod.client.gui.hud;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
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
import org.confluence.lib.util.LibClientUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.common.item.common.EverBeneficialItem;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Locale;

import static org.confluence.mod.util.ClientUtils.colorDraw;
import static org.confluence.mod.util.ClientUtils.drawString;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TerraStyleHealthHud implements LayeredDraw.Layer {
    private static final ResourceLocation LEGACY_TEXTURE = Confluence.asResource("textures/gui/hud/icon.png");
    private static final ResourceLocation OVERLAY_TEXTURE = Confluence.asResource("textures/gui/hud/overlay.png");
    private static final int LEGACY_SIZE = 128;
    private static final int OVERLAY_SIZE = 128;
    private static final int[] HEALTH = new int[]{0xab311e, 0x5d11ba, 0x41a9ba, 0x37c438, 0xeed536};
    private static final int[] HEALTH_LOW = new int[]{0xab1f5d, 0x9d44ac, 0x12f7dd, 0x1fab7f, 0xf7b60b};
    private static final int[] HEALTH_HIGH = new int[]{0xffb5b5, 0xd6e7eb, 0xbdced0, 0xd6eead, 0xeff4ce};

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (!ClientConfigs.terraStyleHealth) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui || !LibClientUtils.shouldDrawSurvivalElements(minecraft)) return;
        LibClientUtils.setupOverlayRenderState(true, false);
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
                float absorptionHealth = 0.0F;
                Player player = minecraft.player;
                if (player != null) {
                    maxHealth = player.getMaxHealth();
                    currentHealth = player.getHealth();
                    absorptionHealth = player.getAbsorptionAmount();
                }
                int widthHealth = guiGraphics.guiWidth() / 2 - 91;
                int heightHealth = guiGraphics.guiHeight() - minecraft.gui.leftHeight;
                String abHealth = String.format("%.1f", absorptionHealth);
                minecraft.gui.leftHeight += 10;
                RandomSource random = RandomSource.create(114514);
                colorDraw(guiGraphics, minecraft, random, OVERLAY_TEXTURE, HEALTH, HEALTH_HIGH, HEALTH_LOW, maxHealth, currentHealth, widthHealth, heightHealth, OVERLAY_SIZE, 0, true);
                if (absorptionHealth > 0.0F) {
                    guiGraphics.blit(Confluence.asResource("textures/gui/hud/icon_0.png"), widthHealth, heightHealth, 0, 0, 81, 9, 128, 16);
                    drawString(guiGraphics, minecraft.font, abHealth, widthHealth + 41F - (minecraft.font.width(Component.literal(abHealth))) / 2.0F, heightHealth + 1, 0x5efff8);
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
