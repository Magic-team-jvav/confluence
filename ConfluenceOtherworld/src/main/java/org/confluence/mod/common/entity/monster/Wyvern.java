package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;

public class Wyvern extends BaseWormMonster {

    public Wyvern(EntityType<? extends BaseWormMonster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseWormMonster.createWormAttributes();
    }

    @Override
    protected int getSegmentCount() {
        return 8;
    }
}
