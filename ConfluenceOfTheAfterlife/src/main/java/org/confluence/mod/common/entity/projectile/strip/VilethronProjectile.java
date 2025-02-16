package org.confluence.mod.common.entity.projectile.strip;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.confluence.mod.common.init.ModEntities;

public class VilethronProjectile extends StripedProjectile {
    public VilethronProjectile(EntityType<? extends StripedProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public VilethronProjectile(LivingEntity living) {
        super(ModEntities.VILETHRON_PROJECTILE.get(), living);
    }

    public VilethronProjectile(LivingEntity living, Vec3 pos) {
        super(ModEntities.VILETHRON_PROJECTILE.get(), living, pos);
    }

    @Override
    protected void onTouchEntity(EntityHitResult result) {
        result.getEntity().hurt(damageSources().indirectMagic(this, getOwner()), 2.5f);
    }

    @Override
    protected StripedProjectile createBody(LivingEntity shooter) {
        return new VilethronProjectile(shooter, position());
    }

    @OnlyIn(Dist.CLIENT)
    public int getAlpha() {
        return 255 - tickCount * 255 / ticksForBodyRemove;
    }
}
