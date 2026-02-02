package org.confluence.terraentity.registries.npc_trade_lock.variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.confluence.terraentity.registries.npc_trade_lock.TradeLockProvider;
import org.confluence.terraentity.registries.npc_trade_lock.TradeLockProviderTypes;

/**
 * 心情值锁
 * <p>仅当holder为npc有效
 * @param value 最小/最大心情值
 * @param less 默认为false。当为true时且mood小于value可交易
 */
public record MoodLock(int value, boolean less) implements ITradeLock {
    public static final MapCodec<MoodLock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("value").forGetter(MoodLock::value),
            Codec.BOOL.lenientOptionalFieldOf("less", false).forGetter(MoodLock::less)
    ).apply(instance, MoodLock::new));

    @Override
    public boolean canTrade(Player player, ITradeHolder npc, int index) {
        if (npc instanceof ITradeHolder holder) {
            int mood = holder.getMood().getValue();
            return (less ? mood <= value : mood >= value);
        }
        return false;
    }

    public static MoodLock greater(int value) {
        return new MoodLock(value, false);
    }

    public static MoodLock less(int value) {
        return new MoodLock(value, true);
    }

    @Override
    public TradeLockProvider getCodec() {
        return TradeLockProviderTypes.MOOD_LOCK.get();
    }
}
