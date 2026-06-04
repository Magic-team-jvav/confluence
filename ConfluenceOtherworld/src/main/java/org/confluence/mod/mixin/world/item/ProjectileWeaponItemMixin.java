package org.confluence.mod.mixin.world.item;

import net.minecraft.world.item.ProjectileWeaponItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = ProjectileWeaponItem.class, priority = 1100)
public abstract class ProjectileWeaponItemMixin {
}
