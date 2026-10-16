package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import org.confluence.lib.api.animation.first_person.HandAnimationChannel;
import org.confluence.lib.api.animation.first_person.HandAnimationProfile;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.BaseBullet;
import org.confluence.mod.common.item.gun.BaseGun;
import org.confluence.mod.common.item.gun.StarCannonItem;
import org.confluence.mod.common.item.gun.definition.BulletBehavior;
import org.confluence.mod.common.item.gun.definition.BulletDefinition;
import org.confluence.mod.common.item.gun.definition.BulletImpactEffect;
import org.confluence.mod.common.item.gun.definition.GunDefinition;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;
import software.bernie.geckolib.core.animation.Animation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;
import static org.confluence.mod.common.item.gun.definition.BulletImpactEffect.*;

public class GunItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);
    public static final List<PortDeferredItem<BaseGun>> GUN_ITEMS = new ArrayList<>();
    public static final List<PortDeferredItem<BaseBullet>> BULLET_ITEMS = new ArrayList<>();

    public static final PortDeferredItem<BaseGun> STAR_CANNON = registerGun("star_cannon", "星星炮", StarCannonItem::new);
    public static final PortDeferredItem<BaseGun> BLOWGUN = registerGun("blowgun", "吹箭筒", properties -> new BaseGun(properties, GunDefinition.automatic(8, 2.8F, 1.4F, 0.17F, 0.04F, 0, 0.0F, ModRarity.WHITE).withGravity(0.08F)));
    public static final PortDeferredItem<BaseGun> SNOWBALL_CANNON = registerGun("snowball_cannon", "雪球炮", properties -> new BaseGun(
            properties,
            GunDefinition.automatic(3, 5.0F, 1.4F, 0.05F, 0.04F, 0, 3.0F, ModRarity.BLUE).withGravity(0.05F)));
    public static final PortDeferredItem<BaseGun> FLINTLOCK_PISTOL = registerGun("flintlock_pistol", "燧发枪", properties -> new BaseGun(properties, GunDefinition.manual(5, 7.0F, 0.7F, 0.05F, 0.10F, 0, 0.0F, ModRarity.BLUE)));
    public static final PortDeferredItem<BaseGun> THE_UNDERTAKER = registerGun("the_undertaker", "夺命枪", properties -> new BaseGun(properties, GunDefinition.manual(9, 5.5F, 0.75F, 0.1F, 0.04F, 0, 0.0F, ModRarity.BLUE)));
    public static final PortDeferredItem<BaseGun> MUSKET = registerGun("musket", "火枪", properties -> new BaseGun(properties, GunDefinition.manual(14, 16.0F, 1.1F, 0.25F, 0.12F, 0, 0.0F, ModRarity.BLUE)));
    public static final PortDeferredItem<BaseGun> MINISHARK = registerGun("minishark", "迷你鲨", properties -> new BaseGun(properties, GunDefinition.automatic(3, 3.4F, 1.0F, 0.0F, 0.04F, 0, 5.0F, ModRarity.GREEN)));
    public static final PortDeferredItem<BaseGun> BOOMSTICK = registerGun("boomstick", "三发猎枪", properties -> new BaseGun(
            properties,
            GunDefinition.manual(13, 7.0F, 0.66F, 0.28F, 0.04F, 0, 8.0F, ModRarity.GREEN).withShotgunPattern(3, 4)));
    public static final PortDeferredItem<BaseGun> HAND_GUN = registerGunWithEnglishName("hand_gun", "手枪", "Handgun", properties -> new BaseGun(
            properties,
            GunDefinition.manual(5, 13.0F, 1.25F, 0.15F, 0.04F, 0, 0.0F, ModRarity.GREEN),
            HandAnimationProfile.builder()
                    .channel(HandAnimationChannel.builder("hand_pose")
                            .idle("static_idle")
                            .animation(BaseGun.DRAW_ACTION, "draw", Animation.LoopType.PLAY_ONCE)
                            .animation(BaseGun.PUT_AWAY_ACTION, "put_away", Animation.LoopType.PLAY_ONCE)
                            .animation(BaseGun.INSPECT_ACTION, "inspect", Animation.LoopType.PLAY_ONCE)
                            .build())
                    .channel(HandAnimationChannel.builder("weapon_action")
                            .animation(BaseGun.SHOOT_ACTION, "shoot", Animation.LoopType.PLAY_ONCE)
                            .build())
                    .build()));
    public static final PortDeferredItem<BaseGun> PHOENIX_BLASTER = registerGun("phoenix_blaster", "凤凰爆破枪", properties -> new BaseGun(properties, GunDefinition.manual(4, 16.0F, 2.5F, 0.1F, 0.04F, 0, 2.5F, ModRarity.ORANGE)));
    public static final PortDeferredItem<BaseGun> SHOTGUN = registerGun("shotgun", "霰弹枪", properties -> new BaseGun(
            properties,
            GunDefinition.manual(15, 12.0F, 0.9F, 0.375F, 0.04F, 0, 10.0F, ModRarity.LIGHT_RED).withShotgunPattern(3, 5)));
    public static final PortDeferredItem<BaseGun> TACTICAL_SHOTGUN = registerGun("tactical_shotgun", "战术霰弹枪", properties -> new BaseGun(
            properties,
            GunDefinition.manual(11, 18.0F, 0.75F, 0.35F, 0.04F, 0, 12.0F, ModRarity.YELLOW).withShotgunPattern(6, 6)));

    public static final PortDeferredItem<BaseBullet> MUSKET_BULLET = registerBullet("musket_bullet", "火枪子弹", properties -> new BaseBullet(properties, new BulletDefinition(1.5F, 0.5F, 2.0F, 0.1F, 0, ModRarity.WHITE, false)));
    public static final PortDeferredItem<BaseBullet> SILVER_BULLET = registerBullet("silver_bullet", "银子弹", properties -> new BaseBullet(
            properties,
            new BulletDefinition(2.5F, 0.56F, 2.0F, 0.15F, 0, ModRarity.WHITE, false).withBehavior(BulletBehavior.SILVER_PARTICLES).withImpactEffect(SILVER_CROSS)));
    public static final PortDeferredItem<BaseBullet> TUNGSTEN_BULLET = registerBullet("tungsten_bullet", "钨子弹", properties -> new BaseBullet(properties, new BulletDefinition(2.5F, 0.56F, 2.0F, 0.2F, 0, ModRarity.WHITE, false)));
    public static final PortDeferredItem<BaseBullet> METEOR_SHOT = registerBullet("meteor_shot", "流星弹", properties -> new BaseBullet(
            properties,
            new BulletDefinition(2.0F, 0.37F, 2.0F, 0.05F, 2, ModRarity.BLUE, false).withBehavior(BulletBehavior.METEOR_RICOCHET)));
    public static final PortDeferredItem<BaseBullet> PARTY_BULLET = registerBullet("party_bullet", "派对弹", properties -> new BaseBullet(
            properties,
            new BulletDefinition(3.0F, 0.63F, 3.0F, 0.25F, 0, ModRarity.ORANGE, false)
                    .withBehavior(BulletBehavior.PARTY_CONFETTI)
                    .withImpactEffect(BulletImpactEffect.PARTY_CONFETTI)));
    public static final PortDeferredItem<BaseBullet> CRYSTAL_BULLET = registerBullet("crystal_bullet", "水晶子弹", properties -> new BaseBullet(
            properties,
            new BulletDefinition(2.5F, 0.62F, 2.0F, 0.05F, 0, ModRarity.ORANGE, false).withBehavior(BulletBehavior.CRYSTAL_SPLIT).withImpactEffect(CRYSTAL_IMPACT)));
    public static final PortDeferredItem<BaseBullet> ICHOR_BULLET = registerBullet("ichor_bullet", "灵液弹", properties -> new BaseBullet(
            properties,
            new BulletDefinition(4.5F, 0.65F, 3.0F, 0.2F, 0, ModRarity.ORANGE, false).withBehavior(BulletBehavior.ICHOR_DEBUFF)));
    public static final PortDeferredItem<BaseBullet> CURSED_BULLET = registerBullet("cursed_bullet", "诅咒弹", properties -> new BaseBullet(
            properties,
            new BulletDefinition(4.0F, 0.62F, 3.0F, 0.2F, 0, ModRarity.ORANGE, false).withBehavior(BulletBehavior.CURSED_DEBUFF)));
    public static final PortDeferredItem<BaseBullet> CHLOROPHYTE_BULLET = registerBullet("chlorophyte_bullet", "叶绿弹", properties -> new BaseBullet(
            properties,
            new BulletDefinition(2.5F, 0.62F, 3.0F, 0.22F, 0, ModRarity.LIME, false)
                    .withBehavior(BulletBehavior.CHLOROPHYTE_HOMING)
                    .withImpactEffect(CHLOROPHYTE_IMPACT)));
    public static final PortDeferredItem<BaseBullet> HIGH_VELOCITY_BULLET = registerBullet("high_velocity_bullet", "高速子弹", properties -> new BaseBullet(
            properties,
            new BulletDefinition(3.5F, 0.5F, 8.0F, 0.2F, 3, ModRarity.ORANGE, false).withBehavior(BulletBehavior.HIGH_VELOCITY_DAMAGE_DECAY)));
    public static final PortDeferredItem<BaseBullet> EXPLODING_BULLET = registerBullet("exploding_bullet", "爆破弹", properties -> new BaseBullet(
            properties,
            new BulletDefinition(3.0F, 0.58F, 3.0F, 0.33F, 0, ModRarity.ORANGE, false).withBehavior(BulletBehavior.EXPLOSIVE)));
    public static final PortDeferredItem<BaseBullet> GOLDEN_BULLET = registerBullet("golden_bullet", "金子弹", properties -> new BaseBullet(properties, new BulletDefinition(3.0F, 0.57F, 3.0F, 0.18F, 0, ModRarity.ORANGE, false)));
    public static final PortDeferredItem<BaseBullet> VENOM_BULLET = registerBullet("venom_bullet", "毒液弹", properties -> new BaseBullet(
            properties,
            new BulletDefinition(5.5F, 0.66F, 3.0F, 0.21F, 0, ModRarity.ORANGE, false).withBehavior(BulletBehavior.VENOM_DEBUFF)));
    public static final PortDeferredItem<BaseBullet> NANO_BULLET = registerBullet("nano_bullet", "纳米弹", properties -> new BaseBullet(
            properties,
            new BulletDefinition(5.5F, 0.57F, 3.0F, 0.18F, 0, ModRarity.ORANGE, false).withBehavior(BulletBehavior.NANO_RICOCHET)));
    /// 泰拉瑞亚里「无尽火枪袋」的稀有度是 Green（层 2）。
    public static final PortDeferredItem<BaseBullet> ENDLESS_MUSKET_POUCH = registerBullet("endless_musket_pouch", "无尽火枪袋", 1, properties -> new BaseBullet(properties,
                    new BulletDefinition(1.5F, 0.5F, 2.0F, 0.1F, 0, ModRarity.GREEN, true)));
    public static final PortDeferredItem<BaseBullet> LUMINITE_BULLET = registerBullet("luminite_bullet", "夜明弹", properties -> new BaseBullet(
            properties,
            new BulletDefinition(6.0F, 0.25F, 6.0F, 0.15F, -1, ModRarity.CYAN, false)
                    .withBehavior(BulletBehavior.LUMINITE_DAMAGE_DECAY)
                    .withImpactEffect(LUMINITE_IMPACT)));

    public static final PortDeferredItem<BaseBullet> DUMMY_BULLET = registerItem("dummy_bullet", "占位子弹", () -> new BaseBullet.Dummy(new Item.Properties()));

    private static PortDeferredItem<BaseGun> registerGun(String name, Function<Item.Properties, BaseGun> factory) {
        PortDeferredItem<BaseGun> item = ITEMS.register(name, () -> factory.apply(new Item.Properties()));
        GUN_ITEMS.add(item);
        return item;
    }

    private static PortDeferredItem<BaseBullet> registerBullet(String name, int maxStackSize, Function<Item.Properties, BaseBullet> function) {
        PortDeferredItem<BaseBullet> item = ITEMS.register(name, () -> function.apply(new Item.Properties().stacksTo(maxStackSize)));
        BULLET_ITEMS.add(item);
        return item;
    }

    private static PortDeferredItem<BaseGun> registerGun(String registryId, String chineseName, Function<Item.Properties, BaseGun> factory) {
        PortDeferredItem<BaseGun> entry = registerGun(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static PortDeferredItem<BaseBullet> registerBullet(String registryId, String chineseName, Function<Item.Properties, BaseBullet> function) {
        PortDeferredItem<BaseBullet> entry = registerBullet(registryId, 99, function);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static PortDeferredItem<BaseBullet> registerBullet(String registryId, String chineseName, int maxStackSize, Function<Item.Properties, BaseBullet> function) {
        PortDeferredItem<BaseBullet> entry = registerBullet(registryId, maxStackSize, function);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Item> PortDeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        PortDeferredItem<T> entry = ITEMS.register(registryId, factory::get);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static PortDeferredItem<BaseGun> registerGunWithEnglishName(String registryId, String chineseName, String englishName, Function<Item.Properties, BaseGun> factory) {
        PortDeferredItem<BaseGun> entry = registerGun(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }
}
