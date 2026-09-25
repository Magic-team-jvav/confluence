package org.confluence.mod.client.gui.container.npc_screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.npc.trade.NPCTradeMenu;
import org.confluence.mod.common.init.entity.NpcEntities;

import java.util.Map;

/// NPC 商店界面的入口；只有贴图不同的商店直接登记对应纹理。
public final class NPCTradeScreens {
    private static final Map<EntityType<?>, ResourceLocation> OVERLAYS = Map.of(
            NpcEntities.GOBLIN_TINKERER.get(), Confluence.asResource("textures/gui/trade/goblin_tinkerer_trade.png"),
            NpcEntities.MECHANIC.get(), Confluence.asResource("textures/gui/trade/mechanic_trade.png"),
            NpcEntities.DRYAD.get(), Confluence.asResource("textures/gui/trade/dryad_trade.png"),
            NpcEntities.MERCHANT.get(), Confluence.asResource("textures/gui/trade/merchant_trade.png"),
            NpcEntities.DYE_TRADER.get(), Confluence.asResource("textures/gui/trade/dye_trader_trade.png"),
            NpcEntities.ZOOLOGIST.get(), Confluence.asResource("textures/gui/trade/zoologist_trade.png"),
            NpcEntities.DEMOLITIONIST.get(), Confluence.asResource("textures/gui/trade/demolitionist_trade.png")
    );

    private NPCTradeScreens() {}

    public static NPCTradeScreen create(NPCTradeMenu menu, Inventory inventory, Component title) {
        return new NPCTradeScreen(menu, inventory, title);
    }

    static void renderOverlay(NPCTradeMenu menu, GuiGraphics graphics, int left, int top, int width, int height) {
        ResourceLocation overlay = null;
        if (menu.getNPC() != null) {
            overlay = OVERLAYS.get(menu.getNPC().getType());
        }
        if (overlay != null) {
            graphics.blit(overlay, left, top - 10, 0, 0, width, height);
        }
    }
}
