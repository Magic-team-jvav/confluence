package org.confluence.terraentity.registries.npc_trade_task.variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import org.confluence.terraentity.api.npc.trade.ITrade;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.api.npc.trade.ITradeTask;
import org.confluence.terraentity.entity.npc.trade.TradeParams;
import org.confluence.terraentity.registries.npc_trade_task.TradeTaskProvider;
import org.confluence.terraentity.registries.npc_trade_task.TradeTaskProviderTypes;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * 按进度的任务
 */
public class ProgressTradeTask implements ITradeTask {

    protected final List<ITrade> trades;

    /**
     * @param trades 任务顺序列表
     */
    public ProgressTradeTask(List<ITrade> trades) {
        this.trades = trades;
    }


    public static MapCodec<ProgressTradeTask> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.list(ITrade.TYPED_CODEC).fieldOf("trades").forGetter(ProgressTradeTask::trades)
    ).apply(instance, ProgressTradeTask::new));

    public List<ITrade> trades() {
        return trades;
    }

    @Override
    public @Nullable ITrade getSelected(ITradeHolder npc, int index) {
        TradeParams params = npc.getTradeParams();
        int target = params.getLevel(index);;
        if (target >= trades.size()) {
            return null;
        }
        return trades.get(target);
    }

    @Override
    public Component getTitle(ITradeHolder holder, Component original){
        return Component.translatable(title() == null?"title.terra_entity.npc_trade.task.progress":title());
    }

    @Override
    public void setNext(ITradeHolder npc, int index) {
        Objects.requireNonNull(npc.getTradeParams()).increaseLevel(index);
        npc.syncTradeTasksParams();
    }

    @Override
    public boolean canTrade(ITradeHolder npc, int index) {
        return npc.getTradeParams().getLevel(index) < trades.size();
    }

    @Override
    public List<ITrade> getAllSupportedTrades() {
        return trades;
    }

    @Override
    public TradeTaskProvider getCodec() {
        return TradeTaskProviderTypes.PROGRESS_TRADE_TASK.get();
    }
}
