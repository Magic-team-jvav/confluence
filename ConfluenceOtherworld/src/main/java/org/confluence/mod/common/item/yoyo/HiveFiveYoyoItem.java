package org.confluence.mod.common.item.yoyo;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.sword.BeeKeeperProjectile;
import org.confluence.mod.common.entity.yoyo.YoyoEntity;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.util.CuriosUtils;

public final class HiveFiveYoyoItem extends YoyoItem {
    @Override
    protected String effectTooltip() {return "tooltip.confluence.yoyo.bees";}

    public HiveFiveYoyoItem() {
        super(new Properties().unbreakable(), ModRarity.ORANGE, 10F, 14.0625F, 0xFFC896, 11 * 20, 3.75F);
    }

    @Override
    protected void onHitTarget(YoyoEntity yoyo, ServerPlayer owner, LivingEntity target) {
        if (owner.getRandom1211().nextInt(3) != 0) return;
        BeeKeeperProjectile bee = ModEntities.BEE.get().create(owner.level());
        if (bee == null) return;
        boolean giant = CuriosUtils.hasCurio(owner, TCItems.HIVE_PACK.get()) && owner.getRandom().nextBoolean();
        bee.configure(owner, yoyo.getDamage() * 0.5F, yoyo.getCriticalChance(), yoyo.getKnockback(), giant);
        bee.setPos(yoyo.position().add(0, yoyo.getBbHeight() * 0.5, 0));
        owner.level().addFreshEntity(bee);
    }
}
