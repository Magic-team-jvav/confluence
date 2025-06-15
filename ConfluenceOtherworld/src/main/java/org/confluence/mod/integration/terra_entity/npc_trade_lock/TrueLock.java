package org.confluence.mod.integration.terra_entity.npc_trade_lock;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.integration.terra_entity.init.ModTradeLockProviderTypes;
import org.confluence.terraentity.entity.npc.trade.ITradeHolder;
import org.confluence.terraentity.registries.npc_trade_lock.ITradeLock;
import org.confluence.terraentity.registries.npc_trade_lock.TradeLockProvider;

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
        return ModTradeLockProviderTypes.TRUE_LOCK.get();
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj == INSTANCE || (obj != null && obj.getClass() == TrueLock.class);
    }
}
