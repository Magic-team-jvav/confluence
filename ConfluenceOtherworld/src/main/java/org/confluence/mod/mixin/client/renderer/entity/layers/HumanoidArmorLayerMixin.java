package org.confluence.mod.mixin.client.renderer.entity.layers;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.util.ClientUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static org.confluence.lib.util.LibUtils.getSlotIndex;

@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin<T extends LivingEntity, A extends HumanoidModel<T>> {
    @WrapOperation(method = "renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
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

    @WrapOperation(method = "renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/extensions/common/IClientItemExtensions;getArmorLayerTintColor(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ArmorMaterial$Layer;II)I", remap = false))
    private int dyeColor(IClientItemExtensions instance, ItemStack stack, LivingEntity entity, ArmorMaterial.Layer layer, int layerIdx, int fallbackColor, Operation<Integer> original, @Local(argsOnly = true) EquipmentSlot slot, @Share("extra") LocalRef<ExtraInventory> extra) {
        if (entity instanceof AbstractClientPlayer player) {
            int argb = ClientUtils.getVanityDyeARGB(extra.get(), getSlotIndex(slot), player);
            if (argb != -1) return argb;
        }
        return original.call(instance, stack, entity, layer, layerIdx, fallbackColor);
    }

    @ModifyExpressionValue(method = "renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/ClientHooks;getArmorTexture(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ArmorMaterial$Layer;ZLnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/resources/ResourceLocation;", remap = false))
    private ResourceLocation withGray(ResourceLocation original, @Local(argsOnly = true) LivingEntity entity, @Local(argsOnly = true) EquipmentSlot slot, @Share("extra") LocalRef<ExtraInventory> extra) {
        if (entity instanceof AbstractClientPlayer) {
            if (!extra.get().getVanityArmor(getSlotIndex(slot), true).isEmpty()) {
                return ClientUtils.getGrayTexture(original);
            }
        }
        return original;
    }
}
