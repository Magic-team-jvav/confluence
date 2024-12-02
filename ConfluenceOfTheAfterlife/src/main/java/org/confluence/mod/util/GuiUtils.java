package org.confluence.mod.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public final class GuiUtils {
    public static void drawImage(ResourceLocation loc, GuiGraphics g, int x, int y, int wid, int hig, int imWid, int imHig) {
        g.blit(loc, x, y, 0, 0, wid, hig, imWid, imHig);
    }

    public static void drawImage(ResourceLocation loc, GuiGraphics g, int x, int y, int imWid, int imHig) {
        drawImage(loc, g, x, y, imWid, imHig, imWid, imHig);
    }

    public static Button buildIconButton(GuiGraphics g, ResourceLocation loc, int x, int y, int wid, int hig, int icWid, int icHig, Button.OnPress onPress) {
        drawImage(loc, g, x + ((wid - icWid) / 2), y + ((hig - icHig) / 2), icWid, icHig);
        return Button.builder(Component.empty(), onPress).bounds(x, y, wid, hig).build();
    }

    public static Button buildIconButton(GuiGraphics g, ResourceLocation loc, int x, int y, int wid, int hig, int icWid, int icHig, Button.OnPress onPress, Component hoverText) {
        drawImage(loc, g, x + ((wid - icWid) / 2), y + ((hig - icHig) / 2), icWid, icHig);
        return Button.builder(Component.empty(), onPress).bounds(x, y, wid, hig).tooltip(Tooltip.create(hoverText)).build();
    }

    public static Button buildItemButton(GuiGraphics g, Item it, int x, int y, int wid, int hig, int icWid, int icHig, Button.OnPress onPress, Component hoverText, Minecraft mc) {
        BakedModel model = mc.getItemRenderer().getModel(it.getDefaultInstance(), null, null, 0);
        if (model instanceof SimpleBakedModel simple) {
            g.blit(x + ((wid - icWid) / 2), y + ((hig - icHig) / 2), 0, icWid, icHig, simple.getParticleIcon());
        }
        return Button.builder(Component.empty(), onPress).bounds(x, y, wid, hig).tooltip(Tooltip.create(hoverText)).build();
    }


    public static void drawItemWithoutTooltip(GuiGraphics guiGraphics, Item item, Minecraft mc, float x, float y, int w, int h, float scale) {
        drawItem(guiGraphics, item, mc, x, y, w, h, scale, false, 0, 0);
    }

    public static void drawItem(GuiGraphics guiGraphics, Item item, Minecraft mc, float x, float y, int w, int h, float scale, boolean renderTooltip, int mouseX, int mouseY) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 1);
        guiGraphics.pose().scale(scale, scale, 0);
        guiGraphics.renderItem(item.getDefaultInstance(), 0, 0);

        guiGraphics.pose().popPose();
        if (renderTooltip && mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h) {
            guiGraphics.renderTooltip(mc.font, item.getDefaultInstance().getTooltipLines(Item.TooltipContext.EMPTY, mc.player, TooltipFlag.ADVANCED), Optional.empty(), mouseX, mouseY);
        }
    }

    public static void setupOverlayRenderState(boolean blend, boolean depthTest) {
        if (blend) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
        } else {
            RenderSystem.disableBlend();
        }

        if (depthTest) {
            RenderSystem.enableDepthTest();
        } else {
            RenderSystem.disableDepthTest();
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
    }

    public static boolean shouldDrawSurvivalElements(Minecraft minecraft) {
        return minecraft.gameMode.canHurtPlayer() && minecraft.getCameraEntity() instanceof Player;
    }
}
