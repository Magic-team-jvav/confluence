package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.entity.ModEntities;

// todo 专家模式下，黄金雨对毁灭者及其探测怪仅造成 75% 伤害。
public class GoldenShowerProjectile extends AbstractManaProjectile {
    public GoldenShowerProjectile(EntityType<GoldenShowerProjectile> entityType, Level level) {
        super(entityType, level);
        withParticle(Confluence.asResource("golden_shower"));
    }

    public GoldenShowerProjectile(LivingEntity living) {
        this(ModEntities.GOLDEN_SHOWER.get(), living.level());
    }

    @Override
    public void baseTick() {
        super.baseTick();
        setDeltaMovement(doSimpleMove().add(0.0, -0.24, 0.0));
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!level().isClientSide) {
            discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        if (doPenetrateCheck(entity)) {
            if (doHurtAndKnockback(entity, 3.5, 0.2) && entity instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(ModEffects.ICHOR, 200));
            }
            doDiscardInMaxPenetrate(4);
        }
    }
}
