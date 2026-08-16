package org.confluence.mod.common.entity.monster.slime;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;

/// 粉史莱姆 —— 极小体积、稀有、高血量、掉落 Pink Gel。
public class Pinky extends BaseSlime {
    public Pinky(EntityType<? extends BaseSlime> type, Level level) {
        super(type, level, 0xFF87B3, true, 1);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createSlimeAttributes(2.0f, 2, 97.0f);
    }

}
