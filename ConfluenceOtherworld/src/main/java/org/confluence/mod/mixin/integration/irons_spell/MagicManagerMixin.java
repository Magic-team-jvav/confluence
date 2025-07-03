package org.confluence.mod.mixin.integration.irons_spell;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.integration.irons_spell.IronSpellHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "io.redspace.ironsspellbooks.capabilities.magic.MagicManager", remap = false)
public abstract class MagicManagerMixin {
    @ModifyExpressionValue(method = "lambda$tick$0(ZLnet/minecraft/world/entity/player/Player;)V", at = @At(value = "INVOKE", target = "Lio/redspace/ironsspellbooks/api/magic/MagicData;getMana()F"))
    private float getMana(float original, @Local(argsOnly = true) Player player) {
        return IronSpellHelper.getMana(original, player);
    }
}
