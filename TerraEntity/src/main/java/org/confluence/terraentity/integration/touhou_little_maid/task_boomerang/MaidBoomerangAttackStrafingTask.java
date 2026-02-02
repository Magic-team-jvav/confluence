package org.confluence.terraentity.integration.touhou_little_maid.task_boomerang;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.ItemStack;
import org.confluence.terraentity.item.Boomerang;

public class MaidBoomerangAttackStrafingTask extends Behavior<EntityMaid> {
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;

    public MaidBoomerangAttackStrafingTask() {
        super(ImmutableMap.of(
                MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT,
                MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT,
                MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT),
                1200);
    }

    protected boolean checkExtraStartConditions(ServerLevel worldIn, EntityMaid owner) {
        return owner.getMainHandItem().getItem() instanceof Boomerang &&
                owner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).filter(Entity::isAlive).isPresent();
    }

    protected void tick(ServerLevel worldIn, EntityMaid owner, long gameTime) {
        ItemStack stack = owner.getMainHandItem();
        if (stack.getItem() instanceof Boomerang) {
            owner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).ifPresent((target) -> {
                int maxAttackDistance = 10;
                double distance = owner.distanceTo(target);
                if (distance < (double)owner.searchRadius()) {
                    ++this.strafingTime;
                } else {
                    this.strafingTime = -1;
                }

                if (this.strafingTime >= 20) {
                    if ((double)owner.getRandom().nextFloat() < 0.3) {
                        this.strafingClockwise = !this.strafingClockwise;
                    }

                    if ((double)owner.getRandom().nextFloat() < 0.3) {
                        this.strafingBackwards = !this.strafingBackwards;
                    }

                    this.strafingTime = 0;
                }

                if (this.strafingTime > -1) {
                    if (distance > (double)maxAttackDistance) {
                        this.strafingBackwards = false;
                    } else if (distance < (double)maxAttackDistance * 0.5) {
                        this.strafingBackwards = true;
                    }

                    owner.getMoveControl().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
                    owner.setYRot(Mth.rotateIfNecessary(owner.getYRot(), owner.yHeadRot, 0.0F));
                    BehaviorUtils.lookAtEntity(owner, target);
                } else {
                    BehaviorUtils.lookAtEntity(owner, target);
                }

            });
        }
    }

    protected void start(ServerLevel worldIn, EntityMaid entityIn, long gameTimeIn) {
//        entityIn.setSwingingArms(true);
    }

    protected void stop(ServerLevel worldIn, EntityMaid entityIn, long gameTimeIn) {
        entityIn.setSwingingArms(false);
        entityIn.getMoveControl().strafe(0.0F, 0.0F);
    }

    protected boolean canStillUse(ServerLevel worldIn, EntityMaid entityIn, long gameTimeIn) {
        return this.checkExtraStartConditions(worldIn, entityIn);
    }
}
