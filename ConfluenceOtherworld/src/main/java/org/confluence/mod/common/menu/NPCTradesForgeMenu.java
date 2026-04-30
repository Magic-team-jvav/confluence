package org.confluence.mod.common.menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.client.gui.container.WithForgeTradeScreen;
import org.confluence.mod.common.component.ValueComponent;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.mod.integration.terra_entity.npc_trade.SellTrade;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.init.entity.TENpcEntities;
import org.confluence.terraentity.menu.TETradesMenu;
import org.confluence.terraentity.mixed.IPlayer;
import org.jetbrains.annotations.Nullable;

public class NPCTradesForgeMenu extends TETradesMenu {
    private boolean forge;

    public NPCTradesForgeMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, null, false);
        @Nullable ITradeHolder holder = IPlayer.of(inventory.player).terra_entity$getTradeHolder();
        if (holder instanceof AbstractTerraNPC npc && npc.getType() == TENpcEntities.GOBLIN_TINKERER.get()) {
            this.forge = true;
        } else {
            try {
                if (holder != null && holder.getClass().isAssignableFrom(Class.forName("com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid"))) {
                    this.forge = true;
                }
            } catch (ClassNotFoundException ignored) {}
        }
    }

    public NPCTradesForgeMenu(int containerId, Inventory inventory, @Nullable ITradeHolder NPCTrades, boolean forge) {
        super(ModMenuTypes.NPC_TRADES_MENU.get(), containerId, inventory, NPCTrades);
        this.forge = forge;
    }

    public boolean hasForge() {
        return forge;
    }

    @Override
    protected void addResultSlot() {
        addSlot(new Slot(container, 0, 238, 37));
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (player.isLocalPlayer() && slotId > 0 && slotId <= 36 && Screen.hasControlDown()) { // 快速售卖
            ItemStack stack = getSlot(slotId).getItem();

            if (ValueComponent.getValue(stack, 0) > 0) {
                if (Minecraft.getInstance().screen instanceof WithForgeTradeScreen screen) {
                    @Nullable ITradeHolder holder = IPlayer.of(player).terra_entity$getTradeHolder();

                    if (holder != null && screen.shopItem >= 0 && holder.getTradeManager().availableTrades().get(screen.shopItem) instanceof SellTrade sell) {
                        sell.onClick(0, 0, button, 1000 + slotId, getSlot(slotId));
                    }
                }
            }
        }
        super.clicked(slotId, button, clickType, player);
    }
}
