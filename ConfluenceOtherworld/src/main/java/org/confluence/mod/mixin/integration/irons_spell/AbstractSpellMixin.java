package org.confluence.mod.mixin.integration.irons_spell;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.integration.irons_spell.IronSpellHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "io.redspace.ironsspellbooks.api.spells.AbstractSpell", remap = false)
public abstract class AbstractSpellMixin {
    @ModifyExpressionValue(method = "canBeCastedBy", at = @At(value = "INVOKE", target = "Lio/redspace/ironsspellbooks/api/magic/MagicData;getMana()F"))
    private float getMana(float original, @Local(argsOnly = true) Player player) {
        return IronSpellHelper.getMana(original, player);
    }

    @ModifyReceiver(method = "castSpell", at = @At(value = "INVOKE", target = "Lio/redspace/ironsspellbooks/api/magic/MagicData;setMana(F)V"))
    private MagicData consumeMana(MagicData instance, float newMana, @Local(argsOnly = true) ServerPlayer serverPlayer) {
        IronSpellHelper.extractMana(instance.getMana() - newMana, serverPlayer);
        return instance;
    }

    @ModifyReturnValue(method = "getManaCost", at = @At("RETURN"))
    private int getManaCost(int original) {
        if (CommonConfigs.IRONS_SPELL_COMPATIBILITY.get()) {
            return (int) (original * IronSpellHelper.toConfluence());
        }
        return original;
    }
}
