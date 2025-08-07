package org.confluence.mod.mixin.integration.geckolib;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import software.bernie.geckolib.util.InternalUtil;

import static org.confluence.lib.util.LibUtils.getSlotIndex;

@Mixin(value = InternalUtil.class, remap = false)
public abstract class InternalUtilMixin {
    @ModifyVariable(method = "tryRenderGeoArmorPiece", at = @At("HEAD"), argsOnly = true)
    private static <T extends LivingEntity> ItemStack wrapItem(ItemStack stack, @Local(argsOnly = true) T entity, @Local(argsOnly = true) EquipmentSlot slot) {
        if (entity instanceof AbstractClientPlayer player) {
            int index = getSlotIndex(slot);
            if (index != -1) {
                ItemStack vanityArmor = ExtraInventory.of(player).getVanityArmor(index);
                if (!vanityArmor.isEmpty()) return vanityArmor;
            }
        }
        return stack;
    }
}
