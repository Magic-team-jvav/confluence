package org.confluence.terraentity.entity.monster.slime;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;
import org.confluence.lib.color.FloatRGB;
import org.confluence.terraentity.entity.util.DeathAnimOptions;
import org.confluence.terraentity.init.TEParticles;
import org.confluence.terraentity.mixin.accessor.SlimeAccessor;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class GoldenSlime extends Slime implements DeathAnimOptions {
    private final FloatRGB color;

    public GoldenSlime(EntityType<? extends Slime> entityType, Level level) {
        super(entityType, level);
        setSize(2, true);
        this.color = FloatRGB.fromInteger(0xfcf8bd);
    }

    @Override
    public void tick() {
        resetFallDistance();
        if (onGround() && !((SlimeAccessor) this).isWasOnGround()) {
            int i = getSize();
            for (int j = 0; j < i * 8; ++j) {
                float f = random.nextFloat() * Mth.TWO_PI;
                float f1 = random.nextFloat() * 0.5F + 0.5F;
                float f2 = Mth.sin(f) * (float) i * 0.5F * f1;
                float f3 = Mth.cos(f) * (float) i * 0.5F * f1;
                level().addParticle(TEParticles.ITEM_GEL.get(), getX() + (double) f2, getY(), getZ() + (double) f3, color.red(), color.green(), color.blue());
            }
        }
        if (this.tickCount % 22 == 0){
            if (level() instanceof ServerLevel serverLevel){
                serverLevel.sendParticles(
                        new DustParticleOptions(
                                new Vector3f(1f, 0.666f, 0f),
                                1.0f
                        ),
                        getX(), getY(), getZ(),
                        12,
                        random.nextFloat(), random.nextFloat(), random.nextFloat(),
                        0.01
                );
            }
        }
        super.tick();
    }

    @Override
    public void setSize(int pSize, boolean pResetHealth) {
        entityData.set(ID_SIZE, 2);
        reapplyPosition();
        refreshDimensions();
        getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.4f);
    }

    public static AttributeSupplier.Builder createSlimeAttributes() {
        return BaseSlime.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, 5)
                .add(Attributes.ARMOR, 2)
                .add(Attributes.MAX_HEALTH, 97);
    }

    @Override
    public float[] getBloodColor() {
        return color.mixture(new FloatRGB(0, 0, 0), 0.5f).toArray();
    }

    @Override
    protected int getJumpDelay() {
        return 8;
    }

    @Override
    public void remove(@NotNull RemovalReason removalReason) {
        brain.clearMemories();
        setRemoved(removalReason);
//        invalidateCaps();
    }

    @Override
    protected void actuallyHurt(DamageSource damageSource, float damageAmount) {
        super.actuallyHurt(damageSource, damageAmount);
        // confluence mixin here
    }
}
