package org.confluence.mod.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidType;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.network.s2c.WindSpeedPacketS2C;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;

import java.util.Hashtable;
import java.util.Map;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public final class WeatherHandler {
    private static final Map<ResourceLocation, Map<Block, ParticleOptions>> BLOCK_PARTICLES = new Hashtable<>();
    private static final Map<ResourceLocation, Map<FluidType, ParticleOptions>> FLUID_PARTICLES = new Hashtable<>();
    public static final Vector2f WIND_SPEED = new Vector2f();
    public static Direction windDirection = null;
    public static String windSpeedInfo = "0.00";

    public static void handleBlock(ClientLevel level, RandomSource random, BlockState blockState, BlockPos.MutableBlockPos blockPos, Map<Block, ParticleOptions> data) {
        if (!ClientConfigs.showWindParticles) return;
        ParticleOptions particleOptions = data.get(blockState.getBlock());
        if (particleOptions == null) return;

        Direction opposite = windDirection.getOpposite();
        BlockPos relative = blockPos.relative(opposite);
        BlockState relativeState = level.getBlockState(relative);
        if (!relativeState.isAir() && relativeState.isSuffocating(level, relative)) return;
        relative = blockPos.relative(windDirection);
        relativeState = level.getBlockState(relative);
        if (!relativeState.isAir() && Block.isFaceFull(relativeState.getCollisionShape(level, relative), opposite)) return;

        Vec3 vec3 = Vec3.atCenterOf(blockPos);
        int i = windDirection.getStepX();
        int k = windDirection.getStepZ();
        double x = vec3.x + (i == 0 ? Mth.nextDouble(random, -0.5, 0.5) : i * 0.57);
        double y = vec3.y + Mth.nextDouble(random, -0.5, 0.5);
        double z = vec3.z + (k == 0 ? Mth.nextDouble(random, -0.5, 0.5) : k * 0.57);
        spawnParticle(particleOptions, x, y, z);
    }

    public static void handleFluid(ClientLevel level, RandomSource random, FluidState fluidState, BlockPos.MutableBlockPos blockPos, Map<FluidType, ParticleOptions> data) {
        if (!ClientConfigs.showWindParticles) return;
        ParticleOptions particleOptions = data.get(fluidState.getType().getFluidType());
        if (particleOptions == null) return;

        BlockPos relative = blockPos.above();
        BlockState aboveState = level.getBlockState(relative);
        if (!aboveState.isAir() && aboveState.getFluidState().isEmpty() && aboveState.isSuffocating(level, relative)) return;

        Vec3 vec3 = Vec3.atCenterOf(blockPos);
        double x = vec3.x + Mth.nextDouble(random, -0.5, 0.5);
        double y = vec3.y + 0.57;
        double z = vec3.z + Mth.nextDouble(random, -0.5, 0.5);
        spawnParticle(particleOptions, x, y, z);
    }

    private static void spawnParticle(ParticleOptions particleOptions, double x, double y, double z) {
        Particle particle = Minecraft.getInstance().particleEngine.createParticle(particleOptions, x, y, z, 0.0, 0.0, 0.0);
        if (particle != null) {
            float windSpeedX = getWindSpeedX();
            float windSpeedZ = getWindSpeedZ();
            particle.setParticleSpeed(windSpeedX * 0.01, 0.0, windSpeedZ * 0.01);
        }
    }

    public static void initialize(@Nullable LocalPlayer player) {
        if (player == null) {
            BLOCK_PARTICLES.clear();
            FLUID_PARTICLES.clear();
        } else {
            for (Holder<Biome> biome : player.registryAccess().registryOrThrow(Registries.BIOME).asHolderIdMap()) {
                ResourceKey<Biome> key = biome.getKey();
                if (key == null || !biome.is(BiomeTags.IS_OVERWORLD)) continue;
                registerBlockParticle(key, map -> {
                    defaultLeavesParticles(map);
                });
            }
        }
    }

    public static void defaultLeavesParticles(Map<Block, ParticleOptions> map) {
        map.put(Blocks.OAK_LEAVES, ParticleTypes.CHERRY_LEAVES);
        map.put(Blocks.SPRUCE_LEAVES, ParticleTypes.CHERRY_LEAVES);
        map.put(Blocks.BIRCH_LEAVES, ParticleTypes.CHERRY_LEAVES);
        map.put(Blocks.JUNGLE_LEAVES, ParticleTypes.CHERRY_LEAVES);
        map.put(Blocks.ACACIA_LEAVES, ParticleTypes.CHERRY_LEAVES);
        map.put(Blocks.CHERRY_LEAVES, ParticleTypes.CHERRY_LEAVES);
        map.put(Blocks.DARK_OAK_LEAVES, ParticleTypes.CHERRY_LEAVES);
        map.put(Blocks.MANGROVE_LEAVES, ParticleTypes.CHERRY_LEAVES);
        map.put(Blocks.AZALEA_LEAVES, ParticleTypes.CHERRY_LEAVES);
        map.put(Blocks.FLOWERING_AZALEA_LEAVES, ParticleTypes.CHERRY_LEAVES);
    }

    private static void registerBlockParticle(ResourceKey<Biome> biome, Consumer<Map<Block, ParticleOptions>> consumer) {
        consumer.accept(BLOCK_PARTICLES.computeIfAbsent(biome.location(), location -> new Hashtable<>()));
    }

    private static void registerFluidParticle(ResourceKey<Biome> biome, Consumer<Map<FluidType, ParticleOptions>> consumer) {
        consumer.accept(FLUID_PARTICLES.computeIfAbsent(biome.location(), location -> new Hashtable<>()));
    }

    public static Map<Block, ParticleOptions> getBlockParticles(Holder<Biome> biome) {
        ResourceKey<Biome> key = biome.getKey();
        return key == null ? null : BLOCK_PARTICLES.get(key.location());
    }

    public static Map<FluidType, ParticleOptions> getFluidParticles(Holder<Biome> biome) {
        ResourceKey<Biome> key = biome.getKey();
        return key == null ? null : FLUID_PARTICLES.get(key.location());
    }

    public static void handleWindSpeed(WindSpeedPacketS2C packet) {
        WIND_SPEED.set(packet.x(), packet.z());
        windSpeedInfo = "%.2f".formatted(WIND_SPEED.length());
        windDirection = Direction.getNearest(packet.x(), 0.0F, packet.z());
    }

    public static float getWindSpeedX() {
        return WIND_SPEED.x;
    }

    public static float getWindSpeedZ() {
        return WIND_SPEED.y;
    }
}
