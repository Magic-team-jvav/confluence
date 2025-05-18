package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.IgnoreThrowerExplosionDamageCalculator;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModEntities;

public class CursedFlamesProjectile extends AbstractManaProjectile {
    private int collideCount = 0;
    private int penetrateCount = 0;

    public CursedFlamesProjectile(EntityType<CursedFlamesProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public CursedFlamesProjectile(Player player) {
        super(ModEntities.CURSED_FLAMES_PROJECTILE.get(), player.level());
        setOwner(player);
        setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
    }

    @Override
    public void baseTick() {
        if (!level().getFluidState(blockPosition()).isEmpty()) {
            discard();
            return;
        }
        super.baseTick();

        Vec3 vec3 = getDeltaMovement();
        move(MoverType.SELF, vec3.add(0.0, -0.04, 0.0));
        Vec3 motion = getDeltaMovement();
        if (!vec3.equals(motion)) {
            if (motion.x != vec3.x) motion = new Vec3(-vec3.x, vec3.y, vec3.z);
            if (motion.y != vec3.y) motion = new Vec3(vec3.x, -vec3.y, vec3.z);
            if (motion.z != vec3.z) motion = new Vec3(vec3.x, vec3.y, -vec3.z);
            if (this.collideCount++ >= 5) {
                discard();
                return;
            }
        }
        setDeltaMovement(motion.scale(0.99).add(0.0, -0.04, 0.0));

        if (ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity) instanceof EntityHitResult entityHitResult) {
            Entity entity = entityHitResult.getEntity();
            if (entity instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(ModEffects.CURSED_INFERNO, 140));
            }
            if (entity.hurt(getDamagesource(), 11)) {
                VectorUtils.knockBackA2B(this, entity, 0.6, 0.2);
            }
            if (this.penetrateCount++ >= 1) {
                level().explode(this, getDamagesource(), new IgnoreThrowerExplosionDamageCalculator(2, getOwner()), position(), 1.5F, false, Level.ExplosionInteraction.MOB);
                discard();
            }
        }

        if (tickCount > 1200) discard();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.collideCount = compound.getInt("CollideCount");
        this.penetrateCount = compound.getInt("PenetrateCount");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("CollideCount", collideCount);
        compound.putInt("PenetrateCount", penetrateCount);
    }
}
