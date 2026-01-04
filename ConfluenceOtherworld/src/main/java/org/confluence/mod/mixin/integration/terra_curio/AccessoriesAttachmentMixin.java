package org.confluence.mod.mixin.integration.terra_curio;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.component.prefix.PrefixComponent;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.integration.terra_curio.WrappedAccessoriesAttachment;
import org.confluence.mod.util.PrefixUtils;
import org.confluence.terra_curio.api.primitive.FloatValue;
import org.confluence.terra_curio.api.primitive.IntegerValue;
import org.confluence.terra_curio.common.attachment.AccessoriesAttachment;
import org.confluence.terra_curio.common.attachment.PrimitiveValueHolder;
import org.confluence.terra_curio.common.init.TCAttachments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

@Mixin(value = AccessoriesAttachment.class, remap = false)
public abstract class AccessoriesAttachmentMixin extends PrimitiveValueHolder {
    @Inject(method = "lambda$flushAbility$4", at = @At(value = "INVOKE", target = "Lorg/confluence/lib/util/LibUtils;forMixin$Inject()V"))
    private void additionalMana(LivingEntity living, ICuriosItemHandler handler, CallbackInfo ci, @Local(name = "stack") ItemStack stack) {
        PrefixComponent prefix = PrefixUtils.getPrefix(stack);
        if (prefix == null) return;
        combineValue(AccessoryItems.ADDITIONAL$MANA, new IntegerValue(prefix.additionalMana()));
        combineValue(AccessoryItems.MANA$USE$REDUCE, new FloatValue(-prefix.manaCost()));
    }

    @ModifyReturnValue(method = "of", at = @At("RETURN"))
    private static AccessoriesAttachment wrap(AccessoriesAttachment original, @Local(argsOnly = true) Entity entity) {
        if (entity instanceof Player player) {
            if (original instanceof WrappedAccessoriesAttachment) {
                return original;
            }
            AccessoriesAttachment wrapped = new WrappedAccessoriesAttachment(player, original);
            entity.setData(TCAttachments.ACCESSORIES, wrapped);
            return wrapped;
        }
        return original;
    }
}
