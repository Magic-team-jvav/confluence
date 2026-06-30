package org.confluence.mod.common.entity.npc.trade.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.trade.TradeCondition;
import org.confluence.mod.common.init.ModTradeConditions;

public record DateCondition(int fromMonth, int fromDay, int toMonth, int toDay) implements TradeCondition {
    public static final MapCodec<DateCondition> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
                    Codec.INT.fieldOf("from_month").forGetter(DateCondition::fromMonth),
                    Codec.INT.fieldOf("from_day").forGetter(DateCondition::fromDay),
                    Codec.INT.fieldOf("to_month").forGetter(DateCondition::toMonth),
                    Codec.INT.fieldOf("to_day").forGetter(DateCondition::toDay)
            ).apply(b, DateCondition::new));

    @Override public boolean test(ServerLevel level, BaseNPC npc) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        int month = cal.get(java.util.Calendar.MONTH);
        int day = cal.get(java.util.Calendar.DATE);
        int from = fromMonth * 100 + fromDay;
        int to = toMonth * 100 + toDay;
        int now = month * 100 + day;
        return now >= from && now <= to;
    }
    @Override public MapCodec<? extends TradeCondition> codec() { return ModTradeConditions.DATE.get(); }
}
