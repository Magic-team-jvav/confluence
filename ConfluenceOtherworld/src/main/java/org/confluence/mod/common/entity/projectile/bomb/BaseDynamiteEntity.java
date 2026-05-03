package org.confluence.mod.common.entity.projectile.bomb;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import org.confluence.lib.util.damage.MultiplyExplosionDamageCalculator;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.util.TerraStyleExplosion;

public class BaseDynamiteEntity extends BaseBombEntity {
    public static final float DIAMETER = 0.25F;
    public static final float BLAST_POWER = 10.0F;

    public BaseDynamiteEntity(EntityType<? extends BaseDynamiteEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.delay = 100;
        this.diameter = DIAMETER;
        this.blastPower = BLAST_POWER;
    }

    public BaseDynamiteEntity(EntityType<? extends BaseDynamiteEntity> pEntityType, LivingEntity pShooter) {
        super(pEntityType, pShooter);
        this.delay = 100;
        this.diameter = DIAMETER;
        this.blastPower = BLAST_POWER;
    }

    public BaseDynamiteEntity(LivingEntity pShooter) {
        super(ModEntities.DYNAMITE.get(), pShooter);
        this.delay = 100;
        this.diameter = DIAMETER;
        this.blastPower = BLAST_POWER;
    }

    @Override
    protected void explodeFunction(ServerLevel level) {
        TerraStyleExplosion.terraExplode(level, this, Explosion.getDefaultDamageSource(level, this), new MultiplyExplosionDamageCalculator(0.2F), getX(), getY(), getZ(), blastPower, Level.ExplosionInteraction.TNT);
    }

    @Override
    protected void createEmitter() {}
}
