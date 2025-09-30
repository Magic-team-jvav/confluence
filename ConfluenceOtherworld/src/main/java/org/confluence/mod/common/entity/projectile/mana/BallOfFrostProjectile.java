package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModEntities;
import org.mesdag.particlestorm.PSGameClient;
import org.mesdag.particlestorm.particle.ParticleEmitter;

public class BallOfFrostProjectile extends AbstractManaProjectile {
    private int collideCount = 0;
    private ParticleEmitter emitter;
    private ParticleEmitter trail;

    public BallOfFrostProjectile(EntityType<? extends AbstractManaProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public BallOfFrostProjectile(Player player) {
        super(ModEntities.BALL_OF_FROST_PROJECTILE.get(), player.level());
        setOwner(player);
        setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
    }

    @Override
    public void baseTick() {
        super.baseTick();

        Vec3 vec3 = getDeltaMovement();
        move(MoverType.SELF, vec3);
        Vec3 motion = getDeltaMovement();
        if (!vec3.equals(motion)) {
            if (motion.x != vec3.x) motion = new Vec3(-vec3.x, vec3.y, vec3.z);
            if (motion.y != vec3.y) motion = new Vec3(vec3.x, -vec3.y, vec3.z);
            if (motion.z != vec3.z) motion = new Vec3(vec3.x, vec3.y, -vec3.z);
            if (this.collideCount++ >= 8) {
                discard();
                return;
            }
        }
        setDeltaMovement(motion);

        if (!(level() instanceof ServerLevel)) {
            if (emitter == null || trail == null) {
                this.emitter = new ParticleEmitter(level(), position(), Confluence.asResource("ball_of_frost"));
                this.trail = new ParticleEmitter(level(), position(), Confluence.asResource("ball_of_frost_trail"));
                emitter.attachEntity(this);
                trail.attachEntity(this);
                PSGameClient.LOADER.addEmitter(emitter, false);
                PSGameClient.LOADER.addEmitter(trail, false);
            }
        } else if (ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity) instanceof EntityHitResult entityHitResult) {
            Entity entity = entityHitResult.getEntity();
            if (entity instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(ModEffects.FROSTBITE, Mth.randomBetweenInclusive(living.getRandom(), 100, 280)));
            }
            if (entity.hurt(getDamagesource(), getCalculatedDamage())) {
                VectorUtils.knockBackA2B(this, entity, 0.65, 0.2);
            }
        }

        if (tickCount > 1200) discard();
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
