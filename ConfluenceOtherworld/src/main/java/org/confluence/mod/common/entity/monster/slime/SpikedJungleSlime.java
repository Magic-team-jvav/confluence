package org.confluence.mod.common.entity.monster.slime;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.projectile.SlimeSpikeEntity;

/// 尖刺丛林史莱姆 —— 战斗时发射 8 方向尖刺，游荡时也发射单发尖刺。
public class SpikedJungleSlime extends SpikedSlime {

    public SpikedJungleSlime(EntityType<? extends BaseSlime> type, Level level) {
        super(type, level, 0x9ae920, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createSlimeAttributes(15.0f, 8, 33.0f);
    }

    @Override
    protected SlimeSpikeEntity.Variant spikeVariant() {
        return SlimeSpikeEntity.Variant.JUNGLE;
    }

    @Override
    protected boolean canFireDistantSingleSpike() {
        return true;
    }
}
