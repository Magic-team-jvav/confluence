package org.confluence.mod.common.worldgen.secret_seed;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import org.confluence.mod.common.init.ModSecretSeeds;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

public class TheConstant extends SecretSeed {
    public TheConstant(long flag) {
        super(flag);
    }

    @Override
    public boolean match(String seed) {
        return "constant".equals(seed) || "theconstant".equals(seed) || "the constant".equals(seed) || "eye4aneye".equals(seed) || "eyeforaneye".equals(seed);
    }

    public static float applyExhaustion(float exhaustion) {
        if (ModSecretSeeds.THE_CONSTANT.match()) {
            return exhaustion * 2;
        }
        return exhaustion;
    }

    public static float applyAttackDamage(Entity causer, float amount) {
        if (causer instanceof ServerPlayer serverPlayer && serverPlayer.getFoodData().needsFood() && ModSecretSeeds.THE_CONSTANT.match(serverPlayer.server)) {
            return amount * 0.8F;
        }
        return amount;
    }

    public static void applyDarkness(ServerPlayer player, ServerLevel level) {
        if (ModSecretSeeds.THE_CONSTANT.match(level)) {
            CompoundTag data = player.getPersistentData();
            int tick = data.getInt("confluence:in_darkness_tick");
            if (level.getLightEngine().getRawBrightness(player.blockPosition().above(), 0) <= 3) {
                if (tick < 100) {
                    if (++tick == 60) {
                        player.sendSystemMessage(Component.translatable("secret_seed.the_constant.in_darkness_for_3_second"), false);
                    }
                    data.putInt("confluence:in_darkness_tick", tick);
                } else if (level.getGameTime() % 20 == 0) {
                    player.hurt(player.damageSources().magic(), 50);
                }
            } else if (tick != 0) {
                data.putInt("confluence:in_darkness_tick", 0);
            }
        }
    }

    // todo 世界中会生成“Wavy Caves”（皱曲洞穴）。它们会生成为上下尖锐起伏的之字形，或者斜向下的曲线形状。
    @ParametersAreNonnullByDefault
    public static class WavyCaveCarver extends WorldCarver<WavyCaveCarver.Config> {
        public WavyCaveCarver(Codec<Config> codec) {
            super(codec);
        }

        @Override
        public boolean carve(CarvingContext context, Config config, ChunkAccess chunk, Function<BlockPos, Holder<Biome>> biomeAccessor, RandomSource random, Aquifer aquifer, ChunkPos chunkPos, CarvingMask carvingMask) {
            return false;
        }

        @Override
        public boolean isStartChunk(Config config, RandomSource random) {
            return random.nextFloat() < config.probability;
        }

        public static class Config extends CarverConfiguration {
            public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    CarverConfiguration.CODEC.forGetter(config -> config)
            ).apply(instance, Config::new));

            public Config(CarverConfiguration configuration) {
                super(configuration.probability, configuration.y, configuration.yScale, configuration.lavaLevel, configuration.debugSettings, configuration.replaceable);
            }
        }
    }
}
