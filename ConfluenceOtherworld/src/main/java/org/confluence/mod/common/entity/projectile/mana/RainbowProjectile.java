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
import org.confluence.mod.common.item.mana.RainbowRodItem;

public class RainbowProjectile extends BaseDraggingProjectile {
    public static final double RANGE = 8.0 * 2 / 3;
    public static final double KNOCKBACK = 0.75;

    public RainbowProjectile(EntityType<? extends BaseDraggingProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public RainbowProjectile(LivingEntity living) {
        this(ModEntities.RAINBOW_PROJECTILE.get(), living.level());
    }

    @Override
    protected int getCooldown() {
        return RainbowRodItem.COOLDOWN;
    }

    @Override
    protected BaseDraggingStaffItem<?> getDraggingStaff() {
        return ManaWeaponItems.RAINBOW_ROD.get();
    }

    @Override
    protected ResourceLocation getParticleId() {
        return Confluence.asResource("rainbow_projectile");
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (shot) {
            doExplosion(RANGE, KNOCKBACK);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        doExplosion(RANGE, KNOCKBACK);
    }
}
