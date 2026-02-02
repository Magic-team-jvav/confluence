package org.confluence.terraentity.registries.npc_trade.variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.confluence.lib.common.recipe.AmountIngredient;
import org.confluence.terraentity.api.npc.trade.IIngredientTrade;
import org.confluence.terraentity.api.npc.trade.ITradeHealth;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.registries.npc_trade.TradeProperties;
import org.confluence.terraentity.registries.npc_trade.TradeProvider;
import org.confluence.terraentity.registries.npc_trade.TradeProviderTypes;

import java.util.List;
import java.util.Optional;

public record ItemTradeHealth(List<AmountIngredient> costs, int health, TradeProperties properties) implements IIngredientTrade, ITradeHealth {

    public static final MapCodec<ItemTradeHealth> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            AmountIngredient.CODEC.codec().listOf().fieldOf("costs").forGetter(ItemTradeHealth::costs),
            Codec.INT.fieldOf("health").forGetter(ItemTradeHealth::health),
            TradeProperties.CODEC.optionalFieldOf("properties").forGetter(i-> Optional.ofNullable(i.properties))

    ).apply(instance, (cost, health, properties)->new ItemTradeHealth(
            cost,
            health,
            properties.orElse(null)
    )));


    @Override
    public boolean canTrade(Player player, ITradeHolder npc, int index) {
        return ITradeHealth.super.canTrade(player, npc, index) && IIngredientTrade.super.canTrade(player, npc, index);
    }

    @Override
    public void onTrade(ServerPlayer player, ITradeHolder npc, int index) {
        ITradeHealth.super.onTrade(player, npc, index);
        IIngredientTrade.super.onTrade(player, npc, index);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderResultHover(ITradeHolder npc, GuiGraphics guiGraphics, Font font, int x, int y, int startx, int starty, int mouseX, int mouseY) {

    }


    @Override
    public TradeProvider getCodec() {
        return TradeProviderTypes.ITEM_TRADE_HEALTH.get();
    }

}
