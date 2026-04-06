package org.confluence.mod.api;

import net.minecraft.world.item.ProjectileWeaponItem;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.common.entity.projectile.range.arrow.BaseArrowEntity;
import org.confluence.mod.common.item.arrow.BaseTerraArrowItem;

/// 泰拉使用箭或之类的弹射物的[ProjectileWeaponItem]需要继承此接口
public interface ITerraArrowProjectileWeaponItem<T extends ProjectileWeaponItem & ITerraArrowProjectileWeaponItem<T>> extends SelfGetter<T> {
    BaseTerraArrowItem.ModifyArrowBuilder getModifyArrowBuilder();

    BaseArrowEntity.Builder getArrowModifier();
}
