package org.confluence.mod.client.gui.container;

import PortLib.extensions.net.minecraft.world.item.ItemStack.PortItemStackExtension;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.item.PaintItems;
import org.confluence.mod.common.init.item.VanityArmorItems;
import org.confluence.mod.common.item.common.BaseDyeItem;
import org.confluence.mod.common.item.paint.PaintItem;
import org.confluence.mod.common.menu.DyeMixMenu;
import org.confluence.mod.network.c2s.DyeMixPacketC2S;
import org.confluence.mod.network.c2s.OpenMenuPacketC2S;
import org.mesdag.portlib.wrapper.common.util.PortTriState;

public class DyeMixScreen extends AbstractContainerScreen<DyeMixMenu> {
    private static final ResourceLocation BACKGROUND = Confluence.asResource("textures/gui/container/dye_mix.png");
    private EditBox editBox;
    private int rgb = 0;
    private PortTriState isDye = PortTriState.DEFAULT;
    private ItemStack stack = ItemStack.EMPTY;

    public DyeMixScreen(DyeMixMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        menu.registerUpdateListener(() -> {
            ItemStack red = menu.getSlot(0).getItem();
            ItemStack green = menu.getSlot(1).getItem();
            ItemStack blue = menu.getSlot(2).getItem();
            if (red.getItem().getClass() == green.getItem().getClass() && green.getItem().getClass() == blue.getItem().getClass()) {
                this.isDye = red.getItem() instanceof BaseDyeItem ? PortTriState.TRUE : PortTriState.FALSE;
            } else {
                this.isDye = PortTriState.DEFAULT;
            }
        });
        addRenderableWidget(Button.builder(Component.translatable("button.confluence.dye_vat"), button -> {
            LocalPlayer player = minecraft.player;
            if (player != null) {
                ItemStack stack = player.containerMenu.getCarried();
                player.containerMenu.setCarried(ItemStack.EMPTY);
                OpenMenuPacketC2S.sendToServer(OpenMenuPacketC2S.DYE_VAT_MENU, stack);
            }
        }).width(48).pos(leftPos + 109, topPos + 58).build());
        addRenderableWidget(this.editBox = new EditBox(minecraft.font, leftPos + 64, topPos + 24, 44, 10, Component.empty()));
        editBox.setMaxLength(6);
        editBox.setFilter(value -> {
            if (value.isEmpty()) {
                this.rgb = 0;
                return true;
            }
            try {
                this.rgb = Integer.parseInt(value, 16) | 0xFF000000;
                return true;
            } catch (Exception e) {
                return false;
            }
        });
        editBox.setResponder(value -> {
            if (isDye.isTrue()) {
                BaseDyeItem.setRGB(this.stack = VanityArmorItems.DYE.get().getDefaultInstance(), rgb);
            } else if (isDye.isFalse()) {
                PaintItem.setRGB(this.stack = PaintItems.PAINT.get().getDefaultInstance(), rgb);
            } else {
                this.stack = ItemStack.EMPTY;
            }
        });
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
        if (!stack.isEmpty()) {
            int x = leftPos + 125;
            int y = topPos + 35;
            guiGraphics.renderFakeItem(stack, x, y);
            int count = Math.min(
                    Math.min(
                            menu.getSlot(0).getItem().getCount(),
                            menu.getSlot(1).getItem().getCount()
                    ),
                    menu.getSlot(2).getItem().getCount()
            );
            guiGraphics.renderItemDecorations(minecraft.font, stack, x, y, Integer.toString(count));
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (editBox.isFocused()) {
            editBox.keyPressed(keyCode, scanCode, modifiers);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!editBox.isHovered()) {
            editBox.setFocused(false);
        }
        if (mouseX > leftPos + 125 && mouseX < leftPos + 141 && mouseY > topPos + 35 && mouseY < topPos + 51) {
            ItemStack carried = menu.getCarried();
            if (!stack.isEmpty() && (carried.isEmpty() || (PortItemStackExtension.isSameItemSameComponents(carried, stack) && carried.getCount() < carried.getMaxStackSize()))) {
                DyeMixPacketC2S.sendToServer(stack);
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
