package org.confluence.mod.mixin.level;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.serialization.*;
import net.minecraft.world.level.levelgen.WorldOptions;
import org.confluence.mod.mixed.IWorldOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.OptionalLong;
import java.util.stream.Stream;

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
    public WorldOptions confluence$copyWithoutSecretFlag() {
        return new WorldOptions(seed, generateStructures, generateBonusChest, legacyCustomOptions);
    }

//    @SuppressWarnings("unchecked")
//    @WrapOperation(method = "lambda$static$1", at = @At(value = "INVOKE", target = "Lcom/mojang/datafixers/Products$P4;apply(Lcom/mojang/datafixers/kinds/Applicative;Lcom/mojang/datafixers/kinds/App;)Lcom/mojang/datafixers/kinds/App;"))
//    private static <R, F extends K1, T1, T2, T3, T4, T5> App<F, R> addSecretFlag(Products.P4<F, T1, T2, T3, T4> instance, Applicative<F, ?> applicative, App<F, Function4<T1, T2, T3, T4, R>> function, Operation<App<F, R>> original) {
//        Products.P5<F, T1, T2, T3, T4, T5> products = instance.and((App<F, T5>) Codec.LONG.lenientOptionalFieldOf("secret_flag", 0L).stable().forGetter((Function<WorldOptions, Long>) options -> IWorldOptions.of(options).confluence$getSecretFlag()));
//        return (App<F, R>) products.apply(applicative, (t1, t2, t3, t4, t5) -> {
//            WorldOptions worldOptions = new WorldOptions((Long) t1, (Boolean) t2, (Boolean) t3);
//            IWorldOptions options = IWorldOptions.of(worldOptions);
//            options.confluence$setLegacyCustomOptions((Optional<String>) t4);
//            options.confluence$withSecretFlag((Long) t5);
//            return worldOptions;
//        });
//    }

    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder;mapCodec(Ljava/util/function/Function;)Lcom/mojang/serialization/MapCodec;"))
    private static MapCodec<WorldOptions> wrapCodec(MapCodec<WorldOptions> original) {
        return new MapCodec<>() {
            private final String secretFlagName = "secret_flag";

            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return Stream.concat(Stream.of(ops.createString(secretFlagName)), original.keys(ops));
            }

            @Override
            public <T> DataResult<WorldOptions> decode(DynamicOps<T> ops, MapLike<T> input) {
                DataResult<WorldOptions> result = original.decode(ops, input);
                result.result().ifPresent(value -> {
                    long secretFlag = ops.getNumberValue(input.get(secretFlagName), 0L).longValue();
                    IWorldOptions.of(value).confluence$withSecretFlag(secretFlag);
                });
                return result;
            }

            @Override
            public <T> RecordBuilder<T> encode(WorldOptions input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                return original.encode(input, ops, prefix).add(secretFlagName, ops.createLong(IWorldOptions.of(input).confluence$getSecretFlag()));
            }

            @Override
            public String toString() {
                return super.toString() + "(SecretFlag)";
            }
        };
    }

    @Inject(method = "withSeed", at = @At("RETURN"))
    private void withSecretFlag1(OptionalLong seed, CallbackInfoReturnable<WorldOptions> cir) {
        IWorldOptions.of(cir.getReturnValue()).confluence$withSecretFlag(confluence$secretFlag);
    }

    @Inject(method = "withStructures", at = @At("RETURN"))
    private void withSecretFlag2(boolean generateStructures, CallbackInfoReturnable<WorldOptions> cir) {
        IWorldOptions.of(cir.getReturnValue()).confluence$withSecretFlag(confluence$secretFlag);
    }

    @Inject(method = "withBonusChest", at = @At("RETURN"))
    private void withSecretFlag3(boolean generateBonusChest, CallbackInfoReturnable<WorldOptions> cir) {
        IWorldOptions.of(cir.getReturnValue()).confluence$withSecretFlag(confluence$secretFlag);
    }
}
