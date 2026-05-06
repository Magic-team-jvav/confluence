package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.common.Tags;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.item.ManaWeaponItems;
import org.confluence.mod.common.item.mana.BaseDraggingStaffItem;
import org.confluence.mod.common.item.mana.FlamelashItem;

public class FlamelashProjectile extends BaseDraggingProjectile {
    public static final double RANGE = 6.0 * 2 / 3;

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

        if (!level().isClientSide) {
            if (tickCount > 1200) {
                discard();
            } else {
                FluidState fluidState = level().getFluidState(blockPosition());
                if (fluidState.is(Tags.Fluids.WATER) || fluidState.is(Tags.Fluids.HONEY)) {
                    discard();
                }
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!level().isClientSide) {
            doExplosion(RANGE);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (level().isClientSide) return;

        Entity entity = result.getEntity();
        if (doPenetrateCheck(entity)) { // todo 如果有足够大的空间，其射弹可以击中同一目标两次：当其穿透敌怪后转一圈可再次击中敌怪。
            if (getPenetrateSet().size() < 2) {
                doHurtEntity(entity);
            } else {
                doExplosion(RANGE);
            }
        }
    }

    protected void doExplosion(double range) {
        level().playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.VOICE);
        for (LivingEntity living : level().getEntities(EntityTypeTest.forClass(LivingEntity.class), new AABB(blockPosition()).inflate(range / 2), this::canHitEntity)) {
            doHurtEntity(living);
        }
        discard();
    }

    private void doHurtEntity(Entity entity) {
        if (doHurtAndKnockback(entity, 0.65, 0.2) && random.nextBoolean()) {
            entity.igniteForTicks(Mth.randomBetweenInclusive(random, 80, 160));
        }
    }
}
