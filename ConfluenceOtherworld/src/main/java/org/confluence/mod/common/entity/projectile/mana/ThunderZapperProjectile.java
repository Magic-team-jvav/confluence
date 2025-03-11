package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class ThunderZapperProjectile extends BaseManaStaffProjectileEntity {
    private float flip = 0.32F;
    private Vec3 dir;

    public ThunderZapperProjectile(LivingEntity living) {
        super(living, Variant.THUNDER_ZAPPER);
    }

    @Override
    public void tick() {
        if (dir == null) {
            this.dir = getDeltaMovement().normalize();
        }
        int i = tickCount % 10;
        if (i == 0) {
            this.flip = -flip;
            setDeltaMovement(dir);
        } else if (i >= 5) {
            setDeltaMovement(dir.add(0, flip, 0));
        }
        super.tick();
    }
}
