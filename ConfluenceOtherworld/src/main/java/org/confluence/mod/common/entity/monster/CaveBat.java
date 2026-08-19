package org.confluence.mod.common.entity.monster;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.FlyWanderAction;
import org.confluence.mod.common.entity.ai.bt.leaf.SteeringDashAction;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.entity.MonsterEntities;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

/// 洞穴蝙蝠 —— 飞行 + 环绕冲刺攻击。与 DemonEye 共享同款 BT。
public class CaveBat extends BaseFlyingMonster {
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("fly");

    public CaveBat(EntityType<? extends CaveBat> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseFlyingMonster.createFlyingAttributes();
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(SequenceNode.of(new HasTargetCondition(CaveBat.this), new SteeringDashAction(CaveBat.this, 1.0, 0.5, 0.02, 20.0, 20.0, 45.0, 30)), new FlyWanderAction(CaveBat.this, 0.15, 10));
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
        } else if (getType() == MonsterEntities.HELL_BAT.get()) {
            spawnHellBatParticles();
        } else if (getType() == MonsterEntities.ICE_BAT.get()) {
            spawnIceBatParticles();
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (getType() == MonsterEntities.HELL_BAT.get() && source.is(DamageTypeTags.IS_FIRE))
            return false;
        return super.hurt(source, amount);
    }

    /// 保留 1.21 地狱蝙蝠分布在身体两侧的熔岩粒子轨迹。
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

    /// 保留 1.21 冰蝙蝠围绕身体随机散落的成对雪花粒子。
    private void spawnIceBatParticles() {
        float side = getBbWidth() * 0.2F;
        float x = Mth.cos(getYRot() * Mth.DEG_TO_RAD + getRandom().nextFloat()) * side;
        float z = Mth.sin(getYRot() * Mth.DEG_TO_RAD + getRandom().nextFloat()) * side;
        float y = 0.6F * (getBbHeight() + getRandom().nextFloat() - 0.5F);
        level().addParticle(ParticleTypes.SNOWFLAKE, getX() + x, getY() + y, getZ() + z, 0.0, 0.0, 0.0);
        level().addParticle(ParticleTypes.SNOWFLAKE, getX() - x, getY() + y, getZ() - z, 0.0, 0.0, 0.0);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.BAT_DEATH.get();
    }
}
