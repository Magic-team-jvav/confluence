package org.confluence.mod.common.entity.projectile.arrow;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class HellBatArrowEntity extends BaseArrowEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public HellBatArrowEntity(EntityType<? extends AbstractArrow> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public HellBatArrowEntity(EntityType<? extends AbstractArrow> pEntityType, LivingEntity owner, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon){
        super(pEntityType, owner, pickupItemStack, firedFromWeapon);
    }
    @Override
    protected int getPenetrationCount() {
        return 99999;
    }

    @Override
    public double getDefaultGravity() {
        return 0;
    }

    @Override
    protected int getAutoDiscardTick() {
        return 100;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide && !this.inGround && this.tickCount > 2 && this.tickCount % 5 == 0) {
            this.level().addParticle(ParticleTypes.LAVA, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }
    }

    static RawAnimation fly = RawAnimation.begin().thenLoop("fly");

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this, "fly", 0, state -> {
            if (!this.inGround) {
                state.setControllerSpeed(3f);
                return state.setAndContinue(fly);
            }
            return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
