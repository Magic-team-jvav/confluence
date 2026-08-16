package org.confluence.mod.common.item.gun.definition;

import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.combat.gun.HomingController;
import org.confluence.mod.common.entity.projectile.BaseBulletEntity;
import org.confluence.mod.common.init.ModEffects;

import java.util.Comparator;

/// Confluence 自带弹药所使用的标准行为。
public final class BulletBehaviors {
    public static final BulletBehavior NORMAL = simple("");
    public static final BulletBehavior SILVER_PARTICLES = simple("");
    public static final BulletBehavior PARTY_CONFETTI = simple("tooltip.confluence.ability.party_confetti");
    public static final BulletBehavior CRYSTAL_SPLIT = new CrystalSplit();
    public static final BulletBehavior CHLOROPHYTE_HOMING = new ChlorophyteHoming();
    public static final BulletBehavior METEOR_RICOCHET = new MeteorRicochet();
    public static final BulletBehavior NANO_RICOCHET = new NanoRicochet();
    public static final BulletBehavior HIGH_VELOCITY_DAMAGE_DECAY = damageDecay(
            "tooltip.confluence.ability.high_velocity_damage_decay", 0.85F);
    public static final BulletBehavior EXPLOSIVE = new Explosive();
    public static final BulletBehavior ICHOR_DEBUFF = debuff(
            "tooltip.confluence.ability.ichor_debuff", ModEffects.ICHOR.get(), 240);
    public static final BulletBehavior CURSED_DEBUFF = debuff(
            "tooltip.confluence.ability.cursed_debuff", MobEffects.WITHER, 120);
    public static final BulletBehavior VENOM_DEBUFF = debuff(
            "tooltip.confluence.ability.venom_debuff", MobEffects.POISON, 160);
    public static final BulletBehavior LUMINITE_DAMAGE_DECAY = damageDecay(
            "tooltip.confluence.ability.luminite_damage_decay", 0.96F);

    private static final double RICOCHET_POSITION_EPSILON = 0.02D;

    private BulletBehaviors() {}

    private static BulletBehavior simple(String tooltipKey) {
        return new NamedBehavior(tooltipKey) {};
    }

    private static BulletBehavior damageDecay(String tooltipKey, float multiplier) {
        return new NamedBehavior(tooltipKey) {
            @Override
            public void onHitEntity(BaseBulletEntity entity, EntityHitResult result) {
                float damage = entity.getDamage() * multiplier;
                entity.setDamage(damage < 0.01F ? 0.0F : damage);
            }
        };
    }

    private static BulletBehavior debuff(String tooltipKey, MobEffect effect, int duration) {
        return new NamedBehavior(tooltipKey) {
            @Override
            public void onHitEntity(BaseBulletEntity entity, EntityHitResult result) {
                if (!entity.level().isClientSide && result.getEntity() instanceof LivingEntity living) {
                    living.addEffect(new MobEffectInstance(effect, duration, 0));
                }
            }
        };
    }

    private abstract static class NamedBehavior implements BulletBehavior {
        private final String tooltipKey;

        private NamedBehavior(String tooltipKey) {
            this.tooltipKey = tooltipKey;
        }

        @Override
        public String tooltipKey() {
            return tooltipKey;
        }
    }

    private static final class ChlorophyteHoming extends NamedBehavior {
        private static final double FORWARD_RANGE = 18.75D;
        private static final double HALF_WIDTH = 6.0D;
        private static final double HALF_HEIGHT = 5.0D;
        private static final double TURN_RATE = Math.toRadians(14.0D);

        private ChlorophyteHoming() {
            super("tooltip.confluence.ability.chlorophyte_homing");
        }

        @Override
        public void tick(BaseBulletEntity entity) {
            Vec3 velocity = entity.getDeltaMovement();
            if (velocity.lengthSqr() < 1.0E-10D) return;

            LivingEntity target = entity.level().isClientSide
                    ? entity.getHomingTarget()
                    : findTarget(entity, velocity.normalize());
            if (!entity.level().isClientSide) entity.setHomingTarget(target);
            if (!isValidTarget(entity, target) || !insideRectangle(entity, target, velocity.normalize()))
                return;

            Vec3 offset = target.getBoundingBox().getCenter().subtract(entity.position());
            entity.setDeltaMovement(HomingController.rotateVelocityToward(velocity, offset, TURN_RATE));
        }

        private static LivingEntity findTarget(BaseBulletEntity entity, Vec3 direction) {
            double margin = HALF_WIDTH + HALF_HEIGHT + 1.0D;
            AABB search = entity.getBoundingBox().expandTowards(direction.scale(FORWARD_RANGE)).inflate(margin);
            return entity.level().getEntitiesOfClass(LivingEntity.class, search,
                            target -> isValidTarget(entity, target) && insideRectangle(entity, target, direction))
                    .stream()
                    .min(Comparator.comparingDouble((LivingEntity target) -> entity.position()
                                    .distanceToSqr(target.getBoundingBox().getCenter()))
                            .thenComparingInt(Entity::getId))
                    .orElse(null);
        }

        private static boolean insideRectangle(BaseBulletEntity entity, LivingEntity target, Vec3 direction) {
            Vec3 offset = target.getBoundingBox().getCenter().subtract(entity.position());
            double forward = offset.dot(direction);
            double targetWidth = target.getBbWidth() * 0.5D;
            double targetHeight = target.getBbHeight() * 0.5D;
            if (forward < -targetWidth || forward > FORWARD_RANGE + targetWidth) return false;

            Vec3 side = direction.cross(new Vec3(0.0D, 1.0D, 0.0D));
            if (side.lengthSqr() <= 1.0E-10D) side = direction.cross(new Vec3(1.0D, 0.0D, 0.0D));
            side = side.normalize();
            Vec3 vertical = side.cross(direction).normalize();
            return Math.abs(offset.dot(side)) <= HALF_WIDTH + targetWidth
                    && Math.abs(offset.dot(vertical)) <= HALF_HEIGHT + targetHeight;
        }
    }

    private static final class CrystalSplit extends NamedBehavior {
        private CrystalSplit() {
            super("tooltip.confluence.ability.crystal_split");
        }

        @Override
        public boolean onHitBlock(BaseBulletEntity entity, BlockHitResult result) {
            Direction side = result.getDirection();
            split(entity, new Vec3(side.getStepX(), side.getStepY(), side.getStepZ()).scale(0.12D));
            return false;
        }

        @Override
        public void onHitEntity(BaseBulletEntity entity, EntityHitResult result) {
            Vec3 velocity = entity.getDeltaMovement();
            split(entity, velocity.lengthSqr() > 1.0E-5D ? velocity.normalize().scale(-0.12D) : Vec3.ZERO);
        }

        private static void split(BaseBulletEntity entity, Vec3 offset) {
            if (entity.level().isClientSide || entity.getEffectState() > 0) return;
            Vec3 velocity = entity.getDeltaMovement();
            if (velocity.lengthSqr() < 1.0E-5D) velocity = entity.getLookAngle();
            if (velocity.lengthSqr() < 1.0E-5D) return;

            double speed = Math.min(6.0D, Math.max(0.35D, velocity.length() * 0.35D));
            Vec3 backward = velocity.normalize().scale(-1.0D);
            for (int index = -1; index <= 1; index += 2) {
                double angle = index * 0.32D;
                double cos = Math.cos(angle);
                double sin = Math.sin(angle);
                Vec3 direction = new Vec3(
                        backward.x * cos + backward.z * sin,
                        backward.y,
                        backward.z * cos - backward.x * sin).normalize();
                BaseBulletEntity child = entity.createChild(direction.scale(speed), 0.5F, 1, offset);
                child.setIgnoresBlockCollision(true);
                entity.level().addFreshEntity(child);
            }
        }
    }

    private static final class MeteorRicochet extends NamedBehavior {
        private MeteorRicochet() {
            super("tooltip.confluence.ability.meteor_ricochet");
        }

        @Override
        public boolean onHitBlock(BaseBulletEntity entity, BlockHitResult result) {
            if (entity.getEffectState() >= 1) return false;
            Vec3 velocity = entity.getDeltaMovement();
            Direction side = result.getDirection();
            Vec3 normal = new Vec3(side.getStepX(), side.getStepY(), side.getStepZ());
            double dot = velocity.dot(normal);
            if (dot >= -1.0E-6D) return false;
            Vec3 reflected = velocity.subtract(normal.scale(2.0D * dot));
            if (reflected.lengthSqr() < 1.0E-5D) return false;
            entity.setEffectState(1);
            entity.setDeltaMovement(reflected);
            moveOutsideBlock(entity, result);
            return true;
        }

        @Override
        public void onHitEntity(BaseBulletEntity entity, EntityHitResult result) {
            if (entity.getEffectState() == 1) {
                entity.setPenetrate(1);
            } else if (entity.getEffectState() == 0) {
                entity.setEffectState(2);
                int penetrate = entity.getPenetrate();
                entity.setPenetrate(penetrate < 0 ? 2 : Math.min(penetrate, 2));
            }
        }
    }

    private static final class NanoRicochet extends NamedBehavior {
        private NanoRicochet() {
            super("tooltip.confluence.ability.nano_ricochet");
        }

        @Override
        public boolean onHitBlock(BaseBulletEntity entity, BlockHitResult result) {
            if (entity.getEffectState() >= 1) return false;
            LivingEntity target = nearestTarget(entity, 32.0D);
            if (target == null && entity.level().isClientSide) target = entity.getHomingTarget();
            if (!isValidTarget(entity, target) || homingDistance(entity, target) > 32.0D)
                return false;

            Vec3 offset = target.getBoundingBox().getCenter().subtract(entity.position());
            if (offset.lengthSqr() <= 1.0E-7D) return false;
            double speed = Math.max(0.25D, entity.getDeltaMovement().length());
            entity.setEffectState(1);
            entity.setDamage(entity.getDamage() * 0.66F);
            entity.setHomingTarget(target);
            entity.setDeltaMovement(offset.normalize().scale(speed));
            moveOutsideBlock(entity, result);
            return true;
        }
    }

    private static final class Explosive extends NamedBehavior {
        private Explosive() {
            super("tooltip.confluence.ability.explosive");
        }

        @Override
        public boolean onHitBlock(BaseBulletEntity entity, BlockHitResult result) {
            explode(entity);
            return false;
        }

        @Override
        public void onHitEntity(BaseBulletEntity entity, EntityHitResult result) {
            explode(entity);
        }
    }

    private static boolean isValidTarget(BaseBulletEntity entity, LivingEntity target) {
        Entity owner = entity.getOwner();
        return target instanceof Enemy && target.isAlive() && entity.canHitTarget(target)
                && (owner == null || !owner.isAlliedTo(target));
    }

    private static LivingEntity nearestTarget(BaseBulletEntity entity, double range) {
        return entity.level().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(range),
                        target -> isValidTarget(entity, target) && homingDistance(entity, target) <= range)
                .stream()
                .min(Comparator.comparingDouble((LivingEntity target) -> homingDistance(entity, target))
                        .thenComparingInt(Entity::getId))
                .orElse(null);
    }

    private static double homingDistance(BaseBulletEntity entity, LivingEntity target) {
        Vec3 offset = target.getBoundingBox().getCenter().subtract(entity.position());
        return Math.sqrt(offset.x * offset.x + offset.z * offset.z) + Math.abs(offset.y);
    }

    private static void moveOutsideBlock(BaseBulletEntity entity, BlockHitResult result) {
        Direction side = result.getDirection();
        Vec3 normal = new Vec3(side.getStepX(), side.getStepY(), side.getStepZ());
        double halfExtent = Math.max(entity.getBbWidth(), entity.getBbHeight()) * 0.5D;
        Vec3 safe = result.getLocation().add(normal.scale(halfExtent + RICOCHET_POSITION_EPSILON));
        entity.setPos(safe.x, safe.y, safe.z);
    }

    private static void explode(BaseBulletEntity entity) {
        if (!entity.level().isClientSide) {
            entity.level().explode(entity, entity.getX(), entity.getY(), entity.getZ(),
                    2.0F, Level.ExplosionInteraction.NONE);
        }
    }
}
