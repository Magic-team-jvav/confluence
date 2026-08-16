package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

/// 具有红、蓝两种外观并会中距离跃击的龙虾。
public final class Crawdad extends BaseWarriorMonster {
    private static final String VARIANT_TAG = "Variant";
    private static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(
                    Crawdad.class, EntityDataSerializers.INT);
    private static final RawAnimation WALK =
            RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation ATTACK =
            RawAnimation.begin().thenPlay("attack");
    private boolean variantInitialized;

    public Crawdad(EntityType<? extends Crawdad> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseMonster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 26.0)
                .add(Attributes.ATTACK_DAMAGE, 15.0)
                .add(Attributes.ARMOR, 6.0)
                .add(Attributes.FOLLOW_RANGE, 25.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.1);
    }

    @Override
    protected JumpProfile jumpProfile() {
        return new JumpProfile(2.0, 4.1, 60, 0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(VARIANT, 0);
    }

    @Override
    public void onAddedToWorld() {
        if (!variantInitialized && !level().isClientSide) {
            setVariant(random.nextInt(2));
            variantInitialized = true;
        }
        super.onAddedToWorld();
    }

    public int getVariant() {
        return entityData.get(VARIANT);
    }

    private void setVariant(int variant) {
        entityData.set(VARIANT, Math.floorMod(variant, 2));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(VARIANT_TAG, getVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setVariant(tag.getInt(VARIANT_TAG));
        variantInitialized = true;
    }

    @Override
    public void registerControllers(
            AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(
                this,
                "movement_and_attack",
                5,
                state -> {
                    if (swinging) {
                        return state.setAndContinue(ATTACK);
                    }
                    if (state.isMoving()) {
                        return state.setAndContinue(WALK);
                    }
                    state.getController().forceAnimationReset();
                    return PlayState.STOP;
                }));
    }

    @Override
    public int getCurrentSwingDuration() {
        return 12;
    }
}
