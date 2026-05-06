package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibClientUtils;
import org.confluence.mod.common.item.mana.BaseDraggingStaffItem;
import org.confluence.terraentity.api.entity.ITrackType;
import org.confluence.terraentity.registries.track.variant.BasisTrack;
import org.confluence.terraentity.utils.TEUtils;
import org.mesdag.particlestorm.PSGameClient;
import org.mesdag.particlestorm.particle.ParticleEmitter;

public abstract class BaseDraggingProjectile extends AbstractManaProjectile {
    protected boolean shot;
    protected ITrackType trackType = new BasisTrack(90, 0.4F);
    protected ParticleEmitter emitter;

    public BaseDraggingProjectile(EntityType<? extends BaseDraggingProjectile> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void baseTick() {
        super.baseTick();

        LivingEntity owner = getLivingOwner();
        if (owner == null) return;

        if (shot) {
            doTracking();
        } else {
            dragOrShoot(owner);
        }

        createParticleEmitter();
    }

    protected abstract int getCooldown();

    protected abstract BaseDraggingStaffItem<?> getDraggingStaff();

    protected abstract ResourceLocation getParticleId();

    protected void doTracking() {
        LivingEntity target = TEUtils.getAABBAngleTarget(position(), position().add(getDeltaMovement().normalize().scale(10)), level(), this, getTrackingRange(), 30, this::canHitEntity);
        if (target != null) {
            Vec3 motion = getDeltaMovement();
            Vec3 dir = target.position().add(0, target.getEyeHeight() * 0.5f, 0).subtract(position());
            double angle = TEUtils.angleBetween(motion, dir);
            Vec3 movement = trackType.calDeltaMovement(getDeltaMovement(), dir, angle);
            setDeltaMovement(movement);
        }

        doSimpleMove();
    }

    protected int getTrackingRange() {
        return 50 * 2 / 3;
    }

    protected void createParticleEmitter() {
        if (level().isClientSide && (emitter == null || emitter.isRemoved())) {
            this.emitter = new ParticleEmitter(level(), position(), getParticleId());
            emitter.attachEntity(this);
            emitter.offsetPos = new Vec3(0, getBbHeight() / 2, 0);
            PSGameClient.LOADER.addEmitter(emitter, false);
        }
    }

    protected void dragOrShoot(LivingEntity owner) {
        if (owner.isUsingItem() && owner.getMainHandItem().is(getDraggingStaff())) {
            Vec3 vector = owner.getViewVector(1);
            Vec3 position = position();
            setPos(owner.getX() + vector.x * 2, owner.getEyeY() - 0.1 + vector.y * 2, owner.getZ() + vector.z * 2);
            setDeltaMovement(position().subtract(position));
        } else {
            shootFromRotation(owner, owner.getXRot(), owner.getYRot(), 0.0F, getDefaultVelocity(), 0.0F);
            if (owner instanceof Player player) {
                player.getCooldowns().addCooldown(getDraggingStaff(), getCooldown());
            }
            this.shot = true;
        }
    }

    protected void doExplosion(double range) {
        if (level().isClientSide) {
            level().playSound(LibClientUtils.getPlayer(), blockPosition(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.VOICE);
        } else {
            for (LivingEntity living : level().getEntities(EntityTypeTest.forClass(LivingEntity.class), new AABB(blockPosition()).inflate(range / 2), this::canHitEntity)) {
                doHurtAndKnockback(living, 0.75, 0.2);
            }
            discard();
        }
    }
}
