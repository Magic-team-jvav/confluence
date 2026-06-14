package org.confluence.mod.integration.terra_entity.npc_trade_lock;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.client.handler.bestiary.ClientBestiary;
import org.confluence.mod.common.data.saved.Bestiary;
import org.confluence.mod.integration.terra_entity.init.ModTradeLockProviderTypes;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.confluence.terraentity.registries.npc_trade_lock.TradeLockProvider;

public record BestiaryUnlockedCountLock(int count) implements ITradeLock {
    public static final MapCodec<BestiaryUnlockedCountLock> CODEC = ExtraCodecs.POSITIVE_INT.fieldOf("count").xmap(BestiaryUnlockedCountLock::new, BestiaryUnlockedCountLock::count);

    @Override
    public boolean canTrade(Player player, ITradeHolder npc, int index) {
        if (player.isLocalPlayer()) {
            return ClientBestiary.getInstance().getUnlockedCount() >= count;
        }
        return Bestiary.INSTANCE.getUnlockedCount() >= count;
    }

    @Override
    public TradeLockProvider getCodec() {
        return ModTradeLockProviderTypes.BESTIARY_UNLOCKED_COUNT_LOCK.get();
    }

    @Override
    public String toString() {
        return "BestiaryUnlockedCountLock{" +
                "count=" + count +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof BestiaryUnlockedCountLock that && that.count() == count);
    }

    @Override
    public int hashCode() {
        return count;
    }
}
