package org.confluence.mod.common.item.flail;

import net.minecraft.world.entity.player.Player;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.flail.BaseFlailEntity;
import org.confluence.mod.common.entity.projectile.mana.HurtnadoProjectile;

/// 风锚。
///
/// 投出瞬间在链球所在位置额外召唤一枚天候棒的痛苦飓风（{@link HurtnadoProjectile}）。
/// 飓风直接复用天候棒的模型与行为，只把伤害来源切成近战，伤害量取风锚自身的投出伤害
/// （{@code damageFactor × 近战攻击力}，该属性已含链球词条），因此与链球吃同一套近战加成。
public class WindAnchorItem extends BaseFlailItem {
    /// 召唤出的飓风继承投出速度的比例。
    private static final double SUMMON_VELOCITY_SCALE = 1.0 / 3.0;

    public WindAnchorItem(Parameters parameters, ModRarity rarity) {
        super(parameters, rarity);
    }

    @Override
    public void onFlailLaunch(Player owner, BaseFlailEntity flail) {
        if (owner.level().isClientSide) return;

        HurtnadoProjectile tornado = new HurtnadoProjectile(owner);
        tornado.setPos(flail.getX(), flail.getY() + flail.getBbHeight() * 0.5, flail.getZ());
        tornado.setOwner(owner);
        tornado.setMeleeDamage(true);
        tornado.setDamage(parameters().damageFactor() * (float) owner.getAttributeValue(LibAttributes.getAttackDamage()));
        // 沿投出方向带一小段初速，避免飓风与链球重叠后再被自瞄慢慢拉开。
        tornado.setDeltaMovement(flail.getDeltaMovement().scale(SUMMON_VELOCITY_SCALE).add(0.0, 0.15, 0.0));
        owner.level().addFreshEntity(tornado);
    }
}
