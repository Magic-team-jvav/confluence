package org.confluence.mod.mixin.integration.terra_curio;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.network.s2c.ExtraInventoryStackPacketS2C;
import org.confluence.terra_curio.common.item.DemonHeart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

@Mixin(value = DemonHeart.class, remap = false)
public abstract class DemonHeartMixin {
    @Inject(method = "lambda$use$0", at = @At(value = "INVOKE", target = "Lorg/confluence/lib/util/LibUtils;forMixin$ModifyExpression(Ljava/lang/Object;)Ljava/lang/Object;"))
    private static void update(ItemStack itemStack, ServerPlayer serverPlayer, ICuriosItemHandler iCuriosItemHandler, CallbackInfo ci, @Local ICurioStacksHandler iCurioStacksHandler) {
        int slots = iCurioStacksHandler.getSlots();
        serverPlayer.getData(ModAttachmentTypes.EXTRA_INVENTORY).updateAccessorySize(slots);
        ExtraInventoryStackPacketS2C.sendToPlayersTrackingEntityAndSelf(serverPlayer, serverPlayer, slots, slots - 1, ItemStack.EMPTY);
    }
}
