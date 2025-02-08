package org.confluence.mod.common.worldgen.secret_seed;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.mixed.IMinecraftServer;
import org.confluence.mod.mixin.client.accessor.GameRendererAccessor;
import org.confluence.terra_curio.util.CuriosUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

public class TheConstant extends SecretSeed {
    private static final ResourceLocation POST_EFFECT = Confluence.asResource("shaders/post/the_constant.json");

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
            if (level.getLightEngine().getRawBrightness(player.blockPosition().above(), 0) <= 5) {
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

    @OnlyIn(Dist.CLIENT)
    public static void postEffect(boolean post) {
        GameRenderer gameRenderer = Minecraft.getInstance().gameRenderer;
        PostChain postChain = gameRenderer.currentEffect();
        if (post) {
            if (postChain == null || !POST_EFFECT.toString().equals(postChain.getName())) {
                gameRenderer.loadEffect(POST_EFFECT);
            }
            ((GameRendererAccessor) gameRenderer).setEffectActive(true);
        } else {
            if (postChain != null && POST_EFFECT.toString().equals(postChain.getName())) {
                ((GameRendererAccessor) gameRenderer).setEffectActive(false);
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

    public record PostEffectPacketS2C(boolean post) implements CustomPacketPayload {
        public static final Type<PostEffectPacketS2C> TYPE = new Type<>(Confluence.asResource("the_constant_post_effect"));
        public static final StreamCodec<ByteBuf, PostEffectPacketS2C> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL, p -> p.post,
                PostEffectPacketS2C::new
        );

        @Override
        public @NotNull Type<PostEffectPacketS2C> type() {
            return TYPE;
        }

        public void handle(IPayloadContext context) {
            context.enqueueWork(() -> {
                if (context.player().isLocalPlayer()) {
                    postEffect(post);
                }
            }).exceptionally(e -> {
                context.disconnect(Component.translatable("neoforge.network.invalid_flow", e.getMessage()));
                return null;
            });
        }

        public static void sendToClient(ServerPlayer serverPlayer) {
            boolean secretSeed = IMinecraftServer.matchesSecretFlag(serverPlayer.server, ModSecretSeeds.THE_CONSTANT.getFlag());
            boolean accessory = CuriosUtils.hasCurio(serverPlayer, AccessoryItems.RADIO_THING.get());
            PacketDistributor.sendToAllPlayers(new PostEffectPacketS2C(secretSeed ^ accessory));
        }
    }
}
