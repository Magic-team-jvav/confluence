package org.confluence.mod.common.entity.monster.slime;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;

/**
 * 夜明史莱姆 —— 持续发光并产生残影粒子。
 */
public class LuminousSlime extends BaseSlime {

    public LuminousSlime(EntityType<? extends BaseSlime> type, Level level) {
        super(type, level, 0xFFFFFF, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createSlimeAttributes(36.4f, 30, 93.0f);
    }
}
