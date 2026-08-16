package org.confluence.mod.common.item.gun;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshot;
import org.confluence.lib.api.projectile.ProjectileFireContext;
import org.confluence.lib.api.projectile.ProjectileLaunch;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.mana.BeeGunBullet;
import org.confluence.mod.common.init.item.ArmorItems;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.util.TCUtils;

import java.util.ArrayList;
import java.util.List;

/// 蜜蜂枪的多弹幕魔力武器实现。
///
/// <p>蜂巢背包会把每次生成数量从一至三只提升为一至四只，并保留随机巨蜂。所有蜜蜂共享同一次
/// 发射快照和魔力消耗，但实体列表只存在于本次请求局部，多个玩家之间不会串用。</p>
public class BeeGunItem extends ManaGunItem {
    public BeeGunItem(Properties properties) {
        super(properties, 4, 4.6F, 1.0F, 0.01F, 0.04F, 2, 1.5F, ModRarity.GREEN, 5);
    }

    /// 创建本次请求局部的蜜蜂弹幕，不在物品类中直接把实体加入世界。
    @Override
    protected List<ProjectileLaunch> createLaunches(
            ProjectileFireContext context,
            ProjectileCombatSnapshot snapshot,
            ShotData shot
    ) {
        ServerPlayer player = context.player();
        boolean hasHivePack = TCUtils.hasType(player, TCItems.HIVE$PACK);
        int count = player.getRandom().nextInt(1, hasHivePack ? 5 : 4);
        List<ProjectileLaunch> launches = new ArrayList<>(count);
        for (int index = 0; index < count; index++) {
            BeeGunBullet projectile = new BeeGunBullet(
                    context.level(), player, hasHivePack && player.getRandom().nextBoolean());
            projectile.setPos(player.getX(), player.getEyeY(), player.getZ());
            projectile.shootFromRotation(
                    player,
                    player.getXRot(),
                    player.getYRot(),
                    0.0F,
                    Math.max(0.0F, snapshot.resolvedVelocity()),
                    Math.max(0.0F, shot.inaccuracy()));
            Vec3 velocity = projectile.getDeltaMovement();
            Vec3 origin = projectile.position();
            launches.add(new ProjectileLaunch(projectile, origin, velocity));
        }
        return List.copyOf(launches);
    }

    /// 只有实际成功生成整批蜜蜂后才检查并授予成就。
    @Override
    protected void onSuccessfulShot(ProjectileFireContext context, ShotData shot) {
        super.onSuccessfulShot(context, shot);
        awardNotTheBees(context.player());
    }

    /// 与 1.21 行为保持一致：穿戴任意一件蜜蜂套装即可触发成就。
    private static void awardNotTheBees(ServerPlayer player) {
        if (player.getItemBySlot(EquipmentSlot.HEAD).is(ArmorItems.BEE_HELMET.get())
                || player.getItemBySlot(EquipmentSlot.CHEST).is(ArmorItems.BEE_CHESTPLATE.get())
                || player.getItemBySlot(EquipmentSlot.LEGS).is(ArmorItems.BEE_LEGGINGS.get())
                || player.getItemBySlot(EquipmentSlot.FEET).is(ArmorItems.BEE_BOOTS.get())) {
            AchievementUtils.awardAchievement(player, "not_the_bees");
        }
    }
}
