package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.StarCannonBulletEntity;
import org.confluence.mod.common.item.BaseBullet;
import org.confluence.mod.common.item.gun.BaseGun;
import org.confluence.mod.common.item.gun.definition.BulletBehaviors;
import org.confluence.mod.common.item.gun.definition.BulletDefinition;
import org.confluence.mod.common.item.gun.definition.BulletImpactEffect;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.confluence.mod.common.item.gun.definition.BulletBehaviors.*;
import static org.confluence.mod.common.item.gun.definition.BulletImpactEffect.*;

public class GunItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);
    public static final List<PortDeferredItem<BaseGun>> GUN_ITEMS = new ArrayList<>();
    public static final List<PortDeferredItem<BaseBullet>> BULLET_ITEMS = new ArrayList<>();

    public static final PortDeferredItem<BaseGun> STAR_CANNON = registerGun("star_cannon", new BaseGun.Builder(4, 14.8f, 1.8f)
            .knockback(0.15f)
            .critical(0.04f)
            .penetrate(-1)
            .rarity(ModRarity.GREEN)
            .gravity(0f)
            .bulletFactory((player, bullet) -> new StarCannonBulletEntity(player, 0f, bullet)));
    public static final PortDeferredItem<BaseGun> BLOWGUN = registerGun("blowgun", new BaseGun.Builder(8, 2.8f, 1.4f).knockback(0.17f).critical(0.04f).gravity(0.08f).rarity(ModRarity.WHITE).automatic());
    public static final PortDeferredItem<BaseGun> SNOWBALL_CANNON = registerGun("snowball_cannon", new BaseGun.Builder(3, 5, 1.4f).knockback(0.05f).critical(0.04f).gravity(0.05f).inaccuracy(3.0f).rarity(ModRarity.BLUE).automatic());
    public static final PortDeferredItem<BaseGun> FLINTLOCK_PISTOL = registerGun("flintlock_pistol", new BaseGun.Builder(5, 7f, 0.7f).knockback(0.05f).critical(0.10f).rarity(ModRarity.BLUE));
    public static final PortDeferredItem<BaseGun> THE_UNDERTAKER = registerGun("the_undertaker", new BaseGun.Builder(9, 5.5f, 0.75f).knockback(0.1f).critical(0.04f).rarity(ModRarity.BLUE));
    public static final PortDeferredItem<BaseGun> MUSKET = registerGun("musket", new BaseGun.Builder(14, 16f, 1.1f).knockback(0.25f).critical(0.12f).rarity(ModRarity.BLUE));
    public static final PortDeferredItem<BaseGun> MINISHARK = registerGun("minishark", new BaseGun.Builder(3, 3.4f, 1.0f).critical(0.04f).inaccuracy(5.0f).rarity(ModRarity.GREEN).automatic());
    public static final PortDeferredItem<BaseGun> BOOMSTICK = registerGun("boomstick", new BaseGun.Builder(13, 7f, 0.66f).knockback(0.28f).critical(0.04f).inaccuracy(8.0f).rarity(ModRarity.GREEN).bullets(3, 4));
    public static final PortDeferredItem<BaseGun> HAND_GUN = registerGun("hand_gun", new BaseGun.Builder(5, 13f, 1.25f).knockback(0.15f).critical(0.04f).rarity(ModRarity.GREEN).handgunAnimations());
    public static final PortDeferredItem<BaseGun> PHOENIX_BLASTER = registerGun("phoenix_blaster", new BaseGun.Builder(4, 16f, 2.50f).knockback(0.10f).critical(0.04f).inaccuracy(2.50f).rarity(ModRarity.ORANGE));
    public static final PortDeferredItem<BaseGun> SHOTGUN = registerGun("shotgun", new BaseGun.Builder(15, 12f, 0.9f).knockback(0.375f).critical(0.04f).inaccuracy(10.0f).rarity(ModRarity.LIGHT_RED).bullets(3, 5));
    public static final PortDeferredItem<BaseGun> TACTICAL_SHOTGUN = registerGun("tactical_shotgun", new BaseGun.Builder(11, 18f, 0.75f).knockback(0.35f).critical(0.04f).inaccuracy(12.0f).rarity(ModRarity.YELLOW).bullets(6, 6));

    // 子弹
    public static final PortDeferredItem<BaseBullet> MUSKET_BULLET = registerBullet("musket_bullet", properties -> new BaseBullet(properties, new BulletDefinition(1.5f, 0.5f, 2f, 0.1f, 0, ModRarity.WHITE, false)));
    public static final PortDeferredItem<BaseBullet> SILVER_BULLET = registerBullet("silver_bullet", properties -> new BaseBullet(properties, new BulletDefinition(2.5f, 0.56f, 2f, 0.15f, 0, ModRarity.WHITE, false).withBehavior(SILVER_PARTICLES).withImpactEffect(SILVER_CROSS)));
    public static final PortDeferredItem<BaseBullet> TUNGSTEN_BULLET = registerBullet("tungsten_bullet", properties -> new BaseBullet(properties, new BulletDefinition(2.5f, 0.56f, 2f, 0.2f, 0, ModRarity.WHITE, false)));
    public static final PortDeferredItem<BaseBullet> METEOR_SHOT = registerBullet("meteor_shot", properties -> new BaseBullet(properties, new BulletDefinition(2f, 0.37f, 2f, 0.05f, 2, ModRarity.BLUE, false).withBehavior(METEOR_RICOCHET)));
    public static final PortDeferredItem<BaseBullet> PARTY_BULLET = registerBullet("party_bullet", properties -> new BaseBullet(properties, new BulletDefinition(3f, 0.63f, 3f, 0.25f, 0, ModRarity.ORANGE, false).withBehavior(BulletBehaviors.PARTY_CONFETTI).withImpactEffect(BulletImpactEffect.PARTY_CONFETTI)));
    public static final PortDeferredItem<BaseBullet> CRYSTAL_BULLET = registerBullet("crystal_bullet", properties -> new BaseBullet(properties, new BulletDefinition(2.5f, 0.62f, 2f, 0.05f, 0, ModRarity.ORANGE, false).withBehavior(CRYSTAL_SPLIT).withImpactEffect(CRYSTAL_IMPACT)));
    public static final PortDeferredItem<BaseBullet> ICHOR_BULLET = registerBullet("ichor_bullet", properties -> new BaseBullet(properties, new BulletDefinition(4.5f, 0.65f, 3f, 0.2f, 0, ModRarity.ORANGE, false).withBehavior(ICHOR_DEBUFF)));
    public static final PortDeferredItem<BaseBullet> CURSED_BULLET = registerBullet("cursed_bullet", properties -> new BaseBullet(properties, new BulletDefinition(4f, 0.62f, 3f, 0.2f, 0, ModRarity.ORANGE, false).withBehavior(CURSED_DEBUFF)));
    public static final PortDeferredItem<BaseBullet> CHLOROPHYTE_BULLET = registerBullet("chlorophyte_bullet", properties -> new BaseBullet(properties, new BulletDefinition(2.5f, 0.62f, 3f, 0.22f, 0, ModRarity.LIME, false).withBehavior(CHLOROPHYTE_HOMING).withImpactEffect(CHLOROPHYTE_IMPACT)));
    public static final PortDeferredItem<BaseBullet> HIGH_VELOCITY_BULLET = registerBullet("high_velocity_bullet", properties -> new BaseBullet(properties, new BulletDefinition(3.5f, 0.5f, 8f, 0.2f, 3, ModRarity.ORANGE, false).withBehavior(HIGH_VELOCITY_DAMAGE_DECAY)));
    public static final PortDeferredItem<BaseBullet> EXPLODING_BULLET = registerBullet("exploding_bullet", properties -> new BaseBullet(properties, new BulletDefinition(3f, 0.58f, 3f, 0.33f, 0, ModRarity.ORANGE, false).withBehavior(EXPLOSIVE)));
    public static final PortDeferredItem<BaseBullet> GOLDEN_BULLET = registerBullet("golden_bullet", properties -> new BaseBullet(properties, new BulletDefinition(3f, 0.57f, 3f, 0.18f, 0, ModRarity.ORANGE, false)));
    public static final PortDeferredItem<BaseBullet> VENOM_BULLET = registerBullet("venom_bullet", properties -> new BaseBullet(properties, new BulletDefinition(5.5f, 0.66f, 3f, 0.21f, 0, ModRarity.ORANGE, false).withBehavior(VENOM_DEBUFF)));
    public static final PortDeferredItem<BaseBullet> NANO_BULLET = registerBullet("nano_bullet", properties -> new BaseBullet(properties, new BulletDefinition(5.5f, 0.57f, 3f, 0.18f, 0, ModRarity.ORANGE, false).withBehavior(NANO_RICOCHET)));
    public static final PortDeferredItem<BaseBullet> ENDLESS_MUSKET_POUCH = registerBullet("endless_musket_pouch", 1, properties -> new BaseBullet(properties, new BulletDefinition(1.5f, 0.5f, 2f, 0.1f, 0, ModRarity.GREEN, true)));
    public static final PortDeferredItem<BaseBullet> LUMINITE_BULLET = registerBullet("luminite_bullet", properties -> new BaseBullet(properties, new BulletDefinition(6f, 0.25f, 6f, 0.15f, -1, ModRarity.CYAN, false).withBehavior(LUMINITE_DAMAGE_DECAY).withImpactEffect(LUMINITE_IMPACT)));

    public static final PortDeferredItem<BaseBullet> DUMMY_BULLET = ITEMS.register("dummy_bullet", () -> new BaseBullet.Dummy(new Item.Properties()));

    private static PortDeferredItem<BaseGun> registerGun(String name, BaseGun.Builder builder) {
        PortDeferredItem<BaseGun> item = ITEMS.register(name, builder::build);
        GUN_ITEMS.add(item);
        return item;
    }

    private static PortDeferredItem<BaseBullet> registerBullet(String name, Function<Item.Properties, BaseBullet> function) {
        return registerBullet(name, 99, function);
    }

    private static PortDeferredItem<BaseBullet> registerBullet(String name, int maxStackSize, Function<Item.Properties, BaseBullet> function) {
        // 泰拉弹药的常规堆叠上限对齐 1.21 侧；无尽弹药袋等特殊物品单独传入自己的上限。
        PortDeferredItem<BaseBullet> item = ITEMS.register(name, () -> function.apply(new Item.Properties().stacksTo(maxStackSize)));
        BULLET_ITEMS.add(item);
        return item;
    }
}
