package org.confluence.mod.common.entity.npc.trade.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.trade.TradeCondition;
import org.confluence.mod.common.init.ModTradeConditions;

public record TimeCondition(int from, int to, boolean exclude) implements TradeCondition {
    public static final MapCodec<TimeCondition> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
                    Codec.INT.fieldOf("from").forGetter(TimeCondition::from),
                    Codec.INT.fieldOf("to").forGetter(TimeCondition::to),
                    Codec.BOOL.optionalFieldOf("exclude", false).forGetter(TimeCondition::exclude)
            ).apply(b, TimeCondition::new));

    @Override public boolean test(ServerPlayer player, BaseNPC npc) {
        int dayTime = (int) (player.serverLevel().dayTime() % 24000);
        boolean inRange = from > to
                ? (dayTime >= from || dayTime <= to)
                : (dayTime >= from && dayTime <= to);
        return inRange != exclude;
    }
    @Override public MapCodec<? extends TradeCondition> codec() { return ModTradeConditions.TIME.get(); }
}
