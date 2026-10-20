package org.confluence.mod.mixin.integration.terracurio;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.item.soulcerer.SoulKnifeItem;
import org.confluence.terra_curio.client.handler.TCClientPacketHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = TCClientPacketHandler.class, remap = false)
public abstract class TCClientPacketHandlerMixin {
    @ModifyExpressionValue(method = "applyAutoAttack", at = @At(value = "INVOKE", target = "Lorg/confluence/terra_curio/client/handler/TCClientPacketHandler;couldAutoAttack()Z"))
    private static boolean extraAutoAttack(boolean original, @Local(name = "itemStack") ItemStack itemStack) {
        /// 祈灵之刃这类魂师副武器不是 SwordItem，但也需要参与自动挥舞判定
        if (!(itemStack.getItem() instanceof SwordItem) && !(itemStack.getItem() instanceof SoulKnifeItem)) {
            return false;
        }
        if (itemStack.is(ModTags.Items.AUTO_ATTACK_BLACKLIST)) {
            return false;
        }
        return CommonConfigs.AUTO_SWING_ALL_SWORDS.get() || itemStack.is(ModTags.Items.AUTO_ATTACK_WHITELIST) || original;
    }
}
