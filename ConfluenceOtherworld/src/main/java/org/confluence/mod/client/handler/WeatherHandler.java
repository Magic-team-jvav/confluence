package org.confluence.mod.client.handler;

import com.mojang.datafixers.util.Function3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidType;
import org.confluence.mod.common.init.ModParticleTypes;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.network.s2c.WindSpeedPacketS2C;
import org.joml.Vector2f;

import java.util.Hashtable;
import java.util.Map;
import java.util.function.Consumer;

public final class WeatherHandler {
    private static final Map<ResourceLocation, Map<Block, Context>> BLOCK_PARTICLES = new Hashtable<>();
    private static final Map<ResourceLocation, Map<FluidType, ParticleOptions>> FLUID_PARTICLES = new Hashtable<>();
    public static final Vector2f WIND_SPEED = new Vector2f();
    public static final Vector2f WIND_SPEED_N = new Vector2f();
    public static Direction windDirection = null;
    public static String windSpeedInfo = "0.00";
    public static final float STEP = 0.02f;

    public static void handleBlock(ClientLevel level, RandomSource random, BlockState blockState, BlockPos.MutableBlockPos blockPos, Map<Block, Context> data) {
        if (getWindSpeedX() < 0.1 && getWindSpeedZ() < 0.1) return;

        Context context = data.get(blockState.getBlock());
        if (context != null && context.facing.isAvailable(level, random, blockPos, blockState, context)) {
            context.facing.apply(level, random, blockPos, blockState, context);
        }
    }

    public static void handleFluid(ClientLevel level, RandomSource random, FluidState fluidState, BlockPos.MutableBlockPos blockPos, Map<FluidType, ParticleOptions> data) {
        if (getWindSpeedX() < 0.1 && getWindSpeedZ() < 0.1) return;

        ParticleOptions particleOptions = data.get(fluidState.getType().getFluidType());
        if (particleOptions == null) return;

        BlockPos relative = blockPos.above();
        BlockState aboveState = level.getBlockState(relative);
        if (!aboveState.isAir() && aboveState.getFluidState().isEmpty() && aboveState.isSuffocating(level, relative)) return;

        Vec3 vec3 = Vec3.atCenterOf(blockPos);
        double x = vec3.x + Mth.nextDouble(random, -0.5, 0.5);
        double y = vec3.y + 0.57;
        double z = vec3.z + Mth.nextDouble(random, -0.5, 0.5);
        spawnParticle(particleOptions, x, y, z, random);
    }

    private static void spawnParticle(ParticleOptions particleOptions, double x, double y, double z, RandomSource random) {
        Particle particle = Minecraft.getInstance().particleEngine.createParticle(particleOptions, x, y, z, 0.0, 0.0, 0.0);
        if (particle != null) {
            float windSpeedX = getWindSpeedX();
            float windSpeedZ = getWindSpeedZ();
            particle.setParticleSpeed(windSpeedX * random.nextDouble() * 0.02, 0.0, windSpeedZ * random.nextDouble() * 0.02);
            float roll = Mth.nextFloat(random, -Mth.HALF_PI, Mth.HALF_PI);
            particle.oRoll = roll;
            particle.roll = roll;
        }
    }

    public static void initialize(Player player) {
        if (BLOCK_PARTICLES.isEmpty()) {
            for (Holder<Biome> biome : player.registryAccess().registryOrThrow(Registries.BIOME).asHolderIdMap()) {
                ResourceKey<Biome> key = biome.getKey();
                if (key == null || !biome.is(BiomeTags.IS_OVERWORLD)) continue;
                registerBlockParticle(key, map -> {
                    leavesParticles(map);
                    sandParticles(map);
                    snowParticles(map);
                });
            }
        }
    }

    public static void reset() {
        BLOCK_PARTICLES.clear();
        FLUID_PARTICLES.clear();
    }

    public static void leavesParticles(Map<Block, Context> map) {
        map.put(Blocks.OAK_LEAVES, new Context(Facing.HORIZONTAL, ModParticleTypes.LEAVES.get(), Context.FULL));
        map.put(Blocks.SPRUCE_LEAVES, new Context(Facing.HORIZONTAL, ModParticleTypes.LEAVES.get(), Context.FULL));
        map.put(Blocks.BIRCH_LEAVES, new Context(Facing.HORIZONTAL, ModParticleTypes.LEAVES.get(), Context.FULL));
        map.put(Blocks.JUNGLE_LEAVES, new Context(Facing.HORIZONTAL, ModParticleTypes.LEAVES.get(), Context.FULL));
        map.put(Blocks.ACACIA_LEAVES, new Context(Facing.HORIZONTAL, ModParticleTypes.LEAVES.get(), Context.FULL));
        map.put(Blocks.CHERRY_LEAVES, new Context(Facing.HORIZONTAL, ParticleTypes.CHERRY_LEAVES, Context.FULL));
        map.put(Blocks.DARK_OAK_LEAVES, new Context(Facing.HORIZONTAL, ModParticleTypes.LEAVES.get(), Context.FULL));
        map.put(Blocks.MANGROVE_LEAVES, new Context(Facing.HORIZONTAL, ModParticleTypes.LEAVES.get(), Context.FULL));
        map.put(Blocks.AZALEA_LEAVES, new Context(Facing.HORIZONTAL, ModParticleTypes.LEAVES.get(), Context.FULL));
        map.put(Blocks.FLOWERING_AZALEA_LEAVES, new Context(Facing.HORIZONTAL, ModParticleTypes.LEAVES.get(), Context.FULL));
        map.put(NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.LEAVES.get(), new Context(Facing.HORIZONTAL, ModParticleTypes.YELLOW_WILLOW.get(), Context.FULL));
    }

    public static void sandParticles(Map<Block, Context> map) {
        map.put(Blocks.RED_SAND, new Context(Facing.POSITIVE_Y, ModParticleTypes.RED_SAND.get(), Context.FULL));
        map.put(NatureBlocks.RED_SAND_LAYER_BLOCK.get(), new Context(Facing.POSITIVE_Y, ModParticleTypes.RED_SAND.get(), Context.SUIT_YP));
        map.put(Blocks.SAND, new Context(Facing.POSITIVE_Y, ModParticleTypes.SAND.get(), Context.FULL));
        map.put(NatureBlocks.SAND_LAYER_BLOCK.get(), new Context(Facing.POSITIVE_Y, ModParticleTypes.SAND.get(), Context.SUIT_YP));
    }

    public static void snowParticles(Map<Block, Context> map) {
        map.put(Blocks.SNOW_BLOCK, new Context(Facing.POSITIVE_Y, ModParticleTypes.SNOW.get(), Context.FULL));
        map.put(Blocks.SNOW, new Context(Facing.POSITIVE_Y, ModParticleTypes.SNOW.get(), Context.SUIT_YP));
        map.put(Blocks.POWDER_SNOW, new Context(Facing.POSITIVE_Y, ModParticleTypes.SNOW.get(), Context.FULL));
    }

    private static void registerBlockParticle(ResourceKey<Biome> biome, Consumer<Map<Block, Context>> consumer) {
        consumer.accept(BLOCK_PARTICLES.computeIfAbsent(biome.location(), location -> new Hashtable<>()));
    }

    private static void registerFluidParticle(ResourceKey<Biome> biome, Consumer<Map<FluidType, ParticleOptions>> consumer) {
        consumer.accept(FLUID_PARTICLES.computeIfAbsent(biome.location(), location -> new Hashtable<>()));
    }

    public static Map<Block, Context> getBlockParticles(Holder<Biome> biome) {
        ResourceKey<Biome> key = biome.getKey();
        return key == null ? null : BLOCK_PARTICLES.get(key.location());
    }

    public static Map<FluidType, ParticleOptions> getFluidParticles(Holder<Biome> biome) {
        ResourceKey<Biome> key = biome.getKey();
        return key == null ? null : FLUID_PARTICLES.get(key.location());
    }

    public static void handleWindSpeed(WindSpeedPacketS2C packet) {
        WIND_SPEED.set(WIND_SPEED_N);
        WIND_SPEED_N.set(packet.x(), packet.z());
        windSpeedInfo = "%.2f".formatted(WIND_SPEED_N.length());
        windDirection = Direction.getNearest(packet.x(), 0.0F, packet.z());
    }

    public static void handle() {
        if (WIND_SPEED.equals(WIND_SPEED_N)) return;
        Vector2f delta = new Vector2f(WIND_SPEED);
        WIND_SPEED_N.sub(WIND_SPEED, delta);

        if (delta.length() > STEP) WIND_SPEED.add(delta.normalize().mul(STEP));
        else WIND_SPEED.set(WIND_SPEED_N);
    }

    public static float getWindSpeedX() {
        return WIND_SPEED.x;
    }

    public static float getWindSpeedZ() {
        return WIND_SPEED.y;
    }

    public record Context(Facing facing, ParticleOptions options, Function3<Level, BlockPos, BlockState, Vec3> step) {
        private static final Vec3 DOT_57 = new Vec3(0.57, 0.57, 0.57);
        public static final Function3<Level, BlockPos, BlockState, Vec3> FULL = (level, blockPos, blockState) -> DOT_57;
        public static final Function3<Level, BlockPos, BlockState, Vec3> SUIT_YP = (level, blockPos, blockState) -> {
            VoxelShape shape = blockState.getCollisionShape(level, blockPos);
            return new Vec3(0.0, shape.max(Direction.Axis.Y) - 0.23, 0.0);
        };
    }

    public static abstract class Facing {
        public static final Facing POSITIVE_Y = new Facing() {
            @Override
            public boolean isAvailable(Level level, RandomSource random, BlockPos blockPos, BlockState blockState, Context context) {
                Direction opposite = windDirection.getOpposite();
                BlockPos relative = blockPos.relative(opposite).above();
                BlockState relativeState = level.getBlockState(relative);
                return relativeState.isAir() || !relativeState.isSuffocating(level, relative);
            }

            @Override
            public void apply(Level level, RandomSource random, BlockPos blockPos, BlockState blockState, Context context) {
                Vec3 vec3 = Vec3.atCenterOf(blockPos);
                double x = vec3.x + Mth.nextDouble(random, -0.5, 0.5);
                double y = vec3.y + context.step.apply(level, blockPos, blockState).y;
                double z = vec3.z + Mth.nextDouble(random, -0.5, 0.5);
                spawnParticle(context.options, x, y, z, random);
            }
        };
        public static final Facing HORIZONTAL = new Facing() {
            @Override
            public boolean isAvailable(Level level, RandomSource random, BlockPos blockPos, BlockState blockState, Context context) {
                Direction opposite = windDirection.getOpposite();
                BlockPos relative = blockPos.relative(opposite);
                BlockState relativeState = level.getBlockState(relative);
                if (!relativeState.isAir() && relativeState.isSuffocating(level, relative)) return false;
                relative = blockPos.relative(windDirection);
                relativeState = level.getBlockState(relative);
                return relativeState.isAir() || !Block.isFaceFull(relativeState.getCollisionShape(level, relative), opposite);
            }

            @Override
            public void apply(Level level, RandomSource random, BlockPos blockPos, BlockState blockState, Context context) {
                Vec3 vec3 = Vec3.atCenterOf(blockPos);
                int i = windDirection.getStepX();
                int k = windDirection.getStepZ();
                Vec3 step = context.step.apply(level, blockPos, blockState);
                double x = vec3.x + (i == 0 ? Mth.nextDouble(random, -0.5, 0.5) : i * step.x);
                double y = vec3.y + Mth.nextDouble(random, -0.5, 0.5);
                double z = vec3.z + (k == 0 ? Mth.nextDouble(random, -0.5, 0.5) : k * step.z);
                spawnParticle(context.options, x, y, z, random);
            }
        };

        public abstract boolean isAvailable(Level level, RandomSource random, BlockPos blockPos, BlockState blockState, Context context);

        public abstract void apply(Level level, RandomSource random, BlockPos blockPos, BlockState blockState, Context context);
    }
}
