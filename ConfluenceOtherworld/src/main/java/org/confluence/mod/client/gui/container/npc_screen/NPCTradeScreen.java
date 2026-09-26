package org.confluence.mod.client.gui.container.npc_screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.trade.NPCTradeMenu;
import org.confluence.mod.common.init.item.ModItems;
import org.lwjgl.glfw.GLFW;

import java.util.function.Supplier;

/// 客户端只显示服务端同步的商品和价格说明，不参与报价与成交计算。
public class NPCTradeScreen extends AbstractContainerScreen<NPCTradeMenu> {
    private static final ResourceLocation CONTAINER_TEXTURE = Confluence.asResource("textures/gui/trade/npc_trade_menu.png");
    private static final int HOLD_DELAY = 8;
    private static final int HOLD_INTERVAL = 2;
    /// 相对原版容器居中位置的整体偏移；正数下移，负数上移，单位为 GUI 像素。
    private static final int UI_OFFSET_Y = 10;
    /// 钱币纵排位于商店左侧，数量右对齐并显示在图标左边。
    private static final int MONEY_ICON_X = -20;
    private static final int MONEY_FIRST_Y = 42;
    private static final int MONEY_ROW_SPACING = 15;
    private static final int MONEY_TEXT_GAP = 3;
    private static final int MONEY_TEXT_Y = 4;
    private static final int PAGE_BUTTON_X = 190;
    private static final int PREVIOUS_PAGE_Y = 42 + NPCTradeMenu.CONTENT_OFFSET_Y;
    private static final int NEXT_PAGE_Y = PREVIOUS_PAGE_Y + 8;
    private static final int PAGE_BUTTON_WIDTH = 8;
    private static final int PAGE_BUTTON_HEIGHT = 8;

    private Button previousPage;
    private Button nextPage;
    private int heldOfferSlot = -1;
    private int heldTicks;
    private NPCTradePortrait portrait;
    private final NPCTradeItemOutline itemOutline = new NPCTradeItemOutline();
    private final ItemStack[] moneyIcons = {
            ModItems.PLATINUM_COIN.toStack(), ModItems.GOLD_COIN.toStack(),
            ModItems.SILVER_COIN.toStack(), ModItems.COPPER_COIN.toStack()
    };

    public NPCTradeScreen(NPCTradeMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 198;
        imageHeight = 176 + NPCTradeMenu.CONTENT_OFFSET_Y;
    }

    @Override
    protected void init() {
        super.init();
        topPos += UI_OFFSET_Y;
        if (portrait == null && menu.getNPC().getType().create(menu.getNPC().level()) instanceof BaseNPC preview) {
            var appearance = menu.getNPC().getEntityData().getNonDefaultValues();
            if (appearance != null) preview.getEntityData().assignValues(appearance);
            portrait = new NPCTradePortrait(preview);
        }
        previousPage = addRenderableWidget(createPageButton(true));
        nextPage = addRenderableWidget(createPageButton(false));
        updatePageButtons();
    }

    @Override
    public void containerTick() {
        super.containerTick();
        if (portrait != null) portrait.tick();
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
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        if (portrait != null) portrait.render(graphics, leftPos, topPos, mouseX, mouseY);
        graphics.blit(CONTAINER_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        NPCTradeScreens.renderOverlay(menu, graphics, leftPos, topPos, imageWidth, imageHeight - NPCTradeMenu.CONTENT_OFFSET_Y);
        for (int slot = 0; slot < menu.getMoneySlotCount(); slot++) {
            int x = leftPos + MONEY_ICON_X;
            int y = topPos + MONEY_FIRST_Y + slot * MONEY_ROW_SPACING;
            String count = Integer.toString(menu.getSlot(menu.getMoneySlotStart() + slot).getItem().getCount());
            graphics.renderItem(moneyIcons[slot], x, y);
            graphics.drawString(font, count, x - MONEY_TEXT_GAP - font.width(count), y + MONEY_TEXT_Y, 0xFFFFFF);
        }
        itemOutline.prepare(graphics, menu, imageWidth);
        graphics.pose().pushPose();
        graphics.pose().translate(leftPos, topPos, 0);
        for (Slot slot : menu.slots) {
            if (menu.isOfferSlot(slot.index) && slot.hasItem() && slot.isActive())
                itemOutline.render(graphics, slot);
        }
        graphics.pose().popPose();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
        if (!menu.getCarried().isEmpty() && hoveredSlot != null && hoveredSlot.index < 36 && !hoveredSlot.hasItem()) {
            graphics.renderTooltip(font, Component.translatable("gui.confluence.sell"), mouseX, mouseY);
        }
    }

    @Override
    public void removed() {
        itemOutline.close();
        super.removed();
    }

    private Button createPageButton(boolean previous) {
        return new Button(leftPos + PAGE_BUTTON_X, topPos + (previous ? PREVIOUS_PAGE_Y : NEXT_PAGE_Y), PAGE_BUTTON_WIDTH, PAGE_BUTTON_HEIGHT,
                Component.translatable(previous ? "spectatorMenu.previous_page" : "spectatorMenu.next_page"),
                button -> requestPage(menu.getCurrentPage() + (previous ? -1 : 1)), Supplier::get) {
            @Override
            public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
                int u = !active ? 201 : isHoveredOrFocused() ? 219 : 210;
                graphics.blit(CONTAINER_TEXTURE, getX(), getY(), width, height,
                        u, (previous ? 54 : 62) + NPCTradeMenu.CONTENT_OFFSET_Y, 8, 8, 256, 256);
            }
        };
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
    }

    private void stopHeldPurchase() {
        heldOfferSlot = -1;
        heldTicks = 0;
    }
}
