package org.confluence.mod.common.entity.fishing;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.mixed.IFishingHook;

public abstract class AbstractFishingHook extends FishingHook {
    public AbstractFishingHook(EntityType<? extends FishingHook> entityType, Level level) {
        super(entityType, level);
    }

    public AbstractFishingHook(EntityType<? extends FishingHook> entityType, Level level, int luck, int lureSpeed) {
        super(entityType, level, luck, lureSpeed);
    }

    protected void setup(Player player) {
        setOwner(player);
        float f = player.getXRot();
        float f1 = player.getYRot();
        float f2 = Mth.cos(-f1 * Mth.DEG_TO_RAD - Mth.PI);
        float f3 = Mth.sin(-f1 * Mth.DEG_TO_RAD - Mth.PI);
        float f4 = -Mth.cos(-f * Mth.DEG_TO_RAD);
        float f5 = Mth.sin(-f * Mth.DEG_TO_RAD);
        double d0 = player.getX() - f3 * 0.3;
        double d1 = player.getEyeY();
        double d2 = player.getZ() - f2 * 0.3;
        moveTo(d0, d1, d2, f1, f);
        Vec3 vec3 = new Vec3(-f3, Mth.clamp(-f5 / f4, -5.0F, 5.0F), -f2);
        double d3 = 0.6 / vec3.length();
        vec3 = vec3.multiply(d3 + random.triangle(0.5, 0.0103365), d3 + random.triangle(0.5, 0.0103365), d3 + random.triangle(0.5, 0.0103365));
        setDeltaMovement(vec3);
        setYRot((float) (Mth.atan2(vec3.x, vec3.z) * Mth.DEG_TO_RAD));
        setXRot((float) (Mth.atan2(vec3.y, vec3.horizontalDistance()) * Mth.DEG_TO_RAD));
        this.yRotO = getYRot();
        this.xRotO = getXRot();
    }

    @Override
    public boolean fireImmune() {
        return super.fireImmune() || IFishingHook.of(this).confluence$isLavaHook();
    }
}
