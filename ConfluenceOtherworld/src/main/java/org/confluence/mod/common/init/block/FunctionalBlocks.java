package org.confluence.mod.common.init.block;

import PortLib.extensions.net.minecraft.world.item.Item.PortItemExtension;
import com.mojang.datafixers.DSL;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipBlockItem;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.lib.util.MobEffectInstanceData;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.common.EnchantedFragileBricksBlock;
import org.confluence.mod.common.block.functional.*;
import org.confluence.mod.common.block.functional.boulder.BoulderBlock;
import org.confluence.mod.common.block.functional.boulder.ContactEffectBoulderBlock;
import org.confluence.mod.common.block.functional.boulder.FullCollisionBoulderBlock;
import org.confluence.mod.common.block.functional.boulder.GeoBoulderBlock;
import org.confluence.mod.common.block.functional.crafting.*;
import org.confluence.mod.common.block.functional.crafting.LoomBlock;
import org.confluence.mod.common.block.functional.network.INetworkBlock;
import org.confluence.mod.common.block.natural.MagicMailBox;
import org.confluence.mod.common.block.natural.TreeHolesBlock;
import org.confluence.mod.common.entity.projectile.boulder.*;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.item.MaterialItems;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.mixin.world.level.block.AnvilBlockMixin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.minecraft.world.level.block.Blocks.LANTERN;
import static org.confluence.mod.common.init.block.ModBlocks.BLOCK_ENTITIES;

public class FunctionalBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, Confluence.MODID);
    static List<RegistryObject<? extends Block>> MECHANICAL_BLOCKS = new ArrayList<>();

    public static final RegistryObject<ExtractinatorBlock> EXTRACTINATOR = registerWithItem("extractinator", () -> new ExtractinatorBlock(BlockBehaviour.Properties.of().strength(2.2F, 5.0F).requiresCorrectToolForDrops()), ExtractinatorBlock.BItem::new);
    public static final Supplier<BlockEntityType<ExtractinatorBlock.BEntity>> EXTRACTINATOR_ENTITY = BLOCK_ENTITIES.register("extractinator_entity", () -> BlockEntityType.Builder.of(ExtractinatorBlock.BEntity::new, EXTRACTINATOR.get()).build(DSL.remainderType()));

    public static final RegistryObject<AltarBlock> DEMON_ALTAR = registerWithItem("demon_altar", () -> new AltarBlock(BlockBehaviour.Properties.of().strength(3.0F, 18000.0F).lightLevel(state -> 5), AltarBlock.Variant.DEMON), AltarBlock.BItem::new);
    public static final RegistryObject<AltarBlock> CRIMSON_ALTAR = registerWithItem("crimson_altar", () -> new AltarBlock(BlockBehaviour.Properties.of().strength(3.0F, 18000.0F).lightLevel(state -> 5), AltarBlock.Variant.CRIMSON), AltarBlock.BItem::new);
    public static final Supplier<BlockEntityType<AltarBlock.BEntity>> ALTAR_BLOCK_ENTITY = BLOCK_ENTITIES.register("altar_block_entity", () -> BlockEntityType.Builder.of(AltarBlock.BEntity::new, DEMON_ALTAR.get(), CRIMSON_ALTAR.get()).build(DSL.remainderType()));

    public static final RegistryObject<SkyMillBlock> SKY_MILL = registerWithItem("sky_mill", () -> new SkyMillBlock(BlockBehaviour.Properties.copy(Blocks.GRINDSTONE)), SkyMillBlock.BItem::new);
    public static final Supplier<BlockEntityType<SkyMillBlock.BEntity>> SKY_MILL_ENTITY = BLOCK_ENTITIES.register("sky_mill_entity", () -> BlockEntityType.Builder.of(SkyMillBlock.BEntity::new, SKY_MILL.get()).build(DSL.remainderType()));
    public static final RegistryObject<HeavyWorkBenchBlock> HEAVY_WORK_BENCH = registerWithItem("heavy_work_bench", () -> new HeavyWorkBenchBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_BRICKS)), block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.WHITE, "tooltip.item.confluence.heavy_work_bench.0"));
    public static final RegistryObject<SawmillBlock> SAWMILL = registerWithItem("sawmill", () -> new SawmillBlock(BlockBehaviour.Properties.copy(Blocks.OAK_WOOD)), block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.WHITE, "tooltip.item.confluence.sawmill.0"));
    public static final RegistryObject<HellforgeBlock> HELLFORGE = registerWithItem("hellforge", () -> new HellforgeBlock(BlockBehaviour.Properties.copy(Blocks.MAGMA_BLOCK).lightLevel(state -> state.getValue(BlockStateProperties.LIT) ? 15 : 7).noOcclusion()), block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.WHITE, "tooltip.item.confluence.hellforge.0"));
    public static final Supplier<BlockEntityType<HellforgeBlock.BEntity>> HELLFORGE_ENTITY = BLOCK_ENTITIES.register("hellforge_entity", () -> BlockEntityType.Builder.of(HellforgeBlock.BEntity::new, HELLFORGE.get()).build(DSL.remainderType()));
    public static final RegistryObject<CookingPotBlock> COOKING_POT = registerWithItem("cooking_pot", () -> new CookingPotBlock(BlockBehaviour.Properties.copy(Blocks.CAULDRON)), block -> new CookingPotBlock.BItem(block, new Item.Properties()));
    public static final Supplier<BlockEntityType<CookingPotBlock.BEntity>> COOKING_POT_ENTITY = BLOCK_ENTITIES.register("cooking_pot_entity", () -> BlockEntityType.Builder.of(CookingPotBlock.BEntity::new, COOKING_POT.get()).build(DSL.remainderType()));
    public static final RegistryObject<BaseCauldronBlock> CAULDRON = registerWithItem("cauldron", () -> new BaseCauldronBlock(BlockBehaviour.Properties.copy(Blocks.CAULDRON)));
    public static final Supplier<BlockEntityType<BaseCauldronBlock.BEntity>> CAULDRON_ENTITY = BLOCK_ENTITIES.register("cauldron_entity", () -> BlockEntityType.Builder.of(BaseCauldronBlock.BEntity::new, CAULDRON.get()).build(DSL.remainderType()));
    public static final RegistryObject<AlchemyTableBlock> ALCHEMY_TABLE = registerWithItem("alchemy_table", () -> new AlchemyTableBlock(BlockBehaviour.Properties.copy(Blocks.ENCHANTING_TABLE).lightLevel(state -> 0)), block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.BLUE, "tooltip.item.confluence.alchemy_table.0"));
    public static final RegistryObject<SolidifierBlock> SOLIDIFIER = registerWithItem("solidifier", () -> new SolidifierBlock(BlockBehaviour.Properties.copy(Blocks.BARREL)), block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.WHITE, "tooltip.item.confluence.solidifier.0"));
    public static final RegistryObject<WeatherVaneBlock> WEATHER_VANE = registerWithItem("weather_vane", () -> new WeatherVaneBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BARS)), block -> new BlockItem(block, PortItemExtension.Properties.component(new Item.Properties(), ConfluenceMagicLib.MOD_RARITY, ModRarity.BLUE)));
    public static final Supplier<BlockEntityType<WeatherVaneBlock.BEntity>> WEATHER_VANE_ENTITY = BLOCK_ENTITIES.register("weather_vane_entity", () -> BlockEntityType.Builder.of(WeatherVaneBlock.BEntity::new, WEATHER_VANE.get()).build(DSL.remainderType()));
    public static final RegistryObject<TuffBoothBlock> TUFF_BOOTH = registerWithItem("tuff_booth", () -> new TuffBoothBlock(ModBlocks.tuffBricksProperties()), block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.WHITE, "tooltip.item.confluence.tuff_booth.0"));
    public static final Supplier<BlockEntityType<TuffBoothBlock.TuffBoothBlockEntity>> TUFF_BOOTH_ENTITY =
            BLOCK_ENTITIES.register("tuff_booth_entity", () ->
                    BlockEntityType.Builder.of(TuffBoothBlock.TuffBoothBlockEntity::new, TUFF_BOOTH.get()).build(DSL.remainderType())
            );


    /// [AnvilBlockMixin]
    public static final RegistryObject<AnvilBlock> LEAD_ANVIL = registerWithItem("lead_anvil", () -> new AnvilBlock(BlockBehaviour.Properties.copy(Blocks.ANVIL)));
    public static final RegistryObject<AnvilBlock> CHIPPED_LEAD_ANVIL = registerWithItem("chipped_lead_anvil", () -> new AnvilBlock(BlockBehaviour.Properties.copy(Blocks.CHIPPED_ANVIL)));
    public static final RegistryObject<AnvilBlock> DAMAGED_LEAD_ANVIL = registerWithItem("damaged_lead_anvil", () -> new AnvilBlock(BlockBehaviour.Properties.copy(Blocks.DAMAGED_ANVIL)));
    public static final RegistryObject<SharpeningStationBlock> SHARPENING_STATION = registerWithItem("sharpening_station", () -> new SharpeningStationBlock(BlockBehaviour.Properties.copy(Blocks.STONECUTTER)), block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.BLUE, "tooltip.item.confluence.sharpening_station.0"));
    public static final RegistryObject<AmmoBoxBlock> AMMO_BOX = registerWithItem("ammo_box", () -> new AmmoBoxBlock(BlockBehaviour.Properties.copy(Blocks.CHEST)), block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.LIGHT_PURPLE, "tooltip.item.confluence.ammo_box.0"));
    public static final RegistryObject<BewitchingTableBlock> BEWITCHING_TABLE = registerWithItem("bewitching_table", () -> new BewitchingTableBlock(BlockBehaviour.Properties.copy(Blocks.ENCHANTING_TABLE).lightLevel(state -> 0)), block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.BLUE, "tooltip.item.confluence.bewitching_table.0"));
    public static final Supplier<BlockEntityType<BewitchingTableBlock.BEntity>> BEWITCHING_TABLE_ENTITY = BLOCK_ENTITIES.register("bewitching_table_entity", () -> BlockEntityType.Builder.of(BewitchingTableBlock.BEntity::new, BEWITCHING_TABLE.get()).build(DSL.remainderType()));
    public static final RegistryObject<KegBlock> KEG = registerWithItem("keg", () -> new KegBlock(BlockBehaviour.Properties.copy(Blocks.BARREL)), block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.WHITE, "tooltip.item.confluence.keg.0"));
    public static final RegistryObject<CrystalBallBlock> CRYSTAL_BALL = registerWithItem("crystal_ball", () -> new CrystalBallBlock(BlockBehaviour.Properties.copy(Blocks.BEACON).sound(SoundType.AMETHYST)), block -> new BlockItem(block, PortItemExtension.Properties.component(new Item.Properties(), ConfluenceMagicLib.MOD_RARITY, ModRarity.ORANGE)));
    public static final RegistryObject<HardmodeAnvilBlock> MYTHRIL_ANVIL = registerWithItem("mythril_anvil", () -> new HardmodeAnvilBlock(BlockBehaviour.Properties.copy(Blocks.ANVIL)), block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.ORANGE, "tooltip.item.confluence.hardmode_anvil.0"));
    public static final RegistryObject<HardmodeAnvilBlock> ORICHALCUM_ANVIL = registerWithItem("orichalcum_anvil", () -> new HardmodeAnvilBlock(BlockBehaviour.Properties.copy(Blocks.ANVIL)), block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.ORANGE, "tooltip.item.confluence.hardmode_anvil.0"));
    public static final RegistryObject<ChlorophyteExtractinatorBlock> CHLOROPHYTE_EXTRACTINATOR = registerWithItem("chlorophyte_extractinator", () -> new ChlorophyteExtractinatorBlock(BlockBehaviour.Properties.of().strength(4.4F, 10.0F).requiresCorrectToolForDrops()), block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.LIME, TooltipItem.getTooltipsFromString("chlorophyte_extractinator", 3, ChatFormatting.GRAY)));
    public static final RegistryObject<SingleItemStackSwapperBlock> BLEND_O_MATIC = registerWithItem("blend_o_matic", () -> new SingleItemStackSwapperBlock(BlockBehaviour.Properties.copy(Blocks.CAULDRON), 1, itemStack -> itemStack.is(MaterialItems.RAW_ASPHALT.get()) ? DecorativeBlocks.ASPHALT_BLOCK.get().asItem().getDefaultInstance() : ItemStack.EMPTY), block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.WHITE, "tooltip.item.confluence.blend_o_matic.0"));
    public static final RegistryObject<SingleItemStackSwapperBlock> MEAT_GRINDER = registerWithItem("meat_grinder", () -> new SingleItemStackSwapperBlock(BlockBehaviour.Properties.copy(Blocks.CAULDRON), 2, itemStack -> {
        if (itemStack.is(NatureBlocks.COBBLED_CRIMSTONE.get().asItem())) {
            return DecorativeBlocks.FLESH_BLOCK.get().asItem().getDefaultInstance();
        } else if (itemStack.is(NatureBlocks.COBBLED_EBONSTONE.get().asItem())) {
            return DecorativeBlocks.LESION_BLOCK.get().asItem().getDefaultInstance();
        }
        return ItemStack.EMPTY;
    }), block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.WHITE, "tooltip.item.confluence.meat_grinder.0"));
    public static final RegistryObject<AdamantiteForgeBlock> ADAMANTITE_FORGE = registerWithItem("adamantite_forge", AdamantiteForgeBlock::new, block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.ORANGE, "tooltip.item.confluence.hardmode_forge.0"));
    public static final RegistryObject<TitaniumForgeBlock> TITANIUM_FORGE = registerWithItem("titanium_forge", TitaniumForgeBlock::new, block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.ORANGE, "tooltip.item.confluence.hardmode_forge.0"));
    public static final Supplier<BlockEntityType<HardmodeForgeBlock.BEntity>> HARDMODE_FORGE_ENTITY = BLOCK_ENTITIES.register("hardmode_forge_entity", () -> BlockEntityType.Builder.of(HardmodeForgeBlock.BEntity::new, ADAMANTITE_FORGE.get(), TITANIUM_FORGE.get()).build(DSL.remainderType()));
    public static final RegistryObject<LoomBlock> LOOM = registerWithItem("loom", LoomBlock::new, block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.WHITE, "tooltip.item.confluence.loom.0"));
    public static final Supplier<BlockEntityType<LoomBlock.BEntity>> LOOM_ENTITY = BLOCK_ENTITIES.register("loom_entity", () -> BlockEntityType.Builder.of(LoomBlock.BEntity::new, LOOM.get()).build(DSL.remainderType()));
    public static final RegistryObject<DyeVatBlock> DYE_VAT = registerWithItem("dye_vat", DyeVatBlock::new, block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.WHITE, "tooltip.item.confluence.dye_vat.0"));

    public static final RegistryObject<LifeCampfireBlock> LIFE_CAMPFIRE = registerWithItem("life_campfire", () -> new LifeCampfireBlock(BlockBehaviour.Properties.copy(Blocks.CAMPFIRE)), block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.WHITE, "tooltip.item.confluence.life_campfire.0"));
    public static final RegistryObject<PiggyBankBlock> PIGGY_BANK = registerWithItem("piggy_bank", () -> new PiggyBankBlock(BlockBehaviour.Properties.copy(Blocks.DECORATED_POT)), block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.WHITE, TooltipItem.getTooltipsFromString("piggy_bank", 2, ChatFormatting.GRAY)));
    public static final Supplier<BlockEntityType<PiggyBankBlock.BEntity>> PIGGY_BANK_ENTITY = BLOCK_ENTITIES.register("piggy_bank_entity", () -> BlockEntityType.Builder.of(PiggyBankBlock.BEntity::new, PIGGY_BANK.get()).build(DSL.remainderType()));
    public static final RegistryObject<SafeBlock> SAFE = registerWithItem("safe", () -> new SafeBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)), block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.WHITE, TooltipItem.getTooltipsFromString("safe", 2, ChatFormatting.GRAY)));
    public static final Supplier<BlockEntityType<SafeBlock.BEntity>> SAFE_ENTITY = BLOCK_ENTITIES.register("safe_entity", () -> BlockEntityType.Builder.of(SafeBlock.BEntity::new, SAFE.get()).build(DSL.remainderType()));

    public static final RegistryObject<EchoBlock> ECHO_BLOCK = registerWithItem("echo_block", EchoBlock::new, block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.BLUE, "tooltip.item.confluence.echo_block.0"));
    public static final RegistryObject<EverPoweredRailBlock> EVER_POWERED_RAIL = registerWithItem("ever_powered_rail", () -> new EverPoweredRailBlock(BlockBehaviour.Properties.copy(Blocks.ACTIVATOR_RAIL)));
    public static final RegistryObject<StepOnTrapBlock> SHIMMER_TRAP = registerWithItem("shimmer_trap", () -> new StepOnTrapBlock(BlockBehaviour.Properties.copy(Blocks.DISPENSER), StepOnTrapBlock.SHIMMER));
    public static final RegistryObject<StepOnTrapBlock> GRAVITATION_TRAP = registerWithItem("gravitation_trap", () -> new StepOnTrapBlock(BlockBehaviour.Properties.copy(Blocks.DISPENSER), StepOnTrapBlock.GRAVITATION));
    public static final RegistryObject<StepOnTrapBlock> PNEUMATIC_TRAP = registerWithItem("pneumatic_trap", () -> new StepOnTrapBlock(BlockBehaviour.Properties.copy(Blocks.DISPENSER), StepOnTrapBlock.PNEUMATIC));
    public static final RegistryObject<SpikeBlock> SPIKE = registerWithItem("spike", () -> new SpikeBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BARS), 12));
    public static final RegistryObject<SpikeBlock> WOODEN_SPIKE = registerWithItem("wooden_spike", () -> new SpikeBlock(BlockBehaviour.Properties.copy(Blocks.JUNGLE_PLANKS), 16));
    public static final RegistryObject<FragileBlock> FRAGILE_SANDSTONE = registerWithItem("fragile_sandstone", () -> new FragileBlock(BlockBehaviour.Properties.copy(Blocks.SANDSTONE), Blocks.SANDSTONE::defaultBlockState));
    public static final RegistryObject<FragileBlock> FRAGILE_BLUE_BRICKS = registerWithItem("fragile_blue_bricks", () -> new FragileBlock(BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS), () -> DecorativeBlocks.BLUE_BRICKS.FULL.get().defaultBlockState()));
    public static final RegistryObject<FragileBlock> FRAGILE_GREEN_BRICKS = registerWithItem("fragile_green_bricks", () -> new FragileBlock(BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS), () -> DecorativeBlocks.GREEN_BRICKS.FULL.get().defaultBlockState()));
    public static final RegistryObject<FragileBlock> FRAGILE_PINK_BRICKS = registerWithItem("fragile_pink_bricks", () -> new FragileBlock(BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS), () -> DecorativeBlocks.PINK_BRICKS.FULL.get().defaultBlockState()));
    public static final RegistryObject<EnchantedFragileBricksBlock> ENCHANTED_FRAGILE_BLUE_BRICKS = registerWithItem("enchanted_fragile_blue_bricks", () -> new EnchantedFragileBricksBlock(BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS), () -> DecorativeBlocks.ENCHANTED_BLUE_BRICKS.get().defaultBlockState()));
    public static final RegistryObject<EnchantedFragileBricksBlock> ENCHANTED_FRAGILE_GREEN_BRICKS = registerWithItem("enchanted_fragile_green_bricks", () -> new EnchantedFragileBricksBlock(BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS), () -> DecorativeBlocks.ENCHANTED_GREEN_BRICKS.get().defaultBlockState()));
    public static final RegistryObject<EnchantedFragileBricksBlock> ENCHANTED_FRAGILE_PINK_BRICKS = registerWithItem("enchanted_fragile_pink_bricks", () -> new EnchantedFragileBricksBlock(BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS), () -> DecorativeBlocks.ENCHANTED_PINK_BRICKS.get().defaultBlockState()));
    public static final RegistryObject<SculkTrapBlock> SCULK_TRAP = registerWithItem("sculk_trap", () -> new SculkTrapBlock(BlockBehaviour.Properties.copy(Blocks.SCULK_SENSOR)));

    public static final RegistryObject<SillyBalloonMachineBlock> SILLY_BALLOON_MACHINE = registerWithItem("silly_balloon_machine", () -> new SillyBalloonMachineBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final Supplier<BlockEntityType<SillyBalloonMachineBlock.BEntity>> SILLY_BALLOON_MACHINE_ENTITY = BLOCK_ENTITIES.register("silly_balloon_machine_entity", () -> BlockEntityType.Builder.of(SillyBalloonMachineBlock.BEntity::new, SILLY_BALLOON_MACHINE.get()).build(DSL.remainderType()));

    public static final RegistryObject<BehaviourPressurePlateBlock> PLAYER_PRESSURE_PLATE = registerWithEntity("player_pressure_plate", () -> new BehaviourPressurePlateBlock(BehaviourPressurePlateBlock.PLAYER, BlockBehaviour.Properties.copy(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE), BlockSetType.IRON));
    public static final RegistryObject<SignalPressurePlateBlock> STONE_PRESSURE_PLATE = registerWithEntity("stone_pressure_plate", () -> new SignalPressurePlateBlock(BlockSetType.STONE, BlockBehaviour.Properties.copy(Blocks.STONE_PRESSURE_PLATE)));
    public static final RegistryObject<SignalPressurePlateBlock> DEEPSLATE_PRESSURE_PLATE = registerWithEntity("deepslate_pressure_plate", () -> new SignalPressurePlateBlock(BlockSetType.STONE, BlockBehaviour.Properties.copy(Blocks.STONE_PRESSURE_PLATE).mapColor(MapColor.DEEPSLATE)));
    public static final RegistryObject<FragilePressureBlock> STONE_PRESSURE_BLOCK = registerWithEntity("stone_pressure_block", () -> new FragilePressureBlock(BlockBehaviour.Properties.copy(Blocks.STONE), BlockSetType.STONE, Blocks.STONE::defaultBlockState));
    public static final RegistryObject<FragilePressureBlock> DEEPSLATE_PRESSURE_BLOCK = registerWithEntity("deepslate_pressure_block", () -> new FragilePressureBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE), BlockSetType.STONE, Blocks.DEEPSLATE::defaultBlockState));
    public static final RegistryObject<InstantExplosionBlock> INSTANTANEOUS_EXPLOSION_TNT = registerWithEntity("instantaneous_explosion_tnt", InstantExplosionBlock::new);
    public static final RegistryObject<SwitchBlock> SWITCH = registerWithEntity("switch", SwitchBlock::new);
    public static final RegistryObject<SwitchBlock> LEVER = registerWithEntity("lever", SwitchBlock::new);
    public static final RegistryObject<SignalAdapterBlock> SIGNAL_ADAPTER = registerWithEntity("signal_adapter", SignalAdapterBlock::new);
    public static final RegistryObject<DartTrapBlock> DART_TRAP = registerWithEntity("dart_trap", () -> new DartTrapBlock(BlockBehaviour.Properties.copy(Blocks.DISPENSER)));
    public static final RegistryObject<DartTrapBlock> STONE_DART_TRAP = registerWithEntity("stone_dart_trap", () -> new DartTrapBlock(BlockBehaviour.Properties.copy(Blocks.DISPENSER)));
    public static final RegistryObject<DartTrapBlock> DEEPSLATE_DART_TRAP = registerWithEntity("deepslate_dart_trap", () -> new DartTrapBlock(BlockBehaviour.Properties.copy(Blocks.DISPENSER)));
    public static final RegistryObject<TimersBlock> TIMERS_BLOCK_1_1 = registerWithEntity("timers_1_1", () -> new TimersBlock(20)); // 1s
    public static final RegistryObject<TimersBlock> TIMERS_BLOCK_3_1 = registerWithEntity("timers_3_1", () -> new TimersBlock(60)); // 3s
    public static final RegistryObject<TimersBlock> TIMERS_BLOCK_5_1 = registerWithEntity("timers_5_1", () -> new TimersBlock(100)); // 5s
    public static final RegistryObject<TimersBlock> TIMERS_BLOCK_1_2 = registerWithEntity("timers_1_2", () -> new TimersBlock(10)); // 1/2s
    public static final RegistryObject<TimersBlock> TIMERS_BLOCK_1_4 = registerWithEntity("timers_1_4", () -> new TimersBlock(5)); // 1/4s
    public static final RegistryObject<GeyserBlock> GEYSER_BLOCK = registerWithEntity("geyser_block", GeyserBlock::new);
    public static final RegistryObject<BoulderBlock> NORMAL_BOULDER = registerWithEntity("normal_boulder", BoulderBlock::new);
    public static final RegistryObject<FullCollisionBoulderBlock> OAK_LOG_BOULDER = registerWithEntity("oak_log_boulder", FullCollisionBoulderBlock::new);
    public static final RegistryObject<BoulderBlock> FOLLOWER_BOULDER = registerWithEntity("follower_boulder", () -> new BoulderBlock(FollowerBoulderEntity::new));
    public static final RegistryObject<BoulderBlock> EXPLODE_BOULDER = registerWithEntity("explode_boulder", () -> new BoulderBlock(ExplodeBoulderEntity::new));
    public static final RegistryObject<BoulderBlock> ROLLING_CACTUS_BOULDER = registerWithEntity("rolling_cactus_boulder", () -> new ContactEffectBoulderBlock(RollingCactusBoulderEntity::new, ContactEffectBoulderBlock.ContactEffect.createHurt((entity) -> entity instanceof Player ? 19.0F : 1.5F, (level) -> level.damageSources().cactus())));
    public static final RegistryObject<BoulderBlock> BOUNCY_BOULDER = registerWithEntity("bouncy_boulder", () -> new BoulderBlock(BouncyBoulderEntity::new));
    public static final RegistryObject<BoulderBlock> GHOULDER = registerWithEntity("ghoulder", () -> new BoulderBlock(GhoulderEntity::new));
    public static final RegistryObject<BoulderBlock> LAVA_BOULDER = registerWithEntity("lava_boulder", () -> new BoulderBlock(LavaBoulderEntity::new));
    public static final RegistryObject<BoulderBlock> POO_BOULDER = registerWithEntity("poo_boulder", () -> new ContactEffectBoulderBlock(PooBoulderEntity::new, ContactEffectBoulderBlock.ContactEffect.createEffect(new MobEffectInstanceData(ModEffects.STINKY, 20 * 3))));
    public static final RegistryObject<BoulderBlock> SPIDER_BOULDER = registerWithEntity("spider_boulder", () -> new BoulderBlock(SpiderBoulderEntity::new));
    public static final RegistryObject<BoulderBlock> RAINBOW_BOULDER = registerWithEntity("rainbow_boulder", () -> new BoulderBlock(RainbowBoulderEntity::new));
    public static final RegistryObject<GeoBoulderBlock> LIFECRYSTAL_BOULDER = registerGeoBoulder("lifecrystal_boulder", () -> new GeoBoulderBlock(LifecrystalBoulderEntity::new), GeoBoulderBlock.BItem::new);
    public static final Supplier<BlockEntityType<GeoBoulderBlock.BEntity>> LIFECRYSTAL_BOULDER_ENTITY = BLOCK_ENTITIES.register("lifecrystal_boulder_entity", () -> BlockEntityType.Builder.of(GeoBoulderBlock.BEntity::new, LIFECRYSTAL_BOULDER.get()).build(DSL.remainderType()));
    public static final RegistryObject<DetonatorBlock> DETONATOR = registerWithEntity("detonator", () -> new DetonatorBlock(BlockBehaviour.Properties.copy(Blocks.IRON_TRAPDOOR)));
    public static final RegistryObject<MechanicalFragileBlock> MECHANICAL_FRAGILE_SANDSTONE = registerWithEntity("mechanical_fragile_sandstone", () -> new MechanicalFragileBlock(BlockBehaviour.Properties.copy(Blocks.SANDSTONE), Blocks.SANDSTONE::defaultBlockState));
    public static final RegistryObject<MechanicalFragileBlock> MECHANICAL_FRAGILE_OBSIDIAN_BRICKS = registerWithEntity("mechanical_fragile_obsidian_bricks", () -> new MechanicalFragileBlock(BlockBehaviour.Properties.copy(Blocks.BRICKS), DecorativeBlocks.OBSIDIAN_BRICKS.FULL.get()::defaultBlockState));
    public static final RegistryObject<LandMineBlock> LAND_MINE = registerWithEntity("land_mine", () -> new LandMineBlock(BlockBehaviour.Properties.copy(Blocks.TNT)));
    public static final RegistryObject<SuperDartTrapBlock> SUPER_DART_TRAP = registerWithEntity("super_dart_trap", () -> new SuperDartTrapBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).requiresCorrectToolForDrops().strength(100.0F, ModBlocks.getObsidianBasedExplosionResistance(1000.0F))));
    public static final RegistryObject<FlameTrapBlock> FLAME_TRAP = registerWithEntity("flame_trap", () -> new FlameTrapBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).requiresCorrectToolForDrops().strength(100.0F, ModBlocks.getObsidianBasedExplosionResistance(1000.0F))));
    public static final RegistryObject<SpikyBallTrapBlock> SPIKY_BALL_TRAP = registerWithEntity("spiky_ball_trap", () -> new SpikyBallTrapBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).requiresCorrectToolForDrops().strength(100.0F, ModBlocks.getObsidianBasedExplosionResistance(1000.0F))));
    public static final RegistryObject<SpearTrapBlock> SPEAR_TRAP = registerWithEntity("spear_trap", () -> new SpearTrapBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).requiresCorrectToolForDrops().strength(100.0F, ModBlocks.getObsidianBasedExplosionResistance(1000.0F))));

    // 有效果的蜡烛
    public static final RegistryObject<EffectiveCandleBlock> WATER_CANDLE = registerCandle("water_candle", new MobEffectInstanceData(ModEffects.WATER_CANDLE, 100));
    public static final RegistryObject<EffectiveCandleBlock> PEACE_CANDLE = registerCandle("peace_candle", new MobEffectInstanceData(ModEffects.PEACE_CANDLE, 100));

    public static final RegistryObject<AnnouncementBoxBlock> ANNOUNCEMENT_BOX = BLOCKS.register("announcement_box", () -> new AnnouncementBoxBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F).noCollission().noOcclusion().sound(SoundType.METAL).isValidSpawn(Blocks::never)));
    public static final RegistryObject<AnnouncementBoxBlock.Wall> WALL_ANNOUNCEMENT_BOX = BLOCKS.register("wall_announcement_box", () -> new AnnouncementBoxBlock.Wall(BlockBehaviour.Properties.copy(ANNOUNCEMENT_BOX.get())));
    public static final DeferredItem<SignItem> ANNOUNCEMENT_BOX_ITEM = ModItems.BLOCK_ITEMS.register("announcement_box", () -> new SignItem(new Item.Properties(), ANNOUNCEMENT_BOX.get(), WALL_ANNOUNCEMENT_BOX.get()));
    public static final Supplier<BlockEntityType<AnnouncementBoxBlock.BEntity>> ANNOUNCEMENT_BOX_ENTITY = BLOCK_ENTITIES.register("announcement_box_entity", () -> BlockEntityType.Builder.of(AnnouncementBoxBlock.BEntity::new, ANNOUNCEMENT_BOX.get(), WALL_ANNOUNCEMENT_BOX.get()).build(DSL.remainderType()));
    public static final Supplier<BlockEntityType<AbstractMechanicalBlock.BEntity>> MECHANICAL_BLOCK_ENTITY = BLOCK_ENTITIES.register("mechanical_block_entity", () -> {
        Block[] validBlocks = MECHANICAL_BLOCKS.stream().map(RegistryObject::get).toArray(Block[]::new);
        MECHANICAL_BLOCKS = null;
        return BlockEntityType.Builder.of(AbstractMechanicalBlock.BEntity::new, validBlocks).build(DSL.remainderType());
    });

    public static final RegistryObject<TreeHolesBlock> TREE_HOLES_BLOCK = registerWithItem("tree_holes", () -> new TreeHolesBlock(BlockBehaviour.Properties.copy(Blocks.BARREL)));
    public static final Supplier<BlockEntityType<TreeHolesBlock.BEntity>> TREE_HOLES_ENTITY = BLOCK_ENTITIES.register("tree_holes", () -> BlockEntityType.Builder.of(TreeHolesBlock.BEntity::new, TREE_HOLES_BLOCK.get()).build(DSL.remainderType()));
    public static final RegistryObject<MagicMailBox> MAGIC_MAIL_BOX = registerWithItem("magic_mail_box", MagicMailBox::new);

    public static final RegistryObject<LockBlock> LOCK_BLOCK = registerWithItem("lock_block", () -> new LockBlock(BlockBehaviour.Properties.copy(Blocks.BEDROCK)));
    public static final Supplier<BlockEntityType<LockBlock.BEntity>> LOCK_BLOCK_ENTITY = BLOCK_ENTITIES.register("lock_block_entity", () -> BlockEntityType.Builder.of(LockBlock.BEntity::new, LOCK_BLOCK.get()).build(DSL.remainderType()));

    // 心灯，星星瓶
    public static final RegistryObject<HeartLanternBlock> HEART_LANTERN = registerWithItem("heart_lantern", () -> new HeartLanternBlock(BlockBehaviour.Properties.copy(LANTERN).lightLevel(state -> 7).mapColor(MapColor.COLOR_RED)), block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.WHITE, "tooltip.item.confluence.heart_lantern.0"));
    public static final Supplier<BlockEntityType<HeartLanternBlock.BEntity>> HEART_LANTERN_ENTITY = ModBlocks.BLOCK_ENTITIES.register("heart_lantern_entity", () -> BlockEntityType.Builder.of(HeartLanternBlock.BEntity::new, HEART_LANTERN.get()).build(DSL.remainderType()));

    public static final RegistryObject<StarInABottleBlock> STAR_IN_A_BOTTLE = registerWithItem("star_in_a_bottle", () -> new StarInABottleBlock(BlockBehaviour.Properties.copy(LANTERN).lightLevel(state -> 7).mapColor(MapColor.COLOR_YELLOW)), block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.WHITE, "tooltip.item.confluence.star_in_a_bottle.0"));
    public static final Supplier<BlockEntityType<StarInABottleBlock.BEntity>> STAR_IN_A_BOTTLE_ENTITY = ModBlocks.BLOCK_ENTITIES.register("star_in_a_bottle_entity", () -> BlockEntityType.Builder.of(StarInABottleBlock.BEntity::new, STAR_IN_A_BOTTLE.get()).build(DSL.remainderType()));

    public static final RegistryObject<BaseSoulInABottleBlock> SOUL_OF_FLIGHT_IN_A_BOTTLE = registerWithItem("soul_of_flight_in_a_bottle", () -> new BaseSoulInABottleBlock(BlockBehaviour.Properties.copy(LANTERN).lightLevel(state -> 7).mapColor(MapColor.COLOR_CYAN)));
    public static final RegistryObject<BaseSoulInABottleBlock> SOUL_OF_LIGHT_IN_A_BOTTLE = registerWithItem("soul_of_light_in_a_bottle", () -> new BaseSoulInABottleBlock(BlockBehaviour.Properties.copy(LANTERN).lightLevel(state -> 7).mapColor(MapColor.COLOR_PINK)));
    public static final RegistryObject<BaseSoulInABottleBlock> SOUL_OF_FRIGHT_IN_A_BOTTLE = registerWithItem("soul_of_fright_in_a_bottle", () -> new BaseSoulInABottleBlock(BlockBehaviour.Properties.copy(LANTERN).lightLevel(state -> 7).mapColor(MapColor.COLOR_RED)));
    public static final RegistryObject<BaseSoulInABottleBlock> SOUL_OF_NIGHT_IN_A_BOTTLE = registerWithItem("soul_of_night_in_a_bottle", () -> new BaseSoulInABottleBlock(BlockBehaviour.Properties.copy(LANTERN).lightLevel(state -> 7).mapColor(MapColor.COLOR_PURPLE)));
    public static final RegistryObject<BaseSoulInABottleBlock> SOUL_OF_MIGHT_IN_A_BOTTLE = registerWithItem("soul_of_might_in_a_bottle", () -> new BaseSoulInABottleBlock(BlockBehaviour.Properties.copy(LANTERN).lightLevel(state -> 7).mapColor(MapColor.COLOR_BLUE)));
    public static final RegistryObject<BaseSoulInABottleBlock> SOUL_OF_SIGHT_IN_A_BOTTLE = registerWithItem("soul_of_sight_in_a_bottle", () -> new BaseSoulInABottleBlock(BlockBehaviour.Properties.copy(LANTERN).lightLevel(state -> 7).mapColor(MapColor.COLOR_LIGHT_GREEN)));
    public static final RegistryObject<BaseSoulInABottleBlock> SOUL_OF_BRIGHT_IN_A_BOTTLE = registerWithItem("soul_of_bright_in_a_bottle", () -> new BaseSoulInABottleBlock(BlockBehaviour.Properties.copy(LANTERN).lightLevel(state -> 7).mapColor(MapColor.COLOR_YELLOW)));
    public static final RegistryObject<BaseSoulInABottleBlock> SOUL_OF_VOIGHT_IN_A_BOTTLE = registerWithItem("soul_of_voight_in_a_bottle", () -> new BaseSoulInABottleBlock(BlockBehaviour.Properties.copy(LANTERN).lightLevel(state -> 7).mapColor(MapColor.COLOR_PURPLE)));
    public static final Supplier<BlockEntityType<BaseSoulInABottleBlock.BEntity>> SOUL_BOTTLE_ENTITY = BLOCK_ENTITIES.register("soul_bottle_entity", () ->
            BlockEntityType.Builder.of(BaseSoulInABottleBlock.BEntity::new,
                    SOUL_OF_FLIGHT_IN_A_BOTTLE.get(),
                    SOUL_OF_LIGHT_IN_A_BOTTLE.get(),
                    SOUL_OF_FRIGHT_IN_A_BOTTLE.get(),
                    SOUL_OF_NIGHT_IN_A_BOTTLE.get(),
                    SOUL_OF_MIGHT_IN_A_BOTTLE.get(),
                    SOUL_OF_SIGHT_IN_A_BOTTLE.get(),
                    SOUL_OF_VOIGHT_IN_A_BOTTLE.get(),
                    SOUL_OF_BRIGHT_IN_A_BOTTLE.get()
            ).build(DSL.remainderType())
    );

    private static <B extends Block> RegistryObject<B> registerWithItem(String id, Supplier<B> supplier) {
        RegistryObject<B> block = BLOCKS.register(id, supplier);
        ModItems.BLOCK_ITEMS.registerSimpleBlockItem(block);
        return block;
    }

    private static <B extends Block> RegistryObject<B> registerWithItem(String id, Supplier<B> supplier, Function<B, BlockItem> function) {
        RegistryObject<B> block = BLOCKS.register(id, supplier);
        ModItems.BLOCK_ITEMS.register(id, () -> function.apply(block.get()));
        return block;
    }

    private static <B extends Block & EntityBlock & INetworkBlock> RegistryObject<B> registerWithEntity(String id, Supplier<B> supplier) {
        RegistryObject<B> holder = registerWithItem(id, supplier, block -> new TooltipBlockItem(block, new Item.Properties(), ModRarity.WHITE, "tooltip.item.confluence.wireable.0"));
        MECHANICAL_BLOCKS.add(holder);
        return holder;
    }

    private static <B extends Block & EntityBlock & INetworkBlock> RegistryObject<B> registerWithEntity(String id, Supplier<B> supplier, Function<B, BlockItem> function) {
        RegistryObject<B> holder = registerWithItem(id, supplier, function);
        MECHANICAL_BLOCKS.add(holder);
        return holder;
    }

    private static <B extends Block & EntityBlock> RegistryObject<B> registerGeoBoulder(String id, Supplier<B> supplier) {
        RegistryObject<B> holder = registerWithItem(id, supplier);
        return holder;
    }

    private static <B extends Block & EntityBlock> RegistryObject<B> registerGeoBoulder(String id, Supplier<B> supplier, Function<B, BlockItem> function) {
        RegistryObject<B> holder = registerWithItem(id, supplier, function);
        return holder;
    }

    private static RegistryObject<EffectiveCandleBlock> registerCandle(String id, MobEffectInstanceData... effectData) {
        return registerWithEntity(id,
                () -> new EffectiveCandleBlock(BlockBehaviour.Properties.of()
                        .mapColor(DyeColor.WHITE)
                        .noOcclusion()
                        .strength(0.1F)
                        .sound(SoundType.CANDLE)
                        .lightLevel(state -> state.getValue(EffectiveCandleBlock.LIT) ? 15 : 0)
                        .pushReaction(PushReaction.DESTROY), (float) 50, effectData),
                block -> new EffectiveCandleBlock.BItem(block, ModRarity.BLUE, List.of(
                        Component.translatable("tooltip.item.confluence." + id + ".0").withStyle(ChatFormatting.GRAY),
                        Component.translatable("tooltip.item.confluence.wireable.0").withStyle(ChatFormatting.GRAY)
                ), effectData)
        );
    }
}
