package org.confluence.mod.mixin.forge.client.model;

import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.ForgeItemModelShaper;
import org.confluence.mod.common.data.saved.GlobalCloakData;
import org.mesdag.portlib.diff.Diff;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Diff(reason = "Rename")
@Mixin(value = ForgeItemModelShaper.class, remap = false, priority = 1100)
public abstract class ForgeItemModelShaperMixin {
    @ModifyVariable(method = "getItemModel(Lnet/minecraft/world/item/Item;)Lnet/minecraft/client/resources/model/BakedModel;", at = @At("HEAD"), argsOnly = true, remap = true)
    private Item getModel(Item value) {
        return GlobalCloakData.INSTANCE.getTarget(value);
    }
}
