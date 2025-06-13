package org.confluence.mod.common.init.item;

import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.mana.*;
import org.confluence.mod.common.entity.projectile.mana.BaseManaStaffProjectileEntity.Variant;
import org.confluence.mod.common.entity.projectile.strip.VilethronProjectile;
import org.confluence.mod.common.item.gun.BeeGunItem;
import org.confluence.mod.common.item.gun.SpaceGunItem;
import org.confluence.mod.common.item.mana.MagicDaggerItem;
import org.confluence.mod.common.item.mana.ManaStaffItem;
import org.confluence.mod.common.item.mana.WeatherPainItem;

public class ManaWeaponItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<ManaStaffItem<WandOfSparkingProjectile>> WAND_OF_SPARKING = ITEMS.register("wand_of_sparking", () -> new ManaStaffItem<>(ModRarity.BLUE, WandOfSparkingProjectile::new, 3.6F, 2, 7.0F, 13, 0.14));
    public static final DeferredItem<ManaStaffItem<WandOfFrostingProjectile>> WAND_OF_FROSTING = ITEMS.register("wand_of_frosting", () -> new ManaStaffItem<>(ModRarity.BLUE, WandOfFrostingProjectile::new, 5.0F, 2, 7.0F, 13, 0.14));
    public static final DeferredItem<ManaStaffItem<ThunderZapperProjectile>> THUNDER_ZAPPER = ITEMS.register("thunder_zapper", () -> new ManaStaffItem<>(ModRarity.BLUE, ThunderZapperProjectile::new, 9.8F, 7, 12, 6, 0.04));
    public static final DeferredItem<ManaStaffItem<BaseManaStaffProjectileEntity>> RUBY_STAFF = ITEMS.register("ruby_staff", () -> new ManaStaffItem<>(ModRarity.BLUE, player -> new BaseManaStaffProjectileEntity(player, Variant.RUBY), 10.4F, 7, 9.0F, 15, 0.04));
    public static final DeferredItem<ManaStaffItem<BaseManaStaffProjectileEntity>> AMBER_STAFF = ITEMS.register("amber_staff", () -> new ManaStaffItem<>(ModRarity.BLUE, player -> new BaseManaStaffProjectileEntity(player, Variant.AMBER), 10.4F, 7, 9.0F, 15, 0.04));
    public static final DeferredItem<ManaStaffItem<BaseManaStaffProjectileEntity>> TOPAZ_STAFF = ITEMS.register("topaz_staff", () -> new ManaStaffItem<>(ModRarity.BLUE, player -> new BaseManaStaffProjectileEntity(player, Variant.TOPAZ), 8.4F, 5, 6.5F, 13, 0.04));
    public static final DeferredItem<ManaStaffItem<BaseManaStaffProjectileEntity>> JADE_STAFF = ITEMS.register("jade_staff", () -> new ManaStaffItem<>(ModRarity.BLUE, player -> new BaseManaStaffProjectileEntity(player, Variant.JADE), 9.6F, 6, 8.0F, 14, 0.04));
    public static final DeferredItem<ManaStaffItem<BaseManaStaffProjectileEntity>> SAPPHIRE_STAFF = ITEMS.register("sapphire_staff", () -> new ManaStaffItem<>(ModRarity.BLUE, player -> new BaseManaStaffProjectileEntity(player, Variant.SAPPHIRE), 9.2F, 6, 7.5F, 14, 0.04));
    public static final DeferredItem<ManaStaffItem<BaseManaStaffProjectileEntity>> AMETHYST_STAFF = ITEMS.register("amethyst_staff", () -> new ManaStaffItem<>(ModRarity.BLUE, player -> new BaseManaStaffProjectileEntity(player, Variant.AMETHYST), 8.0F, 5, 6.0F, 13, 0.04));
    public static final DeferredItem<ManaStaffItem<BaseManaStaffProjectileEntity>> DIAMOND_STAFF = ITEMS.register("diamond_staff", () -> new ManaStaffItem<>(ModRarity.BLUE, player -> new BaseManaStaffProjectileEntity(player, Variant.DIAMOND), 11.2F, 8, 9.5F, 15, 0.04));
    public static final DeferredItem<ManaStaffItem<VilethronProjectile>> VILETHRON = ITEMS.register("vilethron", () -> new ManaStaffItem<>(ModRarity.BLUE, VilethronProjectile::new, 3.5F, 10, 8.0F, 18, 0.04));
    public static final DeferredItem<ManaStaffItem<HurtnadoProjectile>> WEATHER_PAIN = ITEMS.register("weather_pain", WeatherPainItem::new);
    /* 魔法飞弹 */
    public static final DeferredItem<ManaStaffItem<WaterStreamProjectile>> AQUA_SCEPTER = ITEMS.register("aqua_scepter", () -> new ManaStaffItem<>(ModRarity.GREEN, WaterStreamProjectile::new, 8, 7, 32.0F, 3, 0.04));
    public static final DeferredItem<ManaStaffItem<BallOfFireProjectile>> FLOWER_OF_FIRE = ITEMS.register("flower_of_fire", () -> new ManaStaffItem<>(ModRarity.ORANGE, BallOfFireProjectile::new, 25.6F, 12, 7.5F, 7, 0.04));
    /* 烈焰火鞭 */
    public static final DeferredItem<ManaStaffItem<WaterBoltProjectile>> WATER_BOLT = ITEMS.register("water_bolt", () -> new ManaStaffItem<>(ModRarity.GREEN, WaterBoltProjectile::new, 14.5F, 10, 4.5F, 10, 0.04));

    public static final DeferredItem<ManaStaffItem<MagicDaggerProjectile>> MAGIC_DAGGER = ITEMS.register("magic_dagger", MagicDaggerItem::new);
    public static final DeferredItem<ManaStaffItem<CrystalStormProjectile>> CRYSTAL_STORM = ITEMS.register("crystal_storm", () -> new ManaStaffItem<>(ModRarity.LIGHT_RED, CrystalStormProjectile::new, 1.6F, 5, 16, 7, 0.04));
    public static final DeferredItem<ManaStaffItem<CursedFlamesProjectile>> CURSED_FLAMES = ITEMS.register("cursed_flames", () -> new ManaStaffItem<>(ModRarity.LIGHT_RED, CursedFlamesProjectile::new, 11, 9, 10, 5, 0.04));
    public static final DeferredItem<ManaStaffItem<BallOfFrostProjectile>> FLOWER_OF_FROST = ITEMS.register("flower_of_frost", () -> new ManaStaffItem<>(ModRarity.PINK, BallOfFrostProjectile::new, 12, 11, 9, 4, 0.04));

    public static final DeferredItem<BeeGunItem> BEE_GUN = ITEMS.registerItem("bee_gun", BeeGunItem::new);
    public static final DeferredItem<SpaceGunItem> SPACE_GUN = ITEMS.registerItem("space_gun", SpaceGunItem::new);

    public static void acceptTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> tag) {
        ITEMS.getEntries().forEach(item -> tag.add(item.get()));
    }
}
