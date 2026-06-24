package org.confluence.mod.common.entity.ai.goal.behavior.leaf;

import net.minecraft.util.RandomSource;

/// 随机时长的WaitAction
public class RandomWaitAction extends WaitAction {
    private final Runnable waitTicksApplier;

    public RandomWaitAction(int minInclusive, int maxInclusive, RandomSource random) {
        super(minInclusive);
        if (maxInclusive <= minInclusive) {
            throw new IllegalArgumentException("max must greater than min");
        }
        this.waitTicksApplier = () -> this.waitTicks = random.nextIntBetweenInclusive(minInclusive, maxInclusive);
        waitTicksApplier.run();
    }

    @Override
    protected void cleanup() {
        super.cleanup();
        waitTicksApplier.run();
    }
}
