package org.confluence.mod.mixin.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.entity.projectile.range.arrow.BaseArrowEntity;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.item.bow.BaseTerraBowItem;
import org.confluence.mod.common.item.bow.arrow.BaseTerraArrowItem;
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
    public void createArrow(Level level, ItemStack ammo, LivingEntity shooter, @Nullable ItemStack weapon, CallbackInfoReturnable<AbstractArrow> cir) {
        if (weapon != null && weapon.getItem() instanceof BaseTerraBowItem bow) {
            BaseTerraBowItem.Builder builder = bow.modifyArrowBuilder;
            if(builder.entityTransform != null){
                // 非物品箭的箭实体转化
                BaseArrowEntity arrow = builder.entityTransform.factory().create(builder.entityTransform.type(), shooter, ammo.copyWithCount(1), weapon, null, bow.modifyArrowBuilder);
                cir.setReturnValue(arrow);
                return;
            }
            BaseTerraArrowItem arrowItem = bow.arrowModifier.getTransformArrow();
            if (arrowItem != null) {
                // 木箭转化
                cir.setReturnValue(new BaseArrowEntity(ModEntities.ARROW_PROJECTILE.get(), shooter, ammo.copyWithCount(1), weapon, arrowItem, bow.modifyArrowBuilder));
            }
        }
    }
}
