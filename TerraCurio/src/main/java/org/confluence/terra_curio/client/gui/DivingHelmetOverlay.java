package org.confluence.terra_curio.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.common.init.TCItems;
import org.jetbrains.annotations.NotNull;

public class DivingHelmetOverlay implements LayeredDraw.Layer {
    private static final ResourceLocation TEXTURE = TerraCurio.asResource("textures/gui/diving_helmet.png");

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;
        if (minecraft.options.getCameraType().isFirstPerson()) {
            ItemStack itemstack = minecraft.player.getInventory().getArmor(3);
            if (itemstack.is(TCItems.DIVING_HELMET.get())) {
                float f = (float)Math.min(guiGraphics.guiWidth(), guiGraphics.guiHeight());
                float f1 = Math.min((float)guiGraphics.guiWidth() / f, (float)guiGraphics.guiHeight() / f);
                int i = Mth.floor(f * f1);
                int j = Mth.floor(f * f1);
                int k = (guiGraphics.guiWidth() - i) / 2;
                int l = (guiGraphics.guiHeight() - j) / 2;
                int i1 = k + i;
                int j1 = l + j;
                RenderSystem.enableBlend();
                guiGraphics.blit(TEXTURE, k, l, -90, 0.0F, 0.0F, i, j, i, j);
                RenderSystem.disableBlend();
                guiGraphics.fill(RenderType.guiOverlay(), 0, j1, guiGraphics.guiWidth(), guiGraphics.guiHeight(), -90, -16777216);
                guiGraphics.fill(RenderType.guiOverlay(), 0, 0, guiGraphics.guiWidth(), l, -90, -16777216);
                guiGraphics.fill(RenderType.guiOverlay(), 0, l, k, j1, -90, -16777216);
                guiGraphics.fill(RenderType.guiOverlay(), i1, l, guiGraphics.guiWidth(), j1, -90, -16777216);
            }
        }
    }
}
