package org.confluence.mod.common.entity.npc.trade.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.trade.TradeCondition;
import org.confluence.mod.common.init.ModTradeConditions;

public record OrCondition(TradeCondition left, TradeCondition right) implements TradeCondition {
    public static final MapCodec<OrCondition> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
                    TradeCondition.CODEC.fieldOf("left").forGetter(OrCondition::left),
                    TradeCondition.CODEC.fieldOf("right").forGetter(OrCondition::right)
            ).apply(b, OrCondition::new));

    @Override public boolean test(ServerLevel level, BaseNPC npc) {
        return left.test(level, npc) || right.test(level, npc);
    }
    @Override public MapCodec<? extends TradeCondition> codec() { return ModTradeConditions.OR.get(); }
}
