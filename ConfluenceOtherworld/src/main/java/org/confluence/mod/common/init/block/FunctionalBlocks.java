package org.confluence.mod.common.init.block;

import com.mojang.datafixers.DSL;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipBlockItem;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.common.EnchantedFragileBricksBlock;
import org.confluence.mod.common.block.functional.*;
import org.confluence.mod.common.block.functional.crafting.*;
import org.confluence.mod.common.block.functional.network.INetworkBlock;
import org.confluence.mod.common.block.natural.MagicMailBox;
import org.confluence.mod.common.block.natural.TreeHolesBlock;
import org.confluence.mod.common.entity.projectile.boulder.ExplodeBoulderEntity;
import org.confluence.mod.common.entity.projectile.boulder.FollowerBoulderEntity;
import org.confluence.mod.common.init.item.MaterialItems;
import org.confluence.mod.common.init.item.ModItems;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.confluence.mod.common.init.block.ModBlocks.BLOCK_ENTITIES;

public class FunctionalBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Confluence.MODID);
    static List<DeferredBlock<? extends Block>> MECHANICAL_BLOCKS = new ArrayList<>();

    public static final DeferredBlock<ExtractinatorBlock> EXTRACTINATOR = registerWithItem("extractinator", () -> new ExtractinatorBlock(BlockBehaviour.Properties.of().strength(2.2F, 5.0F).requiresCorrectToolForDrops()), ExtractinatorBlock.BItem::new);
    public static final Supplier<BlockEntityType<ExtractinatorBlock.BEntity>> EXTRACTINATOR_ENTITY = BLOCK_ENTITIES.register("extractinator_entity", () -> BlockEntityType.Builder.of(ExtractinatorBlock.BEntity::new, EXTRACTINATOR.get()).build(DSL.remainderType()));
    public static final DeferredBlock<AltarBlock> DEMON_ALTAR = registerWithItem("demon_altar", () -> new AltarBlock(BlockBehaviour.Properties.of().strength(3.0F, 18000.0F).lightLevel(state -> 5), AltarBlock.Variant.DEMON), AltarBlock.BItem::new);
    public static final DeferredBlock<AltarBlock> CRIMSON_ALTAR = registerWithItem("crimson_altar", () -> new AltarBlock(BlockBehaviour.Properties.of().strength(3.0F, 18000.0F).lightLevel(state -> 5), AltarBlock.Variant.CRIMSON), AltarBlock.BItem::new);
    public static final Supplier<BlockEntityType<AltarBlock.BEntity>> ALTAR_BLOCK_ENTITY = BLOCK_ENTITIES.register("altar_block_entity", () -> BlockEntityType.Builder.of(AltarBlock.BEntity::new, DEMON_ALTAR.get(), CRIMSON_ALTAR.get()).build(DSL.remainderType()));
    public static final DeferredBlock<SkyMillBlock> SKY_MILL = registerWithItem("sky_mill", () -> new SkyMillBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GRINDSTONE)), SkyMillBlock.BItem::new);
    public static final Supplier<BlockEntityType<SkyMillBlock.BEntity>> SKY_MILL_ENTITY = BLOCK_ENTITIES.register("sky_mill_entity", () -> BlockEntityType.Builder.of(SkyMillBlock.BEntity::new, SKY_MILL.get()).build(DSL.remainderType()));
    public static final DeferredBlock<HeavyWorkBenchBlock> HEAVY_WORK_BENCH = registerWithItem("heavy_work_bench", () -> new HeavyWorkBenchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_BRICKS)), block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.WHITE, "tooltip.item.confluence.heavy_work_bench.0"));
    public static final DeferredBlock<SawmillBlock> SAWMILL = registerWithItem("sawmill", () -> new SawmillBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD)), block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.WHITE, "tooltip.item.confluence.sawmill.0"));
    public static final DeferredBlock<HellforgeBlock> HELLFORGE = registerWithItem("hellforge", () -> new HellforgeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MAGMA_BLOCK).lightLevel(state -> state.getValue(BlockStateProperties.LIT) ? 15 : 7).noOcclusion()), block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.WHITE, "tooltip.item.confluence.hellforge.0"));
    public static final Supplier<BlockEntityType<HellforgeBlock.BEntity>> HELLFORGE_ENTITY = BLOCK_ENTITIES.register("hellforge_entity", () -> BlockEntityType.Builder.of(HellforgeBlock.BEntity::new, HELLFORGE.get()).build(DSL.remainderType()));
    public static final DeferredBlock<CookingPotBlock> COOKING_POT = registerWithItem("cooking_pot", () -> new CookingPotBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON)), block -> new CookingPotBlock.BItem(block, new Item.Properties()));
    public static final Supplier<BlockEntityType<CookingPotBlock.BEntity>> COOKING_POT_ENTITY = BLOCK_ENTITIES.register("cooking_pot_entity", () -> BlockEntityType.Builder.of(CookingPotBlock.BEntity::new, COOKING_POT.get()).build(DSL.remainderType()));
    public static final DeferredBlock<BaseCauldronBlock> CAULDRON = registerWithItem("cauldron", () -> new BaseCauldronBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON)));
    public static final Supplier<BlockEntityType<BaseCauldronBlock.BEntity>> CAULDRON_ENTITY = BLOCK_ENTITIES.register("cauldron_entity", () -> BlockEntityType.Builder.of(BaseCauldronBlock.BEntity::new, CAULDRON.get()).build(DSL.remainderType()));
    public static final DeferredBlock<AlchemyTableBlock> ALCHEMY_TABLE = registerWithItem("alchemy_table", () -> new AlchemyTableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ENCHANTING_TABLE).lightLevel(state -> 0)), block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.BLUE, "tooltip.item.confluence.alchemy_table.0"));
    public static final DeferredBlock<SolidifierBlock> SOLIDIFIER = registerWithItem("solidifier", () -> new SolidifierBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)), block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.WHITE, "tooltip.item.confluence.solidifier.0"));
    public static final DeferredBlock<WeatherVaneBlock> WEATHER_VANE = registerWithItem("weather_vane", () -> new WeatherVaneBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BARS)), block -> new BlockItem(block, new Item.Properties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.BLUE)));
    public static final Supplier<BlockEntityType<WeatherVaneBlock.BEntity>> WEATHER_VANE_ENTITY = BLOCK_ENTITIES.register("weather_vane_entity", () -> BlockEntityType.Builder.of(WeatherVaneBlock.BEntity::new, WEATHER_VANE.get()).build(DSL.remainderType()));
    /**
     * @see org.confluence.mod.mixin.block.AnvilBlockMixin
     */
    public static final DeferredBlock<AnvilBlock> LEAD_ANVIL = registerWithItem("lead_anvil", () -> new AnvilBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ANVIL)));
    public static final DeferredBlock<AnvilBlock> CHIPPED_LEAD_ANVIL = registerWithItem("chipped_lead_anvil", () -> new AnvilBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHIPPED_ANVIL)));
    public static final DeferredBlock<AnvilBlock> DAMAGED_LEAD_ANVIL = registerWithItem("damaged_lead_anvil", () -> new AnvilBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DAMAGED_ANVIL)));
    public static final DeferredBlock<SharpeningStationBlock> SHARPENING_STATION = registerWithItem("sharpening_station", () -> new SharpeningStationBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONECUTTER)), block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.BLUE, "tooltip.item.confluence.sharpening_station.0"));
    public static final DeferredBlock<AmmoBoxBlock> AMMO_BOX = registerWithItem("ammo_box", () -> new AmmoBoxBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHEST)), block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.LIGHT_PURPLE, "tooltip.item.confluence.ammo_box.0"));
    public static final DeferredBlock<BewitchingTableBlock> BEWITCHING_TABLE = registerWithItem("bewitching_table", () -> new BewitchingTableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ENCHANTING_TABLE).lightLevel(state -> 0)), block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.BLUE, "tooltip.item.confluence.bewitching_table.0"));
    public static final Supplier<BlockEntityType<BewitchingTableBlock.BEntity>> BEWITCHING_TABLE_ENTITY = BLOCK_ENTITIES.register("bewitching_table_entity", () -> BlockEntityType.Builder.of(BewitchingTableBlock.BEntity::new, BEWITCHING_TABLE.get()).build(DSL.remainderType()));
    public static final DeferredBlock<KegBlock> KEG = registerWithItem("keg", () -> new KegBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)), block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.WHITE, "tooltip.item.confluence.keg.0"));
    public static final DeferredBlock<CrystalBallBlock> CRYSTAL_BALL = registerWithItem("crystal_ball", () -> new CrystalBallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BEACON).sound(SoundType.AMETHYST)), block -> new BlockItem(block, new Item.Properties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.ORANGE)));
    public static final DeferredBlock<HardmodeAnvilBlock> MYTHRIL_ANVIL = registerWithItem("mythril_anvil", () -> new HardmodeAnvilBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ANVIL)), block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.ORANGE, "tooltip.item.confluence.hardmode_anvil.0"));
    public static final DeferredBlock<HardmodeAnvilBlock> ORICHALCUM_ANVIL = registerWithItem("orichalcum_anvil", () -> new HardmodeAnvilBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ANVIL)), block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.ORANGE, "tooltip.item.confluence.hardmode_anvil.0"));
    public static final DeferredBlock<ChlorophyteExtractinatorBlock> CHLOROPHYTE_EXTRACTINATOR = registerWithItem("chlorophyte_extractinator", () -> new ChlorophyteExtractinatorBlock(BlockBehaviour.Properties.of().strength(4.4F, 10.0F).requiresCorrectToolForDrops()), block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.LIME, TooltipItem.getTooltipsFromString("chlorophyte_extractinator", 3, ChatFormatting.GRAY)));
    public static final DeferredBlock<SingleItemStackSwapperBlock> BLEND_O_MATIC = registerWithItem("blend_o_matic", () -> new SingleItemStackSwapperBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON), 1, itemStack -> itemStack.is(MaterialItems.RAW_ASPHALT) ? DecorativeBlocks.ASPHALT_BLOCK.toStack() : ItemStack.EMPTY), block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.WHITE, "tooltip.item.confluence.blend_o_matic.0"));
    public static final DeferredBlock<SingleItemStackSwapperBlock> MEAT_GRINDER = registerWithItem("meat_grinder", () -> new SingleItemStackSwapperBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON), 2, itemStack -> {
        if (itemStack.is(NatureBlocks.COBBLED_CRIMSTONE.asItem())) {
            return DecorativeBlocks.FLESH_BLOCK.toStack();
        } else if (itemStack.is(NatureBlocks.COBBLED_EBONSTONE.asItem())) {
            return DecorativeBlocks.LESION_BLOCK.toStack();
        }
        return ItemStack.EMPTY;
    }), block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.WHITE, "tooltip.item.confluence.meat_grinder.0"));
    public static final DeferredBlock<AdamantiteForgeBlock> ADAMANTITE_FORGE = registerWithItem("adamantite_forge", AdamantiteForgeBlock::new, block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.ORANGE, Component.translatable("tooltip.item.confluence.hardmode_forge.0").withStyle(ChatFormatting.GRAY)));
    public static final DeferredBlock<TitaniumForgeBlock> TITANIUM_FORGE = registerWithItem("titanium_forge", TitaniumForgeBlock::new);
    public static final Supplier<BlockEntityType<HardmodeForgeBlock.BEntity>> HARDMODE_FORGE_ENTITY = BLOCK_ENTITIES.register("hardmode_forge_entity", () -> BlockEntityType.Builder.of(HardmodeForgeBlock.BEntity::new, ADAMANTITE_FORGE.get(), TITANIUM_FORGE.get()).build(DSL.remainderType()));

    public static final DeferredBlock<LifeCampfireBlock> LIFE_CAMPFIRE = registerWithItem("life_campfire", () -> new LifeCampfireBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAMPFIRE)), block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.WHITE, "tooltip.item.confluence.life_campfire.0"));
    public static final DeferredBlock<PiggyBankBlock> PIGGY_BANK = registerWithItem("piggy_bank", () -> new PiggyBankBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DECORATED_POT)), block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.WHITE, TooltipItem.getTooltipsFromString("piggy_bank", 2, ChatFormatting.GRAY)));
    public static final Supplier<BlockEntityType<PiggyBankBlock.BEntity>> PIGGY_BANK_ENTITY = BLOCK_ENTITIES.register("piggy_bank_entity", () -> BlockEntityType.Builder.of(PiggyBankBlock.BEntity::new, PIGGY_BANK.get()).build(DSL.remainderType()));
    public static final DeferredBlock<SafeBlock> SAFE = registerWithItem("safe", () -> new SafeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)), block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.WHITE, TooltipItem.getTooltipsFromString("safe", 2, ChatFormatting.GRAY)));
    public static final Supplier<BlockEntityType<SafeBlock.BEntity>> SAFE_ENTITY = BLOCK_ENTITIES.register("safe_entity", () -> BlockEntityType.Builder.of(SafeBlock.BEntity::new, SAFE.get()).build(DSL.remainderType()));

    public static final DeferredBlock<EchoBlock> ECHO_BLOCK = registerWithItem("echo_block", EchoBlock::new, block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.BLUE, "tooltip.item.confluence.echo_block.0"));
    public static final DeferredBlock<EverPoweredRailBlock> EVER_POWERED_RAIL = registerWithItem("ever_powered_rail", () -> new EverPoweredRailBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ACTIVATOR_RAIL)));
    public static final DeferredBlock<StepOnTrapBlock> SHIMMER_TRAP = registerWithItem("shimmer_trap", () -> new StepOnTrapBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DISPENSER), StepOnTrapBlock.SHIMMER));
    public static final DeferredBlock<StepOnTrapBlock> GRAVITATION_TRAP = registerWithItem("gravitation_trap", () -> new StepOnTrapBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DISPENSER), StepOnTrapBlock.GRAVITATION));
    public static final DeferredBlock<StepOnTrapBlock> PNEUMATIC_TRAP = registerWithItem("pneumatic_trap", () -> new StepOnTrapBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DISPENSER), StepOnTrapBlock.PNEUMATIC));
    public static final DeferredBlock<SpikeBlock> SPIKE = registerWithItem("spike", () -> new SpikeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BARS), 12));
    public static final DeferredBlock<SpikeBlock> WOODEN_SPIKE = registerWithItem("wooden_spike", () -> new SpikeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.JUNGLE_PLANKS), 16));
    public static final DeferredBlock<FragileBlock> FRAGILE_SANDSTONE = registerWithItem("fragile_sandstone", () -> new FragileBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE), Blocks.SANDSTONE::defaultBlockState));
    public static final DeferredBlock<FragileBlock> FRAGILE_BLUE_BRICKS = registerWithItem("fragile_blue_bricks", () -> new FragileBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS), () -> DecorativeBlocks.BLUE_BRICKS.get().defaultBlockState()));
    public static final DeferredBlock<FragileBlock> FRAGILE_GREEN_BRICKS = registerWithItem("fragile_green_bricks", () -> new FragileBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS), () -> DecorativeBlocks.GREEN_BRICKS.get().defaultBlockState()));
    public static final DeferredBlock<FragileBlock> FRAGILE_PINK_BRICKS = registerWithItem("fragile_pink_bricks", () -> new FragileBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS), () -> DecorativeBlocks.PINK_BRICKS.get().defaultBlockState()));
    public static final DeferredBlock<EnchantedFragileBricksBlock> ENCHANTED_FRAGILE_BLUE_BRICKS = registerWithItem("enchanted_fragile_blue_bricks", () -> new EnchantedFragileBricksBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS), () -> DecorativeBlocks.ENCHANTED_BLUE_BRICKS.get().defaultBlockState()));
    public static final DeferredBlock<EnchantedFragileBricksBlock> ENCHANTED_FRAGILE_GREEN_BRICKS = registerWithItem("enchanted_fragile_green_bricks", () -> new EnchantedFragileBricksBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS), () -> DecorativeBlocks.ENCHANTED_GREEN_BRICKS.get().defaultBlockState()));
    public static final DeferredBlock<EnchantedFragileBricksBlock> ENCHANTED_FRAGILE_PINK_BRICKS = registerWithItem("enchanted_fragile_pink_bricks", () -> new EnchantedFragileBricksBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS), () -> DecorativeBlocks.ENCHANTED_PINK_BRICKS.get().defaultBlockState()));
    public static final DeferredBlock<SculkTrapBlock> SCULK_TRAP = registerWithItem("sculk_trap", () -> new SculkTrapBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SCULK_SENSOR)));

    public static final DeferredBlock<SillyBalloonMachineBlock> SILLY_BALLOON_MACHINE = registerWithItem("silly_balloon_machine", () -> new SillyBalloonMachineBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));
    public static final Supplier<BlockEntityType<SillyBalloonMachineBlock.BEntity>> SILLY_BALLOON_MACHINE_ENTITY = BLOCK_ENTITIES.register("silly_balloon_machine_entity", () -> BlockEntityType.Builder.of(SillyBalloonMachineBlock.BEntity::new, SILLY_BALLOON_MACHINE.get()).build(DSL.remainderType()));

    public static final DeferredBlock<BehaviourPressurePlateBlock> PLAYER_PRESSURE_PLATE = registerWithEntity("player_pressure_plate", () -> new BehaviourPressurePlateBlock(BehaviourPressurePlateBlock.PLAYER, BlockBehaviour.Properties.ofFullCopy(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE), BlockSetType.IRON));
    public static final DeferredBlock<SignalPressurePlateBlock> STONE_PRESSURE_PLATE = registerWithEntity("stone_pressure_plate", () -> new SignalPressurePlateBlock(BlockSetType.STONE, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_PRESSURE_PLATE)));
    public static final DeferredBlock<SignalPressurePlateBlock> DEEPSLATE_PRESSURE_PLATE = registerWithEntity("deepslate_pressure_plate", () -> new SignalPressurePlateBlock(BlockSetType.STONE, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_PRESSURE_PLATE).mapColor(MapColor.DEEPSLATE)));
    public static final DeferredBlock<FragilePressureBlock> STONE_PRESSURE_BLOCK = registerWithEntity("stone_pressure_block", () -> new FragilePressureBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE), BlockSetType.STONE, Blocks.STONE::defaultBlockState));
    public static final DeferredBlock<FragilePressureBlock> DEEPSLATE_PRESSURE_BLOCK = registerWithEntity("deepslate_pressure_block", () -> new FragilePressureBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE), BlockSetType.STONE, Blocks.DEEPSLATE::defaultBlockState));
    public static final DeferredBlock<InstantExplosionBlock> INSTANTANEOUS_EXPLOSION_TNT = registerWithEntity("instantaneous_explosion_tnt", InstantExplosionBlock::new);
    public static final DeferredBlock<SwitchBlock> SWITCH = registerWithEntity("switch", SwitchBlock::new);
    public static final DeferredBlock<SwitchBlock> LEVER = registerWithEntity("lever", SwitchBlock::new);
    public static final DeferredBlock<SignalAdapterBlock> SIGNAL_ADAPTER = registerWithEntity("signal_adapter", SignalAdapterBlock::new);
    public static final DeferredBlock<DartTrapBlock> DART_TRAP = registerWithEntity("dart_trap", () -> new DartTrapBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DISPENSER)));
    public static final DeferredBlock<DartTrapBlock> STONE_DART_TRAP = registerWithEntity("stone_dart_trap", () -> new DartTrapBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DISPENSER)));
    public static final DeferredBlock<DartTrapBlock> DEEPSLATE_DART_TRAP = registerWithEntity("deepslate_dart_trap", () -> new DartTrapBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DISPENSER)));
    public static final DeferredBlock<TimersBlock> TIMERS_BLOCK_1_1 = registerWithEntity("timers_1_1", () -> new TimersBlock(20)); // 1s
    public static final DeferredBlock<TimersBlock> TIMERS_BLOCK_3_1 = registerWithEntity("timers_3_1", () -> new TimersBlock(60)); // 3s
    public static final DeferredBlock<TimersBlock> TIMERS_BLOCK_5_1 = registerWithEntity("timers_5_1", () -> new TimersBlock(100)); // 5s
    public static final DeferredBlock<TimersBlock> TIMERS_BLOCK_1_2 = registerWithEntity("timers_1_2", () -> new TimersBlock(10)); // 1/2s
    public static final DeferredBlock<TimersBlock> TIMERS_BLOCK_1_4 = registerWithEntity("timers_1_4", () -> new TimersBlock(5)); // 1/4s
    public static final DeferredBlock<GeyserBlock> GEYSER_BLOCK = registerWithEntity("geyser_block", GeyserBlock::new);
    public static final DeferredBlock<BoulderBlock> NORMAL_BOULDER = registerWithEntity("normal_boulder", BoulderBlock::new);
    public static final DeferredBlock<BoulderBlock> OAK_LOG_BOULDER = registerWithEntity("oak_log_boulder", BoulderBlock::new);
    public static final DeferredBlock<BoulderBlock> FOLLOWER_BOULDER = registerWithEntity("follower_boulder", () -> new BoulderBlock(FollowerBoulderEntity::new));
    public static final DeferredBlock<BoulderBlock> EXPLODE_BOULDER = registerWithEntity("explode_boulder", () -> new BoulderBlock(ExplodeBoulderEntity::new));
    public static final DeferredBlock<BoulderBlock> ROLLING_CACTUS_BOULDER = registerWithEntity("rolling_cactus_boulder", RollingCactusBoulderBlock::new);
    public static final DeferredBlock<DetonatorBlock> DETONATOR = registerWithEntity("detonator", () -> new DetonatorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_TRAPDOOR)));
    public static final DeferredBlock<MechanicalFragileBlock> MECHANICAL_FRAGILE_SANDSTONE = registerWithEntity("mechanical_fragile_sandstone", () -> new MechanicalFragileBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE), Blocks.SANDSTONE::defaultBlockState));
    public static final DeferredBlock<MechanicalFragileBlock> MECHANICAL_FRAGILE_OBSIDIAN_BRICKS = registerWithEntity("mechanical_fragile_obsidian_bricks", () -> new MechanicalFragileBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS), DecorativeBlocks.OBSIDIAN_BRICKS.get()::defaultBlockState));
    public static final DeferredBlock<LandMineBlock> LAND_MINE = registerWithEntity("land_mine", () -> new LandMineBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TNT)));
    public static final DeferredBlock<SuperDartTrapBlock> SUPER_DART_TRAP = registerWithEntity("super_dart_trap", () -> new SuperDartTrapBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).requiresCorrectToolForDrops().strength(100.0F, ModBlocks.getObsidianBasedExplosionResistance(1000.0F))));
    public static final DeferredBlock<FlameTrapBlock> FLAME_TRAP = registerWithEntity("flame_trap", () -> new FlameTrapBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).requiresCorrectToolForDrops().strength(100.0F, ModBlocks.getObsidianBasedExplosionResistance(1000.0F))));
    public static final DeferredBlock<SpikyBallTrapBlock> SPIKY_BALL_TRAP = registerWithEntity("spiky_ball_trap", () -> new SpikyBallTrapBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).requiresCorrectToolForDrops().strength(100.0F, ModBlocks.getObsidianBasedExplosionResistance(1000.0F))));
    public static final DeferredBlock<SpearTrapBlock> SPEAR_TRAP = registerWithEntity("spear_trap", () -> new SpearTrapBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).requiresCorrectToolForDrops().strength(100.0F, ModBlocks.getObsidianBasedExplosionResistance(1000.0F))));

    public static final DeferredBlock<AnnouncementBoxBlock> ANNOUNCEMENT_BOX = BLOCKS.register("announcement_box", () -> new AnnouncementBoxBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F).noCollission().noOcclusion().sound(SoundType.METAL).isValidSpawn(Blocks::never)));
    public static final DeferredBlock<AnnouncementBoxBlock.Wall> WALL_ANNOUNCEMENT_BOX = BLOCKS.register("wall_announcement_box", () -> new AnnouncementBoxBlock.Wall(BlockBehaviour.Properties.ofFullCopy(ANNOUNCEMENT_BOX.get())));
    public static final DeferredItem<SignItem> ANNOUNCEMENT_BOX_ITEM = ModItems.BLOCK_ITEMS.register("announcement_box", () -> new SignItem(new Item.Properties(), ANNOUNCEMENT_BOX.get(), WALL_ANNOUNCEMENT_BOX.get()));
    public static final Supplier<BlockEntityType<AnnouncementBoxBlock.BEntity>> ANNOUNCEMENT_BOX_ENTITY = BLOCK_ENTITIES.register("announcement_box_entity", () -> BlockEntityType.Builder.of(AnnouncementBoxBlock.BEntity::new, ANNOUNCEMENT_BOX.get(), WALL_ANNOUNCEMENT_BOX.get()).build(DSL.remainderType()));
    public static final Supplier<BlockEntityType<AbstractMechanicalBlock.BEntity>> MECHANICAL_BLOCK_ENTITY = BLOCK_ENTITIES.register("mechanical_block_entity", () -> {
        Block[] validBlocks = MECHANICAL_BLOCKS.stream().map(DeferredBlock::get).toArray(Block[]::new);
        MECHANICAL_BLOCKS = null;
        return BlockEntityType.Builder.of(AbstractMechanicalBlock.BEntity::new, validBlocks).build(DSL.remainderType());
    });

    public static final DeferredBlock<TreeHolesBlock> TREE_HOLES_BLOCK = registerWithItem("tree_holes", () -> new TreeHolesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)));
    public static final Supplier<BlockEntityType<TreeHolesBlock.BEntity>> TREE_HOLES_ENTITY = BLOCK_ENTITIES.register("tree_holes", () -> BlockEntityType.Builder.of(TreeHolesBlock.BEntity::new, TREE_HOLES_BLOCK.get()).build(DSL.remainderType()));
    public static final DeferredBlock<MagicMailBox> MAGIC_MAIL_BOX = registerWithItem("magic_mail_box", MagicMailBox::new);

    public static final DeferredBlock<LockBlock> LOCK_BLOCK = registerWithItem("lock_block", () -> new LockBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BEDROCK)));
    public static final Supplier<BlockEntityType<LockBlock.BEntity>> LOCK_BLOCK_ENTITY = BLOCK_ENTITIES.register("lock_block_entity", () -> BlockEntityType.Builder.of(LockBlock.BEntity::new, LOCK_BLOCK.get()).build(DSL.remainderType()));

    private static <B extends Block> DeferredBlock<B> registerWithItem(String id, Supplier<B> supplier) {
        DeferredBlock<B> block = BLOCKS.register(id, supplier);
        ModItems.BLOCK_ITEMS.registerSimpleBlockItem(block);
        return block;
    }

    private static <B extends Block> DeferredBlock<B> registerWithItem(String id, Supplier<B> supplier, Function<B, BlockItem> function) {
        DeferredBlock<B> block = BLOCKS.register(id, supplier);
        ModItems.BLOCK_ITEMS.register(id, () -> function.apply(block.get()));
        return block;
    }

    static <B extends Block & EntityBlock & INetworkBlock> DeferredBlock<B> registerWithEntity(String id, Supplier<B> supplier) {
        DeferredBlock<B> holder = registerWithItem(id, supplier);
        MECHANICAL_BLOCKS.add(holder);
        return holder;
    }
}
