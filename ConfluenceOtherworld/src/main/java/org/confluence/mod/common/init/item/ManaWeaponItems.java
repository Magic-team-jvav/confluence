package org.confluence.mod.common.init.item;

import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.mana.*;
import org.confluence.mod.common.entity.projectile.mana.BaseManaStaffProjectileEntity.Variant;
import org.confluence.mod.common.entity.projectile.strip.VilethronProjectile;
import org.confluence.mod.common.item.gun.BeeGunItem;
import org.confluence.mod.common.item.gun.SpaceGunItem;
import org.confluence.mod.common.item.mana.ManaStaffItem;
import org.confluence.mod.common.item.mana.WeatherPainItem;
import org.confluence.terra_curio.common.component.ModRarity;

import java.util.function.Supplier;

public class ManaWeaponItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final Supplier<ManaStaffItem<?>> WAND_OF_SPARKING = ITEMS.register("wand_of_sparking", () -> new ManaStaffItem<>(ModRarity.BLUE, WandOfSparkingProjectile::new, 2, 7.0F, 26, 0.14));
    public static final Supplier<ManaStaffItem<?>> WAND_OF_FROSTING = ITEMS.register("wand_of_frosting", () -> new ManaStaffItem<>(ModRarity.BLUE, WandOfFrostingProjectile::new, 2, 7.0F, 26, 0.14));
    public static final Supplier<ManaStaffItem<?>> THUNDER_ZAPPER = ITEMS.register("thunder_zapper", () -> new ManaStaffItem<>(ModRarity.BLUE, ThunderZapperProjectile::new, 7, 16, 6, 0.04));
    public static final Supplier<ManaStaffItem<?>> RUBY_STAFF = ITEMS.register("ruby_staff", () -> new ManaStaffItem<>(ModRarity.BLUE, player -> new BaseManaStaffProjectileEntity(player, Variant.RUBY), 7, 9.0F, 28, 0.04));
    public static final Supplier<ManaStaffItem<?>> AMBER_STAFF = ITEMS.register("amber_staff", () -> new ManaStaffItem<>(ModRarity.BLUE, player -> new BaseManaStaffProjectileEntity(player, Variant.AMBER), 7, 9.0F, 28, 0.04));
    public static final Supplier<ManaStaffItem<?>> TOPAZ_STAFF = ITEMS.register("topaz_staff", () -> new ManaStaffItem<>(ModRarity.BLUE, player -> new BaseManaStaffProjectileEntity(player, Variant.TOPAZ), 5, 6.5F, 36, 0.04));
    public static final Supplier<ManaStaffItem<?>> EMERALD_STAFF = ITEMS.register("emerald_staff", () -> new ManaStaffItem<>(ModRarity.BLUE, player -> new BaseManaStaffProjectileEntity(player, Variant.EMERALD), 6, 8.0F, 32, 0.04));
    public static final Supplier<ManaStaffItem<?>> SAPPHIRE_STAFF = ITEMS.register("sapphire_staff", () -> new ManaStaffItem<>(ModRarity.BLUE, player -> new BaseManaStaffProjectileEntity(player, Variant.SAPPHIRE), 6, 7.5F, 34, 0.04));
    public static final Supplier<ManaStaffItem<?>> AMETHYST_STAFF = ITEMS.register("amethyst_staff", () -> new ManaStaffItem<>(ModRarity.BLUE, player -> new BaseManaStaffProjectileEntity(player, Variant.AMETHYST), 5, 6.0F, 37, 0.04));
    public static final Supplier<ManaStaffItem<?>> DIAMOND_STAFF = ITEMS.register("diamond_staff", () -> new ManaStaffItem<>(ModRarity.BLUE, player -> new BaseManaStaffProjectileEntity(player, Variant.DIAMOND), 8, 9.5F, 26, 0.04));
    public static final Supplier<ManaStaffItem<?>> VILETHRON = ITEMS.register("vilethron", () -> new ManaStaffItem<>(ModRarity.BLUE, VilethronProjectile::new, 10, 8.0F, 28, 0.04));
    public static final Supplier<ManaStaffItem<?>> WEATHER_PAIN = ITEMS.register("weather_pain", WeatherPainItem::new);
    /* 魔法飞弹 */
    public static final Supplier<ManaStaffItem<?>> AQUA_SCEPTER = ITEMS.register("aqua_scepter", () -> new ManaStaffItem<>(ModRarity.GREEN, WaterStreamProjectile::new, 7, 32.0F, 3, 0.04));
    public static final Supplier<ManaStaffItem<?>> FLOWER_OF_FIRE = ITEMS.register("flower_of_fire", () -> new ManaStaffItem<>(ModRarity.ORANGE, BallOfFireProjectile::new, 12, 7.5F, 16, 0.04));
    /* 烈焰火鞭 */
    public static final Supplier<ManaStaffItem<?>> WATER_BOLT = ITEMS.register("water_bolt", () -> new ManaStaffItem<>(ModRarity.GREEN, WaterBoltProjectile::new, 10, 4.5F, 17, 0.04));

    public static final Supplier<BeeGunItem> BEE_GUN = ITEMS.register("bee_gun", BeeGunItem::new);
    public static final Supplier<SpaceGunItem> SPACE_GUN = ITEMS.register("space_gun", SpaceGunItem::new);

    public static void acceptTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> tag) {
        ITEMS.getEntries().forEach(item -> tag.add(item.get()));
    }
}
