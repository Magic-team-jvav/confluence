package org.confluence.mod.mixin.server.level;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.confluence.mod.common.character.TerraCharacterRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/// 泰拉式角色难度的作用时段：死亡（掉落、Curios 掉落、掉钱）和复活时复制数据（含 Clone 事件里 Curios 的复制）。
@Mixin(ServerPlayer.class)
public abstract class ServerPlayerCharacterMixin {
    @Inject(method = "die", at = @At("HEAD"))
    private void confluence$characterDieStart(DamageSource source, CallbackInfo ci) {
        TerraCharacterRules.enter((ServerPlayer) (Object) this);
    }

    @Inject(method = "die", at = @At("RETURN"))
    private void confluence$characterDieEnd(DamageSource source, CallbackInfo ci) {
        TerraCharacterRules.exit();
    }

    @Inject(method = "restoreFrom", at = @At("HEAD"))
    private void confluence$characterRestoreStart(ServerPlayer that, boolean keepEverything, CallbackInfo ci) {
        TerraCharacterRules.enter(that);
    }

    @Inject(method = "restoreFrom", at = @At("RETURN"))
    private void confluence$characterRestoreEnd(ServerPlayer that, boolean keepEverything, CallbackInfo ci) {
        TerraCharacterRules.exit();
    }
}
