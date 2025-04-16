package org.confluence.mod.integration.terra_entity.brain;

import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.entity.npc.brain.ArmDealerNPCAi;
import org.confluence.terraentity.entity.npc.brain.behavior.NPCRangeAttackBrain;

public class ConfluenceArmDealerNPCAi extends ArmDealerNPCAi {

    public ConfluenceArmDealerNPCAi(AbstractTerraNPC npc) {
        super(npc);
    }

    protected NPCRangeAttackBrain<? super AbstractTerraNPC> getRangeAttackBrain() {
        return super.getRangeAttackBrain();
//        return new NPCRangeAttackBrain<>(10, npc.getAttackRange()){
            // todo 发射子弹

//            protected void doAttack(ServerLevel level, Mob owner, LivingEntity target){
//                Arrow arrow = new Arrow(owner.level(), owner, Items.ARROW.getDefaultInstance(), Items.ARROW.getDefaultInstance());
//                arrow.setPos(owner.getX(), owner.getY() + owner.getEyeHeight(), owner.getZ());
//                arrow.shootFromRotation(owner, owner.getXRot(), owner.getYRot(), 0.0F, 2F, 1.0F);
//                owner.level().addFreshEntity(arrow);
//            }
//        };
    }
}
