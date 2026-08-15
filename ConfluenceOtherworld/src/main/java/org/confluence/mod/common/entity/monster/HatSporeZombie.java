package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class HatSporeZombie extends SporeZombie {
    public HatSporeZombie(EntityType<? extends HatSporeZombie> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 114.0)
                .add(Attributes.ATTACK_DAMAGE, 19.0)
                .add(Attributes.ARMOR, 16.0)
                .add(Attributes.MOVEMENT_SPEED, 0.08)
                .add(Attributes.FOLLOW_RANGE, 60.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.72);
    }
}
