package org.confluence.mod.mixin.client.renderer;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.RegistryAwareItemModelShaper;
import org.confluence.mod.common.data.saved.GlobalCloakData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = RegistryAwareItemModelShaper.class, remap = false)
public abstract class RegistryAwareItemModelShaperMixin {
    @ModifyVariable(method = "getItemModel(Lnet/minecraft/world/item/Item;)Lnet/minecraft/client/resources/model/BakedModel;", at = @At("HEAD"), argsOnly = true)
    private Item getModel(Item value) {
        return GlobalCloakData.INSTANCE.getTarget(value);
    }
}
