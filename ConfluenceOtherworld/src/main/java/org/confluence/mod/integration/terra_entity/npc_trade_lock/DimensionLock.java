package org.confluence.mod.integration.terra_entity.npc_trade_lock;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.mod.integration.terra_entity.init.ModTradeLockProviderTypes;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.confluence.terraentity.registries.npc_trade_lock.TradeLockProvider;

public record DimensionLock(ResourceKey<Level> dimension) implements ITradeLock {
    public static final MapCodec<DimensionLock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(DimensionLock::dimension)
    ).apply(instance, DimensionLock::new));

    @Override
    public boolean canTrade(Player player, ITradeHolder npc, int index) {
        return npc.level().dimension() == dimension;
    }

    @Override
    public TradeLockProvider getCodec() {
        return ModTradeLockProviderTypes.DIMENSION_LOCK.get();
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof DimensionLock(ResourceKey<Level> dimension1) && dimension == dimension1);
    }

    @Override
    public int hashCode() {
        return dimension.location().hashCode();
    }
}
