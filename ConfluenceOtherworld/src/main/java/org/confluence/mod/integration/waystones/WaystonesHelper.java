package org.confluence.mod.integration.waystones;

import com.mojang.datafixers.DSL;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.LibTags;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.GroupItem;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.component.ValueComponent;
import org.confluence.mod.common.data.gen.data_map.ValueSubProvider;
import org.confluence.mod.common.init.ModBiomes;
import org.confluence.mod.common.init.ModTags;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class WaystonesHelper {
    public static final String MODID = "waystones";
    public static final boolean IS_LOADED = LibUtils.isModLoaded(MODID);
    public static final Map<ResourceLocation, ResourceLocation> TYPE_TO_TEXTURE = new HashMap<>();
    static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Confluence.MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Confluence.MODID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    // 确保不要在任何地方直接调用它们！在没安装指路石模组时这些注册项不会被注册
    public static final DeferredBlock<Block> FOREST_PYLON = register("forest_pylon", DyeColor.CYAN, (world, pos, biome) -> biome.is(Tags.Biomes.IS_FOREST) || biome.is(Tags.Biomes.IS_PLAINS));
    public static final DeferredBlock<Block> SNOW_PYLON = register("snow_pylon", DyeColor.WHITE, (world, pos, biome) -> biome.is(Tags.Biomes.IS_SNOWY) || biome.is(Tags.Biomes.IS_ICY));
    public static final DeferredBlock<Block> DESERT_PYLON = register("desert_pylon", DyeColor.YELLOW, (world, pos, biome) -> biome.is(Tags.Biomes.IS_DESERT) || biome.is(Tags.Biomes.IS_BADLANDS));
    public static final DeferredBlock<Block> CAVERN_PYLON = register("cavern_pylon", DyeColor.GRAY, (world, pos, biome) -> world.dimensionType().bedWorks() && pos.getY() < world.getMinBuildHeight() + 104);
    public static final DeferredBlock<Block> OCEAN_PYLON = register("ocean_pylon", DyeColor.BLUE, (world, pos, biome) -> biome.is(Tags.Biomes.IS_OCEAN));
    public static final DeferredBlock<Block> JUNGLE_PYLON = register("jungle_pylon", DyeColor.GREEN, (world, pos, biome) -> biome.is(Tags.Biomes.IS_JUNGLE));
    public static final DeferredBlock<Block> HALLOW_PYLON = register("hallow_pylon", DyeColor.LIGHT_BLUE, (world, pos, biome) -> biome.is(ModTags.Biomes.THE_HALLOW));
    public static final DeferredBlock<Block> MUSHROOM_PYLON = register("mushroom_pylon", DyeColor.PURPLE, (world, pos, biome) -> biome.is(ModBiomes.GLOWING_MUSHROOM));
    public static final DeferredBlock<Block> UNIVERSAL_PYLON = register("universal_pylon", DyeColor.BROWN, (world, pos, biome) -> true);

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

    private static DeferredBlock<Block> register(String name, DyeColor dyeColor, PylonBlock.Survive survive) {
        return register(name, BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).mapColor(dyeColor).lightLevel(state -> 10), survive);
    }

    public static void register(IEventBus eventBus) {
        if (IS_LOADED) {
            BLOCKS.register(eventBus);
            BLOCK_ENTITY_TYPES.register(eventBus);
            ITEMS.register(eventBus);
        }
    }

    public static void accept(CreativeModeTab.Output output) {
        if (IS_LOADED) {
            List<ItemStack> pylons = ITEMS.getEntries().stream().map(holder -> holder.get().getDefaultInstance()).toList();
            output.accept(GroupItem.of(Confluence.asResource("pylon"), pylons));
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
        IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block> isTeleportTarget = consumer.apply(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(MODID, "is_teleport_target")));
        IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block> mineableWithPickaxe = consumer.apply(BlockTags.MINEABLE_WITH_PICKAXE);
        IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block> needsOneLevel = consumer.apply(ModTags.Blocks.NEEDS_1_LEVEL);
        BLOCKS.getEntries().forEach(block -> {
            ResourceLocation id = block.getId();
            isTeleportTarget.addOptional(id);
            mineableWithPickaxe.addOptional(id);
            needsOneLevel.addOptional(id);
        });
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
}
