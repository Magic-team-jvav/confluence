package org.confluence.mod.common.entity.monster.slime;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;

/**
 * 猩红史莱姆保持 1.21 侧实际生效的二号体型，并在成功接触攻击时有概率施加黑暗。
 */
public class Crimslime extends BaseSlime {

    public Crimslime(EntityType<? extends BaseSlime> type, Level level) {
        super(type, level, 0x8B4949, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createSlimeAttributes(31.2f, 26, 104.0f);
    }

    @Override
    protected void onAttackTarget(LivingEntity target) {
        if (target.getRandom().nextFloat() <= 0.25F) {
            target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 300), this);
        }
    }
}
