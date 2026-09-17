package org.confluence.mod.common.entity.animal;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.entity.CritterEntities;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class CloudSheep extends Sheep implements CritterVisual {
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int jumpCooldown = 40;

    public CloudSheep(EntityType<? extends Sheep> type, Level level) {
        super(type, level);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!level().isClientSide && !isNoAi() && onGround() && !isInWaterOrBubble()) {
            if (jumpCooldown > 0) jumpCooldown--;
            if (jumpCooldown == 0 && !getNavigation().isDone()) {
                getJumpControl().jump();
                jumpCooldown = 40 + random.nextInt(41);
            }
        }
        if (!onGround() && !isInWaterOrBubble() && !isInLava() && getDeltaMovement().y < 0.0) {
            setDeltaMovement(getDeltaMovement().multiply(1.0, 0.65, 1.0));
            fallDistance = 0.0F;
        }
    }

    @Override
    protected float getJumpPower() {
        return super.getJumpPower() * 2.0F;
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public Sheep getBreedOffspring(ServerLevel level, AgeableMob other) {
        CloudSheep child = CritterEntities.CLOUD_SHEEP.get().create(level);
        if (child != null)
            child.setColor(other instanceof Sheep sheep && random.nextBoolean() ? sheep.getColor() : getColor());
        return child;
    }

    @Override
    public ResourceLocation getModelPath() {
        return Confluence.asResource(level().isRaining() ? "animal/cloud_sheep_rainy" : "animal/cloud_sheep");
    }

    @Override
    public ResourceLocation getTexturePath() {
        return getModelPath().withPrefix("textures/entity/").withSuffix(".png");
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Movement", 4, state -> state.isMoving() ? state.setAndContinue(WALK) : PlayState.STOP));
    }
}
