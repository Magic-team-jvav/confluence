package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.bomb.*;
import org.confluence.mod.common.init.ModLootTables;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.item.common.*;
import org.confluence.mod.common.item.mana.ArcaneCrystalItem;
import org.confluence.mod.common.item.mana.ManaStarItem;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terraentity.entity.boss.CthulhuEye;
import org.confluence.terraentity.entity.boss.EaterOfWorld;
import org.confluence.terraentity.entity.boss.KingSlime;

import java.util.function.Supplier;

public class ConsumableItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final Supplier<ManaStarItem> MANA_STAR = ITEMS.register("mana_star", ManaStarItem::new);
    public static final Supplier<EverBeneficialItem> LIFE_CRYSTAL = ITEMS.register("life_crystal", () -> new EverBeneficialItem(ModRarity.GREEN, EverBeneficialItem.LIFE_CRYSTAL, ModSoundEvents.LIFE_CRYSTAL_USE));
    public static final Supplier<EverBeneficialItem> LIFE_FRUIT = ITEMS.register("life_fruit", () -> new EverBeneficialItem(ModRarity.LIME, EverBeneficialItem.LIFE_FRUITS));
    public static final Supplier<EverBeneficialItem> VITAL_CRYSTAL = ITEMS.register("vital_crystal", () -> new EverBeneficialItem(ModRarity.LIGHT_PURPLE, EverBeneficialItem.VITAL_CRYSTAL));
    public static final Supplier<ArcaneCrystalItem> ARCANE_CRYSTAL = ITEMS.register("arcane_crystal", ArcaneCrystalItem::new);
    public static final Supplier<EverBeneficialItem> AEGIS_APPLE = ITEMS.register("aegis_apple", () -> new EverBeneficialItem(ModRarity.LIGHT_PURPLE, EverBeneficialItem.AEGIS_APPLE));
    public static final Supplier<EverBeneficialItem> AMBROSIA = ITEMS.register("ambrosia", () -> new EverBeneficialItem(ModRarity.LIGHT_PURPLE, EverBeneficialItem.AMBROSIA));
    public static final Supplier<EverBeneficialItem> GUMMY_WORM = ITEMS.register("gummy_worm", () -> new EverBeneficialItem(ModRarity.LIGHT_PURPLE, EverBeneficialItem.GUMMY_WORM));
    public static final Supplier<EverBeneficialItem> GALAXY_PEARL = ITEMS.register("galaxy_pearl", () -> new EverBeneficialItem(ModRarity.LIGHT_PURPLE, EverBeneficialItem.GALAXY_PEARL));
    public static final Supplier<EverBeneficialItem> MINECART_UPGRADE_KIT = ITEMS.register("minecart_upgrade_kit", () -> new EverBeneficialItem(ModRarity.EXPERT, EverBeneficialItem.MINECART_UPGRADE_KIT));

    public static final Supplier<Item> GRENADE = ITEMS.register("grenade", () -> new Item(new Item.Properties())); // todo
    public static final Supplier<BombItem> BOMB = ITEMS.register("bomb", () -> new BombItem(BaseBombEntity::new));
    public static final Supplier<BombItem> BOUNCY_BOMB = ITEMS.register("bouncy_bomb", () -> new BombItem(BouncyBombEntity::new));
    public static final Supplier<BombItem> STICKY_BOMB = ITEMS.register("sticky_bomb", () -> new BombItem(StickyBombEntity::new));
    public static final Supplier<BombItem> BOMB_FISH = ITEMS.register("bomb_fish", () -> new BombItem(BombFishEntity::new));
    public static final Supplier<BombItem> SCARAB_BOMB = ITEMS.register("scarab_bomb", () -> new BombItem(ScarabBombEntity::new));

    public static final Supplier<ShurikenItem> SHURIKEN = ITEMS.register("shuriken", ShurikenItem::new);
    public static final Supplier<ThrowingKnivesItem> THROWING_KNIVES = ITEMS.register("throwing_knives", ThrowingKnivesItem::new);

    public static final Supplier<RightClickLootItem> CLAM = ITEMS.register("clam", () -> new RightClickLootItem(ModRarity.BLUE, ModLootTables.CLAM));
    public static final Supplier<RightClickLootItem> HERB_BAG = ITEMS.register("herb_bag", () -> new RightClickLootItem(ModRarity.BLUE, ModLootTables.HERB_BAG));
    public static final Supplier<RightClickLootItem> CAN_OF_WORMS = ITEMS.register("can_of_worms", () -> new RightClickLootItem(ModRarity.BLUE, ModLootTables.CAN_OF_WORMS));
    public static final Supplier<RightClickLootItem> RED_ENVELOPE = ITEMS.register("red_envelope", () -> new RightClickLootItem(ModRarity.RED, ModLootTables.RED_ENVELOPE));
    public static final Supplier<RightClickLootItem> SUGAR_TANGERINE = ITEMS.register("sugar_tangerine", () -> new RightClickLootItem(ModRarity.RED, ModLootTables.SUGAR_TANGERINE));
    public static final Supplier<RightClickLootItem> DELUXE_PACKAGE = ITEMS.register("deluxe_package", () -> new RightClickLootItem(ModRarity.RED, ModLootTables.DELUXE_PACKAGE));
    public static final Supplier<RightClickLootItem> CHRISTMAS_GIFT = ITEMS.register("christmas_gift", () -> new RightClickLootItem(ModRarity.BLUE, ModLootTables.CHRISTMAS_GIFT));

    public static final Supplier<BossSummingItem> SUSPICIOUS_LOOKING_EYE = ITEMS.register("suspicious_looking_eye", () -> new BossSummingItem(player -> player.level().isNight(), CthulhuEye::new));
    public static final Supplier<BossSummingItem> SLIME_CROWN = ITEMS.register("slime_crown", () -> new BossSummingItem(player -> true, KingSlime::new));
    public static final Supplier<BossSummingItem> WORM_FOOD = ITEMS.register("worm_food", () -> new BossSummingItem(player -> player.level().getBiome(player.blockPosition()).is(ModTags.Biomes.THE_CORRUPTION), level -> new EaterOfWorld(level, true)));
}
