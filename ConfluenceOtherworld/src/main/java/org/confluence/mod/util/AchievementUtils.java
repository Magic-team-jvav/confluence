package org.confluence.mod.util;

import com.google.common.collect.Streams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.FileUtil;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.loading.FMLPaths;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.block.functional.DartTrapBlock;
import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.mod.mixed.ILevelChunkSection;
import org.confluence.mod.mixed.IMinecraftServer;
import org.confluence.mod.mixed.IPlayerAdvancements;
import org.confluence.mod.mixed.IWorldOptions;
import org.confluence.mod.network.task.ReplyAchievementsPacketC2S;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.confluence.mod.common.attachment.ExtraInventory.SIZE_VANITY_ARMOR;

public final class AchievementUtils {
    public static final String PREFIX = "achievements/";
    public static final Path CONFLUENCE_ACHIEVEMENTS_DIR = FMLPaths.GAMEDIR.get().resolve("confluence").resolve("achievements");
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final StreamCodec<FriendlyByteBuf, PlayerAdvancements.Data> DATA_STREAM_CODEC = new StreamCodec<>() {
        @Override
        public PlayerAdvancements.Data decode(FriendlyByteBuf buffer) {
            int size = buffer.readVarInt();
            Map<ResourceLocation, AdvancementProgress> advancement = new LinkedHashMap<>();
            for (int i = 0; i < size; i++) {
                ResourceLocation id = asAchievement(buffer.readUtf());
                int j = buffer.readVarInt();
                Map<String, CriterionProgress> criterion = new HashMap<>();
                for (int k = 0; k < j; k++) {
                    String criteria = buffer.readUtf();
                    String time = buffer.readUtf();
                    try {
                        Instant instant = Instant.from(AdvancementProgress.OBTAINED_TIME_FORMAT.parse(time));
                        criterion.put(criteria, new CriterionProgress(instant));
                    } catch (Exception ignored) {}
                }
                advancement.put(id, new AdvancementProgress(criterion));
            }
            return new PlayerAdvancements.Data(advancement);
        }

        @Override
        public void encode(FriendlyByteBuf buffer, PlayerAdvancements.Data value) {
            buffer.writeVarInt(value.map().size());
            value.forEach((id, progress) -> {
                buffer.writeUtf(asPath(id));
                buffer.writeVarInt(progress.criteria.size());
                for (Map.Entry<String, CriterionProgress> entry : progress.criteria.entrySet()) {
                    Instant instant = entry.getValue().getObtained();
                    if (instant != null) {
                        buffer.writeUtf(entry.getKey());
                        buffer.writeUtf(instant.atZone(ZoneId.systemDefault()).format(AdvancementProgress.OBTAINED_TIME_FORMAT));
                    }
                }
            });
        }
    };
    private static @Nullable PlayerAdvancements.Data data;

    public static Codec<PlayerAdvancements.Data> getCodecClientOnly() {
        return DataFixTypes.ADVANCEMENTS.wrapCodec(PlayerAdvancements.Data.CODEC, ClientUtils.getDataFixer(), 1343);
    }

    public static void setData(ServerPlayer player) {
        Map<UUID, PlayerAdvancements.Data> map = player.connection.getConnection().channel().attr(ReplyAchievementsPacketC2S.ACHIEVEMENTS).get();
        if (map == null) return;
        PlayerAdvancements.Data data = map.get(player.getGameProfile().getId());
        if (data == null) return;
        IPlayerAdvancements.of(player.getAdvancements()).confluence$load(player.server.getAdvancements(), data);
    }

    public static void handleData(PlayerAdvancements.Data data) {
        AchievementUtils.data = data;
    }

    public static void saveData(Player player) {
        if (data == null) return;
        Path path = AchievementUtils.CONFLUENCE_ACHIEVEMENTS_DIR.resolve(player.getUUID() + ".json");
        saveData(data, path, GSON, getCodecClientOnly());
        data = null;
    }

    public static void saveData(PlayerAdvancements.Data data, Path savePath, Gson gson, Codec<PlayerAdvancements.Data> codec) {
        JsonElement element = codec.encodeStart(JsonOps.INSTANCE, data).getOrThrow();
        try {
            FileUtil.createDirectoriesSafe(savePath.getParent());
            try (Writer writer = Files.newBufferedWriter(savePath, StandardCharsets.UTF_8)) {
                gson.toJson(element, gson.newJsonWriter(writer));
            }
        } catch (JsonIOException | IOException ioexception) {
            Confluence.LOGGER.error("Couldn't save confluence achievements to {}", savePath, ioexception);
        }
    }

    public static ResourceLocation asAchievement(String path) {
        return Confluence.asResource(PREFIX + path);
    }

    public static String asPath(ResourceLocation achievement) {
        return achievement.getPath().substring(PREFIX.length());
    }

    public static boolean achievedAchievement(ServerPlayer player, String path) {
        if (LibUtils.getOrCreatePersistedData(player).getBoolean(Confluence.asPlainId(path))) {
            return true;
        }
        AdvancementHolder advancement = player.server.getAdvancements().get(asAchievement(path));
        return advancement != null && player.getAdvancements().getOrStartProgress(advancement).isDone();
    }

    public static void awardAchievement(ServerPlayer player, String path) {
        CompoundTag data = LibUtils.getOrCreatePersistedData(player);
        String key = Confluence.asPlainId(path);
        if (!data.getBoolean(key)) {
            AdvancementHolder advancement = player.server.getAdvancements().get(asAchievement(path));
            if (advancement != null) {
                player.getAdvancements().award(advancement, "never");
            }
            data.putBoolean(key, true);
        }
    }

    public static void youCanDoIt(ServerPlayer player, ServerLevel level, long gameTime) {
        if (gameTime % 1200 == 0L) { // 每分钟检查一次
            byte firstNight = LibUtils.getOrCreatePersistedData(player).getByte("confluence:you_can_do_it");
            if (firstNight == -1) return;
            int dayTime = LibDateUtils.getDayTime(level);
            if (LibDateUtils.isNight(dayTime)) {
                LibUtils.getOrCreatePersistedData(player).putByte("confluence:you_can_do_it", (byte) 1);
            } else if (firstNight == 1 && LibDateUtils.isDay(dayTime)) {
                AdvancementHolder advancement = player.server.getAdvancements().get(asAchievement("you_can_do_it"));
                if (advancement != null) {
                    player.getAdvancements().award(advancement, "never");
                }
                LibUtils.getOrCreatePersistedData(player).putByte("confluence:you_can_do_it", (byte) -1);
            }
        }
    }

    public static boolean marathonMedalist(ServerPlayer player, ServerStatsCounter stats, boolean marathon) {
        if (marathon) return true;
        int sprint = stats.getValue(Stats.CUSTOM.get(Stats.SPRINT_ONE_CM));
        int crouch = stats.getValue(Stats.CUSTOM.get(Stats.CROUCH_ONE_CM));
        int walk = stats.getValue(Stats.CUSTOM.get(Stats.WALK_ONE_CM));
        if (sprint + crouch + walk > 46112_00) {
            AdvancementHolder advancement = player.server.getAdvancements().get(asAchievement("marathon_medalist"));
            if (advancement != null) {
                player.getAdvancements().award(advancement, "never");
            }
            return true;
        }
        return false;
    }

    public static void luckyBreak_watchYourStep(ServerPlayer player, DamageSource damageSource, Entity sourceEntity) {
        if (player.isAlive()) {
            if (player.getHealth() / player.getMaxHealth() < 0.1F && damageSource.is(DamageTypeTags.IS_FALL)) {
                awardAchievement(player, "lucky_break");
            }
        } else if (sourceEntity != null && DartTrapBlock.NAME.equals(sourceEntity.getCustomName())) {
            awardAchievement(player, "watch_your_step");
        }
    }

    public static void matchingAttire_fashionStatement(EquipmentSlot.Type type, ServerPlayer player) {
        if (type != EquipmentSlot.Type.HUMANOID_ARMOR) return;
        if (Streams.stream(player.getArmorSlots()).noneMatch(ItemStack::isEmpty)) {
            awardAchievement(player, "matching_attire");
            ExtraInventory extraInventory = ExtraInventory.of(player);
            boolean fashionStatement = true;
            for (int i = 0; i < SIZE_VANITY_ARMOR; i++) {
                if (extraInventory.getVanityArmor(i, false).isEmpty()) {
                    fashionStatement = false;
                    break;
                }
            }
            if (fashionStatement) awardAchievement(player, "fashion_statement");
        }
    }

    public static void theFrequentFlyer(ServerPlayer player, long cost) {
        CompoundTag tag = LibUtils.getOrCreatePersistedData(player);
        short before = tag.getShort("confluence:the_frequent_flyer");
        if (before > 10000) return;
        long total = before + cost;
        if (total >= 10000) {
            AdvancementHolder advancement = player.server.getAdvancements().get(asAchievement("the_frequent_flyer"));
            if (advancement != null) {
                player.getAdvancements().award(advancement, "never");
            }
        }
        tag.putShort("confluence:the_frequent_flyer", (short) total);
    }

    public static void noHobo(AbstractTerraNPC npc, NPCSpawner.Region region) {
        if (npc.level() instanceof ServerLevel serverLevel) {
            for (ServerPlayer player : serverLevel.players()) {
                if (region.isOnRegion(player.chunkPosition())) {
                    AchievementUtils.awardAchievement(player, "no_hobo");
                }
            }
        }
    }

    public static void quietNeighborhood(ServerPlayer player, ServerLevel level, long gameTime) {
        if (gameTime % 40 == 2) {
            ILevelChunkSection iSection = DynamicBiomeUtils.getISection(level, player.blockPosition());
            if (iSection != null && iSection.confluence$isGraveyard()) {
                awardAchievement(player, "quiet_neighborhood");
            }
        }
    }

    public static void aRareRealm(ServerPlayer player, ServerLevel level, long gameTime) {
        if (IMinecraftServer.of(player.server).confluence$matchesSecretFlag(IWorldOptions.SECRET_SEED) && gameTime % 40 == 3) {
            awardAchievement(player, "a_rare_realm");
        }
    }

    public static void unusualSurvivalStrategies(ServerPlayer player, boolean isWatterBottle) {
        if (isWatterBottle && player.isInWater() && player.getAirSupply() <= 0) {
            awardAchievement(player, "unusual_survival_strategies");
        }
    }
}
