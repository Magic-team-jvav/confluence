package org.confluence.mod.common.entity.npc.trade.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.trade.TradeCondition;
import org.confluence.mod.common.init.ModTradeConditions;

import java.util.Optional;

/// 世界的降雨和雷暴状态与所有已提供字段一致时成立。
public record WeatherCondition(Optional<Boolean> raining,
                               Optional<Boolean> thundering) implements TradeCondition {
    public static final MapCodec<WeatherCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Codec.BOOL.optionalFieldOf("raining").forGetter(WeatherCondition::raining), Codec.BOOL.optionalFieldOf("thundering").forGetter(WeatherCondition::thundering)).apply(instance, WeatherCondition::new));

    @Override
    public boolean test(ServerPlayer player, BaseNPC npc) {
        return (raining.isEmpty() || raining.get() == npc.level().isRaining()) && (thundering.isEmpty() || thundering.get() == npc.level().isThundering());
    }

    @Override
    public MapCodec<? extends TradeCondition> codec() {
        return ModTradeConditions.WEATHER.get();
    }
}
