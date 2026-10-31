package org.confluence.mod.common.entity.projectile.sword;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.mana.AbstractManaProjectile;

/// 村正命中敌怪时生成的蓝色斩击射弹，本体不渲染，仅由粒子风暴表现刀光。
public class MuramasaSlashProjectile extends AbstractManaProjectile {
    /// 存活时间，约 0.4 秒。
    public static final int MAX_AGE = 8;

    public MuramasaSlashProjectile(EntityType<MuramasaSlashProjectile> entityType, Level level) {
        super(entityType, level);
        withParticle(Confluence.asResource("muramasa_slash"));
    }

    @Override
    public DamageSource getDamageSource() {
        return LibDamageTypes.of(level(), LibDamageTypes.SWORD_PROJECTILE, this, getOwner());
    }

    @Override
    public void baseTick() {
        super.baseTick();
        doSimpleMove();
        doAgeCheck(MAX_AGE);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (doPenetrateCheck(result.getEntity())) {
            doHurtAndKnockback(result.getEntity(), 0.0, 0.0);
            doDiscardInMaxPenetrate(1);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!level().isClientSide) discardInTicks(1);
    }
}
