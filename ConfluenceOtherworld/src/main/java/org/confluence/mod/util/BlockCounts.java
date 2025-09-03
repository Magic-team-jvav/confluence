package org.confluence.mod.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public final class BlockCounts {
    public final AtomicInteger crimson = new AtomicInteger();
    public final AtomicInteger crimsonSand = new AtomicInteger();
    public final AtomicInteger crimsonIce = new AtomicInteger();
    public final AtomicInteger corrupt = new AtomicInteger();
    public final AtomicInteger corruptSand = new AtomicInteger();
    public final AtomicInteger corruptIce = new AtomicInteger();
    public final AtomicInteger hallow = new AtomicInteger();
    public final AtomicInteger hallowSand = new AtomicInteger();
    public final AtomicInteger hallowIce = new AtomicInteger();
    public final AtomicInteger sunflower = new AtomicInteger();
    public final AtomicInteger tomb = new AtomicInteger();

    public boolean isGraveyard() {
        return tomb.get() - sunflower.get() >= 7;
    }

    public enum Type implements Function<BlockCounts, AtomicInteger> {
        CRIMSON(counter -> counter.crimson),
        CRIMSON_SAND(counter -> counter.crimsonSand),
        CRIMSON_ICE(counter -> counter.crimsonIce),
        CORRUPT(counter -> counter.corrupt),
        CORRUPT_SAND(counter -> counter.corruptSand),
        CORRUPT_ICE(counter -> counter.corruptIce),
        HALLOW(counter -> counter.hallow),
        HALLOW_SAND(counter -> counter.hallowSand),
        HALLOW_ICE(counter -> counter.hallowIce),
        SUNFLOWER(counter -> counter.sunflower),
        TOMB(counter -> counter.tomb);

        private final Function<BlockCounts, AtomicInteger> getter;

        Type(Function<BlockCounts, AtomicInteger> getter) {
            this.getter = getter;
        }

        @Override
        public AtomicInteger apply(BlockCounts counter) {
            return getter.apply(counter);
        }
    }
}
