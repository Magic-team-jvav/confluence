package org.confluence.mod.common.init.block;

import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.natural.*;
import org.confluence.mod.common.init.item.ModItems;

import java.util.function.Function;
import java.util.function.Supplier;

public class OreBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Confluence.MODID);


    public static final DeferredBlock<Block> SANCTIFICATION_COAL_ORE = copyBlockRegister("sanctification_coal_ore", Blocks.COAL_ORE);
    public static final DeferredBlock<Block> CORRUPTION_COAL_ORE = copyBlockRegister("corruption_coal_ore", Blocks.COAL_ORE);
    public static final DeferredBlock<Block> FLESHIFICATION_COAL_ORE = copyBlockRegister("fleshification_coal_ore", Blocks.COAL_ORE);

    public static final DeferredBlock<Block> SANCTIFICATION_COPPER_ORE = copyBlockRegister("sanctification_copper_ore", Blocks.COPPER_ORE);
    public static final DeferredBlock<Block> CORRUPTION_COPPER_ORE = copyBlockRegister("corruption_copper_ore", Blocks.COPPER_ORE);
    public static final DeferredBlock<Block> FLESHIFICATION_COPPER_ORE = copyBlockRegister("fleshification_copper_ore", Blocks.COPPER_ORE);

    public static final DeferredBlock<Block> TIN_ORE = copyBlockRegister("tin_ore", Blocks.COPPER_ORE);
    public static final DeferredBlock<Block> DEEPSLATE_TIN_ORE = copyBlockRegister("deepslate_tin_ore", Blocks.DEEPSLATE_COPPER_ORE);
    public static final DeferredBlock<Block> SANCTIFICATION_TIN_ORE = copyBlockRegister("sanctification_tin_ore", Blocks.COPPER_ORE);
    public static final DeferredBlock<Block> CORRUPTION_TIN_ORE = copyBlockRegister("corruption_tin_ore", Blocks.COPPER_ORE);
    public static final DeferredBlock<Block> FLESHIFICATION_TIN_ORE = copyBlockRegister("fleshification_tin_ore", Blocks.COPPER_ORE);
    public static final DeferredBlock<Block> RAW_TIN_BLOCK = copyBlockRegister("raw_tin_block", Blocks.RAW_COPPER_BLOCK);
    public static final DeferredBlock<Block> TIN_BLOCK = copyBlockRegister("tin_block", Blocks.COPPER_BLOCK);

    public static final DeferredBlock<Block> SANCTIFICATION_IRON_ORE = copyBlockRegister("sanctification_iron_ore", Blocks.IRON_ORE);
    public static final DeferredBlock<Block> CORRUPTION_IRON_ORE = copyBlockRegister("corruption_iron_ore", Blocks.IRON_ORE);
    public static final DeferredBlock<Block> FLESHIFICATION_IRON_ORE = copyBlockRegister("fleshification_iron_ore", Blocks.IRON_ORE);

    public static final DeferredBlock<Block> LEAD_ORE = copyBlockRegister("lead_ore", Blocks.IRON_ORE);
    public static final DeferredBlock<Block> DEEPSLATE_LEAD_ORE = copyBlockRegister("deepslate_lead_ore", Blocks.DEEPSLATE_IRON_ORE);
    public static final DeferredBlock<Block> SANCTIFICATION_LEAD_ORE = copyBlockRegister("sanctification_lead_ore", Blocks.IRON_ORE);
    public static final DeferredBlock<Block> CORRUPTION_LEAD_ORE = copyBlockRegister("corruption_lead_ore", Blocks.IRON_ORE);
    public static final DeferredBlock<Block> FLESHIFICATION_LEAD_ORE = copyBlockRegister("fleshification_lead_ore", Blocks.IRON_ORE);
    public static final DeferredBlock<Block> RAW_LEAD_BLOCK = copyBlockRegister("raw_lead_block", Blocks.RAW_IRON_BLOCK);
    public static final DeferredBlock<Block> LEAD_BLOCK = copyBlockRegister("lead_block", Blocks.IRON_BLOCK);

    public static final DeferredBlock<Block> SILVER_ORE = copyBlockRegister("silver_ore", Blocks.IRON_ORE);
    public static final DeferredBlock<Block> DEEPSLATE_SILVER_ORE = copyBlockRegister("deepslate_silver_ore", Blocks.DEEPSLATE_IRON_ORE);
    public static final DeferredBlock<Block> SANCTIFICATION_SILVER_ORE = copyBlockRegister("sanctification_silver_ore", Blocks.IRON_ORE);
    public static final DeferredBlock<Block> CORRUPTION_SILVER_ORE = copyBlockRegister("corruption_silver_ore", Blocks.IRON_ORE);
    public static final DeferredBlock<Block> FLESHIFICATION_SILVER_ORE = copyBlockRegister("fleshification_silver_ore", Blocks.IRON_ORE);
    public static final DeferredBlock<Block> RAW_SILVER_BLOCK = copyBlockRegister("raw_silver_block", Blocks.RAW_IRON_BLOCK);
    public static final DeferredBlock<Block> SILVER_BLOCK = copyBlockRegister("silver_block", Blocks.IRON_BLOCK);

    public static final DeferredBlock<Block> TUNGSTEN_ORE = copyBlockRegister("tungsten_ore", Blocks.IRON_ORE);
    public static final DeferredBlock<Block> DEEPSLATE_TUNGSTEN_ORE = copyBlockRegister("deepslate_tungsten_ore", Blocks.DEEPSLATE_IRON_ORE);
    public static final DeferredBlock<Block> SANCTIFICATION_TUNGSTEN_ORE = copyBlockRegister("sanctification_tungsten_ore", Blocks.IRON_ORE);
    public static final DeferredBlock<Block> CORRUPTION_TUNGSTEN_ORE = copyBlockRegister("corruption_tungsten_ore", Blocks.IRON_ORE);
    public static final DeferredBlock<Block> FLESHIFICATION_TUNGSTEN_ORE = copyBlockRegister("fleshification_tungsten_ore", Blocks.IRON_ORE);
    public static final DeferredBlock<Block> RAW_TUNGSTEN_BLOCK = copyBlockRegister("raw_tungsten_block", Blocks.RAW_IRON_BLOCK);
    public static final DeferredBlock<Block> TUNGSTEN_BLOCK = copyBlockRegister("tungsten_block", Blocks.IRON_BLOCK);

    public static final DeferredBlock<Block> PLATINUM_ORE = copyBlockRegister("platinum_ore", Blocks.GOLD_ORE);
    public static final DeferredBlock<Block> DEEPSLATE_PLATINUM_ORE = copyBlockRegister("deepslate_platinum_ore", Blocks.DEEPSLATE_GOLD_ORE);
    public static final DeferredBlock<Block> SANCTIFICATION_PLATINUM_ORE = copyBlockRegister("sanctification_platinum_ore", Blocks.GOLD_ORE);
    public static final DeferredBlock<Block> CORRUPTION_PLATINUM_ORE = copyBlockRegister("corruption_platinum_ore", Blocks.GOLD_ORE);
    public static final DeferredBlock<Block> FLESHIFICATION_PLATINUM_ORE = copyBlockRegister("fleshification_platinum_ore", Blocks.GOLD_ORE);
    public static final DeferredBlock<Block> RAW_PLATINUM_BLOCK = copyBlockRegister("raw_platinum_block", Blocks.RAW_GOLD_BLOCK);
    public static final DeferredBlock<Block> PLATINUM_BLOCK = copyBlockRegister("platinum_block", Blocks.GOLD_BLOCK);


    public static final DeferredBlock<Block> SANCTIFICATION_GOLD_ORE = copyBlockRegister("sanctification_gold_ore", Blocks.GOLD_ORE);
    public static final DeferredBlock<Block> CORRUPTION_GOLD_ORE = copyBlockRegister("corruption_gold_ore", Blocks.GOLD_ORE);
    public static final DeferredBlock<Block> FLESHIFICATION_GOLD_ORE = copyBlockRegister("fleshification_gold_ore", Blocks.GOLD_ORE);

    public static final DeferredBlock<Block> METEORITE_ORE = registerWithItem("meteorite_ore", MeteoriteOre::new, block -> new BlockItem(block, new Item.Properties().fireResistant()));
    public static final DeferredBlock<Block> RAW_METEORITE_BLOCK = registerWithItem("raw_meteorite_block", MeteoriteOre::new, block -> new BlockItem(block, new Item.Properties().fireResistant()));
    public static final DeferredBlock<Block> METEORITE_BLOCK = registerWithItem("meteorite_block", MeteoriteOre::new, block -> new BlockItem(block, new Item.Properties().fireResistant()));

    public static final DeferredBlock<Block> STURDY_FOSSIL_BLOCK = copyBlockRegister("sturdy_fossil_block", Blocks.DIAMOND_BLOCK);
    public static final DeferredBlock<Block> OPAL_BLOCK = copyBlockRegister("opal_block", Blocks.DIAMOND_BLOCK);
    public static final DeferredBlock<Block> GELSTONE_BLOCK = copyBlockRegister("gelstone_block", Blocks.DIAMOND_BLOCK);
    public static final DeferredBlock<Block> COLD_CRYSTAL_BLOCK = copyBlockRegister("cold_crystal_block", Blocks.DIAMOND_BLOCK);

    public static final DeferredBlock<Block> SANCTIFICATION_EMERALD_ORE = copyBlockRegister("sanctification_emerald_ore", Blocks.EMERALD_ORE);
    public static final DeferredBlock<Block> CORRUPTION_EMERALD_ORE = copyBlockRegister("corruption_emerald_ore", Blocks.EMERALD_ORE);
    public static final DeferredBlock<Block> FLESHIFICATION_EMERALD_ORE = copyBlockRegister("fleshification_emerald_ore", Blocks.EMERALD_ORE);

    public static final DeferredBlock<Block> SANCTIFICATION_DIAMOND_ORE = copyBlockRegister("sanctification_diamond_ore", Blocks.DIAMOND_ORE);
    public static final DeferredBlock<Block> CORRUPTION_DIAMOND_ORE = copyBlockRegister("corruption_diamond_ore", Blocks.DIAMOND_ORE);
    public static final DeferredBlock<Block> FLESHIFICATION_DIAMOND_ORE = copyBlockRegister("fleshification_diamond_ore", Blocks.DIAMOND_ORE);

    public static final DeferredBlock<Block> RUBY_ORE = copyBlockRegister("ruby_ore", Blocks.IRON_ORE);
    public static final DeferredBlock<Block> DEEPSLATE_RUBY_ORE = copyBlockRegister("deepslate_ruby_ore", Blocks.DEEPSLATE_IRON_ORE);
    public static final DeferredBlock<Block> SANCTIFICATION_RUBY_ORE = copyBlockRegister("sanctification_ruby_ore", Blocks.IRON_ORE);
    public static final DeferredBlock<Block> CORRUPTION_RUBY_ORE = copyBlockRegister("corruption_ruby_ore", Blocks.IRON_ORE);
    public static final DeferredBlock<Block> FLESHIFICATION_RUBY_ORE = copyBlockRegister("fleshification_ruby_ore", Blocks.IRON_ORE);

    public static final DeferredBlock<Block> AMBER_ORE = copyBlockRegister("amber_ore", Blocks.IRON_ORE);
    public static final DeferredBlock<Block> RED_SAND_AMBER_ORE = copyBlockRegister("red_sand_amber_ore", Blocks.IRON_ORE);
    public static final DeferredBlock<Block> SANCTIFICATION_AMBER_ORE = copyBlockRegister("sanctification_amber_ore", Blocks.IRON_ORE);
    public static final DeferredBlock<Block> CORRUPTION_AMBER_ORE = copyBlockRegister("corruption_amber_ore", Blocks.IRON_ORE);
    public static final DeferredBlock<Block> FLESHIFICATION_AMBER_ORE = copyBlockRegister("fleshification_amber_ore", Blocks.IRON_ORE);

    public static final DeferredBlock<Block> TOPAZ_ORE = copyBlockRegister("topaz_ore", Blocks.IRON_ORE);
    public static final DeferredBlock<Block> DEEPSLATE_TOPAZ_ORE = copyBlockRegister("deepslate_topaz_ore", Blocks.DEEPSLATE_DIAMOND_ORE);
    public static final DeferredBlock<Block> SANCTIFICATION_TOPAZ_ORE = copyBlockRegister("sanctification_topaz_ore", Blocks.IRON_ORE);
    public static final DeferredBlock<Block> CORRUPTION_TOPAZ_ORE = copyBlockRegister("corruption_topaz_ore", Blocks.IRON_ORE);
    public static final DeferredBlock<Block> FLESHIFICATION_TOPAZ_ORE = copyBlockRegister("fleshification_topaz_ore", Blocks.IRON_ORE);

    public static final DeferredBlock<Block> JADE_ORE = copyBlockRegister("jade_ore", Blocks.EMERALD_ORE);
    public static final DeferredBlock<Block> DEEPSLATE_JADE_ORE = copyBlockRegister("deepslate_jade_ore", Blocks.DEEPSLATE_DIAMOND_ORE);
    public static final DeferredBlock<Block> SANCTIFICATION_JADE_ORE = copyBlockRegister("sanctification_jade_ore", Blocks.EMERALD_ORE);
    public static final DeferredBlock<Block> CORRUPTION_JADE_ORE = copyBlockRegister("corruption_jade_ore", Blocks.EMERALD_ORE);
    public static final DeferredBlock<Block> FLESHIFICATION_JADE_ORE = copyBlockRegister("fleshification_jade_ore", Blocks.EMERALD_ORE);

    public static final DeferredBlock<Block> SAPPHIRE_ORE = copyBlockRegister("sapphire_ore", Blocks.IRON_ORE);
    public static final DeferredBlock<Block> DEEPSLATE_SAPPHIRE_ORE = copyBlockRegister("deepslate_sapphire_ore", Blocks.DEEPSLATE_DIAMOND_ORE);
    public static final DeferredBlock<Block> SANCTIFICATION_SAPPHIRE_ORE = copyBlockRegister("sanctification_sapphire_ore", Blocks.IRON_ORE);
    public static final DeferredBlock<Block> CORRUPTION_SAPPHIRE_ORE = copyBlockRegister("corruption_sapphire_ore", Blocks.IRON_ORE);
    public static final DeferredBlock<Block> FLESHIFICATION_SAPPHIRE_ORE = copyBlockRegister("fleshification_sapphire_ore", Blocks.IRON_ORE);

    public static final DeferredBlock<Block> AMETHYST_ORE = copyBlockRegister("amethyst_ore", Blocks.IRON_ORE);
    public static final DeferredBlock<Block> DEEPSLATE_AMETHYST_ORE = copyBlockRegister("deepslate_amethyst_ore", Blocks.DEEPSLATE_DIAMOND_ORE);
    public static final DeferredBlock<Block> SANCTIFICATION_AMETHYST_ORE = copyBlockRegister("sanctification_amethyst_ore", Blocks.IRON_ORE);
    public static final DeferredBlock<Block> CORRUPTION_AMETHYST_ORE = copyBlockRegister("corruption_amethyst_ore", Blocks.IRON_ORE);
    public static final DeferredBlock<Block> FLESHIFICATION_AMETHYST_ORE = copyBlockRegister("fleshification_amethyst_ore", Blocks.IRON_ORE);

    public static final DeferredBlock<Block> SANCTIFICATION_LAPIS_ORE = copyBlockRegister("sanctification_lapis_ore", Blocks.LAPIS_ORE);
    public static final DeferredBlock<Block> CORRUPTION_LAPIS_ORE = copyBlockRegister("corruption_lapis_ore", Blocks.LAPIS_ORE);
    public static final DeferredBlock<Block> FLESHIFICATION_LAPIS_ORE = copyBlockRegister("fleshification_lapis_ore", Blocks.LAPIS_ORE);

    public static final DeferredBlock<Block> DEMONITE_ORE = copyBlockRegister("demonite_ore", Blocks.IRON_ORE);
    public static final DeferredBlock<Block> DEEPSLATE_DEMONITE_ORE = copyBlockRegister("deepslate_demonite_ore", Blocks.DEEPSLATE_IRON_ORE);
    public static final DeferredBlock<Block> SANCTIFICATION_DEMONITE_ORE = copyBlockRegister("sanctification_demonite_ore", Blocks.IRON_ORE);
    public static final DeferredBlock<Block> CORRUPTION_DEMONITE_ORE = copyBlockRegister("corruption_demonite_ore", Blocks.IRON_ORE);
    public static final DeferredBlock<Block> FLESHIFICATION_DEMONITE_ORE = copyBlockRegister("fleshification_demonite_ore", Blocks.IRON_ORE);

    public static final DeferredBlock<Block> DEMONITE_BLOCK = copyBlockRegister("demonite_block", Blocks.RAW_IRON_BLOCK);
    public static final DeferredBlock<Block> RAW_DEMONITE_BLOCK = copyBlockRegister("raw_demonite_block", Blocks.IRON_BLOCK);

    public static final DeferredBlock<Block> CRIMTANE_ORE = copyBlockRegister("crimtane_ore", Blocks.IRON_ORE);
    public static final DeferredBlock<Block> DEEPSLATE_CRIMTANE_ORE = copyBlockRegister("deepslate_crimtane_ore", Blocks.DEEPSLATE_IRON_ORE);
    public static final DeferredBlock<Block> SANCTIFICATION_CRIMTANE_ORE = copyBlockRegister("sanctification_crimtane_ore", Blocks.IRON_ORE);
    public static final DeferredBlock<Block> CORRUPTION_CRIMTANE_ORE = copyBlockRegister("corruption_crimtane_ore", Blocks.IRON_ORE);
    public static final DeferredBlock<Block> FLESHIFICATION_CRIMTANE_ORE = copyBlockRegister("fleshification_crimtane_ore", Blocks.IRON_ORE);

    public static final DeferredBlock<Block> RAW_CRIMTANE_BLOCK = copyBlockRegister("raw_crimtane_block", Blocks.IRON_BLOCK);
    public static final DeferredBlock<Block> CRIMTANE_BLOCK = copyBlockRegister("crimtane_block", Blocks.RAW_IRON_BLOCK);

    public static final DeferredBlock<Block> HALLOWED_BLOCK = copyBlockRegister("hallowed_block", Blocks.IRON_BLOCK);

    public static final DeferredBlock<Block> CHLOROPHYTE_ORE = registerWithItem("chlorophyte_ore", ChlorophyteOreBlock::new);
    public static final DeferredBlock<Block> RAW_CHLOROPHYTE_BLOCK = copyBlockRegister("raw_chlorophyte_block", Blocks.IRON_BLOCK);
    public static final DeferredBlock<Block> CHLOROPHYTE_BLOCK = copyBlockRegister("chlorophyte_block", Blocks.RAW_IRON_BLOCK);

    public static final DeferredBlock<Block> SHROOMITE_BLOCK = copyBlockRegister("shroomite_block", Blocks.IRON_BLOCK);

    public static final DeferredBlock<Block> SPECTRE_BLOCK = copyBlockRegister("spectre_block", Blocks.IRON_BLOCK);

    public static final DeferredBlock<Block> RAW_LUMINITE_BLOCK = copyBlockRegister("raw_luminite_block", Blocks.RAW_IRON_BLOCK);
    public static final DeferredBlock<Block> LUMINITE_BLOCK = copyBlockRegister("luminite_block", Blocks.IRON_BLOCK);

    public static final DeferredBlock<Block> OPAL_ORE = simpleBlockRegister("opal_ore", OpalOreBlock::new);
    public static final DeferredBlock<Block> GELSTONE_ORE = copyBlockRegister("gelstone_ore", Blocks.IRON_ORE);
    public static final DeferredBlock<Block> SPORE_ROOT_BLOCK = copyBlockRegister("spore_root_block", Blocks.IRON_ORE);
    public static final DeferredBlock<Block> WINTER_MARROW_BLOCK = copyBlockRegister("winter_marrow_block", Blocks.IRON_ORE);
    public static final DeferredBlock<Block> COLD_CRYSTAL_ORE = registerWithItem("cold_crystal_ore", () -> new TransparentBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_ORE).noOcclusion().sound(SoundType.GRASS)));
    public static final DeferredBlock<Block> HELLSTONE = registerWithItem("hellstone", () -> new HellStoneBlock(true), block -> new BlockItem(block, new Item.Properties().fireResistant()));
    public static final DeferredBlock<Block> ASH_HELLSTONE = registerWithItem("ash_hellstone", () -> new HellStoneBlock(true), block -> new BlockItem(block, new Item.Properties().fireResistant()));
    public static final DeferredBlock<Block> HELLSTONE_BRICKS = registerWithItem("hellstone_bricks", () -> new HellStoneBlock(true), block -> new BlockItem(block, new Item.Properties().fireResistant()));
    public static final DeferredBlock<Block> RAW_HELLSTONE_BLOCK = registerWithItem("raw_hellstone_block", () -> new HellStoneBlock(false), block -> new BlockItem(block, new Item.Properties().fireResistant()));
    public static final DeferredBlock<Block> HELLSTONE_BLOCK = registerWithItem("hellstone_block", () -> new HellStoneBlock(false), block -> new BlockItem(block, new Item.Properties().fireResistant()));

    // 红石矿
    public static final DeferredBlock<Block> SANCTIFICATION_REDSTONE_ORE = simpleBlockRegister("sanctification_redstone_ore", () -> new RedStoneOreBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_REDSTONE_ORE)));
    public static final DeferredBlock<Block> CORRUPTION_REDSTONE_ORE = simpleBlockRegister("corruption_redstone_ore", () -> new RedStoneOreBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_REDSTONE_ORE)));
    public static final DeferredBlock<Block> FLESHIFICATION_REDSTONE_ORE = simpleBlockRegister("fleshification_redstone_ore", () -> new RedStoneOreBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_REDSTONE_ORE)));

    public static final DeferredBlock<Block> DEEPSLATE_COBALT_ORE = simpleBlockRegister("deepslate_cobalt_ore", () -> new StepRevealingBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE).strength(15.0F, 1200.0F)));
    public static final DeferredBlock<Block> RAW_COBALT_BLOCK = simpleBlockRegister("raw_cobalt_block");
    public static final DeferredBlock<Block> COBALT_BLOCK = simpleBlockRegister("cobalt_block");

    public static final DeferredBlock<Block> DEEPSLATE_PALLADIUM_ORE = simpleBlockRegister("deepslate_palladium_ore", () -> new StepRevealingBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE).strength(15.0F, 1200.0F)));
    public static final DeferredBlock<Block> RAW_PALLADIUM_BLOCK = simpleBlockRegister("raw_palladium_block");
    public static final DeferredBlock<Block> PALLADIUM_BLOCK = simpleBlockRegister("palladium_block");

    public static final DeferredBlock<Block> DEEPSLATE_MYTHRIL_ORE = simpleBlockRegister("deepslate_mythril_ore", () -> new StepRevealingBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE).strength(20.0F, 1200.0F)));
    public static final DeferredBlock<Block> RAW_MYTHRIL_BLOCK = simpleBlockRegister("raw_mythril_block");
    public static final DeferredBlock<Block> MYTHRIL_BLOCK = simpleBlockRegister("mythril_block");

    public static final DeferredBlock<Block> DEEPSLATE_ORICHALCUM_ORE = simpleBlockRegister("deepslate_orichalcum_ore", () -> new StepRevealingBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE).strength(20.0F, 1200.0F)));
    public static final DeferredBlock<Block> RAW_ORICHALCUM_BLOCK = simpleBlockRegister("raw_orichalcum_block");
    public static final DeferredBlock<Block> ORICHALCUM_BLOCK = simpleBlockRegister("orichalcum_block");

    public static final DeferredBlock<Block> DEEPSLATE_ADAMANTITE_ORE = simpleBlockRegister("deepslate_adamantite_ore", () -> new StepRevealingBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE).strength(25.0F, 1200.0F)));
    public static final DeferredBlock<Block> RAW_ADAMANTITE_BLOCK = simpleBlockRegister("raw_adamantite_block");
    public static final DeferredBlock<Block> ADAMANTITE_BLOCK = simpleBlockRegister("adamantite_block");

    public static final DeferredBlock<Block> DEEPSLATE_TITANIUM_ORE = simpleBlockRegister("deepslate_titanium_ore", () -> new StepRevealingBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE).strength(25.0F, 1200.0F)));
    public static final DeferredBlock<Block> RAW_TITANIUM_BLOCK = simpleBlockRegister("raw_titanium_block");
    public static final DeferredBlock<Block> TITANIUM_BLOCK = simpleBlockRegister("titanium_block");

    private static DeferredBlock<Block> simpleBlockRegister(String name) {
        DeferredBlock<Block> block = BLOCKS.registerSimpleBlock(name, BlockBehaviour.Properties.of());
        ModItems.BLOCK_ITEMS.registerSimpleBlockItem(name, block);
        return block;
    }

    private static DeferredBlock<Block> simpleBlockRegister(String name, Supplier<Block> blockSupplier) {
        DeferredBlock<Block> block = BLOCKS.register(name, blockSupplier);
        ModItems.BLOCK_ITEMS.registerSimpleBlockItem(name, block);
        return block;
    }

    private static DeferredBlock<Block> simpleBlockRegister(String name, BlockBehaviour.Properties props) {
        DeferredBlock<Block> block = BLOCKS.registerSimpleBlock(name, props);
        ModItems.BLOCK_ITEMS.registerSimpleBlockItem(name, block);
        return block;
    }

    private static DeferredBlock<Block> registerWithItem(String name, Supplier<Block> blockSupplier, Function<Block, BlockItem> function) {
        DeferredBlock<Block> block = BLOCKS.register(name, blockSupplier);
        ModItems.BLOCK_ITEMS.register(name, () -> function.apply(block.get()));
        return block;
    }

    private static DeferredBlock<Block> registerWithItem(String name, Supplier<Block> blockSupplier) {
        DeferredBlock<Block> block = BLOCKS.register(name, blockSupplier);
        ModItems.BLOCK_ITEMS.registerSimpleBlockItem(block);
        return block;
    }

    private static DeferredBlock<Block> copyBlockRegister(String newName, Block originalBlock) {
        DeferredBlock<Block> block = BLOCKS.registerSimpleBlock(newName, BlockBehaviour.Properties.ofFullCopy(originalBlock));
        ModItems.BLOCK_ITEMS.registerSimpleBlockItem(newName, block);
        return block;
    }

    public static void acceptTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block> tag) {
        BLOCKS.getEntries().forEach(block -> tag.add(block.get()));
    }
}
