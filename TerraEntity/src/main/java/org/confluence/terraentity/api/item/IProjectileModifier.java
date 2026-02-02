package org.confluence.terraentity.api.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

/**
 * 由于命中效果很多是不涉及武器的，只能使用这种办法适配少数情况来对弹幕进行修改
 * 由于泛型类型检查是不安全的，所以需要注意物品通配类型和触发弹幕的类型
 */
public interface IProjectileModifier<T extends Projectile> {

    void modifyProjectile(Level level, LivingEntity shooter, T projectile);



}
