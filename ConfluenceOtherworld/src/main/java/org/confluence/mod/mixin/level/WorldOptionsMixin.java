package org.confluence.mod.mixin.level;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.WorldOptions;
import org.confluence.mod.mixed.IWorldOptions;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Function;

@Mixin(WorldOptions.class)
public abstract class WorldOptionsMixin implements IWorldOptions {
    @Shadow
    @Final
    private long seed;
    @Shadow
    @Final
    private boolean generateStructures;
    @Shadow
    @Final
    private boolean generateBonusChest;
    @Mutable
    @Shadow
    @Final
    private Optional<String> legacyCustomOptions;
    @Unique
    private long confluence$secretFlag = 0L;

    @Override
    public void confluence$withSecretFlag(long flag) {
        this.confluence$secretFlag |= flag;
    }

    @Override
    public long confluence$getSecretFlag() {
        return confluence$secretFlag;
    }

    @Override
    public void confluence$setLegacyCustomOptions(Optional<String> legacyCustomOptions) {
        this.legacyCustomOptions = legacyCustomOptions;
    }

    @Override
    public WorldOptions confluence$copyWithoutSecretFlag() {
        WorldOptions worldOptions = new WorldOptions(seed, generateStructures, generateBonusChest);
        ((IWorldOptions) worldOptions).confluence$setLegacyCustomOptions(legacyCustomOptions);
        return worldOptions;
    }

    @SuppressWarnings("unchecked")
    @WrapOperation(method = "lambda$static$1", at = @At(value = "INVOKE", target = "Lcom/mojang/datafixers/Products$P4;apply(Lcom/mojang/datafixers/kinds/Applicative;Lcom/mojang/datafixers/kinds/App;)Lcom/mojang/datafixers/kinds/App;"))
    private static <R, F extends K1, T1, T2, T3, T4, T5> App<F, R> addSecretFlag(Products.P4<F, T1, T2, T3, T4> instance, Applicative<F, ?> applicative, App<F, Function4<T1, T2, T3, T4, R>> function, Operation<App<F, R>> original) {
        Products.P5<F, T1, T2, T3, T4, T5> products = instance.and((App<F, T5>) Codec.LONG.lenientOptionalFieldOf("secret_flag", 0L).stable().forGetter((Function<WorldOptions, Long>) options -> ((IWorldOptions) options).confluence$getSecretFlag()));
        return (App<F, R>) products.apply(applicative, (t1, t2, t3, t4, t5) -> {
            WorldOptions worldOptions = new WorldOptions((Long) t1, (Boolean) t2, (Boolean) t3);
            IWorldOptions options = (IWorldOptions) worldOptions;
            options.confluence$setLegacyCustomOptions((Optional<String>) t4);
            options.confluence$withSecretFlag((Long) t5);
            return worldOptions;
        });
    }

    @Inject(method = "withSeed", at = @At("RETURN"))
    private void withSecretFlag1(OptionalLong seed, CallbackInfoReturnable<WorldOptions> cir) {
        ((IWorldOptions) cir.getReturnValue()).confluence$withSecretFlag(confluence$secretFlag);
    }

    @Inject(method = "withStructures", at = @At("RETURN"))
    private void withSecretFlag2(boolean generateStructures, CallbackInfoReturnable<WorldOptions> cir) {
        ((IWorldOptions) cir.getReturnValue()).confluence$withSecretFlag(confluence$secretFlag);
    }

    @Inject(method = "withBonusChest", at = @At("RETURN"))
    private void withSecretFlag3(boolean generateBonusChest, CallbackInfoReturnable<WorldOptions> cir) {
        ((IWorldOptions) cir.getReturnValue()).confluence$withSecretFlag(confluence$secretFlag);
    }
}
