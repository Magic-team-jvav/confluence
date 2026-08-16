package org.confluence.mod.common.init;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.monster.BaseAquaticMonster;
import org.confluence.mod.common.entity.monster.BaseFlyingMonster;
import org.confluence.mod.common.init.entity.ModEntities;
import org.mesdag.portlib.wrapper.PortEnvironment;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/// 开发环境使用的客户端实测场景命令。
///
/// <p>测试场景不维护一份容易遗漏的手写物品或实体清单，而是在执行时读取实际注册表。这样新增
/// 生物、武器、工具、普通道具或方块后，会自动进入对应测试序列。生物和方块采用逐个切换方式，
/// 避免多个 Boss、环境生物及功能方块同时运行，互相干扰行为判断。</p>
///
/// <p>该命令只在开发环境注册，并且仍要求二级命令权限，不会成为正式游戏内容或服务器管理接口。</p>
public final class DeveloperTestSceneCommands {
    static final String TEST_ENTITY_TAG = "confluence_client_test_entity";
    private static final int BARREL_CAPACITY = 27;
    private static final int LIBRARY_COLUMNS = 18;
    private static final Map<UUID, Integer> ENTITY_INDICES = new ConcurrentHashMap<>();
    private static final Map<UUID, Integer> ITEM_INDICES = new ConcurrentHashMap<>();
    private static final Map<UUID, Integer> BLOCK_INDICES = new ConcurrentHashMap<>();
    private static final Map<UUID, BlockPos> SCENE_ORIGINS = new ConcurrentHashMap<>();

    private DeveloperTestSceneCommands() {}

    public static LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("testScene")
                .requires(source -> PortEnvironment.isDeveloper()
                        && source.hasPermission(2))
                .then(Commands.literal("build")
                        .executes(context -> build(context.getSource())))
                .then(Commands.literal("clearEntity")
                        .executes(context -> clearEntity(context.getSource())))
                .then(Commands.literal("target")
                        .executes(context -> spawnCombatTarget(context.getSource())))
                .then(Commands.literal("worldgen")
                        .then(Commands.literal("sample")
                                .then(Commands.argument("x", IntegerArgumentType.integer())
                                        .then(Commands.argument("z", IntegerArgumentType.integer())
                                                .executes(context -> sampleGeneratedChunk(
                                                        context.getSource(),
                                                        IntegerArgumentType.getInteger(context, "x"),
                                                        IntegerArgumentType.getInteger(context, "z")))))))
                .then(Commands.literal("entity")
                        .then(Commands.literal("next")
                                .executes(context -> changeEntity(context.getSource(), 1)))
                        .then(Commands.literal("previous")
                                .executes(context -> changeEntity(context.getSource(), -1)))
                        .then(Commands.argument("id", ResourceLocationArgument.id())
                                .executes(context -> spawnEntity(
                                        context.getSource(),
                                        ResourceLocationArgument.getId(context, "id")))))
                .then(Commands.literal("item")
                        .then(Commands.literal("hold")
                                .then(Commands.literal("next")
                                        .executes(context -> changeHeldItem(
                                                context.getSource(), 1)))
                                .then(Commands.literal("previous")
                                        .executes(context -> changeHeldItem(
                                                context.getSource(), -1)))
                                .then(Commands.argument("id", ResourceLocationArgument.id())
                                        .executes(context -> holdItem(
                                                context.getSource(),
                                                ResourceLocationArgument.getId(context, "id")))))
                        .then(Commands.literal("next")
                                .executes(context -> changeItem(context.getSource(), 1)))
                        .then(Commands.literal("previous")
                                .executes(context -> changeItem(context.getSource(), -1)))
                        .then(Commands.literal("page")
                                .then(Commands.argument("index", IntegerArgumentType.integer(1))
                                        .executes(context -> giveItemPage(
                                                context.getSource(),
                                                IntegerArgumentType.getInteger(context, "index")))))
                        .then(Commands.argument("id", ResourceLocationArgument.id())
                                .executes(context -> giveItem(
                                        context.getSource(),
                                        ResourceLocationArgument.getId(context, "id")))))
                .then(Commands.literal("block")
                        .then(Commands.literal("next")
                                .executes(context -> changeBlock(context.getSource(), 1)))
                        .then(Commands.literal("previous")
                                .executes(context -> changeBlock(context.getSource(), -1)))
                        .then(Commands.argument("id", ResourceLocationArgument.id())
                                .executes(context -> placeBlock(
                                        context.getSource(),
                                        ResourceLocationArgument.getId(context, "id")))))
                .then(Commands.literal("status")
                        .executes(context -> showStatus(context.getSource())));
    }

    /// 在执行者附近建立互相分离的战斗区、水域、飞行区、方块区和完整物品库。
    /// 这里只搭建可重复使用的基础设施，不主动生成生物，也不会清除玩家原有建筑。
    static SceneSummary buildScene(ServerLevel level, BlockPos origin) {
        BlockPos combat = origin.offset(0, 0, 20);
        BlockPos water = origin.offset(-24, 0, 0);
        BlockPos flight = origin.offset(0, 0, -20);
        BlockPos blockPad = origin.offset(24, 0, 0);

        buildFloor(level, combat, 12, Blocks.RED_CONCRETE);
        buildFloor(level, flight, 8, Blocks.LIGHT_BLUE_CONCRETE);
        buildFloor(level, blockPad, 8, Blocks.GRAY_CONCRETE);
        buildWaterTank(level, water);
        buildRangeMarkers(level, combat);
        buildToolLane(level, origin.offset(24, 0, 20));

        List<Item> items = registeredItems();
        int barrelCount = (items.size() + BARREL_CAPACITY - 1) / BARREL_CAPACITY;
        BlockPos libraryStart = origin.offset(-LIBRARY_COLUMNS / 2, 0, 35);
        for (int barrelIndex = 0; barrelIndex < barrelCount; barrelIndex++) {
            int x = barrelIndex % LIBRARY_COLUMNS;
            int z = barrelIndex / LIBRARY_COLUMNS;
            BlockPos barrelPos = libraryStart.offset(x, 0, z);
            level.setBlock(barrelPos.below(), Blocks.SMOOTH_STONE.defaultBlockState(), 3);
            level.setBlock(barrelPos, Blocks.BARREL.defaultBlockState(), 3);
            BlockEntity blockEntity = level.getBlockEntity(barrelPos);
            if (!(blockEntity instanceof Container container)) {
                continue;
            }
            for (int slot = 0; slot < BARREL_CAPACITY; slot++) {
                int itemIndex = barrelIndex * BARREL_CAPACITY + slot;
                if (itemIndex >= items.size()) {
                    break;
                }
                container.setItem(slot, items.get(itemIndex).getDefaultInstance());
            }
            if (blockEntity instanceof BarrelBlockEntity barrel) {
                int displayIndex = barrelIndex + 1;
                barrel.setCustomName(Component.literal(
                        "Confluence item library " + displayIndex + " / " + barrelCount));
            }
            blockEntity.setChanged();
        }
        return new SceneSummary(items.size(), registeredLivingEntityTypes(level).size(),
                registeredBlocks().size(), barrelCount);
    }

    private static int build(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        BlockPos origin = player.blockPosition();
        SceneSummary summary = buildScene(source.getLevel(), origin);
        SCENE_ORIGINS.put(player.getUUID(), origin);
        source.sendSuccess(() -> Component.literal(
                "Client test scene built: " + summary.items() + " items, "
                        + summary.livingEntities() + " living entities, "
                        + summary.blocks() + " blocks, "
                        + summary.barrels() + " item barrels."), true);
        return summary.items();
    }

    private static int changeEntity(CommandSourceStack source, int direction)
            throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        List<EntityType<?>> types = registeredLivingEntityTypes(source.getLevel());
        if (types.isEmpty()) {
            source.sendFailure(Component.literal("No Confluence living entity type is registered."));
            return 0;
        }
        int index = moveIndex(ENTITY_INDICES, player.getUUID(), direction, types.size());
        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(types.get(index));
        return id == null ? 0 : spawnEntity(source, id);
    }

    private static int spawnEntity(CommandSourceStack source, ResourceLocation id)
            throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(id);
        if (type == null || !Confluence.MODID.equals(id.getNamespace())) {
            source.sendFailure(Component.literal("Unknown Confluence entity type: " + id));
            return 0;
        }
        Entity entity = type.create(source.getLevel());
        if (!(entity instanceof LivingEntity livingEntity)) {
            if (entity != null) entity.discard();
            source.sendFailure(Component.literal("Entity type is not a living creature: " + id));
            return 0;
        }
        clearTaggedEntities(source.getLevel(), player.position());
        Vec3 spawnPosition = selectEntityTestPosition(player, livingEntity);
        entity.setPos(spawnPosition);
        entity.setYRot(player.getYRot() + 180.0F);
        entity.addTag(TEST_ENTITY_TAG);
        if (!source.getLevel().addFreshEntity(entity)) {
            source.sendFailure(Component.literal("Failed to add test entity: " + id));
            return 0;
        }
        source.sendSuccess(() -> Component.literal("Spawned test entity: " + id), false);
        return 1;
    }

    /// 在战斗区生成一只高生命、无 AI 的原版僵尸，用于反复检查武器伤害、弹道、
    /// 暴击和命中特效。测试目标不会替代真实生物测试，只提供稳定的伤害参照物。
    private static int spawnCombatTarget(CommandSourceStack source)
            throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        Zombie target = EntityType.ZOMBIE.create(source.getLevel());
        if (target == null) {
            source.sendFailure(Component.literal("Failed to create the client test target."));
            return 0;
        }
        Vec3 position = sceneOrigin(player).offset(0, 0, 28).getCenter();
        target.setPos(position);
        target.setNoAi(true);
        target.setSilent(true);
        target.setPersistenceRequired();
        target.addTag(TEST_ENTITY_TAG);
        if (target.getAttribute(Attributes.MAX_HEALTH) != null) {
            target.getAttribute(Attributes.MAX_HEALTH).setBaseValue(2048.0);
            target.setHealth(target.getMaxHealth());
        }
        if (!source.getLevel().addFreshEntity(target)) {
            target.discard();
            source.sendFailure(Component.literal("Failed to add the client test target."));
            return 0;
        }
        source.sendSuccess(() -> Component.literal(
                "Spawned a 2048-health weapon test target."), false);
        return 1;
    }

    private static int clearEntity(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        int removed = clearTaggedEntities(source.getLevel(), player.position());
        source.sendSuccess(() -> Component.literal("Removed " + removed + " test entities."), false);
        return removed;
    }

    private static int clearTaggedEntities(ServerLevel level, Vec3 center) {
        List<Entity> entities = level.getEntities((Entity) null,
                new AABB(center, center).inflate(192.0),
                entity -> entity.getTags().contains(TEST_ENTITY_TAG));
        entities.forEach(Entity::discard);
        return entities.size();
    }

    private static int changeItem(CommandSourceStack source, int direction)
            throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        List<Item> items = registeredItems();
        if (items.isEmpty()) return 0;
        int index = moveIndex(ITEM_INDICES, player.getUUID(), direction, items.size());
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(items.get(index));
        return id == null ? 0 : giveItem(source, id);
    }

    /// 切换主手物品，让客户端真实加载每个注册物品的手持模型、第一人称变换和物品属性。
    /// 与 {@code item next} 分开，避免模型烟测把数千个物品堆进背包或散落到世界中。
    private static int changeHeldItem(CommandSourceStack source, int direction)
            throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        List<Item> items = registeredItems();
        if (items.isEmpty()) return 0;
        int index = moveIndex(ITEM_INDICES, player.getUUID(), direction, items.size());
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(items.get(index));
        return id == null ? 0 : holdItem(source, id);
    }

    private static int holdItem(CommandSourceStack source, ResourceLocation id)
            throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        Item item = ForgeRegistries.ITEMS.getValue(id);
        if (item == null || !Confluence.MODID.equals(id.getNamespace())) {
            source.sendFailure(Component.literal("Unknown Confluence item: " + id));
            return 0;
        }
        player.setItemInHand(InteractionHand.MAIN_HAND, item.getDefaultInstance());
        source.sendSuccess(() -> Component.literal("Holding test item: " + id), false);
        return 1;
    }

    private static int giveItemPage(CommandSourceStack source, int page)
            throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        List<Item> items = registeredItems();
        int first = (page - 1) * BARREL_CAPACITY;
        if (first >= items.size()) {
            source.sendFailure(Component.literal("Item page is outside the registered range: " + page));
            return 0;
        }
        int given = 0;
        for (int index = first; index < Math.min(first + BARREL_CAPACITY, items.size()); index++) {
            ItemStack stack = items.get(index).getDefaultInstance();
            if (!player.getInventory().add(stack)) player.drop(stack, false);
            given++;
        }
        int finalGiven = given;
        source.sendSuccess(() -> Component.literal("Gave item test page " + page
                + " with " + finalGiven + " entries."), false);
        return given;
    }

    private static int giveItem(CommandSourceStack source, ResourceLocation id)
            throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        Item item = ForgeRegistries.ITEMS.getValue(id);
        if (item == null || !Confluence.MODID.equals(id.getNamespace())) {
            source.sendFailure(Component.literal("Unknown Confluence item: " + id));
            return 0;
        }
        ItemStack stack = item.getDefaultInstance();
        if (!player.getInventory().add(stack)) player.drop(stack, false);
        source.sendSuccess(() -> Component.literal("Gave test item: " + id), false);
        return 1;
    }

    private static int changeBlock(CommandSourceStack source, int direction)
            throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        List<Block> blocks = registeredBlocks();
        if (blocks.isEmpty()) return 0;
        int index = moveIndex(BLOCK_INDICES, player.getUUID(), direction, blocks.size());
        ResourceLocation id = ForgeRegistries.BLOCKS.getKey(blocks.get(index));
        return id == null ? 0 : placeBlock(source, id);
    }

    private static int placeBlock(CommandSourceStack source, ResourceLocation id)
            throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        Block block = ForgeRegistries.BLOCKS.getValue(id);
        if (block == null || !Confluence.MODID.equals(id.getNamespace())) {
            source.sendFailure(Component.literal("Unknown Confluence block: " + id));
            return 0;
        }
        Vec3 position = player.position().add(horizontalLook(player).scale(5.0));
        BlockPos blockPos = BlockPos.containing(position.x, player.getY(), position.z);
        source.getLevel().setBlock(blockPos.below(), Blocks.SMOOTH_STONE.defaultBlockState(), 3);
        source.getLevel().setBlock(blockPos, block.defaultBlockState(), 2);
        source.sendSuccess(() -> Component.literal("Placed test block: " + id), false);
        return 1;
    }

    private static int showStatus(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        int entities = registeredLivingEntityTypes(source.getLevel()).size();
        int items = registeredItems().size();
        int blocks = registeredBlocks().size();
        source.sendSuccess(() -> Component.literal("Confluence client coverage: "
                + entities + " living entities, " + items + " items, " + blocks + " blocks. "
                + "Current indices: entity=" + (ENTITY_INDICES.getOrDefault(player.getUUID(), 0) + 1)
                + ", item=" + (ITEM_INDICES.getOrDefault(player.getUUID(), 0) + 1)
                + ", block=" + (BLOCK_INDICES.getOrDefault(player.getUUID(), 0) + 1) + "."), false);
        return entities + items + blocks;
    }

    /// 强制完成目标自然区块并输出可复查的世界生成样本。
    ///
    /// <p>该命令不放置任何特征，只读取完整生成后的区块：记录指定列的地表、区块内出现的
    /// 本体生物群系、本体方块首次坐标以及有效结构起点。固定种子专用服务端可以据此把
    /// {@code locate} 的候选位置转成“实际区块已经生成”的证据。</p>
    private static int sampleGeneratedChunk(
            CommandSourceStack source, int blockX, int blockZ) {
        ServerLevel level = source.getLevel();
        var chunk = level.getChunk(
                SectionPos.blockToSectionCoord(blockX),
                SectionPos.blockToSectionCoord(blockZ));
        int minX = chunk.getPos().getMinBlockX();
        int minZ = chunk.getPos().getMinBlockZ();
        int surfaceY = level.getHeight(
                Heightmap.Types.WORLD_SURFACE, blockX, blockZ);
        ResourceLocation surfaceBlock = ForgeRegistries.BLOCKS.getKey(
                level.getBlockState(new BlockPos(blockX, surfaceY - 1, blockZ)).getBlock());

        TreeSet<ResourceLocation> biomes = new TreeSet<>();
        TreeMap<ResourceLocation, BlockPos> firstBlocks = new TreeMap<>();
        for (int x = minX; x < minX + 16; x += 4) {
            for (int z = minZ; z < minZ + 16; z += 4) {
                for (int y = level.getMinBuildHeight(); y < level.getMaxBuildHeight(); y += 4) {
                    level.getBiome(new BlockPos(x, y, z)).unwrapKey()
                            .map(key -> key.location())
                            .filter(id -> Confluence.MODID.equals(id.getNamespace()))
                            .ifPresent(biomes::add);
                }
            }
        }
        for (int x = minX; x < minX + 16; x++) {
            for (int z = minZ; z < minZ + 16; z++) {
                for (int y = level.getMinBuildHeight(); y < level.getMaxBuildHeight(); y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    ResourceLocation id = ForgeRegistries.BLOCKS.getKey(
                            chunk.getBlockState(pos).getBlock());
                    if (id != null && Confluence.MODID.equals(id.getNamespace())) {
                        firstBlocks.putIfAbsent(id, pos.immutable());
                    }
                }
            }
        }

        TreeSet<ResourceLocation> structures = new TreeSet<>();
        var structureRegistry = level.registryAccess()
                .registryOrThrow(Registries.STRUCTURE);
        for (Map.Entry<Structure, StructureStart> entry
                : chunk.getAllStarts().entrySet()) {
            if (!entry.getValue().isValid()) continue;
            ResourceLocation id = structureRegistry.getKey(entry.getKey());
            if (id != null && Confluence.MODID.equals(id.getNamespace())) {
                structures.add(id);
            }
        }

        String result = "WORLDGEN_SAMPLE dimension=" + level.dimension().location()
                + " chunk=" + chunk.getPos()
                + " column=" + blockX + "," + blockZ
                + " surface=" + surfaceY + ":" + surfaceBlock
                + " biomes=" + biomes
                + " blocks=" + firstBlocks
                + " structures=" + structures;
        Confluence.LOGGER.info(result);
        source.sendSuccess(() -> Component.literal(result), false);
        return 1;
    }

    static List<Item> registeredItems() {
        return ForgeRegistries.ITEMS.getEntries().stream()
                .filter(entry -> Confluence.MODID.equals(entry.getKey().location().getNamespace()))
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(key -> key.location().toString())))
                .map(Map.Entry::getValue)
                .toList();
    }

    static List<Block> registeredBlocks() {
        return ForgeRegistries.BLOCKS.getEntries().stream()
                .filter(entry -> Confluence.MODID.equals(entry.getKey().location().getNamespace()))
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(key -> key.location().toString())))
                .map(Map.Entry::getValue)
                .toList();
    }

    static List<EntityType<?>> registeredLivingEntityTypes(ServerLevel level) {
        List<EntityType<?>> result = new ArrayList<>();
        ForgeRegistries.ENTITY_TYPES.getEntries().stream()
                .filter(entry -> Confluence.MODID.equals(entry.getKey().location().getNamespace()))
                .filter(entry -> entry.getValue() != ModEntities.BESTIARY_ENTRY_DISPLAY.get())
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(key -> key.location().toString())))
                .forEach(entry -> {
                    Entity candidate = entry.getValue().create(level);
                    if (candidate instanceof LivingEntity) result.add(entry.getValue());
                    if (candidate != null) candidate.discard();
                });
        return List.copyOf(result);
    }

    private static int moveIndex(Map<UUID, Integer> indices, UUID player, int direction, int size) {
        int current = indices.getOrDefault(player, direction > 0 ? -1 : 0);
        int next = Math.floorMod(current + direction, size);
        indices.put(player, next);
        return next;
    }

    private static Vec3 horizontalLook(ServerPlayer player) {
        Vec3 look = player.getLookAngle().multiply(1.0, 0.0, 1.0);
        return look.lengthSqr() < 1.0E-6 ? new Vec3(0.0, 0.0, 1.0) : look.normalize();
    }

    private static BlockPos sceneOrigin(ServerPlayer player) {
        return SCENE_ORIGINS.getOrDefault(player.getUUID(), player.blockPosition());
    }

    /// 按生物的基本运动环境选择场地。水生生物进入水箱，飞行生物进入净空区，
    /// 其余生物进入战斗区；这样顺序遍历注册表时不会把鲨鱼生成在陆地上。
    private static Vec3 selectEntityTestPosition(
            ServerPlayer player, LivingEntity entity) {
        BlockPos origin = sceneOrigin(player);
        if (entity instanceof BaseAquaticMonster) {
            return origin.offset(-24, 2, 0).getCenter();
        }
        if (entity instanceof BaseFlyingMonster
                || entity instanceof FlyingAnimal) {
            return origin.offset(0, 6, -20).getCenter();
        }
        return origin.offset(0, 1, 20).getCenter();
    }

    private static void buildFloor(ServerLevel level, BlockPos center, int radius, Block block) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                level.setBlock(center.offset(x, -1, z), block.defaultBlockState(), 3);
            }
        }
    }

    private static void buildWaterTank(ServerLevel level, BlockPos center) {
        int radius = 5;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                level.setBlock(center.offset(x, -1, z), Blocks.GLASS.defaultBlockState(), 3);
                for (int y = 0; y <= 5; y++) {
                    boolean wall = Math.abs(x) == radius || Math.abs(z) == radius;
                    level.setBlock(center.offset(x, y, z),
                            wall ? Blocks.GLASS.defaultBlockState() : Blocks.WATER.defaultBlockState(), 3);
                }
            }
        }
    }

    /// 在战斗区标出 5、10、20 格射击距离，便于检查散布、射程、下坠和穿透。
    private static void buildRangeMarkers(ServerLevel level, BlockPos center) {
        int[] distances = {5, 10, 20};
        Block[] markers = {
                Blocks.LIME_CONCRETE,
                Blocks.YELLOW_CONCRETE,
                Blocks.BLUE_CONCRETE
        };
        for (int index = 0; index < distances.length; index++) {
            int z = distances[index];
            for (int x = -2; x <= 2; x++) {
                level.setBlock(center.offset(x, -1, z),
                        markers[index].defaultBlockState(), 3);
            }
        }
    }

    /// 依次铺设常见挖掘材料，供镐、斧、锹、锤及特殊工具在同一地点实测。
    private static void buildToolLane(ServerLevel level, BlockPos start) {
        Block[] materials = {
                Blocks.STONE,
                Blocks.DEEPSLATE,
                Blocks.IRON_ORE,
                Blocks.OAK_LOG,
                Blocks.OAK_PLANKS,
                Blocks.DIRT,
                Blocks.SAND,
                Blocks.COBWEB
        };
        for (int index = 0; index < materials.length; index++) {
            BlockPos position = start.offset(index, 0, 0);
            level.setBlock(position.below(), Blocks.SMOOTH_STONE.defaultBlockState(), 3);
            level.setBlock(position, materials[index].defaultBlockState(), 3);
        }
    }

    record SceneSummary(int items, int livingEntities, int blocks, int barrels) {}
}
