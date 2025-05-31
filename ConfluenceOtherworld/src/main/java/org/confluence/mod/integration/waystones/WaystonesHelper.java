package org.confluence.mod.integration.waystones;

import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.LibTags;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTabs;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class WaystonesHelper {
    public static final boolean LOADED = ModList.get().isLoaded("waystones");
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Confluence.MODID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    private static final DeferredBlock<Block> FOREST_PYLON = register("forest_pylon", BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).mapColor(DyeColor.CYAN));
    private static final DeferredBlock<Block> SNOW_PYLON = register("snow_pylon", BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).mapColor(DyeColor.WHITE));
    private static final DeferredBlock<Block> DESERT_PYLON = register("desert_pylon", BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).mapColor(DyeColor.YELLOW));
    private static final DeferredBlock<Block> CAVERN_PYLON = register("cavern_pylon", BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).mapColor(DyeColor.GRAY));
    private static final DeferredBlock<Block> OCEAN_PYLON = register("ocean_pylon", BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).mapColor(DyeColor.BLUE));
    private static final DeferredBlock<Block> JUNGLE_PYLON = register("jungle_pylon", BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).mapColor(DyeColor.GREEN));
    private static final DeferredBlock<Block> HALLOW_PYLON = register("hallow_pylon", BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).mapColor(DyeColor.LIGHT_BLUE));
    private static final DeferredBlock<Block> MUSHROOM_PYLON = register("mushroom_pylon", BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).mapColor(DyeColor.PURPLE));
    private static final DeferredBlock<Block> UNIVERSAL_PYLON = register("universal_pylon", BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).mapColor(DyeColor.BROWN));

    private static DeferredBlock<Block> register(String name, BlockBehaviour.Properties properties) {
        DeferredBlock<Block> block = BLOCKS.register(name, () -> {
            try {
                return PylonBlock.class.getDeclaredConstructor(BlockBehaviour.Properties.class).newInstance(properties);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.BLUE)));
        return block;
    }

    public static void register(IEventBus eventBus) {
        if (LOADED) {
            BLOCKS.register(eventBus);
            ITEMS.register(eventBus);
            eventBus.addListener(WaystonesHelper::blockEntityTypeAddBlocks);
            eventBus.addListener(WaystonesHelper::buildCreativeModeTabContents);
        }
    }

    private static void blockEntityTypeAddBlocks(BlockEntityTypeAddBlocksEvent event) {
        event.modify(
                ResourceKey.create(Registries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("waystones", "waystone")),
                BLOCKS.getEntries().stream().map(DeferredHolder::get).toArray(Block[]::new)
        );
    }

    private static void buildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == ModTabs.MECHANICAL.get()) {
            ITEMS.getEntries().forEach(item -> event.accept(item.get()));
        }
    }

    public static void blockTag(Function<TagKey<Block>, IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block>> consumer) {
        IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block> isTeleportTarget = consumer.apply(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("waystones", "is_teleport_target")));
        BLOCKS.getEntries().forEach(block -> isTeleportTarget.addOptional(block.getId()));
    }

    public static void itemTag(Function<TagKey<Item>, IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item>> consumer) {
        IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> wip = consumer.apply(LibTags.Items.WIP);
        ITEMS.getEntries().forEach(item -> wip.addOptional(item.getId()));
    }

    public static void addTranslateKeys(BiConsumer<String, String> consumer, boolean en) {
        if (en) {
            BLOCKS.getEntries().forEach(block -> consumer.accept(Util.makeDescriptionId("block", block.getId()), LibUtils.toTitleCase(block.getId().getPath())));
        } else {
            consumer.accept(Util.makeDescriptionId("block", FOREST_PYLON.getId()), "森林晶塔");
            consumer.accept(Util.makeDescriptionId("block", SNOW_PYLON.getId()), "雪原晶塔");
            consumer.accept(Util.makeDescriptionId("block", DESERT_PYLON.getId()), "沙漠晶塔");
            consumer.accept(Util.makeDescriptionId("block", CAVERN_PYLON.getId()), "洞穴晶塔");
            consumer.accept(Util.makeDescriptionId("block", OCEAN_PYLON.getId()), "海洋晶塔");
            consumer.accept(Util.makeDescriptionId("block", JUNGLE_PYLON.getId()), "丛林晶塔");
            consumer.accept(Util.makeDescriptionId("block", HALLOW_PYLON.getId()), "神圣晶塔");
            consumer.accept(Util.makeDescriptionId("block", MUSHROOM_PYLON.getId()), "蘑菇晶塔");
            consumer.accept(Util.makeDescriptionId("block", UNIVERSAL_PYLON.getId()), "万能晶塔");
        }
    }
}
