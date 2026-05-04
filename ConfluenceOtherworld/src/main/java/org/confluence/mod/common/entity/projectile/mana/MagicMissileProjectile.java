package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibClientUtils;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.item.ManaWeaponItems;
import org.confluence.mod.common.item.mana.MagicMissileItem;
import org.confluence.terraentity.api.entity.ITrackType;
import org.confluence.terraentity.registries.track.variant.BasisTrack;
import org.confluence.terraentity.utils.TEUtils;

public class MagicMissileProjectile extends AbstractManaProjectile {
    public static final double RANGE = 8.0 * 2 / 3;

    private final ITrackType trackType = new BasisTrack(90, 0.4F);
    private boolean shot;

    public MagicMissileProjectile(EntityType<? extends MagicMissileProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public MagicMissileProjectile(LivingEntity living) {
        this(ModEntities.MAGIC_MISSILE_PROJECTILE.get(), living.level());
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
            if (owner.isUsingItem() && owner.getMainHandItem().is(ManaWeaponItems.MAGIC_MISSILE)) {
                Vec3 vector = owner.getViewVector(1);
                Vec3 position = position();
                setPos(owner.getX() + vector.x * 2, owner.getEyeY() - 0.1 + vector.y * 2, owner.getZ() + vector.z * 2);
                setDeltaMovement(position().subtract(position));
            } else {
                shootFromRotation(owner, owner.getXRot(), owner.getYRot(), 0.0F, getDefaultVelocity(), 0.0F);
                if (owner instanceof Player player) {
                    player.getCooldowns().addCooldown(ManaWeaponItems.MAGIC_MISSILE.get(), MagicMissileItem.COOLDOWN);
                }
                this.shot = true;
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        doExplosion();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        doExplosion();
    }

    private void doExplosion() {
        if (level().isClientSide) {
            for (int i = 0; i < 4; i++) {
                level().addParticle(ParticleTypes.EXPLOSION, getRandomX(2), getY(4 * random.nextDouble() - 2), getRandomZ(2), 0, 0, 0);
            }
            level().playSound(LibClientUtils.getPlayer(), blockPosition(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.VOICE);
        } else {
            for (LivingEntity living : level().getEntities(EntityTypeTest.forClass(LivingEntity.class), new AABB(blockPosition()).inflate(RANGE / 2), this::canHitEntity)) {
                doHurtAndKnockback(living, 0.75, 0.2);
            }
            discard();
        }
    }
}
