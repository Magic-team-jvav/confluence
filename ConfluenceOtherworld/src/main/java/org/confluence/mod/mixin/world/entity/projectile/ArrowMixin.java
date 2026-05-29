package org.confluence.mod.mixin.world.entity.projectile;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.api.ITerraArrowProjectileWeaponItem;
import org.confluence.mod.common.block.functional.DartTrapBlock;
import org.confluence.mod.common.init.item.VanityArmorItems;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

@Mixin(Arrow.class)
public abstract class ArrowMixin implements SelfGetter<Arrow> {
    @Inject(method = "doPostHurtEffects", at = @At("HEAD"))
    public void doPostHurtEffects(LivingEntity living, CallbackInfo ci) {
        @Nullable ItemStack weapon = confluence$self().getWeaponItem();
        if (weapon == null || !(weapon.getItem() instanceof ITerraArrowProjectileWeaponItem<?> bow)) {
            return;
        }
        try {
            var onHitEffects = bow.getArrowModifier().onHitEffects;
            if (onHitEffects != null) {
                onHitEffects.forEach(effect -> effect.applyAll((LivingEntity) confluence$self().getOwner(), living));
            }
        } catch (Exception ignored) {}
    }

    @WrapOperation(method = "doPostHurtEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean halveEffectIfHasSweater(LivingEntity living, MobEffectInstance effect, Entity entity, Operation<Boolean> original) {
        Component name = ((Arrow) (Object) this).getCustomName();
        if (name != null && name.equals(DartTrapBlock.NAME)) {
            if (living.getItemBySlot(EquipmentSlot.CHEST).is(VanityArmorItems.DEAD_MANS_SWEATER.get())) {
                effect = new MobEffectInstance(
                        effect.getEffect(),
                        effect.getDuration() / 2,
                        effect.getAmplifier(),
                        effect.isAmbient(),
                        effect.isVisible(),
                        effect.showIcon()
                );
            }
        }
        return original.call(living, effect, entity);
    }
}