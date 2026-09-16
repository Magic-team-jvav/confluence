package org.confluence.mod.common.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.init.item.ConsumableItems;

public class CyborgExplosiveProjectile extends NPCWeaponProjectile {
    private static final EntityDataAccessor<Integer> MODE = SynchedEntityData.defineId(CyborgExplosiveProjectile.class, EntityDataSerializers.INT);
    private boolean settled;

    public CyborgExplosiveProjectile(EntityType<? extends CyborgExplosiveProjectile> type, Level level) {
        super(type, level);
    }

    public CyborgExplosiveProjectile(BaseNPC owner, float damage, int mode) {
        super(ModEntities.CYBORG_EXPLOSIVE.get(), owner,
                mode == 0 ? new ItemStack(Items.FIREWORK_ROCKET) : ConsumableItems.GRENADE.toStack(),
                damage, NPCProjectileEffects.EXPLOSIVE);
        entityData.set(MODE, mode);
        setNoGravity(mode == 0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(MODE, 0);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        int mode = entityData.get(MODE);
        if (mode == 0) {
            super.onHitBlock(result);
            return;
        }
        Vec3 normal = Vec3.atLowerCornerOf(result.getDirection().getNormal());
        setPos(result.getLocation().add(normal.scale(0.02)));
        if (mode == 2) {
            settled = true;
            setNoGravity(true);
            setDeltaMovement(Vec3.ZERO);
        } else {
            Vec3 velocity = getDeltaMovement();
            setDeltaMovement(velocity.subtract(normal.scale(2 * velocity.dot(normal))).scale(0.6));
        }
        hasImpulse = true;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide || isRemoved()) return;
        int mode = entityData.get(MODE);
        if (mode == 1 && tickCount >= 60) {
            createContext().areaDamage(2.5);
            discard();
        } else if (mode == 2 && settled && getOwner() instanceof BaseNPC owner) {
            for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(2))) {
                if (owner.canAttack(target)) {
                    createContext().areaDamage(2.5);
                    discard();
                    break;
                }
            }
        }
    }

    @Override
    protected NPCProjectileEffects.Context createContext() {
        NPCProjectileEffects.Context context = super.createContext();
        return entityData.get(MODE) == 2 ? new NPCProjectileEffects.Context(this, context.owner(), context.damage() * 3) : context;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Mode", entityData.get(MODE));
        tag.putBoolean("Settled", settled);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(MODE, tag.getInt("Mode"));
        settled = tag.getBoolean("Settled");
    }
}
