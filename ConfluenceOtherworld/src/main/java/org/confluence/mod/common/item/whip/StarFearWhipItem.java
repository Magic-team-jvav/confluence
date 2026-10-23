package org.confluence.mod.common.item.whip;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.mod.api.whip.WhipDirectHitContext;
import org.confluence.mod.common.entity.projectile.mana.StarFearStarEntity;
import org.confluence.mod.common.summoner.summonMark.SummonMarkType;

import java.util.function.Supplier;

/// 星惧：鞭子击中敌人时冒出一个快速逃出的坠星实体。
///
/// 施法者成功碰到这颗坠星即可获得「摘星」增益（仆从数量 +1）。
public final class StarFearWhipItem extends BaseWhipItem {
    public StarFearWhipItem(Supplier<? extends SummonMarkType> summonMarkType) {
        super("star_fear", 22.0F, 0.6F, 2.0F, 15, summonMarkType);
    }

    @Override
    public void onDirectHit(WhipDirectHitContext context) {
        Player owner = context.owner();
        Level level = owner.level();
        if (level.isClientSide) return;
        StarFearStarEntity star = new StarFearStarEntity(org.confluence.mod.common.init.entity.ModEntities.STAR_FEAR_STAR.get(), level);
        star.setOwner(owner);
        star.setPos(context.target().getX(), context.target().getY() + context.target().getBbHeight() * 0.5, context.target().getZ());
        level.addFreshEntity(star);
    }
}
