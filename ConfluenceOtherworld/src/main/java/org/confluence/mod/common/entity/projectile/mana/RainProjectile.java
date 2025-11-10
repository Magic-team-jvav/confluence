package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.mixed.Immunity;

public class RainProjectile extends AbstractManaProjectile implements Immunity {
    private int maxPenetrate = 2;

    public RainProjectile(EntityType<? extends RainProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public RainProjectile(EntityType<? extends RainProjectile> entityType, LivingEntity living, Vec3 position) {
        this(entityType, living.level());
        setOwner(living);
        setPos(position);
    }

    public void setMaxPenetrate(int maxPenetrate) {
        this.maxPenetrate = maxPenetrate;
    }

    @Override
    public void baseTick() {
        if (tickCount > 200) {
            discard();
            return;
        }
        super.baseTick();

        Vec3 vec3 = getDeltaMovement();
        double offX = getX() + vec3.x;
        double offY = getY() + vec3.y;
        double offZ = getZ() + vec3.z;
        setPos(offX, offY, offZ);
        setDeltaMovement(vec3.add(0, -0.08, 0));
    }

    @Override
    protected void doHitCheck() {
        HitResult hitResult = ProjectileUtil.getHitResult(position(), this, this::canHitEntity, getDeltaMovement(), level(), 0.6F, ClipContext.Block.COLLIDER);
        checkInsideBlocks();
        HitResult.Type hitresult$type = hitResult.getType();
        if (hitresult$type == HitResult.Type.BLOCK) {
            onHitBlock((BlockHitResult) hitResult);
        } else if (hitresult$type == HitResult.Type.ENTITY) {
            onHitEntity((EntityHitResult) hitResult);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        if (doPenetrateCheck(entity)) {
            doHurtAndKnockback(entity, 0, 0);
            doDiscardInMaxPenetrate(maxPenetrate);
        }
    }

    @Override
    public Type confluence$getImmunityType() {
        return Type.STATIC;
    }

    @Override
    public int confluence$getImmunityDuration(DamageSource damageSource) {
        return 3;
    }
}
