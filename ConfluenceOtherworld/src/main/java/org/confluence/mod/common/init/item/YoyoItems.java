package org.confluence.mod.common.init.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshot;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.mana.BeeGunBullet;
import org.confluence.mod.common.entity.yoyo.YoyoEntity;
import org.confluence.mod.common.item.yoyo.YoyoItem;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

/**
 * 悠悠球物品注册。
 *
 * <p>普通品种只填写自身数值；共享实体、网络和渲染不需要重复注册。只有瀑布与蜂巢球
 * 在最后一个参数声明特殊命中回调。</p>
 */
public final class YoyoItems {
    private YoyoItems() {
    }

    public static void init() {
    }

    public static final PortItemRegistration ITEMS =
            PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<YoyoItem> AMAZON = register(
            "amazon", 450, ModRarity.ORANGE,
            4.5F, 14.0F, 0xFFC896, 8.0F, YoyoItem.HitEffect.NONE);
    public static final PortDeferredItem<YoyoItem> ARTERY = register(
            "artery", 420, ModRarity.BLUE,
            4.2F, 13.0F, 0x9696FF, 6.0F, YoyoItem.HitEffect.NONE);
    public static final PortDeferredItem<YoyoItem> CASCADE = register(
            "cascade", 550, ModRarity.ORANGE,
            5.5F, 15.0F, 0xFFC896, 13.0F,
            (yoyo, owner, target) -> target.setSecondsOnFire(5));
    public static final PortDeferredItem<YoyoItem> CODE_1 = register(
            "code_1", 480, ModRarity.GREEN,
            4.8F, 14.0F, 0x96FF96, 9.0F, YoyoItem.HitEffect.NONE);
    public static final PortDeferredItem<YoyoItem> HIVE_FIVE = register(
            "hive_five", 520, ModRarity.ORANGE,
            5.2F, 14.0F, 0xFFC896, 8.0F, YoyoItems::spawnBee);
    public static final PortDeferredItem<YoyoItem> MALAISE = register(
            "malaise", 380, ModRarity.BLUE,
            3.8F, 12.0F, 0x9696FF, 7.0F, YoyoItem.HitEffect.NONE);
    public static final PortDeferredItem<YoyoItem> RALLY = register(
            "rally", 350, ModRarity.BLUE,
            3.5F, 10.0F, 0x9696FF, 5.0F, YoyoItem.HitEffect.NONE);
    public static final PortDeferredItem<YoyoItem> VALOR = register(
            "valor", 570, ModRarity.ORANGE,
            5.7F, 15.0F, 0xFFC896, 11.0F, YoyoItem.HitEffect.NONE);
    public static final PortDeferredItem<YoyoItem> WOODEN_YOYO = register(
            "wooden_yoyo", 150, ModRarity.WHITE,
            1.5F, 8.0F, 0x00FF00, 3.0F, YoyoItem.HitEffect.NONE);

    private static PortDeferredItem<YoyoItem> register(
            String name,
            int durability,
            ModRarity rarity,
            float damage,
            float range,
            int stringColor,
            float lifetimeSeconds,
            YoyoItem.HitEffect hitEffect
    ) {
        return ITEMS.register(
                name,
                () -> new YoyoItem(
                        new Item.Properties().durability(durability),
                        rarity,
                        damage,
                        range,
                        stringColor,
                        lifetimeSeconds,
                        hitEffect));
    }

    /**
     * 蜂巢球复用已有追踪蜂实体，但派生快照保持悠悠球的近战通道并把基础伤害减半。
     */
    private static void spawnBee(
            YoyoEntity yoyo,
            ServerPlayer owner,
            LivingEntity target
    ) {
        ProjectileCombatSnapshot snapshot =
                yoyo.getProjectileCombatSnapshot();
        if (snapshot == null || yoyo.getRandom().nextFloat() >= 0.33F) {
            return;
        }
        BeeGunBullet bee = new BeeGunBullet(yoyo.level(), owner, false);
        bee.setPos(yoyo.position());
        Vec3 direction = target.getBoundingBox()
                .getCenter()
                .subtract(yoyo.position());
        if (direction.lengthSqr() < 1.0E-8) {
            direction = owner.getLookAngle();
        }
        bee.setDeltaMovement(direction.normalize().scale(0.35));
        bee.setProjectileCombatSnapshot(snapshot.derive(
                snapshot.baseDamage() * 0.5F,
                0.35F,
                snapshot.knockback() * 0.5F));
        yoyo.level().addFreshEntity(bee);
    }
}
