package org.confluence.mod.common.entity.monster.slime;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.projectile.SlimeSpikeEntity;

/// 尖刺冰雪史莱姆 —— 远距离发射带霜冻效果的冰刺。
public class SpikedIceSlime extends SpikedSlime {

    public SpikedIceSlime(EntityType<? extends BaseSlime> type, Level level) {
        super(type, level, 0xB3F0EA, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createSlimeAttributes(6.0f, 8, 31.0f);
    }

    @Override
    protected SlimeSpikeEntity.Variant spikeVariant() {
        return SlimeSpikeEntity.Variant.ICE;
    }

    @Override
    protected boolean canFireDistantSingleSpike() {
        return true;
    }

}
