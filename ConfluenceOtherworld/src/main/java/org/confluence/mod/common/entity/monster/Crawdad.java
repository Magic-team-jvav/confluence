package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.Map;

/**
 * 龙虾
 */
public class Crawdad extends AbstractMonster implements IVanillaVariant<Integer> {

    private static final EntityDataAccessor<Integer> DATA_VARIANT_ID = SynchedEntityData.defineId(Crawdad.class, EntityDataSerializers.INT);
    private static final RawAnimation attack = RawAnimation.begin().thenPlay("attack");
    private static final RawAnimation walk = RawAnimation.begin().thenLoop("walk");


    public Crawdad(EntityType<? extends Monster> type, Level level) {
        super(type, level);
//        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.5f);

    }

    @Override
    protected void registerGoals() {

        this.goalSelector.addGoal(1, new JumpAttack(this, 2, 4));
        this.goalSelector.addGoal(2, new JumpOverBlockGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this,  1f, true));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class,false, LivingEntity::canBeSeenAsEnemy));

    }

    @Override
    public void onAddedToLevel(){
        super.onAddedToLevel();
        this.setVariant(random.nextInt(getTexturesMap().size()));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT_ID, 0);
    }


    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Variant", this.getVariant());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setVariant(pCompound.getInt("Variant"));
    }

    @Override
    public void setVariant(@NotNull Integer integer) {
        this.entityData.set(DATA_VARIANT_ID, integer);
    }

    @Override
    public @NotNull Integer getVariant() {
        return this.entityData.get(DATA_VARIANT_ID);
    }

    @Override
    public Map<Integer, ResourceLocation> getTexturesMap() {
        return textures;
    }

    static Map<Integer, ResourceLocation> textures = Map.of(
            0, TerraEntity.space("textures/entity/crawdad/blue.png"),
            1, TerraEntity.space("textures/entity/crawdad/red.png")
    );

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this, "Controller", 5, state->{
            if (this.swinging){
                return state.setAndContinue(attack);
            }

            return state.isMoving()? state.setAndContinue(walk) : PlayState.STOP;
        }));
    }

    @Override
    public int getCurrentSwingDuration() {
        return 12;
    }
}
