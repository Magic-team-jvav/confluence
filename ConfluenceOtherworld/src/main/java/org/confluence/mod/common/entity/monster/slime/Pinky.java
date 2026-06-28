package org.confluence.mod.common.entity.monster.slime;

import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;

/**
 * 粉史莱姆 —— 极小体积、稀有、高血量、掉落 Pink Gel。
 */
public class Pinky extends BaseSlime {
    private static final EntityDimensions TINY = EntityDimensions.scalable(0.3F, 0.3F);

    public Pinky(EntityType<? extends BaseSlime> type, Level level) {
        super(type, level, 0xFF87B3, false, -40);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createSlimeAttributes(1.0f, 2, 150.0f, -40);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return TINY;
    }
}
