package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.projectile.DamageSettableProjectile;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.util.ModUtils;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractManaProjectile extends DamageSettableProjectile {
    protected boolean localVelocity = false;

    public AbstractManaProjectile(EntityType<? extends AbstractManaProjectile> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        if (getOwner() == null) {
            discard();
        } else {
            super.tick();
        }

        if (this.getClass() != BaseManaStaffProjectileEntity.class){
            HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            checkInsideBlocks();
            HitResult.Type hitresult$type = hitresult.getType();
            if (hitresult$type == HitResult.Type.BLOCK) {
                onHitBlock((BlockHitResult) hitresult);
            }
        }
    }

    public @Nullable LivingEntity getLivingOwner() {
        return getOwner() instanceof LivingEntity living ? living : null;
    }

    public DamageSource getDamagesource() {
        return ModDamageTypes.of(level(), ModDamageTypes.MAGICAL_PROJECTILE, this, getOwner());
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return ModUtils.canHitEntity(target, getOwner());
    }

    @Override
    public void shootFromRotation(Entity shooter, float x, float y, float z, float velocity, float inaccuracy) {
        float cos = Mth.cos(x * Mth.DEG_TO_RAD);
        float f = -Mth.sin(y * Mth.DEG_TO_RAD) * cos;
        float f1 = -Mth.sin((x + z) * Mth.DEG_TO_RAD);
        float f2 = Mth.cos(y * Mth.DEG_TO_RAD) * cos;
        shoot(f, f1, f2, velocity, inaccuracy);
        if (localVelocity) {
            Vec3 vec3 = shooter.getKnownMovement();
            setDeltaMovement(getDeltaMovement().add(vec3.x, shooter.onGround() ? 0.0 : vec3.y, vec3.z));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Age", tickCount);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.tickCount = compound.getInt("Age");
    }
}
