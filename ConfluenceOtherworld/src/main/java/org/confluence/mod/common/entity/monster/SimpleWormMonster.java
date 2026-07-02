package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;

/**
 * 简单蠕虫怪物——只需指定体节数。
 */
public class SimpleWormMonster extends BaseWormMonster {
    private final int segments;

    public SimpleWormMonster(EntityType<? extends SimpleWormMonster> type, Level level, int segments) {
        super(type, level);
        this.segments = segments;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseWormMonster.createWormAttributes();
    }

    @Override
    protected int getSegmentCount() {
        return segments;
    }
}
