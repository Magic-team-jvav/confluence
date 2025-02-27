package org.confluence.mod.common.entity.projectile;

import net.minecraft.core.Direction;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.util.HomingUtils;
import org.mesdag.particlestorm.PSGameClient;
import org.mesdag.particlestorm.particle.ParticleEmitter;

public class BallOfFireProjectile extends Projectile {
    private int collideCount = 0;
    private ParticleEmitter emitter;
    private ParticleEmitter trail;

    public BallOfFireProjectile(EntityType<BallOfFireProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public BallOfFireProjectile(LivingEntity living) {
        this(ModEntities.BALL_OF_FIRE_PROJECTILE.get(), living.level());
        setOwner(living);
        setPos(living.getX(), living.getEyeY() - 0.1, living.getZ());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    public void tick() {
        if (getOwner() == null || !level().getFluidState(blockPosition()).isEmpty()) {
            discard();
            return;
        }
        super.tick();

        if (level().isClientSide && (emitter == null || trail == null)) {
            this.emitter = new ParticleEmitter(level(), position(), Confluence.asResource("ball_of_fire"));
            this.trail = new ParticleEmitter(level(), position(), Confluence.asResource("ball_of_fire_trail"));
            emitter.attached = this;
            trail.attached = this;
            PSGameClient.LOADER.addEmitter(emitter, false);
            PSGameClient.LOADER.addEmitter(trail, false);
        }

        Vec3 vec3 = getDeltaMovement();
        move(MoverType.SELF, vec3.add(0.0, -0.04, 0.0));
        Vec3 motion = getDeltaMovement();
        if (!vec3.equals(motion)) {
            Direction.Axis axis = null;
            if (motion.x != vec3.x) axis = Direction.Axis.X;
            if (motion.y != vec3.y) axis = Direction.Axis.Y;
            if (motion.z != vec3.z) axis = Direction.Axis.Z;
            if (axis != null) {
                if (collideCount >= 6) {
                    discard();
                } else {
                    motion = HomingUtils.relativeScale(vec3, axis, -1.0);
                    this.collideCount++;
                }
            }
        }
        if (level() instanceof ServerLevel serverLevel && ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity) instanceof EntityHitResult entityHitResult) {
            Entity entity = entityHitResult.getEntity();
            boolean ddu = ModSecretSeeds.DONT_DIG_UP.match(serverLevel);
            if (random.nextBoolean()) {
                if (ddu && entity instanceof LivingEntity living) {
                    living.addEffect(new MobEffectInstance(ModEffects.HELL_FIRE, 100));
                } else {
                    entity.setRemainingFireTicks(100);
                }
            }
            if (entity.hurt(damageSources().indirectMagic(this, getOwner()), ddu ? 17.0F : 9.6F)) {
                HomingUtils.knockBackA2B(this, entity, 0.6, 0.2);
            }
        }
        setDeltaMovement(motion.scale(0.99).add(0.0, -0.04, 0.0));
    }
}
