package org.confluence.mod.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.FlyingPursuitAction;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;
import org.confluence.mod.common.init.ModSoundEvents;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.Iterator;

public class Pixie extends BaseFlyingMonster {
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("move.fly");

    public Pixie(EntityType<? extends Pixie> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 180, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseFlyingMonster.createFlyingAttributes()
                .add(Attributes.MAX_HEALTH, 30.0).add(Attributes.ATTACK_DAMAGE, 8.0);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(new HasTargetCondition(Pixie.this), new FlyingPursuitAction(Pixie.this, 2.0)),
                        new VanillaGoalAction(new PixieWanderGoal(Pixie.this, 1.0)));
            }
        };
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Fly", 0, state -> state.setAndContinue(FLY)));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.PIXIE_FREE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.PIXIE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.PIXIE_DEATH.get();
    }

    @Override
    protected boolean hasPushableBody() {
        return true;
    }

    /// 妖精与 1.21 一致，可以利用水面漂浮导航，但不会穿门。
    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        return navigation;
    }

    @Override
    protected double contactAttackInflation() {
        return 0.5;
    }

    private static final class PixieWanderGoal extends WaterAvoidingRandomFlyingGoal {
        private PixieWanderGoal(Pixie pixie, double speedModifier) {
            super(pixie, speedModifier);
        }

        @Override
        protected @Nullable Vec3 getPosition() {
            Vec3 position = mob.isInWater() ? LandRandomPos.getPos(mob, 15, 15) : null;
            if (mob.getRandom().nextFloat() >= probability) position = getTreePosition();
            return position == null ? super.getPosition() : position;
        }

        private @Nullable Vec3 getTreePosition() {
            BlockPos origin = mob.blockPosition();
            BlockPos.MutableBlockPos below = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos above = new BlockPos.MutableBlockPos();
            Iterator<BlockPos> positions = BlockPos.betweenClosed(
                    Mth.floor(mob.getX() - 3.0), Mth.floor(mob.getY() - 6.0), Mth.floor(mob.getZ() - 3.0),
                    Mth.floor(mob.getX() + 3.0), Mth.floor(mob.getY() + 6.0), Mth.floor(mob.getZ() + 3.0)).iterator();
            while (positions.hasNext()) {
                BlockPos candidate = positions.next();
                if (origin.equals(candidate)) continue;
                BlockState support = mob.level().getBlockState(below.setWithOffset(candidate, Direction.DOWN));
                if ((support.getBlock() instanceof LeavesBlock || support.is(BlockTags.LOGS))
                        && mob.level().isEmptyBlock(candidate) && mob.level().isEmptyBlock(above.setWithOffset(candidate, Direction.UP))) {
                    return Vec3.atBottomCenterOf(candidate);
                }
            }
            return null;
        }
    }
}
