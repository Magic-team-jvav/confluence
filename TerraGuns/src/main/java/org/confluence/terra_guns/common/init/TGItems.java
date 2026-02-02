package org.confluence.terra_guns.common.init;

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.terra_guns.TerraGuns;
import org.confluence.terra_guns.common.item.bullet.BaseBullet;
import org.confluence.terra_guns.common.item.gun.BaseGun;
import org.confluence.terra_guns.common.item.gun.CustomGun;
import org.confluence.terra_guns.common.item.gun.Shotgun;

/**
 * tr2mc数值转换
 * 时间 三分之一
 * 伤害 二分之一，然后适当偏下0.5f-1f的范围
 * 速度 八分之一
 * 击退 二十分之一
 */
public class TGItems {
    public static final DeferredRegister.Items GUNS = DeferredRegister.createItems(TerraGuns.MODID);
    public static final DeferredRegister.Items BULLETS = DeferredRegister.createItems(TerraGuns.MODID);
    public static final DeferredRegister.Items OTHER = DeferredRegister.createItems(TerraGuns.MODID);

    public static final DeferredItem<BaseGun> BLOWGUN = GUNS.registerItem("blowgun", properties -> new CustomGun(properties, 8, 2.8f, 1.4f, 0.17f, 0.04f, 0, ModRarity.WHITE, 0.08f)); // 吹管
    public static final DeferredItem<BaseGun> SNOWBALL_CANNON = GUNS.registerItem("snowball_cannon", properties -> new CustomGun(properties, 3, 5, 1.4f, 0.05f, 0.04f, 3.0f, ModRarity.BLUE, 0.05f)); // 雪球炮
    public static final DeferredItem<BaseGun> FLINTLOCK_PISTOL = GUNS.registerItem("flintlock_pistol", properties -> new BaseGun(properties, 5, 7f, 0.7f, 0.05f, 0.10f, 0, ModRarity.BLUE)); // 燧发枪
    public static final DeferredItem<BaseGun> THE_UNDERTAKER = GUNS.registerItem("the_undertaker", properties -> new BaseGun(properties, 9, 5.5f, 0.75f, 0.1f, 0.04f, 0, ModRarity.BLUE)); // 夺命枪
    public static final DeferredItem<BaseGun> MUSKET = GUNS.registerItem("musket", properties -> new BaseGun(properties, 14, 16f, 1.1f, 0.25f, 0.12f, 0, ModRarity.BLUE)); // 火枪
    public static final DeferredItem<BaseGun> MINISHARK = GUNS.registerItem("minishark", properties -> new BaseGun(properties, 3, 3.4f, 1.0f, 0.0f, 0.04f, 5.0f, ModRarity.GREEN)); // 迷你鲨
    public static final DeferredItem<BaseGun> BOOMSTICK = GUNS.registerItem("boomstick", properties -> new Shotgun(properties, 13, 7f, 0.66f, 0.28f, 0.04f, 8.0f, ModRarity.GREEN, 3, 4)); // 三发猎枪
    public static final DeferredItem<BaseGun> HAND_GUN = GUNS.registerItem("hand_gun", properties -> new BaseGun(properties, 5, 13f, 1.25f, 0.15f, 0.04f, 0, ModRarity.GREEN)); // 手枪
    public static final DeferredItem<BaseGun> PHOENIX_BLASTER = GUNS.registerItem("phoenix_blaster", properties -> new BaseGun(properties, 4, 16f, 2.50f, 0.10f, 0.04f, 0, ModRarity.ORANGE)); // 凤凰爆破枪
    public static final DeferredItem<BaseGun> SHOTGUN = GUNS.registerItem("shotgun", properties -> new Shotgun(properties, 15, 12f, 0.9f, 0.375f, 0.04f, 10.0f, ModRarity.LIGHT_RED, 3, 5)); // 霰弹枪
    public static final DeferredItem<BaseGun> TACTICAL_SHOTGUN = GUNS.registerItem("tactical_shotgun", properties -> new Shotgun(properties, 11, 18f, 0.75f, 0.35f, 0.04f, 12.0f, ModRarity.YELLOW, 6, 6)); // 战术霰弹枪

    public static final DeferredItem<BaseBullet> MUSKET_BULLET = BULLETS.registerItem("musket_bullet", properties -> new BaseBullet(properties.stacksTo(99), 1.5f, 0.5f, 2, 0.1f, ModRarity.WHITE, 0, false));
    public static final DeferredItem<BaseBullet> SILVER_BULLET = BULLETS.registerItem("silver_bullet", properties -> new BaseBullet(properties.stacksTo(99), 2.5f, 0.56f, 2, 0.15f, ModRarity.WHITE, 0, false));
    public static final DeferredItem<BaseBullet> TUNGSTEN_BULLET = BULLETS.registerItem("tungsten_bullet", properties -> new BaseBullet(properties.stacksTo(99), 2.5f, 0.56f, 2, 0.2f, ModRarity.WHITE, 0, false));
    public static final DeferredItem<BaseBullet> METEOR_SHOT = BULLETS.registerItem("meteor_shot", properties -> new BaseBullet(properties.stacksTo(99), 2f, 0.37f, 2, 0.05f, ModRarity.BLUE, 1, false));
    public static final DeferredItem<BaseBullet> PARTY_BULLET = BULLETS.registerItem("party_bullet", properties -> new BaseBullet(properties.stacksTo(99), 3f, 0.63f, 3, 0.25f, ModRarity.ORANGE, 0, false));

    public static final DeferredItem<BaseBullet> CRYSTAL_BULLET = BULLETS.registerItem("crystal_bullet", properties -> new BaseBullet(properties.stacksTo(99), 2.5f, 0.62f, 2, 0.05f, ModRarity.ORANGE, 0, false));
    public static final DeferredItem<BaseBullet> ICHOR_BULLET = BULLETS.registerItem("ichor_bullet", properties -> new BaseBullet(properties.stacksTo(99), 4.5f, 0.65f, 3, 0.2f, ModRarity.ORANGE, 0, false));
    public static final DeferredItem<BaseBullet> CURSED_BULLET = BULLETS.registerItem("cursed_bullet", properties -> new BaseBullet(properties.stacksTo(99), 4f, 0.62f, 3, 0.2f, ModRarity.ORANGE, 0, false));
    public static final DeferredItem<BaseBullet> CHLOROPHYTE_BULLET = BULLETS.registerItem("chlorophyte_bullet", properties -> new BaseBullet(properties.stacksTo(99), 2.5f, 0.62f, 3, 0.22f, ModRarity.LIME, 0, false));
    public static final DeferredItem<BaseBullet> HIGH_VELOCITY_BULLET = BULLETS.registerItem("high_velocity_bullet", properties -> new BaseBullet(properties.stacksTo(99), 3.5f, 0.5f, 8, 0.2f, ModRarity.ORANGE, 2, false));
    public static final DeferredItem<BaseBullet> EXPLODING_BULLET = BULLETS.registerItem("exploding_bullet", properties -> new BaseBullet(properties.stacksTo(99), 3f, 0.58f, 3, 0.33f, ModRarity.ORANGE, 0, false));
    public static final DeferredItem<BaseBullet> GOLDEN_BULLET = BULLETS.registerItem("golden_bullet", properties -> new BaseBullet(properties.stacksTo(99), 3f, 0.57f, 3, 0.18f, ModRarity.ORANGE, 0, false));
    public static final DeferredItem<BaseBullet> VENOM_BULLET = BULLETS.registerItem("venom_bullet", properties -> new BaseBullet(properties.stacksTo(99), 5.5f, 0.66f, 3, 0.21f, ModRarity.ORANGE, 0, false));
    public static final DeferredItem<BaseBullet> NANO_BULLET = BULLETS.registerItem("nano_bullet", properties -> new BaseBullet(properties.stacksTo(99), 5.5f, 0.57f, 3, 0.18f, ModRarity.ORANGE, 0, false));
    public static final DeferredItem<BaseBullet> ENDLESS_MUSKET_POUCH = BULLETS.registerItem("endless_musket_pouch", properties -> new BaseBullet(properties.stacksTo(1), 1.5f, 0.5f, 2, 0.1f, ModRarity.GREEN, 0, true));
    public static final DeferredItem<BaseBullet> LUMINITE_BULLET = BULLETS.registerItem("luminite_bullet", properties -> new BaseBullet(properties.stacksTo(99), 6f, 0.25f, 6, 0.15f, ModRarity.CYAN, -1, false));

    public static final DeferredItem<BaseBullet.EmptyBullet> EMPTY_BULLET = OTHER.registerItem("empty_bullet", BaseBullet.EmptyBullet::new);
}
