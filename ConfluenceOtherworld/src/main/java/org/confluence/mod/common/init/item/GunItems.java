package org.confluence.mod.common.init.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.StarCannonBulletEntity;
import org.confluence.mod.common.item.BaseBullet;
import org.confluence.mod.common.item.gun.BaseGun;

import java.util.function.Function;

public class GunItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Confluence.MODID);

    public static final RegistryObject<BaseGun> STAR_CANNON = registerGun("star_cannon", new BaseGun.Builder(4, 14.8f, 1.8f).knockback(0.15f)
            .critical(0.04f)
            .penetrate(-1)
            .rarity(ModRarity.GREEN)
            .gravity(0f)
            .bulletFactory((player, bullet) -> new StarCannonBulletEntity(player, 0f, bullet)));
    public static final RegistryObject<BaseGun> BLOWGUN = registerGun("blowgun", new BaseGun.Builder(8, 2.8f, 1.4f).knockback(0.17f).critical(0.04f).inaccuracy(0.08f).rarity(ModRarity.WHITE));
    public static final RegistryObject<BaseGun> SNOWBALL_CANNON = registerGun("snowball_cannon", new BaseGun.Builder(3, 5, 1.4f).knockback(0.05f).critical(0.04f).gravity(3.0f).inaccuracy(0.05f).rarity(ModRarity.BLUE));
    public static final RegistryObject<BaseGun> FLINTLOCK_PISTOL = registerGun("flintlock_pistol", new BaseGun.Builder(5, 7f, 0.7f).knockback(0.05f).critical(0.10f).rarity(ModRarity.BLUE));
    public static final RegistryObject<BaseGun> THE_UNDERTAKER = registerGun("the_undertaker", new BaseGun.Builder(9, 5.5f, 0.75f).knockback(0.1f).critical(0.04f).rarity(ModRarity.BLUE));
    public static final RegistryObject<BaseGun> MUSKET = registerGun("musket", new BaseGun.Builder(14, 16f, 1.1f).knockback(0.25f).critical(0.12f).rarity(ModRarity.BLUE));
    public static final RegistryObject<BaseGun> MINISHARK = registerGun("minishark", new BaseGun.Builder(3, 3.4f, 1.0f).critical(0.04f).inaccuracy(5.0f).rarity(ModRarity.GREEN));
    public static final RegistryObject<BaseGun> BOOMSTICK = registerGun("boomstick", new BaseGun.Builder(13, 7f, 0.66f).knockback(0.28f).critical(0.04f).inaccuracy(8.0f).rarity(ModRarity.GREEN).bullets(3, 4));
    public static final RegistryObject<BaseGun> HAND_GUN = registerGun("hand_gun", new BaseGun.Builder(5, 13f, 1.25f).knockback(0.15f).critical(0.04f).rarity(ModRarity.GREEN));
    public static final RegistryObject<BaseGun> PHOENIX_BLASTER = registerGun("phoenix_blaster", new BaseGun.Builder(4, 16f, 2.50f).knockback(0.10f).critical(0.04f).rarity(ModRarity.ORANGE));
    public static final RegistryObject<BaseGun> SHOTGUN = registerGun("shotgun", new BaseGun.Builder(15, 12f, 0.9f).knockback(0.375f).critical(0.04f).inaccuracy(10.0f).rarity(ModRarity.LIGHT_RED).bullets(3, 5));
    public static final RegistryObject<BaseGun> TACTICAL_SHOTGUN = registerGun("tactical_shotgun", new BaseGun.Builder(11, 18f, 0.75f).knockback(0.35f).critical(0.04f).inaccuracy(12.0f).rarity(ModRarity.YELLOW).bullets(6, 6));

    // 子弹
    public static final RegistryObject<BaseBullet> MUSKET_BULLET = registerBullet("musket_bullet", properties -> new BaseBullet(properties.stacksTo(99), 1.5f, 0.5f, 2, 0.1f, ModRarity.WHITE, 0, false));
    public static final RegistryObject<BaseBullet> SILVER_BULLET = registerBullet("silver_bullet", properties -> new BaseBullet(properties.stacksTo(99), 2.5f, 0.56f, 2, 0.15f, ModRarity.WHITE, 0, false));
    public static final RegistryObject<BaseBullet> TUNGSTEN_BULLET = registerBullet("tungsten_bullet", properties -> new BaseBullet(properties.stacksTo(99), 2.5f, 0.56f, 2, 0.2f, ModRarity.WHITE, 0, false));
    public static final RegistryObject<BaseBullet> METEOR_SHOT = registerBullet("meteor_shot", properties -> new BaseBullet(properties.stacksTo(99), 2f, 0.37f, 2, 0.05f, ModRarity.BLUE, 1, false));
    public static final RegistryObject<BaseBullet> PARTY_BULLET = registerBullet("party_bullet", properties -> new BaseBullet(properties.stacksTo(99), 3f, 0.63f, 3, 0.25f, ModRarity.ORANGE, 0, false));
    public static final RegistryObject<BaseBullet> CRYSTAL_BULLET = registerBullet("crystal_bullet", properties -> new BaseBullet(properties.stacksTo(99), 2.5f, 0.62f, 2, 0.05f, ModRarity.ORANGE, 0, false));
    public static final RegistryObject<BaseBullet> ICHOR_BULLET = registerBullet("ichor_bullet", properties -> new BaseBullet(properties.stacksTo(99), 4.5f, 0.65f, 3, 0.2f, ModRarity.ORANGE, 0, false));
    public static final RegistryObject<BaseBullet> CURSED_BULLET = registerBullet("cursed_bullet", properties -> new BaseBullet(properties.stacksTo(99), 4f, 0.62f, 3, 0.2f, ModRarity.ORANGE, 0, false));
    public static final RegistryObject<BaseBullet> CHLOROPHYTE_BULLET = registerBullet("chlorophyte_bullet", properties -> new BaseBullet(properties.stacksTo(99), 2.5f, 0.62f, 3, 0.22f, ModRarity.LIME, 0, false));
    public static final RegistryObject<BaseBullet> HIGH_VELOCITY_BULLET = registerBullet("high_velocity_bullet", properties -> new BaseBullet(properties.stacksTo(99), 3.5f, 0.5f, 8, 0.2f, ModRarity.ORANGE, 2, false));
    public static final RegistryObject<BaseBullet> EXPLODING_BULLET = registerBullet("exploding_bullet", properties -> new BaseBullet(properties.stacksTo(99), 3f, 0.58f, 3, 0.33f, ModRarity.ORANGE, 0, false));
    public static final RegistryObject<BaseBullet> GOLDEN_BULLET = registerBullet("golden_bullet", properties -> new BaseBullet(properties.stacksTo(99), 3f, 0.57f, 3, 0.18f, ModRarity.ORANGE, 0, false));
    public static final RegistryObject<BaseBullet> VENOM_BULLET = registerBullet("venom_bullet", properties -> new BaseBullet(properties.stacksTo(99), 5.5f, 0.66f, 3, 0.21f, ModRarity.ORANGE, 0, false));
    public static final RegistryObject<BaseBullet> NANO_BULLET = registerBullet("nano_bullet", properties -> new BaseBullet(properties.stacksTo(99), 5.5f, 0.57f, 3, 0.18f, ModRarity.ORANGE, 0, false));
    public static final RegistryObject<BaseBullet> ENDLESS_MUSKET_POUCH = registerBullet("endless_musket_pouch", properties -> new BaseBullet(properties.stacksTo(1), 1.5f, 0.5f, 2, 0.1f, ModRarity.GREEN, 0, true));
    public static final RegistryObject<BaseBullet> LUMINITE_BULLET = registerBullet("luminite_bullet", properties -> new BaseBullet(properties.stacksTo(99), 6f, 0.25f, 6, 0.15f, ModRarity.CYAN, -1, false));
    public static final RegistryObject<BaseBullet.EmptyBullet> EMPTY_BULLET = ITEMS.register("empty_bullet", () -> new BaseBullet.EmptyBullet(new Item.Properties()));

    private static RegistryObject<BaseGun> registerGun(String name, BaseGun.Builder builder) {
        return ITEMS.register(name, builder::build);
    }

    private static <T extends Item> RegistryObject<T> registerBullet(String name, Function<Item.Properties, T> function) {
        return ITEMS.register(name, () -> function.apply(new Item.Properties()));
    }
}
