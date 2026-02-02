package org.confluence.terraentity.registries.npc_trade.variant;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.confluence.lib.common.recipe.AmountIngredient;
import org.confluence.terraentity.api.npc.trade.IIngredientTrade;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.api.npc.trade.ITradeItemList;
import org.confluence.terraentity.registries.npc_trade.TradeProperties;
import org.confluence.terraentity.registries.npc_trade.TradeProvider;
import org.confluence.terraentity.registries.npc_trade.TradeProviderTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 物品交换其他的东西，统一使用这个类
 * @param costs 花费的物品原料
 * @param result 获得的物品列表
 * @param properties 交易属性
 */
public record ItemTradeItemList(List<AmountIngredient> costs, List<ItemStack> result, TradeProperties properties) implements IIngredientTrade, ITradeItemList {

    public static final MapCodec<ItemTradeItemList> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            AmountIngredient.CODEC.codec().listOf().fieldOf("costs").forGetter(ItemTradeItemList::costs),
            ItemStack.OPTIONAL_CODEC.listOf().fieldOf("result").forGetter(ItemTradeItemList::result),
            TradeProperties.CODEC.optionalFieldOf("properties").forGetter(i-> Optional.ofNullable(i.properties))
    ).apply(instance, (costs, result, properties)->new ItemTradeItemList(
            costs,
            result,
            properties.orElse(null)
    )));

    public static Builder builder(){
        return new Builder();
    }


    public static class Builder extends IIngredientTrade.Builder<ItemTradeItemList, Builder> {
        private final List<ItemStack> result = new ArrayList<>();

        public Builder addResult(ItemLike item) {
            return addResult(new ItemStack(item));
        }

        public Builder addResult(ItemLike item, int count) {
            return addResult(new ItemStack(item, count));
        }

        public Builder addResult(ItemStack result) {
            this.result.add(result);
            return this;
        }

        public Builder addResult(List<ItemStack> result) {
            this.result.addAll(result);
            return this;
        }

        public ItemTradeItemList build(){
            return new ItemTradeItemList(costs, result, properties);
        }
    }

    @Override
    public void onTrade(ServerPlayer player, ITradeHolder npc, int index) {
        IIngredientTrade.super.onTrade(player, npc, index);
        ITradeItemList.super.onTrade(player, npc, index);

    }

    @Override
    public TradeProvider getCodec() {
        return TradeProviderTypes.INGREDIENT_TRADE_ITEM_LIST.get();
    }


}
