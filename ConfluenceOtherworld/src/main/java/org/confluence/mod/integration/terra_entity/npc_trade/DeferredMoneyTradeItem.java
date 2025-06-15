package org.confluence.mod.integration.terra_entity.npc_trade;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.component.ValueComponent;
import org.confluence.mod.integration.terra_entity.init.ModTradeProviders;
import org.confluence.terraentity.entity.npc.trade.ITradeHolder;
import org.confluence.terraentity.registries.npc_trade.ITradeItem;
import org.confluence.terraentity.registries.npc_trade.TradeProperties;
import org.confluence.terraentity.registries.npc_trade.TradeProvider;
import org.confluence.terraentity.registries.npc_trade_lock.ITradeLock;

public class DeferredMoneyTradeItem implements ITradeItem, IMoneyTrade {
    public static final MapCodec<DeferredMoneyTradeItem> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("deferred_result").forGetter(trade -> trade.deferredResult),
            ExtraCodecs.intRange(1, LibUtils.MAX_STACK_SIZE).lenientOptionalFieldOf("count", 1).forGetter(trade -> trade.count),
            ITradeLock.TYPED_CODEC.fieldOf("lock").forGetter(trade -> trade.properties.lock())
    ).apply(instance, DeferredMoneyTradeItem::new));
    private final ResourceLocation deferredResult;
    private final int count;
    private final TradeProperties properties;
    private ItemStack cachedResult = ItemStack.EMPTY;

    public DeferredMoneyTradeItem(ResourceLocation deferredResult, int count, ITradeLock lock) {
        this.deferredResult = deferredResult;
        this.count = count;
        this.properties = new TradeProperties(lock);
    }

    @Override
    public ItemStack result() {
        if (cachedResult == ItemStack.EMPTY) {
            Item item = BuiltInRegistries.ITEM.get(deferredResult);
            if (item == Items.AIR) throw new IllegalArgumentException("Illegal money trade result: '" + deferredResult + "'");
            this.cachedResult = new ItemStack(item, count);
        }
        return cachedResult;
    }

    @Override
    public TradeProperties properties() {
        return properties;
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
        return ValueComponent.getValue(result(), 0) * 5L; // 买入价是卖出价的五倍
    }

    @Override
    public TradeProvider getCodec() {
        return ModTradeProviders.DEFERRED_MONEY_TRADE_ITEM.get();
    }
}