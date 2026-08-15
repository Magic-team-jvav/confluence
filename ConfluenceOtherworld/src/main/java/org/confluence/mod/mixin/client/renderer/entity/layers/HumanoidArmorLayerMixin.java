package org.confluence.mod.mixin.client.renderer.entity.layers;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static org.confluence.lib.util.LibUtils.getSlotIndex;

@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin<T extends LivingEntity, A extends HumanoidModel<T>> {
    @WrapOperation(method = "renderArmorPiece", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack wrapItem(LivingEntity instance, EquipmentSlot slot, Operation<ItemStack> original, @Share("extra") LocalRef<ExtraInventory> extra) {
        if (instance instanceof AbstractClientPlayer player) {
            int index = getSlotIndex(slot);
            if (index != -1) {
                ExtraInventory inventory = ExtraInventory.of(player);
                extra.set(inventory);
                ItemStack vanityArmor = inventory.getVanityArmor(index, false);
                if (!vanityArmor.isEmpty()) return vanityArmor;
            }
        }
        return original.call(instance, slot);
    }
}
