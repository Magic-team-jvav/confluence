package org.confluence.terraentity.registries.npc_trade_lock.variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.confluence.terraentity.registries.npc_trade_lock.TradeLockProvider;
import org.confluence.terraentity.registries.npc_trade_lock.TradeLockProviderTypes;

import java.util.Optional;

/**
 * 时间锁
 * <p> 限定范围时间可以交易
 * @param from 起始时间
 * @param to 结束时间
 * @param reverse 是否翻转,默认为false,即时间区间内可以交易
 */
public record TimeLock(int from, int to, boolean reverse) implements ITradeLock {

    public static final MapCodec<TimeLock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("from").forGetter(TimeLock::from),
            Codec.INT.fieldOf("to").forGetter(TimeLock::to),
            Codec.BOOL.optionalFieldOf("exclude").forGetter(i-> Optional.of(i.reverse))
    ).apply(instance, (from, to, reverse)-> new TimeLock(from, to, reverse.orElse(false))));

    @Override
    public boolean canTrade(Player player, ITradeHolder npc, int index) {
        int dayTime = (int) (npc.level().dayTime() % 24000);
        if (from > to) { // 从前一天晚上到第二天凌晨会从23999跳到0
            return (dayTime >= from || dayTime <= to) != reverse;
        }
        return (dayTime >= from && dayTime <= to) != reverse;
    }

    @Override
    public TradeLockProvider getCodec() {
        return TradeLockProviderTypes.TIME_LOCK.get();
    }
}
