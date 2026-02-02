package org.confluence.terra_curio.client.renderer.tooltip;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.api.primitive.TooltipComponentsValue;
import org.confluence.terra_curio.client.handler.InformationHandler;
import org.confluence.terra_curio.common.item.IMultiFunctionCouldEnable;
import org.confluence.terra_curio.network.InfoDisablePacket;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.List;

public class MultiFunctionTooltip implements ClientTooltipComponent {
    private static final Component TOOLTIP = Component.translatable("tooltip.terra_curio.hold_and_scroll");
    public static final ResourceLocation HIGHLIGHT = TerraCurio.asResource("textures/gui/information/highlight.png");
    public static final float SCALE = 10.0F / 9.0F;
    public static int mouseScrollY = 0;
    public static boolean isShowing = false;

    private final List<TooltipComponentsValue.Storage> storages;
    private int mouseScrolledY = -1;
    private int width = 0;

    public MultiFunctionTooltip(List<TooltipComponentsValue.Storage> storages) {
        this.storages = storages;
    }

    @Override
    public int getHeight() {
        return isShiftKeyDown() ? storages.size() * 10 : 10;
    }

    @Override
    public int getWidth(@NotNull Font font) {
        if (isShiftKeyDown()) {
            isShowing = true;
            for (TooltipComponentsValue.Storage storage : storages) {
                int w = font.width(storage.text());
                if (w > width) this.width = w;
            }
            this.width += 10;
            return width;
        } else {
            isShowing = false;
        }
        return font.width(TOOLTIP);
    }

    @Override
    public void renderText(@NotNull Font font, int mouseX, int mouseY, @NotNull Matrix4f matrix, MultiBufferSource.@NotNull BufferSource bufferSource) {
        if (isShowing) {
            int size = storages.size();
            for (int i = 0; i < size; i++) {
                font.drawInBatch(storages.get(i).text(), mouseX + 10, mouseY + i * 10, -1, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 0xF000F0);
            }
            this.mouseScrolledY = Mth.clamp(mouseScrollY, 0, size) - 1;
        } else {
            font.drawInBatch(TOOLTIP, mouseX, mouseY, 0xAAAAAAAA, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 0xF000F0);
        }
    }

    @Override
    public void renderImage(@NotNull Font font, int x, int y, @NotNull GuiGraphics guiGraphics) {
        int size = storages.size();
        if (mouseScrollY > size) {
            mouseScrollY = 0;
        } else if (mouseScrollY < 0) {
            mouseScrollY = size;
        }
        if (isShowing) {
            for (int i = 0; i < size; i++) {
                PoseStack pose = guiGraphics.pose();
                pose.pushPose();
                pose.translate(x - 2.0F, y - 1.0F + i * 10.0F,0.0F);
                pose.scale(SCALE, SCALE, SCALE);
                TooltipComponentsValue.Storage storage = storages.get(i);
                guiGraphics.blit(storage.texture(), 1, 1, 0, 0, 7, 7, 7, 7);
                int index = IMultiFunctionCouldEnable.INDEX_MAP.getOrDefault(storage, -1);
                if (index != -1 && !InformationHandler.DISABLE[index]) {
                    guiGraphics.blit(HIGHLIGHT, 0, 0, 0,0 , 9, 9, 9, 9);
                }
                pose.popPose();
            }
            if (mouseScrolledY >= 0) {
                guiGraphics.renderOutline(x - 2, y - 2 + mouseScrolledY * 10, width + 2, 12, 0xFFFF0000);
            }
        } else {
            if (mouseScrollY > 0) {
                int index = IMultiFunctionCouldEnable.INDEX_MAP.getOrDefault(storages.get(mouseScrollY - 1), -1);
                if (index != -1) {
                    InformationHandler.DISABLE[index] = !InformationHandler.DISABLE[index];
                }
                InfoDisablePacket.sendToServer(InformationHandler.DISABLE);
            }
            mouseScrollY = 0;
        }
    }

    private static boolean isShiftKeyDown() {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), InputConstants.KEY_LSHIFT);
    }
}
