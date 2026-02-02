package org.confluence.terraentity.registries.npc_trade_lock.variant;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.player.Player;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.confluence.terraentity.registries.npc_trade_lock.TradeLockProvider;
import org.confluence.terraentity.registries.npc_trade_lock.TradeLockProviderTypes;

public final class TrueLock implements ITradeLock {
    public static final TrueLock INSTANCE = new TrueLock();
    public static final MapCodec<TrueLock> CODEC = MapCodec.unit(INSTANCE);

    private TrueLock() {}

    @Override
    public boolean canTrade(Player player, ITradeHolder npc, int index) {
        return true;
    }

    @Override
    public TradeLockProvider getCodec() {
        return TradeLockProviderTypes.TRUE_LOCK.get();
    }
}
