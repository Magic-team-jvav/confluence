package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.FlyingVolleyCombatAction;
import org.confluence.mod.common.entity.ai.bt.leaf.FlyWanderAction;
import org.confluence.mod.common.entity.ai.bt.leaf.SteeringDashAction;
import org.confluence.mod.common.entity.projectile.HarpyFeatherProjectile;
import org.confluence.mod.common.init.entity.ModEntities;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

public class Harpy extends ReboundingFlyingMonster {
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("move.fly");

    public Harpy(
            EntityType<? extends BaseFlyingMonster> type,
            Level level) {
        super(type, level);
    }

    @Override
    protected Vec3 reboundVelocity(Vec3 requested, Vec3 allowed) {
        Vec3 rebound = requested;
        if (allowed.x != requested.x) {
            rebound = new Vec3(
                    -requested.x * 0.8,
                    rebound.y + 0.2,
                    rebound.z);
        }
        if (allowed.z != requested.z) {
            rebound = new Vec3(
                    rebound.x,
                    rebound.y + 0.2,
                    -requested.z * 0.8);
        }
        return rebound;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseFlyingMonster.createFlyingAttributes()
                .add(Attributes.ATTACK_DAMAGE, 10.0);
    }

    @Override
    protected BTRoot createBT() {
        BTNode combat = new FlyingVolleyCombatAction(
                this,
                new SteeringDashAction(
                        this,
                        0.95,
                        0.5,
                        0.02,
                        10.0,
                        90.0,
                        30.0,
                        30),
                this::createFeatherProjectile,
                150,
                171,
                192,
                213);
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(new HasTargetCondition(Harpy.this), combat),
                        new FlyWanderAction(Harpy.this, 0.15, 10));
            }
        };
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Fly", 0, state -> state.setAndContinue(FLY)));
    }

    HarpyFeatherProjectile createFeatherProjectile(LivingEntity target) {
        HarpyFeatherProjectile projectile = new HarpyFeatherProjectile(
                ModEntities.HARPY_FEATHER.get(), level());
        projectile.configure(
                this,
                target,
                (float) getAttributeValue(Attributes.ATTACK_DAMAGE),
                0.5F,
                2.0F);
        swing(InteractionHand.MAIN_HAND);
        return projectile;
    }
}
