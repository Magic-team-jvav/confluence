package org.confluence.mod.integration.terra_entity.npc_trade_lock;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.integration.terra_entity.init.ModTradeLockProviderTypes;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.confluence.terraentity.registries.npc_trade_lock.TradeLockProvider;

public final class AnyBossDefeatedLock implements ITradeLock {
    public static final AnyBossDefeatedLock INSTANCE = new AnyBossDefeatedLock();
    public static final MapCodec<AnyBossDefeatedLock> CODEC = MapCodec.unit(INSTANCE);

    public AnyBossDefeatedLock() {}

    @Override
    public boolean canTrade(Player player, ITradeHolder npc, int index) {
        return !KillBoard.INSTANCE.getDefeatedBosses().isEmpty();
    }

    @Override
    public TradeLockProvider getCodec() {
        return ModTradeLockProviderTypes.ANY_BOSS_DEFEATED_LOCK.get();
    }
}
