package org.confluence.mod.util;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.neoforged.neoforge.common.Tags;
import org.confluence.mod.common.init.ModBiomes;
import org.confluence.mod.common.init.ModFeatures;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.worldgen.secret_seed.NotTheBees;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Supplier;

/// 用于维度补丁模组
public final class OverworldUtils {
    public static ResourceKey<Level> dimension() {
        return Level.OVERWORLD;
    }

    public static ResourceKey<Level> underworld() {
        return Level.NETHER;
    }

    private static boolean uninitialized = true;
    private static MinecraftServer server;
    private static boolean notTheBees;
    private static Holder<Biome> plains;
    private static ConfiguredFeature<?, ?> yellowWillowTree;
    private static ConfiguredFeature<?, ?> cherry;
    private static ConfiguredFeature<?, ?> pineTree;

    @ApiStatus.Internal
    public static void open(MinecraftServer server) {
        uninitialized = false;
        OverworldUtils.server = server;
        notTheBees = ModSecretSeeds.NOT_THE_BEES.match(server);
        plains = server.registryAccess().holderOrThrow(Biomes.PLAINS);
        Registry<ConfiguredFeature<?, ?>> registry = server.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);
        yellowWillowTree = registry.getOrThrow(ModFeatures.Configured.YELLOW_WILLOW_TREE);
        cherry = registry.getOrThrow(TreeFeatures.CHERRY);
        pineTree = registry.getOrThrow(ModFeatures.Configured.PINE_TREE);
    }

    @ApiStatus.Internal
    public static void close() {
        uninitialized = true;
        server = null;
        notTheBees = false;
        plains = null;
        yellowWillowTree = null;
        cherry = null;
        pineTree = null;
    }

    @ApiStatus.Internal
    public static void replaceBiome(
            MultiNoiseBiomeSource biomeSource,
            int x,
            int y,
            int z,
            CallbackInfoReturnable<Holder<Biome>> cir,
            Supplier<List<Holder<Biome>>> jungleGetter,
            Supplier<Pair<Holder<Biome>, Holder<Biome>>> biomePairGetter
    ) {
        if (uninitialized) return;
        Holder<Biome> replaced = cir.getReturnValue();
        if (replaced == null) return;
        if (notTheBees) {
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
        if (replaced.is(ModBiomes.THE_CORRUPTION) || replaced.is(ModBiomes.THE_CRIMSON)) {
            BlockPos spawnPos = server.getWorldData().overworldData().getSpawnPos();
            if (Mth.lengthSquared(spawnPos.getX() - x, spawnPos.getZ() - z) <= 128 * 128) {
                replaced = plains;
            }
        }
        cir.setReturnValue(replaced);
    }

    @ApiStatus.Internal
    public static void replaceTree(FeaturePlaceContext<TreeConfiguration> context, CallbackInfoReturnable<Boolean> cir) {
        if (uninitialized) return;
        WorldGenLevel level = context.level();
        if (!(level instanceof WorldGenRegion)) return;
        BlockPos origin = context.origin();
        Holder<Biome> biome = level.getBiome(origin);
        if (biome.is(ModTags.Biomes.VANITY_TREES_REPLACEABLE)) {
            RandomSource random = context.random();
            if (context.config().trunkProvider.getState(random, origin).is(Blocks.CHERRY_LOG)) {
                return;
            }
            float v = ModSecretSeeds.DRUNK_WORLD.match() ? 0.02F : 0.01F;
            if (random.nextFloat() < v) {
                if (random.nextFloat() < 0.75F) {
                    boolean placed = yellowWillowTree.place(level, context.chunkGenerator(), random, origin);
                    if (placed) cir.setReturnValue(true);
                } else {
                    boolean placed = cherry.place(level, context.chunkGenerator(), random, origin);
                    if (placed) cir.setReturnValue(true);
                }
            }
        }
    }

    @ApiStatus.Internal
    public static boolean replaceLogBoulder(WorldGenLevel instance, BlockPos blockPos, BlockState blockState, int i, Operation<Boolean> original) {
        if (blockState.is(Blocks.OAK_LOG) && ModSecretSeeds.NO_TRAPS.match(instance.getLevel().getServer()) && blockState.getValue(BlockStateProperties.AXIS) == Direction.Axis.Y) {
            if (instance.getRandom().nextFloat() < 0.2F) {
                blockState = FunctionalBlocks.OAK_LOG_BOULDER.get().defaultBlockState();
            }
        }
        return original.call(instance, blockPos, blockState, i);
    }

    @ApiStatus.Internal
    public static boolean replacePine(PlacementContext context, RandomSource random, BlockPos pos) {
        if (uninitialized) return false;
        WorldGenLevel level = context.getLevel();
        if (!(level instanceof WorldGenRegion)) return false;
        if (random.nextInt(4) == 0) {
            return pineTree.place(level, context.generator(), random, pos);
        }
        return false;
    }

    /// 获取主世界
    public static ServerLevel getLevel(MinecraftServer server) {
        return server.getLevel(dimension());
    }

    /// default 320
    public static int getUltraY() {
        return 320;
    }

    /// default 260
    public static int getSpaceY() {
        return 260;
    }

    /// default 40
    public static int getSurfaceY() {
        return 40;
    }

    /// default 0
    public static int getUndergroundY() {
        return 0;
    }

    /// default -64
    public static int getCaveY() {
        return -60;
    }

    public static boolean isDesert(Holder<Biome> holder) {
        return holder.is(Tags.Biomes.IS_DESERT);
    }

    /// 对应泰拉的地表雪原和地下雪原，由于MC的这两个群系不以高度为判据，所以使用的时候请先用该方法判断是否是雪原,再以y轴高度判断
    public static boolean isSnowy(Holder<Biome> holder) {
        return holder.is(Tags.Biomes.IS_SNOWY) || holder.is(Tags.Biomes.IS_ICY);
    }

    public static boolean isOcean(Holder<Biome> holder) {
        return holder.is(Tags.Biomes.IS_OCEAN);
    }

    public static boolean isJungle(Holder<Biome> holder) {
        return holder.is(Tags.Biomes.IS_JUNGLE);
    }

    public static boolean isMushroom(Holder<Biome> holder) {
        return holder.is(ModBiomes.GLOWING_MUSHROOM);
    }

    public static boolean isCorruption(Holder<Biome> holder) {
        return holder.is(ModTags.Biomes.THE_CORRUPTION);
    }

    public static boolean isCrimson(Holder<Biome> holder) {
        return holder.is(ModTags.Biomes.THE_CRIMSON);
    }

    public static boolean isHallow(Holder<Biome> holder) {
        return holder.is(ModTags.Biomes.THE_HALLOW);
    }
}
