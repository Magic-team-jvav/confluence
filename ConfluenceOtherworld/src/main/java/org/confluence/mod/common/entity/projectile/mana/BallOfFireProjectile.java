package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.terraentity.init.TEEffects;
import org.mesdag.particlestorm.PSGameClient;
import org.mesdag.particlestorm.particle.ParticleEmitter;

public class BallOfFireProjectile extends AbstractManaProjectile {
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
    public void baseTick() {
        if (!level().getFluidState(blockPosition()).isEmpty()) {
            discard();
            return;
        }
        super.baseTick();

        Vec3 vec3 = getDeltaMovement();
        move(MoverType.SELF, vec3.add(0.0, -0.04, 0.0));
        Vec3 motion = getDeltaMovement();
        if (!vec3.equals(motion)) {
            if (motion.x != vec3.x) motion = new Vec3(-vec3.x, vec3.y, vec3.z);
            if (motion.y != vec3.y) motion = new Vec3(vec3.x, -vec3.y, vec3.z);
            if (motion.z != vec3.z) motion = new Vec3(vec3.x, vec3.y, -vec3.z);
            if (this.collideCount++ >= 6) {
                discard();
                return;
            }
        }
        setDeltaMovement(motion.scale(0.99).add(0.0, -0.04, 0.0));

        if (!(level() instanceof ServerLevel serverLevel)) {
            if ((emitter == null || trail == null)) {
                this.emitter = new ParticleEmitter(level(), position(), Confluence.asResource("ball_of_fire"));
                this.trail = new ParticleEmitter(level(), position(), Confluence.asResource("ball_of_fire_trail"));
                emitter.attached = this;
                trail.attached = this;
                PSGameClient.LOADER.addEmitter(emitter, false);
                PSGameClient.LOADER.addEmitter(trail, false);
            }
        } else if (ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity) instanceof EntityHitResult entityHitResult) {
            Entity entity = entityHitResult.getEntity();
            boolean ddu = ModSecretSeeds.DONT_DIG_UP.match(serverLevel);
            if (random.nextBoolean()) {
                if (ddu && entity instanceof LivingEntity living) {
                    living.addEffect(new MobEffectInstance(TEEffects.HELLFIRE, 100));
                } else {
                    entity.setRemainingFireTicks(100);
                }
            }
            if (entity.hurt(getDamagesource(), ddu ? getCalculatedDamage() * 1.5F : getCalculatedDamage())) {
                VectorUtils.knockBackA2B(this, entity, 0.6, 0.2);
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.collideCount = compound.getInt("CollideCount");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("CollideCount", collideCount);
    }
}
