package org.confluence.terraentity.registries.npc_trade_modify.variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.api.npc.trade.ITrade;
import org.confluence.terraentity.api.npc.trade.ITradeModifier;
import org.confluence.terraentity.entity.npc.trade.NPCTradeManager;
import org.confluence.terraentity.registries.npc_trade_modify.TradeModifierProvider;
import org.confluence.terraentity.registries.npc_trade_modify.TradeModifierProviderTypes;

import java.util.List;
import java.util.Optional;

public record TradeListModifier(int priority, ResourceLocation id, OperatorType operatorType, List<ITrade> tradeList) implements ITradeModifier {


    public static final MapCodec<TradeListModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("priority").forGetter(TradeListModifier::priority),
            ResourceLocation.CODEC.fieldOf("id").forGetter(TradeListModifier::id),
            OperatorType.CODEC.fieldOf("operator").forGetter(TradeListModifier::operatorType),
            ITrade.TYPED_CODEC.listOf().optionalFieldOf("trade_list").forGetter(i-> Optional.ofNullable(i.tradeList))
    ).apply(instance, (priority, id, operatorType,tradeList)-> new TradeListModifier(priority, id, operatorType, tradeList.orElse(null))));


    @Override
    public TradeModifierProvider getCodec() {
        return TradeModifierProviderTypes.MODIFY_LIST.get();
    }

    @Override
    public void accept(NPCTradeManager manager, ResourceLocation location) {
        List<ITrade> trades = manager.trades();
        if(operatorType == OperatorType.ADD) {
            if(tradeList == null){
                TerraEntity.LOGGER.error("TradeListModifier: trade_list is null to add for location: {}", location);
                return;
            }
            trades.addAll(tradeList);
        } else if(operatorType == OperatorType.DEL) {
            if(tradeList != null){
                TerraEntity.LOGGER.warn("TradeListModifier: trade_list is not null to delete for location, that's not need: {}", location);
                return;
            }
            trades.clear();
        } else if(operatorType == OperatorType.REPLACE) {
            if(tradeList == null){
                TerraEntity.LOGGER.error("TradeListModifier: trade_list is null to replace for location: {}", location);
                return;
            }
            trades.clear();
            trades.addAll(tradeList);
        } else {
            TerraEntity.LOGGER.error("TradeListModifier: invalid operator_type for location: {}", location);
        }
    }
}
