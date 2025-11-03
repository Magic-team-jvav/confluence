package org.confluence.mod.common.init.item;

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.mana.*;
import org.confluence.mod.common.entity.projectile.mana.BaseManaStaffProjectileEntity.Variant;
import org.confluence.mod.common.entity.projectile.strip.VilethronProjectile;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.item.gun.BeeGunItem;
import org.confluence.mod.common.item.gun.SpaceGunItem;
import org.confluence.mod.common.item.mana.*;

public class ManaWeaponItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    // 魔法武器数值参考为 原数值÷2 +5 为基础值
    public static final DeferredItem<ManaStaffItem<WandOfSparkingProjectile>> WAND_OF_SPARKING = ITEMS.register("wand_of_sparking", () -> new ManaStaffItem<>(ModRarity.BLUE, WandOfSparkingProjectile::new, 5.6F, 2, 7.0F, 13, 0.14));
    public static final DeferredItem<ManaStaffItem<WandOfFrostingProjectile>> WAND_OF_FROSTING = ITEMS.register("wand_of_frosting", () -> new ManaStaffItem<>(ModRarity.BLUE, WandOfFrostingProjectile::new, 10.0F, 2, 7.0F, 13, 0.14));
    public static final DeferredItem<ManaStaffItem<ThunderZapperProjectile>> THUNDER_ZAPPER = ITEMS.register("thunder_zapper", () -> new ManaStaffItem<>(ModRarity.BLUE, ThunderZapperProjectile::new, 14.8F, 7, 12, 6, 0.04));
    public static final DeferredItem<ManaStaffItem<BaseManaStaffProjectileEntity>> RUBY_STAFF = ITEMS.register("ruby_staff", () -> new ManaStaffItem<>(ModRarity.BLUE, player -> new BaseManaStaffProjectileEntity(player, Variant.RUBY), 15.4F, 7, 9.0F, 15, 0.04));
    public static final DeferredItem<ManaStaffItem<BaseManaStaffProjectileEntity>> AMBER_STAFF = ITEMS.register("amber_staff", () -> new ManaStaffItem<>(ModRarity.BLUE, player -> new BaseManaStaffProjectileEntity(player, Variant.AMBER), 15.4F, 7, 9.0F, 15, 0.04));
    public static final DeferredItem<ManaStaffItem<BaseManaStaffProjectileEntity>> TOPAZ_STAFF = ITEMS.register("topaz_staff", () -> new ManaStaffItem<>(ModRarity.BLUE, player -> new BaseManaStaffProjectileEntity(player, Variant.TOPAZ), 13.4F, 5, 6.5F, 13, 0.04));
    public static final DeferredItem<ManaStaffItem<BaseManaStaffProjectileEntity>> JADE_STAFF = ITEMS.register("jade_staff", () -> new ManaStaffItem<>(ModRarity.BLUE, player -> new BaseManaStaffProjectileEntity(player, Variant.JADE), 14.6F, 6, 8.0F, 14, 0.04));
    public static final DeferredItem<ManaStaffItem<BaseManaStaffProjectileEntity>> SAPPHIRE_STAFF = ITEMS.register("sapphire_staff", () -> new ManaStaffItem<>(ModRarity.BLUE, player -> new BaseManaStaffProjectileEntity(player, Variant.SAPPHIRE), 14.2F, 6, 7.5F, 14, 0.04));
    public static final DeferredItem<ManaStaffItem<BaseManaStaffProjectileEntity>> AMETHYST_STAFF = ITEMS.register("amethyst_staff", () -> new ManaStaffItem<>(ModRarity.BLUE, player -> new BaseManaStaffProjectileEntity(player, Variant.AMETHYST), 13.0F, 5, 6.0F, 13, 0.04));
    public static final DeferredItem<ManaStaffItem<BaseManaStaffProjectileEntity>> DIAMOND_STAFF = ITEMS.register("diamond_staff", () -> new ManaStaffItem<>(ModRarity.BLUE, player -> new BaseManaStaffProjectileEntity(player, Variant.DIAMOND), 16.2F, 8, 9.5F, 15, 0.04));
    public static final DeferredItem<ManaStaffItem<VilethronProjectile>> VILETHRON = ITEMS.register("vilethron", () -> new ManaStaffItem<>(ModRarity.BLUE, VilethronProjectile::new, 4.5F, 10, 8.0F, 18, 0.04));
    public static final DeferredItem<ManaStaffItem<HurtnadoProjectile>> WEATHER_PAIN = ITEMS.register("weather_pain", WeatherPainItem::new);
    /* 魔法飞弹 */
    public static final DeferredItem<AquaScepterItem> AQUA_SCEPTER = ITEMS.register("aqua_scepter", AquaScepterItem::new);
    public static final DeferredItem<ManaStaffItem<BallOfFireProjectile>> FLOWER_OF_FIRE = ITEMS.register("flower_of_fire", () -> new ManaStaffItem<>(ModRarity.ORANGE, BallOfFireProjectile::new, 29F, 12, 7.5F, 7, 0.04));
    /* 烈焰火鞭 */
    public static final DeferredItem<ManaStaffItem<SkullProjectile>> BOOK_OF_SKULLS = ITEMS.register("book_of_skulls", () -> new ManaStaffItem<>(ModRarity.GREEN, SkullProjectile::new, 18.2F, 18, 3.5F, 2, 0.04));
    public static final DeferredItem<ManaStaffItem<WaterBoltProjectile>> WATER_BOLT = ITEMS.register("water_bolt", () -> new ManaStaffItem<>(ModRarity.GREEN, WaterBoltProjectile::new, 19.5F, 10, 4.5F, 7, 0.04));
    public static final DeferredItem<ManaStaffItem<DemonScytheProjectile>> DEMON_SCYTHE = ITEMS.register("demon_scythe", () -> new ManaStaffItem<>(ModRarity.ORANGE, DemonScytheProjectile::new, 25.8f, 14, 0.2F, 3, 0.04));
    public static final DeferredItem<CloudRodItem> CRIMSON_ROD = ITEMS.register("crimson_rod", () -> new CloudRodItem(ModRarity.BLUE, player -> new CloudProjectile(ModEntities.BLOOD_CLOUD_PROJECTILE.get(), ModEntities.BLOOD_RAIN_PROJECTILE.get(), player, 3, 2), 8, 30, 12, 3, 0.04));

    public static final DeferredItem<CloudRodItem> NIMBUS_ROD = ITEMS.register("nimbus_rod", () -> new CloudRodItem(ModRarity.LIGHT_PURPLE, player -> new CloudProjectile(ModEntities.RAIN_CLOUD_PROJECTILE.get(), ModEntities.RAIN_PROJECTILE.get(), player, 2, 5), 15, 30, 16, 3, 0.04).setMaxCloud(2));
    public static final DeferredItem<ManaStaffItem<MagicDaggerProjectile>> MAGIC_DAGGER = ITEMS.register("magic_dagger", MagicDaggerItem::new);
    public static final DeferredItem<ManaStaffItem<CrystalStormProjectile>> CRYSTAL_STORM = ITEMS.register("crystal_storm", () -> new ManaStaffItem<>(ModRarity.LIGHT_RED, CrystalStormProjectile::new, 21, 5, 16, 3, 0.04));
    public static final DeferredItem<ManaStaffItem<CursedFlamesProjectile>> CURSED_FLAMES = ITEMS.register("cursed_flames", () -> new ManaStaffItem<>(ModRarity.LIGHT_RED, CursedFlamesProjectile::new, 32, 9, 10, 5, 0.04));
    public static final DeferredItem<ManaStaffItem<BallOfFrostProjectile>> FLOWER_OF_FROST = ITEMS.register("flower_of_frost", () -> new ManaStaffItem<>(ModRarity.PINK, BallOfFrostProjectile::new, 35, 11, 9, 4, 0.04));
    public static final DeferredItem<GoldenShowerItem> GOLDEN_SHOWER = ITEMS.register("golden_shower", GoldenShowerItem::new);

    public static final DeferredItem<BeeGunItem> BEE_GUN = ITEMS.registerItem("bee_gun", BeeGunItem::new);
    public static final DeferredItem<SpaceGunItem> SPACE_GUN = ITEMS.registerItem("space_gun", SpaceGunItem::new);
}
