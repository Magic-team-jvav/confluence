package org.confluence.terraentity.registries.npc_trade_lock.variant;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.confluence.terraentity.registries.npc_trade_lock.TradeLockProvider;
import org.confluence.terraentity.registries.npc_trade_lock.TradeLockProviderTypes;

/**
 * 或锁
 * @param lock
 *
 */
public record NotLock(ITradeLock lock) implements ITradeLock {

    public static final MapCodec<NotLock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ITradeLock.TYPED_CODEC.fieldOf("locks").forGetter(NotLock::lock)
    ).apply(instance, NotLock::new));

    @Override
    public boolean canTrade(Player player, ITradeHolder npc, int index) {
        return !lock.canTrade(player, npc, index);
    }

    @Override
    public TradeLockProvider getCodec() {
        return TradeLockProviderTypes.NOT_LOCK.get();
    }
}
