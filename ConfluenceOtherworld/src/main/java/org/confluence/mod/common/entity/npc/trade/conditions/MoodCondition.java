package org.confluence.mod.common.entity.npc.trade.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.trade.TradeCondition;
import org.confluence.mod.common.init.ModTradeConditions;

public record MoodCondition(int value, boolean less) implements TradeCondition {
    public static final MapCodec<MoodCondition> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
                    Codec.INT.fieldOf("value").forGetter(MoodCondition::value),
                    Codec.BOOL.optionalFieldOf("less", false).forGetter(MoodCondition::less)
            ).apply(b, MoodCondition::new));

    @Override public boolean test(ServerLevel level, BaseNPC npc) {
        int mood = npc.getMood().getValue();
        return less ? mood <= value : mood >= value;
    }
    @Override public MapCodec<? extends TradeCondition> codec() { return ModTradeConditions.MOOD.get(); }
}
