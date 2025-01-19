package org.confluence.mod.client.gui.hud;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.common.item.common.EverBeneficialItem;
import org.confluence.mod.util.ClientUtils;

import java.util.ArrayList;
import java.util.List;

public class HealthHudLayer implements LayeredDraw.Layer {
    public static final ResourceLocation ICON = Confluence.asResource("textures/gui/hud/icon.png");
    public static final Integer SIZE = 128;
    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (!ClientConfigs.terraStyleHealth) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui || !ClientUtils.shouldDrawSurvivalElements(minecraft)) return;
        ClientUtils.setupOverlayRenderState(true, false);
        minecraft.getProfiler().push("health");

        int width = guiGraphics.guiWidth() - 130;
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
        int yLine = 4 + (int) (6.0F / (float) ((countHeart + countHeartAbsorptionHealth - 1) / 20 + 1));
        int heightToAbsorption = blit(heartBuff, maxHealth, currentHealth, lifeFruitHealth, guiGraphics, width, 10, 0, yLine);
        blit(0, countHeartAbsorptionHealth * 4, absorptionHealth, 0, guiGraphics, width, heightToAbsorption, 1, yLine);
        minecraft.getProfiler().pop();
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
        List<Float> heartNumList = new ArrayList<>();
        float currentHealthToType = currentHealth;
        float heartNum;
        boolean ifLifeFruit;
        for (int i = 0; i < countHeart; i ++) {
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
        for (int countToBlit = 0; countToBlit < countHeart; countToBlit ++) {
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
            blitHeart(guiGraphics, type, typeHeart, heartNumList.get(countToBlit), width + blitX + blitXFirst + 1, height + blitY, heartBuff);
        }
        return (height + blitY + yLine);
    }
}
