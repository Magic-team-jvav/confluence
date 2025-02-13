package org.confluence.mod.common.entity.projectile;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ThunderZapperProjectile extends BaseManaStaffProjectileEntity {
    private float flip;
    private Vec3 dir;

    public ThunderZapperProjectile(LivingEntity living, Level level) {
        super(living, level, Variant.THUNDER_ZAPPER);
        this.flip = 0.32F;
    }

    @Override
    public void tick() {
        if (dir == null) {
            this.dir = getDeltaMovement().normalize();
        }
        if (level().getGameTime() % 10 < 5) {
            this.flip = -flip;
            setDeltaMovement(dir);
        } else {
            setDeltaMovement(dir.add(0, flip, 0));
        }
        super.tick();
    }
}
