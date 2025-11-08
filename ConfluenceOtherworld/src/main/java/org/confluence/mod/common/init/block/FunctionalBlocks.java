package org.confluence.mod.common.init.block;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.util.Function3;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;
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
import org.confluence.mod.common.block.functional.boulder.AbstractBoulderBlock;
import org.confluence.mod.common.block.functional.boulder.BoulderBlock;
import org.confluence.mod.common.block.functional.boulder.RollingCactusBoulderBlock;
import org.confluence.mod.common.block.functional.crafting.*;
import org.confluence.mod.common.block.functional.crafting.LoomBlock;
import org.confluence.mod.common.block.functional.network.INetworkBlock;
import org.confluence.mod.common.block.natural.MagicMailBox;
import org.confluence.mod.common.block.natural.TreeHolesBlock;
import org.confluence.mod.common.entity.projectile.boulder.*;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.item.MaterialItems;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.item.common.EffectiveCandleItem;
import org.confluence.mod.common.item.food.ModFoodPropertiesBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.confluence.mod.common.init.block.ModBlocks.BLOCK_ENTITIES;

public class FunctionalBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Confluence.MODID);
    static List<DeferredBlock<? extends Block>> MECHANICAL_BLOCKS = new ArrayList<>();

    // 提取机
    public static final DeferredBlock<ExtractinatorBlock> EXTRACTINATOR = registerWithItem(
            "extractinator",
            () -> new ExtractinatorBlock(BlockBehaviour.Properties.of()
                    .strength(2.2F, 5.0F)
                    .requiresCorrectToolForDrops()),
            ExtractinatorBlock.BItem::new);
    public static final Supplier<BlockEntityType<ExtractinatorBlock.BEntity>> EXTRACTINATOR_ENTITY = BLOCK_ENTITIES.register(
            "extractinator_entity",
            () -> BlockEntityType.Builder.of(ExtractinatorBlock.BEntity::new, EXTRACTINATOR.get())
                    .build(DSL.remainderType()));

    // 祭坛
    public static final DeferredBlock<AltarBlock> DEMON_ALTAR = registerWithItem(
            "demon_altar",
            () -> new AltarBlock(BlockBehaviour.Properties.of()
                    .strength(3.0F, 18000.0F)
                    .lightLevel(state -> 5), AltarBlock.Variant.DEMON),
            AltarBlock.BItem::new);
    public static final DeferredBlock<AltarBlock> CRIMSON_ALTAR = registerWithItem(
            "crimson_altar",
            () -> new AltarBlock(BlockBehaviour.Properties.of()
                    .strength(3.0F, 18000.0F)
                    .lightLevel(state -> 5), AltarBlock.Variant.CRIMSON),
            AltarBlock.BItem::new);
    public static final Supplier<BlockEntityType<AltarBlock.BEntity>> ALTAR_BLOCK_ENTITY = BLOCK_ENTITIES.register(
            "altar_block_entity",
            () -> BlockEntityType.Builder.of(AltarBlock.BEntity::new, DEMON_ALTAR.get(), CRIMSON_ALTAR.get())
                    .build(DSL.remainderType()));

    // 天空磨床
    public static final DeferredBlock<SkyMillBlock> SKY_MILL = registerWithItem(
            "sky_mill",
            () -> new SkyMillBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GRINDSTONE)),
            SkyMillBlock.BItem::new);
    public static final Supplier<BlockEntityType<SkyMillBlock.BEntity>> SKY_MILL_ENTITY = BLOCK_ENTITIES.register(
            "sky_mill_entity",
            () -> BlockEntityType.Builder.of(SkyMillBlock.BEntity::new, SKY_MILL.get())
                    .build(DSL.remainderType()));

    // 重型工作台
    public static final DeferredBlock<HeavyWorkBenchBlock> HEAVY_WORK_BENCH = registerWithItem(
            "heavy_work_bench",
            () -> new HeavyWorkBenchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_BRICKS)),
            block -> new TooltipBlockItem(block,
                    new Item.Properties(),
                    ModRarity.WHITE,
                    "tooltip.item.confluence.heavy_work_bench.0"));

    // 锯木机
    public static final DeferredBlock<SawmillBlock> SAWMILL = registerWithItem(
            "sawmill",
            () -> new SawmillBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD)),
            block -> new TooltipBlockItem(block,
                    new Item.Properties(),
                    ModRarity.WHITE,
                    "tooltip.item.confluence.sawmill.0"));

    // 地狱熔炉
    public static final DeferredBlock<HellforgeBlock> HELLFORGE = registerWithItem(
            "hellforge",
            () -> new HellforgeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MAGMA_BLOCK)
                    .lightLevel(state -> state.getValue(BlockStateProperties.LIT) ? 15 : 7)
                    .noOcclusion()),
            block -> new TooltipBlockItem(block,
                    new Item.Properties(),
                    ModRarity.WHITE,
                    "tooltip.item.confluence.hellforge.0"));
    public static final Supplier<BlockEntityType<HellforgeBlock.BEntity>> HELLFORGE_ENTITY = BLOCK_ENTITIES.register(
            "hellforge_entity",
            () -> BlockEntityType.Builder.of(HellforgeBlock.BEntity::new, HELLFORGE.get())
                    .build(DSL.remainderType()));

    // 烹饪锅
    public static final DeferredBlock<CookingPotBlock> COOKING_POT = registerWithItem(
            "cooking_pot",
            () -> new CookingPotBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON)),
            block -> new CookingPotBlock.BItem(block, new Item.Properties()));
    public static final Supplier<BlockEntityType<CookingPotBlock.BEntity>> COOKING_POT_ENTITY = BLOCK_ENTITIES.register(
            "cooking_pot_entity",
            () -> BlockEntityType.Builder.of(CookingPotBlock.BEntity::new, COOKING_POT.get())
                    .build(DSL.remainderType()));

    // 炼金锅
    public static final DeferredBlock<BaseCauldronBlock> CAULDRON = registerWithItem(
            "cauldron",
            () -> new BaseCauldronBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON)));
    public static final Supplier<BlockEntityType<BaseCauldronBlock.BEntity>> CAULDRON_ENTITY = BLOCK_ENTITIES.register(
            "cauldron_entity",
            () -> BlockEntityType.Builder.of(BaseCauldronBlock.BEntity::new, CAULDRON.get())
                    .build(DSL.remainderType()));

    // 炼金桌
    public static final DeferredBlock<AlchemyTableBlock> ALCHEMY_TABLE = registerWithItem(
            "alchemy_table",
            () -> new AlchemyTableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ENCHANTING_TABLE)
                    .lightLevel(state -> 0)),
            block -> new TooltipBlockItem(block,
                    new Item.Properties(),
                    ModRarity.BLUE,
                    "tooltip.item.confluence.alchemy_table.0"));

    // 固化机
    public static final DeferredBlock<SolidifierBlock> SOLIDIFIER = registerWithItem(
            "solidifier",
            () -> new SolidifierBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)),
            block -> new TooltipBlockItem(block,
                    new Item.Properties(),
                    ModRarity.WHITE,
                    "tooltip.item.confluence.solidifier.0"));

    // 天气风向标
    public static final DeferredBlock<WeatherVaneBlock> WEATHER_VANE = registerWithItem(
            "weather_vane",
            () -> new WeatherVaneBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BARS)),
            block -> new BlockItem(block,
                    new Item.Properties()
                            .component(ConfluenceMagicLib.MOD_RARITY, ModRarity.BLUE)));
    public static final Supplier<BlockEntityType<WeatherVaneBlock.BEntity>> WEATHER_VANE_ENTITY = BLOCK_ENTITIES.register(
            "weather_vane_entity",
            () -> BlockEntityType.Builder.of(WeatherVaneBlock.BEntity::new, WEATHER_VANE.get())
                    .build(DSL.remainderType()));

    /**
     * @see org.confluence.mod.mixin.block.AnvilBlockMixin
     */
    // 铅砧
    public static final DeferredBlock<AnvilBlock> LEAD_ANVIL = registerWithItem(
            "lead_anvil",
            () -> new AnvilBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ANVIL)));
    public static final DeferredBlock<AnvilBlock> CHIPPED_LEAD_ANVIL = registerWithItem(
            "chipped_lead_anvil",
            () -> new AnvilBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHIPPED_ANVIL)));
    public static final DeferredBlock<AnvilBlock> DAMAGED_LEAD_ANVIL = registerWithItem(
            "damaged_lead_anvil",
            () -> new AnvilBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DAMAGED_ANVIL)));

    // 磨刀石
    public static final DeferredBlock<SharpeningStationBlock> SHARPENING_STATION = registerWithItem(
            "sharpening_station",
            () -> new SharpeningStationBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONECUTTER)),
            block -> new TooltipBlockItem(block,
                    new Item.Properties(),
                    ModRarity.BLUE,
                    "tooltip.item.confluence.sharpening_station.0"));

    // 弹药箱
    public static final DeferredBlock<AmmoBoxBlock> AMMO_BOX = registerWithItem(
            "ammo_box",
            () -> new AmmoBoxBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHEST)),
            block -> new TooltipBlockItem(block,
                    new Item.Properties(),
                    ModRarity.LIGHT_PURPLE,
                    "tooltip.item.confluence.ammo_box.0"));

    // 巫师工作台
    public static final DeferredBlock<BewitchingTableBlock> BEWITCHING_TABLE = registerWithItem(
            "bewitching_table",
            () -> new BewitchingTableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ENCHANTING_TABLE)
                    .lightLevel(state -> 0)),
            block -> new TooltipBlockItem(block,
                    new Item.Properties(),
                    ModRarity.BLUE,
                    "tooltip.item.confluence.bewitching_table.0"));
    public static final Supplier<BlockEntityType<BewitchingTableBlock.BEntity>> BEWITCHING_TABLE_ENTITY = BLOCK_ENTITIES.register(
            "bewitching_table_entity",
            () -> BlockEntityType.Builder.of(BewitchingTableBlock.BEntity::new, BEWITCHING_TABLE.get())
                    .build(DSL.remainderType()));

    // 木桶
    public static final DeferredBlock<KegBlock> KEG = registerWithItem(
            "keg",
            () -> new KegBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)),
            block -> new TooltipBlockItem(block,
                    new Item.Properties(),
                    ModRarity.WHITE,
                    "tooltip.item.confluence.keg.0"));

    // 水晶球
    public static final DeferredBlock<CrystalBallBlock> CRYSTAL_BALL = registerWithItem(
            "crystal_ball",
            () -> new CrystalBallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BEACON)
                    .sound(SoundType.AMETHYST)),
            block -> new BlockItem(block,
                    new Item.Properties()
                            .component(ConfluenceMagicLib.MOD_RARITY, ModRarity.ORANGE)));

    // 硬化砧
    public static final DeferredBlock<HardmodeAnvilBlock> MYTHRIL_ANVIL = registerWithItem(
            "mythril_anvil",
            () -> new HardmodeAnvilBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ANVIL)),
            block -> new TooltipBlockItem(block,
                    new Item.Properties(),
                    ModRarity.ORANGE,
                    "tooltip.item.confluence.hardmode_anvil.0"));
    public static final DeferredBlock<HardmodeAnvilBlock> ORICHALCUM_ANVIL = registerWithItem(
            "orichalcum_anvil",
            () -> new HardmodeAnvilBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ANVIL)),
            block -> new TooltipBlockItem(block,
                    new Item.Properties(),
                    ModRarity.ORANGE,
                    "tooltip.item.confluence.hardmode_anvil.0"));

    // 叶绿提取机
    public static final DeferredBlock<ChlorophyteExtractinatorBlock> CHLOROPHYTE_EXTRACTINATOR = registerWithItem(
            "chlorophyte_extractinator",
            () -> new ChlorophyteExtractinatorBlock(BlockBehaviour.Properties.of()
                    .strength(4.4F, 10.0F)
                    .requiresCorrectToolForDrops()),
            block -> new TooltipBlockItem(block,
                    new Item.Properties(),
                    ModRarity.LIME,
                    TooltipItem.getTooltipsFromString("chlorophyte_extractinator", 3, ChatFormatting.GRAY)));

    // 混合器
    public static final DeferredBlock<SingleItemStackSwapperBlock> BLEND_O_MATIC = registerWithItem(
            "blend_o_matic",
            () -> new SingleItemStackSwapperBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON), 1,
                    itemStack -> itemStack.is(MaterialItems.RAW_ASPHALT) ? DecorativeBlocks.ASPHALT_BLOCK.toStack() : ItemStack.EMPTY),
            block -> new TooltipBlockItem(block,
                    new Item.Properties(),
                    ModRarity.WHITE,
                    "tooltip.item.confluence.blend_o_matic.0"));
    public static final DeferredBlock<SingleItemStackSwapperBlock> MEAT_GRINDER = registerWithItem(
            "meat_grinder",
            () -> new SingleItemStackSwapperBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON), 2,
                    itemStack -> {
                        if (itemStack.is(NatureBlocks.COBBLED_CRIMSTONE.asItem())) {
                            return DecorativeBlocks.FLESH_BLOCK.toStack();
                        } else if (itemStack.is(NatureBlocks.COBBLED_EBONSTONE.asItem()))
                            return DecorativeBlocks.LESION_BLOCK.toStack();
                        return ItemStack.EMPTY;
                    }),
            block -> new TooltipBlockItem(block,
                    new Item.Properties(),
                    ModRarity.WHITE,
                    "tooltip.item.confluence.meat_grinder.0"));

    // 硬化熔炉
    public static final DeferredBlock<AdamantiteForgeBlock> ADAMANTITE_FORGE = registerWithItem(
            "adamantite_forge",
            AdamantiteForgeBlock::new,
            block -> new TooltipBlockItem(block,
                    new Item.Properties(),
                    ModRarity.ORANGE,
                    "tooltip.item.confluence.hardmode_forge.0"));
    public static final DeferredBlock<TitaniumForgeBlock> TITANIUM_FORGE = registerWithItem(
            "titanium_forge",
            TitaniumForgeBlock::new,
            block -> new TooltipBlockItem(block,
                    new Item.Properties(),
                    ModRarity.ORANGE,
                    "tooltip.item.confluence.hardmode_forge.0"));
    public static final Supplier<BlockEntityType<HardmodeForgeBlock.BEntity>> HARDMODE_FORGE_ENTITY = BLOCK_ENTITIES.register(
            "hardmode_forge_entity",
            () -> BlockEntityType.Builder.of(HardmodeForgeBlock.BEntity::new, ADAMANTITE_FORGE.get(), TITANIUM_FORGE.get())
                    .build(DSL.remainderType()));

    // 织布机
    public static final DeferredBlock<LoomBlock> LOOM = registerWithItem(
            "loom",
            LoomBlock::new,
            block -> new TooltipBlockItem(block,
                    new Item.Properties(),
                    ModRarity.WHITE,
                    "tooltip.item.confluence.loom.0"));
    public static final Supplier<BlockEntityType<LoomBlock.BEntity>> LOOM_ENTITY = BLOCK_ENTITIES.register(
            "loom_entity",
            () -> BlockEntityType.Builder.of(LoomBlock.BEntity::new, LOOM.get())
                    .build(DSL.remainderType()));

    // 染料缸
    public static final DeferredBlock<DyeVatBlock> DYE_VAT = registerWithItem(
            "dye_vat",
            DyeVatBlock::new,
            block -> new TooltipBlockItem(block,
                    new Item.Properties(),
                    ModRarity.WHITE,
                    "tooltip.item.confluence.dye_vat.0"));

    // 生命篝火
    public static final DeferredBlock<LifeCampfireBlock> LIFE_CAMPFIRE = registerWithItem(
            "life_campfire",
            () -> new LifeCampfireBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAMPFIRE)),
            block -> new TooltipBlockItem(block,
                    new Item.Properties(),
                    ModRarity.WHITE,
                    "tooltip.item.confluence.life_campfire.0"));

    // 储蓄罐
    public static final DeferredBlock<PiggyBankBlock> PIGGY_BANK = registerWithItem(
            "piggy_bank",
            () -> new PiggyBankBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DECORATED_POT)),
            block -> new TooltipBlockItem(block,
                    new Item.Properties(),
                    ModRarity.WHITE,
                    TooltipItem.getTooltipsFromString("piggy_bank", 2, ChatFormatting.GRAY)));
    public static final Supplier<BlockEntityType<PiggyBankBlock.BEntity>> PIGGY_BANK_ENTITY = BLOCK_ENTITIES.register(
            "piggy_bank_entity",
            () -> BlockEntityType.Builder.of(PiggyBankBlock.BEntity::new, PIGGY_BANK.get())
                    .build(DSL.remainderType()));

    // 保险箱
    public static final DeferredBlock<SafeBlock> SAFE = registerWithItem(
            "safe",
            () -> new SafeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)),
            block -> new TooltipBlockItem(block,
                    new Item.Properties(),
                    ModRarity.WHITE,
                    TooltipItem.getTooltipsFromString("safe", 2, ChatFormatting.GRAY)));
    public static final Supplier<BlockEntityType<SafeBlock.BEntity>> SAFE_ENTITY = BLOCK_ENTITIES.register(
            "safe_entity",
            () -> BlockEntityType.Builder.of(SafeBlock.BEntity::new, SAFE.get())
                    .build(DSL.remainderType()));

    // 回响方块
    public static final DeferredBlock<EchoBlock> ECHO_BLOCK = registerWithItem(
            "echo_block",
            EchoBlock::new,
            block -> new TooltipBlockItem(block,
                    new Item.Properties(),
                    ModRarity.BLUE,
                    "tooltip.item.confluence.echo_block.0"));

    // 永恒动力铁轨
    public static final DeferredBlock<EverPoweredRailBlock> EVER_POWERED_RAIL = registerWithItem(
            "ever_powered_rail",
            () -> new EverPoweredRailBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ACTIVATOR_RAIL)));

    // 陷阱
    public static final DeferredBlock<StepOnTrapBlock> SHIMMER_TRAP = registerWithItem(
            "shimmer_trap",
            () -> new StepOnTrapBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DISPENSER),
                    StepOnTrapBlock.SHIMMER));
    public static final DeferredBlock<StepOnTrapBlock> GRAVITATION_TRAP = registerWithItem(
            "gravitation_trap",
            () -> new StepOnTrapBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DISPENSER),
                    StepOnTrapBlock.GRAVITATION));
    public static final DeferredBlock<StepOnTrapBlock> PNEUMATIC_TRAP = registerWithItem(
            "pneumatic_trap",
            () -> new StepOnTrapBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DISPENSER),
                    StepOnTrapBlock.PNEUMATIC));

    // 尖刺
    public static final DeferredBlock<SpikeBlock> SPIKE = registerWithItem(
            "spike",
            () -> new SpikeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BARS), 12));
    public static final DeferredBlock<SpikeBlock> WOODEN_SPIKE = registerWithItem(
            "wooden_spike",
            () -> new SpikeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.JUNGLE_PLANKS), 16));

    // 易碎方块
    public static final DeferredBlock<FragileBlock> FRAGILE_SANDSTONE = registerWithItem(
            "fragile_sandstone",
            () -> new FragileBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE),
                    Blocks.SANDSTONE::defaultBlockState));
    public static final DeferredBlock<FragileBlock> FRAGILE_BLUE_BRICKS = registerWithItem(
            "fragile_blue_bricks",
            () -> new FragileBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS),
                    () -> DecorativeBlocks.BLUE_BRICKS.get().defaultBlockState()));
    public static final DeferredBlock<FragileBlock> FRAGILE_GREEN_BRICKS = registerWithItem(
            "fragile_green_bricks",
            () -> new FragileBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS),
                    () -> DecorativeBlocks.GREEN_BRICKS.get().defaultBlockState()));
    public static final DeferredBlock<FragileBlock> FRAGILE_PINK_BRICKS = registerWithItem(
            "fragile_pink_bricks",
            () -> new FragileBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS),
                    () -> DecorativeBlocks.PINK_BRICKS.get().defaultBlockState()));
    public static final DeferredBlock<EnchantedFragileBricksBlock> ENCHANTED_FRAGILE_BLUE_BRICKS = registerWithItem(
            "enchanted_fragile_blue_bricks",
            () -> new EnchantedFragileBricksBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS),
                    () -> DecorativeBlocks.ENCHANTED_BLUE_BRICKS.get().defaultBlockState()));
    public static final DeferredBlock<EnchantedFragileBricksBlock> ENCHANTED_FRAGILE_GREEN_BRICKS = registerWithItem(
            "enchanted_fragile_green_bricks",
            () -> new EnchantedFragileBricksBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS),
                    () -> DecorativeBlocks.ENCHANTED_GREEN_BRICKS.get().defaultBlockState()));
    public static final DeferredBlock<EnchantedFragileBricksBlock> ENCHANTED_FRAGILE_PINK_BRICKS = registerWithItem(
            "enchanted_fragile_pink_bricks",
            () -> new EnchantedFragileBricksBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS),
                    () -> DecorativeBlocks.ENCHANTED_PINK_BRICKS.get().defaultBlockState()));

    // 幽匿陷阱
    public static final DeferredBlock<SculkTrapBlock> SCULK_TRAP = registerWithItem(
            "sculk_trap",
            () -> new SculkTrapBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SCULK_SENSOR)));

    // 气球机
    public static final DeferredBlock<SillyBalloonMachineBlock> SILLY_BALLOON_MACHINE = registerWithItem(
            "silly_balloon_machine",
            () -> new SillyBalloonMachineBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));
    public static final Supplier<BlockEntityType<SillyBalloonMachineBlock.BEntity>> SILLY_BALLOON_MACHINE_ENTITY = BLOCK_ENTITIES.register(
            "silly_balloon_machine_entity",
            () -> BlockEntityType.Builder.of(SillyBalloonMachineBlock.BEntity::new, SILLY_BALLOON_MACHINE.get())
                    .build(DSL.remainderType()));

    // 压力板
    public static final DeferredBlock<BehaviourPressurePlateBlock> PLAYER_PRESSURE_PLATE = registerWithEntity(
            "player_pressure_plate",
            () -> new BehaviourPressurePlateBlock(BehaviourPressurePlateBlock.PLAYER,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE),
                    BlockSetType.IRON));
    public static final DeferredBlock<SignalPressurePlateBlock> STONE_PRESSURE_PLATE = registerWithEntity(
            "stone_pressure_plate",
            () -> new SignalPressurePlateBlock(BlockSetType.STONE,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_PRESSURE_PLATE)));
    public static final DeferredBlock<SignalPressurePlateBlock> DEEPSLATE_PRESSURE_PLATE = registerWithEntity(
            "deepslate_pressure_plate",
            () -> new SignalPressurePlateBlock(BlockSetType.STONE,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_PRESSURE_PLATE)
                            .mapColor(MapColor.DEEPSLATE)));
    public static final DeferredBlock<FragilePressureBlock> STONE_PRESSURE_BLOCK = registerWithEntity(
            "stone_pressure_block",
            () -> new FragilePressureBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE),
                    BlockSetType.STONE,
                    Blocks.STONE::defaultBlockState));
    public static final DeferredBlock<FragilePressureBlock> DEEPSLATE_PRESSURE_BLOCK = registerWithEntity(
            "deepslate_pressure_block",
            () -> new FragilePressureBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE),
                    BlockSetType.STONE,
                    Blocks.DEEPSLATE::defaultBlockState));

    // 爆炸物
    public static final DeferredBlock<InstantExplosionBlock> INSTANTANEOUS_EXPLOSION_TNT = registerWithEntity(
            "instantaneous_explosion_tnt",
            InstantExplosionBlock::new);

    // 开关
    public static final DeferredBlock<SwitchBlock> SWITCH = registerWithEntity(
            "switch",
            SwitchBlock::new);
    public static final DeferredBlock<SwitchBlock> LEVER = registerWithEntity(
            "lever",
            SwitchBlock::new);

    // 信号适配器
    public static final DeferredBlock<SignalAdapterBlock> SIGNAL_ADAPTER = registerWithEntity(
            "signal_adapter",
            SignalAdapterBlock::new);

    // 飞镖陷阱
    public static final DeferredBlock<DartTrapBlock> DART_TRAP = registerWithEntity(
            "dart_trap",
            () -> new DartTrapBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DISPENSER)));
    public static final DeferredBlock<DartTrapBlock> STONE_DART_TRAP = registerWithEntity(
            "stone_dart_trap",
            () -> new DartTrapBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DISPENSER)));
    public static final DeferredBlock<DartTrapBlock> DEEPSLATE_DART_TRAP = registerWithEntity(
            "deepslate_dart_trap",
            () -> new DartTrapBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DISPENSER)));

    // 定时器
    public static final DeferredBlock<TimersBlock> TIMERS_BLOCK_1_1 = registerWithEntity(
            "timers_1_1",
            () -> new TimersBlock(20)); // 1s
    public static final DeferredBlock<TimersBlock> TIMERS_BLOCK_3_1 = registerWithEntity(
            "timers_3_1",
            () -> new TimersBlock(60)); // 3s
    public static final DeferredBlock<TimersBlock> TIMERS_BLOCK_5_1 = registerWithEntity(
            "timers_5_1",
            () -> new TimersBlock(100)); // 5s
    public static final DeferredBlock<TimersBlock> TIMERS_BLOCK_1_2 = registerWithEntity(
            "timers_1_2",
            () -> new TimersBlock(10)); // 1/2s
    public static final DeferredBlock<TimersBlock> TIMERS_BLOCK_1_4 = registerWithEntity(
            "timers_1_4",
            () -> new TimersBlock(5)); // 1/4s

    // 间歇泉
    public static final DeferredBlock<GeyserBlock> GEYSER_BLOCK = registerWithEntity(
            "geyser_block",
            GeyserBlock::new);

    // 巨石
    public static final DeferredBlock<AbstractBoulderBlock<?>> NORMAL_BOULDER = registerBoulder(
            "normal_boulder",
            BoulderBlock::new);
    public static final DeferredBlock<AbstractBoulderBlock<?>> OAK_LOG_BOULDER = registerBoulder(
            "oak_log_boulder",
            BoulderBlock::new);
    public static final DeferredBlock<AbstractBoulderBlock<?>> FOLLOWER_BOULDER = registerBoulder(
            "follower_boulder",
            BoulderBlock::new,
            FollowerBoulderEntity::new);
    public static final DeferredBlock<AbstractBoulderBlock<?>> EXPLODE_BOULDER = registerBoulder(
            "explode_boulder",
            BoulderBlock::new,
            ExplodeBoulderEntity::new);
    public static final DeferredBlock<AbstractBoulderBlock<?>> ROLLING_CACTUS_BOULDER = registerBoulder(
            "rolling_cactus_boulder",
            RollingCactusBoulderBlock::new);

    // 起爆器
    public static final DeferredBlock<DetonatorBlock> DETONATOR = registerWithEntity(
            "detonator",
            () -> new DetonatorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_TRAPDOOR)));

    // 机械易碎方块
    public static final DeferredBlock<MechanicalFragileBlock> MECHANICAL_FRAGILE_SANDSTONE = registerWithEntity(
            "mechanical_fragile_sandstone",
            () -> new MechanicalFragileBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE),
                    Blocks.SANDSTONE::defaultBlockState));
    public static final DeferredBlock<MechanicalFragileBlock> MECHANICAL_FRAGILE_OBSIDIAN_BRICKS = registerWithEntity(
            "mechanical_fragile_obsidian_bricks",
            () -> new MechanicalFragileBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS),
                    DecorativeBlocks.OBSIDIAN_BRICKS.get()::defaultBlockState));

    // 地雷
    public static final DeferredBlock<LandMineBlock> LAND_MINE = registerWithEntity(
            "land_mine",
            () -> new LandMineBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TNT)));

    // 超级陷阱
    public static final DeferredBlock<SuperDartTrapBlock> SUPER_DART_TRAP = registerWithEntity(
            "super_dart_trap",
            () -> new SuperDartTrapBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .requiresCorrectToolForDrops()
                    .strength(100.0F, ModBlocks.getObsidianBasedExplosionResistance(1000.0F))));
    public static final DeferredBlock<FlameTrapBlock> FLAME_TRAP = registerWithEntity(
            "flame_trap",
            () -> new FlameTrapBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .requiresCorrectToolForDrops()
                    .strength(100.0F, ModBlocks.getObsidianBasedExplosionResistance(1000.0F))));
    public static final DeferredBlock<SpikyBallTrapBlock> SPIKY_BALL_TRAP = registerWithEntity(
            "spiky_ball_trap",
            () -> new SpikyBallTrapBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .requiresCorrectToolForDrops()
                    .strength(100.0F, ModBlocks.getObsidianBasedExplosionResistance(1000.0F))));
    public static final DeferredBlock<SpearTrapBlock> SPEAR_TRAP = registerWithEntity(
            "spear_trap",
            () -> new SpearTrapBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .requiresCorrectToolForDrops()
                    .strength(100.0F, ModBlocks.getObsidianBasedExplosionResistance(1000.0F))));

    // 公告牌
    public static final DeferredBlock<AnnouncementBoxBlock> ANNOUNCEMENT_BOX = BLOCKS.register(
            "announcement_box",
            () -> new AnnouncementBoxBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0F)
                    .noCollission()
                    .noOcclusion()
                    .sound(SoundType.METAL)
                    .isValidSpawn(Blocks::never)));
    public static final DeferredBlock<AnnouncementBoxBlock.Wall> WALL_ANNOUNCEMENT_BOX = BLOCKS.register(
            "wall_announcement_box",
            () -> new AnnouncementBoxBlock.Wall(BlockBehaviour.Properties.ofFullCopy(ANNOUNCEMENT_BOX.get())));
    public static final DeferredItem<SignItem> ANNOUNCEMENT_BOX_ITEM = ModItems.BLOCK_ITEMS.register(
            "announcement_box",
            () -> new SignItem(new Item.Properties(), ANNOUNCEMENT_BOX.get(), WALL_ANNOUNCEMENT_BOX.get()));
    public static final Supplier<BlockEntityType<AnnouncementBoxBlock.BEntity>> ANNOUNCEMENT_BOX_ENTITY = BLOCK_ENTITIES.register(
            "announcement_box_entity",
            () -> BlockEntityType.Builder.of(AnnouncementBoxBlock.BEntity::new, ANNOUNCEMENT_BOX.get(), WALL_ANNOUNCEMENT_BOX.get())
                    .build(DSL.remainderType()));
    public static final Supplier<BlockEntityType<AbstractMechanicalBlock.BEntity>> MECHANICAL_BLOCK_ENTITY = BLOCK_ENTITIES.register(
            "mechanical_block_entity",
            () -> {
                Block[] validBlocks = MECHANICAL_BLOCKS.stream().map(DeferredBlock::get).toArray(Block[]::new);
                MECHANICAL_BLOCKS = null;
                return BlockEntityType.Builder.of(AbstractMechanicalBlock.BEntity::new, validBlocks)
                        .build(DSL.remainderType());
            });

    // 树洞
    public static final DeferredBlock<TreeHolesBlock> TREE_HOLES_BLOCK = registerWithItem(
            "tree_holes",
            () -> new TreeHolesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)));
    public static final Supplier<BlockEntityType<TreeHolesBlock.BEntity>> TREE_HOLES_ENTITY = BLOCK_ENTITIES.register(
            "tree_holes",
            () -> BlockEntityType.Builder.of(TreeHolesBlock.BEntity::new, TREE_HOLES_BLOCK.get())
                    .build(DSL.remainderType()));

    // 魔法邮箱
    public static final DeferredBlock<MagicMailBox> MAGIC_MAIL_BOX = registerWithItem(
            "magic_mail_box",
            MagicMailBox::new);

    // 锁块
    public static final DeferredBlock<LockBlock> LOCK_BLOCK = registerWithItem(
            "lock_block",
            () -> new LockBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BEDROCK)));
    public static final Supplier<BlockEntityType<LockBlock.BEntity>> LOCK_BLOCK_ENTITY = BLOCK_ENTITIES.register(
            "lock_block_entity",
            () -> BlockEntityType.Builder.of(LockBlock.BEntity::new, LOCK_BLOCK.get())
                    .build(DSL.remainderType()));

    // 有效果的蜡烛
    public static final DeferredBlock<EffectiveCandleBlock> WATER_CANDLE = registerCandle(
            "water_candle",
            50,
            ModFoodPropertiesBuilder.EffectData.of(ModEffects.WATER_CANDLE, 1));
    public static final DeferredBlock<EffectiveCandleBlock> PEACE_CANDLE = registerCandle(
            "peace_candle",
            50,
            ModFoodPropertiesBuilder.EffectData.of(ModEffects.PEACE_CANDLE, 1));

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

    static DeferredBlock<EffectiveCandleBlock> registerCandle(
            String id,
            float scope,
            ModFoodPropertiesBuilder.EffectData... effectData) {
        return registerWithEntity(id, () -> new EffectiveCandleBlock(BlockBehaviour.Properties.of(), scope, effectData));
    }

    static DeferredBlock<EffectiveCandleBlock> registerCandle(
            String id,
            BlockBehaviour.Properties properties,
            float scope,
            ModFoodPropertiesBuilder.EffectData... effectData) {
        return registerWithEntity(id, () -> new EffectiveCandleBlock(properties, scope, effectData));
    }

    static DeferredBlock<EffectiveCandleBlock> registerCandle(
            String id,
            BlockBehaviour.Properties properties,
            float scope,
            EffectiveCandleBlock.TickEffect tickEffects) {
        return registerWithEntity(id, () -> new EffectiveCandleBlock(properties, scope, tickEffects));
    }

    static <B extends AbstractBoulderBlock<?>> DeferredBlock<B> registerBoulder(
				String id,
				Supplier<B> block) {
        return registerWithEntity(id, block);
    }

    static <B extends AbstractBoulderBlock<?>> DeferredBlock<B> registerBoulder(
            String id,
            Function<BoulderEntity.Builder, B> block,
            BoulderEntity.Builder builder) {
        return registerWithEntity(id, ()-> block.apply(builder));
    }

    static <B extends AbstractBoulderBlock<? extends E>, E extends AbstractBoulderEntity> DeferredBlock<B> registerBoulder(
				String id,
				Function<AbstractBoulderBlock.BoulderEntityFactory<E>, B> block,
				AbstractBoulderBlock.BoulderEntityFactory<E> entity) {
        return registerWithEntity(id, () -> block.apply(entity));
    }

    static <B extends AbstractBoulderBlock<? extends E>, E extends AbstractBoulderEntity> DeferredBlock<B> registerBoulder(
		    String id,
		    BiFunction<AbstractBoulderBlock.BoulderEntityFactory<E>, BoulderEntity.Builder, B> block,
		    AbstractBoulderBlock.BoulderEntityFactory<E> entity,
		    BoulderEntity.Builder builder) {
		    return registerWithEntity(id, () -> block.apply(entity, builder));
    }

		static <B extends AbstractBoulderBlock<? extends E>, E extends AbstractBoulderEntity> DeferredBlock<B> registerBoulder(
		    String id,
		    Function3<BlockBehaviour.Properties, AbstractBoulderBlock.BoulderEntityFactory<E>, BoulderEntity.Builder, B> block,
		    AbstractBoulderBlock.BoulderEntityFactory<E> entity,
		    BlockBehaviour.Properties properties,
		    BoulderEntity.Builder builder) {
		    return registerWithEntity(id, () -> block.apply(properties, entity, builder));
    }

		static <B extends AbstractBoulderBlock<? extends E>, E extends AbstractBoulderEntity> DeferredBlock<B> baseRegisterBoulder(
				String id,
				Function<AbstractBoulderBlock.BoulderEntityFactory<E>, B> block,
				Function3<Level, Vec3, BlockState, E> entity) {
			return registerWithEntity(id, () -> block.apply(
					(level, position, blockState, builder) -> entity.apply(level, position, blockState)
			));
		}

		static <B extends AbstractBoulderBlock<? extends E>, E extends AbstractBoulderEntity> DeferredBlock<B> baseRegisterBoulder(
		    String id,
		    BiFunction<BlockBehaviour.Properties, AbstractBoulderBlock.BoulderEntityFactory<E>, B> block,
		    Function3<Level, Vec3, BlockState, E> entity,
		    BlockBehaviour.Properties properties) {
		    return registerWithEntity(id, () -> block.apply(properties, (level, position, blockState, builder1) -> entity.apply(level, position, blockState)));
    }
}
