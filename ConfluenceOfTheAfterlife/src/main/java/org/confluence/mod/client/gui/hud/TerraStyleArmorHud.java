package org.confluence.mod.client.gui.hud;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.TranslatableEnum;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.util.ClientUtils;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Locale;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TerraStyleArmorHud implements LayeredDraw.Layer {
    private static final ResourceLocation LEGACY_TEXTURE = Confluence.asResource("textures/gui/hud/icon.png");
    private static final ResourceLocation OVERLAY_TEXTURE = Confluence.asResource("textures/gui/hud/overlay.png");
    private static final int LEGACY_SIZE = 128;
    private static final int OVERLAY_SIZE = 128;

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (!ClientConfigs.terraStyleArmor) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui || !ClientUtils.shouldDrawSurvivalElements(minecraft)) return;
        ClientUtils.setupOverlayRenderState(true, false);
        minecraft.getProfiler().push("terra_style_hud");

        ClientConfigs.armorStyle.render(guiGraphics, minecraft);

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
        };

        private static void drawString(GuiGraphics guiGraphics, Font font, @Nullable String text, float x, float y, int color) {
            guiGraphics.drawString(font, text, x + 1, y, 0x000000, false);
            guiGraphics.drawString(font, text, x - 1, y, 0x000000, false);
            guiGraphics.drawString(font, text, x, y + 1, 0x000000, false);
            guiGraphics.drawString(font, text, x, y - 1, 0x000000, false);
            guiGraphics.drawString(font, text, x, y, color, false);
        }

        private static void draw(GuiGraphics guiGraphics, Minecraft minecraft, int type) {
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
            guiGraphics.blit(LEGACY_TEXTURE, widthArmor, heightArmor, 0, 51, 23, 25, LEGACY_SIZE, LEGACY_SIZE);
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
