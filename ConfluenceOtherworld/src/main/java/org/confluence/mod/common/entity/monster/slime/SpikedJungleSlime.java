package org.confluence.mod.common.entity.monster.slime;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;

/**
 * 尖刺丛林史莱姆使用更高的弹幕伤害，并可在远距离追击前偶尔补射单发尖刺。
 */
public class SpikedJungleSlime extends SpikedSlime {

    public SpikedJungleSlime(EntityType<? extends BaseSlime> type, Level level) {
        super(type, level, 0x9ae920, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createSlimeAttributes(15.0f, 8, 33.0f);
    }

    @Override
    protected float spikeDamage() {
        return 8.0f;
    }

    @Override
    protected boolean canFireDistantSingleSpike() {
        return true;
    }
}
