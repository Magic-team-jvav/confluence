package org.confluence.terraentity.entity.monster.humanoid;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.api.entity.animation.IUseItemAnimatable;
import org.confluence.terraentity.entity.ai.goal.TERangedAttackGoal;
import org.confluence.terraentity.entity.animation.BoneStateMachine;
import org.confluence.terraentity.entity.animation.BoneStates;
import org.confluence.terraentity.entity.monster.AbstractMonster;
import org.confluence.terraentity.entity.monster.prefab.AttributeBuilder;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.constant.DefaultAnimations;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.temporal.ChronoField;

/**
 * 人形怪，可以远程攻击也可以近战，根据手中物品决定
 */
public class HumanoidMonster extends AbstractMonster implements RangedAttackMob, IUseItemAnimatable<BoneStates> {

    BoneStateMachine<BoneStates> leftArmBoneStateMachine;
    BoneStateMachine<BoneStates> rightArmBoneStateMachine;
    AttributeBuilder builder;
    protected final TERangedAttackGoal<?> bowGoal = this.createBowGoal();
    protected final Goal meleeGoal = this.createMeleeGoal();

    public HumanoidMonster(EntityType<? extends HumanoidMonster> entityType, Level level, AttributeBuilder builder) {
        super(entityType, level, builder);
        this.reassessWeaponGoal();
        if(level.isClientSide){
            leftArmBoneStateMachine = new BoneStateMachine<>(BoneStates.IDLE);
            rightArmBoneStateMachine = new BoneStateMachine<>(BoneStates.IDLE);
        }
        this.builder = builder;
    }

    public HumanoidMonster(EntityType<? extends HumanoidMonster> entityType, Level level) {
        this(entityType, level, new HumanoidBuilder());
    }

    @Override
    protected boolean isSunBurnTick() {
        return false;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity entity) {
        this.swing(InteractionHand.MAIN_HAND, true);
        return super.doHurtTarget(entity);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Walk/Idle", 5, state ->{
            state.setControllerSpeed((float) (this.getAttributeValue(Attributes.MOVEMENT_SPEED) / 0.25f));
            return state.setAndContinue(state.isMoving() ? DefaultAnimations.WALK : DefaultAnimations.IDLE);
        }
        ));
    }

    @Override
    public int getCurrentSwingDuration() {
        return 10;
    }

//    protected void playStepSound(BlockPos pos, BlockState block) {
//        this.playSound(this.getStepSound(), 0.15F, 1.0F);
//    }
//
//    protected abstract SoundEvent getStepSound();

    @Override
    public void aiStep() {
        boolean flag = this.isSunBurnTick();
        if (flag) {
            ItemStack itemstack = this.getItemBySlot(EquipmentSlot.HEAD);
            if (!itemstack.isEmpty()) {
                if (itemstack.isDamageableItem()) {
                    Item item = itemstack.getItem();
                    itemstack.setDamageValue(itemstack.getDamageValue() + this.random.nextInt(2));
                    if (itemstack.getDamageValue() >= itemstack.getMaxDamage()) {
                        this.onEquippedItemBroken(item, EquipmentSlot.HEAD);
                        this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                    }
                }

                flag = false;
            }

            if (flag) {
                this.igniteForSeconds(8.0F);
            }
        }

        super.aiStep();
    }

    @Override
    public void rideTick() {
        super.rideTick();
        Entity var2 = this.getControlledVehicle();
        if (var2 instanceof PathfinderMob pathfindermob) {
            this.yBodyRot = pathfindermob.yBodyRot;
        }

    }

    @Override
    protected void populateDefaultEquipmentSlots(@NotNull RandomSource random, @NotNull DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);
        if(builder instanceof HumanoidMonster.HumanoidBuilder builder1){
            this.setItemSlot(EquipmentSlot.MAINHAND, builder1.mainHand);

        }
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, @NotNull DifficultyInstance difficulty, @NotNull MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        spawnGroupData = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
        RandomSource randomsource = level.getRandom();
        this.populateDefaultEquipmentSlots(randomsource, difficulty);
        this.populateDefaultEquipmentEnchantments(level, randomsource, difficulty);
        this.reassessWeaponGoal();
        this.setCanPickUpLoot(randomsource.nextFloat() < 0.55F * difficulty.getSpecialMultiplier());
        if (this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            LocalDate localdate = LocalDate.now();
            int i = localdate.get(ChronoField.DAY_OF_MONTH);
            int j = localdate.get(ChronoField.MONTH_OF_YEAR);
            if (j == 10 && i == 31 && randomsource.nextFloat() < 0.25F) {
                this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(randomsource.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
                this.armorDropChances[EquipmentSlot.HEAD.getIndex()] = 0.0F;
            }
        }

        return spawnGroupData;
    }

    public void reassessWeaponGoal() {
        if (!this.level().isClientSide) {
            this.goalSelector.removeGoal(this.meleeGoal);
            this.goalSelector.removeGoal(this.bowGoal);
            ItemStack itemstack = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, (item) -> {
                return item instanceof BowItem;
            }));
            if (itemstack.getItem() instanceof BowItem) {
                int i = this.getHardAttackInterval();
                if (this.level().getDifficulty() != Difficulty.HARD) {
                    i = this.getAttackInterval();
                }

                this.bowGoal.setMinAttackInterval(i);
                this.goalSelector.addGoal(4, this.bowGoal);
            } else {
                this.goalSelector.addGoal(4, this.meleeGoal);
            }
        }
    }

    protected Goal createMeleeGoal() {
        return new MeleeAttackGoal(this, 1.2, false) {
            public void stop() {
                super.stop();
                HumanoidMonster.this.setAggressive(false);
            }

            public void start() {
                super.start();
                HumanoidMonster.this.setAggressive(true);
            }
        };
    }

    protected TERangedAttackGoal<?> createBowGoal() {
        return new TERangedAttackGoal<>(this, 1.0, 50, 15.0F);
    }

    protected int getHardAttackInterval() {
        return 20;
    }

    protected int getAttackInterval() {
        return 40;
    }

    @Override
    public void performRangedAttack(@NotNull LivingEntity target, float distanceFactor) {
        ItemStack weapon = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, (item) -> {
            return item instanceof BowItem;
        }));
        ItemStack itemstack1 = this.getProjectile(weapon);
        AbstractArrow abstractarrow = this.getArrow(itemstack1, distanceFactor, weapon);
        Item var7 = weapon.getItem();
        if (var7 instanceof ProjectileWeaponItem weaponItem) {
            abstractarrow = weaponItem.customArrow(abstractarrow, itemstack1, weapon);
        }

        double d0 = target.getX() - this.getX();
        double d1 = target.getY(0.3333333333333333) - abstractarrow.getY();
        double d2 = target.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        abstractarrow.shoot(d0, d1 + d3 * 0.20000000298023224, d2, 1.6F, (float)(14 - this.level().getDifficulty().getId() * 4));
        this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(abstractarrow);
    }

    protected AbstractArrow getArrow(ItemStack arrow, float velocity, @Nullable ItemStack weapon) {
        return ProjectileUtil.getMobArrow(this, arrow, velocity, weapon);
    }

    @Override
    public boolean canFireProjectileWeapon(@NotNull ProjectileWeaponItem projectileWeapon) {
        return projectileWeapon == Items.BOW;
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.reassessWeaponGoal();
    }

    @Override
    public void setItemSlot(@NotNull EquipmentSlot slot, @NotNull ItemStack stack) {
        super.setItemSlot(slot, stack);
        if (!this.level().isClientSide) {
            this.reassessWeaponGoal();
        }

    }

    public boolean isShaking() {
        return this.isFullyFrozen();
    }

    @Override
    public boolean isChargingCrossbow() {
        return false;
    }

    @Override
    public int getChargingTicks() {
        return 0;
    }

    @Override
    public BoneStateMachine<BoneStates> getLeftArmBoneStateMachine() {
        return leftArmBoneStateMachine;
    }

    @Override
    public BoneStateMachine<BoneStates> getRightArmBoneStateMachine() {
        return rightArmBoneStateMachine;
    }

    @Override
    public boolean isLieDown() {
        return false;
    }

    @Override
    public Vec3 getVehicleAttachmentPoint(Entity entity) {
        return super.getVehicleAttachmentPoint(entity).add(0,0.65,0);
    }

    public static class HumanoidBuilder extends AttributeBuilder {
        private ItemStack mainHand = ItemStack.EMPTY;

        public HumanoidBuilder() {
        }

        public HumanoidBuilder setMainHand(ItemStack mainHand) {
            this.mainHand = mainHand;
            return this;
        }

    }


}
