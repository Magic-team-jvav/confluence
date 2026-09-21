package org.confluence.mod.common.item.yoyo;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.yoyo.YoyoEffectProjectile;
import org.confluence.mod.common.entity.yoyo.YoyoEntity;
import org.confluence.mod.mixed.Immunity;

public final class TerrarianYoyoItem extends YoyoItem implements Immunity {
    public TerrarianYoyoItem() {
        super(new Properties().unbreakable(), ModRarity.RED, 95F, 25, 0xFFFFFFFF, 0, 6.5F);
    }

    /// 每 0.1 秒发射一次，副球与脱手球同样保留此能力。
    @Override
    public void tickAttack(YoyoEntity yoyo) {
        if (yoyo.tickCount % 2 == 0)
            YoyoEffectProjectile.shootAtNearest(yoyo, YoyoEffectProjectile.Kind.TERRARIAN, null);
    }

    @Override
    public Immunity hitImmunity(ServerPlayer owner) {return this;}

    @Override
    public Type confluence$getImmunityType() {return Type.STATIC;}

    @Override
    public int confluence$getImmunityDuration(DamageSource source) {return 4;}

    @Override
    public float bonusCriticalChance() {return 0.1F;}

    @Override
    public boolean fullBright() {return true;}

    @Override
    public boolean supportsLegendaryPrefix() {return true;}

    @Override
    protected String effectTooltip() {return "tooltip.confluence.yoyo.terrarian";}
}
