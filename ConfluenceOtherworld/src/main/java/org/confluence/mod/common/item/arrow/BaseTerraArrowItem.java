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
import org.confluence.mod.common.item.bow.BaseTerraBowItem;
import org.jetbrains.annotations.Nullable;

public class BaseTerraArrowItem extends ArrowItem {
    public BaseTerraArrowItem(Properties properties) {
        super(properties);
    }

    protected EntityType<? extends BaseArrowEntity> getEntityType() {
        return ModEntities.BASE_ARROW.get();
    }

    @Override
    public AbstractArrow createArrow(Level level, ItemStack stack, LivingEntity shooter, @Nullable ItemStack weapon) {
        if (weapon != null && stack.getItem() instanceof BaseTerraArrowItem arrowItem) {
            BaseArrowEntity arrow = null;
            if (weapon.getItem() instanceof BaseTerraBowItem bow) {
                arrow = bow.createCustomArrow(shooter, stack, weapon);
            }
            if (arrow == null) {
                arrow = arrowItem.createArrowEntity(shooter, stack, weapon);
            }
            if (weapon.getItem() instanceof BaseTerraBowItem bow) {
                bow.modifyArrowEntity(arrow);
            }
            return arrow;
        }
        return super.createArrow(level, stack, shooter, weapon);
    }

    @Override
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        if (stack.getItem() instanceof BaseTerraArrowItem) {
            EntityType<? extends BaseArrowEntity> type = getEntityType();
            return new BaseArrowEntity(type, pos.x(), pos.y(), pos.z(), level, stack.copyWithCount(1), null);
        }
        return super.asProjectile(level, pos, stack, direction);
    }

    protected BaseArrowEntity createArrowEntity(LivingEntity shooter, ItemStack ammo, ItemStack weapon) {
        return new BaseArrowEntity(getEntityType(), shooter, ammo, weapon);
    }
}
