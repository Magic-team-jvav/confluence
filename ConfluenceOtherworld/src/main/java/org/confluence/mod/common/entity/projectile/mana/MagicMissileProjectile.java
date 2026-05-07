package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.item.ManaWeaponItems;
import org.confluence.mod.common.item.mana.BaseDraggingStaffItem;
import org.confluence.mod.common.item.mana.MagicMissileItem;

public class MagicMissileProjectile extends BaseDraggingProjectile {
    public static final double RANGE = 8.0 * 2 / 3;

    public MagicMissileProjectile(EntityType<? extends MagicMissileProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public MagicMissileProjectile(LivingEntity living) {
        this(ModEntities.MAGIC_MISSILE_PROJECTILE.get(), living.level());
    }

    @Override
    protected BaseDraggingStaffItem<?> getDraggingStaff() {
        return ManaWeaponItems.MAGIC_MISSILE.get();
    }

    @Override
    protected int getCooldown() {
        return MagicMissileItem.COOLDOWN;
    }

    @Override
    protected ResourceLocation getParticleId() {
        return Confluence.asResource("magic_missile_projectile");
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        doExplosion(RANGE);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        doExplosion(RANGE);
    }
}
