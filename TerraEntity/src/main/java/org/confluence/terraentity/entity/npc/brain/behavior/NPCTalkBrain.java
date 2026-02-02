package org.confluence.terraentity.entity.npc.brain.behavior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.entity.npc.chat.ChatHolder;
import org.confluence.terraentity.entity.npc.chat.ChatManager;
import org.confluence.terraentity.entity.npc.chat.NPCChat;
import org.confluence.terraentity.init.TEAi;
import org.confluence.terraentity.registries.chat.variant.SpriteChatElement;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class NPCTalkBrain extends Behavior<AbstractTerraNPC> {

    int talkTime; // 对话间隔时间
    int _talkTime;
    boolean isPassive; // 是否被动触发
    int talkContinueTime; // 对话持续时间

    public NPCTalkBrain(int talkTime) {
        super(Map.of(
                TEAi.MemoryModules.NEARBY_NPC.get(), MemoryStatus.VALUE_PRESENT,
                MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED,
                TEAi.MemoryModules.TALKING_NPC.get(), MemoryStatus.REGISTERED
        ), talkTime);
        this.talkTime = talkTime;
        this._talkTime = talkTime;
        this.talkContinueTime = 200;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, AbstractTerraNPC owner) {
        if(owner.talkingBrainTick > 0 || owner.talkingTarget!= null){ // 正在被对话中，强制触发
            if(owner.getRandom().nextFloat() < 0.2f){ // 但是有概率拒绝
                owner.talkingBrainTick = 0;
                owner.talkingTarget = null;
                return false;
            }
            return true;
        }
        if(--talkTime <= 0){
            this.talkTime = _talkTime;
            // 条件触发
            if(owner.getRandom().nextFloat() < 0.3f) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void start(ServerLevel level, AbstractTerraNPC living, long gameTimeIn) {
        Brain<?> brain = living.getBrain();
        List<AbstractTerraNPC> nearbyNPCs = brain.getMemory(TEAi.MemoryModules.NEARBY_NPC.get()).get();

        if(!nearbyNPCs.isEmpty()) {
            for(AbstractTerraNPC npc : nearbyNPCs) {
                double distance = living.distanceToSqr(npc);
                if(distance > 3*3){
                    continue;
                }
                if(!living.hasLineOfSight(npc)){
                    continue;
                }
                var memory = npc.getBrain().getMemory(TEAi.MemoryModules.TALKING_NPC.get());

                if(memory.isEmpty()){
                    brain.setMemory(TEAi.MemoryModules.TALKING_NPC.get(), npc);
                    npc.getBrain().setMemory(TEAi.MemoryModules.TALKING_NPC.get(), living);
                    npc.talkingBrainTick = this.talkContinueTime;
                    living.talkingBrainTick = this.talkContinueTime;

                    ChatManager manager = living.getChatManager();
                    if(manager != null) {
                        AtomicBoolean isFound = new AtomicBoolean(false);
                        manager.getToOtherChat().ifPresent(chat -> {
                            ChatHolder holder = chat.getChatHolder(npc);
                            if (holder != null && holder.canChat(npc, holder)) {
                                var ch = holder.getChat().generateChat(living.getRandom());
                                living.setChat(ch);
                                isFound.set(true);
                                living.talkingTarget = npc;
                                npc.talkingTarget = living;
                                this.isPassive = false;
                            }
                        });
                        if (isFound.get()) {
                            return;
                        }
                    }
                }else{
                    if(memory.get() == living){ // 被对话的对象

                        this.isPassive = true;
                        return;
                    }
                }
            }
        }
        // 没有找到合适的对话对象，重新开始
        living.talkingBrainTick = 0;

    }

    @Override
    protected void tick(ServerLevel level, AbstractTerraNPC living, long gameTime) {
        --talkTime;
//        --this.talkingTime;
        --living.talkingBrainTick;
        Brain<?> brain = living.getBrain();
        brain.getMemory(TEAi.MemoryModules.TALKING_NPC.get()).ifPresent(
                e->{
                    living.getLookControl().setLookAt(e);
                    if(living.talkingBrainTick == 150 && this.isPassive){
                        living.setChat(new NPCChat(List.of(new SpriteChatElement(
                                List.of(TerraEntity.space("textures/gui/sprites/random_gift.png")), 2f))));
                    }
                    brain.eraseMemory(MemoryModuleType.WALK_TARGET);
                    living.getNavigation().stop();
                }
        );


    }

    @Override
    protected void stop(ServerLevel level, AbstractTerraNPC living, long gameTime) {
        talkTime = (int) (_talkTime * (1 + living.getRandom().nextFloat() * 0.5f));
        living.getBrain().eraseMemory(TEAi.MemoryModules.TALKING_NPC.get());
        living.talkingTarget = null;
        living.talkingBrainTick = 0;
    }


    protected boolean canStillUse(ServerLevel level, AbstractTerraNPC entity, long gameTime) {
        var memory =  entity.getBrain().getMemory(TEAi.MemoryModules.TALKING_NPC.get());
        if(memory.isPresent()){
            return memory.get().distanceToSqr(entity) < 3*3 && (entity.talkingBrainTick > 0);
        }
        return false;
    }
}
