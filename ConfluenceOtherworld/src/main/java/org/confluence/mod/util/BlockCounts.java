package org.confluence.mod.util;

import java.util.concurrent.atomic.AtomicInteger;

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

    public boolean isEctoMist() {
        return tomb.get() - sunflower.get() >= 7;
    }
}
