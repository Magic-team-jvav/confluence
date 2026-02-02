package org.confluence.terraentity.registries.npc_trade_task.variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.confluence.terraentity.api.npc.trade.ITrade;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.api.npc.trade.ITradeTask;
import org.confluence.terraentity.data.codec.TECodecs;
import org.confluence.terraentity.registries.npc_trade.variant.ItemTradeItemList;
import org.confluence.terraentity.registries.npc_trade_task.TradeTaskProvider;
import org.confluence.terraentity.registries.npc_trade_task.TradeTaskProviderTypes;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 动态交易表，resultPool存放每个等级对应的固定奖励List，若不含这个等级，则使用默认奖励
 */
public class DynamicPoolTradeTask implements ITradeTask {

    private ITrade dynamicTrade;

    private final ITrade defaultTrade;
    private final Map<Integer, List<ItemStack>> resultPool;
    private final List<ItemStack> costPool;

    public static final MapCodec<DynamicPoolTradeTask> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ITrade.TYPED_CODEC.fieldOf("default_trade").forGetter(DynamicPoolTradeTask::getDefaultTrade),
            Codec.unboundedMap(TECodecs.INT_KEY, ItemStack.CODEC.listOf()).fieldOf("result_pool").forGetter(task -> task.resultPool),
            ItemStack.OPTIONAL_CODEC.listOf().fieldOf("cost_pool").forGetter(DynamicPoolTradeTask::getCostPool),
            ITrade.TYPED_CODEC.optionalFieldOf("dynamic_trade").forGetter(DynamicPoolTradeTask::getDynamicTrade)
    ).apply(instance, (defaultTrade, solid_rewards, costPool, dynamicTrade)->{
        return new DynamicPoolTradeTask(
                defaultTrade,
                solid_rewards,
                costPool,
                dynamicTrade.orElse(null)
        );
    }));

    public Component getTitle(ITradeHolder holder, Component original){
        return Component.translatable(title() == null?"title.terra_entity.npc_trade.task.dynamic_reward":title());
    }


    private Optional<ITrade> getDynamicTrade() {
        return Optional.ofNullable(dynamicTrade);
    }

    private List<ItemStack> getCostPool() {
        return costPool;
    }

    private ITrade getDefaultTrade() {
        return defaultTrade;
    }

    /**
     * 用于数据生成
     * @param defaultTrade 默认奖励
     * @param resultPool 等级对应的固定奖励池
     * @param costPool 消耗物品池
     */
    public DynamicPoolTradeTask(ITrade defaultTrade, Map<Integer, List<ItemStack>> resultPool, List<ItemStack> costPool) {
        this(defaultTrade, resultPool, costPool, null);
    }

    public DynamicPoolTradeTask(ITrade defaultTrade, Map<Integer, List<ItemStack>> resultPool, List<ItemStack> costPool, ITrade dynamicTrade) {
        this.defaultTrade = defaultTrade;
        this.resultPool = resultPool;
        this.costPool = costPool;
        this.dynamicTrade = dynamicTrade;
    }

    @Override
    public @Nullable ITrade getSelected(ITradeHolder npc, int index) {
        int cur = npc.getTradeParams().getLevel(index);
        if(resultPool.containsKey(cur)){
            return dynamicTrade == null ? defaultTrade:dynamicTrade;
        }
        return defaultTrade;
    }

    // 在指定时间由渔夫主动调用
    @Override
    public void setNext(ITradeHolder npc, int index) {
        int cur = npc.getTradeParams().getLevel(index)+1;
        if(resultPool.containsKey(cur)){
            int maxCost = costPool.size();
            int random = npc.getRandom().nextInt(maxCost);


            dynamicTrade = ItemTradeItemList.builder()
                    .addCost(costPool.get(random))
                    .addResult(resultPool.get(cur)).build();
            npc.getTradeManager().addToBeSync(index);
        }
        npc.getTradeParams().increaseLevel(index);
        npc.syncTradeTasksParams();
    }



    @Override
    public boolean canTrade(ITradeHolder npc, int index) {
        return true;
    }


    // todo
    @Override
    public List<ITrade> getAllSupportedTrades() {
        return List.of();
    }

    @Override
    public TradeTaskProvider getCodec() {
        return TradeTaskProviderTypes.DYNAMIC_POOL_MAP_TRADE_TASK.get();
    }
}
