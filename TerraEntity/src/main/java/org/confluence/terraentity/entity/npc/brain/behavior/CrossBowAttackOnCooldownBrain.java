package org.confluence.terraentity.entity.npc.brain.behavior;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ChargedProjectiles;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;

public class CrossBowAttackOnCooldownBrain<T extends AbstractTerraNPC> extends NPCRangeAttackOnCooldownBrain<T> {

    public CrossBowAttackOnCooldownBrain(int cooldownTime, float attackRange, float speedModifier) {
        super(cooldownTime, attackRange, speedModifier);
    }

    @Override
    protected void start(ServerLevel level, T entity, long gameTimeIn) {
        super.start(level, entity, gameTimeIn);
        entity.getBrain().getMemory(MemoryModuleType.ATTACK_COOLING_DOWN).ifPresent(
                cd-> {
                    if(cd){
                        entity.setChargingCrossbow(true);
                        entity.startUsingItem(InteractionHand.MAIN_HAND);
                    }
                }
        );
    }

    @Override
    protected void stop(ServerLevel level,T entity, long gameTimeIn) {
        super.stop(level, entity, gameTimeIn);
        entity.setChargingCrossbow(false);
        entity.getMainHandItem().set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(Items.ARROW.getDefaultInstance()));
    }

}
