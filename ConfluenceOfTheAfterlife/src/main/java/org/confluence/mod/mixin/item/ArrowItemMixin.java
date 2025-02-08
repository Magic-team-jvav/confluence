package org.confluence.mod.mixin.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.projectile.BaseArrowEntity;
import org.confluence.mod.common.item.bow.BaseArrowItem;
import org.confluence.mod.common.item.bow.TerraBowItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArrowItem.class)
public class ArrowItemMixin {

    @Inject(method = "createArrow", at = @At("HEAD"), cancellable = true)
    public void createArrow(Level level, ItemStack ammo, LivingEntity shooter, ItemStack weapon, CallbackInfoReturnable<AbstractArrow> cir) {
        // 木箭转化
        if(weapon.getItem() instanceof TerraBowItem bow){
            BaseArrowItem arrowItem = bow.arrowModifier.getTransformArrow();
            if(arrowItem!= null) {
                BaseArrowEntity arrow = new BaseArrowEntity(shooter, ammo.copyWithCount(1), weapon, arrowItem,  bow.modifyArrowBuilder);

                cir.setReturnValue(arrow);
            }
        }
    }
}
