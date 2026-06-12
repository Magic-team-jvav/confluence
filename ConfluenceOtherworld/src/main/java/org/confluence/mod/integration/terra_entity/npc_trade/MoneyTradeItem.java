package org.confluence.mod.integration.terra_entity.npc_trade;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.confluence.mod.common.component.ValueComponent;
import org.confluence.mod.integration.terra_entity.init.ModTradeProviders;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.api.npc.trade.ITradeItem;
import org.confluence.terraentity.registries.npc_trade.TradeProperties;
import org.confluence.terraentity.registries.npc_trade.TradeProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record MoneyTradeItem(ItemStack result,
                             @Nullable TradeProperties properties) implements ITradeItem, IMoneyTrade {
    public static final MapCodec<MoneyTradeItem> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemStack.CODEC.fieldOf("result").forGetter(MoneyTradeItem::result),
            TradeProperties.CODEC.optionalFieldOf("properties").forGetter(i -> Optional.ofNullable(i.properties))
    ).apply(instance, (result, properties) -> new MoneyTradeItem(result, properties.orElse(null))));

    public static class Builder {
        private ItemStack result;
        private TradeProperties properties;

        public Builder setProperties(TradeProperties properties) {
            this.properties = properties;
            return this;
        }

        public Builder setResult(ItemStack result) {
            this.result = result;
            return this;
        }

        public Builder setResult(ItemLike result) {
            this.result = result.asItem().getDefaultInstance();
            return this;
        }

        public MoneyTradeItem build() {
            return new MoneyTradeItem(result, properties);
        }
    }

    @Override
    public void onTrade(ServerPlayer player, ITradeHolder npc, int index) {
        IMoneyTrade.super.onTrade(player, npc, index);
    }

    @Override
    public void onTradeSuccess(ServerPlayer player, ITradeHolder npc, int index, long cost) {
        ItemStack result = this.result();
        if (player.getInventory().getFreeSlot() == -1) {
            player.drop(result.copy(), false);
        } else player.addItem(result.copy());
    }

    @Override
    public long cost() {
        return ValueComponent.getValue(result, 0) * 5L; // 买入价是卖出价的五倍
    }

    @Override
    public TradeProvider getCodec() {
        return ModTradeProviders.MONEY_TRADE_ITEM.get();
    }
}
