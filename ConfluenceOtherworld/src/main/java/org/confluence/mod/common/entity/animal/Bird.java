package org.confluence.mod.common.entity.animal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowMobGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import javax.annotation.Nullable;
import java.util.Iterator;

/// 能够起飞、降落并在三维空间巡游的普通鸟类。
///
/// <p>鸟类保留重力，因此停止飞行后会自然落地；飞行移动控制器只负责空中的转向与加速。
/// 原版成熟的漂浮、观察、树冠巡游与跟随动作通过叶节点接入行为树，实体本身仍只有一个
/// 行为调度器，不会重新安装第二套 Goal 组合。</p>
public class Bird extends BaseFlyingCritter implements FlyingAnimal {
    private static final RawAnimation FLY_ONLY =
            RawAnimation.begin().thenLoop("move.fly");
    public float flap;
    public float flapSpeed;
    public float oFlapSpeed;
    public float oFlap;
    private float flapping = 1.0F;
    private float nextFlap = 1.0F;
    private boolean partyBird;
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
                .add(Attributes.FLYING_SPEED, 0.4);
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
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return withPassivePanic(createBirdDailyRoutine(), 1.25);
            }
        };
    }

    /// 创建鸟类日常行为分支，供继承鸟类运动语义的昆虫与仙灵复用。
    ///
    /// <p>恐慌分支由具体实体包在最外层，以便仙灵在受伤时优先逃生，同时仍可在平常状态下
    /// 用引导玩家的动作抢占日常巡游。</p>
    protected final BTNode createBirdDailyRoutine() {
        return SelectorNode.of(
                new VanillaGoalAction(new FloatGoal(this)),
                new VanillaGoalAction(
                        new LookAtPlayerGoal(this, Player.class, 8.0F)),
                new VanillaGoalAction(new BirdWanderGoal(this, 1.0)),
                new VanillaGoalAction(
                        new FollowMobGoal(this, 1.0, 3.0F, 7.0F)));
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

    /// 为只提供 {@code move.fly} 的昆虫资源安装持续飞行动画。
    ///
    /// <p>这些资源没有 {@code move.walk} 或 {@code misc.idle}，使用通用走路控制器会持续输出
    /// 缺失动画警告，并在停顿阶段让翅膀完全静止。</p>
    protected final void registerFlyOnlyController(
            AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(
                this,
                "Fly",
                5,
                state -> state.setAndContinue(FLY_ONLY)));
    }

    @Override
    public void aiStep() {
        if (jukebox == null
                || !jukebox.closerToCenterThan(position(), 3.46)
                || !level().getBlockState(jukebox).is(Blocks.JUKEBOX)) {
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
        return new Vec3(
                0.0,
                0.5F * getEyeHeight(),
                getBbWidth() * 0.4F);
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
            BlockPos.MutableBlockPos above =
                    new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos below =
                    new BlockPos.MutableBlockPos();
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
                BlockState support = mob.level().getBlockState(
                        below.setWithOffset(candidate, Direction.DOWN));
                boolean tree = support.getBlock() instanceof LeavesBlock
                        || support.is(BlockTags.LOGS);
                if (tree
                        && mob.level().isEmptyBlock(candidate)
                        && mob.level().isEmptyBlock(
                        above.setWithOffset(candidate, Direction.UP))) {
                    return Vec3.atBottomCenterOf(candidate);
                }
            }
            return null;
        }
    }
}
