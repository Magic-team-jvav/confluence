package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.common.Tags;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.item.ManaWeaponItems;
import org.confluence.mod.common.item.mana.BaseDraggingStaffItem;
import org.confluence.mod.common.item.mana.FlamelashItem;

public class FlamelashProjectile extends BaseDraggingProjectile {
    public static final double RANGE = 6.0 * 2 / 3;
    public static final double KNOCKBACK = 0.65;

    private boolean penetrated;

    public FlamelashProjectile(EntityType<? extends FlamelashProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public FlamelashProjectile(LivingEntity living) {
        this(ModEntities.FLAMELASH_PROJECTILE.get(), living.level());
    }

    @Override
    protected BaseDraggingStaffItem<?> getDraggingStaff() {
        return ManaWeaponItems.FLAMELASH.get();
    }

    @Override
    protected int getCooldown() {
        return FlamelashItem.COOLDOWN;
    }

    @Override
    protected ResourceLocation getParticleId() {
        return Confluence.asResource("flamelash_projectile");
    }

    @Override
    public void baseTick() {
        super.baseTick();
        doFluidCheck(fluidState -> fluidState.is(Tags.Fluids.WATER) || fluidState.is(Tags.Fluids.HONEY));
        doAgeCheck(1200);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        doExplosion(RANGE, KNOCKBACK);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (level().isClientSide) return;
        if (penetrated) {
            doExplosion(RANGE, KNOCKBACK);
        } else {
            this.penetrated = true;
            doHurtAndKnockback(result.getEntity(), KNOCKBACK, 0.2);
        }
    }

    @Override
    protected boolean doHurtAndKnockback(Entity target, double knockbackStrength, double knockbackMotionY) {
        if (super.doHurtAndKnockback(target, knockbackStrength, knockbackMotionY)) {
            if (random.nextBoolean()) {
                target.igniteForTicks(Mth.randomBetweenInclusive(random, 80, 160));
            }
            return true;
        }
        return false;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Penetrated", penetrated);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.penetrated = compound.getBoolean("Penetrated");
    }
}
