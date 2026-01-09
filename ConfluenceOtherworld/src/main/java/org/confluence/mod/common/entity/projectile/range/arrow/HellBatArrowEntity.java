package org.confluence.mod.common.entity.projectile.range.arrow;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.item.bow.BaseTerraBowItem;
import org.confluence.mod.common.item.bow.arrow.BaseTerraArrowItem;
import org.jetbrains.annotations.NotNull;
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

    public HellBatArrowEntity(EntityType<? extends AbstractArrow> pEntityType, LivingEntity owner, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon, @NotNull BaseTerraArrowItem arrow, BaseTerraBowItem.Builder modifyConsumer) {
        super(pEntityType, owner, pickupItemStack, firedFromWeapon, arrow, modifyConsumer);
        this.modify.penetration_count = 99999;
        this.modify.setGravity(0);
        this.modify.setAutoDiscard(100);

    }

    @Override
    public void tick(){
        super.tick();
        if(level().isClientSide && !this.inGround && this.tickCount > 2 && this.tickCount % 5 == 0){
            this.level().addParticle(ParticleTypes.LAVA, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }
    }

    static RawAnimation fly = RawAnimation.begin().thenLoop("fly");

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this, "fly", 0, state->{
            if(!this.inGround) {
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
