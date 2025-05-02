package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * todo 来自骷髅头法书
 */
public class SkullProjectile extends AbstractManaProjectile {
    public SkullProjectile(EntityType<SkullProjectile> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void baseTick() {
        super.baseTick();

        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        checkInsideBlocks();
        HitResult.Type hitresult$type = hitresult.getType();
        if (hitresult$type == HitResult.Type.BLOCK) {
            onHitBlock((BlockHitResult) hitresult);
            discard();
            return;
        } else if (hitresult$type == HitResult.Type.ENTITY) {
            onHitEntity((EntityHitResult) hitresult);
        }

        Vec3 vec3 = getDeltaMovement();
        double offX = getX() + vec3.x;
        double offY = getY() + vec3.y;
        double offZ = getZ() + vec3.z;
        setPos(offX, offY, offZ);


    }
}
