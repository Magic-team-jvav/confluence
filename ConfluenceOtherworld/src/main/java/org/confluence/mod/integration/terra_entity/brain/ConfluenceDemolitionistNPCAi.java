package org.confluence.mod.integration.terra_entity.brain;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.ExplosionDamageCalculator;
import org.confluence.lib.util.IgnoreThrowerExplosionDamageCalculator;
import org.confluence.mod.common.entity.projectile.bomb.BaseBombEntity;
import org.confluence.mod.common.entity.projectile.bomb.BaseGrenadeEntity;
import org.confluence.mod.common.init.item.ConsumableItems;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.entity.npc.brain.NPCAi;
import org.confluence.terraentity.entity.npc.brain.behavior.NPCRangeAttackBrain;

/**
 * 汇流的炸弹商AI，投掷手雷
 */
public class ConfluenceDemolitionistNPCAi extends NPCAi {

    public ConfluenceDemolitionistNPCAi(AbstractTerraNPC npc) {
        super(npc);
    }

    @Override
    protected NPCRangeAttackBrain<? super AbstractTerraNPC> getRangeAttackBrain() {
        return new NPCRangeAttackBrain<>(30, npc.getAttackRange()){
            @Override
            protected boolean customDoAttack(ServerLevel level, AbstractTerraNPC owner, LivingEntity target){
                BaseBombEntity bomb = new BaseGrenadeEntity(npc){
                    @Override
                    protected ExplosionDamageCalculator getExplosionDamageCalculator() {
                        return new IgnoreThrowerExplosionDamageCalculator(1.0f, npc);
                    }
                };
                bomb.setOwner(npc);
                bomb.setItem(ConsumableItems.BOMB.toStack());
                bomb.shootFromRotation(npc, npc.getXRot(), npc.getYRot(), 0.0F, 0.8f, 1.0F);
                level.addFreshEntity(bomb);
                return true;
            }

            @Override
            protected void onPrepare(ServerLevel level, AbstractTerraNPC owner, LivingEntity target, int prepareTime){
                // 可能要停下来瞄准
                if(owner.getRandom().nextFloat() < 0.1f){
                    owner.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
                }
            }

        };

    }

}
