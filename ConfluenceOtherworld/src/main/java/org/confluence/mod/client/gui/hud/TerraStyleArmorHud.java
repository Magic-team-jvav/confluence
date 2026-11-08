package org.confluence.mod.client.gui.hud;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.TranslatableEnum;
import org.confluence.lib.util.LibRenderUtils;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.event.ModClientSetups;

import java.util.Locale;

import static org.confluence.mod.util.ClientUtils.*;

public class TerraStyleArmorHud implements LayeredDraw.Layer {
    private static final int[] ARMOR = new int[]{0x979191, 0xd8c849, 0x8097b8, 0x3b2754, 0xea5d39};
    private static final int[] ARMOR_LOW = new int[]{0x5d4b4b, 0x645241, 0x515277, 0x201735, 0xb50000};
    private static final int[] ARMOR_HIGH = new int[]{0xffffeb, 0xfff9b7, 0xf6d8eb, 0x5b3b6e, 0xffffeb};

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        // 泰拉界面：防御只在背包里显示（护饰品左侧的盾牌），平时屏幕上不显示
        if (!ClientConfigs.terraStyleArmor || ClientConfigs.terrariaInterface) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui || !LibRenderUtils.shouldDrawSurvivalElements(minecraft)) return;
        LibRenderUtils.setupOverlayRenderState(true, false);
        minecraft.getProfiler().push("terra_style_hud");

        // 泰拉界面：用泰拉式护盾（若玩家选的是原版位置的 OVERLAY，则换成横排的 LEGACY）
        Armor style = ClientConfigs.armorStyle;
        if (ClientConfigs.terrariaInterface && style == Armor.OVERLAY) style = Armor.LEGACY_HORIZONTAL;
        style.render(guiGraphics, minecraft);

        minecraft.getProfiler().pop();
    }

    public enum Armor implements TranslatableEnum {
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
        },
        OVERLAY {
            @Override
            public void render(GuiGraphics guiGraphics, Minecraft minecraft) {
                float armor = 0.0F;
                Player player = minecraft.player;
                if (player != null) {
                    armor = player.getArmorValue();
                }
                int widthArmor = guiGraphics.guiWidth() / 2 - 91;
                int heightArmor = guiGraphics.guiHeight() - minecraft.gui.leftHeight;
                minecraft.gui.leftHeight += 10;
                RandomSource random = RandomSource.create(59160153);
                colorDraw(guiGraphics, minecraft, random, ModClientSetups.OVERLAY_SPRITE, ARMOR, ARMOR_HIGH, ARMOR_LOW, armor, widthArmor, heightArmor, OVERLAY_SIZE, 20, true);
            }
        };

        private static void draw(GuiGraphics guiGraphics, Minecraft minecraft, int type) {
            drawAt(guiGraphics, minecraft, type, guiGraphics.guiWidth() - 26, guiGraphics.guiHeight() - 28);
        }

        /// 泰拉背包里的防御盾牌（23×25，数字在盾上）
        public static void drawShield(GuiGraphics guiGraphics, Minecraft minecraft, int x, int y) {
            drawAt(guiGraphics, minecraft, 1, x, y);
        }

        private static void drawAt(GuiGraphics guiGraphics, Minecraft minecraft, int type, int widthArmor, int heightArmor) {
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
                case 3 -> "-".repeat(Math.max(armor.length(), armorToughness.length()));
                default -> "/";
            };
            int textWidthArmor = minecraft.font.width(Component.literal(armor));
            int textWidthArmorToughness = minecraft.font.width(Component.literal(armorToughness));
            int textWidth = minecraft.font.width(Component.literal(text));
            int textHeight = minecraft.font.lineHeight;
            int colorWhite = 0xFFFFFF;
            int colorArmor = 0xEEB354;
            int colorArmorToughness = 0x54E4EE;
            float widthOffset;
            float v = textWidthArmor * 0.5F;
            float v1 = textWidth * 0.5F;
            float v2 = textHeight * 0.5F;
            if (armorToughnessNum == 0) {
                widthOffset = 11.5F + v - 22;
            } else {
                widthOffset = switch (type) {
                    case 1 -> 13.5F + v1 + textWidthArmorToughness - 22;
                    case 3 -> 11.5F + Math.max(textWidthArmor, textWidthArmorToughness) * 0.5F - 22;
                    default -> 11.5F + v1 + textWidthArmorToughness - 22;
                };
            }
            widthOffset = widthOffset > 0 ? widthOffset : 0;
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(-widthOffset, 0.0F, 0.0F);
            guiGraphics.blitSprite(ModClientSetups.LEGACY_SPRITE, LEGACY_SIZE, LEGACY_SIZE, 0, 51, widthArmor, heightArmor, 23, 25);
            guiGraphics.pose().popPose();
            if (armorToughnessNum == 0) {
                drawString(guiGraphics, minecraft.font, armor, widthArmor + 11.5F - v - widthOffset, heightArmor + 12.5F - v2, colorArmor);
            } else {
                drawString(guiGraphics, minecraft.font, text, widthArmor + 11.5F - v1 - widthOffset, heightArmor + 12.5F - v2, colorWhite);
                switch (type) {
                    case 1:
                        drawString(guiGraphics, minecraft.font, armor, widthArmor + 9.5F - v1 - textWidthArmor - widthOffset, heightArmor + 12.5F - v2, colorArmor);
                        drawString(guiGraphics, minecraft.font, armorToughness, widthArmor + 13.5F + v1 - widthOffset, heightArmor + 12.5F - v2, colorArmorToughness);
                        break;
                    case 3:
                        drawString(guiGraphics, minecraft.font, armor, widthArmor + 11.5F - v - widthOffset, heightArmor + 10.5F - textHeight, colorArmor);
                        drawString(guiGraphics, minecraft.font, armorToughness, widthArmor + 11.5F - (textWidthArmorToughness * 0.5F) - widthOffset, heightArmor + 14.5F, colorArmorToughness);
                        break;
                    default:
                        drawString(guiGraphics, minecraft.font, armor, widthArmor + 11.5F - v1 - textWidthArmor - widthOffset, heightArmor + 9.5F - v2, colorArmor);
                        drawString(guiGraphics, minecraft.font, armorToughness, widthArmor + 11.5F + v1 - widthOffset, heightArmor + 15.5F - v2, colorArmorToughness);
                }
            }
        }

        public abstract void render(GuiGraphics guiGraphics, Minecraft minecraft);

        @Override
        public Component getTranslatedName() {
            return Component.translatable("confluence.configuration.armorStyle." + name().toLowerCase(Locale.ROOT));
        }
    }
}
