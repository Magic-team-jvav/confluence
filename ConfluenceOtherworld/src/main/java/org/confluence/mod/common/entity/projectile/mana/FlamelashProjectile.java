package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.item.ManaWeaponItems;
import org.confluence.mod.common.item.mana.FlamelashItem;
import org.confluence.terraentity.api.entity.ITrackType;
import org.confluence.terraentity.registries.track.variant.BasisTrack;
import org.confluence.terraentity.utils.TEUtils;

public class FlamelashProjectile extends AbstractManaProjectile {
    public static final double RANGE = 6.0 * 2 / 3;

    private final ITrackType trackType = new BasisTrack(90, 0.4F);
    private boolean shot;

    public FlamelashProjectile(EntityType<? extends FlamelashProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public FlamelashProjectile(LivingEntity living) {
        this(ModEntities.FLAMELASH_PROJECTILE.get(), living.level());
    }

    @Override
    public void baseTick() {
        super.baseTick();
        LivingEntity owner = getLivingOwner();
        if (owner == null) return;

        if (shot) {
            LivingEntity target = TEUtils.getAABBAngleTarget(position(), position().add(getDeltaMovement().normalize().scale(10)), level(), this, 20, 30, this::canHitEntity);
            if (target != null) {
                Vec3 motion = getDeltaMovement();
                Vec3 dir = target.position().add(0, target.getEyeHeight() * 0.5f, 0).subtract(position());
                double angle = TEUtils.angleBetween(motion, dir);
                Vec3 movement = trackType.calDeltaMovement(getDeltaMovement(), dir, angle);
                setDeltaMovement(movement);
            }

            doSimpleMove();
        } else {
            if (owner.isUsingItem() && owner.getMainHandItem().is(ManaWeaponItems.FLAMELASH)) {
                Vec3 vector = owner.getViewVector(1);
                Vec3 position = position();
                setPos(owner.getX() + vector.x * 2, owner.getEyeY() - 0.1 + vector.y * 2, owner.getZ() + vector.z * 2);
                setDeltaMovement(position().subtract(position));
            } else {
                shootFromRotation(owner, owner.getXRot(), owner.getYRot(), 0.0F, getDefaultVelocity(), 0.0F);
                if (owner instanceof Player player) {
                    player.getCooldowns().addCooldown(ManaWeaponItems.FLAMELASH.get(), FlamelashItem.COOLDOWN);
                }
                this.shot = true;
            }
        }

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
            doExplosion();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (level().isClientSide) return;

        Entity entity = result.getEntity();
        if (doPenetrateCheck(entity)) {
            if (getPenetrateSet().size() < 2) {
                doHurtEntity(entity);
            } else {
                doExplosion();
            }
        }
    }

    private void doExplosion() {
        ((ServerLevel) level()).sendParticles(ParticleTypes.EXPLOSION, getX(), getY(), getZ(), 4, 1, 1, 1, 0);
        level().playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.VOICE);
        for (LivingEntity living : level().getEntities(EntityTypeTest.forClass(LivingEntity.class), new AABB(blockPosition()).inflate(RANGE / 2), this::canHitEntity)) {
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
