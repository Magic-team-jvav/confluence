package org.confluence.mod.client.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.component.RepeaterContents;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.item.crossbow.BaseTerraRepeaterItem;
import org.confluence.mod.mixed.IGui;

public class RepeaterHud implements LayeredDraw.Layer {
    public static void handle() {
        IGui.of(Minecraft.getInstance().gui).confluence$setShooting();
    }

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }

        ItemStack itemStack = player.getMainHandItem();
        if (!(itemStack.getItem() instanceof BaseTerraRepeaterItem)) {
            return;
        }

        RepeaterContents repeaterContents = itemStack.getOrDefault(ModDataComponentTypes.REPEATER_CONTENTS.get(), RepeaterContents.EMPTY);
        if (repeaterContents.isEmpty()) {
            return;
        }

        int x = guiGraphics.guiWidth() / 2;
        int y = guiGraphics.guiHeight() / 2;
        int slotSize = repeaterContents.getUedSlotSize();
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        for (int i = 0; i < slotSize; i++) {
            if (i > 5) {
                break;
            }
            ItemStack stack = repeaterContents.getStackInSlot(i);
            pose.pushPose();
            float g = i * 0.1f;
            float scale = 1 - g;
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            guiGraphics.setColor(1, 1, 1, (5 - i) / 6f);
            pose.translate(x - x * scale, y - y * scale, 0);
            pose.scale(scale, scale, 1);
            guiGraphics.renderItem(stack, x, y);
            guiGraphics.renderItemDecorations(minecraft.font, stack, x, y);
            x += Mth.floor(16f);
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            guiGraphics.setColor(1, 1, 1, 1);
            pose.popPose();
        }
        pose.popPose();
    }
}
