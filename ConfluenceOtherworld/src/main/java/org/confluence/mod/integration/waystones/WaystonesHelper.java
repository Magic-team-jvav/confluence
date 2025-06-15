package org.confluence.mod.integration.waystones;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.DSL;
import net.blay09.mods.waystones.block.WaystoneBlock;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.LibTags;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModBiomes;
import org.confluence.mod.common.init.ModTabs;
import org.confluence.mod.common.init.ModTags;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class WaystonesHelper {
    public static final boolean LOADED = ModList.get().isLoaded("waystones");
    static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Confluence.MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Confluence.MODID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    private static final DeferredBlock<Block> FOREST_PYLON = register("forest_pylon", BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).mapColor(DyeColor.CYAN), (world, pos) -> {
        Holder<Biome> biome = world.getBiome(pos);
        return biome.is(Tags.Biomes.IS_FOREST) || biome.is(Tags.Biomes.IS_PLAINS);
    });
    private static final DeferredBlock<Block> SNOW_PYLON = register("snow_pylon", BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).mapColor(DyeColor.WHITE), (world, pos) -> world.getBiome(pos).is(Tags.Biomes.IS_SNOWY));
    private static final DeferredBlock<Block> DESERT_PYLON = register("desert_pylon", BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).mapColor(DyeColor.YELLOW), (world, pos) -> world.getBiome(pos).is(Tags.Biomes.IS_DESERT));
    private static final DeferredBlock<Block> CAVERN_PYLON = register("cavern_pylon", BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).mapColor(DyeColor.GRAY), (world, pos) -> world.dimensionType().bedWorks() && pos.getY() < world.getMinBuildHeight() + 104);
    private static final DeferredBlock<Block> OCEAN_PYLON = register("ocean_pylon", BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).mapColor(DyeColor.BLUE), (world, pos) -> world.getBiome(pos).is(Tags.Biomes.IS_OCEAN));
    private static final DeferredBlock<Block> JUNGLE_PYLON = register("jungle_pylon", BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).mapColor(DyeColor.GREEN), (world, pos) -> world.getBiome(pos).is(Tags.Biomes.IS_JUNGLE));
    private static final DeferredBlock<Block> HALLOW_PYLON = register("hallow_pylon", BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).mapColor(DyeColor.LIGHT_BLUE), (world, pos) -> world.getBiome(pos).is(ModTags.Biomes.THE_HALLOW));
    private static final DeferredBlock<Block> MUSHROOM_PYLON = register("mushroom_pylon", BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).mapColor(DyeColor.PURPLE), (world, pos) -> world.getBiome(pos).is(ModBiomes.GLOWING_MUSHROOM));
    private static final DeferredBlock<Block> UNIVERSAL_PYLON = register("universal_pylon", BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).mapColor(DyeColor.BROWN), (world, pos) -> true);

    public static final Supplier<BlockEntityType<PylonBlock.Entity>> PYLON_ENTITY = BLOCK_ENTITY_TYPES.register("pylon_entity", () -> BlockEntityType.Builder.of(PylonBlock.Entity::new, BLOCKS.getEntries().stream().map(DeferredHolder::get).toArray(Block[]::new)).build(DSL.remainderType()));

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
        return block;
    }

    public static void register(IEventBus eventBus) {
        if (LOADED) {
            BLOCKS.register(eventBus);
            BLOCK_ENTITY_TYPES.register(eventBus);
            ITEMS.register(eventBus);
            eventBus.addListener(WaystonesHelper::buildCreativeModeTabContents);
        }
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

    @OnlyIn(Dist.CLIENT)
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(PYLON_ENTITY.get(), context -> new GeoBlockRenderer<>(new PylonModel()) {
            @Override
            public void defaultRender(PoseStack poseStack, PylonBlock.Entity animatable, MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer, float yaw, float partialTick, int packedLight) {
                if (animatable.getBlockState().getValue(WaystoneBlock.HALF) == DoubleBlockHalf.LOWER) {
                    super.defaultRender(poseStack, animatable, bufferSource, renderType, buffer, yaw, partialTick, packedLight);
                }
            }
        });
    }
}
