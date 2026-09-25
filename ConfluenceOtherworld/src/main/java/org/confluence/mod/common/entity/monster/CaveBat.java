package org.confluence.mod.common.entity.monster;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.LookForwardWanderFlyAction;
import org.confluence.mod.common.entity.ai.bt.leaf.SteeringDashAction;
import org.confluence.mod.common.init.ModSoundEvents;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

/// 使用平滑冲撞和前向巡航的蝙蝠类敌怪。
public class CaveBat extends BaseFlyingMonster {
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("fly");
    private final Variant variant;

    public CaveBat(EntityType<? extends CaveBat> type, Level level) {
        this(type, level, Variant.ROUTINE);
    }

    public CaveBat(EntityType<? extends CaveBat> type, Level level, Variant variant) {
        super(type, level);
        this.variant = variant;
        setDiscardFriction(true);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(new HasTargetCondition(CaveBat.this),
                                new SteeringDashAction(CaveBat.this, 1.0, 0.5, 0.02,
                                        20.0, 20.0, 45.0, 30)),
                        new LookForwardWanderFlyAction(CaveBat.this, 0.2, 0.0F));
            }
        };
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Fly", 0, state -> state.setAndContinue(FLY)));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!level().isClientSide) {
            addDeltaMovement(new Vec3(0.0, Math.sin(tickCount * 0.2) * 0.03, 0.0));
            return;
        }
        switch (variant) {
            case HELL, LAVA -> spawnHellBatParticles();
            case ICE -> spawnIceBatParticles();
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if ((variant == Variant.HELL || variant == Variant.LAVA) && source.is(DamageTypeTags.IS_FIRE))
            return false;
        return super.hurt(source, amount);
    }

    /// 生成分布在地狱蝙蝠身体两侧的熔岩粒子轨迹。
    private void spawnHellBatParticles() {
        int offset = getId() * 3;
        float wave = Mth.cos((offset + tickCount) * 7.448451F * Mth.DEG_TO_RAD + Mth.PI);
        float side = getBbWidth() * 0.5F;
        float x = Mth.cos(getYRot() * Mth.DEG_TO_RAD) * side;
        float z = Mth.sin(getYRot() * Mth.DEG_TO_RAD) * side;
        float y = (0.3F + wave * 0.45F) * getBbHeight() * 0.5F;
        level().addParticle(ParticleTypes.LAVA, getX() + x, getY() + y, getZ() + z, 0.0, 0.0, 0.0);
        level().addParticle(ParticleTypes.LAVA, getX() - x, getY() + y, getZ() - z, 0.0, 0.0, 0.0);
    }

    /// 生成围绕冰蝙蝠身体随机散落的成对雪花粒子。
    private void spawnIceBatParticles() {
        float side = getBbWidth() * 0.2F;
        float x = Mth.cos(getYRot() * Mth.DEG_TO_RAD + getRandom1211().nextFloat()) * side;
        float z = Mth.sin(getYRot() * Mth.DEG_TO_RAD + getRandom1211().nextFloat()) * side;
        float y = 0.6F * (getBbHeight() + getRandom1211().nextFloat() - 0.5F);
        level().addParticle(ParticleTypes.SNOWFLAKE, getX() + x, getY() + y, getZ() + z, 0.0, 0.0, 0.0);
        level().addParticle(ParticleTypes.SNOWFLAKE, getX() - x, getY() + y, getZ() - z, 0.0, 0.0, 0.0);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.BAT_DEATH.get();
    }

    /// 同一套飞行行为的环境表现差异；命中效果由 AttackEffects 数据配置。
    public enum Variant {
        ROUTINE, ICE, ILLUMINANT, LAVA, HELL
    }

}
