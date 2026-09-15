package org.confluence.mod.common.init.block;

import com.mojang.datafixers.DSL;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.functional.BasePylonBlock;
import org.confluence.mod.common.init.ModBiomes;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.ModItems;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PylonBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Confluence.MODID);
    public static final Map<ResourceLocation, ResourceLocation> TYPE_TO_TEXTURE = new HashMap<>();

    // todo 目前只是占位符
    public static final DeferredBlock<BasePylonBlock> FOREST_PYLON = register("forest_pylon", DyeColor.CYAN, (world, pos, biome) -> biome.is(Tags.Biomes.IS_FOREST) || biome.is(Tags.Biomes.IS_PLAINS));
    public static final DeferredBlock<BasePylonBlock> SNOW_PYLON = register("snow_pylon", DyeColor.WHITE, (world, pos, biome) -> biome.is(Tags.Biomes.IS_SNOWY) || biome.is(Tags.Biomes.IS_ICY));
    public static final DeferredBlock<BasePylonBlock> DESERT_PYLON = register("desert_pylon", DyeColor.YELLOW, (world, pos, biome) -> biome.is(Tags.Biomes.IS_DESERT) || biome.is(Tags.Biomes.IS_BADLANDS));
    public static final DeferredBlock<BasePylonBlock> CAVERN_PYLON = register("cavern_pylon", DyeColor.GRAY, (world, pos, biome) -> world.dimensionType().bedWorks() && pos.getY() < world.getMinBuildHeight() + 104);
    public static final DeferredBlock<BasePylonBlock> OCEAN_PYLON = register("ocean_pylon", DyeColor.BLUE, (world, pos, biome) -> biome.is(Tags.Biomes.IS_OCEAN));
    public static final DeferredBlock<BasePylonBlock> JUNGLE_PYLON = register("jungle_pylon", DyeColor.GREEN, (world, pos, biome) -> biome.is(Tags.Biomes.IS_JUNGLE));
    public static final DeferredBlock<BasePylonBlock> HALLOW_PYLON = register("hallow_pylon", DyeColor.LIGHT_BLUE, (world, pos, biome) -> biome.is(ModTags.Biomes.THE_HALLOW));
    public static final DeferredBlock<BasePylonBlock> MUSHROOM_PYLON = register("mushroom_pylon", DyeColor.PURPLE, (world, pos, biome) -> biome.is(ModBiomes.GLOWING_MUSHROOM));
    public static final DeferredBlock<BasePylonBlock> UNIVERSAL_PYLON = register("universal_pylon", DyeColor.BROWN, (world, pos, biome) -> true);
    // todo 地狱晶塔 // todo 以太晶塔+晶塔微光转换配方
    public static final Supplier<BlockEntityType<BasePylonBlock.BEntity>> PYLON_ENTITY = ModBlocks.BLOCK_ENTITIES.register("pylon_entity", () -> BlockEntityType.Builder.of(BasePylonBlock.BEntity::new, BLOCKS.getEntries().stream().map(DeferredHolder::get).toArray(Block[]::new)).build(DSL.remainderType()));

    private static DeferredBlock<BasePylonBlock> register(String name, BlockBehaviour.Properties properties, BasePylonBlock.Survive survive) {
        int count = BLOCKS.getEntries().size();
        DeferredBlock<BasePylonBlock> block = BLOCKS.register(name, () -> new BasePylonBlock(count, properties, survive));
        ModItems.BLOCK_ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.BLUE)));
        TYPE_TO_TEXTURE.put(block.getId(), Confluence.asResource("textures/gui/pylon/" + block.getId().getPath() + ".png"));
        return block;
    }

    private static DeferredBlock<BasePylonBlock> register(String name, DyeColor dyeColor, BasePylonBlock.Survive survive) {
        return register(name, BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).mapColor(dyeColor).lightLevel(state -> 10), survive);
    }
}
