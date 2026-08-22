package org.confluence.mod.common.item.arrow;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;
import org.confluence.mod.common.init.entity.ModEntities;
import org.jetbrains.annotations.Nullable;

public class BaseTerraArrowItem extends ArrowItem {
    public BaseTerraArrowItem(Properties properties) {
        super(properties);
    }

    protected EntityType<? extends BaseArrowEntity> getEntityType() {
        return ModEntities.BASE_ARROW.get();
    }

    @Override
    public AbstractArrow createArrow(Level level, ItemStack stack, LivingEntity shooter) {
        return createArrowEntity(shooter, stack, null);
    }

    @Override
    public AbstractArrow createArrow(Level level, ItemStack stack, LivingEntity shooter, @Nullable ItemStack weapon) {
        return createArrowEntity(shooter, stack, weapon);
    }

    @Override
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        BaseArrowEntity arrow = createArrowEntity(level, stack, null);
        arrow.setPos(pos.x(), pos.y(), pos.z());
        return arrow;
    }

    public BaseArrowEntity createArrowEntity(LivingEntity shooter, ItemStack ammo, @Nullable ItemStack weapon) {
        BaseArrowEntity arrow = createArrowEntity(shooter.level(), ammo, weapon);
        arrow.setPos(shooter.getX(), shooter.getEyeY() - 0.1F, shooter.getZ());
        arrow.setOwner(shooter);
        return arrow;
    }

    private BaseArrowEntity createArrowEntity(Level level, ItemStack ammo, @Nullable ItemStack weapon) {
        BaseArrowEntity arrow = getEntityType().create(level);
        if (arrow == null)
            throw new IllegalStateException("Unable to create arrow entity " + getEntityType());
        arrow.initializeProjectile(ammo.copyWithCount(1), weapon);
        return arrow;
    }
}
