package org.confluence.mod.client.gui.hud;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.TranslatableEnum;
import org.confluence.lib.util.LibClientUtils;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.item.ConsumableItems;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Locale;

import static org.confluence.mod.util.ClientUtils.*;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TerraStyleHealthHud implements LayeredDraw.Layer {
    private static final int[] HEALTH = new int[]{0xab311e, 0x5d11ba, 0x41a9ba, 0x37c438, 0xeed536};
    private static final int[] HEALTH_LOW = new int[]{0xab1f5d, 0x9d44ac, 0x12f7dd, 0x1fab7f, 0xf7b60b};
    private static final int[] HEALTH_HIGH = new int[]{0xffb5b5, 0xd6e7eb, 0xbdced0, 0xd6eead, 0xeff4ce};

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (!ClientConfigs.terraStyleHealth) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui || !LibClientUtils.shouldDrawSurvivalElements(minecraft))
            return;
        LibClientUtils.setupOverlayRenderState(true, false);
        minecraft.getProfiler().push("terra_style_hud");

        ClientConfigs.healthStyle.render(guiGraphics, minecraft);

        minecraft.getProfiler().pop();
    }

    public enum Health implements TranslatableEnum {
        LEGACY {
            @Override
            public void render(GuiGraphics guiGraphics, Minecraft minecraft) {
                int widthHealth = guiGraphics.guiWidth() - 128 + ClientConfigs.healthOffsetX;
                int heightHealth = 4 + ClientConfigs.healthOffsetY;
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
                    AttributeInstance maxHealthAttr = player.getAttribute(Attributes.MAX_HEALTH);
                    if (maxHealthAttr != null) {
                        AttributeModifier modifier = maxHealthAttr.getModifier(ConsumableItems.LIFE_FRUIT.get().getModifierId());
                        if (modifier != null) {
                            lifeFruitHealth = (int) modifier.amount();
                        }
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
                    guiGraphics.blitSprite(LEGACY_TEXTURE, LEGACY_SIZE, LEGACY_SIZE, ((type == 1) ? 54 : 44) + heartBuff, heightUV, x, y, 9, 16);
                } else if (num > 0.0F) {
                    float ts = num / heartNum;
                    guiGraphics.pose().pushPose();
                    guiGraphics.pose().translate(x + 4.5F * (1.0F - ts), y + 9.0F * (1.0F - ts), 0.0F);
                    guiGraphics.pose().scale(ts, ts, 1.0F);
                    guiGraphics.blitSprite(LEGACY_TEXTURE, LEGACY_SIZE, LEGACY_SIZE, ((type == 1) ? 54 : 44) + heartBuff, heightUV, 0, 0, 9, 16);
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
                        guiGraphics.blitSprite(LEGACY_TEXTURE, LEGACY_SIZE, LEGACY_SIZE, 0, heightUV, width + blitX - 1 + blitXFirst, height + blitY, 1, 16);
                        if (countToBlit + 1 == countHeart) {
                            guiGraphics.blitSprite(LEGACY_TEXTURE, LEGACY_SIZE, LEGACY_SIZE, 13, heightUV, width + blitX - 3 + blitXFirst, height + blitY, 17, 16);
                        } else {
                            guiGraphics.blitSprite(LEGACY_TEXTURE, LEGACY_SIZE, LEGACY_SIZE, 2, heightUV, width + blitX + blitXFirst, height + blitY, 10, 16);
                        }
                    } else if (countToBlit + 1 == countHeart) {
                        guiGraphics.blitSprite(LEGACY_TEXTURE, LEGACY_SIZE, LEGACY_SIZE, 13, heightUV, width + blitX - 3 + blitXFirst, height + blitY, 17, 16);
                    } else if ((countToBlit + 1) % 10 == 0) {
                        guiGraphics.blitSprite(LEGACY_TEXTURE, LEGACY_SIZE, LEGACY_SIZE, 31, heightUV, width + blitX + blitXFirst, height + blitY, 12, 16);
                    } else {
                        guiGraphics.blitSprite(LEGACY_TEXTURE, LEGACY_SIZE, LEGACY_SIZE, 2, heightUV, width + blitX + blitXFirst, height + blitY, 10, 16);
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
                int heartBuff = 0;
                boolean hardcore = false;
                if (player != null) {
                    heartBuff = (player.hasEffect(MobEffects.POISON)) ? 1 : heartBuff;
                    heartBuff = (player.getTicksFrozen() >= 140) ? 2 : heartBuff;
                    heartBuff = (player.hasEffect(MobEffects.WITHER)) ? 3 : heartBuff;
                    heartBuff = (player.hasEffect(ModEffects.ACID_VENOM)) ? 4 : heartBuff;
                    absorptionHealth = player.getAbsorptionAmount();
                    maxHealth = player.getMaxHealth();
                    currentHealth = player.getHealth();
                    hardcore = player.level().getLevelData().isHardcore();
                }
                int white = 0xFFFFFF;
                int widthHealth = guiGraphics.guiWidth() / 2 - 91 + ClientConfigs.healthOffsetX;
                int heightHealth = guiGraphics.guiHeight() - minecraft.gui.leftHeight + ClientConfigs.healthOffsetY;
                String abHealth = String.format("%.1f", absorptionHealth);
                int healthI = Math.min((int) Math.ceil(currentHealth), 20);
                int absorptionHealthI = Math.min((int) Math.ceil(absorptionHealth), 20);
                minecraft.gui.leftHeight += 10;
                RandomSource random = RandomSource.create(114514);
                colorDraw(guiGraphics, minecraft, random, OVERLAY_TEXTURE, HEALTH, HEALTH_HIGH, HEALTH_LOW, maxHealth, currentHealth, widthHealth, heightHealth, OVERLAY_SIZE, hardcore ? 40 : 0, true);
                if (absorptionHealth > 0.0F) {
                    draw(widthHealth, heightHealth, guiGraphics, absorptionHealthI, white, white, white, OVERLAY_TEXTURE, OVERLAY_SIZE, 0, 50, true, 1, 20);
                }
                if (heartBuff > 0) {
                    draw(widthHealth, heightHealth, guiGraphics, healthI, white, white, white, OVERLAY_TEXTURE, OVERLAY_SIZE, heartBuff * 20, 50, true, 1, 20);
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
