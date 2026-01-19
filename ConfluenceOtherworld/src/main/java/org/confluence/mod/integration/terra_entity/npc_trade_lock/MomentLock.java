package org.confluence.mod.integration.terra_entity.npc_trade_lock;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.xiaohunao.heaven_destiny_moment.common.init.HDMRegistries;
import com.xiaohunao.heaven_destiny_moment.common.moment.IMoment;
import com.xiaohunao.heaven_destiny_moment.common.moment.MomentInstanceManager;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.integration.terra_entity.init.ModTradeLockProviderTypes;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.confluence.terraentity.registries.npc_trade_lock.TradeLockProvider;
import org.jetbrains.annotations.ApiStatus;

@Deprecated
@ApiStatus.ScheduledForRemoval(inVersion = "1.2.0")
public record MomentLock(ResourceKey<IMoment> moment) implements ITradeLock {
    public static final MapCodec<MomentLock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceKey.codec(HDMRegistries.Keys.MOMENT).fieldOf("moment").forGetter(MomentLock::moment)
    ).apply(instance, MomentLock::new));

    @Override
    public boolean canTrade(Player player, ITradeHolder npc, int index) {
        return MomentInstanceManager.of(npc.level()).hasMoment(moment);
    }

    @Override
    public TradeLockProvider getCodec() {
        return ModTradeLockProviderTypes.MOMENT_LOCK.get();
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof MomentLock(ResourceKey<IMoment> moment1) && moment == moment1);
    }

    @Override
    public int hashCode() {
        return moment.location().hashCode();
    }
}
