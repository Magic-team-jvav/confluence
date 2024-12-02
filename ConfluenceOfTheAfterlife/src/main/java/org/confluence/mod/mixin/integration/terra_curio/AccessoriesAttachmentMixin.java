package org.confluence.mod.mixin.integration.terra_curio;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.component.prefix.PrefixComponent;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.util.PrefixUtils;
import org.confluence.terra_curio.api.primitive.FloatValue;
import org.confluence.terra_curio.api.primitive.IntegerValue;
import org.confluence.terra_curio.api.primitive.PrimitiveValue;
import org.confluence.terra_curio.api.primitive.ValueType;
import org.confluence.terra_curio.common.attachment.AccessoriesAttachment;
import org.confluence.terra_curio.common.component.AccessoriesComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.Iterator;

@Mixin(value = AccessoriesAttachment.class, remap = false)
public abstract class AccessoriesAttachmentMixin {
    @Shadow
    protected abstract <T, V extends PrimitiveValue<T>> void combineValue(ValueType<T, V> type, V value);

    @Inject(method = "lambda$flushAbility$4", at = @At(value = "INVOKE", target = "Lorg/confluence/terra_curio/util/TCUtils;forConfluence$Inject()V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void additionalMana(LivingEntity living, ICuriosItemHandler handler, CallbackInfo ci, Iterator var3, ICurioStacksHandler curioStacksHandler, IDynamicStackHandler stackHandler, int i, ItemStack stack, AccessoriesComponent component, Item item) {
        PrefixComponent prefix = PrefixUtils.getPrefix(stack);
        if (prefix == null) return;
        combineValue(AccessoryItems.ADDITIONAL$MANA, new IntegerValue(prefix.additionalMana()));
        combineValue(AccessoryItems.MANA$USE$REDUCE, new FloatValue(-prefix.manaCost()));
    }
}
