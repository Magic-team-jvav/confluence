package org.confluence.mod.common.entity.projectile.sword;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ForwardSwordProjectile extends SwordProjectile<ForwardSwordProjectile> {

    public ForwardSwordProjectile(EntityType<ForwardSwordProjectile> entityType, Level pLevel) {
        super(entityType, pLevel);
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 vec3 = getDeltaMovement();
        double offX = getX() + vec3.x;
        double offY = getY() + vec3.y;
        double offZ = getZ() + vec3.z;
        float accelerate;
        if(projComponent != null){
            accelerate = projComponent.acceleration();
        }else{
            accelerate = 0.8f;
        }

        setDeltaMovement(vec3.scale(accelerate));
        setPos(offX, offY, offZ);
    }


}
