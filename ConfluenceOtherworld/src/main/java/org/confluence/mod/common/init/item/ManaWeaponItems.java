package org.confluence.mod.common.init.item;

import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.ThrowableDropSelfProjectile;
import org.confluence.mod.common.entity.projectile.mana.*;
import org.confluence.mod.common.entity.projectile.mana.BaseManaStaffProjectileEntity.Variant;
import org.confluence.mod.common.entity.projectile.strip.VilethronProjectile;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.item.mana.ManaStaffItem;
import org.confluence.mod.common.item.mana.WeatherPainItem;
import org.confluence.terraentity.init.TESounds;

public class ManaWeaponItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<ManaStaffItem<WandOfSparkingProjectile>> WAND_OF_SPARKING = ITEMS.register("wand_of_sparking", () -> new ManaStaffItem<>(ModRarity.BLUE, WandOfSparkingProjectile::new, 2, 7.0F, 13, 0.14));
    public static final DeferredItem<ManaStaffItem<WandOfFrostingProjectile>> WAND_OF_FROSTING = ITEMS.register("wand_of_frosting", () -> new ManaStaffItem<>(ModRarity.BLUE, WandOfFrostingProjectile::new, 2, 7.0F, 13, 0.14));
    public static final DeferredItem<ManaStaffItem<ThunderZapperProjectile>> THUNDER_ZAPPER = ITEMS.register("thunder_zapper", () -> new ManaStaffItem<>(ModRarity.BLUE, ThunderZapperProjectile::new, 7, 12, 6, 0.04));
    public static final DeferredItem<ManaStaffItem<BaseManaStaffProjectileEntity>> RUBY_STAFF = ITEMS.register("ruby_staff", () -> new ManaStaffItem<>(ModRarity.BLUE, player -> new BaseManaStaffProjectileEntity(player, Variant.RUBY), 7, 9.0F, 15, 0.04));
    public static final DeferredItem<ManaStaffItem<BaseManaStaffProjectileEntity>> AMBER_STAFF = ITEMS.register("amber_staff", () -> new ManaStaffItem<>(ModRarity.BLUE, player -> new BaseManaStaffProjectileEntity(player, Variant.AMBER), 7, 9.0F, 15, 0.04));
    public static final DeferredItem<ManaStaffItem<BaseManaStaffProjectileEntity>> TOPAZ_STAFF = ITEMS.register("topaz_staff", () -> new ManaStaffItem<>(ModRarity.BLUE, player -> new BaseManaStaffProjectileEntity(player, Variant.TOPAZ), 5, 6.5F, 13, 0.04));
    public static final DeferredItem<ManaStaffItem<BaseManaStaffProjectileEntity>> EMERALD_STAFF = ITEMS.register("emerald_staff", () -> new ManaStaffItem<>(ModRarity.BLUE, player -> new BaseManaStaffProjectileEntity(player, Variant.EMERALD), 6, 8.0F, 14, 0.04));
    public static final DeferredItem<ManaStaffItem<BaseManaStaffProjectileEntity>> SAPPHIRE_STAFF = ITEMS.register("sapphire_staff", () -> new ManaStaffItem<>(ModRarity.BLUE, player -> new BaseManaStaffProjectileEntity(player, Variant.SAPPHIRE), 6, 7.5F, 14, 0.04));
    public static final DeferredItem<ManaStaffItem<BaseManaStaffProjectileEntity>> AMETHYST_STAFF = ITEMS.register("amethyst_staff", () -> new ManaStaffItem<>(ModRarity.BLUE, player -> new BaseManaStaffProjectileEntity(player, Variant.AMETHYST), 5, 6.0F, 13, 0.04));
    public static final DeferredItem<ManaStaffItem<BaseManaStaffProjectileEntity>> DIAMOND_STAFF = ITEMS.register("diamond_staff", () -> new ManaStaffItem<>(ModRarity.BLUE, player -> new BaseManaStaffProjectileEntity(player, Variant.DIAMOND), 8, 9.5F, 15, 0.04));
    public static final DeferredItem<ManaStaffItem<VilethronProjectile>> VILETHRON = ITEMS.register("vilethron", () -> new ManaStaffItem<>(ModRarity.BLUE, VilethronProjectile::new, 10, 8.0F, 18, 0.04));
    public static final DeferredItem<ManaStaffItem<HurtnadoProjectile>> WEATHER_PAIN = ITEMS.register("weather_pain", WeatherPainItem::new);
    /* 魔法飞弹 */
    public static final DeferredItem<ManaStaffItem<WaterStreamProjectile>> AQUA_SCEPTER = ITEMS.register("aqua_scepter", () -> new ManaStaffItem<>(ModRarity.GREEN, WaterStreamProjectile::new, 7, 32.0F, 3, 0.04));
    public static final DeferredItem<ManaStaffItem<BallOfFireProjectile>> FLOWER_OF_FIRE = ITEMS.register("flower_of_fire", () -> new ManaStaffItem<>(ModRarity.ORANGE, BallOfFireProjectile::new, 12, 7.5F, 14, 0.04));
    /* 烈焰火鞭 */
    public static final DeferredItem<ManaStaffItem<WaterBoltProjectile>> WATER_BOLT = ITEMS.register("water_bolt", () -> new ManaStaffItem<>(ModRarity.GREEN, WaterBoltProjectile::new, 10, 4.5F, 10, 0.04));

    public static final DeferredItem<ManaStaffItem<ThrowableDropSelfProjectile>> MAGIC_DAGGER = ITEMS.register("magic_dagger", () -> new ManaStaffItem<>(ModRarity.LIGHT_RED, player -> new ThrowableDropSelfProjectile(ModEntities.MAGIC_DAGGER_PROJECTILE.get(), player.level()), 6, 12, 8, 0.04) {
        @Override
        protected void beforeShoot(ServerPlayer player, ItemStack itemStack, ThrowableDropSelfProjectile projectile) {
            projectile.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
            projectile.setItem(itemStack);
            projectile.setDamage(7);
            projectile.setFlyTicks(10);
            super.beforeShoot(player, itemStack, projectile);
        }

        @Override
        protected void afterShoot(ServerPlayer player, ItemStack itemStack, ThrowableDropSelfProjectile projectile) {
            player.getCooldowns().addCooldown(this, cooldown);
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), TESounds.WAVING.get(), SoundSource.PLAYERS, 1.0F, 1.0F / (player.getRandom().nextFloat() * 0.4F + 0.8F));
        }
    });

//TODO 枪！
//    public static final Supplier<BeeGunItem> BEE_GUN = ITEMS.register("bee_gun", BeeGunItem::new);
//    public static final Supplier<SpaceGunItem> SPACE_GUN = ITEMS.register("space_gun", SpaceGunItem::new);

    public static void acceptTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> tag) {
        ITEMS.getEntries().forEach(item -> tag.add(item.get()));
    }
}
