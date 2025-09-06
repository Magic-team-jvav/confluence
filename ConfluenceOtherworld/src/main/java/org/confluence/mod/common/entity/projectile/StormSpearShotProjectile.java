package org.confluence.mod.common.entity.projectile;

import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.item.lance.StormSpearItem;
import org.confluence.mod.util.ModUtils;

public class StormSpearShotProjectile extends DamageSettableProjectile {
    private float rotation = 0;
    public StormSpearShotProjectile(EntityType<? extends DamageSettableProjectile> entityType, Level level) {
        super(entityType, level);
        setNoGravity(true);
    }

    @Override
    public void baseTick() {
        super.baseTick();

        rotation += 100;
        if (rotation >= 360) rotation -= 360;

        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        checkInsideBlocks();
        HitResult.Type hitresult$type = hitresult.getType();
        if (hitresult$type == HitResult.Type.BLOCK) {
            onHitBlock((BlockHitResult) hitresult);
            discard();
            return;
        } else if (hitresult$type == HitResult.Type.ENTITY) {
            onHitEntity((EntityHitResult) hitresult);
            discard();
            return;
        }

        Vec3 vec3 = getDeltaMovement();
        double offX = getX() + vec3.x;
        double offY = getY() + vec3.y;
        double offZ = getZ() + vec3.z;
        setPos(offX, offY, offZ);

        if (tickCount > 200) discard();
    }
    public float getRotation() {
        return rotation;
    }
    @Override
    protected boolean canHitEntity(Entity target) {
        return ModUtils.canHitEntity(target, getOwner());
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!level().isClientSide) {
            result.getEntity().hurt(ModDamageTypes.of(level(), DamageTypes.STING, this, getOwner()), getCalculatedDamage());
            VectorUtils.knockBackA2B(this, result.getEntity(), StormSpearItem.knockBackScale * 0.5, StormSpearItem.knockBackMotionY * 0.5);
        }
    }
}
