package org.confluence.mod.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraft.sounds.SoundEvent;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.projectile.HostileParticleProjectile;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.init.ModSoundEvents;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

public final class WaterBoltMimic extends BaseFlyingMonster {
    private static final EntityDataAccessor<Boolean> AWAKE = SynchedEntityData.defineId(WaterBoltMimic.class, EntityDataSerializers.BOOLEAN);
    private static final RawAnimation DISGUISE = RawAnimation.begin().thenLoop("pose.folded");
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("misc.idle");

    public WaterBoltMimic(EntityType<? extends WaterBoltMimic> type, Level level) {
        super(type, level);
        noPhysics = true;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(AWAKE, false);
    }

    @Override
    protected boolean mustSeePlayerTarget() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        if (hurt && !level().isClientSide) entityData.set(AWAKE, true);
        return hurt;
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new BTNode() {
                    private int remaining;
                    private int action;
                    private Vec3 dash = Vec3.ZERO;

                    @Override
                    public BTStatus execute() {
                        LivingEntity target = getTarget();
                        if (!entityData.get(AWAKE) || target == null || !target.isAlive()) {
                            setDeltaMovement(Vec3.ZERO);
                            return BTStatus.RUNNING;
                        }
                        Vec3 offset = target.getEyePosition().subtract(getEyePosition());
                        if (remaining-- <= 0) {
                            action = random.nextInt(3);
                            remaining = action == 0 ? 50 : 20;
                            dash = offset.normalize().scale(action == 1 ? -0.45 : 0.75);
                        }
                        if (action == 0) {
                            Vec3 desired = offset.normalize().scale((offset.length() - 5.0) * 0.04);
                            if (desired.lengthSqr() > 0.09)
                                desired = desired.normalize().scale(0.3);
                            setDeltaMovement(getDeltaMovement().lerp(desired, 0.12));
                            if (remaining == 25) {
                                HostileParticleProjectile sphere = ModEntities.DARK_CASTER_PROJECTILE.get().create(level());
                                if (sphere != null) {
                                    sphere.configure(WaterBoltMimic.this, target, (float) getAttributeValue(Attributes.ATTACK_DAMAGE));
                                    if (!level().addFreshEntity(sphere)) sphere.discard();
                                }
                            }
                        } else setDeltaMovement(getDeltaMovement().lerp(dash, 0.18));
                        if (offset.horizontalDistanceSqr() > 0.0001)
                            faceCombatDirection(offset, 8.0F, 8.0F);
                        hasImpulse = true;
                        return BTStatus.RUNNING;
                    }
                };
            }
        };
    }

    public static @Nullable BlockPos findBookSpace(ServerLevelAccessor level, BlockPos origin) {
        BlockPos nearest = null;
        double distance = Double.MAX_VALUE;
        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-8, -4, -8), origin.offset(8, 4, 8))) {
            if (!level.getBlockState(pos).is(Blocks.BOOKSHELF)) continue;
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos space = pos.relative(direction);
                double squared = space.distSqr(origin);
                if (squared < distance && level.getBlockState(space).isAir()
                        && level.getLevel().getNearestPlayer(space.getX() + 0.5, space.getY(), space.getZ() + 0.5, 24.0, false) == null) {
                    nearest = space.immutable();
                    distance = squared;
                }
            }
        }
        return nearest;
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData group, @Nullable CompoundTag tag) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, reason, group, tag);
        if (reason == MobSpawnType.NATURAL) {
            BlockPos book = findBookSpace(level, blockPosition());
            if (book != null) setPos(Vec3.atBottomCenterOf(book));
        }
        return data;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Disguise/Fly", 3, state -> state.setAndContinue(entityData.get(AWAKE) ? FLY : DISGUISE)));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Awake", entityData.get(AWAKE));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(AWAKE, tag.getBoolean("Awake"));
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.METAL_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.METAL_DEATH.get();
    }

}
