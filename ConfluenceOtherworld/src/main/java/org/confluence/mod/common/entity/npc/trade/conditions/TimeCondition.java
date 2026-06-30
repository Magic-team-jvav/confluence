package org.confluence.mod.common.entity.npc.trade.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.trade.TradeCondition;
import org.confluence.mod.common.init.ModTradeConditions;

public record TimeCondition(int from, int to, boolean exclude) implements TradeCondition {
    public static final MapCodec<TimeCondition> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
                    Codec.INT.fieldOf("from").forGetter(TimeCondition::from),
                    Codec.INT.fieldOf("to").forGetter(TimeCondition::to),
                    Codec.BOOL.optionalFieldOf("exclude", false).forGetter(TimeCondition::exclude)
            ).apply(b, TimeCondition::new));

    @Override public boolean test(ServerLevel level, BaseNPC npc) {
        int dayTime = (int) (level.dayTime() % 24000);
        boolean inRange = from > to
                ? (dayTime >= from || dayTime <= to)
                : (dayTime >= from && dayTime <= to);
        return inRange != exclude;
    }
    @Override public MapCodec<? extends TradeCondition> codec() { return ModTradeConditions.TIME.get(); }
}
