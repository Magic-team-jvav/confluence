package org.confluence.mod.client.gui.hud;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.component.RepeaterContents;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.item.crossbow.BaseTerraRepeaterItem;

public class RepeaterHud implements LayeredDraw.Layer {
    private static float t = 0;
    private static float oldRepeaterCrosshairAngle = 0;

    public static void setShooting() {
        t = 3;
    }

    public static void tick(float partialTicks) {
        t = Math.max(0, t - partialTicks);
    }

    public static float getShooting() {
        return t;
    }

    public static void repeaterRenderCrosshair(GuiGraphics instance, ResourceLocation sprite, int x, int y, int width, int height, Operation<Void> original, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            original.call(instance, sprite, x, y, width, height);
            return;
        }
        PoseStack pose = instance.pose();
        pose.pushPose();
        float v = width / 2f;
        float v1 = height / 2f;
        pose.translate(x + v, y + v1, 0);
        ItemStack itemStack = player.getMainHandItem();

        float end = 0;
        if (itemStack.getItem() instanceof BaseTerraRepeaterItem repeaterItem) {
            if (player.isUsingItem() && player.getUseItem().equals(itemStack)) {
                end = oldRepeaterCrosshairAngle + (float) 360 / repeaterItem.getReloadSpeed(player, itemStack);
            } else if (!itemStack.getOrDefault(ModDataComponentTypes.REPEATER_CONTENTS.get(), RepeaterContents.EMPTY).isEmpty()) {
                end = 45;
            }
        }
        float timeDeltaPartialTick = deltaTracker.getGameTimeDeltaPartialTick(true);
        float lerp = Mth.lerp(timeDeltaPartialTick / 2, oldRepeaterCrosshairAngle, end);
        pose.mulPose(Axis.ZN.rotationDegrees(lerp));
        float scale = 1f + (0.5f * (getShooting() / 2));
        pose.scale(scale, scale, 1);
        oldRepeaterCrosshairAngle = lerp;
        tick(timeDeltaPartialTick);
        pose.translate(-v, -v1, 0);
        original.call(instance, sprite, 0, 0, width, height);
        pose.popPose();
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
