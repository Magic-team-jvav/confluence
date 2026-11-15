package org.confluence.mod.common.item.mana;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.entity.projectile.mana.WindEyeTornadoEntity;

/// 风眼：消耗 100 魔力制造一个持续 30 秒的大范围旋风。
///
/// 同一时间只保留一个旋风：再次使用会把上一道旋风移除；
/// 旋风会持续吸附敌人并造成持续伤害，只吸收第一枚打入中心的弹幕。
public class WindEyeItem extends ManaStaffItem<WindEyeTornadoEntity> {
    public static final int MANA_COST = 100;
    /// 旋风的放置距离。
    private static final double PLACE_DISTANCE = 6.0;
    private static final String LAST_TORNADO_TAG = "WindEyeUUID";

    public WindEyeItem(ModRarity rarity) {
        super(rarity, WindEyeTornadoEntity::new, 1.0F, MANA_COST, 0.0F, 40, 0.04);
        withTooltip(Component.translatable("tooltip.item.confluence.wind_eye.0").withStyle(ChatFormatting.GRAY));
    }

    @Override
    protected void beforeShoot(ServerPlayer player, ItemStack stack, WindEyeTornadoEntity projectile) {
        Vec3 center = player.getEyePosition().add(player.getLookAngle().scale(PLACE_DISTANCE));
        projectile.setPos(center.x, center.y, center.z);
        projectile.setDamage(1.0F);
        projectile.setDefaultVelocity(0.0F);
        projectile.setOwner(player);
    }

    /// 与天候棒相同的单例逻辑：记录本次旋风 UUID，下一次使用时移除上一道。
    @Override
    protected void afterShoot(ServerPlayer player, ItemStack stack, WindEyeTornadoEntity projectile) {
        super.afterShoot(player, stack, projectile);
        LibUtils.updateItemStackNbt(stack, tag -> {
            if (tag.hasUUID(LAST_TORNADO_TAG) && player.getServer() != null) {
                for (ServerLevel level : player.getServer().getAllLevels()) {
                    if (level.getEntity(tag.getUUID(LAST_TORNADO_TAG)) instanceof WindEyeTornadoEntity old) {
                        old.discard();
                    }
                }
            }
            tag.putUUID(LAST_TORNADO_TAG, projectile.getUUID());
        });
    }
}
