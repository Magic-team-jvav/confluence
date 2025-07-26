package org.confluence.mod.integration.terra_entity.npc_trade_lock;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.integration.terra_entity.init.ModTradeLockProviderTypes;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.confluence.terraentity.registries.npc_trade_lock.TradeLockProvider;

public record PositionLock(MinMaxBounds.Ints x, MinMaxBounds.Ints y, MinMaxBounds.Ints z) implements ITradeLock {
    public static final MapCodec<PositionLock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            MinMaxBounds.Ints.CODEC.optionalFieldOf("x", MinMaxBounds.Ints.ANY).forGetter(PositionLock::x),
            MinMaxBounds.Ints.CODEC.optionalFieldOf("y", MinMaxBounds.Ints.ANY).forGetter(PositionLock::y),
            MinMaxBounds.Ints.CODEC.optionalFieldOf("z", MinMaxBounds.Ints.ANY).forGetter(PositionLock::z)
    ).apply(instance, PositionLock::new));

    @Override
    public boolean canTrade(Player player, ITradeHolder npc, int index) {
        BlockPos pos = npc.blockPosition();
        return x.matches(pos.getX()) && y.matches(pos.getY()) && z.matches(pos.getZ());
    }

    @Override
    public TradeLockProvider getCodec() {
        return ModTradeLockProviderTypes.POSITION_LOCK.get();
    }
}
