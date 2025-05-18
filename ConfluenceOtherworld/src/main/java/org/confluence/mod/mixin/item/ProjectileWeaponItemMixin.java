package org.confluence.mod.mixin.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import org.confluence.mod.integration.supplementaries.SupplementariesHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ProjectileWeaponItem.class, priority = 1100)
public abstract class ProjectileWeaponItemMixin {
//    @Inject(method = "shoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
//    private void modifyProjectile(ServerLevel level, LivingEntity shooter, InteractionHand hand, ItemStack weapon, List<ItemStack> projectileItems, float velocity, float inaccuracy, boolean isCrit, LivingEntity target, CallbackInfo ci, @Local Projectile projectile) {
//        if (projectile instanceof AbstractArrow abstractArrow) {
//            ShortBowItem.applyToArrow(weapon, abstractArrow);
//            // 激活弓箭满蓄力特殊效果
//            if (abstractArrow instanceof BaseArrowEntity terraArrow) {
//                WeaponStorage data = shooter.getData(ModAttachmentTypes.WEAPON_STORAGE);
//                if (data.bowFullPull) {
//                    terraArrow.fullPull = true;
//                    data.bowFullPull = false;
//                }
//            }
//        }
//    }

    @WrapOperation(method = "useAmmo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;split(I)Lnet/minecraft/world/item/ItemStack;"))
    private static ItemStack skipWrapOperation(ItemStack instance, int amount, Operation<ItemStack> original, @Local(argsOnly = true) LivingEntity shooter) {
        if (SupplementariesHelper.shouldSkip(instance, shooter)) {
            return instance.split(amount);
        }
        return original.call(instance, amount);
    }
}
