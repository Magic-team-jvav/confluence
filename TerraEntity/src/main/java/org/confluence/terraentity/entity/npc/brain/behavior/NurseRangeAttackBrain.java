package org.confluence.terraentity.entity.npc.brain.behavior;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;

public class NurseRangeAttackBrain<T extends AbstractTerraNPC> extends NPCRangeAttackBrain<T> {

    public NurseRangeAttackBrain(int prepareTime, float attackRange) {
        super(prepareTime, attackRange);
    }

    @Override
    protected void start(ServerLevel level, T entity, long gameTimeIn) {

    }

    @Override
    protected boolean customDoAttack(ServerLevel level, T owner, LivingEntity target){
        ThrownPotion thrownpotion = new ThrownPotion(level, owner);
        ItemStack stack = new ItemStack(Items.POTION);
        stack.set(DataComponents.POTION_CONTENTS, PotionContents.EMPTY.withPotion(Potions.HEALING));
        thrownpotion.setItem(stack);
        thrownpotion.shootFromRotation(owner, owner.getXRot(), owner.getYHeadRot(), -20.0F, 0.5F, 1.0F);
        level.addFreshEntity(thrownpotion);
        return true;
    }

    @Override
    protected void stop(ServerLevel level, T entity, long gameTimeIn) {
        super.stop(level, entity, gameTimeIn);
        entity.swing(InteractionHand.MAIN_HAND, true);
    }

    @Override
    protected void tickLook(ServerLevel level, T owner, LivingEntity target){
        if(owner == target){
            owner.getLookControl().setLookAt(owner.position());
            owner.lookAt(EntityAnchorArgument.Anchor.EYES, owner.position());
        }
        else{
            super.tickLook(level, owner, target);
        }
    }

    @Override
    protected void onPrepare(ServerLevel level, T owner, LivingEntity target, int prepareTime){
        // 可能要停下来瞄准
//        owner.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        owner.lookAt(target, 10.0F, 10.0F);
        Vec3 toPos = LandRandomPos.getPosTowards(owner, 3, 1, target.position());
        if (toPos != null) {
            owner.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(toPos,1.0f,1));
        }
    }

    @Override
    protected boolean canTrigger(T owner, LivingEntity target){
        if(target == owner) {
            // 如果是自己，看向脚下就行
            return true;
        }
        return super.canTrigger(owner, target);
    }

}
