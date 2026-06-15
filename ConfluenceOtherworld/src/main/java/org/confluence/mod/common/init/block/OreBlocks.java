package org.confluence.mod.common.init.block;

import com.google.common.base.Supplier;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneOreBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.natural.*;
import org.confluence.mod.common.init.item.ModItems;
import org.mesdag.portlib.registries.PortBlockRegistration;
import org.mesdag.portlib.registries.PortDeferredBlock;
import org.mesdag.portlib.registries.PortRegisterHandler;
import org.mesdag.portlib.wrapper.world.level.block.PortTransparentBlock;

import java.util.function.Function;

public class OreBlocks {
    public static void init() {}

    public static final PortBlockRegistration BLOCKS = PortRegisterHandler.block(Confluence.MODID);

    public static final PortDeferredBlock<Block> SANCTIFICATION_COAL_ORE = copyBlockRegister("sanctification_coal_ore", Blocks.COAL_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_COAL_ORE = copyBlockRegister("corruption_coal_ore", Blocks.COAL_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_COAL_ORE = copyBlockRegister("fleshification_coal_ore", Blocks.COAL_ORE);

    public static final PortDeferredBlock<Block> SANCTIFICATION_COPPER_ORE = copyBlockRegister("sanctification_copper_ore", Blocks.COPPER_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_COPPER_ORE = copyBlockRegister("corruption_copper_ore", Blocks.COPPER_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_COPPER_ORE = copyBlockRegister("fleshification_copper_ore", Blocks.COPPER_ORE);

    public static final PortDeferredBlock<Block> TIN_ORE = copyBlockRegister("tin_ore", Blocks.COPPER_ORE);
    public static final PortDeferredBlock<Block> DEEPSLATE_TIN_ORE = copyBlockRegister("deepslate_tin_ore", Blocks.DEEPSLATE_COPPER_ORE);
    public static final PortDeferredBlock<Block> SANCTIFICATION_TIN_ORE = copyBlockRegister("sanctification_tin_ore", Blocks.COPPER_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_TIN_ORE = copyBlockRegister("corruption_tin_ore", Blocks.COPPER_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_TIN_ORE = copyBlockRegister("fleshification_tin_ore", Blocks.COPPER_ORE);
    public static final PortDeferredBlock<Block> RAW_TIN_BLOCK = copyBlockRegister("raw_tin_block", Blocks.RAW_COPPER_BLOCK);
    public static final PortDeferredBlock<Block> TIN_BLOCK = copyBlockRegister("tin_block", Blocks.COPPER_BLOCK);

    public static final PortDeferredBlock<Block> SANCTIFICATION_IRON_ORE = copyBlockRegister("sanctification_iron_ore", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_IRON_ORE = copyBlockRegister("corruption_iron_ore", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_IRON_ORE = copyBlockRegister("fleshification_iron_ore", Blocks.IRON_ORE);

    public static final PortDeferredBlock<Block> LEAD_ORE = copyBlockRegister("lead_ore", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> DEEPSLATE_LEAD_ORE = copyBlockRegister("deepslate_lead_ore", Blocks.DEEPSLATE_IRON_ORE);
    public static final PortDeferredBlock<Block> SANCTIFICATION_LEAD_ORE = copyBlockRegister("sanctification_lead_ore", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_LEAD_ORE = copyBlockRegister("corruption_lead_ore", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_LEAD_ORE = copyBlockRegister("fleshification_lead_ore", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> RAW_LEAD_BLOCK = copyBlockRegister("raw_lead_block", Blocks.RAW_IRON_BLOCK);
    public static final PortDeferredBlock<Block> LEAD_BLOCK = copyBlockRegister("lead_block", Blocks.IRON_BLOCK);

    public static final PortDeferredBlock<Block> SILVER_ORE = copyBlockRegister("silver_ore", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> DEEPSLATE_SILVER_ORE = copyBlockRegister("deepslate_silver_ore", Blocks.DEEPSLATE_IRON_ORE);
    public static final PortDeferredBlock<Block> SANCTIFICATION_SILVER_ORE = copyBlockRegister("sanctification_silver_ore", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_SILVER_ORE = copyBlockRegister("corruption_silver_ore", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_SILVER_ORE = copyBlockRegister("fleshification_silver_ore", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> RAW_SILVER_BLOCK = copyBlockRegister("raw_silver_block", Blocks.RAW_IRON_BLOCK);
    public static final PortDeferredBlock<Block> SILVER_BLOCK = copyBlockRegister("silver_block", Blocks.IRON_BLOCK);

    public static final PortDeferredBlock<Block> TUNGSTEN_ORE = copyBlockRegister("tungsten_ore", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> DEEPSLATE_TUNGSTEN_ORE = copyBlockRegister("deepslate_tungsten_ore", Blocks.DEEPSLATE_IRON_ORE);
    public static final PortDeferredBlock<Block> SANCTIFICATION_TUNGSTEN_ORE = copyBlockRegister("sanctification_tungsten_ore", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_TUNGSTEN_ORE = copyBlockRegister("corruption_tungsten_ore", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_TUNGSTEN_ORE = copyBlockRegister("fleshification_tungsten_ore", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> RAW_TUNGSTEN_BLOCK = copyBlockRegister("raw_tungsten_block", Blocks.RAW_IRON_BLOCK);
    public static final PortDeferredBlock<Block> TUNGSTEN_BLOCK = copyBlockRegister("tungsten_block", Blocks.IRON_BLOCK);

    public static final PortDeferredBlock<Block> PLATINUM_ORE = copyBlockRegister("platinum_ore", Blocks.GOLD_ORE);
    public static final PortDeferredBlock<Block> DEEPSLATE_PLATINUM_ORE = copyBlockRegister("deepslate_platinum_ore", Blocks.DEEPSLATE_GOLD_ORE);
    public static final PortDeferredBlock<Block> SANCTIFICATION_PLATINUM_ORE = copyBlockRegister("sanctification_platinum_ore", Blocks.GOLD_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_PLATINUM_ORE = copyBlockRegister("corruption_platinum_ore", Blocks.GOLD_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_PLATINUM_ORE = copyBlockRegister("fleshification_platinum_ore", Blocks.GOLD_ORE);
    public static final PortDeferredBlock<Block> RAW_PLATINUM_BLOCK = copyBlockRegister("raw_platinum_block", Blocks.RAW_GOLD_BLOCK);
    public static final PortDeferredBlock<Block> PLATINUM_BLOCK = copyBlockRegister("platinum_block", Blocks.GOLD_BLOCK);


    public static final PortDeferredBlock<Block> SANCTIFICATION_GOLD_ORE = copyBlockRegister("sanctification_gold_ore", Blocks.GOLD_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_GOLD_ORE = copyBlockRegister("corruption_gold_ore", Blocks.GOLD_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_GOLD_ORE = copyBlockRegister("fleshification_gold_ore", Blocks.GOLD_ORE);

    public static final PortDeferredBlock<MeteoriteOre> METEORITE_ORE = registerWithItem("meteorite_ore", MeteoriteOre::new, block -> new BlockItem(block, new Item.Properties().fireResistant()));
    public static final PortDeferredBlock<MeteoriteOre> RAW_METEORITE_BLOCK = registerWithItem("raw_meteorite_block", MeteoriteOre::new, block -> new BlockItem(block, new Item.Properties().fireResistant()));
    public static final PortDeferredBlock<MeteoriteOre> METEORITE_BLOCK = registerWithItem("meteorite_block", MeteoriteOre::new, block -> new BlockItem(block, new Item.Properties().fireResistant()));

    public static final PortDeferredBlock<Block> STURDY_FOSSIL_BLOCK = copyBlockRegister("sturdy_fossil_block", Blocks.DIAMOND_BLOCK);
    public static final PortDeferredBlock<Block> OPAL_BLOCK = copyBlockRegister("opal_block", Blocks.DIAMOND_BLOCK);
    public static final PortDeferredBlock<Block> GELSTONE_BLOCK = copyBlockRegister("gelstone_block", Blocks.DIAMOND_BLOCK);
    public static final PortDeferredBlock<Block> COLD_CRYSTAL_BLOCK = copyBlockRegister("cold_crystal_block", Blocks.DIAMOND_BLOCK);

    public static final PortDeferredBlock<Block> SANCTIFICATION_EMERALD_ORE = copyBlockRegister("sanctification_emerald_ore", Blocks.EMERALD_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_EMERALD_ORE = copyBlockRegister("corruption_emerald_ore", Blocks.EMERALD_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_EMERALD_ORE = copyBlockRegister("fleshification_emerald_ore", Blocks.EMERALD_ORE);

    public static final PortDeferredBlock<Block> SANCTIFICATION_DIAMOND_ORE = copyBlockRegister("sanctification_diamond_ore", Blocks.DIAMOND_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_DIAMOND_ORE = copyBlockRegister("corruption_diamond_ore", Blocks.DIAMOND_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_DIAMOND_ORE = copyBlockRegister("fleshification_diamond_ore", Blocks.DIAMOND_ORE);

    public static final PortDeferredBlock<Block> RUBY_ORE = copyBlockRegister("ruby_ore", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> DEEPSLATE_RUBY_ORE = copyBlockRegister("deepslate_ruby_ore", Blocks.DEEPSLATE_IRON_ORE);
    public static final PortDeferredBlock<Block> SANCTIFICATION_RUBY_ORE = copyBlockRegister("sanctification_ruby_ore", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_RUBY_ORE = copyBlockRegister("corruption_ruby_ore", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_RUBY_ORE = copyBlockRegister("fleshification_ruby_ore", Blocks.IRON_ORE);

    public static final PortDeferredBlock<Block> AMBER_ORE = copyBlockRegister("amber_ore", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> RED_SAND_AMBER_ORE = copyBlockRegister("red_sand_amber_ore", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> SANCTIFICATION_AMBER_ORE = copyBlockRegister("sanctification_amber_ore", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_AMBER_ORE = copyBlockRegister("corruption_amber_ore", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_AMBER_ORE = copyBlockRegister("fleshification_amber_ore", Blocks.IRON_ORE);

    public static final PortDeferredBlock<Block> TOPAZ_ORE = copyBlockRegister("topaz_ore", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> DEEPSLATE_TOPAZ_ORE = copyBlockRegister("deepslate_topaz_ore", Blocks.DEEPSLATE_DIAMOND_ORE);
    public static final PortDeferredBlock<Block> SANCTIFICATION_TOPAZ_ORE = copyBlockRegister("sanctification_topaz_ore", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_TOPAZ_ORE = copyBlockRegister("corruption_topaz_ore", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_TOPAZ_ORE = copyBlockRegister("fleshification_topaz_ore", Blocks.IRON_ORE);

    public static final PortDeferredBlock<Block> JADE_ORE = copyBlockRegister("jade_ore", Blocks.EMERALD_ORE);
    public static final PortDeferredBlock<Block> DEEPSLATE_JADE_ORE = copyBlockRegister("deepslate_jade_ore", Blocks.DEEPSLATE_DIAMOND_ORE);
    public static final PortDeferredBlock<Block> SANCTIFICATION_JADE_ORE = copyBlockRegister("sanctification_jade_ore", Blocks.EMERALD_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_JADE_ORE = copyBlockRegister("corruption_jade_ore", Blocks.EMERALD_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_JADE_ORE = copyBlockRegister("fleshification_jade_ore", Blocks.EMERALD_ORE);

    public static final PortDeferredBlock<Block> SAPPHIRE_ORE = copyBlockRegister("sapphire_ore", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> DEEPSLATE_SAPPHIRE_ORE = copyBlockRegister("deepslate_sapphire_ore", Blocks.DEEPSLATE_DIAMOND_ORE);
    public static final PortDeferredBlock<Block> SANCTIFICATION_SAPPHIRE_ORE = copyBlockRegister("sanctification_sapphire_ore", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_SAPPHIRE_ORE = copyBlockRegister("corruption_sapphire_ore", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_SAPPHIRE_ORE = copyBlockRegister("fleshification_sapphire_ore", Blocks.IRON_ORE);

    public static final PortDeferredBlock<Block> AMETHYST_ORE = copyBlockRegister("amethyst_ore", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> DEEPSLATE_AMETHYST_ORE = copyBlockRegister("deepslate_amethyst_ore", Blocks.DEEPSLATE_DIAMOND_ORE);
    public static final PortDeferredBlock<Block> SANCTIFICATION_AMETHYST_ORE = copyBlockRegister("sanctification_amethyst_ore", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_AMETHYST_ORE = copyBlockRegister("corruption_amethyst_ore", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_AMETHYST_ORE = copyBlockRegister("fleshification_amethyst_ore", Blocks.IRON_ORE);

    public static final PortDeferredBlock<Block> SANCTIFICATION_LAPIS_ORE = copyBlockRegister("sanctification_lapis_ore", Blocks.LAPIS_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_LAPIS_ORE = copyBlockRegister("corruption_lapis_ore", Blocks.LAPIS_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_LAPIS_ORE = copyBlockRegister("fleshification_lapis_ore", Blocks.LAPIS_ORE);

    public static final PortDeferredBlock<Block> DEMONITE_ORE = copyBlockRegister("demonite_ore", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> DEEPSLATE_DEMONITE_ORE = copyBlockRegister("deepslate_demonite_ore", Blocks.DEEPSLATE_IRON_ORE);
    public static final PortDeferredBlock<Block> SANCTIFICATION_DEMONITE_ORE = copyBlockRegister("sanctification_demonite_ore", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_DEMONITE_ORE = copyBlockRegister("corruption_demonite_ore", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_DEMONITE_ORE = copyBlockRegister("fleshification_demonite_ore", Blocks.IRON_ORE);

    public static final PortDeferredBlock<Block> DEMONITE_BLOCK = copyBlockRegister("demonite_block", Blocks.RAW_IRON_BLOCK);
    public static final PortDeferredBlock<Block> RAW_DEMONITE_BLOCK = copyBlockRegister("raw_demonite_block", Blocks.IRON_BLOCK);

    public static final PortDeferredBlock<Block> CRIMTANE_ORE = copyBlockRegister("crimtane_ore", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> DEEPSLATE_CRIMTANE_ORE = copyBlockRegister("deepslate_crimtane_ore", Blocks.DEEPSLATE_IRON_ORE);
    public static final PortDeferredBlock<Block> SANCTIFICATION_CRIMTANE_ORE = copyBlockRegister("sanctification_crimtane_ore", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_CRIMTANE_ORE = copyBlockRegister("corruption_crimtane_ore", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_CRIMTANE_ORE = copyBlockRegister("fleshification_crimtane_ore", Blocks.IRON_ORE);

    public static final PortDeferredBlock<Block> RAW_CRIMTANE_BLOCK = copyBlockRegister("raw_crimtane_block", Blocks.IRON_BLOCK);
    public static final PortDeferredBlock<Block> CRIMTANE_BLOCK = copyBlockRegister("crimtane_block", Blocks.RAW_IRON_BLOCK);

    public static final PortDeferredBlock<Block> HALLOWED_BLOCK = copyBlockRegister("hallowed_block", Blocks.IRON_BLOCK);

    public static final PortDeferredBlock<ChlorophyteOreBlock> CHLOROPHYTE_ORE = registerWithItem("chlorophyte_ore", ChlorophyteOreBlock::new);
    public static final PortDeferredBlock<Block> RAW_CHLOROPHYTE_BLOCK = copyBlockRegister("raw_chlorophyte_block", Blocks.IRON_BLOCK);
    public static final PortDeferredBlock<Block> CHLOROPHYTE_BLOCK = copyBlockRegister("chlorophyte_block", Blocks.RAW_IRON_BLOCK);

    public static final PortDeferredBlock<Block> SHROOMITE_BLOCK = copyBlockRegister("shroomite_block", Blocks.IRON_BLOCK);

    public static final PortDeferredBlock<Block> SPECTRE_BLOCK = copyBlockRegister("spectre_block", Blocks.IRON_BLOCK);

    public static final PortDeferredBlock<Block> RAW_LUMINITE_BLOCK = copyBlockRegister("raw_luminite_block", Blocks.RAW_IRON_BLOCK);
    public static final PortDeferredBlock<Block> LUMINITE_BLOCK = copyBlockRegister("luminite_block", Blocks.IRON_BLOCK);

    public static final PortDeferredBlock<OpalOreBlock> OPAL_ORE = simpleBlockRegister("opal_ore", OpalOreBlock::new);
    public static final PortDeferredBlock<Block> GELSTONE_ORE = copyBlockRegister("gelstone_ore", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> SPORE_ROOT_BLOCK = copyBlockRegister("spore_root_block", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> WINTER_MARROW_BLOCK = copyBlockRegister("winter_marrow_block", Blocks.IRON_ORE);
    public static final PortDeferredBlock<PortTransparentBlock> COLD_CRYSTAL_ORE = registerWithItem("cold_crystal_ore", () -> new PortTransparentBlock(BlockBehaviour.Properties.copy(Blocks.IRON_ORE).noOcclusion().sound(SoundType.GLASS)));
    public static final PortDeferredBlock<HellStoneBlock> HELLSTONE = registerWithItem("hellstone", () -> new HellStoneBlock(true), block -> new BlockItem(block, new Item.Properties().fireResistant()));
    public static final PortDeferredBlock<HellStoneBlock> ASH_HELLSTONE = registerWithItem("ash_hellstone", () -> new HellStoneBlock(true), block -> new BlockItem(block, new Item.Properties().fireResistant()));
    public static final PortDeferredBlock<HellStoneBlock> RAW_HELLSTONE_BLOCK = registerWithItem("raw_hellstone_block", () -> new HellStoneBlock(false), block -> new BlockItem(block, new Item.Properties().fireResistant()));
    public static final PortDeferredBlock<HellStoneBlock> HELLSTONE_BLOCK = registerWithItem("hellstone_block", () -> new HellStoneBlock(false), block -> new BlockItem(block, new Item.Properties().fireResistant()));

    // 红石矿
    public static final PortDeferredBlock<RedStoneOreBlock> SANCTIFICATION_REDSTONE_ORE = simpleBlockRegister("sanctification_redstone_ore", () -> new RedStoneOreBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_REDSTONE_ORE)));
    public static final PortDeferredBlock<RedStoneOreBlock> CORRUPTION_REDSTONE_ORE = simpleBlockRegister("corruption_redstone_ore", () -> new RedStoneOreBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_REDSTONE_ORE)));
    public static final PortDeferredBlock<RedStoneOreBlock> FLESHIFICATION_REDSTONE_ORE = simpleBlockRegister("fleshification_redstone_ore", () -> new RedStoneOreBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_REDSTONE_ORE)));

    public static final PortDeferredBlock<Block> LUNARTEAR_ORE = registerWithItem("lunartear_ore", () -> new Block(
            BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_CYAN)
            .requiresCorrectToolForDrops()
            .strength(30.0F, ModBlocks.getObsidianBasedExplosionResistance(100))
            .sound(SoundType.STONE)));
    public static final PortDeferredBlock<Block> DRAGONSAL_ORE = registerWithItem("dragonsal_ore", () -> new Block(
            BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_PURPLE)
            .requiresCorrectToolForDrops()
            .strength(40.0F, ModBlocks.getObsidianBasedExplosionResistance(100))
            .sound(SoundType.STONE)));

    public static final PortDeferredBlock<StepRevealingBlock> DEEPSLATE_COBALT_ORE = simpleBlockRegister("deepslate_cobalt_ore", () -> new StepRevealingBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE).strength(15.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final PortDeferredBlock<Block> RAW_COBALT_BLOCK = simpleBlockRegister("raw_cobalt_block");
    public static final PortDeferredBlock<Block> COBALT_BLOCK = simpleBlockRegister("cobalt_block");

    public static final PortDeferredBlock<StepRevealingBlock> DEEPSLATE_PALLADIUM_ORE = simpleBlockRegister("deepslate_palladium_ore", () -> new StepRevealingBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE).strength(15.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final PortDeferredBlock<Block> RAW_PALLADIUM_BLOCK = simpleBlockRegister("raw_palladium_block");
    public static final PortDeferredBlock<Block> PALLADIUM_BLOCK = simpleBlockRegister("palladium_block");

    public static final PortDeferredBlock<StepRevealingBlock> DEEPSLATE_MYTHRIL_ORE = simpleBlockRegister("deepslate_mythril_ore", () -> new StepRevealingBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE).strength(20.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final PortDeferredBlock<Block> RAW_MYTHRIL_BLOCK = simpleBlockRegister("raw_mythril_block");
    public static final PortDeferredBlock<Block> MYTHRIL_BLOCK = simpleBlockRegister("mythril_block");

    public static final PortDeferredBlock<StepRevealingBlock> DEEPSLATE_ORICHALCUM_ORE = simpleBlockRegister("deepslate_orichalcum_ore", () -> new StepRevealingBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE).strength(20.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final PortDeferredBlock<Block> RAW_ORICHALCUM_BLOCK = simpleBlockRegister("raw_orichalcum_block");
    public static final PortDeferredBlock<Block> ORICHALCUM_BLOCK = simpleBlockRegister("orichalcum_block");

    public static final PortDeferredBlock<StepRevealingBlock> DEEPSLATE_ADAMANTITE_ORE = simpleBlockRegister("deepslate_adamantite_ore", () -> new StepRevealingBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE).strength(25.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final PortDeferredBlock<Block> RAW_ADAMANTITE_BLOCK = simpleBlockRegister("raw_adamantite_block");
    public static final PortDeferredBlock<Block> ADAMANTITE_BLOCK = simpleBlockRegister("adamantite_block");

    public static final PortDeferredBlock<StepRevealingBlock> DEEPSLATE_TITANIUM_ORE = simpleBlockRegister("deepslate_titanium_ore", () -> new StepRevealingBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE).strength(25.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final PortDeferredBlock<Block> RAW_TITANIUM_BLOCK = simpleBlockRegister("raw_titanium_block");
    public static final PortDeferredBlock<Block> TITANIUM_BLOCK = simpleBlockRegister("titanium_block");

    private static PortDeferredBlock<Block> simpleBlockRegister(String name) {
        PortDeferredBlock<Block> block = BLOCKS.register(name, () -> new Block(BlockBehaviour.Properties.of()));
        ModItems.BLOCK_ITEMS.registerSimpleBlockItem(block);
        return block;
    }

    private static <B extends Block> PortDeferredBlock<B> simpleBlockRegister(String name, Supplier<B> blockSupplier) {
        PortDeferredBlock<B> block = BLOCKS.register(name, blockSupplier);
        ModItems.BLOCK_ITEMS.registerSimpleBlockItem(block);
        return block;
    }

    private static PortDeferredBlock<Block> simpleBlockRegister(String name, BlockBehaviour.Properties props) {
        PortDeferredBlock<Block> block = BLOCKS.register(name, () -> new Block(props));
        ModItems.BLOCK_ITEMS.registerSimpleBlockItem(block);
        return block;
    }

    private static <B extends Block> PortDeferredBlock<B> registerWithItem(String name, Supplier<B> blockSupplier, Function<B, BlockItem> function) {
        PortDeferredBlock<B> block = BLOCKS.register(name, blockSupplier);
        ModItems.BLOCK_ITEMS.register(name, () -> function.apply(block.get()));
        return block;
    }

    private static <B extends Block> PortDeferredBlock<B> registerWithItem(String name, Supplier<B> blockSupplier) {
        PortDeferredBlock<B> block = BLOCKS.register(name, blockSupplier);
        ModItems.BLOCK_ITEMS.registerSimpleBlockItem(block);
        return block;
    }

    private static PortDeferredBlock<Block> copyBlockRegister(String name, Block originalBlock) {
        PortDeferredBlock<Block> block = BLOCKS.register(name, () -> new Block(BlockBehaviour.Properties.copy(originalBlock)));
        ModItems.BLOCK_ITEMS.registerSimpleBlockItem(block);
        return block;
    }

    public static void acceptTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block> tag) {
        BLOCKS.getEntries().forEach(block -> tag.add(block.get()));
    }
}
