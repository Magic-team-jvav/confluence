package org.confluence.mod.common.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.init.ModDamageTypes;

public class DarkLanceEntity extends Projectile {
    public DarkLanceEntity(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

//    public DarkLanceEntity(LivingEntity owner) {
//        super(entityType, owner.level());
//        setOwner(owner);
//    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    public void tick() {
        if (!(getOwner() instanceof LivingEntity owner)) {
            discard();
            return;
        }
        super.tick();

        this.xo = owner.xo;
        this.yo = owner.yo;
        this.zo = owner.zo;
        setPos(owner.position());

        if (!level().isClientSide) {
            Vec3 viewVector = owner.getViewVector(1.0F);
            Vec3 startVec = position().add(viewVector.scale(-0.5));
            Vec3 endVec = position().add(viewVector.scale(tickCount / 30.0F));
            AABB boundingBox = getBoundingBox().inflate(0.3F);
            for (Entity victim : level().getEntities(this, boundingBox, entity -> entity instanceof LivingEntity)) {
                AABB aabb = victim.getBoundingBox().inflate(0.3);
                if (aabb.clip(startVec, endVec).isPresent()) {
                    victim.hurt(ModDamageTypes.of(level(), DamageTypes.STING), 6.8F);
                    VectorUtils.knockBackA2B(owner, victim, 0.5, 0.1);
                }
            }
        }

        if (tickCount > 15) discard();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.tickCount = compound.getInt("Age");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("Age", tickCount);
    }
}
