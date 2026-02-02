package org.confluence.terraentity.registries.npc_trade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.confluence.terraentity.api.npc.trade.ITradeLock;

public record TradeProperties(ITradeLock lock) {
    public static final Codec<TradeProperties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ITradeLock.TYPED_CODEC.lenientOptionalFieldOf("lock", ITradeLock.alwaysTrue()).forGetter(TradeProperties::lock)
    ).apply(instance, TradeProperties::new));

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ITradeLock lock;

        public Builder setLock(ITradeLock lock) {
            this.lock = lock;
            return this;
        }

        public TradeProperties build() {
            return new TradeProperties(lock);
        }
    }
}
