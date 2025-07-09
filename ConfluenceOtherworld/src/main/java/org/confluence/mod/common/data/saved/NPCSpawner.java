package org.confluence.mod.common.data.saved;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.neoforged.neoforge.common.Tags;
import org.confluence.lib.color.GlobalColors;
import org.confluence.lib.common.data.saved.IGlobalData;
import org.confluence.lib.common.worldgen.structure.SimpleTemplatePiece;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.item.common.CoinItem;
import org.confluence.mod.common.worldgen.structure.DungeonStructure;
import org.confluence.mod.integration.terra_entity.IAbstractTerraNPC;
import org.confluence.mod.integration.terra_entity.TEEvents;
import org.confluence.mod.mixed.IStructureStart;
import org.confluence.mod.mixin.integration.terra_entity.AnglerNPCMixin;
import org.confluence.mod.mixin.integration.terra_entity.MechanicNPCMixin;
import org.confluence.mod.util.OverworldUtils;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.terra_guns.common.init.TGTags;
import org.confluence.terraentity.api.event.NPCEvent;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.entity.npc.AnglerNPC;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.confluence.terraentity.init.entity.TENpcEntities;

import java.util.*;
import java.util.function.Predicate;

/**
 * 注：NPC默认生成在对应玩家出生点
 */
public class NPCSpawner implements IGlobalData {
    public static final NPCSpawner INSTANCE = new NPCSpawner();
    public static final Codec<Map<Region, Object2BooleanMap<EntityType<?>>>> NPC_ALIVE_CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<Map<Region, Object2BooleanMap<EntityType<?>>>, T>> decode(DynamicOps<T> ops, T input) {
            Map<Region, Object2BooleanMap<EntityType<?>>> map = new HashMap<>();
            ops.getMap(input).getOrThrow().entries().forEach(pair -> {
                Region region = new Region(new ChunkPos(Long.parseLong(ops.getStringValue(pair.getFirst()).getOrThrow())));
                Object2BooleanMap<EntityType<?>> map1 = new Object2BooleanOpenHashMap<>();
                ops.getMap(pair.getSecond()).getOrThrow().entries().forEach(pair1 -> {
                    EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(ops.getStringValue(pair1.getFirst()).getOrThrow()));
                    boolean spawned = ops.getBooleanValue(pair1.getSecond()).getOrThrow();
                    map1.put(entityType, spawned);
                });
                map.put(region, map1);
            });
            return DataResult.success(new Pair<>(map, input), Lifecycle.stable());
        }

        @Override
        public <T> DataResult<T> encode(Map<Region, Object2BooleanMap<EntityType<?>>> input, DynamicOps<T> ops, T prefix) {
            return DataResult.success(ops.createMap(input.entrySet().stream().map(entry -> {
                T string = ops.createString(Long.toString(entry.getKey().toLong()));
                T map = ops.createMap(entry.getValue().object2BooleanEntrySet().stream().map(entry1 -> {
                    T string1 = ops.createString(BuiltInRegistries.ENTITY_TYPE.getKey(entry1.getKey()).toString());
                    T aBoolean = ops.createBoolean(entry1.getBooleanValue());
                    return new Pair<>(string1, aBoolean);
                }));
                return new Pair<>(string, map);
            })), Lifecycle.stable());
        }
    };
    public static final Codec<Set<EntityType<?>>> NPC_SPAWNED_CODEC = BuiltInRegistries.ENTITY_TYPE.byNameCodec().listOf().xmap(HashSet::new, ArrayList::new);

    private final Map<Region, Object2BooleanMap<EntityType<?>>> npcAlive = new HashMap<>();
    /**
     * 生成过的NPC，可用于NPC复活而无需再次满足条件
     */
    private final Set<EntityType<?>> npcSpawned = new HashSet<>();
    private boolean isAdvancedCombatTechniquesUsed = false; // 先进战斗技术
    private boolean isAdvancedCombatTechniquesVolumeTwoUsed = false; // 先进战斗技术：卷二

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

    public int getAliveNpcCount(Region region) {
        Object2BooleanMap<EntityType<?>> map = npcAlive.get(region);
        if (map == null) return 0;
        int count = 0;
        for (boolean alive : map.values()) if (alive) count++;
        return count;
    }

    public Object2BooleanMap<EntityType<?>> getRegionAliveDetails(Region region) {
        return npcAlive.computeIfAbsent(region, region1 -> new Object2BooleanOpenHashMap<>());
    }

    public boolean hasNPCAlive(Region region, EntityType<?> entityType) {
        Object2BooleanMap<EntityType<?>> map = npcAlive.get(region);
        return map != null && map.getOrDefault(entityType, false);
    }

    public void setNPCAlive(Region region, EntityType<?> entityType, boolean alive) {
        if (alive) {
            getRegionAliveDetails(region).put(entityType, true);
            npcSpawned.add(entityType);
        } else {
            Object2BooleanMap<EntityType<?>> map = npcAlive.get(region);
            if (map != null && map.getBoolean(entityType)) {
                map.put(entityType, false);
            }
        }
    }

    public void moveNPCToAnotherRegion(AbstractTerraNPC living, Region from, Region to) {
        IAbstractTerraNPC npc = IAbstractTerraNPC.of(living);
        EntityType<?> entityType = living.getType();
        if (hasNPCAlive(from, entityType)) {
            setNPCAlive(from, entityType, false);
            setNPCAlive(to, entityType, true);
            npc.confluence$setRegion(to);
            applyBenedictions(living);
        }
    }

    public void onNPCAdded(AbstractTerraNPC living) {
        IAbstractTerraNPC npc = IAbstractTerraNPC.of(living);
        npc.confluence$setRegion(new Region(living.chunkPosition()));
        setNPCAlive(npc.confluence$getRegion(), living.getType(), true);
        applyBenedictions(living);
        broadcastMessageToRegion(living.level(), living, Component.translatable("event.confluence.npc.arrived", living.getType().getDescription(), living.getName()).withColor(GlobalColors.NPC_ARRIVED.get()));
    }

    public void applyBenedictions(AbstractTerraNPC living) {
        if (isAdvancedCombatTechniquesUsed()) {
            applyAdvancedCombatTechniques(living, Confluence.asResource("advanced_combat_techniques"));
        }
        if (isAdvancedCombatTechniquesVolumeTwoUsed()) {
            applyAdvancedCombatTechniques(living, Confluence.asResource("advanced_combat_techniques_volume_two"));
        }
    }

    public void onNPCRemoved(AbstractTerraNPC living) {
        setNPCAlive(((IAbstractTerraNPC) living).confluence$getRegion(), living.getType(), false);
        if (CommonConfigs.BROADCAST_NPC_MSG.get() && living.getType() != TENpcEntities.OLD_MAN.get()) { // 老人不用广播死亡信息
            MutableComponent message;
            if (living instanceof AnglerNPC /* todo 或宠物/公主 */) {
                message = Component.translatable("event.confluence.npc.left", living.getName());
            } else { // todo 旅商已离去！
                message = Component.translatable("event.confluence.npc.slain", living.getType().getDescription(), living.getName());
            }
            broadcastMessageToRegion(living.level(), living, message.withColor(GlobalColors.NPC_SLAIN.get()));
        }
    }

    @Override
    public <T> void decode(Dynamic<T> tag) {
        npcAlive.clear();
        tag.get("npc_alive").orElseEmptyMap().read(NPC_ALIVE_CODEC).ifSuccess(npcAlive::putAll);
        npcSpawned.clear();
        tag.get("npc_spawned").orElseEmptyList().read(NPC_SPAWNED_CODEC).ifSuccess(npcSpawned::addAll);
        this.isAdvancedCombatTechniquesUsed = tag.get("advanced_combat_techniques").asBoolean(false);
        this.isAdvancedCombatTechniquesVolumeTwoUsed = tag.get("advanced_combat_techniques_volume_two").asBoolean(false);
    }

    @Override
    public void encode(CompoundTag tag) {
        Iterator<Map.Entry<Region, Object2BooleanMap<EntityType<?>>>> iterator = npcAlive.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Region, Object2BooleanMap<EntityType<?>>> next = iterator.next();
            next.getValue().object2BooleanEntrySet().removeIf(entry -> !entry.getBooleanValue());
            if (next.getValue().isEmpty()) iterator.remove();
        }
        tag.put("npc_alive", NPC_ALIVE_CODEC.encodeStart(NbtOps.INSTANCE, npcAlive).getOrThrow());
        tag.put("npc_spawned", NPC_SPAWNED_CODEC.encodeStart(NbtOps.INSTANCE, npcSpawned).getOrThrow());
        tag.putBoolean("advanced_combat_techniques", isAdvancedCombatTechniquesUsed);
        tag.putBoolean("advanced_combat_techniques_volume_two", isAdvancedCombatTechniquesVolumeTwoUsed);
    }

    @Override
    public String serializeKey() {
        return "confluence:npc_spawner";
    }

    @Override
    public void clear() {
        npcAlive.clear();
        npcSpawned.clear();
        this.isAdvancedCombatTechniquesUsed = false;
        this.isAdvancedCombatTechniquesVolumeTwoUsed = false;
    }

    public void trySpawnGuide(ServerPlayer serverPlayer) {
        ServerLevel serverLevel = serverPlayer.serverLevel();
        if (serverLevel.dimension() == OverworldUtils.dimension()) {
            BlockPos pos = getNpcSpawnPos(serverPlayer);
            if (!hasNPCAlive(new Region(pos), TENpcEntities.GUIDE.get())) {
                spawnAtPos(serverLevel, pos, TENpcEntities.GUIDE.get());
            }
        }
    }

    /**
     * 每两分钟生成一位NPC
     */
    public void checkNpcRespawn(ServerLevel serverLevel) {
        outer:
        for (ServerPlayer player : serverLevel.players()) {
            BlockPos pos = getNpcSpawnPos(player);
            Region region = new Region(pos);
            if (trySpawnClothier(player, pos, region)) continue;
            if (trySpawnMechanic(player, pos, region)) continue;
            for (EntityType<?> entityType : npcSpawned) {
                if (!hasNPCAlive(region, entityType) && spawnAtPos(serverLevel, pos, entityType)) {
                    continue outer;
                }
            }
            if (trySpawnMerchant(player, pos, region)) continue;
            if (trySpawnNurse(player, pos, region)) continue;
            if (trySpawnDemolitionist(player, pos, region)) continue;
            if (trySpawnDyeTrader(player, pos, region)) continue;
            if (trySpawnAngler(player, region)) continue;
            // 动物学家
            if (trySpawnDryad(player, pos, region)) continue;
            if (trySpawnPainter(player, pos, region)) continue;
            // 高尔夫球手
            if (trySpawnArmsDealer(player, pos, region)) continue;
            // 酒馆老板
            // 发型师
            if (trySpawnGoblinTinkerer(player, pos, region)) continue;
            // 巫医
            // 派对女孩
            // 巫师
            // 税收官
            // 松露人
            // 海盗
            // 蒸汽朋克人
            // 机器侠
        }
    }

    /**
     * 省去“所有玩家钱币总和50银”的条件，改为单玩家
     */
    private boolean trySpawnMerchant(ServerPlayer serverPlayer, BlockPos pos, Region region) {
        if (!hasNPCAlive(region, TENpcEntities.MERCHANT.get())) {
            if (PlayerUtils.getMoney(serverPlayer) >= 50 * CoinItem.UPGRADES_COUNT) {
                return spawnAtPos(serverPlayer.serverLevel(), pos, TENpcEntities.MERCHANT.get());
            }
        }
        return false;
    }

    private boolean trySpawnNurse(ServerPlayer serverPlayer, BlockPos pos, Region region) {
        if (!hasNPCAlive(region, TENpcEntities.NURSE.get())) {
            if (serverPlayer.getMaxHealth() > 20 && hasNPCAlive(region, TENpcEntities.MERCHANT.get())) {
                return spawnAtPos(serverPlayer.serverLevel(), pos, TENpcEntities.NURSE.get());
            }
        }
        return false;
    }

    private boolean trySpawnDemolitionist(ServerPlayer serverPlayer, BlockPos pos, Region region) {
        if (!hasNPCAlive(region, TENpcEntities.DEMOLITIONIST.get())) {
            if (serverPlayer.getInventory().hasAnyMatching(stack -> stack.is(ModTags.Items.EXPLOSIVE)) && hasNPCAlive(region, TENpcEntities.MERCHANT.get())) {
                return spawnAtPos(serverPlayer.serverLevel(), pos, TENpcEntities.DEMOLITIONIST.get());
            }
        }
        return false;
    }

    // todo 可用于做染料的物品
    private boolean trySpawnDyeTrader(ServerPlayer serverPlayer, BlockPos pos, Region region) {
        if (!hasNPCAlive(region, TENpcEntities.DYE_TRADER.get())) {
            if (hasNPCAlive(region, TENpcEntities.MERCHANT.get()) && serverPlayer.getInventory().hasAnyMatching(stack -> stack.is(Tags.Items.DYES))) {
                return spawnAtPos(serverPlayer.serverLevel(), pos, TENpcEntities.DYE_TRADER.get());
            }
        }
        return false;
    }

    /**
     * 先计入NPC列表，待玩家交互了再转移
     *
     * @see AnglerNPCMixin
     */
    private boolean trySpawnAngler(ServerPlayer serverPlayer, Region region) {
        BlockPos playerPos = serverPlayer.blockPosition();
        Region playerRegion = new Region(playerPos);
        if (!hasNPCAlive(playerRegion, TENpcEntities.ANGLER.get()) && !hasNPCAlive(region, TENpcEntities.ANGLER.get())) { // 保证玩家转移渔夫区域时不再生成新的
            Level level = serverPlayer.serverLevel();
            Pair<BlockPos, Holder<Biome>> closestBiome3d = serverPlayer.serverLevel().findClosestBiome3d(biome -> biome.is(Tags.Biomes.IS_OCEAN), playerPos, 64, 8, 64);
            if (closestBiome3d != null) {
                AbstractTerraNPC npc = TENpcEntities.ANGLER.get().create(level);
                if (npc != null) {
                    RandomSource random = level.random;
                    int dx = random.nextInt(4) - 2;
                    int dz = random.nextInt(4) - 2;
                    npc.setPos(closestBiome3d.getFirst().atY(level.getSeaLevel()).offset(dx, 0, dz).getCenter());
                    level.addFreshEntity(npc);
                    IAbstractTerraNPC.of(npc).confluence$setRegion(playerRegion);
                    getRegionAliveDetails(playerRegion).put(TENpcEntities.ANGLER.get(), true);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean trySpawnDryad(ServerPlayer serverPlayer, BlockPos pos, Region region) {
        if (!hasNPCAlive(region, TENpcEntities.DRYAD.get())) {
            if (KillBoard.INSTANCE.isAnyDefeated(
                    TEBossEntities.EYE_OF_CTHULHU.get(),
                    TEBossEntities.EATER_OF_WORLDS.get(),
                    TEBossEntities.BRAIN_OF_CTHULHU.get(),
                    TEBossEntities.SKELETRON.get()
            )) {
                return spawnAtPos(serverPlayer.serverLevel(), pos, TENpcEntities.DRYAD.get());
            }
        }
        return false;
    }

    private boolean trySpawnPainter(ServerPlayer serverPlayer, BlockPos pos, Region region) {
        Object2BooleanMap<EntityType<?>> map = npcAlive.get(region);
        if (map != null && !map.getOrDefault(TENpcEntities.PAINTER.get(), false)) {
            if (map.size() >= 8) {
                return spawnAtPos(serverPlayer.serverLevel(), pos, TENpcEntities.PAINTER.get());
            }
        }
        return false;
    }

    private boolean trySpawnArmsDealer(ServerPlayer serverPlayer, BlockPos pos, Region region) {
        if (!hasNPCAlive(region, TENpcEntities.ARMS_DEALER.get())) {
            Predicate<ItemStack> predicate = stack -> stack.is(TGTags.BULLET) || stack.is(TGTags.GUN);
            if (serverPlayer.getInventory().hasAnyMatching(predicate) || serverPlayer.getData(ModAttachmentTypes.EXTRA_INVENTORY).hasAnyMatching(predicate)) {
                return spawnAtPos(serverPlayer.serverLevel(), pos, TENpcEntities.ARMS_DEALER.get());
            }
        }
        return false;
    }

    private boolean trySpawnGoblinTinkerer(ServerPlayer serverPlayer, BlockPos pos, Region region) {
        if (!hasNPCAlive(region, TENpcEntities.GOBLIN_TINKERER.get())) {
            // todo 等哥布林入侵事件
            if (KillBoard.INSTANCE.isAnyDefeated(TEBossEntities.EATER_OF_WORLDS.get(), TEBossEntities.BRAIN_OF_CTHULHU.get())) {
                return spawnAtPos(serverPlayer.serverLevel(), pos, TENpcEntities.GOBLIN_TINKERER.get());
            }
        }
        return false;
    }

    private boolean trySpawnClothier(ServerPlayer serverPlayer, BlockPos pos, Region region) {
        if (KillBoard.INSTANCE.getGamePhase().isAboveThan(GamePhase.BEFORE_SKELETRON)) {
            if (!hasNPCAlive(region, TENpcEntities.CLOTHIER.get())) {
                return spawnAtPos(serverPlayer.serverLevel(), pos, TENpcEntities.CLOTHIER.get());
            }
        } else {
            ServerLevel level = serverPlayer.serverLevel();
            return DungeonStructure.iterateDungeon(level, serverPlayer.chunkPosition(), structureStart -> {
                if (IStructureStart.of(structureStart).confluence$cachedBoundingBox().isInside(serverPlayer.blockPosition())) {
                    for (StructurePiece piece : structureStart.getPieces()) {
                        if (piece instanceof SimpleTemplatePiece templatePiece && DungeonStructure.GATE.equals(templatePiece.templateName)) {
                            BlockPos offset = switch (templatePiece.getRotation()) {
                                case CLOCKWISE_90 -> templatePiece.templatePosition().offset(-15, 6, 15);
                                case CLOCKWISE_180 -> templatePiece.templatePosition().offset(-15, 6, -15);
                                case COUNTERCLOCKWISE_90 -> templatePiece.templatePosition().offset(15, 6, -15);
                                default -> templatePiece.templatePosition().offset(15, 6, 15);
                            };
                            Region npcRegion = new Region(offset);
                            if (!hasNPCAlive(npcRegion, TENpcEntities.OLD_MAN.get())) {
                                AbstractTerraNPC npc = TENpcEntities.OLD_MAN.get().create(level);
                                if (npc == null) return false;
                                npc.setPos(offset.getBottomCenter());
                                level.addFreshEntity(npc);
                                IAbstractTerraNPC.of(npc).confluence$setRegion(npcRegion);
                                getRegionAliveDetails(npcRegion).put(TENpcEntities.OLD_MAN.get(), true);
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

    /**
     * 未在区域内的机械师会自动移除（因为机械师距离玩家基地可能很远）
     *
     * @see TEEvents#onInteractNpc(NPCEvent.InteractNPCEvent)
     * @see MechanicNPCMixin
     */
    private boolean trySpawnMechanic(ServerPlayer serverPlayer, BlockPos pos, Region region) {
        if (KillBoard.INSTANCE.getGamePhase().isAboveThan(GamePhase.BEFORE_SKELETRON)) {
            if (!hasNPCAlive(region, TENpcEntities.MECHANIC.get())) {
                return spawnAtPos(serverPlayer.serverLevel(), pos, TENpcEntities.MECHANIC.get());
            }
        } else {
            ServerLevel level = serverPlayer.serverLevel();
            return DungeonStructure.iterateDungeon(level, serverPlayer.chunkPosition(), structureStart -> {
                if (IStructureStart.of(structureStart).confluence$cachedBoundingBox().isInside(serverPlayer.blockPosition())) {
                    for (StructurePiece piece : structureStart.getPieces()) {
                        if (piece instanceof SimpleTemplatePiece templatePiece && templatePiece.templateName.endsWith("_dungeon_underground_2_2")) {
                            BlockPos offset = templatePiece.templatePosition().offset(46, 6, -11);
                            Region npcRegion = new Region(offset);
                            if (!hasNPCAlive(npcRegion, TENpcEntities.MECHANIC.get())) {
                                AbstractTerraNPC npc = TENpcEntities.MECHANIC.get().create(level);
                                if (npc == null) return false;
                                npc.setPos(offset.getBottomCenter());
                                level.addFreshEntity(npc);
                                IAbstractTerraNPC terraNPC = IAbstractTerraNPC.of(npc);
                                terraNPC.confluence$setRegion(npcRegion);
                                terraNPC.confluence$setShouldInteract(true); // 标记需要交互
                                getRegionAliveDetails(npcRegion).put(TENpcEntities.MECHANIC.get(), true);
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
        if (!(entityType.create(level) instanceof AbstractTerraNPC living)) return false;
        living.setPos(pos.getCenter());
        level.addFreshEntity(living);
        if (living instanceof AnglerNPC angler) {
            angler.setWakeUp(true); // 重生的渔夫默认醒来
        }
        onNPCAdded(living);
        return true;
    }

    public static BlockPos getNpcSpawnPos(ServerPlayer player) {
        return player.getRespawnPosition() == null ? player.serverLevel().getSharedSpawnPos() : player.getRespawnPosition();
    }

    public static void broadcastMessageToRegion(Level level, AbstractTerraNPC npc, Component message) {
        Region region = ((IAbstractTerraNPC) npc).confluence$getRegion();
        for (Player player : level.players()) {
            if (region.isOnRegion(player.chunkPosition()) || npc.distanceToSqr(player) < 96 * 96) {
                player.sendSystemMessage(message);
            }
        }
    }

    /**
     * 调用前需检查是否已使用过先进战斗技术
     */
    public static void applyAdvancedCombatTechniques(AbstractTerraNPC living, ResourceLocation id) {
        AttributeInstance armor = living.getAttribute(Attributes.ARMOR);
        if (armor != null) {
            armor.addOrReplacePermanentModifier(new AttributeModifier(id, 3, AttributeModifier.Operation.ADD_VALUE));
        }
        AttributeInstance attackDamage = living.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage != null) {
            attackDamage.addOrReplacePermanentModifier(new AttributeModifier(id, 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
    }

    public record Region(int x, int z) {
        public static final Region ZERO = new NPCSpawner.Region(BlockPos.ZERO);
        public static final Codec<Region> CODEC = Codec.LONG.xmap(Region::new, Region::toLong);

        public Region(BlockPos pos) {
            this(new ChunkPos(pos));
        }

        public Region(long packed) {
            this(new ChunkPos(packed));
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
            return o instanceof Region(int x1, int z1) && x1 == x && z1 == z;
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
