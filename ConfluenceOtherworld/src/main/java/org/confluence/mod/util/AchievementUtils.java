package org.confluence.mod.util;

import PortLib.extensions.com.mojang.serialization.DataResult.PortDataResultExtension;
import PortLib.extensions.net.minecraft.advancements.AdvancementProgress.PortAdvancementProgressExtension;
import com.google.common.collect.Streams;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.FileUtil;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.loading.FMLPaths;
import org.confluence.lib.util.LibClientUtils;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gui.AchievementProgress;
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
import org.mesdag.portlib.diff.mixin.CriterionProgressAccessor;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

import static org.confluence.mod.common.attachment.ExtraInventory.SIZE_VANITY_ARMOR;

public final class AchievementUtils {
    public static final String PREFIX = "achievements/";
    public static final Path CONFLUENCE_ACHIEVEMENTS_DIR = FMLPaths.GAMEDIR.get().resolve("confluence").resolve("achievements");
    public static final PortStreamCodec<FriendlyByteBuf, Map<ResourceLocation, AdvancementProgress>> DATA_STREAM_CODEC = new PortStreamCodec<>() {
        @Override
        public Map<ResourceLocation, AdvancementProgress> decode(FriendlyByteBuf buffer) {
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
                        Instant instant = Instant.from(PortAdvancementProgressExtension.obtainedTimeFormat().parse(time));
                        CriterionProgress progress = new CriterionProgress();
                        ((CriterionProgressAccessor) progress).setObtained(Date.from(instant));
                        criterion.put(criteria, progress);
                    } catch (Exception ignored) {}
                }
                advancement.put(id, new AchievementProgress(criterion, buffer.readBoolean()));
            }
            return advancement;
        }

        @Override
        public void encode(FriendlyByteBuf buffer, Map<ResourceLocation, AdvancementProgress> value) {
            buffer.writeVarInt(value.size());
            value.forEach((id, progress) -> {
                buffer.writeUtf(asPath(id));
                List<String[]> criteria = new ArrayList<>(progress.criteria.size());
                for (Map.Entry<String, CriterionProgress> entry : progress.criteria.entrySet()) {
                    Date date = entry.getValue().getObtained();
                    if (date == null) continue;
                    criteria.add(new String[]{
                            entry.getKey(),
                            date.toInstant().atZone(ZoneId.systemDefault()).format(PortAdvancementProgressExtension.obtainedTimeFormat())
                    });
                }
                buffer.writeVarInt(criteria.size());
                for (String[] criterion : criteria) {
                    buffer.writeUtf(criterion[0]);
                    buffer.writeUtf(criterion[1]);
                }
                buffer.writeBoolean(progress.isDone());
            });
        }
    };
    public static final ResourceLocation GOING_OLDSCHOOL = asAchievement("going_oldschool");
    private static @Nullable Map<ResourceLocation, AdvancementProgress> data;

    public static Codec<Map<ResourceLocation, AdvancementProgress>> getCodecClientOnly() {
        Codec<Map<ResourceLocation, AdvancementProgress>> dataCodec = Codec.unboundedMap(ResourceLocation.CODEC, AchievementProgress.CODEC);
        return DataFixTypes.ADVANCEMENTS.wrapCodec(dataCodec, LibClientUtils.getDataFixer(), 1343);
    }

    public static void setData(ServerPlayer player) {
        Map<UUID, Map<ResourceLocation, AdvancementProgress>> map = player.connection.connection.channel().attr(ReplyAchievementsPacketC2S.ACHIEVEMENTS).get();
        if (map == null) return;
        UUID id = player.getGameProfile().getId();
        Map<ResourceLocation, AdvancementProgress> data = map.get(id);
        if (data == null) return;
        IPlayerAdvancements.of(player.getAdvancements()).confluence$load(player.server.getAdvancements(), data);
    }

    public static void handleData(Map<ResourceLocation, AdvancementProgress> data, boolean override) {
        if (override || AchievementUtils.data == null) {
            AchievementUtils.data = data;
        } else {
            Map<ResourceLocation, AdvancementProgress> map = new LinkedHashMap<>(AchievementUtils.data);
            map.putAll(data);
            AchievementUtils.data = map;
        }
    }

    public static void saveData() {
        if (data == null) return;
        Path path = CONFLUENCE_ACHIEVEMENTS_DIR.resolve(LibClientUtils.getGameProfile().getId() + ".json");
        saveData(data, path, ModUtils.GSON, getCodecClientOnly());
        data = null;
    }

    public static void saveData(Map<ResourceLocation, AdvancementProgress> data, Path savePath, Gson gson, Codec<Map<ResourceLocation, AdvancementProgress>> codec) {
        try {
            FileUtil.createDirectoriesSafe(savePath.getParent());
            try (Writer writer = Files.newBufferedWriter(savePath, StandardCharsets.UTF_8)) {
                gson.toJson(PortDataResultExtension.getOrThrow(codec.encodeStart(JsonOps.INSTANCE, data)), gson.newJsonWriter(writer));
            }
        } catch (JsonIOException | IOException ioexception) {
            Confluence.LOGGER.error("Couldn't save confluence achievements to {}", savePath, ioexception);
        }
    }

    public static Map<ResourceLocation, AdvancementProgress> loadData(UUID uuid) {
        Path path = CONFLUENCE_ACHIEVEMENTS_DIR.resolve(uuid + ".json");
        if (Files.isRegularFile(path)) {
            try (JsonReader reader = new JsonReader(Files.newBufferedReader(path, StandardCharsets.UTF_8))) {
                reader.setLenient(false);
                return PortDataResultExtension.getOrThrow(getCodecClientOnly().parse(JsonOps.INSTANCE, com.google.gson.internal.Streams.parse(reader)), JsonParseException::new);
            } catch (JsonIOException | IOException ioexception) {
                Confluence.LOGGER.error("Couldn't access confluence achievements in {}", path, ioexception);
            } catch (JsonParseException jsonParseException) {
                Confluence.LOGGER.error("Couldn't parse confluence achievements in {}", path, jsonParseException);
            }
        }
        return Map.of();
    }

    public static ResourceLocation asAchievement(String path) {
        return Confluence.asResource(PREFIX + path);
    }

    public static String asPath(ResourceLocation achievement) {
        return achievement.getPath().substring(PREFIX.length());
    }

    public static boolean achievedAchievement(ServerPlayer player, String path) {
        if (LibEntityUtils.getOrCreatePersistedData(player).getBoolean(Confluence.asPlainId(path))) {
            return true;
        }
        Advancement advancement = player.server.getAdvancements().getAdvancement(asAchievement(path));
        return advancement != null && player.getAdvancements().getOrStartProgress(advancement).isDone();
    }

    public static void awardAchievement(ServerPlayer player, String path) {
        CompoundTag data = LibEntityUtils.getOrCreatePersistedData(player);
        String key = Confluence.asPlainId(path);
        if (!data.getBoolean(key)) {
            Advancement advancement = player.server.getAdvancements().getAdvancement(asAchievement(path));
            if (advancement != null) {
                player.getAdvancements().award(advancement, "never");
            }
            data.putBoolean(key, true);
        }
    }

    public static void youCanDoIt(ServerPlayer player, ServerLevel level, long gameTime) {
        if (gameTime % 1200 == 0L) { // 每分钟检查一次
            byte firstNight = LibEntityUtils.getOrCreatePersistedData(player).getByte("confluence:you_can_do_it");
            if (firstNight == -1) return;
            int dayTime = LibDateUtils.getDayTime(level);
            if (LibDateUtils.isNight(dayTime)) {
                LibEntityUtils.getOrCreatePersistedData(player).putByte("confluence:you_can_do_it", (byte) 1);
            } else if (firstNight == 1 && LibDateUtils.isDay(dayTime)) {
                Advancement advancement = player.server.getAdvancements().getAdvancement(asAchievement("you_can_do_it"));
                if (advancement != null) {
                    player.getAdvancements().award(advancement, "never");
                }
                LibEntityUtils.getOrCreatePersistedData(player).putByte("confluence:you_can_do_it", (byte) -1);
            }
        }
    }

    public static boolean marathonMedalist(ServerPlayer player, ServerStatsCounter stats, boolean marathon) {
        if (marathon) return true;
        int sprint = stats.getValue(Stats.CUSTOM.get(Stats.SPRINT_ONE_CM));
        int crouch = stats.getValue(Stats.CUSTOM.get(Stats.CROUCH_ONE_CM));
        int walk = stats.getValue(Stats.CUSTOM.get(Stats.WALK_ONE_CM));
        if (sprint + crouch + walk > 46112_00) {
            Advancement advancement = player.server.getAdvancements().getAdvancement(asAchievement("marathon_medalist"));
            if (advancement != null) {
                player.getAdvancements().award(advancement, "never");
            }
            return true;
        }
        return false;
    }

    public static void luckyBreak_watchYourStep(ServerPlayer player, DamageSource damageSource, @Nullable Entity sourceEntity) {
        if (player.isAlive()) {
            if (player.getHealth() / player.getMaxHealth() < 0.1F && damageSource.is(DamageTypeTags.IS_FALL)) {
                awardAchievement(player, "lucky_break");
            }
        } else if (sourceEntity != null && DartTrapBlock.NAME.equals(sourceEntity.getCustomName())) {
            awardAchievement(player, "watch_your_step");
        }
    }

    public static void matchingAttire_fashionStatement(EquipmentSlot.Type type, ServerPlayer player) {
        if (type != EquipmentSlot.Type.ARMOR) return;
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
        CompoundTag tag = LibEntityUtils.getOrCreatePersistedData(player);
        short before = tag.getShort("confluence:the_frequent_flyer");
        if (before > 10000) return;
        long total = before + cost;
        if (total >= 10000) {
            Advancement advancement = player.server.getAdvancements().getAdvancement(asAchievement("the_frequent_flyer"));
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

    public static void aRareRealm(ServerPlayer player, long gameTime) {
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
