package org.confluence.mod.client.gui.container;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.entity.npc.GoblinTinkererNPC;
import org.confluence.mod.common.entity.npc.trade.NPCTradeMenu;
import org.confluence.mod.network.c2s.OpenMenuPacketC2S;

/// 使用原版箱子纹理显示 NPC 商店。
///
/// <p>该类只提供分页按钮和页码，不读取条件、计算价格或决定成交。按钮复用原版菜单
/// 按钮数据包，请求服务端切换已经冻结的会话报价页。</p>
public final class NPCTradeScreen extends AbstractContainerScreen<NPCTradeMenu> {
    private static final ResourceLocation CONTAINER_TEXTURE =
            ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");
    private static final int TRADE_ROWS = 4;
    private static final int TOP_HEIGHT = TRADE_ROWS * 18 + 17;

    private Button previousPage;
    private Button nextPage;

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
        if (menu.getNPC() instanceof GoblinTinkererNPC) {
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
        graphics.blit(CONTAINER_TEXTURE, leftPos, topPos + TOP_HEIGHT, 0, 126,
                imageWidth, 96);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
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
}
