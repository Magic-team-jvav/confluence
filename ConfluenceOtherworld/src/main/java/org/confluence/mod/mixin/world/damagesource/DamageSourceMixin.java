package org.confluence.mod.mixin.world.damagesource;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.entity.monster.difficulty.CreatureDifficultyScaling;
import org.confluence.mod.mixed.Immunity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DamageSource.class)
public abstract class DamageSourceMixin implements SelfGetter<DamageSource> {
    /// 本体敌怪已按统一难度表结算，禁止原版对玩家再次应用简单或困难伤害倍率。
    @Inject(method = "scalesWithDifficulty", at = @At("HEAD"), cancellable = true)
    private void creatureDifficulty(CallbackInfoReturnable<Boolean> cir) {
        var owner = LibEntityUtils.getOwner(confluence$self());
        if (owner != null && CreatureDifficultyScaling.isManaged(owner)) cir.setReturnValue(false);
    }

    @Inject(method = "is(Lnet/minecraft/tags/TagKey;)Z", at = @At("HEAD"), cancellable = true)
    private void isTag(TagKey<DamageType> damageTypeKey, CallbackInfoReturnable<Boolean> cir) {
        if (damageTypeKey == DamageTypeTags.BYPASSES_COOLDOWN && Immunity.getCause(confluence$self()) != null) {
            cir.setReturnValue(true);
        }
    }
}
