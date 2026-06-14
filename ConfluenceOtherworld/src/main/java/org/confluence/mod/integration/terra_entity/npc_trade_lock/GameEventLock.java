package org.confluence.mod.integration.terra_entity.npc_trade_lock;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.common.gameevent.GameEvent;
import org.confluence.mod.common.gameevent.GameEventSystem;
import org.confluence.mod.integration.terra_entity.init.ModTradeLockProviderTypes;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.confluence.terraentity.registries.npc_trade_lock.TradeLockProvider;

public record GameEventLock(ResourceKey<? extends GameEvent> key) implements ITradeLock {
    public static final MapCodec<GameEventLock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            GameEvent.KEY_CODEC.fieldOf("key").forGetter(GameEventLock::key)
    ).apply(instance, GameEventLock::new));

    @Override
    public boolean canTrade(Player player, ITradeHolder npc, int index) {
        return GameEventSystem.INSTANCE.isEventStarted(key);
    }

    @Override
    public TradeLockProvider getCodec() {
        return ModTradeLockProviderTypes.GAME_EVENT_LOCK.get();
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof GameEventLock that && key == that.key());
    }

    @Override
    public int hashCode() {
        return key.location().hashCode();
    }
}
