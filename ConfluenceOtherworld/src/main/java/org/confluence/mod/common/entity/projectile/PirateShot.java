package org.confluence.mod.common.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibUtils;

public class PirateShot extends StraightMonsterProjectile implements ItemSupplier {
    private final Kind kind;

    public PirateShot(EntityType<? extends PirateShot> type, Level level, Kind kind) {
        super(type, level);
        this.kind = kind;
    }

    @Override
    protected Vec3 modifyVelocity(Vec3 velocity) {
        return kind == Kind.BULLET ? velocity : velocity.add(0, -0.025, 0);
    }

    @Override
    protected void onSuccessfulHit(Mob owner, LivingEntity target) {
        if (kind == Kind.ARROW && random.nextInt(3) == 0) {
            target.setSecondsOnFire(LibUtils.isMaster(level(), blockPosition()) ? 18 : LibUtils.isAtLeastExpert(level(), blockPosition()) ? 14 : 7);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (kind == Kind.CANNONBALL) explode(result.getLocation());
        else super.onHitEntity(result);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (kind == Kind.CANNONBALL) explode(result.getLocation());
        else super.onHitBlock(result);
    }

    private void explode(Vec3 point) {
        setPos(point);
        if (level() instanceof ServerLevel server && getOwner() instanceof Mob owner) {
            server.sendParticles(ParticleTypes.EXPLOSION, point.x, point.y, point.z, 1, 0, 0, 0, 0);
            server.playSound(null, blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1, 1);
            for (LivingEntity target : server.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(3))) {
                if (owner.canAttack(target))
                    target.hurt(damageSources().mobProjectile(this, owner), getDamage());
            }
        }
        discard();
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(switch (kind) {
            case BULLET -> Items.IRON_NUGGET;
            case ARROW -> Items.ARROW;
            case CANNONBALL -> Items.COAL;
        });
    }

    public enum Kind {BULLET, ARROW, CANNONBALL}
}
