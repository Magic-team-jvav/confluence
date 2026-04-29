package org.confluence.mod.mixin.level;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.serialization.*;
import net.minecraft.world.level.levelgen.WorldOptions;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.mixed.IWorldOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
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
    @Unique
    private int confluence$version = CURRENT_VERSION;

    @Override
    public void confluence$resetSecretFlag() {
        this.confluence$secretFlag = 0;
    }

    @Override
    public void confluence$withSecretFlag(long flag) {
        this.confluence$secretFlag |= flag;
    }

    @Override
    public long confluence$getSecretFlag() {
        return confluence$secretFlag;
    }

    @Override
    public void confluence$setVersion(int version) {
        this.confluence$version = version;
    }

    @Override
    public int confluence$getVersion() {
        return confluence$version;
    }

    @Override
    public WorldOptions confluence$copyWithoutSecretFlag() {
        return new WorldOptions(seed, generateStructures, generateBonusChest, legacyCustomOptions);
    }

    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder;mapCodec(Ljava/util/function/Function;)Lcom/mojang/serialization/MapCodec;"))
    private static MapCodec<WorldOptions> wrapCodec(MapCodec<WorldOptions> original) {
        return new MapCodec<>() {
            private static final String secretFlagName = "secret_flag";
            private static final String confluenceVersionName = "confluence_version";

            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return Stream.concat(Stream.of(
                        ops.createString(secretFlagName),
                        ops.createString(confluenceVersionName)
                ), original.keys(ops));
            }

            @Override
            public <T> DataResult<WorldOptions> decode(DynamicOps<T> ops, MapLike<T> input) {
                DataResult<WorldOptions> result = original.decode(ops, input);
                result.result().ifPresent(value -> {
                    long secretFlag = ops.getNumberValue(input.get(secretFlagName), 0L).longValue();
                    int version = ops.getNumberValue(input.get(confluenceVersionName), 0).intValue();
                    if (version < CURRENT_VERSION) {
                        secretFlag = ModSecretSeeds.fixWorldOptions(secretFlag, version);
                        version = CURRENT_VERSION;
                    }
                    IWorldOptions options = IWorldOptions.of(value);
                    options.confluence$withSecretFlag(secretFlag);
                    options.confluence$setVersion(version);
                });
                return result;
            }

            @Override
            public <T> RecordBuilder<T> encode(WorldOptions input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                IWorldOptions options = IWorldOptions.of(input);
                return original.encode(input, ops, prefix)
                        .add(secretFlagName, ops.createLong(options.confluence$getSecretFlag()))
                        .add(confluenceVersionName, ops.createInt(options.confluence$getVersion()));
            }

            @Override
            public String toString() {
                return super.toString() + "(SecretFlag)";
            }
        };
    }

    @Inject(method = "withSeed", at = @At("RETURN"))
    private void withSecretFlag1(CallbackInfoReturnable<WorldOptions> cir) {
        IWorldOptions.of(cir.getReturnValue()).confluence$withSecretFlag(confluence$secretFlag);
    }

    @Inject(method = "withStructures", at = @At("RETURN"))
    private void withSecretFlag2(CallbackInfoReturnable<WorldOptions> cir) {
        IWorldOptions.of(cir.getReturnValue()).confluence$withSecretFlag(confluence$secretFlag);
    }

    @Inject(method = "withBonusChest", at = @At("RETURN"))
    private void withSecretFlag3(CallbackInfoReturnable<WorldOptions> cir) {
        IWorldOptions.of(cir.getReturnValue()).confluence$withSecretFlag(confluence$secretFlag);
    }
}
