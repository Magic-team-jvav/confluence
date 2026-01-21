package org.confluence.mod.client.gui.hud;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import org.confluence.mod.common.component.RepeaterContents;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.item.crossbow.BaseTerraRepeaterItem;
import org.confluence.mod.mixed.IGui;

public class RepeaterHud implements LayeredDraw.Layer {
    public static final boolean DYNAMICCROSSHAIR = ModList.get().isLoaded("dynamiccrosshair");

    public static void handle() {
        IGui.of(Minecraft.getInstance().gui).confluence$setShooting();
    }

    public static void renderCrosshair(Gui gui, GuiGraphics instance, ResourceLocation sprite, int x, int y, int width, int height, Operation<Void> original, DeltaTracker deltaTracker) {
        IGui igui = IGui.of(gui);
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || DYNAMICCROSSHAIR) {
            original.call(instance, sprite, x, y, width, height);
            return;
        }
        PoseStack pose = instance.pose();
        pose.pushPose();

        float v = width / 2f;
        float v1 = height / 2f;
        pose.translate(v, v1, 0);
        ItemStack itemStack = player.getMainHandItem();

        float end = 0;
        if (itemStack.getItem() instanceof BaseTerraRepeaterItem repeaterItem) {
            if (player.isUsingItem() && player.getUseItem().equals(itemStack)) {
                end = Math.clamp(igui.confluence$getOldRepeaterCrosshairAngle() + (float) 360 / repeaterItem.getReloadSpeed(player, itemStack), 0, 720);
            } else if (!itemStack.getOrDefault(ModDataComponentTypes.REPEATER_CONTENTS.get(), RepeaterContents.EMPTY).isEmpty()) {
                end = 45;
            }
        }

        float timeDeltaPartialTick = deltaTracker.getGameTimeDeltaPartialTick(true);
        float v2 = Mth.lerp(timeDeltaPartialTick / 2, igui.confluence$getOldRepeaterCrosshairAngle(), end);
        pose.mulPose(Axis.ZN.rotationDegrees(v2 % 360));

        float scale = 1f + (0.5f * (igui.confluence$getScale() / 2));
        pose.scale(scale, scale, 1);

        igui.confluence$setOldRepeaterCrosshairAngle(v2);
        igui.confluence$setScale(Math.max(1, igui.confluence$getScale() - timeDeltaPartialTick));

        pose.translate(v, v1, 0);
        original.call(instance, sprite, x, y, width, height);

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

        final int I = 5;
        final float I1 = I + 1f;

        for (int i = 0; i < slotSize; i++) {
            if (i > I) {
                break;
            }
            ItemStack stack = repeaterContents.getStackInSlot(i);
            pose.pushPose();
            float g = i * 0.1f;
            float scale = 1 - g;
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            guiGraphics.setColor(1, 1, 1, (I - i) / I1);
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
