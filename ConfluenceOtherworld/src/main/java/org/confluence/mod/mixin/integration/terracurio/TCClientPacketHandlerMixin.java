package org.confluence.mod.mixin.integration.terracurio;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.integration.sodium.dynamiclights.SodiumDynamicLightsHelper;
import org.confluence.terra_curio.client.handler.TCClientPacketHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = TCClientPacketHandler.class, remap = false)
public abstract class TCClientPacketHandlerMixin {
    @ModifyReturnValue(method = "getLuminance", at = @At("RETURN"))
    private static int extraLuminance(int original, @Local(argsOnly = true) Entity entity) {
        return SodiumDynamicLightsHelper.getLuminance(entity, original);
    }

    @ModifyExpressionValue(method = "applyAutoAttack", at = @At(value = "INVOKE", target = "Lorg/confluence/terra_curio/client/handler/TCClientPacketHandler;couldAutoAttack()Z"))
    private static boolean extraAutoAttack(boolean original, @Local(name = "itemStack") ItemStack stack) {
        if (!(stack.getItem() instanceof SwordItem) || stack.is(ModTags.Items.AUTO_ATTACK_BLACKLIST))
            return false;
        return CommonConfigs.AUTO_SWING_ALL_SWORDS.get() || stack.is(ModTags.Items.AUTO_ATTACK_WHITELIST) || original;
    }
}
