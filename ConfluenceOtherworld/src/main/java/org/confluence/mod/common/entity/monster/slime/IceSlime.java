package org.confluence.mod.common.entity.monster.slime;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import org.confluence.lib.util.LibUtils;

public class IceSlime extends BaseSlime {

    public IceSlime(EntityType<? extends BaseSlime> type, Level level) {
        super(type, level, 0xB3F0EA, true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createSlimeAttributes(5.0f, 4, 13.0f);
    }

    @Override
    protected void onAttackTarget(LivingEntity target) {
        if (LibUtils.isMaster(level(), blockPosition()) || (LibUtils.isAtLeastExpert(level(), blockPosition()) && random.nextBoolean())) {
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 0), this);
        }
    }
}
