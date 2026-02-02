package org.confluence.terraentity.integration.touhou_little_maid.task_boomerang;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.Item;
import org.confluence.terraentity.attachment.WeaponStorage;
import org.confluence.terraentity.item.Boomerang;

import java.util.Optional;

public class MaidBoomerangShootTargetTask extends Behavior<EntityMaid> {
    private final int attackCooldown;
    private int attackTime = -1;

    public MaidBoomerangShootTargetTask(int attackCooldown) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT), 1200);
        this.attackCooldown = attackCooldown;
    }

    protected boolean checkExtraStartConditions(ServerLevel worldIn, EntityMaid owner) {
        Optional<LivingEntity> memory = owner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
        if (!memory.isPresent()) {
            return false;
        } else {
            LivingEntity target = memory.get();
            return owner.isHolding((item) -> {
                return item.getItem() instanceof Boomerang;
            }) && owner.canSee(target);
        }
    }

    protected boolean canStillUse(ServerLevel worldIn, EntityMaid entityIn, long gameTimeIn) {
        return entityIn.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET) && this.checkExtraStartConditions(worldIn, entityIn);
    }

    protected void start(ServerLevel worldIn, EntityMaid entityIn, long gameTimeIn) {
        entityIn.setSwingingArms(true);
    }

    protected void tick(ServerLevel worldIn, EntityMaid owner, long gameTime) {
        owner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).ifPresent((target) -> {
            owner.getLookControl().setLookAt(target.getX(), target.getY(), target.getZ());
            boolean canSee = owner.canSee(target);
            Item mainHandItem = owner.getMainHandItem().getItem();
            if(mainHandItem instanceof Boomerang boomerang) {
                --attackTime;
                if (canSee) {
                    var data = WeaponStorage.of(owner);
                    int count = data.getCount(mainHandItem);
                    int maxCount = boomerang.boomerangModifier.maxCount;
                    if (count == 0 || count < maxCount && attackTime <= 0) {
                        owner.performRangedAttack(target, 0);
                        this.attackTime = attackCooldown;
                        owner.swing(InteractionHand.MAIN_HAND);
                    }
                }
            }
        });
    }

    protected void stop(ServerLevel worldIn, EntityMaid entityIn, long gameTimeIn) {
        this.attackTime = -1;
        entityIn.stopUsingItem();
    }
}