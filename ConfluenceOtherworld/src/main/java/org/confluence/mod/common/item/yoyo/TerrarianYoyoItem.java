package org.confluence.mod.common.item.yoyo;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.yoyo.TerrarianProjectile;
import org.confluence.mod.common.entity.yoyo.YoyoEntity;
import org.confluence.mod.mixed.Immunity;

public final class TerrarianYoyoItem extends YoyoItem implements Immunity {
    private final int shotInterval;
    private final int immunityTicks;
    private final float bonusCriticalChance;
    private final float projectileDamageMultiplier;
    private final double projectileSpeed;
    private final double projectileTargetRange;

    public TerrarianYoyoItem(ModRarity rarity, float damage, float range, int lifetimeTicks, float knockback, int shotInterval, int immunityTicks, float bonusCriticalChance, float projectileDamageMultiplier, double projectileSpeed, double projectileTargetRange) {
        super(new Properties().unbreakable(), rarity, damage, range, lifetimeTicks, knockback);
        this.shotInterval = shotInterval;
        this.immunityTicks = immunityTicks;
        this.bonusCriticalChance = bonusCriticalChance;
        this.projectileDamageMultiplier = projectileDamageMultiplier;
        this.projectileSpeed = projectileSpeed;
        this.projectileTargetRange = projectileTargetRange;
    }

    /// 每 0.1 秒发射一次，副球与脱手球同样保留此能力。
    @Override
    public void tickAttack(YoyoEntity yoyo) {
        if (yoyo.tickCount % shotInterval == 0)
            TerrarianProjectile.shootAtNearest(yoyo, projectileDamageMultiplier, projectileSpeed, projectileTargetRange);
    }

    @Override
    public Immunity hitImmunity(ServerPlayer owner) {return this;}

    @Override
    public Type confluence$getImmunityType() {return Type.STATIC;}

    @Override
    public int confluence$getImmunityDuration(DamageSource source) {return immunityTicks;}

    @Override
    public float bonusCriticalChance() {return bonusCriticalChance;}

    @Override
    public boolean fullBright() {return true;}

    @Override
    protected Component effectTooltip() {return Component.translatable("tooltip.confluence.yoyo.terrarian");}
}
