package org.confluence.mod.mixed;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import org.confluence.mod.util.ModUtils;
import org.jetbrains.annotations.Nullable;

public interface Immunity {
    @Nullable
    static Immunity getCause(DamageSource damageSource) {
        Entity directEntity = damageSource.getDirectEntity();
        ItemStack weaponItemStack = damageSource.getWeaponItem();
        if (weaponItemStack != null) {
            Item weaponItem = weaponItemStack.getItem();
            boolean fromConfluence = ModUtils.isFromConfluence(BuiltInRegistries.ITEM, weaponItem);
            if (fromConfluence && (weaponItem instanceof SwordItem) && directEntity instanceof Projectile projectile) { // 汇流剑气
                return (Immunity) projectile;
            } else if (weaponItem instanceof Immunity im) { // 非汇流但是实现了Immunity
                return switch (im.confluence$getImmunityType()) {
                    case STATIC -> im;
                    case LOCAL -> (Immunity) (Object) weaponItemStack;
                };
            } else if (fromConfluence) { // 其他所有汇流武器
                return (Immunity) (Object) weaponItemStack;
            }
        }
        if (directEntity instanceof Projectile proj && directEntity instanceof Immunity im && ModUtils.isFromConfluence(BuiltInRegistries.ENTITY_TYPE, directEntity.getType())) { // 汇流射弹
            return switch (im.confluence$getImmunityType()) {
                case STATIC -> (Immunity) proj.getType();
                case LOCAL -> im;
            };
        }
        // TODO: 打表
        return null;
    }

    static void calculateInvTicks(DamageSource damageSource, LivingEntity victim) {
        Immunity cause = getCause(damageSource);
        if (cause != null) {
            Object2IntMap<Immunity> invTicks = ((ILivingEntity) victim).confluence$getImmunityTicks();
            int time = cause.confluence$getImmunityDuration(damageSource);
            if (time != 0) {
                invTicks.put(cause, time);
            }
        }
    }

    enum Types {
        /** 静态无敌帧，以类而不是对象区分不同的伤害，比如魔刺，多个魔刺弹幕叠在一起伤害频率也不会变快 */
        STATIC,
        /** 局部无敌帧，以对象区分不同的伤害，比如召唤物，多个同种召唤物同时击中不会骗伤 */
        LOCAL
    }

    Types confluence$getImmunityType();

    default int confluence$getImmunityDuration(DamageSource damageSource){
        Entity causeEntity = damageSource.getEntity();
        // 自身是汇流近战武器且使用者有攻击速度属性
        if(getSelf() instanceof ItemStack weaponItemStack && weaponItemStack.getItem() instanceof SwordItem weaponItem
            && ModUtils.isFromConfluence(BuiltInRegistries.ITEM,weaponItem)
            && causeEntity instanceof LivingEntity living && living.getAttributes().hasAttribute(Attributes.ATTACK_SPEED)){
            double speed = living.getAttribute(Attributes.ATTACK_SPEED).getValue();
            int time = (int) (20 / speed) - 1;
            return Math.max(0, time);
        }
        return 0;
    }

    default Object getSelf(){
        return this;
    }
}
