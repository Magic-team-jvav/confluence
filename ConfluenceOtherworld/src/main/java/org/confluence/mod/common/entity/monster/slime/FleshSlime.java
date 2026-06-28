package org.confluence.mod.common.entity.monster.slime;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;

/**
 * 血肉史莱姆 —— 免疫火焰/熔岩/摔伤，不攻击血肉同盟生物。
 */
public class FleshSlime extends BaseSlime {

    public FleshSlime(EntityType<? extends BaseSlime> type, Level level) {
        super(type, level, 0xFF0000, false, 30);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createSlimeAttributes(14.0f, 6, 50.0f, 30);
    }

    @Override
    protected boolean isFireImmune() {
        return true;
    }

    @Override
    protected boolean ignoreDrowning() {
        return true;
    }

    @Override
    protected boolean hurtByWater() {
        return false;
    }
}
