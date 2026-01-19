package org.confluence.mod.api;

import net.minecraft.world.item.ProjectileWeaponItem;
import org.confluence.mod.common.entity.projectile.range.arrow.BaseArrowEntity;
import org.confluence.mod.common.item.arrow.BaseTerraArrowItem;

/**
 * 泰拉使用箭或之类的弹射物的{@link ProjectileWeaponItem}需要继承此接口
 */
public interface ITerraArrowProjectileWeaponItem<T extends ProjectileWeaponItem & ITerraArrowProjectileWeaponItem<T>> {
    default T of() {
        return (T) this;
    }

    BaseTerraArrowItem.ModifyArrowBuilder getModifyArrowBuilder();

    BaseArrowEntity.Builder getArrowModifier();
}
