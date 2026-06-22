package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.entity.ModEntities;

public class CursedFlamesProjectile extends AbstractManaProjectile {
    private int penetrateCount = 0;

    public CursedFlamesProjectile(EntityType<CursedFlamesProjectile> entityType, Level level) {
        super(entityType, level);
        withParticle(Confluence.asResource("cursed_flames"));
    }

    public CursedFlamesProjectile(LivingEntity living) {
        this(ModEntities.CURSED_FLAMES.get(), living.level());
    }

    @Override
    public void baseTick() {
        if (!level().getFluidState(blockPosition()).isEmpty()) {
            discard();
            return;
        }
        super.baseTick();
        doBouncyMove(true, () -> doCollisionCheck(5), vec3 -> vec3.scale(0.99));
        doAgeCheck(1200);
    }

    @Override
    protected double getDefaultGravity() {
        return 0.04;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        if (entity instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(ModEffects.CURSED_INFERNO, 140));
        }
        doHurtAndKnockback(entity, 0.6, 0.2);
        if (this.penetrateCount++ >= 1) { // 击中就算一次
            discard();
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.penetrateCount = compound.getInt("PenetrateCount");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("PenetrateCount", penetrateCount);
    }
}
