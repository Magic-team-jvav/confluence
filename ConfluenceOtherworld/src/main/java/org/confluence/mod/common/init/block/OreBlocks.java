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

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

public class OreBlocks {
    public static void init() {}

    public static final PortBlockRegistration BLOCKS = PortRegisterHandler.block(Confluence.MODID);

    public static final PortDeferredBlock<Block> SANCTIFICATION_COAL_ORE = copyBlockRegister("sanctification_coal_ore", "圣化煤矿石", Blocks.COAL_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_COAL_ORE = copyBlockRegister("corruption_coal_ore", "腐化煤矿石", Blocks.COAL_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_COAL_ORE = copyBlockRegister("fleshification_coal_ore", "血化煤矿石", Blocks.COAL_ORE);

    public static final PortDeferredBlock<Block> SANCTIFICATION_COPPER_ORE = copyBlockRegister("sanctification_copper_ore", "圣化铜矿石", Blocks.COPPER_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_COPPER_ORE = copyBlockRegister("corruption_copper_ore", "腐化铜矿石", Blocks.COPPER_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_COPPER_ORE = copyBlockRegister("fleshification_copper_ore", "血化铜矿石", Blocks.COPPER_ORE);

    public static final PortDeferredBlock<Block> TIN_ORE = copyBlockRegister("tin_ore", "锡矿石", Blocks.COPPER_ORE);
    public static final PortDeferredBlock<Block> DEEPSLATE_TIN_ORE = copyBlockRegister("deepslate_tin_ore", "深层锡矿石", Blocks.DEEPSLATE_COPPER_ORE);
    public static final PortDeferredBlock<Block> SANCTIFICATION_TIN_ORE = copyBlockRegister("sanctification_tin_ore", "圣化锡矿石", Blocks.COPPER_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_TIN_ORE = copyBlockRegister("corruption_tin_ore", "腐化锡矿石", Blocks.COPPER_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_TIN_ORE = copyBlockRegister("fleshification_tin_ore", "血化锡矿石", Blocks.COPPER_ORE);
    public static final PortDeferredBlock<Block> RAW_TIN_BLOCK = copyBlockRegister("raw_tin_block", "粗锡块", Blocks.RAW_COPPER_BLOCK);
    public static final PortDeferredBlock<Block> TIN_BLOCK = copyBlockRegister("tin_block", "锡块", Blocks.COPPER_BLOCK);

    public static final PortDeferredBlock<Block> SANCTIFICATION_IRON_ORE = copyBlockRegister("sanctification_iron_ore", "圣化铁矿石", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_IRON_ORE = copyBlockRegister("corruption_iron_ore", "腐化铁矿石", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_IRON_ORE = copyBlockRegister("fleshification_iron_ore", "血化铁矿石", Blocks.IRON_ORE);

    public static final PortDeferredBlock<Block> LEAD_ORE = copyBlockRegister("lead_ore", "铅矿石", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> DEEPSLATE_LEAD_ORE = copyBlockRegister("deepslate_lead_ore", "深层铅矿石", Blocks.DEEPSLATE_IRON_ORE);
    public static final PortDeferredBlock<Block> SANCTIFICATION_LEAD_ORE = copyBlockRegister("sanctification_lead_ore", "圣化铅矿石", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_LEAD_ORE = copyBlockRegister("corruption_lead_ore", "腐化铅矿石", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_LEAD_ORE = copyBlockRegister("fleshification_lead_ore", "血化铅矿石", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> RAW_LEAD_BLOCK = copyBlockRegister("raw_lead_block", "粗铅块", Blocks.RAW_IRON_BLOCK);
    public static final PortDeferredBlock<Block> LEAD_BLOCK = copyBlockRegister("lead_block", "铅块", Blocks.IRON_BLOCK);

    public static final PortDeferredBlock<Block> SILVER_ORE = copyBlockRegister("silver_ore", "银矿石", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> DEEPSLATE_SILVER_ORE = copyBlockRegister("deepslate_silver_ore", "深层银矿石", Blocks.DEEPSLATE_IRON_ORE);
    public static final PortDeferredBlock<Block> SANCTIFICATION_SILVER_ORE = copyBlockRegister("sanctification_silver_ore", "圣化银矿石", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_SILVER_ORE = copyBlockRegister("corruption_silver_ore", "腐化银矿石", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_SILVER_ORE = copyBlockRegister("fleshification_silver_ore", "血化银矿石", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> RAW_SILVER_BLOCK = copyBlockRegister("raw_silver_block", "粗银块", Blocks.RAW_IRON_BLOCK);
    public static final PortDeferredBlock<Block> SILVER_BLOCK = copyBlockRegister("silver_block", "银块", Blocks.IRON_BLOCK);

    public static final PortDeferredBlock<Block> TUNGSTEN_ORE = copyBlockRegister("tungsten_ore", "钨矿石", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> DEEPSLATE_TUNGSTEN_ORE = copyBlockRegister("deepslate_tungsten_ore", "深层钨矿石", Blocks.DEEPSLATE_IRON_ORE);
    public static final PortDeferredBlock<Block> SANCTIFICATION_TUNGSTEN_ORE = copyBlockRegister("sanctification_tungsten_ore", "圣化钨矿石", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_TUNGSTEN_ORE = copyBlockRegister("corruption_tungsten_ore", "腐化钨矿石", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_TUNGSTEN_ORE = copyBlockRegister("fleshification_tungsten_ore", "血化钨矿石", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> RAW_TUNGSTEN_BLOCK = copyBlockRegister("raw_tungsten_block", "粗钨块", Blocks.RAW_IRON_BLOCK);
    public static final PortDeferredBlock<Block> TUNGSTEN_BLOCK = copyBlockRegister("tungsten_block", "钨块", Blocks.IRON_BLOCK);

    public static final PortDeferredBlock<Block> PLATINUM_ORE = copyBlockRegister("platinum_ore", "铂金矿石", Blocks.GOLD_ORE);
    public static final PortDeferredBlock<Block> DEEPSLATE_PLATINUM_ORE = copyBlockRegister("deepslate_platinum_ore", "深层铂金矿石", Blocks.DEEPSLATE_GOLD_ORE);
    public static final PortDeferredBlock<Block> SANCTIFICATION_PLATINUM_ORE = copyBlockRegister("sanctification_platinum_ore", "圣化铂金矿石", Blocks.GOLD_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_PLATINUM_ORE = copyBlockRegister("corruption_platinum_ore", "腐化铂金矿石", Blocks.GOLD_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_PLATINUM_ORE = copyBlockRegister("fleshification_platinum_ore", "血化铂金矿石", Blocks.GOLD_ORE);
    public static final PortDeferredBlock<Block> RAW_PLATINUM_BLOCK = copyBlockRegister("raw_platinum_block", "粗铂金块", Blocks.RAW_GOLD_BLOCK);
    public static final PortDeferredBlock<Block> PLATINUM_BLOCK = copyBlockRegister("platinum_block", "铂金块", Blocks.GOLD_BLOCK);

    public static final PortDeferredBlock<Block> SANCTIFICATION_GOLD_ORE = copyBlockRegister("sanctification_gold_ore", "圣化金矿石", Blocks.GOLD_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_GOLD_ORE = copyBlockRegister("corruption_gold_ore", "腐化金矿石", Blocks.GOLD_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_GOLD_ORE = copyBlockRegister("fleshification_gold_ore", "血化金矿石", Blocks.GOLD_ORE);

    public static final PortDeferredBlock<MeteoriteOre> METEORITE_ORE = registerWithItem("meteorite_ore", "陨石矿石", MeteoriteOre::new, block -> new BlockItem(block, new Item.Properties().fireResistant()));
    public static final PortDeferredBlock<MeteoriteOre> RAW_METEORITE_BLOCK = registerWithItem("raw_meteorite_block", "粗陨石块", MeteoriteOre::new, block -> new BlockItem(block, new Item.Properties().fireResistant()));
    public static final PortDeferredBlock<MeteoriteOre> METEORITE_BLOCK = registerWithItem("meteorite_block", "陨石块", MeteoriteOre::new, block -> new BlockItem(block, new Item.Properties().fireResistant()));

    public static final PortDeferredBlock<Block> STURDY_FOSSIL_BLOCK = copyBlockRegister("sturdy_fossil_block", "坚固化石块", Blocks.DIAMOND_BLOCK);
    public static final PortDeferredBlock<Block> OPAL_BLOCK = copyBlockRegister("opal_block", "欧泊块", Blocks.DIAMOND_BLOCK);
    public static final PortDeferredBlock<Block> GELSTONE_BLOCK = copyBlockRegister("gelstone_block", "黏晶块", Blocks.DIAMOND_BLOCK);
    public static final PortDeferredBlock<Block> COLD_CRYSTAL_BLOCK = copyBlockRegister("cold_crystal_block", "寒晶块", Blocks.DIAMOND_BLOCK);

    public static final PortDeferredBlock<Block> SANCTIFICATION_EMERALD_ORE = copyBlockRegister("sanctification_emerald_ore", "圣化绿宝石矿石", Blocks.EMERALD_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_EMERALD_ORE = copyBlockRegister("corruption_emerald_ore", "腐化绿宝石矿石", Blocks.EMERALD_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_EMERALD_ORE = copyBlockRegister("fleshification_emerald_ore", "血化绿宝石矿石", Blocks.EMERALD_ORE);

    public static final PortDeferredBlock<Block> SANCTIFICATION_DIAMOND_ORE = copyBlockRegister("sanctification_diamond_ore", "圣化钻石矿石", Blocks.DIAMOND_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_DIAMOND_ORE = copyBlockRegister("corruption_diamond_ore", "腐化钻石矿石", Blocks.DIAMOND_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_DIAMOND_ORE = copyBlockRegister("fleshification_diamond_ore", "血化钻石矿石", Blocks.DIAMOND_ORE);

    public static final PortDeferredBlock<Block> RUBY_ORE = copyBlockRegister("ruby_ore", "红玉矿石", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> DEEPSLATE_RUBY_ORE = copyBlockRegister("deepslate_ruby_ore", "深层红玉矿石", Blocks.DEEPSLATE_IRON_ORE);
    public static final PortDeferredBlock<Block> SANCTIFICATION_RUBY_ORE = copyBlockRegister("sanctification_ruby_ore", "圣化红玉矿石", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_RUBY_ORE = copyBlockRegister("corruption_ruby_ore", "腐化红玉矿石", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_RUBY_ORE = copyBlockRegister("fleshification_ruby_ore", "血化红玉矿石", Blocks.IRON_ORE);

    public static final PortDeferredBlock<Block> AMBER_ORE = copyBlockRegister("amber_ore", "琥珀矿石", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> RED_SAND_AMBER_ORE = copyBlockRegister("red_sand_amber_ore", "红沙琥珀矿石", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> SANCTIFICATION_AMBER_ORE = copyBlockRegister("sanctification_amber_ore", "圣化琥珀矿石", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_AMBER_ORE = copyBlockRegister("corruption_amber_ore", "腐化琥珀矿石", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_AMBER_ORE = copyBlockRegister("fleshification_amber_ore", "血化琥珀矿石", Blocks.IRON_ORE);

    public static final PortDeferredBlock<Block> TOPAZ_ORE = copyBlockRegister("topaz_ore", "黄玉矿石", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> DEEPSLATE_TOPAZ_ORE = copyBlockRegister("deepslate_topaz_ore", "深层黄玉矿石", Blocks.DEEPSLATE_DIAMOND_ORE);
    public static final PortDeferredBlock<Block> SANCTIFICATION_TOPAZ_ORE = copyBlockRegister("sanctification_topaz_ore", "圣化黄玉矿石", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_TOPAZ_ORE = copyBlockRegister("corruption_topaz_ore", "腐化黄玉矿石", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_TOPAZ_ORE = copyBlockRegister("fleshification_topaz_ore", "血化黄玉矿石", Blocks.IRON_ORE);

    public static final PortDeferredBlock<Block> JADE_ORE = copyBlockRegister("jade_ore", "翡翠矿石", Blocks.EMERALD_ORE);
    public static final PortDeferredBlock<Block> DEEPSLATE_JADE_ORE = copyBlockRegister("deepslate_jade_ore", "深层翡翠矿石", Blocks.DEEPSLATE_DIAMOND_ORE);
    public static final PortDeferredBlock<Block> SANCTIFICATION_JADE_ORE = copyBlockRegister("sanctification_jade_ore", "圣化翡翠矿石", Blocks.EMERALD_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_JADE_ORE = copyBlockRegister("corruption_jade_ore", "腐化翡翠矿石", Blocks.EMERALD_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_JADE_ORE = copyBlockRegister("fleshification_jade_ore", "血化翡翠矿石", Blocks.EMERALD_ORE);

    public static final PortDeferredBlock<Block> SAPPHIRE_ORE = copyBlockRegister("sapphire_ore", "蓝玉矿石", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> DEEPSLATE_SAPPHIRE_ORE = copyBlockRegister("deepslate_sapphire_ore", "深层蓝玉矿石", Blocks.DEEPSLATE_DIAMOND_ORE);
    public static final PortDeferredBlock<Block> SANCTIFICATION_SAPPHIRE_ORE = copyBlockRegister("sanctification_sapphire_ore", "圣化蓝玉矿石", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_SAPPHIRE_ORE = copyBlockRegister("corruption_sapphire_ore", "腐化蓝玉矿石", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_SAPPHIRE_ORE = copyBlockRegister("fleshification_sapphire_ore", "血化蓝玉矿石", Blocks.IRON_ORE);

    public static final PortDeferredBlock<Block> AMETHYST_ORE = copyBlockRegister("amethyst_ore", "紫晶矿石", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> DEEPSLATE_AMETHYST_ORE = copyBlockRegister("deepslate_amethyst_ore", "深层紫晶矿石", Blocks.DEEPSLATE_DIAMOND_ORE);
    public static final PortDeferredBlock<Block> SANCTIFICATION_AMETHYST_ORE = copyBlockRegister("sanctification_amethyst_ore", "圣化紫晶矿石", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_AMETHYST_ORE = copyBlockRegister("corruption_amethyst_ore", "腐化紫晶矿石", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_AMETHYST_ORE = copyBlockRegister("fleshification_amethyst_ore", "血化紫晶矿石", Blocks.IRON_ORE);

    public static final PortDeferredBlock<Block> SANCTIFICATION_LAPIS_ORE = copyBlockRegister("sanctification_lapis_ore", "圣化青金石矿石", Blocks.LAPIS_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_LAPIS_ORE = copyBlockRegister("corruption_lapis_ore", "腐化青金石矿石", Blocks.LAPIS_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_LAPIS_ORE = copyBlockRegister("fleshification_lapis_ore", "血化青金石矿石", Blocks.LAPIS_ORE);

    public static final PortDeferredBlock<Block> DEMONITE_ORE = copyBlockRegister("demonite_ore", "魔矿石", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> DEEPSLATE_DEMONITE_ORE = copyBlockRegister("deepslate_demonite_ore", "深层魔矿石", Blocks.DEEPSLATE_IRON_ORE);
    public static final PortDeferredBlock<Block> SANCTIFICATION_DEMONITE_ORE = copyBlockRegister("sanctification_demonite_ore", "圣化魔矿石", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_DEMONITE_ORE = copyBlockRegister("corruption_demonite_ore", "腐化魔矿石", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_DEMONITE_ORE = copyBlockRegister("fleshification_demonite_ore", "血化魔矿石", Blocks.IRON_ORE);

    public static final PortDeferredBlock<Block> DEMONITE_BLOCK = copyBlockRegister("demonite_block", "魔矿块", Blocks.RAW_IRON_BLOCK);
    public static final PortDeferredBlock<Block> RAW_DEMONITE_BLOCK = copyBlockRegister("raw_demonite_block", "粗魔矿块", Blocks.IRON_BLOCK);

    public static final PortDeferredBlock<Block> CRIMTANE_ORE = copyBlockRegister("crimtane_ore", "猩红矿石", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> DEEPSLATE_CRIMTANE_ORE = copyBlockRegister("deepslate_crimtane_ore", "深层猩红矿石", Blocks.DEEPSLATE_IRON_ORE);
    public static final PortDeferredBlock<Block> SANCTIFICATION_CRIMTANE_ORE = copyBlockRegister("sanctification_crimtane_ore", "圣化猩红矿石", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> CORRUPTION_CRIMTANE_ORE = copyBlockRegister("corruption_crimtane_ore", "腐化猩红矿石", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> FLESHIFICATION_CRIMTANE_ORE = copyBlockRegister("fleshification_crimtane_ore", "血化猩红矿石", Blocks.IRON_ORE);

    public static final PortDeferredBlock<Block> RAW_CRIMTANE_BLOCK = copyBlockRegister("raw_crimtane_block", "粗猩红矿块", Blocks.IRON_BLOCK);
    public static final PortDeferredBlock<Block> CRIMTANE_BLOCK = copyBlockRegister("crimtane_block", "猩红矿块", Blocks.RAW_IRON_BLOCK);

    public static final PortDeferredBlock<Block> HALLOWED_BLOCK = copyBlockRegister("hallowed_block", "神圣块", Blocks.IRON_BLOCK);

    public static final PortDeferredBlock<ChlorophyteOreBlock> CHLOROPHYTE_ORE = registerWithItem("chlorophyte_ore", "叶绿矿石", ChlorophyteOreBlock::new);
    public static final PortDeferredBlock<Block> RAW_CHLOROPHYTE_BLOCK = copyBlockRegister("raw_chlorophyte_block", "叶绿粗矿块", Blocks.IRON_BLOCK);
    public static final PortDeferredBlock<Block> CHLOROPHYTE_BLOCK = copyBlockRegister("chlorophyte_block", "叶绿块", Blocks.RAW_IRON_BLOCK);

    public static final PortDeferredBlock<Block> SHROOMITE_BLOCK = copyBlockRegister("shroomite_block", "蘑菇矿块", Blocks.IRON_BLOCK);

    public static final PortDeferredBlock<Block> SPECTRE_BLOCK = copyBlockRegister("spectre_block", "幽灵块", Blocks.IRON_BLOCK);

    public static final PortDeferredBlock<Block> RAW_LUMINITE_BLOCK = copyBlockRegister("raw_luminite_block", "粗夜明块", Blocks.RAW_IRON_BLOCK);
    public static final PortDeferredBlock<Block> LUMINITE_BLOCK = copyBlockRegister("luminite_block", "夜明块", Blocks.IRON_BLOCK);

    public static final PortDeferredBlock<OpalOreBlock> OPAL_ORE = simpleBlockRegister("opal_ore", "欧泊矿石", OpalOreBlock::new);
    public static final PortDeferredBlock<Block> GELSTONE_ORE = copyBlockRegister("gelstone_ore", "黏晶矿石", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> SPORE_ROOT_BLOCK = copyBlockRegister("spore_root_block", "孢根", Blocks.IRON_ORE);
    public static final PortDeferredBlock<Block> WINTER_MARROW_BLOCK = copyBlockRegister("winter_marrow_block", "霜髓", Blocks.IRON_ORE);
    public static final PortDeferredBlock<PortTransparentBlock> COLD_CRYSTAL_ORE = registerWithItem("cold_crystal_ore", "寒晶矿石", () -> new PortTransparentBlock(BlockBehaviour.Properties.copy(Blocks.IRON_ORE).noOcclusion().sound(SoundType.GLASS)));
    public static final PortDeferredBlock<HellStoneBlock> HELLSTONE = registerWithItem("hellstone", "下界狱石", () -> new HellStoneBlock(true), block -> new BlockItem(block, new Item.Properties().fireResistant()));
    public static final PortDeferredBlock<HellStoneBlock> ASH_HELLSTONE = registerWithItem("ash_hellstone", "灰烬狱石", () -> new HellStoneBlock(true), block -> new BlockItem(block, new Item.Properties().fireResistant()));
    public static final PortDeferredBlock<HellStoneBlock> RAW_HELLSTONE_BLOCK = registerWithItem("raw_hellstone_block", "粗狱石矿块", () -> new HellStoneBlock(false), block -> new BlockItem(block, new Item.Properties().fireResistant()));
    public static final PortDeferredBlock<HellStoneBlock> HELLSTONE_BLOCK = registerWithItem("hellstone_block", "狱石块", () -> new HellStoneBlock(false), block -> new BlockItem(block, new Item.Properties().fireResistant()));

    // 红石矿
    public static final PortDeferredBlock<RedStoneOreBlock> SANCTIFICATION_REDSTONE_ORE = simpleBlockRegister("sanctification_redstone_ore", "圣化红石矿石", () -> new RedStoneOreBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_REDSTONE_ORE)));
    public static final PortDeferredBlock<RedStoneOreBlock> CORRUPTION_REDSTONE_ORE = simpleBlockRegister("corruption_redstone_ore", "腐化红石矿石", () -> new RedStoneOreBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_REDSTONE_ORE)));
    public static final PortDeferredBlock<RedStoneOreBlock> FLESHIFICATION_REDSTONE_ORE = simpleBlockRegister("fleshification_redstone_ore", "血化红石矿石", () -> new RedStoneOreBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_REDSTONE_ORE)));

    public static final PortDeferredBlock<Block> LUNARTEAR_ORE = registerWithItem("lunartear_ore", "月泣矿石", () -> new Block(
            BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_CYAN)
            .requiresCorrectToolForDrops()
            .strength(30.0F, ModBlocks.getObsidianBasedExplosionResistance(100))
            .sound(SoundType.STONE)));
    public static final PortDeferredBlock<Block> DRAGONSAL_ORE = registerWithItem("dragonsal_ore", "龙涎矿石", () -> new Block(
            BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_PURPLE)
            .requiresCorrectToolForDrops()
            .strength(40.0F, ModBlocks.getObsidianBasedExplosionResistance(100))
            .sound(SoundType.STONE)));

    public static final PortDeferredBlock<StepRevealingBlock> DEEPSLATE_COBALT_ORE = simpleBlockRegister("deepslate_cobalt_ore", "深层钴矿石", () -> new StepRevealingBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE).strength(15.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final PortDeferredBlock<Block> RAW_COBALT_BLOCK = simpleBlockRegister("raw_cobalt_block", "粗钴块");
    public static final PortDeferredBlock<Block> COBALT_BLOCK = simpleBlockRegister("cobalt_block", "钴块");

    public static final PortDeferredBlock<StepRevealingBlock> DEEPSLATE_PALLADIUM_ORE = simpleBlockRegister("deepslate_palladium_ore", "深层钯金矿石", () -> new StepRevealingBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE).strength(15.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final PortDeferredBlock<Block> RAW_PALLADIUM_BLOCK = simpleBlockRegister("raw_palladium_block", "粗钯金块");
    public static final PortDeferredBlock<Block> PALLADIUM_BLOCK = simpleBlockRegister("palladium_block", "钯金块");

    public static final PortDeferredBlock<StepRevealingBlock> DEEPSLATE_MYTHRIL_ORE = simpleBlockRegister("deepslate_mythril_ore", "深层秘银矿石", () -> new StepRevealingBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE).strength(20.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final PortDeferredBlock<Block> RAW_MYTHRIL_BLOCK = simpleBlockRegister("raw_mythril_block", "粗秘银块");
    public static final PortDeferredBlock<Block> MYTHRIL_BLOCK = simpleBlockRegister("mythril_block", "秘银块");

    public static final PortDeferredBlock<StepRevealingBlock> DEEPSLATE_ORICHALCUM_ORE = simpleBlockRegister("deepslate_orichalcum_ore", "深层山铜矿石", () -> new StepRevealingBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE).strength(20.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final PortDeferredBlock<Block> RAW_ORICHALCUM_BLOCK = simpleBlockRegister("raw_orichalcum_block", "粗山铜块");
    public static final PortDeferredBlock<Block> ORICHALCUM_BLOCK = simpleBlockRegister("orichalcum_block", "山铜块");

    public static final PortDeferredBlock<StepRevealingBlock> DEEPSLATE_ADAMANTITE_ORE = simpleBlockRegister("deepslate_adamantite_ore", "深层精金矿石", () -> new StepRevealingBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE).strength(25.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final PortDeferredBlock<Block> RAW_ADAMANTITE_BLOCK = simpleBlockRegister("raw_adamantite_block", "粗精金块");
    public static final PortDeferredBlock<Block> ADAMANTITE_BLOCK = simpleBlockRegister("adamantite_block", "精金块");

    public static final PortDeferredBlock<StepRevealingBlock> DEEPSLATE_TITANIUM_ORE = simpleBlockRegister("deepslate_titanium_ore", "深层钛金矿石", () -> new StepRevealingBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE).strength(25.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final PortDeferredBlock<Block> RAW_TITANIUM_BLOCK = simpleBlockRegister("raw_titanium_block", "粗钛金块");
    public static final PortDeferredBlock<Block> TITANIUM_BLOCK = simpleBlockRegister("titanium_block", "钛金块");

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

    private static PortDeferredBlock<Block> simpleBlockRegister(String registryId, String chineseName) {
        PortDeferredBlock<Block> entry = simpleBlockRegister(registryId);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <B extends Block> PortDeferredBlock<B> simpleBlockRegister(String registryId, String chineseName, Supplier<B> blockSupplier) {
        PortDeferredBlock<B> entry = simpleBlockRegister(registryId, blockSupplier);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static PortDeferredBlock<Block> simpleBlockRegister(String registryId, String chineseName, BlockBehaviour.Properties props) {
        PortDeferredBlock<Block> entry = simpleBlockRegister(registryId, props);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <B extends Block> PortDeferredBlock<B> registerWithItem(String registryId, String chineseName, Supplier<B> blockSupplier, Function<B, BlockItem> function) {
        PortDeferredBlock<B> entry = registerWithItem(registryId, blockSupplier, function);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <B extends Block> PortDeferredBlock<B> registerWithItem(String registryId, String chineseName, Supplier<B> blockSupplier) {
        PortDeferredBlock<B> entry = registerWithItem(registryId, blockSupplier);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static PortDeferredBlock<Block> copyBlockRegister(String registryId, String chineseName, Block originalBlock) {
        PortDeferredBlock<Block> entry = copyBlockRegister(registryId, originalBlock);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }
}
