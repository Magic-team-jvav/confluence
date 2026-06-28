package org.confluence.mod.common.entity.monster.slime;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;

public class LavaSlime extends BaseSlime {

    public LavaSlime(EntityType<? extends BaseSlime> type, Level level) {
        super(type, level, 0xFFB150, false, 50);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createSlimeAttributes(10.0f, 10, 30.0f, 50);
    }

    @Override
    protected boolean isFireImmune() {
        return true;
    }

    @Override
    protected boolean hurtByWater() {
        return true;
    }
}
