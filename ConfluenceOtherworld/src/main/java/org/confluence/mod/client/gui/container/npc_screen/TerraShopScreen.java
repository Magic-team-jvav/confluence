package org.confluence.mod.client.gui.container.npc_screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.client.gui.container.ExtraInventoryScreen;
import org.confluence.mod.client.gui.container.TerraChestScreen;
import org.confluence.mod.common.entity.npc.trade.NPCTradeMenu;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.menu.TerraInventoryActions;
import org.confluence.mod.mixin.world.inventory.SlotPositionAccessor;
import org.confluence.mod.network.c2s.FavoriteSlotPacketC2S;
import org.lwjgl.glfw.GLFW;

import static org.confluence.mod.client.gui.hud.TerraHotbarHud.*;
import static org.confluence.mod.common.menu.TerraInventoryLayout.*;

/// 泰拉风格的 NPC 商店：背包在左上角（与泰拉背包、箱子同位），商品格在背包下方，钱币列显示身上的钱，
/// 右边是翻页的文字按钮。菜单仍是 {@link NPCTradeMenu}，买卖、回购、价格都由服务端照原样处理：
/// 点击商品买到鼠标上（按住连续买），Shift+点击整组买进背包；Shift+点击背包里的物品卖出，拿着物品点空商品格也卖出。
public class TerraShopScreen extends AbstractContainerScreen<NPCTradeMenu> {
    private static final int SHOP_Y = inventoryY(4) + GAP + 10;
    private static final int BUTTONS_X = INV_X + 9 * CELL + GAP + 2;
    private static final int TRADE_SLOTS = 36;
    private static final int HOLD_DELAY = 8;
    private static final int HOLD_INTERVAL = 2;
    private final Player player;
    private final NPCTradeItemOutline itemOutline = new NPCTradeItemOutline();
    private final ItemStack[] moneyIcons = {
            ModItems.PLATINUM_COIN.toStack(), ModItems.GOLD_COIN.toStack(),
            ModItems.SILVER_COIN.toStack(), ModItems.COPPER_COIN.toStack()
    };
    private TerraChestScreen.TextButton previousPage;
    private TerraChestScreen.TextButton nextPage;
    private int heldOfferSlot = -1;
    private int heldTicks;
    /// 上一刻身上的钱（铜币为单位），用来在买卖成交时响钱币声；刚打开的几刻只记录（等服务端同步）
    private long lastMoney = -1;
    private int openTicks;

    public TerraShopScreen(NPCTradeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.player = playerInventory.player;
        this.imageWidth = BUTTONS_X + 100;
        this.imageHeight = SHOP_Y + 4 * CELL;
        layoutSlots();
    }

    /// 背包：第一行快捷栏，下面三行主背包；商品 9×4 在背包下方
    private void layoutSlots() {
        for (Slot slot : menu.slots) {
            int x;
            int y;
            if (slot.container == player.getInventory()) {
                int index = slot.getContainerSlot();
                x = inventoryX(index % 9);
                y = index < 9 ? inventoryY(0) : inventoryY((index - 9) / 9 + 1);
            } else if (slot.index < TRADE_SLOTS) {
                x = inventoryX(slot.index % 9);
                y = SHOP_Y + (slot.index / 9) * CELL;
            } else {
                continue; // 钱币同步用的隐藏槽
            }
            SlotPositionAccessor accessor = (SlotPositionAccessor) slot;
            accessor.confluence$setX(x);
            accessor.confluence$setY(y);
        }
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = SCREEN_MARGIN;
        this.topPos = SCREEN_TOP;
        this.previousPage = addRenderableWidget(new TerraChestScreen.TextButton(leftPos + BUTTONS_X, topPos + SHOP_Y,
                Component.translatable("gui.confluence.shop.previous"), () -> requestPage(menu.getCurrentPage() - 1)));
        this.nextPage = addRenderableWidget(new TerraChestScreen.TextButton(leftPos + BUTTONS_X, topPos + SHOP_Y + 11,
                Component.translatable("gui.confluence.shop.next"), () -> requestPage(menu.getCurrentPage() + 1)));
        updatePageButtons();
    }

    private void requestPage(int page) {
        if (minecraft == null || minecraft.gameMode == null || page < 0 || page >= menu.getPageCount()) return;
        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, page);
    }

    private void updatePageButtons() {
        if (previousPage == null) return;
        boolean pages = menu.getPageCount() > 1;
        previousPage.visible = pages && menu.getCurrentPage() > 0;
        nextPage.visible = pages && menu.getCurrentPage() + 1 < menu.getPageCount();
    }

    @Override
    public void containerTick() {
        super.containerTick();
        updatePageButtons();
        playTradeSound();
        // 按住左键连续购买（与原商店界面一致）
        if (heldOfferSlot < 0 || minecraft == null || minecraft.player == null || minecraft.gameMode == null) return;
        if (GLFW.glfwGetMouseButton(minecraft.getWindow().getWindow(), GLFW.GLFW_MOUSE_BUTTON_LEFT) != GLFW.GLFW_PRESS
                || hoveredSlot == null || hoveredSlot.index != heldOfferSlot || !menu.isOfferSlot(heldOfferSlot)) {
            heldOfferSlot = -1;
            return;
        }
        heldTicks++;
        if (heldTicks >= HOLD_DELAY && (heldTicks - HOLD_DELAY) % HOLD_INTERVAL == 0) {
            minecraft.gameMode.handleInventoryMouseClick(menu.containerId, heldOfferSlot, 0, ClickType.PICKUP, minecraft.player);
        }
    }

    private long money() {
        long total = 0;
        for (int i = 0; i < menu.getMoneySlotCount(); i++) {
            total = total * 100 + menu.getSlot(menu.getMoneySlotStart() + i).getItem().getCount();
        }
        return total;
    }

    /// 泰拉买卖时的钱币声：钱少了是买（低一点），钱多了是卖（高一点）
    private void playTradeSound() {
        long money = money();
        if (openTicks++ >= 5 && lastMoney >= 0 && money != lastMoney && minecraft != null) {
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(org.confluence.mod.common.init.ModSoundEvents.COINS.get(), money < lastMoney ? 1.0F : 1.2F, 0.5F)); // 音量 0.5（界面音默认 0.25 太轻）
        }
        lastMoney = money;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
        if (!menu.getCarried().isEmpty() && hoveredSlot != null && hoveredSlot.index < TRADE_SLOTS && !hoveredSlot.hasItem()) {
            guiGraphics.renderTooltip(font, Component.translatable("gui.confluence.sell"), mouseX, mouseY);
        }
    }

    /// 不压暗世界
    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBg(guiGraphics, partialTick, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.enableBlend();
        for (Slot slot : menu.slots) {
            if (!slot.isActive()) continue;
            boolean own = slot.container == player.getInventory();
            int u = TerraInventoryActions.isFavorite(slot.getItem()) ? 72 : own ? 0 : 18;
            guiGraphics.blit(SLOTS, leftPos + slot.x - 1, topPos + slot.y - 1, u, 0, 18, 18, SLOTS_WIDTH, SLOTS_HEIGHT);
            if (own && slot.getContainerSlot() == player.getInventory().selected) {
                guiGraphics.blit(SLOTS, leftPos + slot.x - 1, topPos + slot.y - 1, 0, 18, 18, 18, SLOTS_WIDTH, SLOTS_HEIGHT);
            }
        }
        // 钱币列：身上的钱（铂、金、银、铜）
        for (int i = 0; i < menu.getMoneySlotCount(); i++) {
            int x = leftPos + COINS_X;
            int y = topPos + inventoryY(i);
            guiGraphics.blit(SLOTS, x - 1, y - 1, 18, 0, 18, 18, SLOTS_WIDTH, SLOTS_HEIGHT);
            int count = menu.getSlot(menu.getMoneySlotStart() + i).getItem().getCount();
            if (count > 0) {
                guiGraphics.renderItem(moneyIcons[i], x, y);
                guiGraphics.renderItemDecorations(font, moneyIcons[i], x, y, String.valueOf(count));
            } else {
                // 这种钱币一个都没有：与背包里的空钱币槽一样，画暗淡的钱币图标
                guiGraphics.blit(SLOTS, x, y, 10 * 16, 36, 16, 16, SLOTS_WIDTH, SLOTS_HEIGHT);
            }
        }
        RenderSystem.disableBlend();
        // 稀有度描边（与原商店相同）
        itemOutline.prepare(guiGraphics, menu, imageWidth);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(leftPos, topPos, 0);
        for (Slot slot : menu.slots) {
            if (menu.isOfferSlot(slot.index) && slot.hasItem() && slot.isActive()) itemOutline.render(guiGraphics, slot);
        }
        guiGraphics.pose().popPose();
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, title, INV_X, SHOP_Y - 10, 0xFFFFFF, true);
        if (menu.getPageCount() > 1) {
            Component page = Component.translatable("gui.confluence.shop.page", menu.getCurrentPage() + 1, menu.getPageCount());
            guiGraphics.drawString(font, page, INV_X + 9 * CELL - font.width(page), SHOP_Y - 10, 0xA0A0A0, true);
        }
    }

    @Override
    protected void renderSlot(GuiGraphics guiGraphics, Slot slot) {
        super.renderSlot(guiGraphics, slot);
        ExtraInventoryScreen.renderSlotOverlay(guiGraphics, slot, slot.container == player.getInventory() && slot.getContainerSlot() < 9);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Alt+点击收藏，与泰拉背包一致（收藏的东西不会被误卖）
        if (button == 0 && hasAltDown() && hoveredSlot != null && hoveredSlot.hasItem() && hoveredSlot.container == player.getInventory() && menu.getCarried().isEmpty()) {
            FavoriteSlotPacketC2S.sendToServer(hoveredSlot.index);
            getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.4F));
            return true;
        }
        // 泰拉：收藏的物品不能卖（Shift+点击背包里的物品是卖出）
        if (hasShiftDown() && hoveredSlot != null && hoveredSlot.container == player.getInventory() && TerraInventoryActions.isFavorite(hoveredSlot.getItem())) {
            return true;
        }
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && !hasShiftDown() && hoveredSlot != null && menu.isOfferSlot(hoveredSlot.index)) {
            heldOfferSlot = hoveredSlot.index;
            heldTicks = 0;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) heldOfferSlot = -1;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void removed() {
        itemOutline.close();
        super.removed();
    }
}
