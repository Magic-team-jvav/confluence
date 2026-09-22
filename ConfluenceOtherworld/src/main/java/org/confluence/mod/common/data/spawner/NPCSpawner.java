package org.confluence.mod.common.data.spawner;

import PortLib.extensions.com.mojang.serialization.DataResult.PortDataResultExtension;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import org.confluence.lib.color.GlobalColors;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.data.saved.IGlobalData;
import org.confluence.lib.common.worldgen.structure.SimpleTemplatePiece;
import org.confluence.lib.util.LibCodecUtils;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.data.GamePhase;
import org.confluence.mod.common.data.saved.Bestiary;
import org.confluence.mod.common.data.saved.HouseHandler;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.entity.boss.Skeletron;
import org.confluence.mod.common.entity.npc.AnglerNPC;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.OldManNPC;
import org.confluence.mod.common.entity.npc.TravelingMerchantNPC;
import org.confluence.mod.common.entity.npc.house.HouseValidater;
import org.confluence.mod.common.gameevent.GameEventSystem;
import org.confluence.mod.common.gameevent.GoblinArmyGameEvent;
import org.confluence.mod.common.gameevent.PartyGameEvent;
import org.confluence.mod.common.gameevent.SolarEclipseGameEvent;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.entity.BossEntities;
import org.confluence.mod.common.init.entity.DevelopmentSpawnPolicy;
import org.confluence.mod.common.init.entity.NpcEntities;
import org.confluence.mod.common.item.common.CoinItem;
import org.confluence.mod.common.worldgen.structure.DungeonStructure;
import org.confluence.mod.mixed.IMinecraftServer;
import org.confluence.mod.mixed.IStructureStart;
import org.confluence.mod.mixed.IWorldOptions;
import org.confluence.mod.util.OverworldUtils;
import org.confluence.mod.util.PlayerUtils;
import org.mesdag.portlib.wrapper.common.PortTags;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;

import java.util.*;
import java.util.function.Predicate;

/// 注：NPC默认生成在对应玩家出生点
public enum NPCSpawner implements IGlobalData {
    INSTANCE;
    public static final int CURRENT_VERSION = 1;
    /// NPC「已存在」判定半径（方块）。玩家活动区跨 region 时，同一个 NPC 会在相邻 region 被
    /// 重复生成；除了 region 标记之外再按附近真实实体做一次兜底检查。
    public static final int NPC_PRESENCE_CHECK_RADIUS = 128;
    /// 城镇（NPC 小镇）判定：region 内已入住 NPC 数量门槛。泰拉为 3。
    public static final int TOWN_NPC_THRESHOLD = 3;
    public static final Codec<Map<Region, Reference2BooleanMap<EntityType<?>>>> NPC_ALIVE_CODEC;
    public static final Codec<Set<EntityType<?>>> NPC_SPAWNED_CODEC;

    static {
        Codec<EntityType<?>> entityTypeCodec = BuiltInRegistries.ENTITY_TYPE.byNameCodec();
        NPC_ALIVE_CODEC = LibCodecUtils.notStringKeyMap(
                "region", Region.CODEC,
                "alive", LibCodecUtils.reference2BooleanMap(entityTypeCodec));
        NPC_SPAWNED_CODEC = entityTypeCodec.listOf().xmap(ReferenceOpenHashSet::new, ReferenceArrayList::new);
    }

    private Map<Region, Reference2BooleanMap<EntityType<?>>> npcAlive = new Object2ObjectOpenHashMap<>();
    /// UUID 记录保留卸载实体；旧区域表继续作为可修改的兼容接口与旧存档保守占用。
    private final Map<UUID, ResidentRecord> residents = new HashMap<>();
    private final Set<UUID> removalNotified = new HashSet<>();

    private record ResidentRecord(EntityType<?> type, Region region, GlobalPos location,
                                  boolean rescued) {}

    public void trackNPC(BaseNPC npc) {
        if (npc.level().isClientSide || npc instanceof OldManNPC || !npc.isAlive() || npc.deathTime > 0
                || removalNotified.contains(npc.getUUID())) return;
        ResidentRecord next = new ResidentRecord(npc.getType(), npc.getRegion(),
                GlobalPos.of(npc.level().dimension(), npc.blockPosition()),
                !npc.shouldInteract() && (!(npc instanceof AnglerNPC angler) || angler.isWakeUp()));
        ResidentRecord previous = residents.put(npc.getUUID(), next);
        if (previous != null && (!previous.region.equals(next.region) || previous.type != next.type
                || previous.location.dimension() != next.location.dimension()))
            refreshResidentPresence(previous);
        if (next.location.dimension() == OverworldUtils.dimension())
            getRegionAliveDetails(next.region).put(next.type, true);
    }

    /// 只移除这一实体，不会清除同区域其他同类 NPC 的占用。
    public void forgetNPC(BaseNPC npc) {
        if (npc.level().isClientSide || npc instanceof OldManNPC) return;
        ResidentRecord previous = residents.remove(npc.getUUID());
        if (previous != null) refreshResidentPresence(previous);
        else refreshResidentPresence(new ResidentRecord(npc.getType(), npc.getRegion(),
                GlobalPos.of(npc.level().dimension(), npc.blockPosition()), false));
    }

    private void refreshResidentPresence(ResidentRecord previous) {
        if (previous.location.dimension() != OverworldUtils.dimension()) return;
        boolean occupied = residents.values().stream().anyMatch(record -> record.type == previous.type
                && record.region.equals(previous.region) && record.location.dimension() == previous.location.dimension());
        getRegionAliveDetails(previous.region).put(previous.type, occupied);
    }
    /// 老人按地牢入口独立保存，不能以城镇 region 或全世界的 NPC 类型判重。
    private final Map<GlobalPos, DungeonResident> dungeonResidents = new Object2ObjectOpenHashMap<>();
    /// 生成过的NPC，可用于NPC复活而无需再次满足条件
    private Set<EntityType<?>> npcSpawned = new ReferenceOpenHashSet<>();
    private boolean isAdvancedCombatTechniquesUsed = false; // 先进战斗技术
    private boolean isAdvancedCombatTechniquesVolumeTwoUsed = false; // 先进战斗技术：卷二
    private boolean isPeddlersSatchelUsed = false; // 商贩背包

    public Iterable<EntityType<?>> getNpcSpawned() {
        return npcSpawned;
    }

    public void setAdvancedCombatTechniquesUsed(boolean used) {
        this.isAdvancedCombatTechniquesUsed = used;
    }

    public boolean isAdvancedCombatTechniquesUsed() {
        return isAdvancedCombatTechniquesUsed;
    }

    public void setAdvancedCombatTechniquesVolumeTwoUsed(boolean used) {
        this.isAdvancedCombatTechniquesVolumeTwoUsed = used;
    }

    public boolean isAdvancedCombatTechniquesVolumeTwoUsed() {
        return isAdvancedCombatTechniquesVolumeTwoUsed;
    }

    public void setPeddlersSatchelUsed(boolean used) {
        this.isPeddlersSatchelUsed = used;
    }

    public boolean isPeddlersSatchelUsed() {
        return isPeddlersSatchelUsed;
    }

    public int getAliveNpcCount(Region region, Predicate<EntityType<?>> filter) {
        Reference2BooleanMap<EntityType<?>> map = npcAlive.get(region);
        if (map == null) return 0;
        int count = 0;
        for (Reference2BooleanMap.Entry<EntityType<?>> entry : map.reference2BooleanEntrySet()) {
            if (entry.getBooleanValue() &&
                    entry.getKey() != NpcEntities.SKELETON_MERCHANT.get() &&
                    NpcEntities.TOWN_SLIMES.stream().noneMatch(type -> type.get() == entry.getKey()) &&
                    filter.test(entry.getKey())
            ) {
                count++;
            }
        }
        return count;
    }

    public Reference2BooleanMap<EntityType<?>> getRegionAliveDetails(Region region) {
        return npcAlive.computeIfAbsent(region, region1 -> new Reference2BooleanOpenHashMap<>());
    }

    public boolean hasNPCAlive(Region region, EntityType<?> entityType) {
        Reference2BooleanMap<EntityType<?>> map = npcAlive.get(region);
        return map != null && map.getOrDefault(entityType, false);
    }

    /// 任意 region 的存活标记。
    ///
    /// 保留全局查询接口；普通城镇生成不使用它限制其他区域的同类 NPC。
    public boolean isNpcAliveAnywhere(EntityType<?> entityType) {
        for (Reference2BooleanMap<EntityType<?>> map : npcAlive.values()) {
            if (map.getOrDefault(entityType, false)) return true;
        }
        return false;
    }

    /// 附近是否已经存在该类型的真实实体。
    ///
    /// `npcAlive` 是持久化的布尔表，可能因实体卸载 / 死亡未走 {@link #onNPCRemoved} 而失真，
    /// 所以唯一性判定要拿真实实体兜底一次。
    public static boolean isNpcNearby(Level level, BlockPos pos, EntityType<?> entityType, double radius) {
        AABB area = new AABB(pos).inflate(radius);
        for (BaseNPC npc : level.getEntitiesOfClass(BaseNPC.class, area)) {
            if (npc.isAlive() && npc.getType() == entityType) return true;
        }
        return false;
    }

    /// 按所属区域防重复，并用已加载实体兜底；不同区域允许存在同类 NPC。
    public boolean isNpcAlreadyPresent(ServerLevel level, BlockPos pos, EntityType<?> entityType) {
        Region region = new Region(pos);
        if (hasNPCAlive(region, entityType)) return true;
        for (Entity entity : level.getAllEntities()) {
            if (entity instanceof BaseNPC npc && npc.isAlive() && npc.getType() == entityType
                    && region.equals(npc.getRegion())) return true;
        }
        return false;
    }

    /// 城镇（NPC 小镇）判定：该 region 内已入住的 NPC 数量是否达到门槛。
    ///
    /// 迷你生物群系里的「小镇」标记直接用这个；`getAliveNpcCount` 已经排除了骷髅商人与城镇史莱姆。
    public boolean isTown(Region region) {
        return getAliveNpcCount(region, type -> true) >= TOWN_NPC_THRESHOLD;
    }

    public void setNPCAlive(Region region, EntityType<?> entityType, boolean alive) {
        if (alive) {
            getRegionAliveDetails(region).put(entityType, true);
            addSpawned(entityType);
        } else {
            Reference2BooleanMap<EntityType<?>> map = npcAlive.get(region);
            if (map != null && map.getBoolean(entityType)) {
                map.put(entityType, false);
            }
        }
    }

    /// 旅商与老人不会加进去
    public void addSpawned(EntityType<?> entityType) {
        if (entityType != NpcEntities.TRAVELING_MERCHANT.get() && entityType != NpcEntities.OLD_MAN.get()
                && entityType != NpcEntities.SKELETON_MERCHANT.get()) {
            npcSpawned.add(entityType);
        }
    }

    public void moveNPCToAnotherRegion(BaseNPC living, Region from, Region to) {
        EntityType<?> entityType = living.getType();
        trackNPC(living);
        if (!from.equals(to)) {
            forgetNPC(living);
        }
        setNPCAlive(to, entityType, true);
        living.setRegion(to);
        trackNPC(living);
        applyBenedictions(living);
    }

    public void onNPCAdded(BaseNPC living) {
        living.setRegion(new Region(living.chunkPosition()));
        removalNotified.remove(living.getUUID());
        trackNPC(living);
        setNPCAlive(living.getRegion(), living.getType(), true);
        applyBenedictions(living);
        broadcastMessageToRegion(living.level(), living, Component.translatable("event.confluence.npc.arrived", living.getType().getDescription(), living.getName()).withColor(GlobalColors.NPC_ARRIVED.get()));
    }

    public void applyBenedictions(BaseNPC living) {
        if (isAdvancedCombatTechniquesUsed()) {
            applyAdvancedCombatTechniques(living, Confluence.asResource("advanced_combat_techniques"));
        }
        if (isAdvancedCombatTechniquesVolumeTwoUsed()) {
            applyAdvancedCombatTechniques(living, Confluence.asResource("advanced_combat_techniques_volume_two"));
        }
    }

    /// [考据](https://terraria.wiki.gg/zh/wiki/%E7%8A%B6%E6%80%81%E8%AE%AF%E6%81%AF#NPC)
    /// - 当 NPC 死亡时，会显示讯息“<NPC的类型><NPC的名字>被杀死了……”。
    ///   - 渔夫、公主、或城镇宠物死亡时，会改为显示讯息“<渔夫/宠物/公主的名字>已离开！”。
    ///   - 两种情况下，都会使用 #ff1919 颜色。
    public void onNPCRemoved(BaseNPC living) {
        if (!removalNotified.add(living.getUUID())) return;
        HouseHandler.INSTANCE.removeHouse(living.level().dimension(), living.getUUID());
        if (living instanceof OldManNPC oldMan) {
            dungeonEntityRemoved(oldMan);
            return;
        }
        forgetNPC(living);
        if (living.shouldInteract() || living.getType() == NpcEntities.SKELETON_MERCHANT.get())
            return;
        if (CommonConfigs.BROADCAST_NPC_MSG.get() && living.getType() != NpcEntities.OLD_MAN.get()) {
            MutableComponent message;
            if (living.isTownPet()) {
                message = Component.translatable("event.confluence.npc.left", living.getName()).withColor(GlobalColors.NPC_SLAIN.get());
            } else if (living instanceof AnglerNPC angler) {
                if (!angler.isWakeUp()) return; // 渔夫未唤醒时死亡不广播
                message = Component.translatable("event.confluence.npc.left", living.getName()).withColor(GlobalColors.NPC_SLAIN.get());
            } else if (living instanceof TravelingMerchantNPC && living.isAlive()) {
                message = Component.translatable("event.confluence.traveling_merchant.departed", living.getName()).withColor(GlobalColors.NPC_ARRIVED.get());
            } else if (!living.hasCustomName()) {
                message = Component.translatable("event.confluence.npc.slain.unnamed", living.getType().getDescription()).withColor(GlobalColors.NPC_SLAIN.get());
            } else {
                message = Component.translatable("event.confluence.npc.slain", living.getType().getDescription(), living.getName()).withColor(GlobalColors.NPC_SLAIN.get());
            }
            broadcastMessageToRegion(living.level(), living, message);
        }
    }

    @Override
    public void decode(CompoundTag tag) {
        residents.clear();
        removalNotified.clear();
        for (Tag element : tag.getList("ResidentRecords", Tag.TAG_COMPOUND)) {
            CompoundTag entry = (CompoundTag) element;
            if (!entry.hasUUID("UUID")) continue;
            ResourceLocation typeId = ResourceLocation.tryParse(entry.getString("Type"));
            if (typeId == null) continue;
            BuiltInRegistries.ENTITY_TYPE.getOptional(typeId).ifPresent(type ->
                    GlobalPos.CODEC.parse(NbtOps.INSTANCE, entry.get("Location")).result().ifPresent(location ->
                            residents.put(entry.getUUID("UUID"), new ResidentRecord(type,
                                    new Region(entry.getLong("Region")), location, entry.getBoolean("Rescued")))));
        }
        dungeonResidents.clear();
        for (Tag element : tag.getList("DungeonResidents", Tag.TAG_COMPOUND)) {
            CompoundTag entry = (CompoundTag) element;
            GlobalPos.CODEC.parse(NbtOps.INSTANCE, entry.get("Entrance")).result().ifPresent(entrance -> {
                DungeonResident resident = new DungeonResident();
                if (entry.hasUUID("OldMan")) resident.oldMan = entry.getUUID("OldMan");
                if (entry.hasUUID("Skeletron")) resident.skeletron = entry.getUUID("Skeletron");
                resident.respawnAfter = entry.getLong("RespawnAfter");
                dungeonResidents.put(entrance, resident);
            });
        }
        PortDataResultExtension.ifSuccess(NPC_ALIVE_CODEC.parse(NbtOps.INSTANCE, tag.get("NpcAlive")), result -> this.npcAlive = new Object2ObjectOpenHashMap<>(result));
        PortDataResultExtension.ifSuccess(NPC_SPAWNED_CODEC.parse(NbtOps.INSTANCE, tag.get("NpcSpawned")), result -> this.npcSpawned = new ObjectOpenHashSet<>(result));
        this.isAdvancedCombatTechniquesUsed = tag.getBoolean("AdvancedCombatTechniquesUsed");
        this.isAdvancedCombatTechniquesVolumeTwoUsed = tag.getBoolean("AdvancedCombatTechniquesVolumeTwoUsed");
        this.isPeddlersSatchelUsed = tag.getBoolean("PeddlersSatchelUsed");
    }

    @Override
    public void encode(CompoundTag tag) {
        ListTag records = new ListTag();
        residents.forEach((uuid, record) -> {
            CompoundTag entry = new CompoundTag();
            entry.putUUID("UUID", uuid);
            entry.putString("Type", BuiltInRegistries.ENTITY_TYPE.getKey(record.type).toString());
            entry.putLong("Region", record.region.toLong());
            entry.putBoolean("Rescued", record.rescued);
            GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, record.location).result().ifPresent(value -> entry.put("Location", value));
            records.add(entry);
        });
        tag.put("ResidentRecords", records);
        ListTag residents = new ListTag();
        dungeonResidents.forEach((entrance, resident) -> {
            CompoundTag entry = new CompoundTag();
            GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, entrance).result().ifPresent(value -> entry.put("Entrance", value));
            if (resident.oldMan != null) entry.putUUID("OldMan", resident.oldMan);
            if (resident.skeletron != null) entry.putUUID("Skeletron", resident.skeletron);
            entry.putLong("RespawnAfter", resident.respawnAfter);
            residents.add(entry);
        });
        tag.put("DungeonResidents", residents);
        tag.putInt("Version", CURRENT_VERSION);
        Iterator<Map.Entry<Region, Reference2BooleanMap<EntityType<?>>>> iterator = npcAlive.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Region, Reference2BooleanMap<EntityType<?>>> next = iterator.next();
            next.getValue().reference2BooleanEntrySet().removeIf(entry -> !entry.getBooleanValue());
            if (next.getValue().isEmpty()) {
                iterator.remove();
            }
        }
        PortDataResultExtension.ifSuccess(NPC_ALIVE_CODEC.encodeStart(NbtOps.INSTANCE, npcAlive), nbt -> tag.put("NpcAlive", nbt));
        PortDataResultExtension.ifSuccess(NPC_SPAWNED_CODEC.encodeStart(NbtOps.INSTANCE, npcSpawned), nbt -> tag.put("NpcSpawned", nbt));
        tag.putBoolean("AdvancedCombatTechniquesUsed", isAdvancedCombatTechniquesUsed);
        tag.putBoolean("AdvancedCombatTechniquesVolumeTwoUsed", isAdvancedCombatTechniquesVolumeTwoUsed);
        tag.putBoolean("PeddlersSatchelUsed", isPeddlersSatchelUsed);
    }

    @Override
    public String serializeKey() {
        return "confluence:npc_spawner";
    }

    @Override
    public void clear() {
        residents.clear();
        removalNotified.clear();
        dungeonResidents.clear();
        npcAlive.clear();
        npcSpawned.clear();
        this.isAdvancedCombatTechniquesUsed = false;
        this.isAdvancedCombatTechniquesVolumeTwoUsed = false;
        this.isPeddlersSatchelUsed = false;
    }

    /// 醉酒世界则会生成派对女孩
    /// todo 其它秘密种子的特殊生成
    public void trySpawnGuide(ServerPlayer player) {
        ServerLevel serverLevel = player.serverLevel();
        if (serverLevel.dimension() == OverworldUtils.dimension()) {
            BlockPos pos = getNpcSpawnPos(player);
            Region region = new Region(pos);
            if (IMinecraftServer.matchesSecretFlag(player.server, IWorldOptions.DW_MASK)) {
                if (!hasNPCAlive(region, NpcEntities.PARTY_GIRL.get())) {
                    spawnAtPos(serverLevel, pos, NpcEntities.PARTY_GIRL.get());
                }
            } else {
                if (!hasNPCAlive(region, NpcEntities.GUIDE.get())) {
                    spawnAtPos(serverLevel, pos, NpcEntities.GUIDE.get());
                }
            }
        }
    }

    public void checkNpcRespawn(ServerLevel serverLevel) {
        if (GameEventSystem.shouldDenyNatureSpawn()) return;
        Set<Region> processedRegions = new ObjectOpenHashSet<>();
        List<ServerPlayer> players = serverLevel.players();
        int start = players.isEmpty() ? 0 : (int) ((serverLevel.getGameTime() / CommonConfigs.NPC_SPAWN_INTERVAL.get()) % players.size());
        for (int index = 0; index < players.size(); index++) {
            ServerPlayer player = players.get((start + index) % players.size());
            if (player.isSpectator()) continue;
            trySpawnUndergroundVisitor(player);
            if (!LibDateUtils.isDay(LibDateUtils.getDayTime(serverLevel))) continue;
            BlockPos pos = getNpcSpawnPos(player);
            Region region = new Region(pos);
            if (processedRegions.contains(region)) continue;
            /// 只有成功生成才占用本轮名额，不能挡住同区域其他玩家的资格检查。
            if (trySpawnForPlayer(player, pos, region)) processedRegions.add(region);
        }
    }

    private boolean trySpawnForPlayer(ServerPlayer player, BlockPos pos, Region region) {
        ServerLevel serverLevel = player.serverLevel();
        if (trySpawnTravelingMerchant(player, pos, region)) return true;
        if (trySpawnClothier(player, pos, region)) return true;
        if (trySpawnMechanic(player, pos, region)) return true;
        for (EntityType<?> entityType : npcSpawned) {
            if (!DevelopmentSpawnPolicy.allowsAutomaticSpawn(entityType)) continue;
            if (!hasNPCAlive(region, entityType) && spawnAtPos(serverLevel, pos, entityType)) {
                return true;
            }
        }
        if (trySpawnMerchant(player, pos, region)) return true;
        if (trySpawnNurse(player, pos, region)) return true;
        if (trySpawnDemolitionist(player, pos, region)) return true;
        if (trySpawnDyeTrader(player, pos, region)) return true;
        if (trySpawnAngler(player)) return true;
        if (trySpawnZoologist(player, pos, region)) return true;
        if (trySpawnDryad(player, pos, region)) return true;
        if (trySpawnPainter(player, pos, region)) return true;
        // 高尔夫球手
        if (trySpawnArmsDealer(player, pos, region)) return true;
        // 酒馆老板
        // 发型师
        if (trySpawnGoblinTinkerer(player, pos, region)) return true;
        if (trySpawnWitchDoctor(player, pos, region)) return true;
        if (trySpawnPartyGirl(player, pos, region)) return true;
        if (trySpawnWizard(player, pos, region)) return true;
        // 税收官
        if (trySpawnTruffle(player, pos, region)) return true;
        if (!Confluence.UNRELEASED_SPAWNS) return false;
        if (trySpawnNerdySlime(serverLevel, region, pos)) return true;
        if (trySpawnCoolSlime(serverLevel, pos, region)) return true;
        // 海盗
        if (trySpawnSteampumker(serverLevel, region, pos)) return true;
        if (trySpawnCyborg(serverLevel, region, pos)) return true;

        return false;
    }

    private boolean trySpawnCyborg(ServerLevel serverLevel, Region region, BlockPos pos) {
        return !hasNPCAlive(region, NpcEntities.CYBORG.get()) && KillBoard.INSTANCE.isDefeated(BossEntities.PLANTERA.get())
                && spawnAtPos(serverLevel, pos, NpcEntities.CYBORG.get());
    }

    private boolean trySpawnSteampumker(ServerLevel serverLevel, Region region, BlockPos pos) {
        return !hasNPCAlive(region, NpcEntities.STEAMPUNKER.get()) && KillBoard.INSTANCE.isAnyMechBossDefeated()
                && spawnAtPos(serverLevel, pos, NpcEntities.STEAMPUNKER.get());
    }

    private boolean trySpawnNerdySlime(ServerLevel serverLevel, Region region, BlockPos pos) {
        return !hasNPCAlive(region, NpcEntities.NERDY_SLIME.get()) && KillBoard.INSTANCE.isDefeated(BossEntities.KING_SLIME.get())
                && spawnAtPos(serverLevel, pos, NpcEntities.NERDY_SLIME.get());
    }

    private boolean trySpawnCoolSlime(ServerLevel level, BlockPos pos, Region region) {
        if (!PartyGameEvent.INSTANCE.isNatural() ||
                isNpcAlreadyPresent(level, pos, NpcEntities.COOL_SLIME.get())
        ) return false;
        var slime = NpcEntities.COOL_SLIME.get().create(level);
        if (slime == null) return false;
        Set<BlockPos> candidates = new HashSet<>();
        candidates.add(pos);
        for (var house : HouseHandler.INSTANCE.getOrCreateHouses(level.dimension(), region).values())
            candidates.add(house.center());
        for (BlockPos candidate : candidates) {
            if (!level.isLoaded(candidate)) continue;
            var house = HouseValidater.scan(level, candidate).make(slime.getUUID());
            if (!house.isValid() || HouseHandler.INSTANCE.isOccupiedByOther(level.dimension(), house, slime.getUUID(), true))
                continue;
            Optional<BlockPos> safePos = findSafeSpawnLocation(level, house.center(), slime);
            if (safePos.isEmpty()) continue;
            BlockPos spawn = safePos.get();
            if (!house.contains(spawn) || !level.noCollision(slime, slime.getBoundingBox().move(spawn.getBottomCenter())))
                continue;
            slime.setPos(spawn.getBottomCenter());
            slime.setHouse(house);
            if (!level.addFreshEntity(slime)) return false;
            HouseHandler.INSTANCE.setHouse(slime, house);
            onNPCAdded(slime);
            return true;
        }
        return false;
    }

    private boolean trySpawnUndergroundVisitor(ServerPlayer player) {
        if (!Confluence.UNRELEASED_SPAWNS) return false;
        ServerLevel level = player.serverLevel();
        if (level.canSeeSky(player.blockPosition()) || player.getY() >= level.getSeaLevel() - 8)
            return false;
        boolean golfer = !npcSpawned.contains(NpcEntities.GOLFER.get()) && level.getBiome(player.blockPosition()).is(PortTags.Biomes.IS_DESERT);
        EntityType<? extends BaseNPC> type = golfer ? NpcEntities.GOLFER.get() : NpcEntities.SKELETON_MERCHANT.get();
        Region region = new Region(player.blockPosition());
        if (isNpcAlreadyPresent(level, player.blockPosition(), type) || player.getRandom().nextInt(8) != 0)
            return false;
        BaseNPC npc = type.create(level);
        if (npc == null) return false;
        for (int attempt = 0; attempt < 32; attempt++) {
            BlockPos candidate = player.blockPosition().offset(player.getRandom().nextInt(41) - 20, player.getRandom().nextInt(13) - 6, player.getRandom().nextInt(41) - 20);
            if (!level.isLoaded(candidate) || candidate.distSqr(player.blockPosition()) < 64 || level.canSeeSky(candidate)
                    || !level.getFluidState(candidate).isEmpty()
                    || !level.getBlockState(candidate.below()).isFaceSturdy(level, candidate.below(), Direction.UP)
                    || golfer && !level.getBiome(candidate).is(PortTags.Biomes.IS_DESERT)) continue;
            npc.setPos(candidate.getBottomCenter());
            if (!level.noCollision(npc)) continue;
            npc.setShouldInteract(golfer);
            npc.setRegion(region);
            if (!level.addFreshEntity(npc)) return false;
            getRegionAliveDetails(region).put(type, true);
            return true;
        }
        return false;
    }

    private boolean trySpawnTruffle(ServerPlayer player, BlockPos pos, Region region) {
        if (!hasNPCAlive(region, NpcEntities.TRUFFLE.get())) {
            if (IMinecraftServer.isHardmode(player.server)) {
                return spawnAtPos(player.serverLevel(), pos, NpcEntities.TRUFFLE.get());
            }
        }
        return false;
    }

    private boolean trySpawnWizard(ServerPlayer player, BlockPos pos, Region region) {
        if (!hasNPCAlive(region, NpcEntities.WIZARD.get())) {
            if (IMinecraftServer.isHardmode(player.server)) {
                return spawnAtPos(player.serverLevel(), pos, NpcEntities.WIZARD.get());
            }
        }
        return false;
    }

    private boolean trySpawnZoologist(ServerPlayer player, BlockPos pos, Region region) {
        if (!hasNPCAlive(region, NpcEntities.ZOOLOGIST.get())) {
            if (Bestiary.INSTANCE.getUnlockedCount() >= 34) {
                return spawnAtPos(player.serverLevel(), pos, NpcEntities.ZOOLOGIST.get());
            }
        }
        return false;
    }

    /// 醉酒世界则会生成向导
    private boolean trySpawnPartyGirl(ServerPlayer player, BlockPos pos, Region region) {
        if (IMinecraftServer.matchesSecretFlag(player.server, IWorldOptions.DW_MASK)) {
            if (!hasNPCAlive(region, NpcEntities.GUIDE.get())) {
                return spawnAtPos(player.serverLevel(), pos, NpcEntities.GUIDE.get());
            }
        } else if (!hasNPCAlive(region, NpcEntities.PARTY_GIRL.get())) {
            if (player.getRandom1211().nextInt(40) == 0 && getAliveNpcCount(region, entityType -> true) >= 14) {
                return spawnAtPos(player.serverLevel(), pos, NpcEntities.PARTY_GIRL.get());
            }
        }
        return false;
    }

    private boolean trySpawnTravelingMerchant(ServerPlayer player, BlockPos pos, Region region) {
        if (!hasNPCAlive(region, NpcEntities.TRAVELING_MERCHANT.get())) {
            if (!GameEventSystem.INSTANCE.isEventStarted(SolarEclipseGameEvent.KEY) &&
                    LibDateUtils.isWithinDayTime(LibDateUtils._04$30, LibDateUtils._12$00, player.level())
            ) {
                int bound = 30000 / CommonConfigs.NPC_SPAWN_INTERVAL.get(); // 6.25分钟内生成期望为22.12%
                if (player.getRandom1211().nextInt(bound) == 0 && getAliveNpcCount(region, entityType -> entityType != NpcEntities.OLD_MAN.get()) >= 2) {
                    return spawnAtPos(player.serverLevel(), pos, NpcEntities.TRAVELING_MERCHANT.get());
                }
            }
        }
        return false;
    }

    /// 省去“所有玩家钱币总和50银”的条件，改为单玩家
    private boolean trySpawnMerchant(ServerPlayer player, BlockPos pos, Region region) {
        if (!hasNPCAlive(region, NpcEntities.MERCHANT.get())) {
            if (PlayerUtils.getMoney(player, true) >= 50 * CoinItem.UPGRADES_COUNT) {
                return spawnAtPos(player.serverLevel(), pos, NpcEntities.MERCHANT.get());
            }
        }
        return false;
    }

    private boolean trySpawnNurse(ServerPlayer player, BlockPos pos, Region region) {
        if (!hasNPCAlive(region, NpcEntities.NURSE.get())) {
            if (player.getMaxHealth() > 20 && hasNPCAlive(region, NpcEntities.MERCHANT.get())) {
                return spawnAtPos(player.serverLevel(), pos, NpcEntities.NURSE.get());
            }
        }
        return false;
    }

    private boolean trySpawnDemolitionist(ServerPlayer player, BlockPos pos, Region region) {
        if (!hasNPCAlive(region, NpcEntities.DEMOLITIONIST.get())) {
            if (player.getInventory().hasAnyMatching(stack -> stack.is(ModTags.Items.EXPLOSIVE)) && hasNPCAlive(region, NpcEntities.MERCHANT.get())) {
                return spawnAtPos(player.serverLevel(), pos, NpcEntities.DEMOLITIONIST.get());
            }
        }
        return false;
    }

    // todo 可用于做染料的物品
    private boolean trySpawnDyeTrader(ServerPlayer player, BlockPos pos, Region region) {
        if (!hasNPCAlive(region, NpcEntities.DYE_TRADER.get())) {
            if (hasNPCAlive(region, NpcEntities.MERCHANT.get()) &&
                    player.getInventory().hasAnyMatching(stack -> stack.is(Tags.Items.DYES))
            ) {
                return spawnAtPos(player.serverLevel(), pos, NpcEntities.DYE_TRADER.get());
            }
        }
        return false;
    }

    /// 先计入NPC列表，待玩家交互了再转移（睡眠状态，交互后唤醒）
    private boolean trySpawnAngler(ServerPlayer player) {
        BlockPos playerPos = player.blockPosition();
        Region playerRegion = new Region(playerPos);
        if (!isNpcAlreadyPresent(player.serverLevel(), playerPos, NpcEntities.ANGLER.get())) { // 保证玩家转移渔夫区域时不再生成新的
            Level level = player.serverLevel();
            Pair<BlockPos, Holder<Biome>> closestBiome3d = player.serverLevel().findClosestBiome3d(biome -> biome.is(PortTags.Biomes.IS_OCEAN), playerPos, 64, 8, 64);
            if (closestBiome3d != null) {
                BaseNPC npc = NpcEntities.ANGLER.get().create(level);
                if (npc != null) {
                    BlockPos spawnPos = findAnglerSpawnPos(level, playerPos, closestBiome3d.getFirst(), npc);
                    if (spawnPos == null) return false;
                    npc.setPos(spawnPos.getBottomCenter());
                    if (!level.addFreshEntity(npc)) return false;
                    npc.setRegion(playerRegion);
                    getRegionAliveDetails(playerRegion).put(NpcEntities.ANGLER.get(), true);
                    trackNPC(npc);
                    return true;
                }
            }
        }
        return false;
    }

    private static BlockPos findAnglerSpawnPos(Level level, BlockPos playerPos, BlockPos oceanPos, BaseNPC npc) {
        AABB bounds = npc.getDimensions(Pose.STANDING).makeBoundingBox(Vec3.ZERO);
        RandomSource random = level.random;
        int seaLevel = level.getSeaLevel();
        for (int attempt = 0; attempt < 32; attempt++) {
            double angle = random.nextDouble() * Mth.TWO_PI;
            int distance = 8 + random.nextInt(17);
            int x = oceanPos.getX() + Mth.floor(Mth.cos((float) angle) * distance);
            int z = oceanPos.getZ() + Mth.floor(Mth.sin((float) angle) * distance);
            BlockPos candidate = new BlockPos(x, seaLevel, z);
            long dx = candidate.getX() - playerPos.getX();
            long dz = candidate.getZ() - playerPos.getZ();
            if (dx * dx + dz * dz < 8 * 8
                    || !level.getBiome(candidate).is(PortTags.Biomes.IS_OCEAN)
                    || !level.getFluidState(candidate.below()).is(FluidTags.WATER)
                    || !level.noCollision(npc, bounds.move(candidate.getBottomCenter()))) {
                continue;
            }
            return candidate;
        }
        return null;
    }

    private boolean trySpawnDryad(ServerPlayer player, BlockPos pos, Region region) {
        if (!hasNPCAlive(region, NpcEntities.DRYAD.get())) {
            if (KillBoard.INSTANCE.isAnyDefeated(
                    BossEntities.EYE_OF_CTHULHU.get(),
                    BossEntities.EATER_OF_WORLDS.get(),
                    BossEntities.BRAIN_OF_CTHULHU.get(),
                    BossEntities.SKELETRON.get()
            )) {
                return spawnAtPos(player.serverLevel(), pos, NpcEntities.DRYAD.get());
            }
        }
        return false;
    }

    private boolean trySpawnWitchDoctor(ServerPlayer player, BlockPos pos, Region region) {
        if (!hasNPCAlive(region, NpcEntities.WITCH_DOCTOR.get())) {
            if (KillBoard.INSTANCE.isDefeated(BossEntities.QUEEN_BEE.get())) {
                return spawnAtPos(player.serverLevel(), pos, NpcEntities.WITCH_DOCTOR.get());
            }
        }
        return false;
    }

    private boolean trySpawnPainter(ServerPlayer player, BlockPos pos, Region region) {
        Reference2BooleanMap<EntityType<?>> map = npcAlive.get(region);
        if (map != null && !map.getOrDefault(NpcEntities.PAINTER.get(), false)) {
            if (getAliveNpcCount(region, type -> true) >= 8) {
                return spawnAtPos(player.serverLevel(), pos, NpcEntities.PAINTER.get());
            }
        }
        return false;
    }

    private boolean trySpawnArmsDealer(ServerPlayer player, BlockPos pos, Region region) {
        if (!hasNPCAlive(region, NpcEntities.ARMS_DEALER.get())) {
            Predicate<ItemStack> predicate = stack -> stack.is(ModTags.Items.BULLET) || stack.is(ModTags.Items.GUN);
            if (player.getInventory().hasAnyMatching(predicate) || ExtraInventory.of(player).hasAnyMatching(predicate)) {
                return spawnAtPos(player.serverLevel(), pos, NpcEntities.ARMS_DEALER.get());
            }
        }
        return false;
    }

    private boolean trySpawnGoblinTinkerer(ServerPlayer player, BlockPos pos, Region region) {
        if (!hasNPCAlive(region, NpcEntities.GOBLIN_TINKERER.get())) {
            if (KillBoard.INSTANCE.isDefeated(GoblinArmyGameEvent.KEY)) {
                return spawnAtPos(player.serverLevel(), pos, NpcEntities.GOBLIN_TINKERER.get());
            }
        }
        return false;
    }

    private boolean trySpawnClothier(ServerPlayer player, BlockPos pos, Region region) {
        if (KillBoard.INSTANCE.getGamePhase().isAtLeast(GamePhase.AFTER_SKELETRON)) {
            if (!hasNPCAlive(region, NpcEntities.CLOTHIER.get())) {
                return spawnAtPos(player.serverLevel(), pos, NpcEntities.CLOTHIER.get());
            }
        }
        return false;
    }

    /// 只检查玩家附近已加载的地牢；初次发现昼夜均可，死亡或召唤后等下一个白天。
    public void tickDungeonResidents(ServerLevel level) {
        if (level.getGameTime() % 40 != 0 || !CommonConfigs.DO_NPC_SPAWNING.get()
                || !level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)
                || KillBoard.INSTANCE.getGamePhase().isAtLeast(GamePhase.AFTER_SKELETRON)) return;
        Set<ChunkPos> checked = new HashSet<>();
        for (ServerPlayer player : level.players()) {
            if (player.isSpectator()) continue;
            ChunkPos center = player.chunkPosition();
            for (int x = center.x - 1; x <= center.x + 1; x++) {
                for (int z = center.z - 1; z <= center.z + 1; z++) {
                    ChunkPos chunk = new ChunkPos(x, z);
                    if (!checked.add(chunk) || !level.hasChunk(x, z)) continue;
                    DungeonStructure.iterateDungeon(level, chunk, start -> {
                        for (StructurePiece piece : start.getPieces()) {
                            if (!(piece instanceof SimpleTemplatePiece gate) || !DungeonStructure.GATE.equals(gate.templateName))
                                continue;
                            BlockPos entrance = switch (gate.getRotation()) {
                                case CLOCKWISE_90 -> gate.templatePosition().offset(-15, 6, 15);
                                case CLOCKWISE_180 -> gate.templatePosition().offset(-15, 6, -15);
                                case COUNTERCLOCKWISE_90 ->
                                        gate.templatePosition().offset(15, 6, -15);
                                default -> gate.templatePosition().offset(15, 6, 15);
                            };
                            dungeonResidents.computeIfAbsent(GlobalPos.of(level.dimension(), entrance), key -> new DungeonResident());
                        }
                        return false;
                    });
                }
            }
        }
        for (var entry : dungeonResidents.entrySet()) {
            GlobalPos entrance = entry.getKey();
            if (!entrance.dimension().equals(level.dimension())) continue;
            BlockPos pos = entrance.pos();
            if (level.players().stream().noneMatch(player -> !player.isSpectator() && player.distanceToSqr(pos.getCenter()) < 64 * 64))
                continue;
            DungeonResident resident = entry.getValue();
            /// UUID 未加载不等于死亡；只由死亡或销毁事件清除，防止卸载期间复制老人。
            if (resident.oldMan != null || resident.skeletron != null) continue;
            if (!entranceEntitiesLoaded(level, pos)) continue;
            /// 兼容旧存档：按原始出生点认领老人，不读取旧 region 的存活布尔值。
            OldManNPC existing = null;
            for (Entity entity : level.getAllEntities()) {
                if (entity instanceof OldManNPC oldMan && oldMan.isAlive()
                        && (entrance.equals(oldMan.getDungeonEntrance())
                        || oldMan.getDungeonEntrance() == null && oldMan.getSpawnAtPos().equals(pos))) {
                    existing = oldMan;
                    break;
                }
            }
            if (existing != null) {
                existing.setDungeonEntrance(entrance);
                resident.oldMan = existing.getUUID();
                continue;
            }
            if (resident.respawnAfter != 0 && (level.getDayTime() < resident.respawnAfter || !LibDateUtils.isDay(level)))
                continue;
            OldManNPC oldMan = NpcEntities.OLD_MAN.get().create(level);
            if (oldMan == null) continue;
            oldMan.setPos(pos.getBottomCenter());
            oldMan.setDungeonEntrance(entrance);
            oldMan.setRegion(new Region(pos));
            if (level.noCollision(oldMan) && level.addFreshEntity(oldMan))
                resident.oldMan = oldMan.getUUID();
        }
    }

    /// 入口附近的实体读盘完成后才能判断旧存档里是否缺少老人，不强制加载区块。
    private boolean entranceEntitiesLoaded(ServerLevel level, BlockPos pos) {
        for (int x = (pos.getX() - 16) >> 4; x <= (pos.getX() + 16) >> 4; x++) {
            for (int z = (pos.getZ() - 16) >> 4; z <= (pos.getZ() + 16) >> 4; z++) {
                if (!level.hasChunk(x, z) || !level.areEntitiesLoaded(ChunkPos.asLong(x, z)))
                    return false;
            }
        }
        return true;
    }

    public void oldManSummoned(OldManNPC oldMan, Skeletron boss) {
        GlobalPos entrance = oldMan.getDungeonEntrance();
        if (entrance == null) return;
        DungeonResident resident = dungeonResidents.computeIfAbsent(entrance, key -> new DungeonResident());
        resident.oldMan = null;
        resident.skeletron = boss.getUUID();
    }

    /// 区块卸载不调用；真正移除后只更新对应入口，不影响其他地牢。
    public void dungeonEntityRemoved(Entity entity) {
        for (DungeonResident resident : dungeonResidents.values()) {
            if (entity.getUUID().equals(resident.oldMan) || entity.getUUID().equals(resident.skeletron)) {
                if (entity.getUUID().equals(resident.oldMan)) resident.oldMan = null;
                if (entity.getUUID().equals(resident.skeletron)) resident.skeletron = null;
                long now = entity.level().getDayTime();
                resident.respawnAfter = now + 24000 - Math.floorMod(now - LibDateUtils._04$30, 24000);
            }
        }
    }

    /// 每座地牢一条记录；实体 UUID 同时覆盖存档重载和战斗离开入口的情况。
    private static final class DungeonResident {
        private UUID oldMan;
        private UUID skeletron;
        private long respawnAfter;
    }

    /// 未在区域内的机械师会自动移除（因为机械师距离玩家基地可能很远）
    ///
    /// 首次交互时 BaseNPC.mobInteract 处理 shouldInteract → 加入区域
    private boolean trySpawnMechanic(ServerPlayer player, BlockPos pos, Region region) {
        if (KillBoard.INSTANCE.isDefeated(BossEntities.SKELETRON.get()) && npcSpawned.contains(NpcEntities.MECHANIC.get())) {
            if (!hasNPCAlive(region, NpcEntities.MECHANIC.get())) {
                return spawnAtPos(player.serverLevel(), pos, NpcEntities.MECHANIC.get());
            }
        } else {
            ServerLevel level = player.serverLevel();
            return DungeonStructure.iterateDungeon(level, player.chunkPosition(), structureStart -> {
                if (IStructureStart.of(structureStart).confluence$cachedBoundingBox().isInside(player.blockPosition())) {
                    for (StructurePiece piece : structureStart.getPieces()) {
                        if (piece instanceof SimpleTemplatePiece templatePiece && templatePiece.templateName.endsWith("_dungeon_underground_2_2")) {
                            BlockPos offset = templatePiece.templatePosition().offset(46, 6, -11);
                            Region npcRegion = new Region(offset);
                            if (!hasNPCAlive(npcRegion, NpcEntities.MECHANIC.get())
                                    && !isNpcNearby(level, offset, NpcEntities.MECHANIC.get(), NPC_PRESENCE_CHECK_RADIUS)) {
                                BaseNPC npc = NpcEntities.MECHANIC.get().create(level);
                                if (npc == null) return false;
                                npc.setPos(offset.getBottomCenter());
                                if (!level.addFreshEntity(npc)) return false;
                                npc.setRegion(npcRegion);
                                npc.setShouldInteract(true); // 标记需要交互
                                getRegionAliveDetails(npcRegion).put(NpcEntities.MECHANIC.get(), true);
                                trackNPC(npc);
                                return true;
                            }
                            return false;
                        }
                    }
                }
                return false;
            });
        }
        return false;
    }

    public boolean spawnAtPos(ServerLevel level, BlockPos pos, EntityType<?> entityType) {
        if (isNpcAlreadyPresent(level, pos, entityType)) return false;
        if (!(entityType.create(level) instanceof BaseNPC npc)) return false;
        Optional<BlockPos> safePos = findSafeSpawnLocation(level, pos, npc);
        if (safePos.isEmpty()) return false;
        npc.setPos(safePos.get().getBottomCenter());
        if (!level.noCollision(npc)) return false;
        if (!level.addFreshEntity(npc)) return false;
        if (npc instanceof AnglerNPC angler) {
            angler.setWakeUp(true); // 重生的渔夫默认醒来
        }
        onNPCAdded(npc);
        return true;
    }

    /// 保留旧 API 的非空返回约定；自动生成使用 findSafeSpawnLocation，失败时不生成。
    public static BlockPos adjustSpawnLocation(ServerLevel level, BlockPos pos, BaseNPC npc) {
        return findSafeSpawnLocation(level, pos, npc).orElse(pos);
    }

    public static Optional<BlockPos> findSafeSpawnLocation(ServerLevel level, BlockPos origin, BaseNPC npc) {
        Region region = new Region(origin);
        /// 普通城镇 NPC 只找地表。地下重生点只提供水平坐标，不在地下寻找空腔。
        for (int radius = 0; radius <= 16; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) != radius && Math.abs(dz) != radius) continue;
                    BlockPos column = origin.offset(dx, 0, dz);
                    if (!region.isOnRegion(column) || !level.hasChunkAt(column)) continue;
                    BlockPos candidate = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, column);
                    if (isSafeSpawnLocation(level, candidate, npc)) return Optional.of(candidate);
                }
            }
        }
        return Optional.empty();
    }

    private static boolean isSafeSpawnLocation(ServerLevel level, BlockPos pos, BaseNPC npc) {
        AABB bounds = npc.getDimensions(Pose.STANDING).makeBoundingBox(pos.getBottomCenter());
        BlockPos min = BlockPos.containing(bounds.minX, bounds.minY - 1, bounds.minZ);
        BlockPos max = BlockPos.containing(bounds.maxX, bounds.maxY, bounds.maxZ);
        if (min.getY() < level.getMinBuildHeight() || max.getY() >= level.getMaxBuildHeight()
                || !level.getWorldBorder().isWithinBounds(bounds)
                || !level.hasChunksAt(min, max)) return false;
        var floor = level.getBlockState(pos.below());
        if (!floor.isFaceSturdy(level, pos.below(), Direction.UP) || floor.is(BlockTags.LEAVES) || floor.is(BlockTags.LOGS))
            return false;
        for (BlockPos check : BlockPos.betweenClosed(min, max)) {
            var state = level.getBlockState(check);
            if (!state.getFluidState().isEmpty()) return false;
        }
        return level.noCollision(npc, bounds);
    }

    public static BlockPos getNpcSpawnPos(ServerPlayer player) {
        return player.getRespawnPosition() == null || player.getRespawnDimension() != player.serverLevel().dimension() ? player.serverLevel().getSharedSpawnPos() : player.getRespawnPosition();
    }

    public static Region getNpcSpawnRegion(ServerPlayer player) {
        return new Region(getNpcSpawnPos(player));
    }

    public static void broadcastMessageToRegion(Level level, BaseNPC npc, Component message) {
        if (level.isClientSide || !CommonConfigs.BROADCAST_NPC_MSG.get()) return;
        Region region = npc.getRegion();
        for (Player player : level.players()) {
            if (region.isOnRegion(player.chunkPosition()) || npc.distanceToSqr(player) < 96 * 96) {
                player.sendSystemMessage(message);
            }
        }
    }

    /// 调用前需检查是否已使用过先进战斗技术
    public static void applyAdvancedCombatTechniques(BaseNPC living, ResourceLocation id) {
        float oldHealth = living.getHealth();
        float oldMaxHealth = living.getMaxHealth();
        boolean wasFullHealth = Math.abs(oldHealth - oldMaxHealth) < 0.001F;
        UUID uuid = PortAttributeModifier.rl2uuid(id);
        AttributeInstance maxHealth = living.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.addOrReplacePermanentModifier(new AttributeModifier(uuid, id.getPath(), 250, AttributeModifier.Operation.ADDITION));
        }
        AttributeInstance armor = living.getAttribute(Attributes.ARMOR);
        if (armor != null) {
            armor.addOrReplacePermanentModifier(new AttributeModifier(uuid, id.getPath(), 8, AttributeModifier.Operation.ADDITION));
        }
        AttributeInstance attackDamage = living.getAttribute(LibAttributes.getAttackDamage());
        if (attackDamage != null) {
            attackDamage.addOrReplacePermanentModifier(new AttributeModifier(uuid, id.getPath(), 0.25, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
        living.setHealth(wasFullHealth ? living.getMaxHealth() : Math.min(oldHealth, living.getMaxHealth()));
    }

    public static void respawnNPC(ServerLevel level, int dayTime) {
        if (CommonConfigs.DO_NPC_SPAWNING.get() &&
                level.getGameTime() % CommonConfigs.NPC_SPAWN_INTERVAL.get() == 0 &&
                level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)
        ) NPCSpawner.INSTANCE.checkNpcRespawn(level);
    }

    public record Region(int x, int z) {
        public static final Region ZERO = new NPCSpawner.Region(BlockPos.ZERO);
        public static final Codec<Region> CODEC = Codec.LONG.xmap(Region::new, Region::toLong);

        public Region(BlockPos pos) {
            this((((pos.getX() >> 4) + 8) >> 4 << 4) - 8, (((pos.getZ() >> 4) + 8) >> 4 << 4) - 8);
        }

        public Region(long packed) {
            this((((int) packed + 8) >> 4 << 4) - 8, (((int) (packed >> 32) + 8) >> 4 << 4) - 8);
        }

        public Region(ChunkPos pos) {
            this(((pos.x + 8) >> 4 << 4) - 8, ((pos.z + 8) >> 4 << 4) - 8);
        }

        public boolean isOnRegion(BlockPos pos) {
            return isOnRegion(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));
        }

        public boolean isOnRegion(ChunkPos pos) {
            return isOnRegion(pos.x, pos.z);
        }

        public boolean isOnRegion(int chunkX, int chunkZ) {
            return chunkX >= x && chunkX < x + 16 && chunkZ >= z && chunkZ < z + 16;
        }

        public long toLong() {
            return ChunkPos.asLong(x, z);
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            return o instanceof Region that && that.x == x && that.z == z;
        }

        @Override
        public int hashCode() {
            return ChunkPos.hash(x, z);
        }

        @Override
        public String toString() {
            return "Region(x=[" + x + ", " + (x + 15) + "], z=[" + z + ", " + (z + 15) + "])";
        }
    }
}
