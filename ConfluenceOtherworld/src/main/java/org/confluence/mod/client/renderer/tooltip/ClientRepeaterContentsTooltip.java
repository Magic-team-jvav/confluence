package org.confluence.mod.client.renderer.tooltip;

import PortLib.extensions.net.minecraft.client.gui.GuiGraphics.PortGuiGraphicsExtension;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.component.RepeaterContents;
import org.confluence.mod.common.item.tooltipcomponent.RepeaterComponent;
import org.mesdag.portlib.client.gui.components.PortSprite;

public class ClientRepeaterContentsTooltip implements ClientTooltipComponent {
    private static final PortSprite ELLIPSIS_SPRITE = new PortSprite(Confluence.asResource("repeater/ellipsis"), 9, 3);
    private final RepeaterContents contents;

    public ClientRepeaterContentsTooltip(RepeaterComponent contents) {
        this.contents = contents.contents();
    }

    @Override
    public int getHeight() {
        return !contents.isEmpty() ? this.gridSizeY() * 20 : 0;
    }

    @Override
    public int getWidth(Font font) {
        return !contents.isEmpty() ? this.gridSizeX() * 18 + 2 : 0;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        int i = this.gridSizeX();
        int j = this.gridSizeY();
        int k = 0;

        f:
        for (int l = 0; l < j; l++) {
            for (int i1 = 0; i1 < i; i1++) {
                int x1 = x + i1 * 18 + 1;
                int y1 = y + l * 20 + 1;
                if (i1 > 4) {
                    PortGuiGraphicsExtension.blitSprite(guiGraphics, ELLIPSIS_SPRITE, x1 + 5, y1 + 8, 9, 3);
                    break f;
                }
                this.renderSlot(x1, y1, k++, guiGraphics, font);
            }
        }
    }

    private void renderSlot(int x, int y, int itemIndex, GuiGraphics guiGraphics, Font font) {
        if (itemIndex >= getSize()) {
            return;
        }
        ItemStack itemstack = getStackInSlot(itemIndex);
        guiGraphics.renderItem(itemstack, x + 1, y + 1, itemIndex);
        guiGraphics.renderItemDecorations(font, itemstack, x + 1, y + 1);
    }

    private ItemStack getStackInSlot(int itemIndex) {
        return this.contents.getStackInSlot(itemIndex);
    }

    private int getSize() {
        return this.contents.getUedSlotSize();
    }

    private int gridSizeX() {
        return Mth.clamp(getSize(), 0, 6);
    }

    private int gridSizeY() {
        return !contents.isEmpty() ? 1 : 0;
    }
}
