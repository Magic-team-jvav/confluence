package org.confluence.terraentity.entity.npc.brain;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.entity.npc.brain.behavior.CrossBowAttackOnCooldownBrain;
import org.confluence.terraentity.entity.npc.brain.behavior.NPCAttackCalmDownBrain;
import org.confluence.terraentity.entity.npc.brain.behavior.NPCRangeAttackBrain;

/**
 * 军火商专家的AI，使用弩
 */
public class ArmDealerNPCAi extends NPCAi {

    public ArmDealerNPCAi(AbstractTerraNPC npc) {
        super(npc);

    }

    @Override
    protected void init(){
        npc.setAttackRange(13);
        npc.setCooldownTicks(30);
    }

    @Override
    public ImmutableList<Pair<Integer, ? extends BehaviorControl<? super AbstractTerraNPC>>> getRangeAttackPackage(float speedModifier) {

        return ImmutableList.of(
//                Pair.of(5, new RangeAttackStrafingBrain()),  // 不是所有远程攻击都需要走位
                Pair.of(5, getRangeAttackBrain()),
                Pair.of(5, new CrossBowAttackOnCooldownBrain<>(npc.getCooldownTicks(), npc.getAttackRange() * 0.6F,speedModifier)),
                Pair.of(5, new NPCAttackCalmDownBrain<>(15){
                    @Override
                    protected void calmDown(ServerLevel level, AbstractTerraNPC living, long gameTimeIn) {
                        super.calmDown(level, living, gameTimeIn);
                        living.stopUsingItem();
                    }
                })

        );
    }

    @Override
    protected NPCRangeAttackBrain<? super AbstractTerraNPC> getRangeAttackBrain() {
        return new NPCRangeAttackBrain<>(10, npc.getAttackRange()){
            @Override
            protected boolean customDoAttack(ServerLevel level, AbstractTerraNPC owner, LivingEntity target){
                ItemStack stack = owner.getMainHandItem();
                if(stack.getItem() instanceof CrossbowItem weaponItem){
                    owner.setTarget(target);
                    owner.performCrossbowAttack(npc, 2.0f);
//                    weaponItem.shoot( level, owner, InteractionHand.MAIN_HAND, stack, List.of(Items.ARROW.getDefaultInstance()), 2.0f, 1f, owner.getRandom().nextFloat() < 0.3f, target);
                    return true;
                }
                return false;
            }

        };
    }
}
