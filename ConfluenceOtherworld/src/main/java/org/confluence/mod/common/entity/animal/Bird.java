package org.confluence.mod.common.entity.animal;

import PortLib.extensions.net.minecraft.world.entity.ai.attributes.Attributes.PortAttributesExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibAttributes;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModSoundEvents;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.Iterator;

/// 直接沿用原版动物的 Goal 调度，并使用鹦鹉式飞行导航与树冠巡游。
public class Bird extends Animal implements FlyingAnimal, CritterVisual {
    private static final RawAnimation FLY_ONLY = RawAnimation.begin().thenLoop("move.fly");
    public float flap;
    public float flapSpeed;
    public float oFlapSpeed;
    public float oFlap;
    private float flapping = 1.0F;
    private float nextFlap = 1.0F;
    private boolean partyBird;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    @Nullable
    private BlockPos jukebox;

    public Bird(EntityType<? extends Bird> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 10, false);
        setNoGravity(false);
        setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -1.0F);
        setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
        setPathfindingMalus(BlockPathTypes.COCOA, -1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.FLYING_SPEED, 0.4)
                .add(LibAttributes.getAttackDamage().get(), 3.0)
                .add(PortAttributesExtension.fallDamageMultiplier().get(), 0.0);
    }

    /// 鸟类没有幼年模型和幼年行为，年龄数据不应改变客户端缩放、碰撞或行为选择。
    @Override
    public boolean isBaby() {
        return false;
    }

    /// 鸟类在 1.21 侧只作为可捕捉的小动物存在，不参与原版繁殖流程。
    @Override
    public boolean canMate(net.minecraft.world.entity.animal.Animal other) {
        return false;
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new PanicGoal(this, 1.25));
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(2, new BirdWanderGoal(this, 1.0));
        goalSelector.addGoal(3, new FollowMobGoal(this, 1.0, 3.0F, 7.0F));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, spawnType, data, tag);
        String key = variantSaveKey();
        if (key != null && (tag == null || !tag.contains(key))) initializeSpawnVariant();
        return result;
    }

    protected @Nullable String variantSaveKey() {
        return null;
    }

    protected void initializeSpawnVariant() {}

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    @Override
    public ResourceLocation getModelPath() {return Confluence.asResource("animal/bird");}
    @Override
    public ResourceLocation getTexturePath() {
        return Confluence.asResource("textures/entity/animal/bird.png");
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericFlyIdleController(this));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.ROUTINE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.ROUTINE_DEATH.get();
    }

    /// 为只提供 {@code move.fly} 的昆虫资源安装持续飞行动画。
    ///
    /// 这些资源没有 {@code move.walk} 或 {@code misc.idle}，使用通用走路控制器会持续输出
    /// 缺失动画警告，并在停顿阶段让翅膀完全静止。
    protected final void registerFlyOnlyController(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Fly", 5, state -> state.setAndContinue(FLY_ONLY)));
    }

    @Override
    public void aiStep() {
        if (jukebox == null || !jukebox.closerToCenterThan(position(), 3.46) || !level().getBlockState(jukebox).is(Blocks.JUKEBOX)) {
            partyBird = false;
            jukebox = null;
        }
        super.aiStep();
        calculateFlapping();
    }

    @Override
    public void setRecordPlayingNearby(BlockPos pos, boolean isPartying) {
        jukebox = pos;
        partyBird = isPartying;
    }

    /// 返回唱片机互动状态，供动画、渲染和附属模组复用。
    public boolean isPartyBird() {
        return partyBird;
    }

    @Override
    public boolean isFlying() {
        return !onGround();
    }

    /// 更新翅膀相位并限制空中下落速度。公开相位字段供模型与附属渲染器平滑插值。
    private void calculateFlapping() {
        oFlap = flap;
        oFlapSpeed = flapSpeed;
        flapSpeed += (!onGround() && !isPassenger() ? 4.0F : -1.0F) * 0.3F;
        flapSpeed = Mth.clamp(flapSpeed, 0.0F, 1.0F);
        if (!onGround() && flapping < 1.0F) {
            flapping = 1.0F;
        }
        flapping *= 0.9F;

        Vec3 movement = getDeltaMovement();
        if (!onGround() && movement.y < 0.0) {
            setDeltaMovement(movement.multiply(1.0, 0.6, 1.0));
        }
        flap += flapping * 2.0F;
    }

    @Override
    protected boolean isFlapping() {
        return flyDist > nextFlap;
    }

    @Override
    protected void onFlap() {
        playSound(SoundEvents.PARROT_FLY, 0.15F, 1.0F);
        nextFlap = flyDist + flapSpeed / 2.0F;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        playSound(SoundEvents.PARROT_STEP, 0.15F, 1.0F);
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.NEUTRAL;
    }

    @Override
    protected void doPush(Entity entity) {
        if (!(entity instanceof Player)) {
            super.doPush(entity);
        }
    }

    @Override
    public Vec3 getLeashOffset() {
        return new Vec3(0.0, 0.5F * getEyeHeight(), getBbWidth() * 0.4F);
    }

    public float getVoicePitch() {
        return getPitch(random);
    }

    public static float getPitch(RandomSource random) {
        return (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F;
    }

    /// 鸟类巡游会优先寻找附近树冠落脚点，找不到时再退回普通随机飞行。
    static final class BirdWanderGoal
            extends WaterAvoidingRandomFlyingGoal {
        BirdWanderGoal(PathfinderMob mob, double speedModifier) {
            super(mob, speedModifier);
        }

        @Nullable
        @Override
        protected Vec3 getPosition() {
            Vec3 position = null;
            if (mob.isInWater()) {
                position = LandRandomPos.getPos(mob, 15, 15);
            }
            if (mob.getRandom().nextFloat() >= probability) {
                position = findTreePosition();
            }
            return position == null ? super.getPosition() : position;
        }

        @Nullable
        private Vec3 findTreePosition() {
            BlockPos origin = mob.blockPosition();
            BlockPos.MutableBlockPos above = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos below = new BlockPos.MutableBlockPos();
            Iterator<BlockPos> candidates = BlockPos.betweenClosed(
                    Mth.floor(mob.getX() - 3.0),
                    Mth.floor(mob.getY() - 6.0),
                    Mth.floor(mob.getZ() - 3.0),
                    Mth.floor(mob.getX() + 3.0),
                    Mth.floor(mob.getY() + 6.0),
                    Mth.floor(mob.getZ() + 3.0)).iterator();

            while (candidates.hasNext()) {
                BlockPos candidate = candidates.next();
                if (origin.equals(candidate)) {
                    continue;
                }
                BlockState support = mob.level().getBlockState(below.setWithOffset(candidate, Direction.DOWN));
                boolean tree = support.getBlock() instanceof LeavesBlock
                        || support.is(BlockTags.LOGS);
                if (tree && mob.level().isEmptyBlock(candidate) && mob.level().isEmptyBlock(above.setWithOffset(candidate, Direction.UP))) {
                    return Vec3.atBottomCenterOf(candidate);
                }
            }
            return null;
        }
    }
}
