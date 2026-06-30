package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class DarkCaster extends BaseCasterMonster {

    public DarkCaster(EntityType<? extends BaseCasterMonster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseCasterMonster.createCasterAttributes()
                .add(Attributes.ATTACK_DAMAGE, 16.0);
    }
}
