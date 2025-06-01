package org.confluence.mod.mixin.level;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import org.confluence.mod.common.init.ModFeatures;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TreeFeature.class)
public abstract class TreeFeatureMixin {
    @Inject(method = "place", at = @At("HEAD"), cancellable = true)
    private void replace(FeaturePlaceContext<TreeConfiguration> context, CallbackInfoReturnable<Boolean> cir) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        if (level.getBiome(origin).is(ModTags.Biomes.VANITY_TREES_REPLACEABLE)) {
            RandomSource random = context.random();
            if (context.config().trunkProvider.getState(random, origin).is(Blocks.CHERRY_LOG)) return;
            float v = ModSecretSeeds.DRUNK_WORLD.match() ? 0.02F : 0.01F;
            if (random.nextFloat() < v) {
                if (random.nextFloat() < 0.75F) {
                    boolean placed = level.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE)
                            .getHolder(ModFeatures.Configured.CONFIGURED_YELLOW_WILLOW).orElseThrow().value()
                            .place(level, context.chunkGenerator(), random, origin);
                    if (placed) cir.setReturnValue(true);
                } else {
                    boolean placed = level.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE)
                            .getHolder(TreeFeatures.CHERRY).orElseThrow().value()
                            .place(level, context.chunkGenerator(), random, origin);
                    if (placed) cir.setReturnValue(true);
                }
            }
        }
    }

    @WrapOperation(method = "lambda$place$6", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/WorldGenLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private static boolean logBoulder(WorldGenLevel instance, BlockPos blockPos, BlockState blockState, int i, Operation<Boolean> original) {
        if (ModSecretSeeds.NO_TRAPS.match(instance.getLevel().getServer()) && blockState.is(Blocks.OAK_LOG) && blockState.getValue(BlockStateProperties.AXIS) == Direction.Axis.Y) {
            if (instance.getRandom().nextFloat() < 0.2F) {
                blockState = FunctionalBlocks.OAK_LOG_BOULDER.get().defaultBlockState();
            }
        }
        return original.call(instance, blockPos, blockState, i);
    }
}
