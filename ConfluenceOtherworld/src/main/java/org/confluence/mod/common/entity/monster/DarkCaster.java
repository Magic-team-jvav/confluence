package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class DarkCaster extends BaseCasterMonster {

    public DarkCaster(EntityType<? extends BaseCasterMonster> type, Level level) {
        super(type, level);
    }

    /// 为仅存在于 1.20 的法师变种保留既有战斗节奏。
    public DarkCaster(EntityType<? extends BaseCasterMonster> type, Level level, boolean legacyCycle) {
        super(type, level,
                legacyCycle ? CycleMode.LEGACY_1_20
                        : CycleMode.SHARED_1_21);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseCasterMonster.createCasterAttributes()
                .add(Attributes.ATTACK_DAMAGE, 16.0);
    }
}
