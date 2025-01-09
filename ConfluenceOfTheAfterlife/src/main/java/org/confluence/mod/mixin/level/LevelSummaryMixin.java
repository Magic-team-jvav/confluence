package org.confluence.mod.mixin.level;

import net.minecraft.world.level.storage.LevelSummary;
import org.confluence.mod.mixed.ILevelSummary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LevelSummary.class)
public abstract class LevelSummaryMixin implements ILevelSummary {
    @Unique
    private long confluence$secretFlag = 0L;

    @Override
    public void confluence$setSecretFlag(long flag) {
        this.confluence$secretFlag = flag;
    }

    @Override
    public long confluence$getSecretFlag() {
        return confluence$secretFlag;
    }
}
