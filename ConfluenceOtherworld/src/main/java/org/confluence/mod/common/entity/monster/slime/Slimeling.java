package org.confluence.mod.common.entity.monster.slime;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;

/**
 * 小史莱姆灵 —— 腐化史莱姆死亡时分裂出的碎片。
 */
public class Slimeling extends BaseSlime {

    public Slimeling(EntityType<? extends BaseSlime> type, Level level) {
        super(type, level, 0xC91717, false, 0);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createSlimeAttributes(7.0f, 2, 45.0f, 0);
    }
}
