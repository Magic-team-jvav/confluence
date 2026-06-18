package org.confluence.mod.mixin.world.entity.projectile;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.common.block.functional.DartTrapBlock;
import org.confluence.mod.common.init.item.VanityArmorItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Arrow.class)
public abstract class ArrowMixin implements SelfGetter<Arrow> {
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
