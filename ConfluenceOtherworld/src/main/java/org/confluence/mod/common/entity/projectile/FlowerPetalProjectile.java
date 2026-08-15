package org.confluence.mod.common.entity.projectile;

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
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.entity.ModEntities;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;

// 山铜套装奖励
public class FlowerPetalProjectile extends Projectile {
    private ParticleEmitter emitter;

    public FlowerPetalProjectile(EntityType<FlowerPetalProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public FlowerPetalProjectile(Player player) {
        super(ModEntities.FLOWER_PETAL.get(), player.level());
        setOwner(player);
        setNoGravity(true);
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    public void baseTick() {
        super.baseTick();

        if (level().isClientSide && (emitter == null || emitter.isRemoved())) {
            this.emitter = new ParticleEmitter(level(), position(), Confluence.asResource("flower_petal"));
            emitter.attachEntity(this);
            MolangParticleEngine.INSTANCE.addEmitter(emitter);
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
        /*
         * 所有者可能在弹幕加载后尚未恢复，也可能被外部代码替换为非生物实体。
         * 花瓣伤害必须归属于穿戴山铜套装的生物；无法确认所有者时直接结束弹幕，
         * 避免制造没有可靠伤害归属的攻击。
         */
        if (!(getOwner() instanceof LivingEntity owner)) {
            discard();
            return;
        }
        result.getEntity().hurt(damageSources().mobProjectile(this, owner), 18.2F);
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return LibEntityUtils.canHitEntity(target, getOwner());
    }
}
