package org.confluence.mod.mixin.world.level.levelgen;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.confluence.lib.common.worldgen.biome.DynamicBiomeUtils;
import org.confluence.lib.mixed.ILevelChunkSection;
import org.confluence.mod.mixed.INoiseBasedChunkGenerator;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NoiseBasedChunkGenerator.class)
public abstract class NoiseBasedChunkGeneratorMixin implements INoiseBasedChunkGenerator {
    /// 由 [org.confluence.mod.common.worldgen.biome.injector.ConfluenceBiomeInjector#install] 在服务器启动时写入，之后只读。
    @Unique
    @Nullable
    private SurfaceRules.RuleSource confluence$surfaceRules;

    @Override
    public void confluence$setSurfaceRules(SurfaceRules.RuleSource rules) {
        this.confluence$surfaceRules = rules;
    }

    @Inject(method = "doCreateBiomes", at = @At("RETURN"))
    private void doCreateBiomes(CallbackInfo ci, @Local(argsOnly = true) StructureManager structureManager, @Local(argsOnly = true) ChunkAccess chunk) {
        HolderLookup.RegistryLookup<Biome> lookup = structureManager.registryAccess().lookupOrThrow(Registries.BIOME);
        for (LevelChunkSection section : chunk.getSections()) {
            ILevelChunkSection.of(section).confluence$setBackupBiome(DynamicBiomeUtils.judgeBackupBiome(section, lookup));
        }
    }

    /// 建面时的规则替换点：原版 7 参 `buildSurface` 里唯一一处 `settings.surfaceRule()`。
    @WrapOperation(method = "buildSurface(Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/world/level/levelgen/WorldGenerationContext;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/biome/BiomeManager;Lnet/minecraft/core/Registry;Lnet/minecraft/world/level/levelgen/blending/Blender;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/NoiseGeneratorSettings;surfaceRule()Lnet/minecraft/world/level/levelgen/SurfaceRules$RuleSource;"))
    private SurfaceRules.RuleSource confluence$replaceBuildSurfaceRules(NoiseGeneratorSettings instance, Operation<SurfaceRules.RuleSource> original) {
        return confluence$resolveRules(instance, original);
    }

    /// 挖洞时的规则替换点：`applyCarvers` 会把同一份 `surfaceRule()` 塞进 `CarvingContext`，
    /// 供 `SurfaceSystem#topMaterial` 使用。一并替换，保证挖洞与建面的表层材料判定一致。
    @WrapOperation(method = "applyCarvers", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/NoiseGeneratorSettings;surfaceRule()Lnet/minecraft/world/level/levelgen/SurfaceRules$RuleSource;"))
    private SurfaceRules.RuleSource confluence$replaceCarverRules(NoiseGeneratorSettings instance, Operation<SurfaceRules.RuleSource> original) {
        return confluence$resolveRules(instance, original);
    }

    @Unique
    private SurfaceRules.RuleSource confluence$resolveRules(NoiseGeneratorSettings instance, Operation<SurfaceRules.RuleSource> original) {
        SurfaceRules.RuleSource composed = this.confluence$surfaceRules;
        if (composed == null) {
            return original.call(instance);
        }
        return composed;
    }
}
