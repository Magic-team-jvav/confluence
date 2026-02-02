package org.confluence.terraentity.registries.npc_trade_task.variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import org.confluence.terraentity.api.npc.trade.ITrade;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.registries.npc_trade_task.TradeTaskProvider;
import org.confluence.terraentity.registries.npc_trade_task.TradeTaskProviderTypes;

import javax.annotation.Nullable;
import java.util.List;

public class RandomTradeTask extends ProgressTradeTask {

    /**
     * @param trades 任务顺序列表
     */

    public RandomTradeTask(List<ITrade> trades) {
        super(trades);
    }

    public static MapCodec<RandomTradeTask> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.list(ITrade.TYPED_CODEC).fieldOf("trades").forGetter(RandomTradeTask::trades)
    ).apply(instance, RandomTradeTask::new));

    @Override
    public @Nullable ITrade getSelected(ITradeHolder npc, int index) {
        int size = trades.size();
        int target = npc.getTradeParams().getLevel(index);
        if (target >= size) {
            target = npc.getRandom().nextInt(size);
            npc.getTradeParams().setLevel(index, target);
        }
        return trades.get(target);
    }

    @Override
    public void setNext(ITradeHolder npc, int index) {
        int size = trades.size();
        int target = npc.getRandom().nextInt(size);
        npc.getTradeParams().setLevel(index, target);
        npc.syncTradeTasksParams();
    }

    @Override
    public Component getTitle(ITradeHolder holder, Component original){
        return Component.translatable(title() == null? "title.terra_entity.npc_trade.task.random" : title());
    }


    @Override
    public TradeTaskProvider getCodec() {
        return TradeTaskProviderTypes.RANDOM_TRADE_TASK.get();
    }
}
