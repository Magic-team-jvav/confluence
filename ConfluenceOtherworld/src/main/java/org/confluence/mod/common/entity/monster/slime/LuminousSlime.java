package org.confluence.mod.common.entity.monster.slime;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;

/**
 * 夜明史莱姆。其行为与 1.21 侧普通白色史莱姆一致。
 */
public class LuminousSlime extends BaseSlime {

    public LuminousSlime(EntityType<? extends BaseSlime> type, Level level) {
        super(type, level, 0xFFFFFF, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createSlimeAttributes(36.4f, 30, 93.0f);
    }
}
