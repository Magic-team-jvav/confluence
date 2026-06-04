package org.confluence.mod.mixin.integration.terracurio;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.init.ModTags;
import org.confluence.terra_curio.client.handler.TCClientPacketHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = TCClientPacketHandler.class, remap = false)
public abstract class TCClientPacketHandlerMixin {
    @ModifyExpressionValue(method = "applyAutoAttack", at = @At(value = "INVOKE", target = "Lorg/confluence/terra_curio/client/handler/TCClientPacketHandler;couldAutoAttack()Z"))
    private static boolean extraAutoAttack(boolean original, @Local(name = "itemStack") ItemStack stack) {
        return original && !stack.is(ModTags.Items.AUTO_ATTACK_BLACKLIST) && stack.is(ModTags.Items.AUTO_ATTACK_WHITELIST);
    }
}
