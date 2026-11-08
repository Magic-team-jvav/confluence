package org.confluence.mod.mixin.world.level;

import net.minecraft.world.level.GameRules;
import org.confluence.mod.common.character.TerraCharacterRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/// 泰拉式角色难度：玩家死亡/复活结算期间，keepInventory 按角色难度取值（只在该结算的线程和时段内）。
@Mixin(GameRules.class)
public abstract class GameRulesCharacterMixin {
    @Inject(method = "getBoolean", at = @At("HEAD"), cancellable = true)
    private void confluence$characterKeepInventory(GameRules.Key<GameRules.BooleanValue> key, CallbackInfoReturnable<Boolean> cir) {
        if (key != GameRules.RULE_KEEPINVENTORY) return;
        Boolean keep = TerraCharacterRules.keepInventoryOverride();
        if (keep != null) cir.setReturnValue(keep);
    }
}
