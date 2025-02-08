package org.confluence.mod.mixin.item;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import org.confluence.mod.common.attachment.WeaponStorage;
import org.confluence.mod.common.entity.projectile.BaseArrowEntity;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.item.bow.BaseArrowItem;
import org.confluence.mod.common.item.bow.ShortBowItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ProjectileWeaponItem.class)
public abstract class ProjectileWeaponItemMixin {
    @Inject(method = "shoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private void modifyProjectile(ServerLevel level, LivingEntity shooter, InteractionHand hand, ItemStack weapon, List<ItemStack> projectileItems, float velocity, float inaccuracy, boolean isCrit, LivingEntity target, CallbackInfo ci, @Local Projectile projectile) {
        if (projectile instanceof AbstractArrow abstractArrow) {
            ShortBowItem.applyToArrow(weapon, abstractArrow);
            // 激活弓箭满蓄力特殊效果
            if(abstractArrow instanceof BaseArrowEntity terraArrow){
                WeaponStorage data = shooter.getData(ModAttachmentTypes.WEAPON_STORAGE);
                if(data.bowFullPull){
                    terraArrow.fullPull = true;
                    data.bowFullPull = false;
                }
            }
        }
    }
}
