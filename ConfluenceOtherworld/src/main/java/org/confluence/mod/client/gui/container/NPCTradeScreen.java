package org.confluence.mod.client.gui.container;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.npc.trade.NPCTradeMenu;
import org.confluence.mod.common.init.entity.NpcEntities;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.network.c2s.OpenMenuPacketC2S;
import org.lwjgl.glfw.GLFW;

/// 使用原版箱子纹理显示 NPC 商店。
///
/// 客户端只显示服务端同步的商品、价格说明和页码，不参与报价与成交计算。
public final class NPCTradeScreen extends AbstractContainerScreen<NPCTradeMenu> {
    private static final ResourceLocation CONTAINER_TEXTURE = ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");
    private static final ResourceLocation PIGGY_BANK_TEXTURE = Confluence.asResource("textures/gui/container/piggy_bank.png");
    private static final int TRADE_ROWS = 4;
    private static final int TOP_HEIGHT = TRADE_ROWS * 18 + 17;
    private static final int HOLD_DELAY = 8;
    private static final int HOLD_INTERVAL = 2;

    private Button previousPage;
    private Button nextPage;
    private int heldOfferSlot = -1;
    private int heldTicks;

    public NPCTradeScreen(NPCTradeMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageHeight = 114 + TRADE_ROWS * 18;
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        previousPage = addRenderableWidget(Button.builder(Component.literal("<"),
                        button -> requestPage(menu.getCurrentPage() - 1))
                .bounds(leftPos + imageWidth - 52, topPos + 4, 20, 14)
                .build());
        nextPage = addRenderableWidget(Button.builder(Component.literal(">"),
                        button -> requestPage(menu.getCurrentPage() + 1))
                .bounds(leftPos + imageWidth - 28, topPos + 4, 20, 14)
                .build());
        if (menu.getNPC().getType() == NpcEntities.GOBLIN_TINKERER.get()) {
            // 重铸属于商店的附加入口，放在容器上方，避免覆盖 NPC 名称和页码。
            addRenderableWidget(Button.builder(
                            Component.translatable("button.confluence.reforge"),
                            button -> {
                                LocalPlayer player = minecraft.player;
                                if (player == null) return;
                                ItemStack stack = player.containerMenu.getCarried();
                                player.containerMenu.setCarried(ItemStack.EMPTY);
                                OpenMenuPacketC2S.sendToServer(
                                        OpenMenuPacketC2S.NPC_REFORGE_MENU,
                                        stack);
                            })
                    .bounds(leftPos + 4, topPos - 18, 48, 16)
                    .build());
        }
        updatePageButtons();
    }

    @Override
    public void containerTick() {
        super.containerTick();
        updatePageButtons();
        if (heldOfferSlot < 0 || minecraft == null || minecraft.player == null || minecraft.gameMode == null)
            return;
        if (GLFW.glfwGetMouseButton(minecraft.getWindow().getWindow(), GLFW.GLFW_MOUSE_BUTTON_LEFT) != GLFW.GLFW_PRESS
                || hoveredSlot == null || hoveredSlot.index != heldOfferSlot || !menu.isOfferSlot(heldOfferSlot)) {
            stopHeldPurchase();
            return;
        }
        heldTicks++;
        if (heldTicks >= HOLD_DELAY && (heldTicks - HOLD_DELAY) % HOLD_INTERVAL == 0) {
            minecraft.gameMode.handleInventoryMouseClick(menu.containerId, heldOfferSlot, 0, ClickType.PICKUP, minecraft.player);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && !hasShiftDown() && hoveredSlot != null && menu.isOfferSlot(hoveredSlot.index)) {
            heldOfferSlot = hoveredSlot.index;
            heldTicks = 0;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) stopHeldPurchase();
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        String page = (menu.getCurrentPage() + 1) + " / " + menu.getPageCount();
        graphics.drawString(font, page, imageWidth - 82 - font.width(page) / 2, 7, 0x404040, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(CONTAINER_TEXTURE, leftPos, topPos, 0, 0, imageWidth, TOP_HEIGHT);
        graphics.blit(CONTAINER_TEXTURE, leftPos, topPos + TOP_HEIGHT, 0, 126, imageWidth, 96);
        graphics.blit(PIGGY_BANK_TEXTURE, leftPos - 33, topPos + 10, 224, 0, 32, 86);
        for (int slot = 0; slot < menu.getMoneySlotCount(); slot++) {
            if (!menu.getSlot(menu.getMoneySlotStart() + slot).hasItem()) {
                graphics.blit(PIGGY_BANK_TEXTURE, leftPos - 25, topPos + 18 + slot * 18, 207, 0, 16, 16);
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        int sellX = leftPos + 152;
        int sellY = topPos + 72;
        graphics.renderFakeItem(ModItems.GOLD_COIN.toStack(), sellX, sellY);
        renderTooltip(graphics, mouseX, mouseY);
        if (mouseX >= sellX && mouseX < sellX + 16 && mouseY >= sellY && mouseY < sellY + 16) {
            graphics.renderTooltip(font, Component.translatable("gui.confluence.sell"), mouseX, mouseY);
        }
    }

    private void requestPage(int page) {
        if (minecraft == null || minecraft.gameMode == null) {
            return;
        }
        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, page);
    }

    private void updatePageButtons() {
        if (previousPage == null || nextPage == null) {
            return;
        }
        previousPage.active = menu.getCurrentPage() > 0;
        nextPage.active = menu.getCurrentPage() + 1 < menu.getPageCount();
        previousPage.visible = menu.getPageCount() > 1;
        nextPage.visible = menu.getPageCount() > 1;
    }

    private void stopHeldPurchase() {
        heldOfferSlot = -1;
        heldTicks = 0;
    }
}
