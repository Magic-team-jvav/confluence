package org.confluence.mod.mixin.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.item.bow.TerraBowItem;
import org.confluence.terraentity.mixinauxiliary.SelfGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Arrow.class)
public class ArrowMixin implements SelfGetter<Arrow> {

    @Inject(method = "doPostHurtEffects", at = @At("HEAD"))
    public void doPostHurtEffects(LivingEntity living, CallbackInfo ci) {
        ItemStack weapon = te$getSelf().getWeaponItem();
        if(weapon !=null && weapon.getItem() instanceof TerraBowItem bow){
            try {
                bow.arrowModifier.onHitEffect.forEach(effect -> effect.accept((LivingEntity) te$getSelf().getOwner(),living));
            }catch (Exception ignored){
            }
        }
    }
}
