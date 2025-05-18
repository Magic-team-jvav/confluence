package org.confluence.mod.common.init;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry;
import org.confluence.lib.common.fluid.FluidTriple;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.saved.GamePhase;
import org.confluence.mod.common.init.block.*;
import org.confluence.mod.common.init.item.*;

import static org.confluence.mod.api.event.ShimmerEntityTransmutationEvent.addEntity;
import static org.confluence.mod.api.event.ShimmerItemTransmutationEvent.addItem;
import static org.confluence.mod.api.event.ShimmerItemTransmutationEvent.blackList;
import static org.confluence.mod.common.init.item.AccessoryItems.*;
import static org.confluence.terra_curio.common.init.TCItems.*;

public final class ModFluids {
    public static final FluidTriple HONEY = FluidTriple.builder(Confluence.asResource("honey"))
            .typeProperties(properties -> properties
                    .density(2000)
                    .canSwim(false)
                    .viscosity(3000)
                    .motionScale(0.0003)
                    .canExtinguish(true)
                    .supportsBoating(true)
                    .rarity(Rarity.UNCOMMON)
                    .fallDistanceModifier(0.2F)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                    .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
                    .addDripstoneDripping(
                            PointedDripstoneBlock.LAVA_TRANSFER_PROBABILITY_PER_RANDOM_TICK,
                            ParticleTypes.DRIPPING_DRIPSTONE_WATER,
                            ModBlocks.HONEY_CAULDRON.get(),
                            SoundEvents.POINTED_DRIPSTONE_DRIP_LAVA_INTO_CAULDRON
                    )
            ).baseProperties(properties -> properties
                    .block(ModBlocks.HONEY)
                    .bucket(ToolItems.HONEY_BUCKET)
            ).build();
    public static final FluidTriple SHIMMER = FluidTriple.builder(Confluence.asResource("shimmer"))
            .typeProperties(properties -> properties
                    .density(800)
                    .lightLevel(10)
                    .viscosity(800)
                    .canSwim(false)
                    .motionScale(0.02)
                    .canExtinguish(true)
                    .supportsBoating(true)
                    .rarity(Rarity.EPIC)
                    .fallDistanceModifier(0.0F)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                    .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
                    .addDripstoneDripping(
                            PointedDripstoneBlock.LAVA_TRANSFER_PROBABILITY_PER_RANDOM_TICK,
                            ParticleTypes.DRIPPING_DRIPSTONE_WATER,
                            ModBlocks.AETHERIUM_CAULDRON.get(),
                            SoundEvents.POINTED_DRIPSTONE_DRIP_WATER_INTO_CAULDRON
                    )
            ).baseProperties(properties -> properties
                    .block(ModBlocks.SHIMMER)
                    .bucket(() -> Items.AIR)
            ).build();

    public static void registerInteraction() {
        FluidInteractionRegistry.addInteraction(HONEY.type().get(), new FluidInteractionRegistry.InteractionInformation(
                NeoForgeMod.WATER_TYPE.value(), fluidState -> fluidState.isSource() ? Blocks.HONEY_BLOCK.defaultBlockState() : NatureBlocks.THIN_HONEY_BLOCK.get().defaultBlockState()
        ));
        FluidInteractionRegistry.addInteraction(HONEY.type().get(), new FluidInteractionRegistry.InteractionInformation(
                NeoForgeMod.LAVA_TYPE.value(), fluidState -> fluidState.isSource() ? DecorativeBlocks.CRISPY_HONEY_BLOCK.get().defaultBlockState() : NatureBlocks.LOOSE_HONEY_BLOCK.get().defaultBlockState()
        ));
        FluidInteractionRegistry.addInteraction(SHIMMER.type().get(), new FluidInteractionRegistry.InteractionInformation(
                NeoForgeMod.WATER_TYPE.value(), fluidState -> fluidState.isSource() ? NatureBlocks.AETHERIUM_BLOCK.get().defaultBlockState() : NatureBlocks.DARK_AETHERIUM_BLOCK.get().defaultBlockState()
        ));
        FluidInteractionRegistry.addInteraction(SHIMMER.type().get(), new FluidInteractionRegistry.InteractionInformation(
                NeoForgeMod.LAVA_TYPE.value(), fluidState -> fluidState.isSource() ? NatureBlocks.AETHERIUM_BLOCK.get().defaultBlockState() : NatureBlocks.DARK_AETHERIUM_BLOCK.get().defaultBlockState()
        ));
        FluidInteractionRegistry.addInteraction(SHIMMER.type().get(), new FluidInteractionRegistry.InteractionInformation(
                HONEY.type().get(), fluidState -> fluidState.isSource() ? NatureBlocks.AETHERIUM_BLOCK.get().defaultBlockState() : NatureBlocks.DARK_AETHERIUM_BLOCK.get().defaultBlockState()
        ));
    }

    public static void registerShimmerTransform() {
        // 黑名单
        blackList(ItemTags.STAIRS);
        blackList(AccessoryItems.BAND_OF_STARPOWER.get());
        // 顶替
        addItem(ItemTags.WOOL, Items.WHITE_WOOL, 1);
        addItem(ItemTags.WOOL_CARPETS, Items.WHITE_CARPET, 1);
        addItem(Items.CRAFTING_TABLE, Items.OAK_PLANKS, 1);
        // 下界合金装备嬗变
        addItem(Items.NETHERITE_CHESTPLATE, Items.NETHERITE_INGOT, 1);
        addItem(Items.NETHERITE_HELMET, Items.NETHERITE_INGOT, 1);
        addItem(Items.NETHERITE_LEGGINGS, Items.NETHERITE_INGOT, 1);
        addItem(Items.NETHERITE_BOOTS, Items.NETHERITE_INGOT, 1);
        addItem(Items.NETHERITE_SWORD, Items.NETHERITE_INGOT, 1);
        addItem(Items.NETHERITE_SHOVEL, Items.NETHERITE_INGOT, 1);
        addItem(Items.NETHERITE_HOE, Items.NETHERITE_INGOT, 1);
        addItem(Items.NETHERITE_AXE, Items.NETHERITE_INGOT, 1);
        addItem(Items.NETHERITE_PICKAXE, Items.NETHERITE_INGOT, 1);
        // 饰品转化
        addItem(BALLOON_PUFFERFISH.get(), SHINY_RED_BALLOON.get());
        addItem(MAGMA_STONE.get(), LAVA_CHARM.get());
        addItem(LAVA_CHARM.get(), MAGMA_STONE.get());
        addItem(SEXTANT.get(), WEATHER_RADIO.get());
        addItem(WEATHER_RADIO.get(), FISHERMANS_POCKET_GUIDE.get());
        addItem(FISHERMANS_POCKET_GUIDE.get(), SEXTANT.get());
        addItem(BEZOAR.get(), ADHESIVE_BANDAGE.get());
        addItem(ADHESIVE_BANDAGE.get(), BEZOAR.get());
        addItem(ARMOR_POLISH.get(), VITAMINS.get());
        addItem(VITAMINS.get(), ARMOR_POLISH.get());
        addItem(POCKET_MIRROR.get(), BLINDFOLD.get());
        addItem(BLINDFOLD.get(), POCKET_MIRROR.get());
        addItem(FAST_CLOCK.get(), TRIFOLD_MAP.get());
        addItem(TRIFOLD_MAP.get(), FAST_CLOCK.get());
        addItem(NAZAR.get(), MEGAPHONE.get());
        addItem(MEGAPHONE.get(), NAZAR.get());
        addItem(HIGH_TEST_FISHING_LINE.get(), ANGLER_EARRING.get());
        addItem(ANGLER_EARRING.get(), TACKLE_BOX.get());
        addItem(TACKLE_BOX.get(), HIGH_TEST_FISHING_LINE.get());
        addItem(STAR_CLOAK.get(), CHROMATIC_CLOAK.get());
        addItem(SUMMONER_EMBLEM.get(), WARRIOR_EMBLEM.get());
        addItem(WARRIOR_EMBLEM.get(), RANGER_EMBLEM.get());
        addItem(RANGER_EMBLEM.get(), SORCERER_EMBLEM.get());
        addItem(SORCERER_EMBLEM.get(), SUMMONER_EMBLEM.get());
        // todo 火把转化
        // 临时转化
        addItem(Blocks.SCULK.asItem(), FunctionalBlocks.ECHO_BLOCK.get().asItem());
        addItem(PaintItems.ECHO_COATING.get(), PaintItems.ILLUMINANT_COATING.get());
        addItem(PaintItems.ILLUMINANT_COATING.get(), PaintItems.NEGATIVE_PAINT.get());
        addItem(PaintItems.NEGATIVE_PAINT.get(), PaintItems.SHADOW_PAINT.get());
        addItem(PaintItems.SHADOW_PAINT.get(), PaintItems.ECHO_COATING.get());
        addItem(AccessoryItems.MECHANICAL_LENS.get(), AccessoryItems.SPECTRE_GOGGLES.get());
        // 微光箭转化
        addItem(ItemTags.ARROWS, ArrowItems.SHIMMER_ARROW.get(), 1);
        // 匣子转化
        addItem(CrateBlocks.PEARLWOOD_CRATE.get().asItem(), CrateBlocks.WOODEN_CRATE.get().asItem());
        addItem(CrateBlocks.MYTHRIL_CRATE.get().asItem(), CrateBlocks.IRON_CRATE.get().asItem());
        addItem(CrateBlocks.TITANIUM_CRATE.get().asItem(), CrateBlocks.GOLDEN_CRATE.get().asItem());
        addItem(CrateBlocks.THORNS_CRATE.get().asItem(), CrateBlocks.JUNGLE_CRATE.get().asItem());
        addItem(CrateBlocks.WILD_CRATE.get().asItem(), CrateBlocks.SAVANNA_CRATE.get().asItem());
        addItem(CrateBlocks.SPACE_CRATE.get().asItem(), CrateBlocks.SKY_CRATE.get().asItem());
        addItem(CrateBlocks.DEFACED_CRATE.get().asItem(), CrateBlocks.CORRUPT_CRATE.get().asItem());
        addItem(CrateBlocks.BLOOD_CRATE.get().asItem(), CrateBlocks.TR_CRIMSON_CRATE.get().asItem());
        addItem(CrateBlocks.PROVIDENTIAL_CRATE.get().asItem(), CrateBlocks.HALLOWED_CRATE.get().asItem());
        addItem(CrateBlocks.FENCING_CRATE.get().asItem(), CrateBlocks.DUNGEON_CRATE.get().asItem());
        addItem(CrateBlocks.CONIFEROUS_WOOD_CRATE.get().asItem(), CrateBlocks.FREEZE_CRATE.get().asItem());
        addItem(CrateBlocks.ILLUSION_CRATE.get().asItem(), CrateBlocks.OASIS_CRATE.get().asItem());
        addItem(CrateBlocks.HELL_STONE_CRATE.get().asItem(), CrateBlocks.OBSIDIAN_CRATE.get().asItem());
        addItem(CrateBlocks.BEACH_CRATE.get().asItem(), CrateBlocks.OCEAN_CRATE.get().asItem());

        // 宝石转化
        addItem(MaterialItems.TOPAZ.get(), MaterialItems.TR_AMETHYST.get());
        addItem(MaterialItems.SAPPHIRE.get(), MaterialItems.TOPAZ.get());
        addItem(MaterialItems.TR_EMERALD.get(), MaterialItems.SAPPHIRE.get());
        addItem(MaterialItems.RUBY.get(), MaterialItems.TR_EMERALD.get());
        addItem(Items.DIAMOND, MaterialItems.RUBY.get());
        addItem(MaterialItems.TR_AMETHYST.get(), Items.COBBLESTONE);
        // 锭到矿的转化
        addItem(MaterialItems.TITANIUM_INGOT.get(), MaterialItems.RAW_TITANIUM.get());
        addItem(MaterialItems.ADAMANTITE_INGOT.get(), MaterialItems.RAW_ADAMANTITE.get());
        addItem(MaterialItems.ORICHALCUM_INGOT.get(), MaterialItems.RAW_ORICHALCUM.get());
        addItem(MaterialItems.MYTHRIL_INGOT.get(), MaterialItems.RAW_MYTHRIL.get());
        addItem(MaterialItems.PALLADIUM_INGOT.get(), MaterialItems.RAW_PALLADIUM.get());
        addItem(MaterialItems.COBALT_INGOT.get(), MaterialItems.RAW_COBALT.get());
        addItem(MaterialItems.HELLSTONE_INGOT.get(), MaterialItems.RAW_HELLSTONE.get());
        addItem(MaterialItems.TR_CRIMSON_INGOT.get(), MaterialItems.RAW_TR_CRIMSON.get());
        addItem(MaterialItems.DEMONITE_INGOT.get(), MaterialItems.RAW_DEMONITE.get());
        addItem(MaterialItems.METEORITE_INGOT.get(), MaterialItems.RAW_METEORITE.get());
        addItem(ModTags.Items.INGOTS_PLATINUM, MaterialItems.RAW_PLATINUM.get(), 1);
        addItem(Items.GOLD_INGOT, Items.RAW_GOLD);
        addItem(ModTags.Items.INGOTS_TUNGSTEN, MaterialItems.RAW_TUNGSTEN.get(), 1);
        addItem(ModTags.Items.INGOTS_SILVER, MaterialItems.RAW_SILVER.get(), 1);
        addItem(Items.IRON_INGOT, Items.RAW_IRON);
        addItem(ModTags.Items.INGOTS_LEAD, MaterialItems.RAW_LEAD.get(), 1);
        addItem(ModTags.Items.INGOTS_TIN, MaterialItems.RAW_TIN.get(), 1);
        addItem(Items.COPPER_INGOT, Items.RAW_COPPER);
        addItem(Items.RAW_COPPER, Items.COBBLESTONE);
        addItem(Items.COBBLESTONE, Items.DIRT);
        // 矿的下级转化（陨石，魔矿，猩红矿不参与这一过程）
        addItem(MaterialItems.RAW_TITANIUM.get(), MaterialItems.RAW_ADAMANTITE.get());
        addItem(MaterialItems.RAW_ADAMANTITE.get(), MaterialItems.RAW_ORICHALCUM.get());
        addItem(MaterialItems.RAW_ORICHALCUM.get(), MaterialItems.RAW_MYTHRIL.get());
        addItem(MaterialItems.RAW_PALLADIUM.get(), MaterialItems.RAW_COBALT.get());
        addItem(MaterialItems.RAW_COBALT.get(), MaterialItems.RAW_PLATINUM.get());
        addItem(MaterialItems.RAW_PLATINUM.get(), Items.RAW_GOLD);
        addItem(Items.RAW_GOLD, MaterialItems.RAW_TUNGSTEN.get());
        addItem(MaterialItems.RAW_TUNGSTEN.get(), MaterialItems.RAW_SILVER.get());
        addItem(MaterialItems.RAW_SILVER.get(), MaterialItems.RAW_LEAD.get());
        addItem(MaterialItems.RAW_LEAD.get(), Items.RAW_IRON);
        addItem(Items.RAW_IRON, MaterialItems.RAW_TIN.get());
        addItem(MaterialItems.RAW_TIN.get(), Items.RAW_COPPER);

        addItem(Items.WATER_BUCKET, Items.LAVA_BUCKET);
        addItem(Items.LAVA_BUCKET, ToolItems.HONEY_BUCKET.get());
        addItem(ToolItems.HONEY_BUCKET.get(), Items.WATER_BUCKET);
        addItem(SwordItems.ZOMBIE_ARM.get(), ModItems.WHOOPIE_CUSHION.get());

        addItem(ConsumableItems.LIFE_CRYSTAL.get(), ConsumableItems.VITAL_CRYSTAL.get());
        addItem(ConsumableItems.MANA_CRYSTAL.get(), ConsumableItems.ARCANE_CRYSTAL.get());
        addItem(ConsumableItems.LIFE_FRUIT.get(), ConsumableItems.AEGIS_APPLE.get());
        addItem(Tags.Items.FOODS_FRUIT, ConsumableItems.AMBROSIA.get(), 1);
        addItem(BaitItems.GOLD_WORM.get(), ConsumableItems.GUMMY_WORM.get());
        addItem(MaterialItems.PINK_PEARL.get(), ConsumableItems.GALAXY_PEARL.get());

        addItem(MaterialItems.GEL.get(), Items.SLIME_BALL);
        addItem(ConsumableItems.HERB_BAG.get(), ConsumableItems.CAN_OF_WORMS.get());
        addItem(ConsumableItems.CAN_OF_WORMS.get(), ConsumableItems.HERB_BAG.get());
        addItem(ConsumableItems.VILE_POWDER.get(), ConsumableItems.PURIFICATION_POWDER.get());
        addItem(ConsumableItems.VICIOUS_POWDER.get(), ConsumableItems.PURIFICATION_POWDER.get());

        addItem(ToolItems.BOTTOMLESS_WATER_BUCKET.get(), ToolItems.BOTTOMLESS_SHIMMER_BUCKET.get(), GamePhase.MOON_LORD);
        addItem(ToolItems.BOTTOMLESS_SHIMMER_BUCKET.get(), ToolItems.BOTTOMLESS_WATER_BUCKET.get(), GamePhase.MOON_LORD);

        addEntity(EntityType.WITCH, EntityType.VILLAGER);
        addEntity(entity -> {
            EntityType<?> entityType = entity.getType();
            return entityType == EntityType.PIGLIN ||
                    entityType == EntityType.PIGLIN_BRUTE ||
                    entityType == EntityType.ZOMBIFIED_PIGLIN ||
                    entityType == EntityType.CREEPER;
        }, EntityType.PIG);
        addEntity(entity -> entity instanceof AbstractSkeleton || entity.getType() == EntityType.ZOMBIE, EntityType.SKELETON);
        addEntity(entity -> entity instanceof AbstractHorse, EntityType.HORSE);
        addEntity(EntityType.VEX, EntityType.ALLAY);
        addEntity(entity -> entity instanceof Creeper creeper && creeper.isPowered(), EntityType.CREEPER);
        addEntity(EntityType.MOOSHROOM, EntityType.COW);
    }

    public static void initialize() {}
}
