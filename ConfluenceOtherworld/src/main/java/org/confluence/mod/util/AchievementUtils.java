package org.confluence.mod.util;

import com.google.common.collect.Streams;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.loading.FMLPaths;
import org.confluence.lib.util.LibClientUtils;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gui.AchievementProgress;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.block.functional.DartTrapBlock;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.data.spawner.NPCSpawner;
import org.confluence.mod.common.entity.boss.BaseBoss;
import org.confluence.mod.common.entity.boss.BrainOfCthulhu;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.projectile.boulder.TombstoneBoulderEntity;
import org.confluence.mod.common.init.ModBlockCounters;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.common.init.entity.BossEntities;
import org.confluence.mod.common.init.entity.NpcEntities;
import org.confluence.mod.mixed.IMinecraftServer;
import org.confluence.mod.mixed.IWorldOptions;
import org.jetbrains.annotations.Nullable;

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
                advancement.put(id, new AchievementProgress(criterion, buffer.readBoolean()));
            }
            return new PlayerAdvancements.Data(advancement);
        }

        @Override
        public void encode(FriendlyByteBuf buffer, PlayerAdvancements.Data value) {
            buffer.writeVarInt(value.map().size());
            value.forEach((id, progress) -> {
                buffer.writeUtf(asPath(id));
                List<String[]> criteria = new ArrayList<>(progress.criteria.size());
                for (Map.Entry<String, CriterionProgress> entry : progress.criteria.entrySet()) {
                    Instant instant = entry.getValue().getObtained();
                    if (instant == null) continue;
                    criteria.add(new String[]{
                            entry.getKey(),
                            instant.atZone(ZoneId.systemDefault()).format(AdvancementProgress.OBTAINED_TIME_FORMAT)
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
    /// 植树造林所需种下的树苗数量。
    private static final int CONSERVATIONIST_REQUIRED = 100;
    private static final String CONSERVATIONIST_KEY = "confluence:conservationist_planted";
    private static @Nullable PlayerAdvancements.Data data;

    public static Codec<PlayerAdvancements.Data> getCodecClientOnly() {
        Codec<PlayerAdvancements.Data> dataCodec = Codec.unboundedMap(ResourceLocation.CODEC, AchievementProgress.CODEC)
                .xmap(PlayerAdvancements.Data::new, PlayerAdvancements.Data::map);
        return DataFixTypes.ADVANCEMENTS.wrapCodec(dataCodec, LibClientUtils.getDataFixer(), 1343);
    }

    public static void handleData(PlayerAdvancements.Data data, boolean override) {
        if (override || AchievementUtils.data == null) {
            AchievementUtils.data = data;
        } else {
            Map<ResourceLocation, AdvancementProgress> map = new LinkedHashMap<>(AchievementUtils.data.map());
            map.putAll(data.map());
            AchievementUtils.data = new PlayerAdvancements.Data(map);
        }
    }

    public static void saveData() {
        if (data == null) return;
        Path path = CONFLUENCE_ACHIEVEMENTS_DIR.resolve(LibClientUtils.getGameProfile().getId() + ".json");
        saveData(data, path, ModUtils.GSON, getCodecClientOnly());
        data = null;
    }

    public static void saveData(PlayerAdvancements.Data data, Path savePath, Gson gson, Codec<PlayerAdvancements.Data> codec) {
        try {
            FileUtil.createDirectoriesSafe(savePath.getParent());
            try (Writer writer = Files.newBufferedWriter(savePath, StandardCharsets.UTF_8)) {
                gson.toJson(codec.encodeStart(JsonOps.INSTANCE, data).getOrThrow(), gson.newJsonWriter(writer));
            }
        } catch (JsonIOException | IOException ioexception) {
            Confluence.LOGGER.error("Couldn't save confluence achievements to {}", savePath, ioexception);
        }
    }

    public static PlayerAdvancements.Data loadData(UUID uuid) {
        Path path = CONFLUENCE_ACHIEVEMENTS_DIR.resolve(uuid + ".json");
        if (Files.isRegularFile(path)) {
            try (JsonReader reader = new JsonReader(Files.newBufferedReader(path, StandardCharsets.UTF_8))) {
                reader.setLenient(false);
                return getCodecClientOnly().parse(JsonOps.INSTANCE, com.google.gson.internal.Streams.parse(reader)).getOrThrow(JsonParseException::new);
            } catch (JsonIOException | IOException ioexception) {
                Confluence.LOGGER.error("Couldn't access confluence achievements in {}", path, ioexception);
            } catch (JsonParseException jsonParseException) {
                Confluence.LOGGER.error("Couldn't parse confluence achievements in {}", path, jsonParseException);
            }
        }
        return new PlayerAdvancements.Data(Map.of());
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
        AdvancementHolder advancement = player.server.getAdvancements().get(asAchievement(path));
        return advancement != null && player.getAdvancements().getOrStartProgress(advancement).isDone();
    }

    public static void awardAchievement(ServerPlayer player, String path) {
        CompoundTag data = LibEntityUtils.getOrCreatePersistedData(player);
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
            byte firstNight = LibEntityUtils.getOrCreatePersistedData(player).getByte("confluence:you_can_do_it");
            if (firstNight == -1) return;
            int dayTime = LibDateUtils.getDayTime(level);
            if (LibDateUtils.isNight(dayTime)) {
                LibEntityUtils.getOrCreatePersistedData(player).putByte("confluence:you_can_do_it", (byte) 1);
            } else if (firstNight == 1 && LibDateUtils.isDay(dayTime)) {
                AdvancementHolder advancement = player.server.getAdvancements().get(asAchievement("you_can_do_it"));
                if (advancement != null) {
                    player.getAdvancements().award(advancement, "never");
                }
                LibEntityUtils.getOrCreatePersistedData(player).putByte("confluence:you_can_do_it", (byte) -1);
            }
        }
    }

    public static void marathonMedalist(ServerPlayer player) {
        if (achievedAchievement(player, "marathon_medalist")) return;
        ServerStatsCounter stats = player.getStats();
        int sprint = stats.getValue(Stats.CUSTOM.get(Stats.SPRINT_ONE_CM));
        int crouch = stats.getValue(Stats.CUSTOM.get(Stats.CROUCH_ONE_CM));
        int walk = stats.getValue(Stats.CUSTOM.get(Stats.WALK_ONE_CM));
        if (sprint + crouch + walk > 46112_00) {
            AdvancementHolder advancement = player.server.getAdvancements().get(asAchievement("marathon_medalist"));
            if (advancement != null) {
                player.getAdvancements().award(advancement, "never");
            }
        }
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
        CompoundTag tag = LibEntityUtils.getOrCreatePersistedData(player);
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

    public static void noHobo_itCanTalk(BaseNPC npc, NPCSpawner.Region region) {
        if (npc.level() instanceof ServerLevel level) {
            for (ServerPlayer player : level.players()) {
                if (region.isOnRegion(player.chunkPosition())) {
                    AchievementUtils.awardAchievement(player, "no_hobo");
                    if (npc.getType() == NpcEntities.TRUFFLE.get()) {
                        AchievementUtils.awardAchievement(player, "it_can_talk");
                    }
                }
            }
        }
    }

    public static void quietNeighborhood(ServerPlayer player, ServerLevel level) {
        if (ModBlockCounters.isGraveyard(level, player.blockPosition())) {
            awardAchievement(player, "quiet_neighborhood");
        }
    }

    public static void aRareRealm(ServerPlayer player) {
        if (IMinecraftServer.of(player.server).matchesSecretFlag(IWorldOptions.SECRET_SEED)) {
            awardAchievement(player, "a_rare_realm");
        }
    }

    public static void unusualSurvivalStrategies(ServerPlayer player, boolean isWatterBottle) {
        if (isWatterBottle && player.isInWater() && player.getAirSupply() <= 0) {
            awardAchievement(player, "unusual_survival_strategies");
        }
    }

    /// 机械 Boss 死亡后的共同结算：三王全灭与三王同时击杀。
    public static void mechanicalBossDefeated(ServerPlayer player, BaseBoss boss) {
        boolean allMechanicalDefeated = KillBoard.INSTANCE.isTwinsDefeated()
                && KillBoard.INSTANCE.isDefeated(BossEntities.THE_DESTROYER.get())
                && KillBoard.INSTANCE.isDefeated(BossEntities.SKELETRON_PRIME.get());
        if (!allMechanicalDefeated) return;
        awardAchievement(player, "buckets_of_bolts");
        if (boss.isMechanicalMayhemParticipant(player.getUUID())) {
            awardAchievement(player, "mecha_mayhem");
        }
    }

    /// 种下树苗，累计一百棵后达成“植树造林”。
    public static void conservationist(ServerPlayer player) {
        if (achievedAchievement(player, "conservationist")) return;
        CompoundTag data = LibEntityUtils.getOrCreatePersistedData(player);
        int planted = data.getInt(CONSERVATIONIST_KEY);
        if (planted >= CONSERVATIONIST_REQUIRED) return;
        if (++planted >= CONSERVATIONIST_REQUIRED) {
            awardAchievement(player, "conservationist");
        }
        data.putInt(CONSERVATIONIST_KEY, planted);
    }

    /// 使用先进战斗技术后，两卷都启用时达成“训练日”。
    public static void trainingDay(ServerLevel level) {
        if (NPCSpawner.INSTANCE.isAdvancedCombatTechniquesUsed() && NPCSpawner.INSTANCE.isAdvancedCombatTechniquesVolumeTwoUsed()) {
            for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
                awardAchievement(player, "training_day");
            }
        }
    }

    /// 只有“为所欲为”种子下墓石才会造成伤害，因此命中即代表用墓石砸死了别人。
    public static void graveMistake(DamageSource damageSource, LivingEntity victim) {
        if (damageSource.getDirectEntity() instanceof TombstoneBoulderEntity tombstone
                && tombstone.getOwner() instanceof ServerPlayer owner && owner != victim
        ) awardAchievement(owner, "grave_mistake");
    }

    /// 冲出世界顶部：Remix／天顶种子，或“为所欲为”种子下位于二阶段克苏鲁之脑 250 格内。
    /// 满足条件越过顶部边界后先结算成就；非无敌模式的玩家会因此死亡（对应原版“单程旅行”）。
    public static void myPeopleNeedMe(ServerPlayer player) {
        if (player.getY() < OverworldUtils.getUltraY()) return;
        boolean canLeaveTop = ModSecretSeeds.DONT_DIG_UP.match(player.server)
                || ModSecretSeeds.GET_FIXED_BOI.match(player.server)
                || ModSecretSeeds.FOR_THE_WORTHY.match(player.server) && isNearPhaseTwoBrain(player);
        if (!canLeaveTop) return;
        awardAchievement(player, "my_people_need_me");
        if (!player.isCreative() && !player.isSpectator() && !player.isInvulnerable()) {
            player.hurt(player.damageSources().fellOutOfWorld(), Float.MAX_VALUE);
        }
    }

    /// 250 格内是否存在第二阶段的克苏鲁之脑。
    private static boolean isNearPhaseTwoBrain(ServerPlayer player) {
        return !player.serverLevel().getEntitiesOfClass(
                BrainOfCthulhu.class,
                player.getBoundingBox().inflate(250.0D * 2 / 3),
                BrainOfCthulhu::isPhase2
        ).isEmpty();
    }
}
