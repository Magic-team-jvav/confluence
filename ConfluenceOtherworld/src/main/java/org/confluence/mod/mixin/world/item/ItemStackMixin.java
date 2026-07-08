package org.confluence.mod.mixin.world.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import org.confluence.mod.mixed.Immunity;
import org.confluence.mod.util.ModUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = ItemStack.class, priority = 1100)
public abstract class ItemStackMixin implements Immunity {
    @Shadow
    public abstract Item getItem();

    @Override
    public Type confluence$getImmunityType() {
        return Type.LOCAL;
    }

    @Override
    public int confluence$getImmunityDuration(DamageSource damageSource) {
        if (getItem() instanceof Immunity immunity) {
            return immunity.confluence$getImmunityDuration(damageSource);
        }
        // 自身是汇流近战武器且使用者有攻击速度属性
        if (getItem() instanceof SwordItem weaponItem
                && ModUtils.isFromConfluence(BuiltInRegistries.ITEM, weaponItem)
                && damageSource.getEntity() instanceof LivingEntity living
        ) {
            AttributeInstance instance = living.getAttribute(Attributes.ATTACK_SPEED);
            if (instance == null) return DEFAULT_IMMUNITY_DURATINO;
            double speed = instance.getValue();
            int time = (int) (20 / speed) - 1;
            return Math.max(0, time);
        }
        return DEFAULT_IMMUNITY_DURATINO;
    }
}
