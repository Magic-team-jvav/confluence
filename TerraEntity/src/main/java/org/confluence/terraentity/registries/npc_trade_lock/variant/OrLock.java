package org.confluence.terraentity.registries.npc_trade_lock.variant;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.confluence.terraentity.registries.npc_trade_lock.TradeLockProvider;
import org.confluence.terraentity.registries.npc_trade_lock.TradeLockProviderTypes;

import java.util.List;

/**
 * 或锁
 * @param locks
 *
 */
public record OrLock(List<ITradeLock> locks) implements ITradeLock {

    public static final MapCodec<OrLock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ITradeLock.TYPED_CODEC.listOf().fieldOf("locks").forGetter(OrLock::locks)
    ).apply(instance, OrLock::new));

    @Override
    public boolean canTrade(Player player, ITradeHolder npc, int index) {
        return locks.stream().anyMatch(lock -> lock.canTrade(player, npc, index));
    }

    @Override
    public TradeLockProvider getCodec() {
        return TradeLockProviderTypes.OR_LOCK.get();
    }
}
