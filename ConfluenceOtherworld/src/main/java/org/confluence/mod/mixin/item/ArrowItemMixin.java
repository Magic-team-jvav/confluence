package org.confluence.mod.mixin.item;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.api.ITerraArrowProjectileWeaponItem;
import org.confluence.mod.common.entity.projectile.range.arrow.BaseArrowEntity;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.item.arrow.BaseTerraArrowItem;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ArrowItem.class, priority = 1100)
public abstract class ArrowItemMixin {
    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;<init>(Lnet/minecraft/world/item/Item$Properties;)V"))
    private static Item.Properties maxStack(Item.Properties properties) {
        int value = LibUtils.MAX_STACK_SIZE;
        if (properties.components != null && !properties.components.map.containsKey(DataComponents.DAMAGE)) {
            int size = (int) properties.components.map.getOrDefault(DataComponents.MAX_STACK_SIZE, 1);
            if (size > value) value = size;
        }
        return properties.stacksTo(value);
    }

    @Inject(method = "createArrow", at = @At("HEAD"), cancellable = true)
    public void createArrow(CallbackInfoReturnable<AbstractArrow> cir, @Local(argsOnly = true, ordinal = 0) ItemStack ammo, @Local(argsOnly = true) LivingEntity shooter, @Local(argsOnly = true, ordinal = 1) @Nullable ItemStack weapon) {
        if (weapon != null && weapon.getItem() instanceof ITerraArrowProjectileWeaponItem<?> bow) {
            BaseTerraArrowItem.ModifyArrowBuilder modifyArrowBuilder = bow.getModifyArrowBuilder();
            if (modifyArrowBuilder.entityTransform != null) {
                // 非物品箭的箭实体转化
                BaseArrowEntity arrow = modifyArrowBuilder.entityTransform.factory().create(modifyArrowBuilder.entityTransform.type(), shooter, ammo.copyWithCount(1), weapon, null, modifyArrowBuilder);
                cir.setReturnValue(arrow);
                return;
            }
            BaseTerraArrowItem arrowItem = bow.getArrowModifier().getTransformArrow();
            if (arrowItem != null) {
                // 木箭转化
                cir.setReturnValue(new BaseArrowEntity(ModEntities.ARROW_PROJECTILE.get(), shooter, ammo.copyWithCount(1), weapon, arrowItem, modifyArrowBuilder));
            }
        }
    }
}
