package org.confluence.mod.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IForgeShearable;
import org.confluence.mod.common.init.ModLootTables;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.entity.ModEntities;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class RainbowSheep extends Animal implements IForgeShearable {
    private static final int EAT_ANIMATION_TICKS = 40;
    private static final EntityDataAccessor<Boolean> DATA_SHEARED = SynchedEntityData.defineId(RainbowSheep.class, EntityDataSerializers.BOOLEAN);

    private int eatAnimationTick;
    private EatHallowBlockGoal eatBlockGoal;

    public RainbowSheep(EntityType<? extends RainbowSheep> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.1, this::isFood, false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1));
        this.goalSelector.addGoal(5, this.eatBlockGoal = new EatHallowBlockGoal(this));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ItemTags.SHEEP_FOOD);
    }

    @Override
    protected void customServerAiStep() {
        this.eatAnimationTick = eatBlockGoal.getEatAnimationTick();
        super.customServerAiStep();
    }

    @Override
    public void aiStep() {
        if (level().isClientSide) {
            this.eatAnimationTick = Math.max(0, eatAnimationTick - 1);
        }
        super.aiStep();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SHEARED, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8.0)
                .add(Attributes.MOVEMENT_SPEED, 0.23F);
    }

    @Override
    public ResourceLocation getDefaultLootTable() {
        if (isSheared()) {
            return getType().getDefaultLootTable();
        }
        return ModLootTables.SHEEP_RAINBOW_WOOL;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 10) {
            this.eatAnimationTick = EAT_ANIMATION_TICKS;
        } else {
            super.handleEntityEvent(id);
        }
    }

    public float getHeadEatPositionScale(float partialTick) {
        if (eatAnimationTick <= 0) {
            return 0.0F;
        }
        if (eatAnimationTick >= 4 && eatAnimationTick <= 36) {
            return 1.0F;
        }
        return eatAnimationTick < 4 ? (eatAnimationTick - partialTick) / 4.0F : -(eatAnimationTick - 40 - partialTick) / 4.0F;
    }

    public float getHeadEatAngleScale(float partialTick) {
        if (eatAnimationTick > 4 && eatAnimationTick <= 36) {
            float f = (eatAnimationTick - 4 - partialTick) / 32.0F;
            return Mth.PI / 5 + 0.21991149F * Mth.sin(f * 28.7F);
        } else {
            return eatAnimationTick > 0 ? Mth.PI / 5 : getXRot() * Mth.DEG_TO_RAD;
        }
    }

    @Override
    public List<ItemStack> onSheared(@Nullable Player player, ItemStack item, Level level, BlockPos pos, int fortune) {
        if (level.isClientSide) {
            return Collections.emptyList();
        }
        Collection<ItemEntity> previous = captureDrops(new ArrayList<>());

        SoundSource category = player == null ? SoundSource.BLOCKS : SoundSource.PLAYERS;
        level().playSound(null, this, SoundEvents.SHEEP_SHEAR, category, 1.0F, 1.0F);
        setSheared(true);
        int i = 1 + random.nextInt(3);

        for (int j = 0; j < i; j++) {
            ItemEntity itemEntity = spawnAtLocation(DecorativeBlocks.RAINBOW_WOOL, 1);
            if (itemEntity == null) continue;
            itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().add(
                    ((random.nextFloat() - random.nextFloat()) * 0.1F),
                    (random.nextFloat() * 0.05F),
                    ((random.nextFloat() - random.nextFloat()) * 0.1F)
            ));
        }

        return captureDrops(previous).stream().map(ItemEntity::getItem).toList();
    }

    @Override
    public boolean isShearable(ItemStack item, Level level, BlockPos pos) {
        return isAlive() && !isSheared() && !isBaby();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Sheared", this.isSheared());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setSheared(compound.getBoolean("Sheared"));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SHEEP_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.SHEEP_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SHEEP_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState block) {
        this.playSound(SoundEvents.SHEEP_STEP, 0.15F, 1.0F);
    }

    public boolean isSheared() {
        return entityData.get(DATA_SHEARED);
    }

    public void setSheared(boolean sheared) {
        entityData.set(DATA_SHEARED, sheared);
    }

    public @Nullable RainbowSheep getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return ModEntities.RAINBOW_SHEEP.get().create(level);
    }

    @Override
    public void ate() {
        super.ate();
        setSheared(false);
        if (isBaby()) {
            ageUp(60);
        }
    }

    public static class EatHallowBlockGoal extends Goal {
        private final Mob mob;
        private final Level level;
        private int eatAnimationTick;

        public EatHallowBlockGoal(Mob mob) {
            this.mob = mob;
            this.level = mob.level();
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            if (mob.getRandom().nextInt(mob.isBaby() ? 50 : 1000) != 0) {
                return false;
            }
            BlockPos pos = mob.blockPosition();
            return level.getBlockState(pos).is(NatureBlocks.HALLOW_GRASS.get()) ||
                    level.getBlockState(pos.below()).is(NatureBlocks.HALLOW_GRASS_BLOCK.get());
        }

        @Override
        public void start() {
            this.eatAnimationTick = this.adjustedTickDelay(EAT_ANIMATION_TICKS);
            this.level.broadcastEntityEvent(this.mob, (byte) 10);
            this.mob.getNavigation().stop();
        }

        @Override
        public void stop() {
            this.eatAnimationTick = 0;
        }

        @Override
        public boolean canContinueToUse() {
            return this.eatAnimationTick > 0;
        }

        public int getEatAnimationTick() {
            return this.eatAnimationTick;
        }

        @Override
        public void tick() {
            this.eatAnimationTick = Math.max(0, eatAnimationTick - 1);
            if (eatAnimationTick == adjustedTickDelay(4)) {
                BlockPos pos = mob.blockPosition();
                if (level.getBlockState(pos).is(NatureBlocks.HALLOW_GRASS.get())) {
                    if (net.minecraftforge.event.ForgeEventFactory.canEntityGrief(level, mob)) {
                        level.destroyBlock(pos, false);
                    }
                    mob.ate();
                } else {
                    BlockPos below = pos.below();
                    if (this.level.getBlockState(below).is(NatureBlocks.HALLOW_GRASS_BLOCK.get())) {
                        if (net.minecraftforge.event.ForgeEventFactory.canEntityGrief(level, mob)) {
                            int id = Block.getId(NatureBlocks.HALLOW_GRASS_BLOCK.get().defaultBlockState());
                            level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, below, id);
                            level.setBlock(below, Blocks.DIRT.defaultBlockState(), 2);
                        }
                        mob.ate();
                    }
                }
            }
        }
    }
}
