package org.confluence.mod.integration.terra_entity.npc_trade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.integration.terra_entity.init.ModTradeProviders;
import org.confluence.terraentity.registries.npc_trade.ITradeHealth;
import org.confluence.terraentity.registries.npc_trade.TradeProvider;

public record MoneyTradeHealth(int health, long cost) implements IMoneyTrade, ITradeHealth  {

    public static final MapCodec<MoneyTradeHealth> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("health").forGetter(MoneyTradeHealth::health),
            Codec.LONG.fieldOf("cost").forGetter(MoneyTradeHealth::cost)
    ).apply(instance, MoneyTradeHealth::new));


    @Override
    public void onTrade(ServerPlayer player) {
        IMoneyTrade.super.onTrade(player);
    }




    public boolean canTrade(Player player) {
        return IMoneyTrade.super.canTrade(player) && ITradeHealth.super.canTrade(player);
    }


    @Override
    public void onTradeSuccess(ServerPlayer player) {
        ITradeHealth.super.onTrade(player);
    }

    @Override
    public TradeProvider getCodec() {
        return ModTradeProviders.MONEY_TRADE_HEALTH.get();
    }
}
