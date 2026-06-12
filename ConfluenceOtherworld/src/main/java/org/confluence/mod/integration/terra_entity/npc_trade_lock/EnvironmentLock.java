package org.confluence.mod.integration.terra_entity.npc_trade_lock;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;
import org.confluence.lib.common.recipe.EnvironmentLevelAccess;
import org.confluence.mod.integration.terra_entity.init.ModTradeLockProviderTypes;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.confluence.terraentity.registries.npc_trade_lock.TradeLockProvider;

import java.util.Objects;

public record EnvironmentLock(EnvironmentLevelAccess.Matcher environment) implements ITradeLock {
    public static final MapCodec<EnvironmentLock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            EnvironmentLevelAccess.Matcher.MAP_CODEC.forGetter(EnvironmentLock::environment)
    ).apply(instance, EnvironmentLock::new));

    @Override
    public boolean canTrade(Player player, ITradeHolder npc, int index) {
        return environment.matches(new EnvironmentLevelAccess(npc.level(), npc.blockPosition()));
    }

    @Override
    public TradeLockProvider getCodec() {
        return ModTradeLockProviderTypes.ENVIRONMENT_LOCK.get();
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof EnvironmentLock(
                EnvironmentLevelAccess.Matcher environment1
        ) && Objects.equals(environment, environment1));
    }

    @Override
    public int hashCode() {
        return environment.hashCode();
    }
}
