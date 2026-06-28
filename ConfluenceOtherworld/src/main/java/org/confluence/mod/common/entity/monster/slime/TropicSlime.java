package org.confluence.mod.common.entity.monster.slime;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;

public class TropicSlime extends BaseSlime {

    public TropicSlime(EntityType<? extends BaseSlime> type, Level level) {
        super(type, level, 0x73bcf4, true, 10);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createSlimeAttributes(5.0f, 1, 13.0f, 10);
    }

    @Override
    protected boolean ignoreDrowning() {
        return true;
    }
}
