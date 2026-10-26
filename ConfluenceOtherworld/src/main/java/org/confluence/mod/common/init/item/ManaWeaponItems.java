package org.confluence.mod.common.init.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.mana.*;
import org.confluence.mod.common.entity.projectile.mana.BaseManaStaffProjectileEntity.Variant;
import org.confluence.mod.common.entity.projectile.strip.CrystalVileShardProjectile;
import org.confluence.mod.common.entity.projectile.strip.VilethronProjectile;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.item.gun.BeeGunItem;
import org.confluence.mod.common.item.gun.SpaceGunItem;
import org.confluence.mod.common.item.mana.*;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

public class ManaWeaponItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    // 魔法武器数值参考为 原数值÷2 +5 为基础值
    public static final PortDeferredItem<ManaStaffItem<WandOfSparkingProjectile>> WAND_OF_SPARKING = registerItemWithEnglishName("wand_of_sparking", "火花魔棒", "Wand of Sparking", () -> new ManaStaffItem<>(ModRarity.BLUE, WandOfSparkingProjectile::new, 5.6F, 2, 7.0F, 13, 0.14));
    public static final PortDeferredItem<ManaStaffItem<WandOfFrostingProjectile>> WAND_OF_FROSTING = registerItemWithEnglishName("wand_of_frosting", "结霜魔棒", "Wand of Frosting", () -> new ManaStaffItem<>(ModRarity.BLUE, WandOfFrostingProjectile::new, 10.0F, 2, 7.0F, 13, 0.14));
    public static final PortDeferredItem<ManaStaffItem<BaseManaStaffProjectileEntity>> RUBY_STAFF = registerItem("ruby_staff", "红玉法杖", () -> new ManaStaffItem<>(ModRarity.BLUE, player -> new BaseManaStaffProjectileEntity(player, Variant.RUBY), 15.4F, 7, 9.0F, 15, 0.04));
    public static final PortDeferredItem<ManaStaffItem<BaseManaStaffProjectileEntity>> AMBER_STAFF = registerItem("amber_staff", "琥珀法杖", () -> new ManaStaffItem<>(ModRarity.BLUE, player -> new BaseManaStaffProjectileEntity(player, Variant.AMBER), 15.4F, 7, 9.0F, 15, 0.04));
    public static final PortDeferredItem<ManaStaffItem<BaseManaStaffProjectileEntity>> TOPAZ_STAFF = registerItem("topaz_staff", "黄玉法杖", () -> new ManaStaffItem<>(ModRarity.BLUE, player -> new BaseManaStaffProjectileEntity(player, Variant.TOPAZ), 13.4F, 5, 6.5F, 13, 0.04));
    public static final PortDeferredItem<ManaStaffItem<BaseManaStaffProjectileEntity>> JADE_STAFF = registerItem("jade_staff", "翡翠法杖", () -> new ManaStaffItem<>(ModRarity.BLUE, player -> new BaseManaStaffProjectileEntity(player, Variant.JADE), 14.6F, 6, 8.0F, 14, 0.04));
    public static final PortDeferredItem<ManaStaffItem<BaseManaStaffProjectileEntity>> SAPPHIRE_STAFF = registerItem("sapphire_staff", "蓝玉法杖", () -> new ManaStaffItem<>(ModRarity.BLUE, player -> new BaseManaStaffProjectileEntity(player, Variant.SAPPHIRE), 14.2F, 6, 7.5F, 14, 0.04));
    public static final PortDeferredItem<ManaStaffItem<BaseManaStaffProjectileEntity>> AMETHYST_STAFF = registerItem("amethyst_staff", "紫晶法杖", () -> new ManaStaffItem<>(ModRarity.BLUE, player -> new BaseManaStaffProjectileEntity(player, Variant.AMETHYST), 13.0F, 5, 6.0F, 13, 0.04));
    public static final PortDeferredItem<ManaStaffItem<BaseManaStaffProjectileEntity>> DIAMOND_STAFF = registerItem("diamond_staff", "钻石法杖", () -> new ManaStaffItem<>(ModRarity.BLUE, player -> new BaseManaStaffProjectileEntity(player, Variant.DIAMOND), 16.2F, 8, 9.5F, 15, 0.04));
    public static final PortDeferredItem<ManaStaffItem<ThunderZapperProjectile>> THUNDER_ZAPPER = registerItem("thunder_zapper", "霹雳法杖", () -> new ManaStaffItem<>(ModRarity.BLUE, ThunderZapperProjectile::new, 14.8F, 6, 12, 6, 0.04));
    public static final PortDeferredItem<CloudRodItem> CRIMSON_ROD = registerItem("crimson_rod", "猩红魔杖", () -> new CloudRodItem(ModRarity.BLUE, player -> new CloudProjectile(ModEntities.BLOOD_CLOUD.get(), ModEntities.BLOOD_RAIN.get(), player, 3, 2), 8, 30, 12, 3, 0.04));
    public static final PortDeferredItem<ManaStaffItem<VilethronProjectile>> VILETHRON = registerItem("vilethron", "魔刺", () -> new ManaStaffItem<>(ModRarity.BLUE, VilethronProjectile::new, 4.5F, 10, 8.0F, 18, 0.04));
    public static final PortDeferredItem<ManaStaffItem<HurtnadoProjectile>> WEATHER_PAIN = registerItem("weather_pain", "天候棒", WeatherPainItem::new);
    public static final PortDeferredItem<SpaceGunItem> SPACE_GUN = registerItem("space_gun", "太空枪", () -> new SpaceGunItem(new Item.Properties()));
    public static final PortDeferredItem<BeeGunItem> BEE_GUN = registerItem("bee_gun", "蜜蜂枪", () -> new BeeGunItem(new Item.Properties()));
    public static final PortDeferredItem<ManaStaffItem<SkullProjectile>> BOOK_OF_SKULLS = registerItemWithEnglishName("book_of_skulls", "骷髅头法书", "Book of Skulls", () -> new ManaStaffItem<>(ModRarity.GREEN, SkullProjectile::new, 18.2F, 18, 3.5F, 2, 0.04));
    public static final PortDeferredItem<ManaStaffItem<WaterBoltProjectile>> WATER_BOLT = registerItem("water_bolt", "水矢", () -> new ManaStaffItem<>(ModRarity.GREEN, WaterBoltProjectile::new, 19.5F, 10, 4.5F, 7, 0.04));
    public static final PortDeferredItem<AquaScepterItem> AQUA_SCEPTER = registerItem("aqua_scepter", "海蓝权杖", AquaScepterItem::new);
    public static final PortDeferredItem<MagicMissileItem> MAGIC_MISSILE = registerItem("magic_missile", "魔法飞弹", MagicMissileItem::new);
    public static final PortDeferredItem<ManaStaffItem<DemonScytheProjectile>> DEMON_SCYTHE = registerItem("demon_scythe", "恶魔之镰", () -> new ManaStaffItem<>(ModRarity.ORANGE, DemonScytheProjectile::new, 25.8f, 14, 0.2F, 3, 0.04));
    public static final PortDeferredItem<ManaStaffItem<BallOfFireProjectile>> FLOWER_OF_FIRE = registerItemWithEnglishName("flower_of_fire", "火之花", "Flower of Fire", () -> new ManaStaffItem<>(ModRarity.ORANGE, BallOfFireProjectile::new, 29F, 12, 7.5F, 7, 0.04));
    public static final PortDeferredItem<FlamelashItem> FLAMELASH = registerItem("flamelash", "烈焰火鞭", FlamelashItem::new);
    public static final PortDeferredItem<CloudRodItem> NIMBUS_ROD = registerItem("nimbus_rod", "雨云魔杖", () -> new CloudRodItem(ModRarity.LIGHT_PURPLE, player -> new CloudProjectile(ModEntities.RAIN_CLOUD.get(), ModEntities.RAIN.get(), player, 2, 5), 15, 30, 16, 3, 0.04).setMaxCloud(2));
    public static final PortDeferredItem<ManaStaffItem<BallOfFrostProjectile>> FLOWER_OF_FROST = registerItemWithEnglishName("flower_of_frost", "寒霜之花", "Flower of Frost", () -> new ManaStaffItem<>(ModRarity.PINK, BallOfFrostProjectile::new, 35, 11, 12, 4, 0.04));
    public static final PortDeferredItem<ManaStaffItem<MagicDaggerProjectile>> MAGIC_DAGGER = registerItem("magic_dagger", "魔法飞刀", MagicDaggerItem::new);
    public static final PortDeferredItem<ManaStaffItem<CrystalStormProjectile>> CRYSTAL_STORM = registerItem("crystal_storm", "水晶风暴", () -> new ManaStaffItem<>(ModRarity.LIGHT_RED, CrystalStormProjectile::new, 23, 5, 22, 3, 0.04));
    public static final PortDeferredItem<ManaStaffItem<CursedFlamesProjectile>> CURSED_FLAMES = registerItem("cursed_flames", "诅咒焰", () -> new ManaStaffItem<>(ModRarity.LIGHT_RED, CursedFlamesProjectile::new, 32, 9, 13, 5, 0.04));
    public static final PortDeferredItem<GoldenShowerItem> GOLDEN_SHOWER = registerItem("golden_shower", "黄金雨", GoldenShowerItem::new);
    public static final PortDeferredItem<ManaStaffItem<CrystalVileShardProjectile>> CRYSTAL_VILE_SHARD = registerItem("crystal_vile_shard", "魔晶碎块", CrystalVileShardItem::new);
    public static final PortDeferredItem<RainbowRodItem> RAINBOW_ROD = registerItem("rainbow_rod", "彩虹魔杖", RainbowRodItem::new);
    public static final PortDeferredItem<SkyFractureItem> SKY_FRACTURE = registerItem("sky_fracture", "裂天剑", SkyFractureItem::new);
    public static final PortDeferredItem<ManaStaffItem<CrystalChargeProjectile>> CRYSTAL_SERPENT = registerItem("crystal_serpent", "水晶蛇", () -> new ManaStaffItem<>(ModRarity.PINK, CrystalChargeProjectile::new, 25, 9, 8, 10, 0.04).withTooltip(Component.translatable("tooltip.item.confluence.crystal_serpent.0").withStyle(ChatFormatting.GRAY)));
    /// 欧泊法杖：洪流弹幕落地后贴地奔涌，折返时伤害翻倍，并额外吃 50% 近战攻击力。
    public static final PortDeferredItem<OpalStaffItem> OPAL_STAFF = registerItem("opal_staff", "欧泊法杖", () -> new OpalStaffItem(ModRarity.GREEN, OpalStaffProjectile::new, 20.0F, 8, 12, 12, 0.04));
    /// 昏晓：恶魔祭坛：永夜 + 极昼。
    public static final PortDeferredItem<DuskDawnItem> DUSK_DAWN = registerItem("dusk_dawn", "昏晓", () -> new DuskDawnItem(ModRarity.PURPLE));
    /// 风页：一次释放三发上飞后追踪的风弹。
    public static final PortDeferredItem<WindPageItem> WIND_PAGE = registerItem("wind_page", "风页", () -> new WindPageItem(ModRarity.PURPLE));
    /// 风眼：制造持续吸附敌人的大范围旋风。
    public static final PortDeferredItem<WindEyeItem> WIND_EYE = registerItem("wind_eye", "风眼", () -> new WindEyeItem(ModRarity.YELLOW));
    /// 星乐：长按聚星，松开爆出追踪坠星。
    public static final PortDeferredItem<StarMusicItem> STAR_MUSIC = registerItem("star_music", "星乐", () -> new StarMusicItem(ModRarity.YELLOW));
    /// 星体研究汇编：初始无法攻击，需要装订蕴魔书页才能逐步解锁附加弹幕。
    public static final PortDeferredItem<StellarStudiesCompilationItem> STELLAR_STUDIES_COMPILATION = registerItem("stellar_studies_compilation", "星体研究汇编", () -> new StellarStudiesCompilationItem(ModRarity.BLUE));

    private static <T extends Item> PortDeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        PortDeferredItem<T> entry = ITEMS.register(registryId, factory::get);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Item> PortDeferredItem<T> registerItemWithEnglishName(String registryId, String chineseName, String englishName, Supplier<T> factory) {
        PortDeferredItem<T> entry = ITEMS.register(registryId, factory::get);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }
}
