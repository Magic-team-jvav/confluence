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
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * 单个表项修改器
 */
public record TradeItemModifier(int priority, int target, ResourceLocation id, OperatorType type, @Nullable ITrade trade) implements ITradeModifier  {

    public static final MapCodec<TradeItemModifier> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
                Codec.INT.fieldOf("priority").forGetter(TradeItemModifier::priority),
                Codec.INT.fieldOf("target").forGetter(TradeItemModifier::target),
                ResourceLocation.CODEC.fieldOf("id").forGetter(TradeItemModifier::id),
                OperatorType.CODEC.fieldOf("operator").forGetter(TradeItemModifier::type),
                ITrade.TYPED_CODEC.optionalFieldOf("trade").forGetter(i-> Optional.ofNullable(i.trade))
           ).apply(builder, (priority, target, id, type, trade)-> new TradeItemModifier(priority, target, id, type, trade.orElse(null))));


    @Override
    public int priority() {
        return priority;
    }

    @Override
    public void accept(NPCTradeManager manager, ResourceLocation id) {
        List<ITrade> trades = manager.trades();
        if(type == OperatorType.ADD){
            if(trade == null){
                TerraEntity.LOGGER.error("TradeItemModifier: trade is null, cannot add to trade: {}", id);
                return;
            }
            trades.add(target, trade);
        }else if(type == OperatorType.DEL){
            if(target < 0 || target >= trades.size()){
                TerraEntity.LOGGER.error("TradeItemModifier: target index out of range, cannot delete from trade: {}", id);
                return;
            }
            trades.remove(target);
        }else if(type == OperatorType.REPLACE){
            if(target < 0 || target >= trades.size()){
                TerraEntity.LOGGER.error("TradeItemModifier: target index out of range, cannot replace in trade: {}", id);
                return;
            }
            if(trade == null){
                TerraEntity.LOGGER.error("TradeItemModifier: trade is null, cannot replace in trade: {}", id);
                return;
            }
            trades.set(target, trade);
        }else{
            TerraEntity.LOGGER.error("TradeItemModifier: unknown operator type, cannot modify trade: {}", id);
            return;
        }
    }

    @Override
    public TradeModifierProvider getCodec() {
        return TradeModifierProviderTypes.MODIFY_SINGLE.get();
    }
}
