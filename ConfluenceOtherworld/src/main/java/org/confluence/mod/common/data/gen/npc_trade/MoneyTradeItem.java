package org.confluence.mod.common.data.gen.npc_trade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.terraentity.registries.npc_trade.ITrade;
import org.confluence.terraentity.registries.npc_trade.TradeProvider;

public record MoneyTradeItem(ItemStack result, long cost)implements ITrade {
    public boolean canTrade(Player player) {
        return PlayerUtils.getMoney(player) >= cost;
    }

    @Override
    public TradeProvider getCodec() {
        return ModTradeProviders.MONEY_TRADE_ITEM.get();
    }

    public static final MapCodec<MoneyTradeItem> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemStack.CODEC.fieldOf("result").forGetter(MoneyTradeItem::result),
            Codec.LONG.fieldOf("cost").forGetter(MoneyTradeItem::cost)
    ).apply(instance, MoneyTradeItem::new));


}