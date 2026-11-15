package org.confluence.mod.common.entity.animal;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.lib.util.damage.MultiplyExplosionDamageCalculator;

public class ExplosiveBunny extends Bunny {
    private static final float EXPLOSION_RADIUS = 3.0F;

    private boolean exploded;

    public ExplosiveBunny(EntityType<? extends ExplosiveBunny> type, Level level) {
        super(type, level);
        setBunnyVariant(Variant.EXPLOSIVE);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setBunnyVariant(Variant.EXPLOSIVE);
    }

    @Override
    protected void initializeSpawnVariant() {
        setBunnyVariant(Variant.EXPLOSIVE);
    }

    /// 爆炸兔死亡后产生一次只伤害实体、不破坏方块的爆炸。
    @Override
    public void die(DamageSource source) {
        super.die(source);
        explodeOnce();
    }

    private void explodeOnce() {
        if (exploded || level().isClientSide) {
            return;
        }
        exploded = true;
        level().explode(this, Explosion.getDefaultDamageSource(level(), this),
                new MultiplyExplosionDamageCalculator(1.0F) {
                    @Override
                    public boolean shouldBlockExplode(Explosion explosion, BlockGetter level, BlockPos pos, BlockState state, float power) {
                        return false;
                    }
                },
                getX(), getY(), getZ(), EXPLOSION_RADIUS, false, Level.ExplosionInteraction.MOB);
    }
}
