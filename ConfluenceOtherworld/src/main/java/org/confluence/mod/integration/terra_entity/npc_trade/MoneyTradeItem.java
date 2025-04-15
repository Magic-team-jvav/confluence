package org.confluence.mod.integration.terra_entity.npc_trade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.integration.terra_entity.init.ModTradeProviders;
import org.confluence.terraentity.registries.npc_trade.ITradeItem;
import org.confluence.terraentity.registries.npc_trade.TradeProvider;

public record MoneyTradeItem(ItemStack result, long cost)implements ITradeItem, IMoneyTrade {

    public static final MapCodec<MoneyTradeItem> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemStack.CODEC.fieldOf("result").forGetter(MoneyTradeItem::result),
            Codec.LONG.fieldOf("cost").forGetter(MoneyTradeItem::cost)
    ).apply(instance, MoneyTradeItem::new));


    @Override
    public void onTrade(ServerPlayer player) {
        IMoneyTrade.super.onTrade(player);
    }

    @Override
    public void onTradeSuccess(ServerPlayer player) {
        ItemStack result = this.result();
        if(player.getInventory().getFreeSlot() ==-1){
            player.drop(result.copy(),false);
        }
        else player.addItem(result.copy());
    }


    @Override
    public TradeProvider getCodec() {
        return ModTradeProviders.MONEY_TRADE_ITEM.get();
    }



}