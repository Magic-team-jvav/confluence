package org.confluence.mod.common.item.yoyo;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.sword.BeeKeeperProjectile;
import org.confluence.mod.common.entity.yoyo.YoyoEntity;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.util.CuriosUtils;

public final class HiveFiveYoyoItem extends YoyoItem {
    private final int procDenominator;
    private final float beeDamageMultiplier;
    @Override
    protected Component effectTooltip() {return Component.translatable("tooltip.confluence.yoyo.bees");}

    public HiveFiveYoyoItem(ModRarity rarity, float damage, float range, int lifetimeTicks, float knockback, int procDenominator, float beeDamageMultiplier) {
        super(new Properties().unbreakable(), rarity, damage, range, lifetimeTicks, knockback);
        this.procDenominator = procDenominator;
        this.beeDamageMultiplier = beeDamageMultiplier;
    }

    @Override
    protected void onHitTarget(YoyoEntity yoyo, ServerPlayer owner, LivingEntity target) {
        if (owner.getRandom1211().nextInt(procDenominator) != 0) return;
        BeeKeeperProjectile bee = ModEntities.BEE.get().create(owner.level());
        if (bee == null) return;
        boolean giant = CuriosUtils.hasCurio(owner, TCItems.HIVE_PACK.get()) && owner.getRandom().nextBoolean();
        bee.configure(owner, yoyo.getDamage() * beeDamageMultiplier, yoyo.getCriticalChance(), yoyo.getKnockback(), giant);
        bee.setPos(yoyo.position().add(0, yoyo.getBbHeight() * 0.5, 0));
        owner.level().addFreshEntity(bee);
    }
}
