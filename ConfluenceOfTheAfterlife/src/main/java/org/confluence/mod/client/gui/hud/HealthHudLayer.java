package org.confluence.mod.client.gui.hud;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.common.item.common.EverBeneficialItem;
import org.confluence.mod.util.GuiUtils;

public class HealthHudLayer implements LayeredDraw.Layer {
    public static final ResourceLocation HEART_SINGLE_FANCY = Confluence.asResource("textures/gui/hud/heart_single_fancy.png");
    public static final ResourceLocation HEART_LEFT = Confluence.asResource("textures/gui/hud/heart_left.png");
    public static final ResourceLocation HEART_MIDDLE = Confluence.asResource("textures/gui/hud/heart_middle.png");
    public static final ResourceLocation HEART_RIGHT_FANCY = Confluence.asResource("textures/gui/hud/heart_right_fancy.png");
    public static final ResourceLocation HEART_RIGHT = Confluence.asResource("textures/gui/hud/heart_right.png");
    public static final ResourceLocation HEART_FILL = Confluence.asResource("textures/gui/hud/heart_fill.png");

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (!ClientConfigs.terraStyleHealth) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui || !GuiUtils.shouldDrawSurvivalElements(minecraft)) return;
        GuiUtils.setupOverlayRenderState(true, false);
        minecraft.getProfiler().push("health");

        int screenWidth = guiGraphics.guiWidth();
        float maxHealth = 0.0F;
        float currentHealth = 0.0F;
        float lifeFruitHealth = 0.0F; // 生命果

        Player player = minecraft.player;
        if (player != null) {
            maxHealth = player.getMaxHealth();
            currentHealth = player.getHealth();
            AttributeInstance instance = player.getAttribute(Attributes.MAX_HEALTH);
            AttributeModifier modifier = instance.getModifier(EverBeneficialItem.LIFE_FRUITS.id());
            if (modifier != null) {
                lifeFruitHealth = (float) modifier.amount();
            }
        }

        //以下是为了画出ui做的坐标计算
        int maxHeartCount = ((int) maxHealth) / 4;
        int currentHealthCount = ((int) currentHealth) / 4;
        float lastHeartSize = 0.0F;
        if (((int) maxHealth) % 4 != 0) {
            maxHeartCount += 1;
        }
        if (((int) currentHealth) % 4 != 0) {
            currentHealthCount += 1;
            lastHeartSize = (currentHealth % 4) / 4;
        }
        int count;
        int heartCount;
        int smP = maxHeartCount / 10;
        int heartSmP = currentHealthCount / 10;
        int move = 0;
        int heartMove = 0;
        if (maxHeartCount % 10 != 0) {
            smP += 1;
        }
        int cSmP = smP;
        if (currentHealthCount % 10 != 0) {
            heartSmP += 1;
        }
        int line = smP * 14;
        int heartLine = heartSmP * 14;

        //绘制血量槽
        while (smP > 0) {
            if (smP > 1) {
                count = 8;
            } else if (maxHeartCount % 10 != 0) {
                count = maxHeartCount % 10 - 2;
            } else {
                count = 8;
            }
            if (cSmP == 1) {
                move = (8 - count) * 12;
            }
            int setCount = 8 - count;
            if (maxHeartCount <= 1) {//绘制血量槽左端
                guiGraphics.blit(HEART_SINGLE_FANCY, screenWidth - 20 - 28 - setCount * 12 + move, 10 + line - smP * 14, 0, 0, 18, 19, 18, 19);
            } else {
                guiGraphics.blit(HEART_LEFT, screenWidth - 20 - (count + 1) * 12 - 26 - setCount * 12 + move, 12 + line - smP * 14, 0, 0, 14, 15, 14, 15);

                //绘制血量槽中部
                if (count >= 1) {
                    while (count > 0) {
                        guiGraphics.blit(HEART_MIDDLE, screenWidth - 20 - count * 12 - 24 - setCount * 12 + move, 12 + line - smP * 14, 0, 0, 12, 15, 12, 15);
                        count -= 1;
                    }
                }

                //绘制血量槽右端
                if (smP == 1) {//按情况判断使用哪种右端材质
                    guiGraphics.blit(HEART_RIGHT_FANCY, screenWidth - 20 - 28 - setCount * 12 + move, 10 + line - smP * 14, 0, 0, 18, 19, 18, 19);
                } else {
                    guiGraphics.blit(HEART_RIGHT, screenWidth - 20 - 24 - setCount * 12 + move, 12 + line - smP * 14, 0, 0, 13, 15, 13, 15);
                }
            }
            smP -= 1;
        }

        //绘制血量
        while (heartSmP > 0) {
            if (heartSmP > 1) {
                heartCount = 8;
            } else if (currentHealthCount % 10 != 0) {
                heartCount = currentHealthCount % 10 - 2;
            } else {
                heartCount = 8;
            }
            if (heartSmP == 1) {
                smP = maxHeartCount / 10;
                if (maxHeartCount % 10 != 0) {
                    smP += 1;
                }
                if (maxHeartCount % 10 != 0) {
                    count = maxHeartCount % 10 - 2;
                } else {
                    count = 8;
                }
                if (smP == 1) {
                    heartMove = (8 - count) * 12;
                }
            }
            int setHeartCount = 8 - heartCount;
            if (currentHealthCount <= 1) {//绘制血条左端
                guiGraphics.blit(HEART_FILL, screenWidth - 20 - 28 + 4 - setHeartCount * 12 + heartMove, 10 + heartLine - heartSmP * 14 + 4, 0, 0, 11, 11, 11, 11);
            } else {
                if ((currentHealthCount % 10) != 1 || (heartSmP == 1 && lastHeartSize == 0.0f) || heartSmP >= 2) {
                    guiGraphics.blit(HEART_FILL, screenWidth - 20 - (heartCount + 1) * 12 - 26 + 2 - setHeartCount * 12 + heartMove, 12 + heartLine - heartSmP * 14 + 2, 0, 0, 11, 11, 11, 11);
                }
                //绘制血条中部

                if (heartCount >= 1) {
                    while (heartCount > 0) {
                        guiGraphics.blit(HEART_FILL, screenWidth - 20 - heartCount * 12 - 24 - setHeartCount * 12 + heartMove, 12 + heartLine - heartSmP * 14 + 2, 0, 0, 11, 11, 11, 11);
                        heartCount -= 1;
                    }
                }

                //绘制血条右端
                if (heartSmP == 1 && lastHeartSize != 0.0f) {
                    PoseStack pose = guiGraphics.pose();
                    pose.pushPose();
                    float delta = (11 - 11 * lastHeartSize) / 2;
                    pose.translate(screenWidth - 20 - 24 - setHeartCount * 12 + heartMove + delta, 12 + heartLine - heartSmP * 14 + 2 + delta, 0.0F);
                    pose.scale(lastHeartSize, lastHeartSize, 0.0F);
                    guiGraphics.blit(HEART_FILL, 0, 0, 0, 0, 11, 11, 11, 11);
                    pose.popPose();
                } else {
                    guiGraphics.blit(HEART_FILL, screenWidth - 20 - 24 - setHeartCount * 12 + heartMove, 12 + heartLine - heartSmP * 14 + 2, 0, 0, 11, 11, 11, 11);
                }
            }
            heartSmP -= 1;
        }

        minecraft.getProfiler().pop();
    }
}
