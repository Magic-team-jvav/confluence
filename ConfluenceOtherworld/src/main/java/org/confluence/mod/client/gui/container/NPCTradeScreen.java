package org.confluence.mod.client.gui.container;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.event.ModClientSetups;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.terraentity.entity.npc.NPCTrades;
import org.confluence.mod.common.menu.NPCTradesMenu;
import org.confluence.mod.network.c2s.OpenMenuPacketC2S;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.terraentity.entity.ai.keyframe.animation.KeyframeAnimation;
import org.confluence.terraentity.init.entity.TENpcEntities;
import org.confluence.terraentity.mixed.IPlayer;

import java.awt.*;
import java.util.List;


public class NPCTradeScreen extends AbstractContainerScreen<NPCTradesMenu> {
    private static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.withDefaultNamespace("container/villager/scroller");
    private static final ResourceLocation SCROLLER_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("container/villager/scroller_disabled");
    private static final ResourceLocation MENU_LOCATION = Confluence.asResource("textures/gui/container/npc_shop.png");
    private static final int NUMBER_OF_LINES = 7;
    private static final Component TRADES_LABEL = Component.translatable("title.confluence.npc_trade");

    private int shopItem = -1;
    private int hoveredItem = -1;
    private int row;
    private final int col = 4;
    private int offsetX;
    private int offsetY;
    private int intervalX = 20;
    int tickCount;

    int scrollOff;
    ImageButton forgeBt;

    KeyframeAnimation interpolator;
    private List<Item> coinItem = List.of(
            ModItems.PLATINUM_COIN.get(),
            ModItems.GOLDEN_COIN.get(),
            ModItems.SILVER_COIN.get(),
            ModItems.COPPER_COIN.get());

    public NPCTradeScreen(NPCTradesMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 290;
        this.inventoryLabelX = 107;

    }

    @Override
    protected void init() {
        super.init();
        if (menu.NPCTrades == null) {
            menu.NPCTrades = ((IPlayer) Minecraft.getInstance().player).terra_entity$getDaveTrades();
            if (menu.NPCTrades == null){
                return;
            }
        }
        this.row = menu.NPCTrades.trades().size() / 3;
        if (menu.NPCTrades.trades().size() % 3 != 0)
            this.row++;

        offsetX = (this.width - this.imageWidth) / 2 + 10;
        offsetY = (this.height - this.imageHeight) / 2 + 20;

        interpolator = KeyframeAnimation.Builder()
                .addKeyframe(5, 0)
                .addKeyframe(20, 40)
                .addKeyframe(30, 55)
                .addKeyframe(40, 60)
                .build();

        if(menu.forge){
            forgeBt = new ImageButton(leftPos + 2, topPos + 2, 16, 16, ModClientSetups.EXTRA_INVENTORY_BUTTON, button -> {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player != null) {
                    ItemStack stack = player.containerMenu.getCarried();
                    player.containerMenu.setCarried(ItemStack.EMPTY);
                    ((IPlayer) player).terra_entity$setDaveTrades(NPCTrades.getTrade(TENpcEntities.GUIDE.getId()));
                    OpenMenuPacketC2S.sendToServer(OpenMenuPacketC2S.NPC_REFORGE_MENU, stack);
                }
            });
            addRenderableWidget(forgeBt);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {

        guiGraphics.setColor(1, 1, 1, (float)(v / 60f));
        int fy = 6 - (int)((60 - v) / 5);

        guiGraphics.drawString(this.font, ((MutableComponent)this.title).withStyle(Style.EMPTY.withBold(true)), 49 + this.imageWidth / 2 - this.font.width(this.title) / 2, fy, 0xFF5656, false);
        guiGraphics.setColor(1, 1, 1, 1);
        guiGraphics.drawString(this.font, this.playerInventoryTitle,90 + this.imageWidth / 2, this.inventoryLabelY, 4210752, false);
        int l = this.font.width(TRADES_LABEL);

        guiGraphics.drawString(this.font, TRADES_LABEL, 5 - l / 2 + 48, 6, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(MENU_LOCATION, i, j, 0, 0.0F, 0.0F, this.imageWidth-15, this.imageHeight, 512, 256);
    }

    private void renderScroller(GuiGraphics guiGraphics, int posX, int posY) {

        int i = menu.NPCTrades.trades().size() - 7;
        if (i > 1) {
            int j = 139 - (27 + (i - 1) * 139 / i);
            int k = 1 + j / i + 139 / i;
            int l = 113;
            int i1 = Math.min(113, this.scrollOff * k);
            if (this.scrollOff == i - 1) {
                i1 = 113;
            }
            guiGraphics.blitSprite(SCROLLER_SPRITE, posX + 94, posY + 18 + i1, 0, 6, 27);
        } else {
            guiGraphics.blitSprite(SCROLLER_DISABLED_SPRITE, posX + 94, posY + 18, 0, 6, 27);
        }
    }

    @Override
    protected void containerTick() {

        this.tickCount++;
    }

    double v;
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if(interpolator == null) return;

        if (menu.NPCTrades == null) {
            menu.NPCTrades = ((IPlayer) Minecraft.getInstance().player).terra_entity$getDaveTrades();
            if (menu.NPCTrades == null){
                return;
            }
        }
        this.row = menu.NPCTrades.trades().size() / 3;
        if (menu.NPCTrades.trades().size() % 3 != 0)
            this.row++;


        v = interpolator.cal(this.tickCount + partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        if(forgeBt != null){
            forgeBt.render(guiGraphics, mouseX, mouseY, partialTick);
        }


        int ii = (this.width - this.imageWidth) / 2;
        int jj = (this.height - this.imageHeight) / 2;
        this.renderScroller(guiGraphics, ii, jj);

        // 左侧物品
        if(this.hoveredItem >= 0 && this.hoveredItem < menu.NPCTrades.trades().size()){
            int x = offsetX + hoveredItem % col * intervalX;
            int y = offsetY + hoveredItem / col * 20;
            renderSlotHighlight(guiGraphics,x ,y, 20);
        }
        // 左侧物品
        if(this.shopItem >= 0 && this.shopItem < menu.NPCTrades.trades().size()){
            int x = offsetX + shopItem % col * intervalX;
            int y = offsetY + shopItem / col * 20;
            renderSlotHighlight(guiGraphics,x ,y , 20);
        }
        var trades = menu.NPCTrades.trades();
        int x = offsetX;
        int y = offsetY;
        for (int l = 0; l < Math.min(row, NUMBER_OF_LINES); l++) {
            for(int k = 0; k < col; k++){
                int index = k+(l+ scrollOff) * col;
                if(index >= trades.size()) break;
                var it = trades.get(index).result();

                guiGraphics.renderItem(it, x , y );
                if(mouseX > x && mouseX < x+16 && mouseY > y && mouseY < y+16){
//                    guiGraphics.setColor(1, 1, 1, 1);
                    guiGraphics.renderTooltip(this.font, it, mouseX, mouseY);
                }

                guiGraphics.renderItemDecorations(this.font, it, x, y);
                x+=intervalX;
            }
            x = offsetX;
            y += 20;
        }
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        //背包的金币

        int []myCoins =  PlayerUtils.getCoins(Minecraft.getInstance().player);
        x = ii + 5;
        y = jj - 20;
        for( int i = 0; i < myCoins.length; i++){
            ItemStack stack = new ItemStack(coinItem.get(i), myCoins[i]);
            int count = stack.getCount();
            guiGraphics.renderItem(stack, x, y );
            float scale = (count < 1000) ? 1.0F : 0.75F;
            int moveX = (count < 1000) ? 0 : 6;
            int moveY = (count < 1000) ? 0 : 5;
            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(scale, scale, 1.0F);
            guiGraphics.renderItemDecorations(this.font, stack, (int) (x / scale) + moveX, (int) (y / scale) + moveY);
            guiGraphics.pose().popPose();
            x+= (count == 0) ? 0 : 20;
        }

        // 上面的材料物品
        if(shopItem < 0 ||shopItem >= trades.size())
            return;

        var trade = trades.get(this.shopItem);

        int[] coins = PlayerUtils.decodeCoin(trade.cost());
        x = ii + 120;
        y = jj + 21;

        for(int k = 0; k < coins.length; k++){
            guiGraphics.renderItem(coinItem.get(3-k).getDefaultInstance(), x, y );
            guiGraphics.drawString(this.font, String.valueOf(coins[k]), x+4, y+16 , Color.orange.getRGB(), true);
            x+=20;
            if( k % 3 == 2){
                y += 25;
                x = ii + 130;
            }
        }


        boolean canBuy = trade.canTrade(Minecraft.getInstance().player);

        // 能否购买
        if(canBuy){
            menu.slots.get(0).set(trade.result().copy());
            guiGraphics.blit(MENU_LOCATION,ii+203,jj+35,276,0,35,17,512,256);
        }else{
            menu.slots.get(0).set(ItemStack.EMPTY);
            guiGraphics.blit(MENU_LOCATION,ii+203,jj+35,276,17,35,17,512,256);
        }
    }

    private boolean canScroll() {
        return row > NUMBER_OF_LINES;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int i = row;
        if (this.canScroll()) {
            int j = i - NUMBER_OF_LINES;
            this.scrollOff = Mth.clamp((int)((double)this.scrollOff - scrollY), 0, j);
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
                mouseX > (double)(i + 238) && mouseX <= (double)(i + 238 + 16) &&
                mouseY > (double)(j + 36)&& mouseY <= (double)(j + 36 + 16)
        ) {
            return super.mouseClicked(mouseX, mouseY, button);
        }
        this.shopItem = hoveredItem;
        menu.selectedMerchantIndex = shopItem;
        if(menu.selectedMerchantIndex <0) menu.slots.get(0).set(ItemStack.EMPTY);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {

        int x = (int) (mouseX - offsetX);
        int y = (int) (mouseY - offsetY);
        int i = x / intervalX;
        int j = y / 20;
        if (i >= 0 && i < col && j >= 0 && j < row
                && x % intervalX < 16 && y % 20 < 16
                && x >= 0 && y >= 0
        ) {
            int index = i + (j + scrollOff) * col;
            if (index < menu.NPCTrades.trades().size()) {
                this.hoveredItem = index;

            }
        }else {
            this.hoveredItem = -1;
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class TradeOfferButton extends Button {
        final int index;
        public TradeOfferButton(int x, int y, int index, OnPress onPress) {
            super(x, y, 88, 20, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
            this.index = index;
            this.visible = false;
        }
        public int getIndex() {
            return this.index;
        }
    }
}
