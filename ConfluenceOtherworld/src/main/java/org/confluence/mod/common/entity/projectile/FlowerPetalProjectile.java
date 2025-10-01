package org.confluence.mod.common.entity.projectile;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.util.ModUtils;
import org.mesdag.particlestorm.PSGameClient;
import org.mesdag.particlestorm.particle.ParticleEmitter;

// 山铜套装奖励
public class FlowerPetalProjectile extends Projectile {
    private ParticleEmitter emitter;
    private ParticleEmitter trail;
    public FlowerPetalProjectile(EntityType<FlowerPetalProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public FlowerPetalProjectile(Player player) {
        super(ModEntities.FLOWER_PETAL_PROJECTILE.get(), player.level());
        setOwner(player);
        setNoGravity(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    public void baseTick() {
        super.baseTick();

        if (level().isClientSide && (emitter == null || trail == null)) {
            this.emitter = new ParticleEmitter(level(), position(), Confluence.asResource("flower_petal"));
            this.trail = new ParticleEmitter(level(), position(), Confluence.asResource("flower_petal_trail"));
            emitter.attachEntity(this);
            trail.attachEntity(this);
            PSGameClient.LOADER.addEmitter(emitter, false);
            PSGameClient.LOADER.addEmitter(trail, false);
        }
        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        checkInsideBlocks();
        HitResult.Type hitresult$type = hitresult.getType();
        if (hitresult$type == HitResult.Type.BLOCK) {
            onHitBlock((BlockHitResult) hitresult);
        } else if (hitresult$type == HitResult.Type.ENTITY) {
            onHitEntity((EntityHitResult) hitresult);
        }

        Vec3 vec3 = getDeltaMovement();
        double offX = getX() + vec3.x;
        double offY = getY() + vec3.y;
        double offZ = getZ() + vec3.z;
        setPos(offX, offY, offZ);

        if (tickCount > 20) discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        result.getEntity().hurt(damageSources().mobProjectile(this, (LivingEntity) getOwner()), 7.2F);
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return ModUtils.canHitEntity(target, getOwner());
    }
}
