package org.confluence.mod.client.gui.container;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import org.confluence.mod.common.attachment.ExtraInventory;
import net.minecraft.world.inventory.Slot;
import org.confluence.mod.client.gui.hud.TerraHotbarHud;
import org.confluence.mod.common.menu.TerraInventoryActions;
import org.confluence.mod.mixin.world.inventory.SlotPositionAccessor;
import org.confluence.mod.network.c2s.ContainerActionPacketC2S;
import org.confluence.mod.network.c2s.FavoriteSlotPacketC2S;

import static org.confluence.mod.client.gui.hud.TerraHotbarHud.*;
import static org.confluence.mod.common.menu.TerraInventoryLayout.*;

/// 泰拉风格的箱子界面：背包在左上角（与泰拉背包相同位置），箱子格子在背包下方，右侧是文字按钮。
/// 菜单仍是原来的（原版 {@link ChestMenu}、保险箱，以及猪猪存钱罐、虚空保险库的菜单），只在客户端重新摆放槽位，服务端逻辑不变。
/// 存钱罐菜单里还有玩家自己的钱币槽，放在背包右边的钱币列（与泰拉背包同位）。
public class TerraChestScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    private static final int CHEST_Y = inventoryY(4) + GAP + 10;
    private static final int BUTTONS_X = INV_X + 9 * CELL + GAP + 2;
    private static final String[] BUTTON_KEYS = {"loot_all", "deposit_all", "quick_stack", "sort", "restock"};
    private static final byte[] BUTTON_ACTIONS = {
            TerraInventoryActions.LOOT_ALL, TerraInventoryActions.DEPOSIT_ALL, TerraInventoryActions.QUICK_STACK,
            TerraInventoryActions.SORT_CONTAINER, TerraInventoryActions.RESTOCK
    };
    private final Player player;
    /// 显示的标题（重命名后立即更新，不用重新打开）
    private Component displayTitle;
    private net.minecraft.client.gui.components.EditBox renameBox;
    private TextButton renameButton;

    public TerraChestScreen(T menu, Inventory playerInventory, Component title, int rows) {
        super(menu, playerInventory, title);
        this.player = playerInventory.player;
        this.imageWidth = BUTTONS_X + 100;
        this.imageHeight = CHEST_Y + rows * CELL;
        this.displayTitle = title;
        layoutSlots();
    }

    /// 背包与泰拉背包同位：第一行快捷栏，下面三行主背包；箱子在背包下方
    private void layoutSlots() {
        for (Slot slot : menu.slots) {
            int x, y;
            int index = slot.getContainerSlot();
            if (slot.container instanceof ExtraInventory) { // 存钱罐界面里的钱币槽
                x = COINS_X;
                y = inventoryY(index - ExtraInventory.COINS_START);
            } else if (slot.container == player.getInventory()) {
                if (index < 9) {
                    x = inventoryX(index);
                    y = inventoryY(0);
                } else {
                    x = inventoryX((index - 9) % 9);
                    y = inventoryY((index - 9) / 9 + 1);
                }
            } else {
                x = inventoryX(index % 9);
                y = CHEST_Y + (index / 9) * CELL;
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
        for (int i = 0; i < BUTTON_KEYS.length; i++) {
            byte action = BUTTON_ACTIONS[i];
            addRenderableWidget(new TextButton(leftPos + BUTTONS_X, topPos + CHEST_Y + i * 11,
                    Component.translatable("gui.confluence.chest." + BUTTON_KEYS[i]), () -> {
                if (menu.getCarried().isEmpty()) ContainerActionPacketC2S.sendToServer(action);
            }));
        }
        // 泰拉的“重命名”：只对箱子、木桶这类方块容器（准星指着的方块实体能改名时才显示）
        if (canRename()) {
            this.renameBox = new net.minecraft.client.gui.components.EditBox(font, leftPos + INV_X, topPos + CHEST_Y - 12, 9 * CELL, 11, Component.translatable("gui.confluence.chest.rename"));
            renameBox.setMaxLength(org.confluence.mod.network.c2s.RenameContainerPacketC2S.MAX_LENGTH);
            renameBox.visible = false;
            addRenderableWidget(renameBox);
            this.renameButton = addRenderableWidget(new TextButton(leftPos + BUTTONS_X, topPos + CHEST_Y + BUTTON_KEYS.length * 11,
                    Component.translatable("gui.confluence.chest.rename"), this::toggleRename));
        }
    }

    private boolean canRename() {
        if (!(menu instanceof ChestMenu)) return false;
        net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
        return minecraft.hitResult instanceof net.minecraft.world.phys.BlockHitResult hit && minecraft.level != null
                && minecraft.level.getBlockEntity(hit.getBlockPos()) instanceof net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
    }

    private boolean renaming() {
        return renameBox != null && renameBox.visible;
    }

    /// 第一次点：出现输入框（原名字）；再点或回车：保存；Esc：取消
    private void toggleRename() {
        if (renaming()) {
            saveRename();
            return;
        }
        renameBox.setValue(displayTitle.getString());
        renameBox.visible = true;
        setFocused(renameBox);
        renameBox.setFocused(true);
        renameButton.setMessage(Component.translatable("gui.confluence.chest.save"));
    }

    private void saveRename() {
        String name = renameBox.getValue().strip();
        org.confluence.mod.network.c2s.RenameContainerPacketC2S.sendToServer(name);
        if (name.isEmpty()) {
            net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
            if (minecraft.hitResult instanceof net.minecraft.world.phys.BlockHitResult hit && minecraft.level != null) {
                this.displayTitle = minecraft.level.getBlockState(hit.getBlockPos()).getBlock().getName();
            }
        } else {
            this.displayTitle = Component.literal(name);
        }
        closeRename();
    }

    private void closeRename() {
        renameBox.visible = false;
        renameBox.setFocused(false);
        setFocused(null);
        renameButton.setMessage(Component.translatable("gui.confluence.chest.rename"));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (renaming()) {
            if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER || keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_KP_ENTER) {
                saveRename();
            } else if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
                closeRename();
            } else {
                renameBox.keyPressed(keyCode, scanCode, modifiers); // 其他键都给输入框（E、数字键不关界面）
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
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
            boolean own = slot.container == player.getInventory();
            int u = TerraInventoryActions.isFavorite(slot.getItem()) ? 72 : own ? 0 : 18;
            guiGraphics.blit(SLOTS, leftPos + slot.x - 1, topPos + slot.y - 1, u, 0, 18, 18, SLOTS_WIDTH, SLOTS_HEIGHT);
            if (own && slot.getContainerSlot() == player.getInventory().selected) {
                guiGraphics.blit(SLOTS, leftPos + slot.x - 1, topPos + slot.y - 1, 0, 18, 18, 18, SLOTS_WIDTH, SLOTS_HEIGHT);
            }
            if (slot.container instanceof ExtraInventory && !slot.hasItem()) { // 空钱币槽的图标
                guiGraphics.blit(SLOTS, leftPos + slot.x, topPos + slot.y, 10 * 16, 36, 16, 16, SLOTS_WIDTH, SLOTS_HEIGHT);
            }
        }
        RenderSystem.disableBlend();
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (!renaming()) guiGraphics.drawString(font, displayTitle, INV_X, CHEST_Y - 10, 0xFFFFFF, true);
    }

    @Override
    protected void renderSlot(GuiGraphics guiGraphics, Slot slot) {
        super.renderSlot(guiGraphics, slot);
        ExtraInventoryScreen.renderSlotOverlay(guiGraphics, slot, slot.container == player.getInventory() && slot.getContainerSlot() < 9);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Alt+点击收藏，与泰拉背包一致
        if (button == 0 && hasAltDown() && hoveredSlot != null && hoveredSlot.hasItem() && hoveredSlot.container == player.getInventory() && menu.getCarried().isEmpty()) {
            FavoriteSlotPacketC2S.sendToServer(hoveredSlot.index);
            getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.4F));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    /// 泰拉的文字按钮：白字，悬停变黄（商店界面也用）
    public static class TextButton extends AbstractButton {
        private final Runnable onPress;

        public TextButton(int x, int y, Component message, Runnable onPress) {
            this(x, y, 100, message, onPress);
        }

        public TextButton(int x, int y, int width, Component message, Runnable onPress) {
            super(x, y, width, 10, message);
            this.onPress = onPress;
        }

        @Override
        public void onPress() {
            onPress.run();
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            // 只看鼠标悬停：点击后按钮会保留键盘焦点，用 isHoveredOrFocused 会让颜色“粘”在按过的按钮上
            int color = isHovered() ? 0xFFF0C030 : 0xFFFFFFFF;
            guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, getMessage(), getX(), getY() + 1, color, true);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {
            defaultButtonNarrationText(output);
        }
    }
}
