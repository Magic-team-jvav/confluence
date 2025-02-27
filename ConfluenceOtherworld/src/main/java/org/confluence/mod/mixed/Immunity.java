package org.confluence.mod.mixed;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import org.confluence.mod.util.ModUtils;

public interface Immunity {
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
