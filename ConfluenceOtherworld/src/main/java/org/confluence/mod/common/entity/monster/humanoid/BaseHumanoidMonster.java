package org.confluence.mod.common.entity.monster.humanoid;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.monster.BaseMonster;

public abstract class BaseHumanoidMonster extends BaseMonster {

    public BaseHumanoidMonster(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createHumanoidAttributes() {
        return BaseMonster.createMonsterAttributes()
                .add(Attributes.ATTACK_KNOCKBACK, 0.5);
    }
}
