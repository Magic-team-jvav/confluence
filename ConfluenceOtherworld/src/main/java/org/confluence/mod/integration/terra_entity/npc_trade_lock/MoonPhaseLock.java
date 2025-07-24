package org.confluence.mod.integration.terra_entity.npc_trade_lock;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.common.data.saved.MoonPhase;
import org.confluence.mod.integration.terra_entity.init.ModTradeLockProviderTypes;
import org.confluence.terraentity.api.trade.ITradeHolder;
import org.confluence.terraentity.api.trade.ITradeLock;
import org.confluence.terraentity.registries.npc_trade_lock.TradeLockProvider;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public record MoonPhaseLock(List<MoonPhase> moonPhases) implements ITradeLock {
    public static final MapCodec<MoonPhaseLock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.either(ExtraCodecs.nonEmptyList(MoonPhase.CODEC.listOf()), MoonPhase.CODEC).xmap(
                    either -> either.map(Function.identity(), List::of),
                    moonPhases -> moonPhases.size() == 1 ? Either.right(moonPhases.getFirst()) : Either.left(moonPhases)
            ).fieldOf("moon_phases").forGetter(MoonPhaseLock::moonPhases)
    ).apply(instance, MoonPhaseLock::new));

    public MoonPhaseLock(MoonPhase... moonPhases) {
        this(Arrays.stream(moonPhases).toList());
    }

    @Override
    public boolean canTrade(Player player, ITradeHolder npc, int index) {
        int phase = player.level().getMoonPhase();
        return moonPhases.stream().anyMatch(moonPhase -> moonPhase.match(phase));
    }

    @Override
    public TradeLockProvider getCodec() {
        return ModTradeLockProviderTypes.MOON_PHASE_LOCK.get();
    }
}
