package org.confluence.mod.integration.terra_entity.npc_trade_lock;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.api.SecretFlagMatcher;
import org.confluence.mod.integration.terra_entity.init.ModTradeLockProviderTypes;
import org.confluence.terraentity.entity.npc.trade.ITradeHolder;
import org.confluence.terraentity.registries.npc_trade_lock.ITradeLock;
import org.confluence.terraentity.registries.npc_trade_lock.TradeLockProvider;

public record SecretFlagLock(long secretFlag, boolean flipMatch) implements ITradeLock, SecretFlagMatcher {
    public static final MapCodec<SecretFlagLock> CODEC = SecretFlagMatcher.createMapCodec(SecretFlagLock::new);

    @Override
    public boolean canTrade(Player player, ITradeHolder npc, int index) {
        return matchesSecretFlag();
    }

    @Override
    public TradeLockProvider getCodec() {
        return ModTradeLockProviderTypes.SECRET_FLAG_LOCK.get();
    }
}
