package org.confluence.terraentity.registries.npc_trade_task.variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import org.confluence.terraentity.api.npc.trade.ITrade;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.api.npc.trade.ITradeTask;
import org.confluence.terraentity.registries.npc_trade_task.TradeTaskProvider;
import org.confluence.terraentity.registries.npc_trade_task.TradeTaskProviderTypes;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>固定等级给予固定物品交易任务，固定次数会获得固定的交易表，其他时候获得指定战利品表。</p>
 * <p>这个接口没有随机性，如果需要随机，采用动态池{@link DynamicPoolTradeTask}</p>
 */
public class FixedMapTradeTask implements ITradeTask  {

    private final Map<Integer, ITrade> fixed_Rewards;
    private final ITrade defaultTrade;


    public static final MapCodec<FixedMapTradeTask> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.unboundedMap(Codec.STRING, ITrade.TYPED_CODEC).fieldOf("fixed_rewards").forGetter(
                    task -> task.fixed_Rewards.entrySet().stream()
                            .map(entry->new AbstractMap.SimpleEntry<>(entry.getKey().toString(), entry.getValue()))
                            .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue))
            ),
            ITrade.TYPED_CODEC.fieldOf("default_trade").forGetter(FixedMapTradeTask::getDefaultTrade)
    ).apply(instance, (solid_rewards, trade)->{
        return new FixedMapTradeTask(solid_rewards.entrySet()
                .stream()
                .map(entry->new AbstractMap.SimpleEntry<>(Integer.parseInt(entry.getKey()), entry.getValue()))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)),
                trade
        );
    }));

    public Component getTitle(ITradeHolder holder, Component original){
        return Component.translatable(title() == null?"title.terra_entity.npc_trade.task.fixed_level":title());
    }


    private ITrade getDefaultTrade() {
        return defaultTrade;
    }

    /**
     * 用于数据生成构造
     * @param solid_rewards 次数对应的物品奖励表
     * @param defaultTrade 默认交易
     */
    public FixedMapTradeTask(Map<Integer, ITrade> solid_rewards, ITrade defaultTrade) {
        this.fixed_Rewards = solid_rewards;
        this.defaultTrade = defaultTrade;
    }

    @Override
    public @Nullable ITrade getSelected(ITradeHolder npc, int index) {
        int current = npc.getTradeParams().getLevel(index);
        if(fixed_Rewards.containsKey(current)){
            return fixed_Rewards.get(current);
        }
        return defaultTrade;
    }

    @Override
    public void setNext(ITradeHolder npc, int index) {
        Objects.requireNonNull(npc.getTradeParams()).increaseLevel(index);
        npc.syncTradeTasksParams();
    }

    @Override
    public boolean canTrade(ITradeHolder npc, int index) {
        return true;
    }

    @Override
    public List<ITrade> getAllSupportedTrades() {
        return new ArrayList<>(fixed_Rewards.values());
    }

    @Override
    public TradeTaskProvider getCodec() {
        return TradeTaskProviderTypes.ANGLER_TRADE_TASK.get();
    }
}
