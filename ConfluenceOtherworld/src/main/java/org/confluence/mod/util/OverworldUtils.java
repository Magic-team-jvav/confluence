package org.confluence.mod.util;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.mod.common.init.ModBiomes;
import org.confluence.mod.common.init.ModFeatures;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.worldgen.secret_seed.NotTheBees;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 用于维度补丁模组
 */
public final class OverworldUtils {
    public static ResourceKey<Level> dimension() {
        return Level.OVERWORLD;
    }

    public static void replaceBiome(MultiNoiseBiomeSource biomeSource, int x, int y, int z, CallbackInfoReturnable<Holder<Biome>> cir, Supplier<List<Holder<Biome>>> jungleGetter, Supplier<Pair<Holder<Biome>, Holder<Biome>>> biomePairGetter, Function<RegistryAccess, Holder<Biome>> protectionFactory) {
        Holder<Biome> replaced = cir.getReturnValue();
        if (replaced != null) {
            if (ModSecretSeeds.NOT_THE_BEES.match()) {
                List<Holder<Biome>> jungle = jungleGetter.get();
                if (!jungle.isEmpty()) {
                    replaced = NotTheBees.replaceBiome(x, y, z, replaced, jungle);
                }
            } else {
                Pair<Holder<Biome>, Holder<Biome>> pair = biomePairGetter.get();
                if (pair != null && replaced == pair.getFirst()) {
                    replaced = pair.getSecond();
                }
            }
            MinecraftServer currentServer = ServerLifecycleHooks.getCurrentServer();
            if (currentServer != null && (replaced.is(ModBiomes.THE_CORRUPTION) || replaced.is(ModBiomes.THE_CRIMSON))) {
                BlockPos spawnPos = currentServer.getWorldData().overworldData().getSpawnPos();
                if (Math.abs((spawnPos.getX() >> 2) - x) <= 50 || Math.abs((spawnPos.getZ() >> 2) - z) <= 50) {
                    replaced = protectionFactory.apply(currentServer.registryAccess());
                }
            }
            cir.setReturnValue(replaced);
        }
    }

    public static void replaceTree(FeaturePlaceContext<TreeConfiguration> context, CallbackInfoReturnable<Boolean> cir) {
        WorldGenLevel level = context.level();
        if (!(level instanceof WorldGenRegion)) return;
        BlockPos origin = context.origin();
        if (level.getBiome(origin).is(ModTags.Biomes.VANITY_TREES_REPLACEABLE)) {
            RandomSource random = context.random();
            if (context.config().trunkProvider.getState(random, origin).is(Blocks.CHERRY_LOG)) return;
            float v = ModSecretSeeds.DRUNK_WORLD.match() ? 0.02F : 0.01F;
            if (random.nextFloat() < v) {
                if (random.nextFloat() < 0.75F) {
                    boolean placed = level.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE)
                            .getHolder(ModFeatures.Configured.YELLOW_WILLOW_TREE).orElseThrow().value()
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

    public static boolean replaceLogBoulder(WorldGenLevel instance, BlockPos blockPos, BlockState blockState, int i, Operation<Boolean> original) {
        if (ModSecretSeeds.NO_TRAPS.match(instance.getLevel().getServer()) && blockState.is(Blocks.OAK_LOG) && blockState.getValue(BlockStateProperties.AXIS) == Direction.Axis.Y) {
            if (instance.getRandom().nextFloat() < 0.2F) {
                blockState = FunctionalBlocks.OAK_LOG_BOULDER.get().defaultBlockState();
            }
        }
        return original.call(instance, blockPos, blockState, i);
    }

    public static int getUltraY() {
        return 320;
    }

    public static int getSpaceY() {
        return 260;
    }

    public static int getSurfaceY() {
        return 40;
    }

    public static int getUndergroundY() {
        return 0;
    }

    public static int getCaveY() {
        return -64;
    }
}
