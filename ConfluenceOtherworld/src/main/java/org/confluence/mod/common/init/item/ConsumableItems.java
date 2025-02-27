package org.confluence.mod.common.init.item;

import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.natural.spreadable.ISpreadable;
import org.confluence.mod.common.entity.projectile.bomb.*;
import org.confluence.mod.common.init.*;
import org.confluence.mod.common.item.common.*;
import org.confluence.mod.common.item.mana.ArcaneCrystalItem;
import org.confluence.mod.common.item.mana.ManaCrystalItem;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terraentity.entity.boss.*;

import java.util.function.Supplier;

public class ConsumableItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final Supplier<ManaCrystalItem> MANA_CRYSTAL = ITEMS.register("mana_crystal", ManaCrystalItem::new);
    public static final Supplier<EverBeneficialItem> LIFE_CRYSTAL = ITEMS.register("life_crystal", () -> new EverBeneficialItem(ModRarity.GREEN, EverBeneficialItem.LIFE_CRYSTAL, ModSoundEvents.LIFE_CRYSTAL_USE));
    public static final Supplier<EverBeneficialItem> LIFE_FRUIT = ITEMS.register("life_fruit", () -> new EverBeneficialItem(ModRarity.LIME, EverBeneficialItem.LIFE_FRUITS, ModSoundEvents.LIFE_CRYSTAL_USE));
    public static final Supplier<EverBeneficialItem> VITAL_CRYSTAL = ITEMS.register("vital_crystal", () -> new EverBeneficialItem(ModRarity.LIGHT_PURPLE, EverBeneficialItem.VITAL_CRYSTAL, ModSoundEvents.TRANSMUTATION_USE));
    public static final Supplier<ArcaneCrystalItem> ARCANE_CRYSTAL = ITEMS.register("arcane_crystal", ArcaneCrystalItem::new);
    public static final Supplier<EverBeneficialItem> AEGIS_APPLE = ITEMS.register("aegis_apple", () -> new EverBeneficialItem(ModRarity.LIGHT_PURPLE, EverBeneficialItem.AEGIS_APPLE, ModSoundEvents.TRANSMUTATION_USE));
    public static final Supplier<EverBeneficialItem> AMBROSIA = ITEMS.register("ambrosia", () -> new EverBeneficialItem(ModRarity.LIGHT_PURPLE, EverBeneficialItem.AMBROSIA, ModSoundEvents.TRANSMUTATION_USE));
    public static final Supplier<EverBeneficialItem> GUMMY_WORM = ITEMS.register("gummy_worm", () -> new EverBeneficialItem(ModRarity.LIGHT_PURPLE, EverBeneficialItem.GUMMY_WORM, ModSoundEvents.TRANSMUTATION_USE));
    public static final Supplier<EverBeneficialItem> GALAXY_PEARL = ITEMS.register("galaxy_pearl", () -> new EverBeneficialItem(ModRarity.LIGHT_PURPLE, EverBeneficialItem.GALAXY_PEARL, ModSoundEvents.TRANSMUTATION_USE));
    public static final Supplier<EverBeneficialItem> MINECART_UPGRADE_KIT = ITEMS.register("minecart_upgrade_kit", () -> new EverBeneficialItem(ModRarity.EXPERT, EverBeneficialItem.MINECART_UPGRADE_KIT, ModSoundEvents.TRANSMUTATION_USE));

    public static final Supplier<ThrowableItem<BaseBombEntity>> BOMB = ITEMS.register("bomb", () -> new ThrowableItem<>(0.8F, BaseBombEntity::new));
    public static final Supplier<ThrowableItem<BouncyBombEntity>> BOUNCY_BOMB = ITEMS.register("bouncy_bomb", () -> new ThrowableItem<>(0.8F, BouncyBombEntity::new));
    public static final Supplier<ThrowableItem<StickyBombEntity>> STICKY_BOMB = ITEMS.register("sticky_bomb", () -> new ThrowableItem<>(0.8F, StickyBombEntity::new));
    public static final Supplier<ThrowableItem<BombFishEntity>> BOMB_FISH = ITEMS.register("bomb_fish", () -> new ThrowableItem<>(0.8F, BombFishEntity::new));
    public static final Supplier<ThrowableItem<ScarabBombEntity>> SCARAB_BOMB = ITEMS.register("scarab_bomb", () -> new ThrowableItem<>(0.8F, ScarabBombEntity::new));
    public static final Supplier<ThrowableItem<BaseDynamiteEntity>> DYNAMITE = ITEMS.register("dynamite", () -> new ThrowableItem<>(0.75F, BaseDynamiteEntity::new));
    public static final Supplier<ThrowableItem<BouncyDynamiteEntity>> BOUNCY_DYNAMITE = ITEMS.register("bouncy_dynamite", () -> new ThrowableItem<>(0.75F, BouncyDynamiteEntity::new));
    public static final Supplier<ThrowableItem<StickyDynamiteEntity>> STICKY_DYNAMITE = ITEMS.register("sticky_dynamite", () -> new ThrowableItem<>(0.75F, StickyDynamiteEntity::new));
    public static final Supplier<ThrowableItem<BaseGrenadeEntity>> GRENADE = ITEMS.register("grenade", () -> new ThrowableItem<>(0.7F, BaseGrenadeEntity::new));
    public static final Supplier<ThrowableItem<BouncyGrenadeEntity>> BOUNCY_GRENADE = ITEMS.register("bouncy_grenade", () -> new ThrowableItem<>(0.7F, BouncyGrenadeEntity::new));
    public static final Supplier<ThrowableItem<StickyGrenadeEntity>> STICKY_GRENADE = ITEMS.register("sticky_grenade", () -> new ThrowableItem<>(0.7F, StickyGrenadeEntity::new));
    public static final Supplier<ThrowableItem<BaseDirtBombEntity>> DIRT_BOMB = ITEMS.register("dirt_bomb", () -> new ThrowableItem<>(0.8F, BaseDirtBombEntity::new));
    public static final Supplier<ThrowableItem<StickyDirtBombEntity>> STICKY_DIRT_BOMB = ITEMS.register("sticky_dirt_bomb", () -> new ThrowableItem<>(0.8F, StickyDirtBombEntity::new));
    public static final Supplier<ThrowableItem<DryBombEntity>> DRY_BOMB = ITEMS.register("dry_bomb", () -> new ThrowableItem<>(0.8F, DryBombEntity::new));
    public static final Supplier<ThrowableItem<LiquidBombEntity>> WET_BOMB = ITEMS.register("wet_bomb", () -> new ThrowableItem<>(0.8F, player -> new LiquidBombEntity(ModEntities.WET_BOMB.get(), player, Fluids.WATER, 3)));
    public static final Supplier<ThrowableItem<LiquidBombEntity>> LAVA_BOMB = ITEMS.register("lava_bomb", () -> new ThrowableItem<>(0.8F, player -> new LiquidBombEntity(ModEntities.LAVA_BOMB.get(), player, Fluids.LAVA, 3)));
    public static final Supplier<ThrowableItem<LiquidBombEntity>> HONEY_BOMB = ITEMS.register("honey_bomb", () -> new ThrowableItem<>(0.8F, player -> new LiquidBombEntity(ModEntities.HONEY_BOMB.get(), player, ModFluids.HONEY.fluid().get(), 3)));
    public static final Supplier<ShurikenItem> SHURIKEN = ITEMS.register("shuriken", ShurikenItem::new);
    public static final Supplier<ThrowingKnivesItem> THROWING_KNIVES = ITEMS.register("throwing_knives", ThrowingKnivesItem::new);

    public static final Supplier<RightClickLootItem> CLAM = ITEMS.register("clam", () -> new RightClickLootItem(ModRarity.BLUE, ModLootTables.CLAM));
    public static final Supplier<RightClickLootItem> HERB_BAG = ITEMS.register("herb_bag", () -> new RightClickLootItem(ModRarity.BLUE, ModLootTables.HERB_BAG));
    public static final Supplier<RightClickLootItem> CAN_OF_WORMS = ITEMS.register("can_of_worms", () -> new RightClickLootItem(ModRarity.BLUE, ModLootTables.CAN_OF_WORMS));
    public static final Supplier<RightClickLootItem> RED_ENVELOPE = ITEMS.register("red_envelope", () -> new RightClickLootItem(ModRarity.RED, ModLootTables.RED_ENVELOPE));
    public static final Supplier<RightClickLootItem> SUGAR_TANGERINE = ITEMS.register("sugar_tangerine", () -> new RightClickLootItem(ModRarity.RED, ModLootTables.SUGAR_TANGERINE));
    public static final Supplier<RightClickLootItem> DELUXE_PACKAGE = ITEMS.register("deluxe_package", () -> new RightClickLootItem(ModRarity.RED, ModLootTables.DELUXE_PACKAGE));
    public static final Supplier<RightClickLootItem> GOODIE_BAG = ITEMS.register("goodie_bag", () -> new RightClickLootItem(ModRarity.ORANGE, ModLootTables.GOODIE_GIFT));
    public static final Supplier<RightClickLootItem> CHRISTMAS_GIFT = ITEMS.register("christmas_gift", () -> new RightClickLootItem(ModRarity.BLUE, ModLootTables.CHRISTMAS_GIFT));

    public static final Supplier<ThrownPowderItem> PURIFICATION_POWDER = ITEMS.register("purification_powder", () -> new ThrownPowderItem(ISpreadable.Type.PURE));
    public static final Supplier<ThrownPowderItem> VILE_POWDER = ITEMS.register("vile_powder", () -> new ThrownPowderItem(ISpreadable.Type.CORRUPT));
    public static final Supplier<ThrownPowderItem> VICIOUS_POWDER = ITEMS.register("vicious_powder", () -> new ThrownPowderItem(ISpreadable.Type.CRIMSON));
    public static final Supplier<FertilizerItem> FERTILIZER = ITEMS.register("fertilizer", FertilizerItem::new);

    public static final Supplier<BossSummingItem> SUSPICIOUS_LOOKING_EYE = ITEMS.register("suspicious_looking_eye", () -> new BossSummingItem(player -> player.level().isNight(), EyeOfCthulhu::new));
    public static final Supplier<BossSummingItem> SLIME_CROWN = ITEMS.register("slime_crown", () -> new BossSummingItem(player -> true, KingSlime::new));
    public static final Supplier<BossSummingItem> WORM_FOOD = ITEMS.register("worm_food", () -> new BossSummingItem(player -> player.level().getBiome(player.blockPosition()).is(ModTags.Biomes.THE_CORRUPTION), level -> new EaterOfWorlds(level, true)));
    public static final Supplier<BossSummingItem> BLOODY_SPINE = ITEMS.register("bloody_spine", () -> new BossSummingItem(player -> player.level().getBiome(player.blockPosition()).is(ModTags.Biomes.TR_CRIMSON), BrainOfCthulhu::new));
    public static final Supplier<BossSummingItem> ABEEMINATION = ITEMS.register("abeemination", () -> new BossSummingItem(player -> player.level().getBiome(player.blockPosition()).is(Biomes.JUNGLE), QueenBee::new));
    public static final Supplier<ModBoneMealItem> ROTTEN_BONE_DUST = ITEMS.register("rotten_bone_dust", ModBoneMealItem::new);
    public static final Supplier<ModBoneMealItem> BLOODSTAINED_POWDER = ITEMS.register("bloodstained_powder", ModBoneMealItem::new);
}
