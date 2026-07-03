package org.confluence.mod.common.entity.boss;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTRoot;

// todo boss
public class DeerClops extends BaseBoss {
    public DeerClops(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    @Override
    protected BTRoot createBT() {
        return null;
    }
}
