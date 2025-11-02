package org.confluence.mod.integration.waystones;

import com.mojang.datafixers.DSL;
import net.blay09.mods.waystones.api.WaystonesAPI;
import net.blay09.mods.waystones.requirement.NoRequirement;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.LibTags;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibUtils;
import org.confluence.lib.util.WipNotDisplayOutput;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.component.ValueComponent;
import org.confluence.mod.common.data.gen.data_map.ValueSubProvider;
import org.confluence.mod.common.init.ModBiomes;
import org.confluence.mod.common.init.ModTabs;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.integration.xaero.PylonWaypointElement;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class WaystonesHelper {
    public static final String MODID = "waystones";
    public static final boolean IS_LOADED = ModList.get().isLoaded(MODID);
    public static final Map<ResourceLocation, ResourceLocation> TYPE_TO_TEXTURE = new HashMap<>();
    static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Confluence.MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Confluence.MODID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    // 确保不要在任何地方直接调用它们！在没安装指路石模组时这些注册项不会被注册
    public static final DeferredBlock<Block> FOREST_PYLON = register("forest_pylon", BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).mapColor(DyeColor.CYAN).lightLevel(state -> 10), (world, pos, biome) -> biome.is(Tags.Biomes.IS_FOREST) || biome.is(Tags.Biomes.IS_PLAINS));
    public static final DeferredBlock<Block> SNOW_PYLON = register("snow_pylon", BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).mapColor(DyeColor.WHITE).lightLevel(state -> 10), (world, pos, biome) -> biome.is(Tags.Biomes.IS_SNOWY) || biome.is(Tags.Biomes.IS_ICY));
    public static final DeferredBlock<Block> DESERT_PYLON = register("desert_pylon", BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).mapColor(DyeColor.YELLOW).lightLevel(state -> 10), (world, pos, biome) -> biome.is(Tags.Biomes.IS_DESERT) || biome.is(Tags.Biomes.IS_BADLANDS));
    public static final DeferredBlock<Block> CAVERN_PYLON = register("cavern_pylon", BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).mapColor(DyeColor.GRAY).lightLevel(state -> 10), (world, pos, biome) -> world.dimensionType().bedWorks() && pos.getY() < world.getMinBuildHeight() + 104);
    public static final DeferredBlock<Block> OCEAN_PYLON = register("ocean_pylon", BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).mapColor(DyeColor.BLUE).lightLevel(state -> 10), (world, pos, biome) -> biome.is(Tags.Biomes.IS_OCEAN));
    public static final DeferredBlock<Block> JUNGLE_PYLON = register("jungle_pylon", BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).mapColor(DyeColor.GREEN).lightLevel(state -> 10), (world, pos, biome) -> biome.is(Tags.Biomes.IS_JUNGLE));
    public static final DeferredBlock<Block> HALLOW_PYLON = register("hallow_pylon", BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).mapColor(DyeColor.LIGHT_BLUE).lightLevel(state -> 10), (world, pos, biome) -> biome.is(ModTags.Biomes.THE_HALLOW));
    public static final DeferredBlock<Block> MUSHROOM_PYLON = register("mushroom_pylon", BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).mapColor(DyeColor.PURPLE).lightLevel(state -> 10), (world, pos, biome) -> biome.is(ModBiomes.GLOWING_MUSHROOM));
    public static final DeferredBlock<Block> UNIVERSAL_PYLON = register("universal_pylon", BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).mapColor(DyeColor.BROWN).lightLevel(state -> 10), (world, pos, biome) -> true);

    public static final Supplier<BlockEntityType<PylonBlock.BEntity>> PYLON_ENTITY = BLOCK_ENTITY_TYPES.register("pylon_entity", () -> BlockEntityType.Builder.of(PylonBlock.BEntity::new, BLOCKS.getEntries().stream().map(DeferredHolder::get).toArray(Block[]::new)).build(DSL.remainderType()));

    private static DeferredBlock<Block> register(String name, BlockBehaviour.Properties properties, PylonBlock.Survive survive) {
        int count = BLOCKS.getEntries().size();
        DeferredBlock<Block> block = BLOCKS.register(name, () -> {
            try {
                return PylonBlock.class.getDeclaredConstructor(int.class, BlockBehaviour.Properties.class, PylonBlock.Survive.class).newInstance(count, properties, survive);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.BLUE)));
        TYPE_TO_TEXTURE.put(block.getId(), Confluence.asResource("textures/gui/pylon/" + block.getId().getPath() + ".png"));
        return block;
    }

    public static void register(IEventBus eventBus) {
        if (IS_LOADED) {
            BLOCKS.register(eventBus);
            BLOCK_ENTITY_TYPES.register(eventBus);
            ITEMS.register(eventBus);
            eventBus.addListener(WaystonesHelper::buildCreativeModeTabContents);
        }
    }

    private static void buildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == ModTabs.MECHANICAL.get()) {
            WipNotDisplayOutput output = new WipNotDisplayOutput(event);
            ITEMS.getEntries().forEach(item -> output.accept(item.get()));
        }
    }

    public static void itemTag(Function<TagKey<Item>, IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item>> consumer) {
        IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> wip = consumer.apply(LibTags.Items.WIP);
        wip.addOptional(JUNGLE_PYLON.getId());
        wip.addOptional(HALLOW_PYLON.getId());
        wip.addOptional(MUSHROOM_PYLON.getId());
        wip.addOptional(UNIVERSAL_PYLON.getId());
    }

    public static void blockTags(Function<TagKey<Block>, IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block>> consumer) {
        IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block> isTeleportTarget = consumer.apply(
                TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(MODID, "is_teleport_target"))
        );
        BLOCKS.getEntries().forEach(block -> isTeleportTarget.addOptional(block.getId()));

        IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block> mineableWithPickaxe = consumer.apply(
                BlockTags.MINEABLE_WITH_PICKAXE
        );
        BLOCKS.getEntries().forEach(block -> mineableWithPickaxe.addOptional(block.getId()));

        IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block> needsOneLevel = consumer.apply(
                ModTags.Blocks.NEEDS_1_LEVEL
        );
        BLOCKS.getEntries().forEach(block -> needsOneLevel.addOptional(block.getId()));
    }

    public static void addTranslateKeys(BiConsumer<DeferredHolder<Block, ? extends Block>, String> consumer, boolean en) {
        if (en) {
            BLOCKS.getEntries().forEach(block -> consumer.accept(block, LibUtils.toTitleCase(block.getId().getPath())));
        } else {
            consumer.accept(FOREST_PYLON, "森林晶塔");
            consumer.accept(SNOW_PYLON, "雪原晶塔");
            consumer.accept(DESERT_PYLON, "沙漠晶塔");
            consumer.accept(CAVERN_PYLON, "洞穴晶塔");
            consumer.accept(OCEAN_PYLON, "海洋晶塔");
            consumer.accept(JUNGLE_PYLON, "丛林晶塔");
            consumer.accept(HALLOW_PYLON, "神圣晶塔");
            consumer.accept(MUSHROOM_PYLON, "蘑菇晶塔");
            consumer.accept(UNIVERSAL_PYLON, "万能晶塔");
        }
    }

    public static void appendValue(ValueSubProvider.Builder builder) {
        ValueComponent value = new ValueComponent(20000);
        ModLoadedCondition condition = new ModLoadedCondition(MODID);
        builder.add(FOREST_PYLON.getId(), value, false, condition);
        builder.add(SNOW_PYLON.getId(), value, false, condition);
        builder.add(DESERT_PYLON.getId(), value, false, condition);
        builder.add(CAVERN_PYLON.getId(), value, false, condition);
        builder.add(OCEAN_PYLON.getId(), value, false, condition);
        builder.add(JUNGLE_PYLON.getId(), value, false, condition);
        builder.add(HALLOW_PYLON.getId(), value, false, condition);
        builder.add(MUSHROOM_PYLON.getId(), value, false, condition);
        builder.add(UNIVERSAL_PYLON.getId(), new ValueComponent(200000), false, condition);
    }

    public static void registerPayload(PayloadRegistrar registrar) {
        if (IS_LOADED) {
            registrar.playToServer(PlayerToPylonPacketC2S.TYPE, PlayerToPylonPacketC2S.STREAM_CODEC, PlayerToPylonPacketC2S::handle);
        }
    }

    public static void handle(ServerPlayer serverPlayer, UUID uuid) {
        // todo 检查NPC
        WaystonesAPI.getWaystone(serverPlayer.server, uuid).ifPresent(waystone -> {
            WaystonesAPI.createDefaultTeleportContext(serverPlayer, waystone, context -> {
                if (CommonConfigs.WAYSTONES_PYLON_NON_COST.get()) {
                    context.setRequirements(NoRequirement.INSTANCE);
                }
            }).ifLeft(WaystonesAPI::tryTeleport).ifRight(error -> {
                serverPlayer.sendSystemMessage(error.getComponent().copy().withStyle(ChatFormatting.DARK_RED), false);
                WaystonesAPI.removeWaystoneFromDatabase(serverPlayer.server, waystone);
            });
        });
    }

    public static boolean teleport(Object o) {
        if (o instanceof PylonWaypointElement element) {
            PacketDistributor.sendToServer(new PlayerToPylonPacketC2S(element.getWaystone().getWaystoneUid()));
            return true;
        }
        return false;
    }
}
