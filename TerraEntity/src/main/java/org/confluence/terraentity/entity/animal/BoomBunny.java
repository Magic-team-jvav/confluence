package org.confluence.terraentity.entity.animal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.lib.util.MultiplyExplosionDamageCalculator;

public class BoomBunny extends Bunny{

    boolean exploded = false;
    public BoomBunny(EntityType<? extends Bunny> entityType, Level level) {
        super(entityType, level);

    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
        this.explode();

    }

    private void explode(){
        if(exploded || level().isClientSide) return;
        this.exploded = true;
        level().explode(this, Explosion.getDefaultDamageSource(level(), this), new MultiplyExplosionDamageCalculator(1f){
            @Override
            public boolean shouldBlockExplode(Explosion explosion, BlockGetter reader, BlockPos pos, BlockState state, float power) {
                return false;
            }
        }, getX(), getY(), getZ(), 3, false, Level.ExplosionInteraction.MOB);
        this.kill();
    }
}
