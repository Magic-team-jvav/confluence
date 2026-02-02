package org.confluence.mod.integration.terra_entity.npc_trade_lock;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.integration.terra_entity.init.ModTradeLockProviderTypes;
import org.confluence.mod.mixed.IMinecraftServer;
import org.confluence.mod.mixed.IWorldOptions;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.confluence.terraentity.registries.npc_trade_lock.TradeLockProvider;

/// check when about to call [org.confluence.mod.mixin.integration.terra_entity.DynamicAnglerTradeTaskMixin]
public record QuestedFishPrecheckLock(boolean isCorruption, ITradeLock then) implements ITradeLock {
    public static final MapCodec<QuestedFishPrecheckLock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.fieldOf("is_corruption").forGetter(QuestedFishPrecheckLock::isCorruption),
            ITradeLock.TYPED_CODEC.lenientOptionalFieldOf("then", ITradeLock.alwaysTrue()).forGetter(QuestedFishPrecheckLock::then)
    ).apply(instance, QuestedFishPrecheckLock::new));

    public QuestedFishPrecheckLock(boolean isCorruption) {
        this(isCorruption, ITradeLock.alwaysTrue());
    }

    public boolean canSetQuestedFish(Level level) {
        if (level.isClientSide) {
            return IMinecraftServer.matchesSecretFlag(ClientPacketHandler.getSecretFlag(), IWorldOptions.THE_CORRUPTION) == isCorruption;
        }
        return IMinecraftServer.matchesSecretFlag(IWorldOptions.THE_CORRUPTION) == isCorruption;
    }

    @Override
    public boolean canTrade(Player player, ITradeHolder npc, int index) {
        return then.canTrade(player, npc, index);
    }

    @Override
    public TradeLockProvider getCodec() {
        return ModTradeLockProviderTypes.QUESTED_FISH_PRECHECK_LOCK.get();
    }
}
