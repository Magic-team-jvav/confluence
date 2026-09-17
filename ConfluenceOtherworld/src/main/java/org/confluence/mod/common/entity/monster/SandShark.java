package org.confluence.mod.common.entity.monster;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.SelectiveBlockCollision;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.init.ModTags;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

public final class SandShark extends BaseMonster {
    private static final RawAnimation SWIM = RawAnimation.begin().thenLoop("move.swim");
    private int jumpCooldown;
    private Vec3 wander = Vec3.ZERO;

    public SandShark(EntityType<? extends SandShark> type, Level level) {
        super(type, level);
        configurePlayerTargetLineOfSight(false);
    }

    public static boolean canBurrow(BlockState state) {
        return state.is(BlockTags.SAND) || state.is(ModTags.Blocks.PURE_CONVERSION_SANDSTONE)
                || state.is(ModTags.Blocks.PURE_CONVERSION_HARDENED_SAND_BLOCK)
                || state.is(ModTags.Blocks.CORRUPTION_CONVERSION_SANDSTONE) || state.is(ModTags.Blocks.CORRUPTION_CONVERSION_HARDENED_SAND_BLOCK)
                || state.is(ModTags.Blocks.CRIMSON_CONVERSION_SANDSTONE) || state.is(ModTags.Blocks.CRIMSON_CONVERSION_HARDENED_SAND_BLOCK)
                || state.is(ModTags.Blocks.HALLOW_CONVERSION_SANDSTONE) || state.is(ModTags.Blocks.HALLOW_CONVERSION_HARDENED_SAND_BLOCK);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new BTNode() {
                    @Override
                    public BTStatus execute() {
                        if (jumpCooldown > 0) jumpCooldown--;
                        boolean sand = level().getBlockStates(getBoundingBox().inflate(0.05)).anyMatch(SandShark::canBurrow);
                        if (tickCount % 40 == 0)
                            wander = new Vec3(random.nextDouble() - 0.5, 0.0, random.nextDouble() - 0.5).normalize().scale(0.15);
                        Vec3 movement = getDeltaMovement();
                        if (sand || isInWater()) {
                            Vec3 desired = getTarget() == null ? wander : getTarget().position().add(0.0, isInWater() ? 0.5 : -1.0, 0.0).subtract(position()).normalize().scale(0.45);
                            movement = movement.lerp(desired, 0.12);
                            if (sand && getTarget() != null && jumpCooldown == 0 && distanceToSqr(getTarget()) < 36.0) {
                                jumpCooldown = 35;
                                Vec3 aim = getTarget().position().subtract(position()).multiply(1.0, 0.0, 1.0).normalize();
                                movement = new Vec3(aim.x * 0.6, 0.75, aim.z * 0.6);
                            }
                        } else {
                            movement = movement.add(0.0, -0.08, 0.0).multiply(0.98, 0.98, 0.98);
                        }
                        setDeltaMovement(movement);
                        if (movement.lengthSqr() > 0.001)
                            faceCombatPosition(position().add(movement), 8.0F, 60.0F);
                        return BTStatus.RUNNING;
                    }
                };
            }
        };
    }

    @Override
    public void travel(Vec3 input) {
        if (isEffectiveAi()) move(MoverType.SELF, getDeltaMovement());
    }

    @Override
    public void move(MoverType type, Vec3 movement) {
        Vec3 allowed = SelectiveBlockCollision.resolve(this, movement, SandShark::canBurrow);
        boolean previous = noPhysics;
        noPhysics = true;
        try {
            super.move(type, allowed);
        } finally {
            noPhysics = previous;
        }
        horizontalCollision = movement.x != allowed.x || movement.z != allowed.z;
        verticalCollision = movement.y != allowed.y;
        setOnGround(verticalCollision && movement.y < 0.0);
        setDeltaMovement(movement.x == allowed.x ? movement.x : 0.0, movement.y == allowed.y ? movement.y : 0.0, movement.z == allowed.z ? movement.z : 0.0);
    }

    @Override
    protected boolean hasEntityContactAttack() {
        return true;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean isInWall() {
        return !canBurrow(level().getBlockState(blockPosition())) && super.isInWall();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Swim", 3, state -> state.setAndContinue(SWIM)));
    }
}
