package org.confluence.mod.common.entity.npc.trade.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.trade.TradeCondition;
import org.confluence.mod.common.init.ModTradeConditions;

public record NotCondition(TradeCondition inner) implements TradeCondition {
    public static final MapCodec<NotCondition> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
                    TradeCondition.CODEC.fieldOf("inner").forGetter(NotCondition::inner)
            ).apply(b, NotCondition::new));

    @Override public boolean test(ServerLevel level, BaseNPC npc) {
        return !inner.test(level, npc);
    }
    @Override public MapCodec<? extends TradeCondition> codec() { return ModTradeConditions.NOT.get(); }
}
