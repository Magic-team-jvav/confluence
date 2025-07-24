package org.confluence.mod.integration.terra_entity.npc_trade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.integration.terra_entity.init.ModTradeProviders;
import org.confluence.terraentity.api.trade.ITradeHolder;
import org.confluence.terraentity.api.trade.ITradeHealth;
import org.confluence.terraentity.registries.npc_trade.TradeProperties;
import org.confluence.terraentity.registries.npc_trade.TradeProvider;

import java.util.Optional;

public record MoneyTradeHealth(int health, long cost, TradeProperties properties) implements IMoneyTrade, ITradeHealth  {

    public static final MapCodec<MoneyTradeHealth> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("health").forGetter(MoneyTradeHealth::health),
            Codec.LONG.fieldOf("cost").forGetter(MoneyTradeHealth::cost),
            TradeProperties.CODEC.optionalFieldOf("properties").forGetter(i-> Optional.ofNullable(i.properties))
    ).apply(instance, (health, cost, properties)-> new MoneyTradeHealth(health, cost, properties.orElse(null))));


    @Override
    public void onTrade(ServerPlayer player, ITradeHolder npc, int index) {
        IMoneyTrade.super.onTrade(player, npc, index);
    }

    @Override
    public void renderResultHover(ITradeHolder npc, GuiGraphics guiGraphics, Font font, int x, int y, int startx, int starty, int mouseX, int mouseY) {

    }


    public boolean canTrade(Player player, ITradeHolder npc, int index) {
        return IMoneyTrade.super.canTrade(player, npc, index) && ITradeHealth.super.canTrade(player, npc,index);
    }


    @Override
    public void onTradeSuccess(ServerPlayer player, ITradeHolder npc, int index, long cost) {
        ITradeHealth.super.onTrade(player, npc, index);
    }

    @Override
    public TradeProvider getCodec() {
        return ModTradeProviders.MONEY_TRADE_HEALTH.get();
    }
}
