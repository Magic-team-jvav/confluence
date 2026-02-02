package org.confluence.terraentity.client.gui.container;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.api.npc.trade.ITrade;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.entity.ai.keyframe.animation.KeyframeAnimation;
import org.confluence.terraentity.entity.npc.AnglerNPC;
import org.confluence.terraentity.menu.TETradesMenu;
import org.confluence.terraentity.mixed.IPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * <p>统一使用trade的抽象菜单类
 * <p>渲染cost的逻辑在{@link ITrade#renderCosts(ITradeHolder, GuiGraphics, Font, int, int, int, int, int, int)}
 * <p>使用时必须继承此类，否则会出现类型推断不匹配</p>
 */
public abstract class TETradeScreen<M extends TETradesMenu> extends AbstractContainerScreen<M> {
    public static final ResourceLocation MENU_LOCATION = TerraEntity.space("textures/gui/container/npc_shop.png");
    private static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.withDefaultNamespace("container/villager/scroller");
    private static final ResourceLocation SCROLLER_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("container/villager/scroller_disabled");
    private static final int NUMBER_OF_LINES = 7;
    private static final Component TRADES_LABEL = Component.translatable("title.terra_entity.npc_trade");

    // 交易项
    public int shopItem = -1;
    private int hoveredItem = -1;
    private int row;
    private final int col = 5;

    // 滑块参数
    private int offsetX;
    private int offsetY;
    private int intervalX = 18;
    private int intervalY = 18;
    int scrollOff;

    // 标题位置曲线插值
    private KeyframeAnimation interpolator;
    double v;
    int tickCount;

    // 只init一次
    boolean triggerOnce = true;

    // 用于长按点击记录参数
    int clickCount = 0;
    boolean isClicked = false;
    int clickBt = 0;
    long lastClickTime = 0;

    public TETradeScreen(M menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 290;
        this.inventoryLabelX = 107;
    }

    @Override
    protected void init() {
        super.init();
        if (menu.NPCTrades == null) {
            menu.NPCTrades = ((IPlayer) Minecraft.getInstance().player).terra_entity$getTradeHolder();
        }
        boolean trade = menu.NPCTrades != null && menu.NPCTrades.getTradeManager() != null;
        if (triggerOnce) {
            if (menu.NPCTrades instanceof AnglerNPC) {
                Minecraft.getInstance().setScreen(new AnglerDialogScreen(this, (byte) 0));
            } else {
                Minecraft.getInstance().setScreen(new DialogScreen(this, trade && !menu.NPCTrades.trades().isEmpty()));
            }
            triggerOnce = false;
        }
        if (!trade) return;
        menu.NPCTrades.getTradeManager().refreshAvailableTrades();
        this.row = menu.NPCTrades.trades().size() / 3;
        if (menu.NPCTrades.trades().size() % 3 != 0)
            this.row++;

        offsetX = (this.width - this.imageWidth) / 2 + 5;
        offsetY = (this.height - this.imageHeight) / 2 + 16;

        interpolator = KeyframeAnimation.builder()
                .addKeyframe(5, 0)
                .addKeyframe(20, 40)
                .addKeyframe(30, 55)
                .addKeyframe(40, 60)
                .build();
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.setColor(1, 1, 1, (float) (v / 60f));
        int fy = 6 - (int) ((60 - v) / 5);

        Component label = this.title;
        ITradeHolder holder = ((IPlayer) Minecraft.getInstance().player).terra_entity$getTradeHolder();
        if (holder != null) {
            if (shopItem >= 0 && shopItem < holder.trades().size()) {
                label = holder.trades().get(shopItem).getTitle(holder, label);
            }
        }
        if (label == title) {
            guiGraphics.drawString(this.font, ((MutableComponent) this.title).withStyle(Style.EMPTY.withBold(true)), 49 + this.imageWidth / 2 - this.font.width(this.title) / 2, fy, 0x348834, false);
        } else {
            guiGraphics.drawString(this.font, label, 49 + this.imageWidth / 2 - this.font.width(label) / 2, fy, 0x348834, false);
        }

        guiGraphics.setColor(1, 1, 1, 1);

        guiGraphics.drawString(this.font, this.playerInventoryTitle, 90 + this.imageWidth / 2, this.inventoryLabelY, 4210752, false);
        var interAct = ((IPlayer) minecraft.player).terra_entity$getTradeHolder();
        Entity interactEntity = null;
        if (interAct instanceof Entity e) {
            interactEntity = e;
        }
        Component title = interactEntity == null || interactEntity.getDisplayName() == null ? TRADES_LABEL : interactEntity.getDisplayName();

        int l = this.font.width(title);
        guiGraphics.drawString(this.font, title, 5 - l / 2 + 48, 6, 4210752, false);
    }

    public Component getTradesLabel(Component original) {
        return original;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(MENU_LOCATION, i, j, 0, 0.0F, 0.0F, this.imageWidth - 15, this.imageHeight, 512, 256);
    }

    private void renderScroller(GuiGraphics guiGraphics, int posX, int posY) {
        int i = menu.NPCTrades.trades().size() - 7;
        if (i > 1) {
            int j = 139 - (27 + (i - 1) * 139 / i);
            int k = 1 + j / i + 139 / i;
            int i1 = Math.min(113, this.scrollOff * k);
            if (this.scrollOff == i - 1) {
                i1 = 113;
            }
            guiGraphics.blitSprite(SCROLLER_SPRITE, posX + 95, posY + 17 + i1, 0, 6, 27);
        } else {
            guiGraphics.blitSprite(SCROLLER_DISABLED_SPRITE, posX + 95, posY + 17, 0, 6, 27);
        }
    }

    @Override
    protected void containerTick() {
        this.tickCount++;
    }


    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (menu.NPCTrades == null || menu.NPCTrades.getTradeManager() == null || menu.NPCTrades.trades() == null) {
            this.onClose();
            return;
        }
        if (isClicked) {
            if (clickCount < 2) {
                if (lastClickTime + 500 < System.currentTimeMillis()) {
                    clickCount++;
                    lastClickTime = System.currentTimeMillis();
                }
            } else {
                double itnl = Math.max(Math.exp(-(clickCount) / 20.0) * 200, 30);
                if (lastClickTime + itnl < System.currentTimeMillis()) {
                    clickCount++;
//                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 1F,0.03f));

                    super.mouseClicked(mouseX, mouseY, clickBt);
                    lastClickTime = System.currentTimeMillis();
                }
            }
        }

        if (interpolator == null) return;
        ITradeHolder holder = ((IPlayer) Minecraft.getInstance().player).terra_entity$getTradeHolder();

        if (holder != null) {
            menu.NPCTrades = holder;
        }
        this.row = menu.NPCTrades.trades().size() / 3;
        if (menu.NPCTrades.trades().size() % 3 != 0)
            this.row++;


        v = interpolator.cal(this.tickCount + partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);


        int ii = (this.width - this.imageWidth) / 2;
        int jj = (this.height - this.imageHeight) / 2;
        this.renderScroller(guiGraphics, ii, jj);

        // 左侧物品
        if (this.hoveredItem >= 0 && this.hoveredItem < menu.NPCTrades.trades().size()) {
            int x = offsetX + hoveredItem % col * intervalX;
            int y = offsetY + (hoveredItem / col - scrollOff) * intervalY;
            renderSlotHighlight(guiGraphics, x, y, 20);
        }
        // 左侧物品
        if (this.shopItem >= 0 && this.shopItem < menu.NPCTrades.trades().size()) {
            int x = offsetX + shopItem % col * intervalX;
            int y = offsetY + (shopItem / col - scrollOff) * intervalY;
            renderSlotHighlight(guiGraphics, x, y, 20);
        }
        List<ITrade> trades = menu.NPCTrades.trades();

        int x = offsetX;
        int y = offsetY;
        int xcache = x;
        int ycache = y;
        int cacheIndex = -1;
        for (int l = 0; l < Math.min(row, NUMBER_OF_LINES); l++) {
            for (int k = 0; k < col; k++) {
                int index = k + (l + scrollOff) * col;
                if (index >= trades.size()) break;
                var trade = trades.get(index);

                // 渲染获得的物品
                renderResult(holder, guiGraphics, font, x, y, ii, jj, mouseX, mouseY, trade, index);
                if (mouseX > x && mouseX < x + 16 && mouseY > y && mouseY < y + 16) {
                    xcache = x;
                    ycache = y;
                    cacheIndex = index;
                }

                x += intervalX;
            }
            x = offsetX;
            y += intervalY;
        }

        // 如果选择了交易项
        // 渲染上面的材料物品
        if (shopItem >= 0 && shopItem < trades.size()) {

            var trade = trades.get(this.shopItem);
            x = ii + 116;
            y = jj + 19;
            renderCosts(holder, guiGraphics, font, x, y, ii, jj, mouseX, mouseY, trade);


            x = ii + 203;
            y = jj + 36;
            // 能否购买
            boolean canBuy = trade.canTradeWithLock(Minecraft.getInstance().player, holder, shopItem);
            renderResultSlot(holder, guiGraphics, font, x, y, ii, jj, mouseX, mouseY, trade, canBuy);

            // 重新渲染悬浮时物品信息
            if (cacheIndex != -1) {
                renderResultHover(holder, guiGraphics, font, xcache, ycache, ii, jj, mouseX, mouseY, trades.get(cacheIndex));
            }
        }
        this.renderTooltip(guiGraphics, mouseX, mouseY);

    }

    protected void renderCosts(ITradeHolder holder, GuiGraphics guiGraphics, Font font, int x, int y, int startx, int starty, int mouseX, int mouseY, ITrade trade) {
        trade.renderCosts(holder, guiGraphics, font, x, y, startx, starty, mouseX, mouseY);
    }

    protected void renderResult(ITradeHolder holder, GuiGraphics guiGraphics, Font font, int x, int y, int startx, int starty, int mouseX, int mouseY, ITrade trade, int slotIndex) {
        trade.renderResult(holder, guiGraphics, font, x, y, startx, starty, mouseX, mouseY, slotIndex);
    }

    protected void renderResultHover(ITradeHolder holder, GuiGraphics guiGraphics, Font font, int x, int y, int startx, int starty, int mouseX, int mouseY, ITrade trade) {
        trade.renderResultHover(holder, guiGraphics, font, x, y, startx, starty, mouseX, mouseY);
    }

    protected void renderResultSlot(ITradeHolder holder, GuiGraphics guiGraphics, Font font, int x, int y, int startx, int starty, int mouseX, int mouseY, ITrade trade, boolean canBuy) {
        trade.renderResultSlot(holder, guiGraphics, font, x, y, startx, starty, mouseX, mouseY, canBuy, menu.slots.getFirst());
    }

    private boolean canScroll() {
        return row > NUMBER_OF_LINES;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int i = row;
        if (this.canScroll()) {
            int j = i - NUMBER_OF_LINES;
            this.scrollOff = Mth.clamp((int) ((double) this.scrollOff - scrollY), 0, j);
        }
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        if (
                mouseX > (double) (i + 238) && mouseX <= (double) (i + 238 + 16) &&
                        mouseY > (double) (j + 36) && mouseY <= (double) (j + 36 + 16)
        ) {
            this.isClicked = true;
            this.lastClickTime = System.currentTimeMillis();
            return super.mouseClicked(mouseX, mouseY, button);
        }
        if (canSelect(hoveredItem, mouseX, mouseY, button)) {
            this.shopItem = hoveredItem;

            this.clickCount = button;
            menu.selectedMerchantIndex = shopItem;
            ITradeHolder.setSelectTradeIndex(shopItem);
            if (shopItem >= 0) {
                onClick(mouseX, mouseY, button, shopItem);
            }
            if (menu.selectedMerchantIndex < 0) menu.slots.getFirst().set(ItemStack.EMPTY);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    protected boolean canSelect(int index, double mouseX, double mouseY, int button) {
        return true;
    }

    protected void onClick(double mouseX, double mouseY, int button, int index) {
        this.menu.NPCTrades.trades().get(index).onClick(mouseX, mouseY, button, index, this.menu.slots.getFirst());
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        int x = (int) (mouseX - offsetX);
        int y = (int) (mouseY - offsetY);
        int i = x / intervalX;
        int j = y / intervalY;
        if (i >= 0 && i < col && j >= 0 && j < row
                && x % intervalX < 16 && y % intervalY < 16
                && x >= 0 && y >= 0
        ) {
            int index = i + (j + scrollOff) * col;
            if (index < menu.NPCTrades.trades().size()) {
                this.hoveredItem = index;

            }
        } else {
            this.hoveredItem = -1;
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.isClicked = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    protected void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type) {
        if (type != ClickType.PICKUP_ALL) {
            super.slotClicked(slot, slotId, mouseButton, type);
        }
    }
}
