package org.confluence.mod.common.entity.npc.trade.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.common.data.saved.MoonPhase;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.trade.TradeCondition;
import org.confluence.mod.common.init.ModTradeConditions;

import java.util.List;

public record MoonPhaseCondition(List<MoonPhase> phases) implements TradeCondition {
    public static final MapCodec<MoonPhaseCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            MoonPhase.CODEC.listOf().fieldOf("phases").forGetter(MoonPhaseCondition::phases)
    ).apply(instance, MoonPhaseCondition::new));

    public MoonPhaseCondition(MoonPhase... phases) {
        this(List.of(phases));
    }

    public MoonPhaseCondition {
        phases = List.copyOf(phases);
    }

    @Override
    public boolean test(ServerPlayer player, BaseNPC npc) {
        return phases.contains(MoonPhase.of(npc.level()));
    }

    @Override
    public MapCodec<? extends TradeCondition> codec() {
        return ModTradeConditions.MOON_PHASE.get();
    }
}
